package com.jd.bluedragon.distribution.consumer.sealVehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.SealVehicleSendCodeInfo;
import com.jd.bluedragon.distribution.sealVehicle.domain.SubmitSealVehicleDto;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sealVehicleFromWorkbenchConsumer")
public class SealVehicleFromWorkbenchConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(SealVehicleFromWorkbenchConsumer.class);

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        log.debug("sealVehicleFromWorkbenchConsumer consume --> 消息Body为【{}】",message.getText());

        if (StringHelper.isEmpty(message.getText())) {
            log.warn("sealVehicleFromWorkbenchConsumer consume --> 消息为空");
            return;
        }

        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("sealVehicleFromWorkbenchConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        SubmitSealVehicleDto submitSealVehicleDto = JsonHelper.fromJson(message.getText(), SubmitSealVehicleDto.class);
        if (submitSealVehicleDto == null) {
            log.warn("inventoryTaskCompleteConsumer consume --> 消息转换对象失败：{}" , message.getText());
            return;
        }

        try {
            NewSealVehicleResponse newSealVehicleResponse = newSealVehicleService.doSealCarWithVehicleJob(this.convert2SealCarDto(submitSealVehicleDto));
            if (newSealVehicleResponse != null && NewSealVehicleResponse.CODE_OK.equals(newSealVehicleResponse.getCode())){
                //写成功回执
            } else {
                //写失败回执
            }
        } catch (Exception e) {
            log.error("处理一键封车任务失败，参数：{}", JsonHelper.toJson(submitSealVehicleDto), e);
            //写失败回执
        }

    }

    private List<SealCarDto> convert2SealCarDto(SubmitSealVehicleDto submitSealVehicleDto){
        List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList = submitSealVehicleDto.getSealVehicleSendCodeInfoList();
        Map<String, SealCarDto> sealCarDtoMap = new HashMap<>();
        for (SealVehicleSendCodeInfo sealVehicleSendCodeInfo : sealVehicleSendCodeInfoList) {
            if (sealVehicleSendCodeInfo.getVehicleBaseInfo() != null &&  StringHelper.isNotEmpty(sealVehicleSendCodeInfo.getVehicleBaseInfo().getVehicleNumber())) {
                String vehicleNumber = sealVehicleSendCodeInfo.getVehicleBaseInfo().getVehicleNumber();
                if (sealCarDtoMap.containsKey(vehicleNumber)) {
                    SealCarDto sealCarDto = sealCarDtoMap.get(vehicleNumber);
                    sealCarDto.getBatchCodes().add(sealVehicleSendCodeInfo.getSendCode());
                } else {
                    SealCarDto sealCarDto = new SealCarDto();
                    ArrayList<String> batchCodes = new ArrayList<>();
                    batchCodes.add(sealVehicleSendCodeInfo.getSendCode());
                    sealCarDto.setBatchCodes(batchCodes);
                    sealCarDto.setSealCarTime(DateHelper.formatDate(submitSealVehicleDto.getOperateTime(), Constants.DATE_TIME_MS_FORMAT));
                    sealCarDto.setSealCodes(Arrays.asList(sealVehicleSendCodeInfo.getVehicleBaseInfo().getSealCode().split(Constants.SEPARATOR_COMMA)));
                    sealCarDto.setSealSiteId(submitSealVehicleDto.getCreateSiteCode());
                    sealCarDto.setSealSiteName(submitSealVehicleDto.getCreateSiteName());
                    sealCarDto.setSealUserCode(submitSealVehicleDto.getOperatorCode().toString());
                    sealCarDto.setSealUserName(submitSealVehicleDto.getOperatorName());
                    sealCarDto.setSource(Constants.SEAL_SOURCE);
                    sealCarDto.setTransportCode(submitSealVehicleDto.getTransportCode());
                    sealCarDto.setVehicleNumber(sealVehicleSendCodeInfo.getVehicleBaseInfo().getVehicleNumber());
                    sealCarDto.setVolume(sealVehicleSendCodeInfo.getVehicleBaseInfo().getVolume());
                    sealCarDto.setWeight(sealVehicleSendCodeInfo.getVehicleBaseInfo().getWeight());
                    sealCarDtoMap.put(sealVehicleSendCodeInfo.getVehicleBaseInfo().getVehicleNumber(), sealCarDto);
                }
            }
        }
        return new ArrayList<>(sealCarDtoMap.values());
    }
}
