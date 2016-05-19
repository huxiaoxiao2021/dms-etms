package com.jd.bluedragon.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.api.WaybillExtendApi;
import com.jd.etms.waybill.domain.BaseEntity;

public class SendSMSUtil {
    private static final Log logger = LogFactory.getLog(SendSMSUtil.class);

    public static Boolean send(String busiCode, Integer msgType, String msg, String customerMobile) {
        if (StringHelper.isEmpty(customerMobile) || StringHelper.isEmpty(msg)) {
            logger.info("短信结构不完整，取消发送。电话号码 [" + customerMobile + "], 短信内容 [" + msg + "]");
            return false;
        }

        WaybillExtendApi waybillAddWS = (WaybillExtendApi) SpringHelper.getBean("waybillExtendApiJsf");
        BaseEntity<Boolean> result = null;
        Long currentTime = System.currentTimeMillis();
        try {
        	result = waybillAddWS.sendBdSms(busiCode, msgType, msg, customerMobile);
        } catch (Exception e) {
            logger.error("短信接口在 [" + currentTime + "], 发送短信 [" + msg
                    + "], 到收件人 [" + customerMobile + "] 失败。" ,e);
        }
		if (result == null)
			return false;
		else
			return result.getData();
    }
    
    /**
     * 发送通知短信
     * @param busiCode
     * @param msg
     * @param customerMobile
     */
    public static Boolean sendNotice(String busiCode, String msg, String customerMobile) {
        return SendSMSUtil.send(busiCode, WaybillExtendApi.NOTICE, msg, customerMobile);
    }
    
    
}
