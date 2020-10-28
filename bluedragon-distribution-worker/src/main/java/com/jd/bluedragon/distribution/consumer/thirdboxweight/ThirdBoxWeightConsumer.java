package com.jd.bluedragon.distribution.consumer.thirdboxweight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ThirdBoxWeightConsumer")
public class ThirdBoxWeightConsumer extends MessageBaseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdBoxWeightConsumer.class);

    @Autowired
    private WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BoxService boxService;

    @Override
    @JProfiler(jKey = "DMSWORKER.ThirdBoxWeightConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER , mState = {JProEnum.TP, JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        if (!JsonHelper.isJsonString(message.getText())) {
            LOGGER.error("消息为非JSON格式，将抛弃：{}", message.getText());
            return;
        }
        try {
            WaybillSyncRequest messageDto = JsonHelper.fromJson(message.getText(), WaybillSyncRequest.class);

            if (messageDto == null || StringHelper.isEmpty(messageDto.getBoxCode()) || StringHelper.isEmpty(messageDto.getWaybillCode())) {
                LOGGER.error("消息中缺少关键字，将抛弃：{}", message.getText());
                return;
            }

            String boxCode = messageDto.getBoxCode();
            String waybillCode = messageDto.getWaybillCode();

            boolean boxBool = weightVolumeFlowJSFService.checkIfExistFlow(boxCode);
            boolean waybillBool = weightVolumeFlowJSFService.checkIfExistFlow(waybillCode);
            LOGGER.info("箱号「{}」称重结果:{}，运单「{}」称重结果:{}。", boxCode, boxBool, waybillCode, waybillBool);

            //如果存在箱号的称重记录，不存在运单的称重记录，则补称重记录
            if (boxBool && !waybillBool) {

                Box box = boxService.findBoxByCode(boxCode);
                if (null == box) {
                    LOGGER.error("查询众邮箱号失败：{}", boxCode);
                    return;
                }

                WeightVolumeEntity itemEntity = new WeightVolumeEntity();
                itemEntity.setBarCode(waybillCode);
                itemEntity.setWaybillCode(waybillCode);
                itemEntity.setBoxCode(boxCode);
                itemEntity.setVolume(1D);
                itemEntity.setWeight(1D);
                itemEntity.setLength(1D);
                itemEntity.setWidth(1D);
                itemEntity.setHeight(1D);
                itemEntity.setOperateSiteCode(box.getReceiveSiteCode());
                itemEntity.setOperateSiteName(box.getReceiveSiteName());
                itemEntity.setOperatorId(-1);
                itemEntity.operatorName(messageDto.getOperatorName());
                itemEntity.setOperateTime(messageDto.getOperatorTime());
                itemEntity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL);
                itemEntity.setSourceCode(FromSourceEnum.DMS_INNER_SPLIT);
                weightVolumeHandlerStrategy.doHandler(itemEntity);
            }

        } catch (RuntimeException e) {
            LOGGER.error("消费消息发生异常：{}",message.getText(),e);
        }

    }
}
