package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.mobilePhoneMsg.sender.client.request.BatchSmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.response.BatchSmsTemplateResponse;

/**
 * 短信发送
 *
 * @author: hujiping
 * @date: 2020/2/10 16:32
 */
public interface SmsMessageManager {

    /**
     * 短信模板单条发送
     * @see <a>https://cf.jd.com/pages/viewpage.action?pageId=106701898</a>
     * @param senderNum 注册时所填写的短信账号
     * @param templateId 短信模板ID
     * @param templateParam 模板参数
     * @param mobileNum 手机号码
     * @param token token
     * @param extension 扩展字段(建议可按“系统名_应用名_业务识别码”格式进行填写)
     * @return
     */
    InvokeResult sendSmsTemplateMessage(String senderNum, Long templateId,
                                        String[] templateParam, String mobileNum, String token, String extension);

    /**
     * 短信模板批量发送
     * @param batchSmsTemplateMessage
     * @return
     */
    BatchSmsTemplateResponse sendBatchSmsTemplateMessage(BatchSmsTemplateMessage batchSmsTemplateMessage);

}
