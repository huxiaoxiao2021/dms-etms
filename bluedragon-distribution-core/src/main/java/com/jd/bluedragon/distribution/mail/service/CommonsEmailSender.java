package com.jd.bluedragon.distribution.mail.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.mail.dto.ImageMailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 邮件发送
 * @author huangliang
 */

public class CommonsEmailSender implements IMailSender {

    private final static Logger log = LoggerFactory.getLogger(CommonsEmailSender.class);

    private String hostName;
    private String charset;
    private DefaultAuthenticator authenticator;
    private int port;
    private boolean ssLOnConnect;

    @Override
    public boolean sendSimpleEmail(String fromEmailAddress,
                                   String fromEmailName, String subject, String msg,
                                   List<String> toEmailAddressList) throws RuntimeException {

        if (CollectionUtils.isEmpty(toEmailAddressList)) {
            throw new RuntimeException("toEmailAddressList is empty!");
        }
        if (toEmailAddressList.size() > Constants.MAX_SEND_SIZE) {
            throw new RuntimeException("toEmailAddressList is more than [" + Constants.MAX_SEND_SIZE + "]!");
        }

        Email email = new SimpleEmail();
        String mimeMessage = "";
        try {
            email.setHostName(hostName);
            email.setSmtpPort(port);
            email.setAuthenticator(authenticator);
            email.setCharset(charset);
            email.setSSLOnConnect(ssLOnConnect);
            email.setFrom(fromEmailAddress, fromEmailName);
            email.setSubject(subject);
            email.setMsg(msg);
            for (String toEmail : toEmailAddressList) {
                email.addTo(toEmail);
            }
            mimeMessage = email.send();
        } catch (EmailException e) {
            log.error("邮件发送异常邮件地址,toEmailAddressList:{}",toEmailAddressList, e);
            throw new RuntimeException(e.getMessage());
        }

        return !StringUtils.isBlank(mimeMessage);
    }

    /**
     * 发送一到多个简单文本邮件
     *
     * @param fromEmailAddress   发件人邮箱地址
     * @param fromEmailName      发件人邮箱显示名称
     * @param subject            主题
     * @param msg                邮件正文
     * @param toEmailAddressList 收件人邮箱集合（支持群发）
     * @return true:成功；false:失败
     */
    @Override
    public boolean sendHtmlEmail(String fromEmailAddress, String fromEmailName, String subject, String msg, List<String> toEmailAddressList) throws RuntimeException {
        if (CollectionUtils.isEmpty(toEmailAddressList)) {
            throw new RuntimeException("toEmailAddressList is empty!");
        }
        if (toEmailAddressList.size() > Constants.MAX_SEND_SIZE) {
            throw new RuntimeException("toEmailAddressList is more than [" + Constants.MAX_SEND_SIZE + "]!");
        }

        HtmlEmail email = new HtmlEmail();
        String mimeMessage = "";
        try {
            email.setHostName(hostName);
            email.setSmtpPort(port);
            email.setAuthenticator(authenticator);
            email.setCharset(charset);
            email.setSSLOnConnect(ssLOnConnect);
            email.setFrom(fromEmailAddress, fromEmailName);
            email.setSubject(subject);
            email.setHtmlMsg(msg);
            for (String toEmail : toEmailAddressList) {
                email.addTo(toEmail);
            }
            mimeMessage = email.send();
        } catch (EmailException e) {
            log.error("邮件发送异常邮件地址{}" , toEmailAddressList, e);
            throw new RuntimeException(e.getMessage());
        }

        return !StringUtils.isBlank(mimeMessage);
    }

    /**
     * 发送内嵌图片的邮件
     *
     * @param fromEmailAddress   发件人邮箱地址
     * @param fromEmailName      发件人邮箱显示名称
     * @param subject            主题
     * @param content            邮件正文
     * @param toEmailAddressList 收件人邮箱集合（支持群发）
     * @return true:成功；false:失败
     */
    @Override
    public boolean sendImageHtmlEmail(String fromEmailAddress, String fromEmailName, String subject, String content, List<String> toEmailAddressList, ImageMailDto[] imageData) throws RuntimeException {
        if (CollectionUtils.isEmpty(toEmailAddressList)) {
            throw new RuntimeException("toEmailAddressList is empty!");
        }
        if (toEmailAddressList.size() > Constants.MAX_SEND_SIZE) {
            throw new RuntimeException("toEmailAddressList is more than [" + Constants.MAX_SEND_SIZE + "]!");
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", hostName);
            props.put("mail.smtp.auth", "true");

            Session sendMailSession = Session.getDefaultInstance(props, authenticator);
            MimeMessage mimeMessage = new MimeMessage(sendMailSession);
            mimeMessage.setFrom(new InternetAddress(fromEmailAddress));
            InternetAddress[] address = new InternetAddress().parse(toEmailAddressList.toString());
            mimeMessage.setRecipients(Message.RecipientType.TO, address);
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(new Date());
            BodyPart mdp = new MimeBodyPart();//新建一个存放信件内容的BodyPart对象
            mdp.setContent(content, "text/html;charset=utf-8");//给BodyPart对象设置内容和格式/编码方式


            Multipart mm = new MimeMultipart();//新建一个MimeMultipart对象用来存放BodyPart对
            mm.addBodyPart(mdp);//将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)

            if (imageData != null) {
                for (ImageMailDto dto : imageData) {
                    MimeBodyPart image = new MimeBodyPart();
                    image.setDataHandler(new DataHandler(new ByteArrayDataSource(dto.getImageData(), dto.getMimeType())));  //javamail jaf
                    image.setContentID(dto.getImageCid());
                    mm.addBodyPart(image);
                }
            }

            mimeMessage.setContent(mm);//把mm作为消息对象的内容
            mimeMessage.saveChanges();

            Transport.send(mimeMessage);
            return true;
        } catch (Exception e) {
            log.error("邮件发送异常邮件地址{}" , toEmailAddressList, e);
            throw new RuntimeException(e);
        }

    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setAuthenticator(DefaultAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSsLOnConnect(boolean ssLOnConnect) {
        this.ssLOnConnect = ssLOnConnect;
    }

}
