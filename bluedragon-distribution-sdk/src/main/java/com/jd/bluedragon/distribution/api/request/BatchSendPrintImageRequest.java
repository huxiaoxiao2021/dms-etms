package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2016/12/21.
 */
public class BatchSendPrintImageRequest implements Serializable {

    private static final long serialVersionUID = -4143581411012026236L;

    private String sendCode;//批次号

    private Integer createSiteCode;//始发分拣中心

    private String createSiteName;//始发分拣中心名字

    private Integer receiveSiteCode;//目的分拣中心

    private String receiveSiteName;//目的分拣中心名字

    private Integer packageNum;//包裹数量

    /**
     * 横向dpi,纸张打印精细度
     */
    private Integer xdpi = 200;

    /**
     * 纵向dpi,纸张打印精细度
     */
    private Integer ydpi = 200;

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

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getXdpi() {
        return xdpi;
    }

    public void setXdpi(Integer xdpi) {
        this.xdpi = xdpi;
    }

    public Integer getYdpi() {
        return ydpi;
    }

    public void setYdpi(Integer ydpi) {
        this.ydpi = ydpi;
    }

    @Override
    public String toString() {
        return "BatchSendPrintImageRequest-->sendCode:" + this.sendCode + ",createSiteCode:" + this.createSiteCode + ",createSiteName:"
                + this.createSiteName + ",receiveSiteCode:" + this.receiveSiteCode + ",receiveSiteName:" + this.receiveSiteName + ",packageNum:" + this.packageNum;
    }
}
