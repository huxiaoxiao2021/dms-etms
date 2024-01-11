package com.jd.bluedragon.distribution.api.response.box;

import java.io.Serializable;
import java.util.List;

/**
 * 打印箱号基础数据
 *
 * @author fanggang7
 * @time 2023-10-25 10:21:10 周三
 */
public class BoxPrintBaseInfo implements Serializable {
    private static final long serialVersionUID = -6221823381571853735L;
    /**
     * 箱号打印模版名称
     */
    private String templateName;
    /**
     * 箱号打印模版版本
     */
    private Integer templateVersion;
    /**
     * 路由信息
     */
    private List<String> router;
    /**
     * 目的站点id
     */
    private Integer receiveSiteCode;
    /**
     * 目的站点名称
     */
    private String receiveSiteName;
    /**
     * 始发站点名称
     */
    private String createSiteName;
    /**
     * 始发站点ID
     */
    private Integer createSiteCode;
    /**
     * 箱号类型
     */
    private String boxType;
    /**
     * 箱号类型名称
     */
    private String boxTypeText;
    /**
     * 箱号子类型
     */
    private String boxSubType;
    /**
     * 运输方式
     */
    private Integer transportType;
    /**
     * 运输方式
     */
    private String transportTypeText;
    /**
     * 是否混箱
     */
    private Integer mixBoxType;
    /**
     * 是否混箱
     */
    private String mixBoxTypeText;
    /**
     * 始发道口和笼车号
     */
    private String routerNum;
    /**
     * router拼接的字符串
     */
    private String routerText;

    public BoxPrintBaseInfo() {
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }

    public List<String> getRouter() {
        return router;
    }

    public void setRouter(List<String> router) {
        this.router = router;
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

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getBoxTypeText() {
        return boxTypeText;
    }

    public void setBoxTypeText(String boxTypeText) {
        this.boxTypeText = boxTypeText;
    }

    public String getBoxSubType() {
        return boxSubType;
    }

    public void setBoxSubType(String boxSubType) {
        this.boxSubType = boxSubType;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getTransportTypeText() {
        return transportTypeText;
    }

    public void setTransportTypeText(String transportTypeText) {
        this.transportTypeText = transportTypeText;
    }

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public void setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
    }

    public String getMixBoxTypeText() {
        return mixBoxTypeText;
    }

    public void setMixBoxTypeText(String mixBoxTypeText) {
        this.mixBoxTypeText = mixBoxTypeText;
    }

    public String getRouterNum() {
        return routerNum;
    }

    public void setRouterNum(String routerNum) {
        this.routerNum = routerNum;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }
}
