package com.jd.bluedragon.distribution.collectNew.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.collectNew.dao.JyCollectRecordDao;
import com.jd.bluedragon.distribution.collectNew.dao.JyCollectRecordDetailDao;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordPo;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailPo;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailCondition;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.service.JyScanCollectCacheService;
import com.jd.bluedragon.distribution.jy.constants.WaybillCustomTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
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


    @Autowired
    private JyCollectRecordDao jyCollectRecordDao;
    @Autowired
    private JyCollectRecordDetailDao jyCollectRecordDetailDao;
    @Autowired
    private JyScanCollectCacheService jyScanCollectCacheService;
    @Qualifier("redisClientOfJy")
    private Cluster redisClient;
    @Autowired
    private WaybillQueryManager waybillQueryManager;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectServiceImpl.insertCollectionRecordDetailInBizId",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void insertCollectionRecordDetailInBizId(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectServiceImpl.insertCollectionRecordDetailInBizId建议扫描处理集齐明细添加：";
        //
        if (!jyScanCollectCacheService.lockInsertCollectRecordDetail(collectDto.getCollectionCode(), collectDto.getPackageCode())) {
            log.error("{}修改集齐运单表获取锁失败，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
            throw new JyBizException("插入集齐运单明细表获取锁失败" + collectDto.getCollectionCode() + collectDto.getPackageCode());
        }
        try{
            JyCollectRecordDetailCondition dPo = new JyCollectRecordDetailCondition();
            dPo.setCollectionCode(collectDto.getCollectionCode());
            dPo.setScanCode(collectDto.getPackageCode());
            dPo.setSiteId(collectDto.getOperateSiteId().longValue());
            List<JyCollectRecordDetailPo> dPoRes = jyCollectRecordDetailDao.findAggCodeByCondition(dPo);
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
     * @param collectDto
     */
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyScanCollectServiceImpl.upInsertCollectionRecordInBizId",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void upInsertCollectionRecordInBizId(JyScanCollectMqDto collectDto) {
        String methodDesc = "JyScanCollectServiceImpl.upInsertCollectionRecordInBizId拣运扫描修改集齐运单表：";
        //
        if (!jyScanCollectCacheService.lockUpInsertCollectRecord(collectDto.getCollectionCode(), collectDto.getWaybillCode())) {
            log.error("{}修改集齐运单表获取锁失败，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
            throw new JyBizException("修改集齐运单表获取锁失败" + collectDto.getCollectionCode() + collectDto.getWaybillCode());
        }
        try{
            JyCollectRecordPo recordPo = new JyCollectRecordPo();
            recordPo.setCollectionCode(collectDto.getCollectionCode());
            recordPo.setAggCode(collectDto.getWaybillCode());
            recordPo.setSiteId(collectDto.getOperateSiteId().longValue());
            JyCollectRecordPo jyCollectRecord = jyCollectRecordDao.findJyCollectRecordByAggCode(recordPo);

            Waybill waybill = waybillQueryManager.getWaybillByWayCode(collectDto.getWaybillCode());
            String wbs = Objects.isNull(waybill) ? "" : waybill.getWaybillSign();
            Integer goodNumber = (Objects.isNull(waybill) || Objects.isNull(waybill.getGoodNumber())) ? 0 : waybill.getGoodNumber();
            recordPo.setShouldCollectNum(goodNumber);

            if(Objects.isNull(jyCollectRecord)) {
                recordPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
                recordPo.setCreateTime(new Date());
                recordPo.setUpdateTime(recordPo.getCreateTime());
                recordPo.setCustomType(toBNetFlag(wbs));
                recordPo.setRealCollectNum(1);
                recordPo.setIsCollected(goodNumber > 1 ? JyScanCollectServiceImpl.BU_QI : JyScanCollectServiceImpl.JI_QI);
                jyCollectRecordDao.insertSelective(recordPo);
            } else {
                JyCollectRecordPo updateRecordPo = new JyCollectRecordPo();
                updateRecordPo.setSiteId(collectDto.getOperateSiteId().longValue());
                updateRecordPo.setCollectionCode(jyCollectRecord.getCollectionCode());
                updateRecordPo.setAggCode(jyCollectRecord.getAggCode());
                updateRecordPo.setUpdateTime(new Date());
                updateRecordPo.setCustomType(toBNetFlag(collectDto.getWaybillCode()));
                updateRecordPo.setShouldCollectNum(goodNumber);
                int collectNum = this.countScanCodeNumNumByCollectedMarkAndAggCode(collectDto);
                updateRecordPo.setRealCollectNum(collectNum);
                updateRecordPo.setIsCollected( (goodNumber > 0 && collectNum >= goodNumber) ? JyScanCollectServiceImpl.JI_QI : JyScanCollectServiceImpl.BU_QI);

                jyCollectRecordDao.updateByCondition(updateRecordPo);
            }
        }catch (Exception ex) {
            log.error("{}服务异常，参数={}， errMsg={}", methodDesc, JsonHelper.toJson(collectDto), ex.getMessage(), ex);
            throw new JyBizException("拣运扫描修改集齐运单表服务异常" + collectDto.getWaybillCode());
        }finally {
            jyScanCollectCacheService.delLockUpInsertCollectRecord(collectDto.getCollectionCode(), collectDto.getWaybillCode());
        }
    }

    /**
     * B网运单标识
     * @param waybillSign
     * @return
     */
    String toBNetFlag(String waybillSign) {
        if(StringUtils.isNotBlank(waybillSign) && BusinessUtil.isB2b(waybillSign)) {
            return WaybillCustomTypeEnum.TO_B.getCode();
        }
        return WaybillCustomTypeEnum.DEFAULT.getCode();
    }

    /**
     * 是否集齐
     * 每次查询最新，避免按运单扫描 按包裹取消同时操作
     * @param collectDto
     * @return 1 集齐  0 未集齐
     */
    private int countScanCodeNumNumByCollectedMarkAndAggCode(JyScanCollectMqDto collectDto){

        JyCollectRecordDetailPo detailPo = new JyCollectRecordDetailPo();
        detailPo.setCollectionCode(collectDto.getCollectionCode());//岗位
        detailPo.setSiteId(collectDto.getOperateSiteId().longValue());
        detailPo.setAggCode(collectDto.getWaybillCode());//运单
        return jyCollectRecordDetailDao.countScanCodeNumNumByCollectedMarkAndAggCode(detailPo);
    }


    @Override
    public List<JyCollectRecordPo> findBuQiWaybillByCollectionCodes(JyCollectRecordCondition condition) {
        return jyCollectRecordDao.findBuQiByCollectionCodes(condition);
    }

    @Override
    public List<JyCollectRecordDetailPo> findByCollectionCodesAndAggCode(JyCollectRecordDetailCondition jqDetailQueryParam) {
        return jyCollectRecordDetailDao.findByCollectionCodesAndAggCode(jqDetailQueryParam);
    }

}
