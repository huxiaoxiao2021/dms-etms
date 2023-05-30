package com.jd.bluedragon.distribution.collection.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDetailDao;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordCondition;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordDetailCondition;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordDetailPo;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordPo;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
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
    private CollectionRecordDao collectionRecordDao;
    @Autowired
    private CollectionRecordDetailDao collectionRecordDetailDao;
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
            CollectionRecordDetailPo dPo = new CollectionRecordDetailPo();
            dPo.setCollectionCode(collectDto.getCollectionCode());
            dPo.setScanCode(collectDto.getPackageCode());
            List<CollectionRecordDetailPo> dPoRes = collectionRecordDetailDao.findAggCodeByScanCode(dPo);
            if(CollectionUtils.isNotEmpty(dPoRes)) {
                log.warn("{}当前包裹重复扫描，collectDto={}", methodDesc, JsonHelper.toJson(collectDto));
                return;
            }
            CollectionRecordDetailPo detailPo = new CollectionRecordDetailPo();
            detailPo.setCollectionCode(collectDto.getCollectionCode());
            detailPo.setScanCode(collectDto.getPackageCode());
            detailPo.setScanCodeType(CollectionScanCodeTypeEnum.package_code.name());
            detailPo.setAggCode(collectDto.getWaybillCode());
            detailPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
            detailPo.setCollectedMark(collectDto.getMainTaskBizId());
            detailPo.setCreateTime(new Date());
            collectionRecordDetailDao.insertSelective(detailPo);
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
            CollectionRecordPo recordPo = new CollectionRecordPo();
            recordPo.setCollectionCode(collectDto.getCollectionCode());
            recordPo.setAggCode(collectDto.getWaybillCode());
            CollectionRecordPo collectionRecordPo = collectionRecordDao.findJyCollectRecordByAggCode(recordPo);

            Waybill waybill = waybillQueryManager.getWaybillByWayCode(collectDto.getWaybillCode());
            String wbs = Objects.isNull(waybill) ? "" : waybill.getWaybillSign();
            Integer goodNumber = (Objects.isNull(waybill) || Objects.isNull(waybill.getGoodNumber())) ? 0 : waybill.getGoodNumber();
            recordPo.setInitNumber(goodNumber);

            if(Objects.isNull(collectionRecordPo)) {
                recordPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
                recordPo.setCreateTime(new Date());
                recordPo.setUpdateTime(recordPo.getCreateTime());
                recordPo.setCustomType(toBNetFlag(wbs));
                recordPo.setRealCollectNum(1);
                recordPo.setIsCollected(goodNumber > 1 ? JyScanCollectServiceImpl.BU_QI : JyScanCollectServiceImpl.JI_QI);
                collectionRecordDao.insertSelective(recordPo);
            } else {
                CollectionRecordPo updateRecordPo = new CollectionRecordPo();
                updateRecordPo.setUpdateTime(new Date());
                updateRecordPo.setCustomType(toBNetFlag(collectDto.getWaybillCode()));
                updateRecordPo.setInitNumber(goodNumber);
                int collectNum = this.countScanCodeNumNumByCollectedMarkAndAggCode(collectDto);
                updateRecordPo.setRealCollectNum(collectNum);
                updateRecordPo.setIsCollected( (goodNumber > 0 && collectNum >= goodNumber) ? JyScanCollectServiceImpl.JI_QI : JyScanCollectServiceImpl.BU_QI);

                collectionRecordDao.updateByPrimaryKeySelective(recordPo);
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
    int countScanCodeNumNumByCollectedMarkAndAggCode(JyScanCollectMqDto collectDto){

        CollectionRecordDetailPo detailPo = new CollectionRecordDetailPo();
        detailPo.setCollectionCode(collectDto.getCollectionCode());//岗位
        detailPo.setCollectedMark(collectDto.getMainTaskBizId());//任务
        detailPo.setAggCode(collectDto.getWaybillCode());//运单
        return collectionRecordDetailDao.countScanCodeNumNumByCollectedMarkAndAggCode(detailPo);
    }


    public List<CollectionRecordPo> findCollectRecordByCondition(CollectionRecordCondition condition) {
        return collectionRecordDao.findCollectRecordByCondition(condition);
    }

    @Override
    public List<CollectionRecordDetailPo> findCollectRecordDetailByCondition(CollectionRecordDetailCondition jqDetailQueryParam) {
        return collectionRecordDetailDao.findCollectRecordDetailByCondition(jqDetailQueryParam);
    }

}
