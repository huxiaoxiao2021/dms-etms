package com.jd.bluedragon.common.dto.box.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : xumigen
 * @date : 2019/6/22
 */
public class BoxDto implements Serializable{

    private static final long serialVersionUID = -1L;


    /**
     * 箱号
     */
    private String boxCode;


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

    private String type;

    /**
     * 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输
     */
    private Integer transportType;

    /**
     * 箱子的路由信息
     */
    private String[] routerInfo;

    /**
     * 箱子路由信息 汉字描述，超过5个站点，打印系统直接用他打印
     */
    private String routerText;

    /**
     * 站点类型
     **/
    private Integer siteType;

    /**
     * 预计发货时间
     */
    private Date predictSendTime;

    /**
     * 混包类型 1混包，0非混包
     */
    private Integer mixBoxType;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String[] getRouterInfo() {
        return routerInfo;
    }

    public void setRouterInfo(String[] routerInfo) {
        this.routerInfo = routerInfo;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Date getPredictSendTime() {
        return predictSendTime;
    }

    public void setPredictSendTime(Date predictSendTime) {
        this.predictSendTime = predictSendTime;
    }

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public void setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
    }
}
