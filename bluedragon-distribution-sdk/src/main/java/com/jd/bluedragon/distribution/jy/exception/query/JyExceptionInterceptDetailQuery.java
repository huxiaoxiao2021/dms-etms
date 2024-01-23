package com.jd.bluedragon.distribution.jy.exception.query;

import com.jd.dms.java.utils.sdk.base.BaseQuery;

import java.io.Serializable;
import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class JyExceptionInterceptDetailQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;

    private List<Long> idList;

    private List<String> bizIdList;

    /**
     * 场地ID
     */
    private Integer siteId;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 包裹号
     */
    private String barCode;

    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 拦截类型
     */
    private Integer interceptType;
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 有效标志
     */
    private Integer yn;

    public JyExceptionInterceptDetailQuery() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

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

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}