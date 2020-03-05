package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.mobilePhoneMsg.sender.client.request.BatchSmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.request.SmsTemplateMessage;
import com.jd.mobilePhoneMsg.sender.client.response.BaseResultMsg;
import com.jd.mobilePhoneMsg.sender.client.response.BatchSmsTemplateResponse;
import com.jd.mobilePhoneMsg.sender.client.response.ResultMsg;
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
     * resultMsg 成功标识
     * */
    private final String resultMsgSuccess = "0";
    /**
     * baseResultMsg 成功标识
     * */
    private final String baseResultMsgSuccess = "999";

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
    @JProfiler(jKey = "DMS.BASE.SmsMessageManagerImpl.sendSmsTemplateMessage", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult sendSmsTemplateMessage(String senderNum,Long templateId,
                                                      String[] templateParam,String mobileNum,String token,String extension) {
        InvokeResult result = new InvokeResult();
        SmsTemplateMessage smsTemplateMessage = new SmsTemplateMessage();
        smsTemplateMessage.setSenderNum(senderNum);
        smsTemplateMessage.setTemplateId(templateId);
        smsTemplateMessage.setTemplateParam(templateParam);
        smsTemplateMessage.setMobileNum(mobileNum);
        smsTemplateMessage.setToken(token);
        smsTemplateMessage.setExtension(extension);
        log.info("冷链卡班暂存收费短信入参对象：{}",JsonHelper.toJson(smsTemplateMessage));
        SmsTemplateResponse response = smsService.sendSmsTemplateMessage(smsTemplateMessage);

        if(response != null){
            ResultMsg resultMsg = response.getResultMsg();
            BaseResultMsg baseResultMsg = response.getBaseResultMsg();
            if(resultMsg == null){
                result.customMessage(InvokeResult.RESULT_NULL_CODE,InvokeResult.RESULT_NULL_MESSAGE);
            }else if(!resultMsgSuccess.equals(resultMsg.getErrorCode())) {
                result.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,resultMsg.getErrorMsg());
            }else if(baseResultMsg == null){
                result.customMessage(InvokeResult.RESULT_NULL_CODE,InvokeResult.RESULT_NULL_MESSAGE);
            }else if(!baseResultMsgSuccess.equals(baseResultMsg.getErrorCode())){
                result.customMessage(InvokeResult.RESULT_NULL_CODE,baseResultMsg.getErrorMsg());
            }else {
                log.info("冷链卡班暂存收费短信发送成功");
                result.success();
            }
        }else {
            result.customMessage(InvokeResult.RESULT_NULL_CODE,InvokeResult.RESULT_NULL_MESSAGE);
        }

        return result;
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
