package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendRecordDao;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Autowired
    @Qualifier(value = "jyPickingGoodSaveWaitScanItemNumProducer")
    private DefaultJMQProducer jyPickingGoodSaveWaitScanItemNumProducer;



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
    public JyPickingSendRecordEntity fetchByPackageCodeAndCondition(Long curSiteId, String packageCode, String bizId) {
        JyPickingSendRecordEntity queryEntity = new JyPickingSendRecordEntity(curSiteId);
        queryEntity.setPackageCode(packageCode);
        queryEntity.setBizId(bizId);
        return jyPickingSendRecordDao.fetchByPackageCodeAndCondition(queryEntity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingSendRecordServiceImpl.initOrUpdateNeedScanDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void initOrUpdateNeedScanDetail(PickingGoodTaskDetailInitDto paramDto) {
        if(!pickingGoodsCacheService.lockPickingGoodDetailRecordInit(paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode())) {
            logWarn("提货明细初始化获取锁失败，异常重试，param={}", JsonHelper.toJson(paramDto));
            throw new JyBizException(String.format("提货明细初始化获取锁失败%s:%s:%s", paramDto.getPickingSiteId(), paramDto.getBizId(), paramDto.getPackageCode()));
        }
        try {

            JyPickingSendRecordEntity queryEntity = new JyPickingSendRecordEntity(paramDto.getPickingSiteId());
            queryEntity.setBizId(paramDto.getBizId());
            queryEntity.setPackageCode(paramDto.getPackageCode());
            JyPickingSendRecordEntity entityQueryRes = jyPickingSendRecordDao.fetchByPackageCodeAndCondition(queryEntity);

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
                this.sendAggWaitScanItemStatistics(insertEntity);

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
                    jyPickingSendRecordDao.fillInitWaitScanField(updateEntity);
                    this.sendAggWaitScanItemStatistics(updateEntity);
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


    private void sendAggWaitScanItemStatistics(JyPickingSendRecordEntity entity){

        List<Message> messageList = new ArrayList<>();
        CalculateWaitPickingItemNumDto nextItemNumDto = new CalculateWaitPickingItemNumDto();
        nextItemNumDto.setBizId(entity.getBizId());
        nextItemNumDto.setCode(entity.getWaitScanCode());
        if(JyPickingSendRecordEntity.SCAN_BOX.equals(entity.getWaitScanCodeType())) {
            nextItemNumDto.setCodeType(CalculateWaitPickingItemNumDto.CODE_TYPE_BOX_CODE);
        }else {
            nextItemNumDto.setCodeType(CalculateWaitPickingItemNumDto.CODE_TYPE_PACKAGE_CODE);
        }
        nextItemNumDto.setNextSiteId(entity.getInitNextSiteId());
        nextItemNumDto.setPickingSiteId(entity.getPickingSiteId());
        nextItemNumDto.setWaitPickingItemNum(1);
        nextItemNumDto.setCalculateNextSiteAggFlag(true);
        Long time = System.currentTimeMillis();
        nextItemNumDto.setOperateTime(time);
        nextItemNumDto.setSysTime(time);
        String businessId = String.format("%s|%s", nextItemNumDto.getBizId(), nextItemNumDto.getCode());

        String msgText1 = JsonUtils.toJSONString(nextItemNumDto);
        logInfo("dmsToDms计算bizId发货流向维度待提总件数发送消息，businessId={},msg={}", businessId, msgText1);
        messageList.add(new Message(jyPickingGoodSaveWaitScanItemNumProducer.getTopic(), msgText1, businessId));
        jyPickingGoodSaveWaitScanItemNumProducer.batchSendOnFailPersistent(messageList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingSendRecordServiceImpl.pickingRecordSave",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void pickingRecordSave(JyPickingGoodScanDto scanDto) {

        JyPickingSendRecordEntity entityRes = this.fetchByPackageCodeAndCondition(scanDto.getPickingSiteId(), scanDto.getPackageCode(), scanDto.getBizId());
        if(Objects.isNull(entityRes)) {
            JyPickingSendRecordEntity insertEntity = generatePickingScanEntity(scanDto);
            insertEntity.setCreateTime(new Date());
            insertEntity.setUpdateTime(new Date());
            jyPickingSendRecordDao.insertSelective(insertEntity);

        }else if(!Constants.NUMBER_ONE.equals(entityRes.getScanFlag())) {
            JyPickingSendRecordEntity updateEntity = generatePickingScanEntity(scanDto);
            updateEntity.setUpdateTime(new Date());
            jyPickingSendRecordDao.fillRealScanField(updateEntity);

        }else {
            logInfo("提货扫描存储重复数据，不做处理，scanDto={}", JsonHelper.toJson(scanDto));
        }
    }
    public JyPickingSendRecordEntity generatePickingScanEntity(JyPickingGoodScanDto scanDto) {
        Date time = new Date();
        JyPickingSendRecordEntity entity = new JyPickingSendRecordEntity(scanDto.getPickingSiteId());
        entity.setBizId(scanDto.getBizId());
        entity.setPickingNodeCode(scanDto.getEndNodeCode());
        entity.setPackageCode(scanDto.getPackageCode());
        entity.setWaybillCode(WaybillUtil.getWaybillCode(scanDto.getPackageCode()));
        entity.setScanFlag(Constants.NUMBER_ONE);
        entity.setScanCode(scanDto.getBarCode());
        entity.setUpdateTime(time);
        entity.setWaitScanFlag(Constants.NUMBER_ZERO);
        if(BusinessUtil.isBoxcode(scanDto.getBarCode())) {
            entity.setScanCodeType(JyPickingSendRecordEntity.SCAN_BOX);
        }
        else if(WaybillUtil.isPackageCode(scanDto.getBarCode())) {
            entity.setScanCodeType(JyPickingSendRecordEntity.SCAN_PACKAGE);
        }
        entity.setMoreScanFlag(Boolean.TRUE.equals(scanDto.getMoreScanFlag()) ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
        entity.setPickingUserErp(scanDto.getUser().getUserErp());
        entity.setPickingUserName(scanDto.getUser().getUserName());
        entity.setPickingTime(new Date(scanDto.getOperateTime()));

        if(Boolean.TRUE.equals(scanDto.getSendGoodFlag())) {
            entity.setSendFlag(Constants.NUMBER_ONE);
            entity.setRealNextSiteId(scanDto.getNextSiteId());
            entity.setBoxRealFlowKey(scanDto.getBoxConfirmNextSiteKey());
            entity.setMoreSendFlag(Boolean.TRUE.equals(scanDto.getForceSendFlag()) ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
            entity.setSendTime(new Date(scanDto.getOperateTime()));
            entity.setSendUserErp(entity.getPickingUserErp());
            entity.setSendUserName(entity.getPickingUserName());
        }
        return entity;
    }


}
