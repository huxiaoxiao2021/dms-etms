package com.jd.bluedragon.distribution.consumer.thirdboxweight;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
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

    @Override
    public void consume(Message message) throws Exception {

        if (!JsonHelper.isJsonString(message.getText())) {
            LOGGER.error("消息为非JSON格式，将抛弃：{}", message.getText());
            return;
        }
        try {
            WaybillSyncRequest messageDto = JsonHelper.fromJsonUseGson(message.getText(), WaybillSyncRequest.class);

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
                Integer staffId = Integer.valueOf(messageDto.getOperatorId());
                BaseStaffSiteOrgDto staffSiteDTO = baseMajorManager.getBaseStaffByStaffId(staffId);

                WeightVolumeEntity itemEntity = new WeightVolumeEntity();
                itemEntity.setBarCode(waybillCode);
                itemEntity.setWaybillCode(waybillCode);
                itemEntity.setBoxCode(boxCode);
                itemEntity.setVolume(1D);
                itemEntity.setWeight(1D);
                itemEntity.setLength(1D);
                itemEntity.setWidth(1D);
                itemEntity.setHeight(1D);
                itemEntity.setOperateSiteCode(Integer.valueOf(messageDto.getStartSiteCode()));
                itemEntity.setOperateSiteName(messageDto.getOperatorUnitName());
                itemEntity.setOperatorId(Integer.valueOf(messageDto.getOperatorId()));
                if (staffSiteDTO != null && StringHelper.isNotEmpty(staffSiteDTO.getErp())) {
                    itemEntity.setOperatorCode(staffSiteDTO.getErp());
                }
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
