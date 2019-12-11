package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.mail.MailProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SendMailUtil {
    private static final Logger log = LoggerFactory.getLogger(SendMailUtil.class);

    public static void sendSimpleEmail(String subject, String content, List<String> users) {
        if (StringHelper.isEmpty(subject) || StringHelper.isEmpty(content) || null == users
                || users.isEmpty()) {
			if (log.isInfoEnabled()) {
				log.info("邮件结构不完整，取消发送。邮件主题 [{}], 邮件内容 [{}], 收件人列表 [{}]"
						,subject,content,users);
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
					log.info("邮件收件人为空，取消发送。邮件主题 [{}], 邮件内容 [{}], 收件人列表 [{}]"
							,subject,content,users);
					continue;
				}
				index++;
				batchUsers.add(user);
				if (index > Constants.MAX_SEND_SIZE - 1) {
					mailProxy.sendSimpleEmail(subject, content, batchUsers);
					index = 0;// 计数归零
					batchUsers.clear();
				} else {
					continue;
				}
			}
			
			if(index>0)
				mailProxy.sendSimpleEmail(subject, content, batchUsers);
		} catch (Exception e) {
			log.error("邮件接口在 [{}], 发送邮件 邮件主题 [{}], 邮件内容 [{}], 收件人列表 [{}]"
					,currentTime,subject,content,users,e);
		}
    }
}
