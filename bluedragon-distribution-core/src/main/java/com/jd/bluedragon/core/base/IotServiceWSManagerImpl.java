package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.ConstantEnums;
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
import org.apache.commons.lang.StringUtils;
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
    private static final Logger log = LoggerFactory.getLogger(IotServiceWSManagerImpl.class);

    @Autowired
    private IotQueryWS iotQueryWS;

    @Autowired
    private IotDeviceWS iotDeviceWS;

    /**
     * 系统来源编号
     */
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
            log.warn("调用iot接口返回失败commonDto[{}]deviceNo[{}]", JsonHelper.toJson(commonDto),deviceNo);
            return Boolean.FALSE;
        }
        if(commonDto.getData() == null){
            log.warn("调用iot接口返回data空deviceNo[{}]", deviceNo);
            return Boolean.FALSE;
        }
        if(!Objects.equals(DeviceStatusEnum.FREE.getKey(),commonDto.getData().getStatus())){
            log.warn("鸡毛信设备不可用deviceNo[{}],status:{}",deviceNo,commonDto.getData().getStatus());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 根据绑定标识查询绑定设备
     * @param waybillCode
     * @return true 有绑定，false 无绑定
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.IotServiceWSManagerImpl.queryBindDevice",mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean queryBindDevice(String waybillCode){
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setBindValue(waybillCode);
        deviceDto.setBindType(IotDeviceBindTypeEnum.WAYBILL.getKey());
        CommonDto<String> commonDto = iotQueryWS.queryBindDevice(deviceDto);
        if(commonDto == null || commonDto.getCode() != 1){
            log.warn("查询鸡毛信绑定状态-返回失败commonDto[{}]waybillCode[{}]", JsonHelper.toJson(commonDto),waybillCode);
            return Boolean.FALSE;
        }
        if(StringUtils.isEmpty(commonDto.getData())){
            log.warn("查询鸡毛信绑定状态-data为空waybillCode[{}]", waybillCode);
            return Boolean.FALSE;
        }
        log.debug("查询鸡毛信绑定状态-waybillCode[{}]data[{}]", waybillCode,commonDto.getData());
        return Boolean.TRUE;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.IotServiceWSManagerImpl.isDeviceCodeEnable",mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean bindDeviceWaybill(String deviceNo,String waybillCode,String erp,ConstantEnums.IotBusiness iotBusiness){
        BindDeviceDto bindDeviceDto = new BindDeviceDto();
        bindDeviceDto.setDeviceNo(deviceNo);
        bindDeviceDto.setBindValue(waybillCode);
        bindDeviceDto.setBindType(IotDeviceBindTypeEnum.WAYBILL.getKey());
        bindDeviceDto.setErp(erp);
        bindDeviceDto.setSystemCode(SYSTEM_CODE);
        if(iotBusiness != null){
            bindDeviceDto.setBusiness(iotBusiness.getType());
        }

        CommonDto commonDto = iotDeviceWS.bindDevice(bindDeviceDto);
        if(log.isDebugEnabled()){
            log.debug("鸡毛信运单处理-调用iot绑定鸡毛信设备bindDeviceDto[{}]",JsonHelper.toJson(bindDeviceDto));
        }
        if(commonDto == null || commonDto.getCode() != 1){
            log.warn("调用iot绑定鸡毛信设备接口返回失败commonDto[{}]deviceNo[{}]waybillCode[{}]",JsonHelper.toJson(commonDto),deviceNo,waybillCode);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }



}
