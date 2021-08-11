package com.jd.bluedragon.distribution.delivery;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.delivery.processor.IDeliveryBaseHandler;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author wyh
 * @className DeliveryOperationService
 * @description
 * @date 2021/8/4 16:48
 **/
@Service
public class DeliveryOperationServiceImpl implements IDeliveryOperationService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryOperationServiceImpl.class);

    @Autowired
    @Qualifier("deliveryPackageHandler")
    private IDeliveryBaseHandler packageHandler;

    @Autowired
    @Qualifier("deliveryBoxHandler")
    private IDeliveryBaseHandler boxHandler;

    @Autowired
    @Qualifier("deliveryWaybillHandler")
    private IDeliveryBaseHandler waybillHandler;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    /**
     * 按包裹、箱号、运单处理发货数据
     * @param requests
     * @param sourceEnum
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.asyncHandleDelivery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeliveryResponse asyncHandleDelivery(List<SendM> requests, SendBizSourceEnum sourceEnum) {

        SendMWrapper packageSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_PACKAGE);
        SendMWrapper boxSendWrapper = new SendMWrapper(SendKeyTypeEnum.BY_BOX);
        SendMWrapper waybillWrapper = new SendMWrapper(SendKeyTypeEnum.BY_WAYBILL);

        // 单次发货的公共属性
        SendM sendM = makeDeliveryDomain(requests.get(0), sourceEnum);

        // 每次发货的操作时间、操作人、批次号等认为是一样的
        packageSendWrapper.setSendM(sendM);
        boxSendWrapper.setSendM(sendM);
        waybillWrapper.setSendM(sendM);

        for (SendM request : requests) {
            String barCode = request.getBoxCode();
            if (WaybillUtil.isPackageCode(barCode)) {
                packageSendWrapper.add(barCode);
            }
            else if (BusinessUtil.isBoxcode(barCode)) {
                boxSendWrapper.add(barCode);
            }
            else if (WaybillUtil.isWaybillCode(barCode)) {
                waybillWrapper.add(barCode);
            }
        }

        if (CollectionUtils.isNotEmpty(packageSendWrapper.getBarCodeList())) {
            packageHandler.initDeliveryTask(packageSendWrapper);
        }

        if (CollectionUtils.isNotEmpty(boxSendWrapper.getBarCodeList())) {
            boxHandler.initDeliveryTask(boxSendWrapper);
        }

        if (CollectionUtils.isNotEmpty(waybillWrapper.getBarCodeList())) {
            waybillHandler.initDeliveryTask(waybillWrapper);
        }

        return DeliveryResponse.oK();
    }

    private SendM makeDeliveryDomain(SendM request, SendBizSourceEnum sourceEnum) {
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getCreateSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        sendM.setCreateUserCode(request.getCreateUserCode());
        sendM.setSendType(request.getSendType());
        sendM.setCreateUser(request.getCreateUser());
        sendM.setSendCode(request.getSendCode());
        sendM.setCreateTime(request.getCreateTime());
        sendM.setOperateTime(request.getOperateTime());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(request.getTurnoverBoxCode());
        sendM.setTransporttype(request.getTransporttype());
        sendM.setBizSource(sourceEnum.getCode());
        return sendM;
    }

    /**
     * 处理发货任务
     * @param task
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DeliveryOperationService.dealDeliveryTask", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealDeliveryTask(Task task) {

        final SendMWrapper wrapper = JsonHelper.fromJson(task.getBody(), SendMWrapper.class);
        if (null == wrapper || null == wrapper.getKeyType()) {
            return;
        }

        SendKeyTypeEnum typeEnum = wrapper.getKeyType();
        switch (typeEnum) {
            case BY_PACKAGE:

                packageHandler.dealCoreDelivery(wrapper);
                break;
            case BY_BOX:

                boxHandler.dealCoreDelivery(wrapper);
                break;
            case BY_WAYBILL:

                waybillHandler.dealCoreDelivery(wrapper);
                break;
            default:
                break;
        }
    }

    /**
     * 发货异步任务切换开关
     * @param createSiteCode
     * @return
     */
    @Override
    public boolean deliverySendAsyncSwitch(Integer createSiteCode) {
        String configSites = uccConfig.getDeliverySendAsyncSite();
        if (StringUtils.isBlank(configSites)) {
            return false;
        }
        if (null == createSiteCode) {
            return false;
        }
        // 全国场地生效
        if (Constants.STR_ALL.equalsIgnoreCase(configSites)) {
            return true;
        }

        List<String> enableSites = Arrays.asList(configSites.split(Constants.SEPARATOR_COMMA));
        return enableSites.contains(createSiteCode.toString());
    }
}
