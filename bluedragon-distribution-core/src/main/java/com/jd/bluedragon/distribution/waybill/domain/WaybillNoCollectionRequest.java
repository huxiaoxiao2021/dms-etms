package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;
import java.util.Date;

public class WaybillNoCollectionRequest implements Serializable {

    /*
     * 查询编号
     * */
    private String queryCode;

    /*
    * 查询类型
    * 1.按批次号查询
    * 2.按板号查询
    * 3.按箱号查询
    * */
    private Integer queryType;

    /*
     * 查询站点
     * */
    private Integer siteCode;

    /*
     * 目的地站点
     * */
    private Integer receiveSiteCode;

    /*
     * 用户编号
     * */
    private String userCode;

    /*
     * 用户名
     * */
    private String userName;

    /*
     * 操作时间
     * */
    private String operateTime;

    public String getQueryCode() {
        return queryCode;
    }

    public void setQueryCode(String queryCode) {
        this.queryCode = queryCode;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
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
