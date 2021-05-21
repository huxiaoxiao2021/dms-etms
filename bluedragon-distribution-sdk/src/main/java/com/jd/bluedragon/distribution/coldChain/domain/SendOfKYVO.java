package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/2
 * @Description:
 */
public class SendOfKYVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean needCheck;

    /** 收货单位编号 */
    private Integer receiveSiteCode;

    /** 箱号 */
    private List<String> barCodes;

    /** 批次号 */
    private String sendCode;

    /** 周转箱号 */
    private String turnoverBoxCode;

    /** 航标发货标示*/
    private Integer transporttype;

    /**
     * 已发货的包裹数量
     */
    private Integer hasSendPackageNum;
    /**
     * 已扫描的包裹数,老发货\快运需要先扫描后一起发货
     */
    private Integer scannedPackageNum;

    private Integer siteCode;

    private Integer userCode;

    private String userName;

    private String operateTime;

    public boolean getNeedCheck() {
        return needCheck;
    }

    public void setNeedCheck(boolean needCheck) {
        this.needCheck = needCheck;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public List<String> getBarCodes() {
        return barCodes;
    }

    public void setBarCodes(List<String> barCodes) {
        this.barCodes = barCodes;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
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

    public Integer getHasSendPackageNum() {
        return hasSendPackageNum;
    }

    public void setHasSendPackageNum(Integer hasSendPackageNum) {
        this.hasSendPackageNum = hasSendPackageNum;
    }

    public Integer getScannedPackageNum() {
        return scannedPackageNum;
    }

    public void setScannedPackageNum(Integer scannedPackageNum) {
        this.scannedPackageNum = scannedPackageNum;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
