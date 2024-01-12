package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendRecordDao;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskStatisticsDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 空铁提发记录服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
@Service
public class JyPickingSendRecordServiceImpl implements JyPickingSendRecordService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendRecordServiceImpl.class);

    @Autowired
    private JyPickingSendRecordDao jyPickingSendRecordDao;
    @Autowired
    private JyAviationRailwayPickingGoodsCacheService pickingGoodsCacheService;
    @Autowired
    private CommonService commonService;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }



    public String fetchWaitPickingBizIdByBarCode(Long siteId, String barCode) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(siteId);
        recordEntity.setWaitScanCode(barCode);
        return jyPickingSendRecordDao.fetchWaitPickingBizIdByBarCode(recordEntity);
    }

    @Override
    public JyPickingSendRecordEntity fetchRealPickingRecordByBarCodeAndBizId(Long curSiteId, String barCode, String bizId) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(curSiteId);
        recordEntity.setScanCode(barCode);
        recordEntity.setBizId(bizId);
        return jyPickingSendRecordDao.fetchRealPickingRecordByBarCodeAndBizId(recordEntity);
    }

    @Override
    public JyPickingSendRecordEntity latestPickingRecord(Long curSiteId, String bizId, String barCode) {
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(curSiteId);
        recordEntity.setScanCode(barCode);
        recordEntity.setBizId(bizId);
        return jyPickingSendRecordDao.latestPickingRecord(recordEntity);
    }

//    @Override
//    public PickingGoodTaskStatisticsDto statisticsByBizId(Long siteId, String bizId, Long nextSiteId) {
//        //待提
//        Integer waitPickingTotalNum = this.countTaskWaitScanItemNum(bizId, siteId);
//
//        //已提
//        Integer realPickingTotalNum = this.countTaskRealScanItemNum(bizId, siteId);
//
//        //多提
//        JyPickingSendRecordEntity morePicking = new JyPickingSendRecordEntity(siteId);
//        morePicking.setScanCode(bizId);
//        morePicking.setMoreScanFlag(Constants.NUMBER_ONE);
//        Integer morePickingTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(morePicking);
//
//        PickingGoodTaskStatisticsDto res = new PickingGoodTaskStatisticsDto();
//        res.setWaitPickingTotalNum(waitPickingTotalNum);
//        res.setRealPickingTotalNum(realPickingTotalNum);
//        res.setMorePickingTotalNum(morePickingTotalNum);
//
//        if(!Objects.isNull(nextSiteId)) {
//            //待发
//            JyPickingSendRecordEntity waitSendEntity = new JyPickingSendRecordEntity(siteId);
//            waitSendEntity.setScanCode(bizId);
//            waitSendEntity.setInitNextSiteId(nextSiteId);
//            Integer waitSendTotalNum = jyPickingSendRecordDao.countTaskWaitScanItemNum(waitSendEntity);
//
//            //已发
//            JyPickingSendRecordEntity realSendEntity = new JyPickingSendRecordEntity(siteId);
//            realSendEntity.setScanCode(bizId);
//            realSendEntity.setInitNextSiteId(nextSiteId);
//            Integer realSendTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(realSendEntity);
//
//            //多发
//            JyPickingSendRecordEntity moreSendEntity = new JyPickingSendRecordEntity(siteId);
//            moreSendEntity.setScanCode(bizId);
//            moreSendEntity.setMoreScanFlag(Constants.NUMBER_ONE);
//            moreSendEntity.setInitNextSiteId(nextSiteId);
//            Integer moreSendTotalNum = jyPickingSendRecordDao.countTaskRealScanItemNum(moreSendEntity);
//
//            res.setWaitSendTotalNum(waitSendTotalNum);
//            res.setRealSendTotalNum(realSendTotalNum);
//            res.setMoreSendTotalNum(moreSendTotalNum);
//        }
//        return res;
//    }

    @Override
    public Integer countTaskRealScanItemNum (String bizId, Long siteId) {
        JyPickingSendRecordEntity realPicking = new JyPickingSendRecordEntity(siteId);
        realPicking.setScanCode(bizId);
        return jyPickingSendRecordDao.countTaskRealScanItemNum(realPicking);
    }

    @Override
    public Integer countTaskWaitScanItemNum(String bizId, Long siteId) {
        JyPickingSendRecordEntity waitPickingEntity = new JyPickingSendRecordEntity(siteId);
        waitPickingEntity.setScanCode(bizId);
        return jyPickingSendRecordDao.countTaskWaitScanItemNum(waitPickingEntity);
    }

    @Override
    public void savePickingScanRecord(JyPickingSendRecordEntity recordEntity) {
        JyPickingSendRecordEntity entity = this.fetchRealPickingRecordByBarCodeAndBizId(recordEntity.getPickingSiteId(), recordEntity.getScanCode(), recordEntity.getBizId());
        if(Objects.isNull(entity)) {
            jyPickingSendRecordDao.insertSelective(recordEntity);
        }else {
            logWarn("提货扫描重复插入，record={}", JsonHelper.toJson(recordEntity));
        }
    }

    @Override
    public void updatePickingGoodRecordByWaitScanCode(JyPickingSendRecordEntity updateEntity) {
        jyPickingSendRecordDao.updatePickingGoodRecordByWaitScanCode(updateEntity);
    }

    @Override
    public void initOrUpdateNeedScanDetail(PickingGoodTaskDetailInitDto paramDto) {
        if(!pickingGoodsCacheService.lockPickingGoodDetailRecordInit(paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode())) {
            logWarn("提货明细初始化获取锁失败，异常重试，param={}", JsonHelper.toJson(paramDto));
            throw new JyBizException(String.format("提货明细初始化获取锁失败%s:%s:%s", paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode()));
        }
        try {

            JyPickingSendRecordEntity queryEntity = new JyPickingSendRecordEntity(paramDto.getPickingSiteId());
            queryEntity.setBizId(paramDto.getBizId());
            queryEntity.setPackageCode(paramDto.getPackageCode());
            JyPickingSendRecordEntity entityQueryRes = jyPickingSendRecordDao.fetchByPackageCodeAndBizId(queryEntity);

            if (Objects.isNull(entityQueryRes)) {
                JyPickingSendRecordEntity insertEntity = new JyPickingSendRecordEntity(paramDto.getPickingSiteId());
                insertEntity.setBizId(paramDto.getBizId());
                insertEntity.setPickingSiteId(paramDto.getPickingSiteId());
                insertEntity.setPickingNodeCode(paramDto.getPickingNodeCode());
                insertEntity.setPackageCode(paramDto.getPackageCode());
                insertEntity.setWaybillCode(WaybillUtil.getWaybillCode(paramDto.getPackageCode()));
                insertEntity.setWaitScanFlag(Constants.NUMBER_ONE);
                if (Boolean.TRUE.equals(paramDto.getScanIsBoxType())) {
                    insertEntity.setWaitScanCode(paramDto.getBoxCode());
                    insertEntity.setWaitScanCodeType(JyPickingSendRecordEntity.SCAN_BOX);
                    BoxNextSiteDto boxNextSiteDto = commonService.getRouteNextSiteByBox(paramDto.getPickingSiteId().intValue(), paramDto.getBoxCode());
                    if (!Objects.isNull(boxNextSiteDto)) {
                        insertEntity.setInitNextSiteId(boxNextSiteDto.getNextSiteId().longValue());
                        insertEntity.setBoxInitFlowKey(boxNextSiteDto.getBoxConfirmNextSiteKey());
                    }
                } else {
                    insertEntity.setWaitScanCode(paramDto.getPackageCode());
                    insertEntity.setWaitScanCodeType(JyPickingSendRecordEntity.SCAN_PACKAGE);
                    BaseStaffSiteOrgDto dto = commonService.getRouteNextSiteByWaybillCode(paramDto.getPickingSiteId().intValue(), WaybillUtil.getWaybillCode(paramDto.getPackageCode()));
                    if (!Objects.isNull(dto)) {
                        insertEntity.setInitNextSiteId(dto.getSiteCode().longValue());
                    }
                }
                Date time = new Date();
                insertEntity.setCreateTime(time);
                insertEntity.setInitTime(time);
                insertEntity.setUpdateTime(time);

                jyPickingSendRecordDao.insertSelective(insertEntity);
            } else {
                if (!Constants.NUMBER_ONE.equals(entityQueryRes.getWaitScanFlag())) {
                    JyPickingSendRecordEntity updateEntity = new JyPickingSendRecordEntity(paramDto.getPickingSiteId());
                    updateEntity.setBizId(paramDto.getBizId());
                    updateEntity.setPackageCode(paramDto.getPackageCode());
                    updateEntity.setWaitScanFlag(Constants.NUMBER_ONE);
                    if (Boolean.TRUE.equals(paramDto.getScanIsBoxType())) {
                        updateEntity.setWaitScanCode(paramDto.getBoxCode());
                        updateEntity.setWaitScanCodeType(JyPickingSendRecordEntity.SCAN_BOX);
                        BoxNextSiteDto boxNextSiteDto = commonService.getRouteNextSiteByBox(paramDto.getPickingSiteId().intValue(), paramDto.getBoxCode());
                        if (!Objects.isNull(boxNextSiteDto)) {
                            updateEntity.setInitNextSiteId(boxNextSiteDto.getNextSiteId().longValue());
                            updateEntity.setBoxInitFlowKey(boxNextSiteDto.getBoxConfirmNextSiteKey());
                        }
                    } else {
                        updateEntity.setWaitScanCode(paramDto.getPackageCode());
                        updateEntity.setWaitScanCodeType(JyPickingSendRecordEntity.SCAN_PACKAGE);
                        BaseStaffSiteOrgDto dto = commonService.getRouteNextSiteByWaybillCode(paramDto.getPickingSiteId().intValue(), WaybillUtil.getWaybillCode(paramDto.getPackageCode()));
                        if (!Objects.isNull(dto)) {
                            updateEntity.setInitNextSiteId(dto.getSiteCode().longValue());
                        }
                    }
                    Date time = new Date();
                    updateEntity.setInitTime(time);
                    updateEntity.setUpdateTime(time);
                    jyPickingSendRecordDao.initUpdateIfExist(updateEntity);
                } else {
                    logInfo("提货扫描明细初始化，重复数据，不做初始化，data={}", JsonHelper.toJson(paramDto));
                }
            }
        }catch (Exception ex) {
            log.error("提货扫描明细初始化异常，errMsg={}, data={}", ex.getMessage(), JsonHelper.toJson(paramDto), ex);
            throw new JyBizException(String.format("提货明细初始化服务异常%s:%s:%s:%s", paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode(), ex.getMessage()));
        }finally {
            pickingGoodsCacheService.unlockPickingGoodDetailRecordInit(paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode());
        }
    }


}
