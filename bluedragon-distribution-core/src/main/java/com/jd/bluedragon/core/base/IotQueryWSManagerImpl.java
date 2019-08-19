package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.iot.dto.CommonDto;
import com.jd.tms.iot.dto.DeviceDto;
import com.jd.tms.iot.enums.DeviceStatusEnum;
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

@Service("iotQueryWSManagerImpl")
public class IotQueryWSManagerImpl implements IotQueryWSManager{
    private static final Logger logger = LoggerFactory.getLogger(IotQueryWSManagerImpl.class);

    @Autowired
    private IotQueryWS iotQueryWS;

    /**
     * 鸡毛信设备是否可用
     * @param deviceNo
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.IotQueryWSManagerImpl.isDeviceCodeEnable",mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean isDeviceCodeEnable(String deviceNo){
        CommonDto<DeviceDto> commonDto = iotQueryWS.queryDeviceStatus(deviceNo);
        if(commonDto == null || commonDto.getCode() != 1){
            logger.info("调用iot接口返回失败commonDto[{}]", JsonHelper.toJson(commonDto));
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
}
