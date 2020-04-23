package com.jd.bluedragon.distribution.sms.domain;

/**
 * 短信属性配置Dto
 *
 * @author: hujiping
 * @date: 2020/2/17 16:24
 */
public class SMSDto {

    /**
     * 短信账号
     * */
    private String account;

    /**
     * 短信模板ID
     * */
    private Long templateId;

    /**
     * 短信token
     * */
    private String token;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
