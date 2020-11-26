package com.jd.bluedragon.distribution.busineCode.sendCode.domain;

/**
 * <p>
 *     批次号的对象
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public class SendCodeDto {

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 始发站点编号
     */
    private Integer createSiteCode;

    /**
     * 目的站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 是否生鲜属性批次
     */
    private Boolean isFresh;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Boolean getFresh() {
        return isFresh;
    }

    public void setFresh(Boolean fresh) {
        isFresh = fresh;
    }
}
