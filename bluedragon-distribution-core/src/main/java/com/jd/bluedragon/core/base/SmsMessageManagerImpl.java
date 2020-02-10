package com.jd.bluedragon.core.base;

import com.jd.mobilePhoneMsg.sender.client.request.BatchSmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.request.SmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.response.BatchSmsTemplateResponse;
import com.jd.mobilePhoneMsg.sender.client.response.SmsTemplateResponse;
import com.jd.mobilePhoneMsg.sender.client.service.SmsMessageTemplateRpcService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 短信发送实现
 *
 * @author: hujiping
 * @date: 2020/2/10 16:36
 */
@Service("smsMessageManager")
public class SmsMessageManagerImpl implements SmsMessageManager {

    private Logger log = LoggerFactory.getLogger(SmsMessageManagerImpl.class);

    @Autowired
    private SmsMessageTemplateRpcService smsService;

    /**
     * 短信模板单条发送
     * @see <a>https://cf.jd.com/pages/viewpage.action?pageId=106701898</a>
     * @param smsTemplateMessage
     *  senderNum 注册时所填写的短信账号
     *  templateId 短信模板ID
     *  templateParam 模板参数
     *  mobileNum 手机号码
     *  token token
     *  extension 扩展字段(建议可按“系统名_应用名_业务识别码”格式进行填写)
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.SmsMessageManagerImpl.sendSmsTemplateMessage", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public SmsTemplateResponse sendSmsTemplateMessage(SmsTemplateMessage smsTemplateMessage) {
        return smsService.sendSmsTemplateMessage(smsTemplateMessage);
    }

    /**
     * 短信模板批量发送
     * @param batchSmsTemplateMessage
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.SmsMessageManagerImpl.sendBatchSmsTemplateMessage", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BatchSmsTemplateResponse sendBatchSmsTemplateMessage(BatchSmsTemplateMessage batchSmsTemplateMessage) {
        return smsService.sendBatchSmsTemplateMessage(batchSmsTemplateMessage);
    }
}
