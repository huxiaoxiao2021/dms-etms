package com.jd.bluedragon.utils;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.etms.waybill.api.WaybillExtendApi;

public class SendMailUtil {
    private static final Log logger = LogFactory.getLog(SendMailUtil.class);

    public static void send(String subject, String content, List<String> users) {
        if (StringHelper.isEmpty(subject) || StringHelper.isEmpty(content) || null == users
                || users.isEmpty()) {
            logger.info("邮件结构不完整，取消发送。邮件主题 [" + subject + "], 邮件内容 [" + content + "], 收件人列表 ["
                    + users + "]");
            return;
        }

        WaybillExtendApi waybillAddWS = (WaybillExtendApi) SpringHelper.getBean("WaybillExtendApi");

        for (String user : users) {
            if (StringHelper.isEmpty(user)) {
                logger.info("邮件收件人为空，取消发送。邮件主题 [" + subject + "], 邮件内容[" + content + "], 收件人["
                        + user + "]");
                continue;
            }
            Long currentTime = System.currentTimeMillis();
            try {
                waybillAddWS.sendMail(user, subject, content, new Date(),
                        String.valueOf(currentTime).substring(1));
            } catch (Exception e) {
                logger.error("邮件接口在 [" + currentTime + "], 发送邮件 [" + subject + "], 内容 [" + content
                        + "], 到收件人 [" + user + "] 失败。", e);
            }
        }

        // SMSWebServiceSoap smsWebServiceSoap
        // =(SMSWebServiceSoap)SpringHelper.getBean("smsWebServiceSoap");
        // Mail mail = new Mail();
        // mail.setMailSubject(subject);
        // mail.setMailBody(content);
        // for(String user:users){
        // mail.setMailAddress(user);
        // try {
        // smsWebServiceSoap.sendMail(mail);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
    }
}
