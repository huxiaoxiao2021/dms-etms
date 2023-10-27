package com.jd.bluedragon.distribution.api.request.box;


import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.base.RequestProfile;

import java.io.Serializable;

/**
 * 创建箱号入参
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public class CreateBoxReq implements Serializable {

    private static final long serialVersionUID = -8128040530625817445L;

    /**
     * 调用方信息
     */
    private RequestProfile requestProfile;

    /**
     * 操作人信息
     */
    private OperateUser operateUser;

    /**
     * 箱号类型
     */
    private String type;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;

    /**
     * 创建站点名称
     */
    private String createSiteName;

    /**
     * 接收站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输
     */
    private Integer transportType;

    /**
     * 1混包0非混包
     */
    private Integer mixBoxType;

    public CreateBoxReq() {
    }

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public CreateBoxReq setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
        return this;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public CreateBoxReq setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
        return this;
    }

    public String getType() {
        return type;
    }

    public CreateBoxReq setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public CreateBoxReq setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
        return this;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public CreateBoxReq setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
        return this;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public CreateBoxReq setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
        return this;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public CreateBoxReq setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CreateBoxReq setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public CreateBoxReq setTransportType(Integer transportType) {
        this.transportType = transportType;
        return this;
    }

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public CreateBoxReq setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
        return this;
    }
}

