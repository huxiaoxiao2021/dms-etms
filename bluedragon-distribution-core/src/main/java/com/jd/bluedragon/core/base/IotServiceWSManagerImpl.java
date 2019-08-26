package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.iot.dto.BindDeviceDto;
import com.jd.tms.iot.dto.CommonDto;
import com.jd.tms.iot.dto.DeviceDto;
import com.jd.tms.iot.enums.DeviceStatusEnum;
import com.jd.tms.iot.enums.IotDeviceBindTypeEnum;
import com.jd.tms.iot.ws.IotDeviceWS;
import com.jd.tms.iot.ws.IotQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * ioT jsf 接口
 * @author : xumigen
 * @date : 2019/8/12
 */

@Service("iotServiceWSManager")
public class IotServiceWSManagerImpl implements IotServiceWSManager {
    private static final Logger logger = LoggerFactory.getLogger(IotServiceWSManagerImpl.class);

    @Autowired
    private IotQueryWS iotQueryWS;

    @Autowired
    private IotDeviceWS iotDeviceWS;

    private static final Integer SYSTEM_CODE = 4;

    /**
     * 鸡毛信设备是否可用
     * @param deviceNo
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.IotServiceWSManagerImpl.isDeviceCodeEnable",mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean isDeviceCodeEnable(String deviceNo){
        CommonDto<DeviceDto> commonDto = iotQueryWS.queryDeviceStatus(deviceNo);
        if(commonDto == null || commonDto.getCode() != 1){
            logger.info("调用iot接口返回失败commonDto[{}]deviceNo[{}]", JsonHelper.toJson(commonDto),deviceNo);
            return Boolean.FALSE;
        }
        if(commonDto.getData() == null){
            logger.info("调用iot接口返回data空deviceNo[{}]", deviceNo);
            return Boolean.FALSE;
        }
        if(!Objects.equals(DeviceStatusEnum.FREE.getKey(),commonDto.getData().getStatus())){
            logger.info("鸡毛信设备不可用deviceNo[{}]status",deviceNo,commonDto.getData().getStatus());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.IotServiceWSManagerImpl.isDeviceCodeEnable",mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean bindDeviceWaybill(String deviceNo,String waybillCode,String erp){
        BindDeviceDto bindDeviceDto = new BindDeviceDto();
        bindDeviceDto.setDeviceNo(deviceNo);
        bindDeviceDto.setBindValue(waybillCode);
        bindDeviceDto.setBindType(IotDeviceBindTypeEnum.WAYBILL.getKey());
        bindDeviceDto.setErp(erp);
        bindDeviceDto.setSystemCode(SYSTEM_CODE);


        CommonDto commonDto = iotDeviceWS.bindDevice(bindDeviceDto);
        logger.info("鸡毛信运单处理-调用iot绑定鸡毛信设备bindDeviceDto[{}]",JsonHelper.toJson(bindDeviceDto));
        if(commonDto == null || commonDto.getCode() != 1){
            logger.info("调用iot绑定鸡毛信设备接口返回失败commonDto[{}]deviceNo[{}]waybillCode[{}]",new String[]{JsonHelper.toJson(commonDto),deviceNo,waybillCode});
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }



}
