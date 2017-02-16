package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.mail.MailProxy;

public class SendMailUtil {
    private static final Log logger = LogFactory.getLog(SendMailUtil.class);

    public static void sendSimpleEmail(String subject, String content, List<String> users) {
        if (StringHelper.isEmpty(subject) || StringHelper.isEmpty(content) || null == users
                || users.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("邮件结构不完整，取消发送。邮件主题 [" + subject + "], 邮件内容 [" + content + "], 收件人列表 [" + users + "]");
			}
            return;
        }

        MailProxy mailProxy = (MailProxy) SpringHelper.getBean("mailProxy");
        Long currentTime = System.currentTimeMillis();
		try {
			// 对mail接收人进行分堆,每500分为一批
			List<String> batchUsers = new ArrayList<String>();
			int index = 0;
			for (String user : users) {
				if (StringHelper.isEmpty(user)) {
					logger.info("邮件收件人为空，取消发送。邮件主题 [" + subject + "], 邮件内容[" + content + "], 收件人[" + user + "]");
					continue;
				}
				index++;
				batchUsers.add(user);
				if (index > Constants.MAX_SEND_SIZE - 1) {
					mailProxy.sendSimpleEmail(subject, content, users);
					index = 0;// 计数归零
					batchUsers.clear();
				} else {
					continue;
				}
			}
			
			if(index>0)
				mailProxy.sendSimpleEmail(subject, content, batchUsers);
		} catch (Exception e) {
			logger.error("邮件接口在 [" + currentTime + "], 发送邮件 [" + subject + "], 内容 [" + content + "], 到收件人 [" + users
					+ "] 失败。", e);
		}
    }
}
