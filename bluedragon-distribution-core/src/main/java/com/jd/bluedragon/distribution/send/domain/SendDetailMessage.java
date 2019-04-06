package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

/**
 * 发货明细对象，用于反序列化发货MQ消息体
 *
 * @author lixin39
 */
public class SendDetailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 操作单位编码
     */
    private Integer createSiteCode;

    /**
     * 操作人名称
     */
    private String createUser;

    /**
     * 操作人编号
     */
    private Integer createUserCode;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 包裹号
     */
    private String packageBarcode;

    /**
     * 目的地编码
     */
    private Integer receiveSiteCode;

    /**
     * 发货交接单号 批次号
     */
    private String sendCode;

    /**
     * 消息来源
     */
    private String source;

    /**
     * 组板板号
     */
    private String boardCode;

    /**
     * 业务来源
     */
    private Integer bizSource;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
