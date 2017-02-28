package com.jd.bluedragon.distribution.mail.service;

import com.jd.bluedragon.distribution.mail.dto.ImageMailDto;

import java.util.List;

/**
 * 邮件发送接口
 *
 */
public interface IMailSender {
	/**
	 * 发送一到多个简单文本邮件
	 * 
	 * @param fromEmailAddress 发件人邮箱地址
	 * @param fromEmailName  发件人邮箱显示名称
	 * @param subject	主题
	 * @param msg	邮件正文
	 * @param toEmailAddressList	收件人邮箱集合（支持群发）
	 * @return true:成功；false:失败
	 */
	public boolean sendSimpleEmail(String fromEmailAddress,String fromEmailName,
			String subject,String msg,List<String> toEmailAddressList) throws RuntimeException;

    /**
     * 发送一到多个简单文本邮件
     *
     * @param fromEmailAddress 发件人邮箱地址
     * @param fromEmailName  发件人邮箱显示名称
     * @param subject	主题
     * @param msg	邮件正文
     * @param toEmailAddressList	收件人邮箱集合（支持群发）
     * @return true:成功；false:失败
     */
    public boolean sendHtmlEmail(String fromEmailAddress,String fromEmailName,
                                   String subject,String msg,List<String> toEmailAddressList) throws RuntimeException;


    /**
     * 发送一到多个简单文本邮件
     *
     * @param fromEmailAddress 发件人邮箱地址
     * @param fromEmailName  发件人邮箱显示名称
     * @param subject	主题
     * @param msg	邮件正文
     * @param toEmailAddressList	收件人邮箱集合（支持群发）
     * @return true:成功；false:失败
     */
    public boolean sendImageHtmlEmail(String fromEmailAddress, String fromEmailName, String subject, String msg, List<String> toEmailAddressList,ImageMailDto[] imageData) throws RuntimeException;
}
