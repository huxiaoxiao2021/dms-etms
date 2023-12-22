package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/21 17:59
 * @Description
 */
public class PickingGoodTaskExtendInitDto  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String batchCode;

    private String sealCarCode;

    private String startSiteCode;

    private String startSiteName;

    private String endSiteCode;

    private String endSiteName;

    private String transportCode;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(String endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
