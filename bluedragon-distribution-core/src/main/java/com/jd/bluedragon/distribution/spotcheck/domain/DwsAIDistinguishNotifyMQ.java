package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 设备图片AI识别回传消息体
 *
 * @author hujiping
 * @date 2022/3/17 4:15 PM
 */
public class DwsAIDistinguishNotifyMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;
    private Integer siteCode;
    private String waybillCode;
    private String packageCode;
    private String status;
    private String message;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
