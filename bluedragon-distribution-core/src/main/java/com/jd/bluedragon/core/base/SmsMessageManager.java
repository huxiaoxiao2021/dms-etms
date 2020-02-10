package com.jd.bluedragon.core.base;

import com.jd.mobilePhoneMsg.sender.client.request.BatchSmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.request.SmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.response.BatchSmsTemplateResponse;
import com.jd.mobilePhoneMsg.sender.client.response.SmsTemplateResponse;

/**
 * 短信发送
 *
 * @author: hujiping
 * @date: 2020/2/10 16:32
 */
public interface SmsMessageManager {

    /**
     * 短信模板单条发送
     * @param smsTemplateMessage
     * @return
     */
    SmsTemplateResponse sendSmsTemplateMessage(SmsTemplateMessage smsTemplateMessage);

    /**
     * 短信模板批量发送
     * @param batchSmsTemplateMessage
     * @return
     */
    BatchSmsTemplateResponse sendBatchSmsTemplateMessage(BatchSmsTemplateMessage batchSmsTemplateMessage);

}
