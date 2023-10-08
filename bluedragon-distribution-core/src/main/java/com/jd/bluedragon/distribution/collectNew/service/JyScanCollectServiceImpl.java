package com.jd.bluedragon.distribution.collectNew.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.collectNew.dao.JyCollectRecordDao;
import com.jd.bluedragon.distribution.collectNew.dao.JyCollectRecordDetailDao;
import com.jd.bluedragon.distribution.collectNew.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.constants.WaybillCustomTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCancelScanDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 16:51
 * @Description
 */
@Slf4j
@Service
public class JyScanCollectServiceImpl implements JyScanCollectService {

    public static final Integer JI_QI = 1;
    public static final Integer BU_QI = 0;

    public static final Integer BUQI_WAYBILL_NUM_MAX = 50000;
    public static final Integer BUQI_WAYBILL_NUM_MIN = 1000;


    @Autowired
    private JyCollectRecordDao jyCollectRecordDao;
    @Autowired
    private JyCollectRecordDetailDao jyCollectRecordDetailDao;
    @Autowired
    private JyScanCollectCacheService jyScanCollectCacheService;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClient;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectServiceImpl.insertCollectionRecordDetail",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void insertCollectionRecordDetail(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectServiceImpl.insertCollectionRecordDetail建议扫描处理集齐明细添加：";
        //
        if (!jyScanCollectCacheService.lockInsertCollectRecordDetail(collectDto.getCollectionCode(), collectDto.getPackageCode())) {
            log.error("{}修改集齐运单明细表获取锁失败，说明重复执行，不做处理，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
//            throw new JyBizException("插入集齐运单明细表获取锁失败" + collectDto.getCollectionCode() + collectDto.getPackageCode());
            return;//加锁失败说明已经在执行，防并发，无需异常重试
        }
        try{
            JyCollectRecordDetailCondition dPo = new JyCollectRecordDetailCondition();
            dPo.setCollectionCode(collectDto.getCollectionCode());
            dPo.setScanCode(collectDto.getPackageCode());
            dPo.setSiteId(collectDto.getOperateSiteId().longValue());
            List<JyCollectRecordDetailPo> dPoRes = jyCollectRecordDetailDao.findAggCodeByScanCode(dPo);
            if(CollectionUtils.isNotEmpty(dPoRes)) {
                log.warn("{}当前包裹重复扫描，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
                return;
            }
            JyCollectRecordDetailPo detailPo = new JyCollectRecordDetailPo();
            detailPo.setCollectionCode(collectDto.getCollectionCode());
            detailPo.setSiteId(collectDto.getOperateSiteId().longValue());
            detailPo.setScanCode(collectDto.getPackageCode());
            detailPo.setScanCodeType(CollectionScanCodeTypeEnum.package_code.name());
            detailPo.setAggCode(collectDto.getWaybillCode());
            detailPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
            detailPo.setOperateTime(new Date(collectDto.getOperateTime()));
            detailPo.setCreateTime(new Date());
            jyCollectRecordDetailDao.insertSelective(detailPo);
        }catch (Exception ex) {
            log.error("{}服务异常，参数={}， errMsg={}", methodDesc, JsonHelper.toJson(collectDto), ex.getMessage(), ex);
            throw new JyBizException("拣运扫描修改集齐运单明细表服务异常" + collectDto.getPackageCode());
        }finally {
            jyScanCollectCacheService.delLockInsertCollectRecordDetail(collectDto.getCollectionCode(), collectDto.getPackageCode());
        }
    }



    /**
     * 修改时仅修改运单toC/toC类型及运单是否集齐字段
     * @param collectRecordPo
     */
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectServiceImpl.upInsertCollectionRecord",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void upInsertCollectionRecord(JyCollectRecordPo collectRecordPo, boolean insertFlag) {
        String methodDesc = String.format("JyScanCollectServiceImpl.upInsertCollectionRecord拣运扫描修改集齐运单表%s：", insertFlag ? "(支持insert)" : "(不支持insert)");
        //
        if (!jyScanCollectCacheService.lockUpInsertCollectRecord(collectRecordPo.getCollectionCode(), collectRecordPo.getAggCode())) {
            log.error("{}修改集齐运单表获取锁失败，collectDto={}", methodDesc, JsonHelper.toJson(collectRecordPo));
            throw new JyBizException("修改集齐运单表获取锁失败" + collectRecordPo.getCollectionCode() + collectRecordPo.getAggCode());
        }
        try{
            JyCollectRecordPo recordPo = new JyCollectRecordPo();
            recordPo.setCollectionCode(collectRecordPo.getCollectionCode());
            recordPo.setAggCode(collectRecordPo.getAggCode());
            recordPo.setSiteId(collectRecordPo.getSiteId());
            JyCollectRecordPo jyCollectRecord = jyCollectRecordDao.findJyCollectRecordByAggCode(recordPo);


            if(Objects.isNull(jyCollectRecord)) {
                if(!insertFlag) {
                    return;
                }
                recordPo.setShouldCollectNum(collectRecordPo.getShouldCollectNum());
                recordPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
                recordPo.setCreateTime(new Date());
                recordPo.setUpdateTime(recordPo.getCreateTime());
                recordPo.setCustomType(collectRecordPo.getCustomType());
                recordPo.setRealCollectNum(1);
                recordPo.setIsCollected(collectRecordPo.getShouldCollectNum() > 1 ? JyScanCollectServiceImpl.BU_QI : JyScanCollectServiceImpl.JI_QI);
                jyCollectRecordDao.insertSelective(recordPo);
            } else {
                JyCollectRecordPo updateRecordPo = new JyCollectRecordPo();
                updateRecordPo.setSiteId(collectRecordPo.getSiteId());
                updateRecordPo.setCollectionCode(jyCollectRecord.getCollectionCode());
                updateRecordPo.setAggCode(jyCollectRecord.getAggCode());
                updateRecordPo.setUpdateTime(new Date());
                int collectNum = this.countScanCodeNumNumByCollectedMarkAndAggCode(collectRecordPo);
                updateRecordPo.setRealCollectNum(collectNum);
                updateRecordPo.setIsCollected( (jyCollectRecord.getShouldCollectNum() > 0 && collectNum >= jyCollectRecord.getShouldCollectNum()) ? JyScanCollectServiceImpl.JI_QI : JyScanCollectServiceImpl.BU_QI);
                if(insertFlag) {
                    //扫描关注更新字段，取消扫描不需要关注
                    updateRecordPo.setShouldCollectNum(collectRecordPo.getShouldCollectNum());
                    updateRecordPo.setCustomType(collectRecordPo.getCustomType());
                }else {
                    //取消扫描关注字段
                    if(collectNum <= 0) {
                        updateRecordPo.setYn(0);
                    }

                }

                jyCollectRecordDao.updateByCondition(updateRecordPo);
            }
        }catch (Exception ex) {
            log.error("{}服务异常，参数={}， errMsg={}", methodDesc, JsonHelper.toJson(collectRecordPo), ex.getMessage(), ex);
            throw new JyBizException("拣运扫描修改集齐运单表服务异常" + collectRecordPo.getAggCode());
        }finally {
            jyScanCollectCacheService.delLockUpInsertCollectRecord(collectRecordPo.getCollectionCode(), collectRecordPo.getAggCode());
        }
    }

    /**
     * B网运单标识
     * @param waybillSign
     * @return
     */
    @Override
    public String toBNetFlag(String waybillCode, String waybillSign) {
        if(StringUtils.isNotBlank(waybillSign) && BusinessUtil.isB2b(waybillSign)) {
            return WaybillCustomTypeEnum.TO_B.getCode();
        }
        if(log.isInfoEnabled()) {
            log.info("{}非B网运单，wbs={}", waybillCode, waybillSign);
        }
        return WaybillCustomTypeEnum.DEFAULT.getCode();
    }

    /**
     * 是否集齐
     * 每次查询最新，避免按运单扫描 按包裹取消同时操作
     * @param collectDto
     * @return 1 集齐  0 未集齐
     */
    private int countScanCodeNumNumByCollectedMarkAndAggCode(JyCollectRecordPo collectDto){

        JyCollectRecordDetailPo detailPo = new JyCollectRecordDetailPo();
        detailPo.setCollectionCode(collectDto.getCollectionCode());//岗位
        detailPo.setSiteId(collectDto.getSiteId());
        detailPo.setAggCode(collectDto.getAggCode());//运单
        return jyCollectRecordDetailDao.countScanCodeNumNumByCollectedMarkAndAggCode(detailPo);
    }


    @Override
    public List<JyCollectRecordStatistics> findBuQiWaybillByCollectionCodes(JyCollectRecordCondition condition) {
        return jyCollectRecordDao.findBuQiByCollectionCodes(condition);
    }

    @Override
    public List<JyCollectRecordDetailPo> findByCollectionCodesAndAggCode(JyCollectRecordDetailCondition jqDetailQueryParam) {
        return jyCollectRecordDetailDao.findByCollectionCodesAndAggCode(jqDetailQueryParam);
    }

    @Override
    public List<JyCollectRecordStatistics> getAllBuQiWaybillCodes(JySendCancelScanDto mqBody) {
        String methodDesc = "JyScanCollectServiceImpl.getAllBuQiWaybillCodes:获取所有不齐运单号：";
        if(Objects.isNull(mqBody) || CollectionUtils.isEmpty(mqBody.getCollectionCodes())) {
            return null;
        }

        JyCollectRecordCondition jqQueryParam = new JyCollectRecordCondition();
        jqQueryParam.setCollectionCodeList(mqBody.getCollectionCodes());
        jqQueryParam.setSiteId(mqBody.getOperateSiteId().longValue());
        jqQueryParam.setCustomType(WaybillCustomTypeEnum.TO_B.getCode());//B网

        int pageSize = 1000;
        //理论上不可能存在的最大值
        int pageNum = JyScanCollectServiceImpl.BUQI_WAYBILL_NUM_MAX / pageSize;

        jqQueryParam.setPageSize(pageSize);
        List<JyCollectRecordStatistics> res = new ArrayList<>();
        for(int pageNo = 1; pageNo <= pageNum; pageNo ++) {
            int offset = (pageNo - 1) * pageSize;
            jqQueryParam.setOffset(offset);
            if(log.isInfoEnabled()) {
                log.info("{}查询参数={}", methodDesc, JsonHelper.toJson(jqQueryParam));
            }
            List<JyCollectRecordStatistics> collectionRecordPoList = this.findBuQiWaybillByCollectionCodes(jqQueryParam);
            if(CollectionUtils.isEmpty(collectionRecordPoList)) {
                break;
            }

            if(collectionRecordPoList.size() < pageSize) {
                res.addAll(collectionRecordPoList);
                break;
            }
            res.addAll(collectionRecordPoList);
            int buQiWaybillCodeMaxSum = this.getBuQiWaybillCodeMaxSum();
            if(res.size() > buQiWaybillCodeMaxSum) {
                throw new JyBizException("异常：不齐运单数量超过预期最大值" + buQiWaybillCodeMaxSum);
            }
        }
        return res;
    }

    /**
     * 获取ucc配置不齐运单数量最大值
     * @return
     */
    private int getBuQiWaybillCodeMaxSum(){
        Integer buQiWaybillMaxSize = dmsConfigManager.getUccPropertyConfig().getJyBuQiWaybillCodeMaxSum();
        if(buQiWaybillMaxSize < JyScanCollectServiceImpl.BUQI_WAYBILL_NUM_MIN) {
            return JyScanCollectServiceImpl.BUQI_WAYBILL_NUM_MIN;
        }
        if(buQiWaybillMaxSize > JyScanCollectServiceImpl.BUQI_WAYBILL_NUM_MAX) {
            return JyScanCollectServiceImpl.BUQI_WAYBILL_NUM_MAX;
        }
        return buQiWaybillMaxSize;
    }


    @Override
    public List<JyCollectRecordDetailPo> findPageCollectDetailByCondition(JyCollectRecordDetailCondition condition) {
        return jyCollectRecordDetailDao.findPageByCondition(condition);
    }

    @Override
    public void deleteByAggCode(JyCollectRecordDetailCondition condition) {
        jyCollectRecordDetailDao.deleteByAggCode(condition);
    }

    @Override
    public void deleteByScanCode(JyCollectRecordDetailCondition condition) {
        jyCollectRecordDetailDao.deleteByScanCode(condition);
    }


    @Override
    public List<JyCollectRecordPo> findPageCollectByCondition(JyCollectRecordCondition condition) {
        return jyCollectRecordDao.findPageByCondition(condition);
    }
}
