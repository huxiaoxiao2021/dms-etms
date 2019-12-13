package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntity;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.distribution.sendprint.service.impl.SendPrintServiceImpl;
import com.jd.bluedragon.distribution.sendprint.utils.SendPrintConstants;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.BoardMeasureDto;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.BoardMeasureService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费发货消息，组装【发货交接清单打印】功能的实体
 */
@Service("dmsWorkSendDetailConsumer")
public class DmsWorkSendDetailConsumer extends MessageBaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(DmsWorkSendDetailConsumer.class);
    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    BoardCombinationService boardCombinationService;

    @Autowired
    private BoardMeasureService boardMeasureService;

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private SealBoxService tSealBoxService;

    @Autowired
    private DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    private SendPrintService sendPrintService;

    @Override
    @JProfiler(jKey = "DmsWorkSendDetailConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER,mState = {JProEnum.TP,JProEnum.Heartbeat})
    public void consume(Message message) throws Exception{
        CallerInfo info = null;
        try {
            info = Profiler.registerInfo( "DMSWORKER.DmsWorkSendDetailConsumer.buildBasicQueryEntity",false, true);
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("发货明细消息MQDmsWorkSendDetail-消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }

            /**将mq消息体转换成SendDetail对象**/
            SendDetail sendDetailMQ = JsonHelper.fromJson(message.getText(), SendDetail.class);
            if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
                log.warn("DmsWorkSendDetailConsumer:消息体[{}]转换实体失败或没有合法的包裹号",message.getText());
                return;
            }

            /**组装baicQueryEntity对象，写入es**/
            BasicQueryEntity basicQueryEntity = buildBasicQueryEntity(sendDetailMQ);
        }catch(Exception e){
            log.error("消费发货消息转换成BasicQueryEntity失败:{}",message.getText(),e);
            Profiler.functionError(info);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }


    private BasicQueryEntity buildBasicQueryEntity(SendDetail sendDetailMQ){
        /**获取操作包裹号、运单号、操作站点等基本信息**/
        String packageCode = sendDetailMQ.getPackageBarcode();
        Integer createSiteCode = sendDetailMQ.getCreateSiteCode();
        Integer receiveSiteCode = sendDetailMQ.getReceiveSiteCode();
        String cSiteName = toSiteName(createSiteCode);
        String rSiteName = toSiteName(receiveSiteCode);
        Integer rSiteType = toSiteType(receiveSiteCode);

        /**
         * 初始化BasicQueryEntity实体
         */
        BasicQueryEntity basicQueryEntity = new BasicQueryEntity();
        basicQueryEntity.setReceiveSiteName(rSiteName);
        basicQueryEntity.setReceiveSiteType(rSiteType);
        basicQueryEntity.setSendSiteName(cSiteName);


        /**通过包裹号查询发货明细**/
        SendDetail querySendDetail = new SendDetail();
        querySendDetail.setCreateSiteCode(createSiteCode);
        querySendDetail.setPackageBarcode(packageCode);
        querySendDetail.setReceiveSiteCode(receiveSiteCode);
        querySendDetail.setIsCancel(1);
        SendDetail sendDetail = sendDatailDao.queryOneSendDetailByPackageCode(querySendDetail);

        if(sendDetail == null){
            log.warn("未找到符合条件的发货记录.createSiteCode:{},receiveSiteCode：{},packageBarCode:{}",createSiteCode,receiveSiteCode,packageCode);
            return null ;
        }

        /** 填充基本信息**/
        buildBasicAttributes(basicQueryEntity, sendDetail);

        /** 如果站点的类型是售后类型，填充取件单信息**/
        if (rSiteType != null && rSiteType.equals(SendPrintServiceImpl.ASM_TYPE)) {
            buildPickUpParams(basicQueryEntity);
        }

        /**调运单接口，获取基本信息**/
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice (sendDetail.getWaybillCode(), true, true, true, true);
        if(baseEntity == null || baseEntity.getData() == null){
            log.warn("调运单接口，获取运单信息为空.waybillCode:{}",sendDetail.getWaybillCode());
            return null;
        }

        /** 填充运单信息**/
        buildWaybillAttributes(basicQueryEntity, baseEntity.getData(), rSiteType);

        /**填充应收体积重量信息**/
        buildWaybillWeightAndVolume(basicQueryEntity,baseEntity.getData());

        /**填充板的重量体积信息**/
        buildBoardWeightAndVolume(basicQueryEntity,sendDetail);

        /**填充应付（出分拣中心的体积）**/
        buildDmsOutVolume(basicQueryEntity,sendDetail);

        return basicQueryEntity;
    }

    private String toSiteName(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getSiteName();
    }

    private Integer toSiteType(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getSiteType();
    }

    /**
     * 组装BasicQueryEntity的基本信息
     * @param basicQueryEntity
     * @param sendDetail
     */
    private void buildBasicAttributes(BasicQueryEntity basicQueryEntity, SendDetail sendDetail) {
        if (sendDetail.getIsCancel() != null && sendDetail.getIsCancel() == 0) {
            basicQueryEntity.setIscancel(SendPrintConstants.TEXT_NO);
        } else {
            basicQueryEntity.setIscancel(SendPrintConstants.TEXT_YES);
        }
        basicQueryEntity.setIsnew(SendPrintConstants.TEXT_NO);
        basicQueryEntity.setPackageBarWeight(0.0);
        basicQueryEntity.setPackageBarWeight2(0.0);
        basicQueryEntity.setSiteCode(0);
        basicQueryEntity.setFcNo(0);
        basicQueryEntity.setPackageBar(sendDetail.getPackageBarcode());
        basicQueryEntity.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        basicQueryEntity.setSendCode(sendDetail.getSendCode());
        basicQueryEntity.setSendUser(sendDetail.getCreateUser());
        basicQueryEntity.setSendUserCode(sendDetail.getCreateUserCode());
        basicQueryEntity.setWaybill(sendDetail.getWaybillCode());
        basicQueryEntity.setInvoice(sendDetail.getPickupCode());
        basicQueryEntity.setBoxCode(sendDetail.getBoxCode());

        SealBox tSealBox = this.tSealBoxService.findByBoxCode(sendDetail.getBoxCode());
        if (tSealBox != null) {
            basicQueryEntity.setSealNo(tSealBox.getCode());
            basicQueryEntity.setSealTime(DateHelper.formatDateTime(tSealBox.getCreateTime()));
        }
    }

    /**
     * 如果有取件单号，补充取件单的信息
     * @param basicQueryEntity
     * @return
     */
    private void buildPickUpParams(BasicQueryEntity basicQueryEntity) {
        if (StringUtils.isNotBlank(basicQueryEntity.getInvoice())) {
            try {
                BaseEntity<PickupTask> tPickupTask = waybillPickupTaskApi.getPickTaskByPickCode(basicQueryEntity.getInvoice());
                if (tPickupTask != null && tPickupTask.getResultCode() > 0) {
                    PickupTask mPickupTask = tPickupTask.getData();
                    if (mPickupTask != null) {
                        basicQueryEntity.setInvoice(mPickupTask.getInvoiceId());
                        if (mPickupTask.getNewWaybillCode() != null)
                            basicQueryEntity.setIsnew(SendPrintConstants.TEXT_YES);
                    }
                } else {
                    log.warn("打印交接清单-取件单基础信息调用失败，运单号：{}，查询结果：{}" ,basicQueryEntity.getWaybill(), JsonHelper.toJson(tPickupTask));
                }
            } catch (Exception e) {
                log.error("打印交接清单-取件单基础信息调用发生异常:{}" , basicQueryEntity.getWaybill(), e);
            }
        }
    }

    /**
     * 填充BasicQueryEntity的运单信息
     * @param basicQueryEntity
     * @param data
     * @param rSiteType
     */
    private void buildWaybillAttributes(BasicQueryEntity basicQueryEntity, BigWaybillDto data, Integer rSiteType) {
        Waybill waybill = data.getWaybill();
        WaybillManageDomain waybillState = data.getWaybillState();

        // 设置基本的运单信息
        basicQueryEntity.setDeclaredValue(waybill.getCodMoney());
        basicQueryEntity.setGoodValue(waybill.getPrice() == null ? SendPrintConstants.TEXT_DEFAULT_PRICE : waybill.getPrice());
        basicQueryEntity.setGoodWeight(waybill.getGoodWeight() == null ? 0.0 : waybill.getGoodWeight());
        basicQueryEntity.setGoodWeight2(waybill.getAgainWeight() == null ? 0.0 : waybill.getAgainWeight());
        basicQueryEntity.setPackageBarNum(waybill.getGoodNumber() == null ? 0 : waybill.getGoodNumber());
        basicQueryEntity.setWaybillType(waybill.getWaybillType() == null ? SendPrintConstants.TEXT_GENERAL_ORDER : sendPrintService.getWaybillType(waybill.getWaybillType()));
        basicQueryEntity.setReceiverName(waybill.getReceiverName());
        basicQueryEntity.setRoadCode(waybill.getRoadCode());

        //设置FCNo
        if (waybillState != null && waybillState.getStoreId() != null) {
            basicQueryEntity.setFcNo(waybillState.getStoreId());
        }

        //设置站点信息
        Integer oldSiteId = waybill.getOldSiteId();
        if (oldSiteId != null) {
            basicQueryEntity.setSiteCode(oldSiteId);
            BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(oldSiteId);
            if (bDto != null) {
                basicQueryEntity.setSiteName(bDto.getSiteName());
                Integer siteType = bDto.getSiteType();
                if (siteType != null && !siteType.equals(16)) {
                    basicQueryEntity.setSiteType(SendPrintConstants.TEXT_SELF_SUPPORT);
                }
            }
        }

        //设置收件人信息-
        if (rSiteType.equals(16)) {
            basicQueryEntity.setReceiverAddress(waybill.getReceiverAddress() == null ? "" : waybill.getReceiverAddress());
            if (waybill.getReceiverMobile() == null && waybill.getReceiverTel() == null) {
                basicQueryEntity.setReceiverMobile(SendPrintConstants.TEXT_DOUBLE_BAR);
            } else {
                basicQueryEntity.setReceiverMobile("");
            }
        } else {
            basicQueryEntity.setReceiverMobile(SendPrintConstants.TEXT_DOUBLE_BAR);
        }

        //设置支付方式
        Integer payment = waybill.getPayment();
        if (payment == null) {
            basicQueryEntity.setPayment(0);
            basicQueryEntity.setSendPay("");
        } else {
            basicQueryEntity.setPayment(payment);
            basicQueryEntity.setSendPay(sendPrintService.getSendPay(payment));
            if (payment != 1 && payment != 3) {
                basicQueryEntity.setDeclaredValue(SendPrintConstants.TEXT_ZERO);
            }
        }

        //设置是否是奢侈品
        String sendPay = waybill.getSendPay();

        if (sendPay != null && sendPay.charAt(19) == '1') {
            basicQueryEntity.setLuxury(SendPrintConstants.TEXT_YES);
        } else {
            basicQueryEntity.setLuxury(SendPrintConstants.TEXT_NO);
        }
    }


    /**
     * 填充应收(入分拣中心)的重量体积信息
     * @param basicQueryEntity
     * @param data
     */
    private void buildWaybillWeightAndVolume(BasicQueryEntity basicQueryEntity, BigWaybillDto data){
        List<DeliveryPackageD> deliveryPackage = data.getPackageList();

        //一单多件的，循环定位当前包裹，并设置重量，体积信息
        if (deliveryPackage != null && !deliveryPackage.isEmpty() && BusinessHelper.checkIntNumRange(deliveryPackage.size())) {
            for (DeliveryPackageD delivery : deliveryPackage) {
                if (delivery.getPackageBarcode().equals(basicQueryEntity.getPackageBar())) {
                    basicQueryEntity.setPackageBarWeight(delivery.getGoodWeight());//运单上的包裹重量
                    basicQueryEntity.setPackageBarWeight2(delivery.getAgainWeight()); //复重

                    //调用运单接口，获取称重流水
                    PackOpeFlowDto packOpeFlow = null;
                    try {
                        String waybillCode = basicQueryEntity.getWaybill();
                        BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
                        for (PackOpeFlowDto packOpeFlowDto : packageOpe.getData()) {
                            if (packOpeFlowDto.getPackageCode().equals(basicQueryEntity.getPackageBar())) {
                                packOpeFlow = packOpeFlowDto;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("获取包裹量方信息接口失败:{}",basicQueryEntity.getWaybill(), e);
                    }

                    //如果获取到的称重流水不为空，则设置体积
                    if (null != packOpeFlow && null != packOpeFlow.getpLength() && null != packOpeFlow.getpWidth() && null != packOpeFlow.getpHigh()
                            && packOpeFlow.getpLength() > 0 && packOpeFlow.getpWidth() > 0 && packOpeFlow.getpHigh() > 0) {
                        basicQueryEntity.setGoodVolume(packOpeFlow.getpLength() * packOpeFlow.getpWidth() * packOpeFlow.getpHigh());
                    } else{
                        basicQueryEntity.setGoodVolume(0.0);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 填充板号及板体积信息
     * @param basicQueryEntity
     * @param sendDetail
     */
    private void buildBoardWeightAndVolume(BasicQueryEntity basicQueryEntity,SendDetail sendDetail){
        //查send_m看是否有板号
        SendM sendMQuery = new SendM();
        sendMQuery.setCreateSiteCode(sendDetail.getCreateSiteCode());
        sendMQuery.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        sendMQuery.setBoxCode(sendDetail.getBoxCode());
        SendM sendM = sendMDao.selectOneByBoxCode(sendMQuery);

        //有板号，调TC接口查询是否称过
        if(sendM != null && StringUtils.isNotBlank(sendM.getBoardCode())){
            List<String> boardCodeList = new ArrayList<String> ();
            boardCodeList.add(sendM.getBoardCode());

            Response<List<BoardMeasureDto>> tcResponse = boardMeasureService.getBoardMeasure(boardCodeList);
            if(tcResponse != null && tcResponse.getData() != null && tcResponse.getData().size() >0){
                basicQueryEntity.setBoardCode(sendM.getBoardCode());
                basicQueryEntity.setBoardVolume(tcResponse.getData().get(0).getVolume());
            }
        }
    }

    /**
     * 填充应付（出分拣中心）的体积
     * 如果包裹装在箱里，按照箱操作过应付量方，该包裹的体积赋值为箱的体积
     * 如果没有装在箱里，则赋值为包裹的应付体积
     * @param basicQueryEntity
     * @param sendDetail
     */
    private void buildDmsOutVolume(BasicQueryEntity basicQueryEntity, SendDetail sendDetail){
        DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(sendDetail.getBoxCode(),sendDetail.getCreateSiteCode());
        if(weightAndVolume == null && BusinessHelper.isBoxcode(sendDetail.getBoxCode())){
            weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(sendDetail.getPackageBarcode(),sendDetail.getCreateSiteCode());
        }
        if(weightAndVolume != null) {
            Integer operateType = weightAndVolume.getOperateType();
            if (operateType.equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)) {
                basicQueryEntity.setDmsOutVolumeStatic(weightAndVolume.getVolume());
            } else if (operateType.equals(DmsOutWeightAndVolume.OPERATE_TYPE_DYNAMIC)) {
                basicQueryEntity.setDmsOutVolumeDynamic(weightAndVolume.getVolume());
            }
        }
    }

}
