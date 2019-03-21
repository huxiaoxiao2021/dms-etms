package com.jd.bluedragon.distribution.send.domain;

import org.apache.commons.lang.StringUtils;

import java.util.Date;


public class SendM implements Cloneable, java.io.Serializable, Comparable<SendM> {

    /**
     *
     */
    private static final long serialVersionUID = -5706252377345301775L;

    /**
     * 全局唯一ID
     */
    private Long sendMId;

    /**
     * 发货交接单号-发货批次号
     */
    private String sendCode;

    /**
     * 运单号(第三方) 非数据库表中属性
     */
    private String thirdWaybillCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 周转箱号
     */
    private String turnoverBoxCode;

    /**
     * 发货司机
     */
    private String sendUser;

    /**
     * 发货司机编码
     */
    private Integer sendUserCode;

    /**
     * 发货单位编码
     */
    private Integer createSiteCode;

    /**
     * 收货单位编码
     */
    private Integer receiveSiteCode;

    /**
     * 车号
     */
    private String carCode;

    /**
     * 发货交接单类型(1 退货 2 转站 3 第三方)
     */
    private Integer sendType;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人编码
     */
    private Integer updateUserCode;

    /**
     * 修改人
     */
    private String updaterUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 封车表主ID
     */
    private Long shieldsCarId;

    /**
     * 是否删除 '0' 删除 '1' 使用
     */
    private Integer yn;

    /**
     * 发货数据回传tmst
     */
    private Integer sendmStatus;

    /**
     * 执行次数
     */
    private Integer excuteCount;

    /**
     * 执行时间
     */
    private Date excuteTime;

    /**
     * 航标发货标示
     */
    private Integer transporttype;

    /**
     * 组板板号
     */
    private String boardCode;

    /**
     * 发货业务来源
     */
    private Integer bizSource;

    public Long getSendMId() {
        return sendMId;
    }

    public void setSendMId(Long sendMId) {
        this.sendMId = sendMId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getThirdWaybillCode() {
        return thirdWaybillCode;
    }

    public void setThirdWaybillCode(String thirdWaybillCode) {
        this.thirdWaybillCode = thirdWaybillCode;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public Integer getSendUserCode() {
        return sendUserCode;
    }

    public void setSendUserCode(Integer sendUserCode) {
        this.sendUserCode = sendUserCode;
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

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
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

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdaterUser() {
        return updaterUser;
    }

    public void setUpdaterUser(String updaterUser) {
        this.updaterUser = updaterUser;
    }

    public Date getUpdateTime() {
        return this.updateTime == null ? null : (Date) this.updateTime.clone();
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? null : (Date) updateTime.clone();
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Long getShieldsCarId() {
        return shieldsCarId;
    }

    public void setShieldsCarId(Long shieldsCarId) {
        this.shieldsCarId = shieldsCarId;
    }

    public Integer getSendmStatus() {
        return sendmStatus;
    }

    public void setSendmStatus(Integer sendmStatus) {
        this.sendmStatus = sendmStatus;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getExcuteCount() {
        return excuteCount;
    }

    public void setExcuteCount(Integer excuteCount) {
        this.excuteCount = excuteCount;
    }

    public Date getExcuteTime() {
        return this.excuteTime == null ? null : (Date) this.excuteTime.clone();
    }

    public void setExcuteTime(Date excuteTime) {
        this.excuteTime = excuteTime == null ? null : (Date) excuteTime.clone();
    }

    public Date getOperateTime() {
        return this.operateTime == null ? null : (Date) this.operateTime.clone();
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime == null ? null : (Date) operateTime.clone();
    }

    public Date getCreateTime() {
        return this.createTime == null ? null : (Date) this.createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? null : (Date) createTime.clone();
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public Integer getTransporttype() {
        return transporttype;
    }

    public void setTransporttype(Integer transporttype) {
        this.transporttype = transporttype;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sendCode == null) ? 0 : sendCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SendM other = (SendM) obj;
        if (sendCode == null) {
            if (other.sendCode != null)
                return false;
        } else if (!sendCode.equals(other.sendCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SendM [sendMId=" + sendMId + ", sendCode=" + sendCode
                + ", boxCode=" + boxCode + ", sendUser=" + sendUser
                + ", sendUserCode=" + sendUserCode + ", createSiteCode="
                + createSiteCode + ", receiveSiteCode=" + receiveSiteCode
                + ", carCode=" + carCode + ", sendType=" + sendType
                + ", createUser=" + createUser + ", createUserCode="
                + createUserCode + ", createTime=" + operateTime
                + ", updateUserCode=" + updateUserCode + ", updaterUser="
                + updaterUser + ", updateTime=" + updateTime
                + ", shieldsCarId=" + shieldsCarId + ", yn=" + yn
                + ", sendmStatus=" + sendmStatus + "]";
    }

    @Override
    public int compareTo(SendM sendM) {
        if (null == sendM || StringUtils.isBlank(sendM.getBoxCode()) || StringUtils.isBlank(this.getBoxCode())) {
            return 0;
        } else {
            return this.getBoxCode().compareTo(sendM.getBoxCode());
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}