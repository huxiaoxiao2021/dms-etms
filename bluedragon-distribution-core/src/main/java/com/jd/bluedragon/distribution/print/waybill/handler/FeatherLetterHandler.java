package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.IotServiceWSManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 鸡毛信拦截校验
 */
@Service("featherLetterHandler")
public class FeatherLetterHandler implements Handler<WaybillPrintContext,JdResult<String>> {
    private static final Log logger= LogFactory.getLog(FeatherLetterHandler.class);

    @Autowired
    private IotServiceWSManager iotServiceWSManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        result.toSuccess();
        String waybillSign = context.getWaybill().getWaybillSign();
        if(!BusinessUtil.isFeatherLetter(waybillSign)){
            context.getResponse().setFeatherLetterWaybill(Boolean.FALSE);
            return result;
        }
        context.getResponse().setFeatherLetterWaybill(Boolean.TRUE);
        WaybillPrintRequest request = context.getRequest();
        //取消鸡毛信
        if(request.isCancelFeatherLetter()){
            return result;
        }
        //运单是否绑定过
        if(iotServiceWSManager.queryBindDevice(context.getWaybill().getWaybillCode())){
            return result;
        }
        //是否有设备号
        if(StringUtils.isEmpty(request.getFeatherLetterDeviceNo())){
            result.toFail(JdResponse.CODE_FEATHER_LETTER_ERROR, JdResponse.MESSAGE_FEATHER_LETTER_ERROR);
            return result;
        }
        //设备号是否可用
        Boolean isEnable = iotServiceWSManager.isDeviceCodeEnable(request.getFeatherLetterDeviceNo());
        if(Objects.equals(isEnable,Boolean.FALSE)){
            result.toFail(JdResponse.CODE_FEATHER_LETTER_DISABLE_ERROR, JdResponse.MESSAGE_FEATHER_LETTER_DISABLE_ERROR);
            return result;
        }
        return result;
    }


}
