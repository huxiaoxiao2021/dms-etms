package com.jd.bluedragon.distribution.client.domain;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */
public class ClientOperateRequest {

    /**
     * 运单号
     */
	protected String waybillCode;
    /**
     * 包裹号
     */
    protected String packageCode;
    /**
     * 箱号
     */
    protected String boxCode;
    /**
     * 批次号
     */
    protected String sendCode;
    /**
     * 单号（模糊概念）
     */
    protected String barCode;
    /**
     * waybillSign
     */
    protected String waybillSign;
    /**
     * 分拣中心编码
     */
    protected Integer createSiteCode;
    /**
     * 分拣中心名称
     */
    protected String createSiteName;
    /**
     * 操作人编码
     */
    protected Integer operateUserCode;
    /**
     * 操作人名称
     */
    protected String operateUserName;
    /**
     * 操作时间
     */
    protected String operateTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "ClientOperateRequest{" +
                "waybillCode='" + waybillCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", boxCode='" + boxCode + '\'' +
                ", sendCode='" + sendCode + '\'' +
                ", barCode='" + barCode + '\'' +
                ", waybillSign='" + waybillSign + '\'' +
                ", createSiteCode=" + createSiteCode +
                ", createSiteName='" + createSiteName + '\'' +
                ", operateUserCode=" + operateUserCode +
                ", operateUserName='" + operateUserName + '\'' +
                ", operateTime='" + operateTime + '\'' +
                '}';
    }
}
