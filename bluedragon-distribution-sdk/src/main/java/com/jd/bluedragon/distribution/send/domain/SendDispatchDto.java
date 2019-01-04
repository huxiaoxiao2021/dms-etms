package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * C网转B网通知调度系统MQ消息体
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月04日 16时:55分
 */
public class SendDispatchDto implements Serializable {

    private static final long serialVersionUID = 2292780849533744283L;

    /** 发货交接单号 */
    private String sendCode;

    /** 箱号 */
    private String boxCode;

    /** 包裹号 */
    private String packageBarcode;

    /** 操作单位编码 */
    private Integer createSiteCode;

    /** 操作单位编码 */
    private Integer receiveSiteCode;

    /** 目的分拣中心名称 */
    private String receiveSiteName;

    /** 操作时间 */
    private Date operateTime;

    /** 操作人 */
    private String createUser;

    /** 操作人编码 */
    private Integer createUserCode;

    /** 默认：DMS */
    private String source;

    /** 组板板号 */
    private String boardCode;

    /** waybillSign */
    private String waybillSign;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
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

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }
}
