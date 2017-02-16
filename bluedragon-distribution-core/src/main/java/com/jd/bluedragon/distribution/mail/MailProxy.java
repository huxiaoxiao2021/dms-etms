package com.jd.bluedragon.distribution.mail;

import java.util.List;

import com.jd.bluedragon.distribution.mail.service.IMailSender;
import com.jd.bluedragon.distribution.mail.dto.ImageMailDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 邮件发送代理
 * 
 * @author ligang
 * 
 */
public class MailProxy {
	private IMailSender commonsEmailSender;
	private String fromEmailAddress;
	private String fromEmailName;
	
	@JProfiler(jKey = "dms.MailProxy.sendSimpleEmail", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
	public boolean sendSimpleEmail(String subject, String msg,
			List<String> toEmailAddressList) {
		return commonsEmailSender.sendSimpleEmail(fromEmailAddress, fromEmailName, subject, msg, toEmailAddressList);
	}

    @JProfiler(jKey = "dms.MailProxy.sendHtmlEmail", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public boolean sendHtmlEmail(String subject, String msg,
                                   List<String> toEmailAddressList) {
		return commonsEmailSender.sendHtmlEmail(fromEmailAddress, fromEmailName, subject, msg, toEmailAddressList);
	}


    @JProfiler(jKey = "dms.MailProxy.sendImageHtmlEmail", mState = { JProEnum.TP,JProEnum.Heartbeat, JProEnum.FunctionError })
    public boolean sendImageHtmlEmail(String subject, String msg,
                                 List<String> toEmailAddressList,ImageMailDto[] imageData) {
        return commonsEmailSender.sendImageHtmlEmail(fromEmailAddress, fromEmailName, subject, msg, toEmailAddressList,imageData);
    }


	public void setCommonsEmailSender(IMailSender commonsEmailSender) {
		this.commonsEmailSender = commonsEmailSender;
	}

	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}

	public void setFromEmailName(String fromEmailName) {
		this.fromEmailName = fromEmailName;
	}
	
}
