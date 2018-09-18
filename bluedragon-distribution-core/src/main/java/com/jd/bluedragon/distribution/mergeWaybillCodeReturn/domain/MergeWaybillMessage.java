package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain;

import java.io.Serializable;

/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/18 13:48
 */
public class MergeWaybillMessage implements Serializable {

    private String operatorNo;
    private String operatorName;
    private Integer siteCode;
    private String siteName;
    private Long operateTime;
    private String newWaybillCode;
    private String waybillCodeList;

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getNewWaybillCode() {
        return newWaybillCode;
    }

    public void setNewWaybillCode(String newWaybillCode) {
        this.newWaybillCode = newWaybillCode;
    }

    public String getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(String waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }
}
