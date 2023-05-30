package com.jd.bluedragon.distribution.collection.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDao;
import com.jd.bluedragon.distribution.collection.dao.CollectionRecordDetailDao;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 16:51
 * @Description
 */
@Slf4j
public class JyScanCollectServiceImpl implements JyScanCollectService {

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
            collectDto.setGoodNumber(goodNumber);

            if(Objects.isNull(collectionRecordPo)) {
                recordPo.setAggCodeType(CollectionScanCodeTypeEnum.waybill_code.name());
                recordPo.setCreateTime(new Date());
                recordPo.setUpdateTime(recordPo.getCreateTime());
                recordPo.setCustomType(toBNetFlag(wbs));
                recordPo.setIsCollected(this.isCollected(collectDto));
                collectionRecordDao.insertSelective(recordPo);
            } else {
                CollectionRecordPo updateRecordPo = new CollectionRecordPo();
                updateRecordPo.setUpdateTime(new Date());
                updateRecordPo.setCustomType(toBNetFlag(collectDto.getWaybillCode()));
                updateRecordPo.setIsCollected(this.isCollected(collectDto));
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
        if(StringUtils.isBlank(waybillSign)) {
            return WaybillCustomTypeEnum.DEFAULT.getCode();
        }
        if(BusinessUtil.isB2b(waybillSign)) {
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
    int isCollected(JyScanCollectMqDto collectDto){
        Integer goodNumber = collectDto.getGoodNumber();
        if(Objects.isNull(goodNumber) || goodNumber <= 0) {
            log.warn("JyScanCollectServiceImpl.isCollected获取运单goodNumber为空，默认不齐，param={}", JsonHelper.toJson(collectDto));
            return 0;
        }

        CollectionRecordDetailPo detailPo = new CollectionRecordDetailPo();
        detailPo.setCollectionCode(collectDto.getCollectionCode());//岗位
        detailPo.setCollectedMark(collectDto.getMainTaskBizId());//任务
        detailPo.setAggCode(collectDto.getWaybillCode());//运单
        Integer scanNum = collectionRecordDetailDao.countScanCodeNumNumByCollectedMarkAndAggCode(detailPo);
        if(scanNum >= goodNumber) {
            if(log.isInfoEnabled()) {
                log.info("JyScanCollectServiceImpl.isCollected运单齐了：param={}", JsonHelper.toJson(collectDto));
            }
            return 1;
        }
        return 0;

    }

}
