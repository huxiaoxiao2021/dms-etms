package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.dms.wb.report.util.JsonHelper;
import com.jd.dms.wb.sdk.model.base.BaseEntity;
import com.jd.shorturl.api.jsf.ShortUrlService;
import com.jd.shorturl.model.UrlInfo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.msp.api.ApiResult;
import com.jdl.jy.msp.api.IMessageSendApi;
import com.jdl.jy.msp.api.MessageEntity;
import com.jdl.jy.msp.api.builder.MailMessageBuilder;
import com.jdl.jy.msp.api.builder.TimLineMessageBuilder;
import com.jdl.jy.msp.api.enums.ResultCodeEnums;
import com.jdl.jy.msp.api.enums.TerminalCodeEnums;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 物流消息发送平台代理类
 *
 */
public class MspClientProxy {

    private static final Logger logger = LoggerFactory.getLogger(MspClientProxy.class);

    /**
     * 短链网址密钥
     */
    @Value("short.url.secret")
    private String urlSecret;

    /**
     * 京东断链服务
     */
    @Autowired
    private ShortUrlService shortUrlService;

    /**
     * jy_msp服务
     */
    @Autowired
    private IMessageSendApi messageSendApi;

    /**
     * 推送咚咚消息
     *
     * @param title   标题
     * @param content 内容
     * @param pins    推送人Erp列表
     * @return 返回结果
     */
    @JProfiler(jKey = "DMSWEB.MspClientProxy.sendTimeline", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> sendTimeline(String title, String content, String url, Set<String> pins, boolean encodeUrl) {
        String accessId = UUID.randomUUID().toString();

        BaseEntity<Boolean> result = new BaseEntity<>();
        result.toFail();
        result.setData(Boolean.FALSE);

        try {
            TimLineMessageBuilder timLineMessageBuilder = new TimLineMessageBuilder();
            for (String pin : pins) {
                timLineMessageBuilder.addToErp(pin);
            }
            timLineMessageBuilder.content(title, content,null,  this.dealWithUrl(url,encodeUrl), null);
            timLineMessageBuilder.toTerminal(TerminalCodeEnums.ALL_TERMINAL);

            MessageEntity messageEntity = timLineMessageBuilder.build();
            ApiResult<Boolean> apiResult = messageSendApi.sendMessage(accessId, messageEntity);
            if (logger.isInfoEnabled()) {
                logger.info("调用JY-MSP的服务，参数为：{}，返回值为：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(apiResult));
            }
            if (apiResult == null || !ResultCodeEnums.SUCCESS.getCode().equals(apiResult.getCode())) {
                logger.error("调用JY-MSP的服务失败，参数为：{}，返回值为：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(apiResult));
                result.toFail();
                result.setData(Boolean.FALSE);
                return result;
            }
            if (ResultCodeEnums.SUCCESS.getCode().equals(apiResult.getCode())) {
                result.toSuccess();
                result.setData(Boolean.TRUE);
            } else {
                logger.error("调用JY-MSP的服务失败，参数为：{}, 返回值：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(apiResult));
                result.toFail(apiResult.getMessage());
                result.setData(Boolean.FALSE);
            }
            return result;
        } catch (Exception e) {
            logger.error("【MSP代理】推送TimLine失败,title: {},content: {},url: {},pins: {},error:", title, content, url, pins, e);
            result.toFail(e.getMessage());
            result.setData(Boolean.FALSE);
            return result;
        }
    }

    /**
     * 发送邮件
     *
     * @param recipient  收件人列表
     * @param senderName 发件人
     * @param subject    邮件主题
     * @param content    邮件正文
     * @return 返回结果
     */
    @JProfiler(jKey = "DMSWEB.MspClientProxy.sendEmail", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> sendEmail(List<String> recipient, String senderName, String subject, String content) {
        return this.sendEmail(recipient, null, null, senderName, subject, content);
    }

    /**
     * 发送邮件
     *
     * @param recipient  收件人列表
     * @param senderName 发件人
     * @param subject    邮件主题
     * @param content    邮件正文
     * @return 返回结果
     */
    @JProfiler(jKey = "DMSWEB.MspClientProxy.sendEmailWithAttachment", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> sendEmailWithAttachment(List<String> recipient,List<String> cc, String senderName, String subject, String content, List<File> fileList) throws IOException {
        return this.sendEmail(recipient, cc, null, senderName,subject,content);
    }

    /**
     * 发送邮件
     *
     * @param recipient     收件人列表
     * @param recipientsCc  抄送人列表
     * @param recipientsBcc 密送人列表
     * @param senderName    发件人
     * @param subject       邮件主题
     * @param content       邮件正文
     * @return 返回结果
     */
    @JProfiler(jKey = "DMSWEB.MspClientProxy.sendEmail", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> sendEmail(List<String> recipient, List<String> recipientsCc,
                                       List<String> recipientsBcc, String senderName, String subject, String content) {
        BaseEntity<Boolean> result = new BaseEntity<>();
        result.toFail();
        result.setData(Boolean.FALSE);

        try {
            MailMessageBuilder mailMessageBuilder = new MailMessageBuilder();
            for (String receivePin : recipient) {
                mailMessageBuilder.addTo(receivePin);
            }
            if (CollectionUtils.isNotEmpty(recipientsCc)) {
                for (String receiveCc : recipientsCc) {
                    mailMessageBuilder.addCc(receiveCc);
                }
            }
            if (CollectionUtils.isNotEmpty(recipientsBcc)) {
                for (String receiveBcc : recipientsBcc) {
                    mailMessageBuilder.addCc(receiveBcc);
                }
            }
            mailMessageBuilder.content(subject, content);

            MessageEntity messageEntity = mailMessageBuilder.build();
            ApiResult<Boolean> apiResult = messageSendApi.sendMessage(UUID.randomUUID().toString(), messageEntity);
            if (logger.isInfoEnabled()) {
                logger.info("调用JY-MSP的服务，参数为：{}，返回值为：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(result));
            }
            if (apiResult == null || !ResultCodeEnums.SUCCESS.getCode().equals(apiResult.getCode())) {
                logger.error("调用JY-MSP的服务失败，参数为：{}，返回值为：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(apiResult));
                result.toFail();
                result.setData(Boolean.FALSE);
                return result;
            }
            if (ResultCodeEnums.SUCCESS.getCode().equals(apiResult.getCode())) {
                result.toSuccess();
                result.setData(Boolean.TRUE);
            } else {
                logger.error("调用JY-MSP的服务失败，参数为：{}, 返回值：{}", JsonHelper.toJson(messageEntity), JsonHelper.toJson(apiResult));
                result.toFail(apiResult.getMessage());
                result.setData(Boolean.FALSE);
            }
            return result;
        } catch (Exception e) {
            logger.error("调用JY-MSP的服务异常，参数为:recipient:{},subject:{},content:{}", recipient, subject, content, e);
            result.toFail(e.getMessage());
            result.setData(Boolean.FALSE);
            return result;
        }
    }

    /**
     * 处理url
     *
     * @param url 链接
     * @return Url
     */
    public String dealWithUrl(String url, boolean encodeUrl) {
        if (StringUtils.isBlank(url)) {
            logger.warn("【TimLine接口代理】Url空或者null");
            return url;
        }
        url = completedProtocol(url);
        String codeUrl = encodeUrl ? encoder(url) : url;
        if (StringUtils.isBlank(codeUrl)) {
            logger.warn("【TimLine接口代理】编码转换出错，返回原Url. {}", url);
            return url;
        }
        String shortUrl = generateUrl(codeUrl);
        if (StringUtils.isBlank(shortUrl)) {
            logger.warn("【TimLine接口代理】短链转换失败，返回转换编码Url. {}", codeUrl);
            return codeUrl;
        }
        return shortUrl;
    }

    /**
     * 短链工具
     *
     * @param url 链接
     * @return 短链
     */
    private String generateUrl(String url) {
        final String domain = "3.cn";
        final int length = 8;
        final int expiredDays = 0;
        UrlInfo urlInfo = shortUrlService.generateURLFastest(domain, length, url, urlSecret, expiredDays);
        if (urlInfo != null) {
            return completedProtocol(urlInfo.getShortUrl());
        }
        return "";
    }

    /**
     * 补全URL协议
     *
     * @param url 链接
     * @return 处理过的URl
     */
    private String completedProtocol(String url) {
        final String httpPrefix = "http://";
        final String httpsPrefix = "https://";
        if (StringUtils.isBlank(url)) {
            return url;
        }
        if (url.contains(httpPrefix) || url.contains(httpsPrefix)) {
            return url;
        } else {
            return "http://" + url;
        }
    }

    /**
     * 中文编码
     *
     * @param url 链接
     * @return 编码后url
     */
    private String encoder(String url) {
        String[] strs = url.split("/");
        try {
            if (strs.length >= 1) {
                String temp = strs[strs.length - 1];
                strs[strs.length - 1] = URLEncoder.encode(temp, "utf8");
            }
            return StringUtils.join(strs, "/");
        }
        catch (UnsupportedEncodingException e) {
            logger.error("【TimLine接口代理】中文编码转换异常", e);
        }
        return "";
    }
}
