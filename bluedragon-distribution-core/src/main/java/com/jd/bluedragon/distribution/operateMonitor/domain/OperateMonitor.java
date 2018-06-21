package com.jd.bluedragon.distribution.operateMonitor.domain;

import java.util.Date;

public class OperateMonitor  implements java.io.Serializable {
    private static final long serialVersionUID = -1l;

    /** 包裹号 */
    private String packageCode;

    /** 运单号 */
    private String waybillCode;

    /** 箱号 */
    private String boxCode;

    /** 发货批次号号 */
    private String sendCode;

    /** 操作单位编码 */
    private Integer createSiteCode;

    /** 目的单位编码 */
    private Integer receiveSiteCode;

    /** 操作时间 */
    private Date operateTime;

    /** 创建时间 */
    private Date createTime;

    /** 操作人 */
    private String createUser;

    /** 状态 */
    private Integer status;

    /** 是否取消状态 */
    private Integer isCancel;

    /** 组板板号 */
    private String boardCode;

    /** 操作类型 1.验货、2.分拣、3.发货*/
    private Integer opreteType;



    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

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

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOpreteType() {
        return opreteType;
    }

    public void setOpreteType(Integer opreteType) {
        this.opreteType = opreteType;
    }
}
