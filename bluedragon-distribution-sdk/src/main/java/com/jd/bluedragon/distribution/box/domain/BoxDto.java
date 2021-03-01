package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BoxDto
 * @Description
 * @Author wyh
 * @Date 2021/2/2 18:46
 **/
public class BoxDto implements Serializable {

    private static final long serialVersionUID = -3716352999552367661L;

    /**
     * 箱号编码集合
     */
    private List<String> boxCodes;

    /**
     * 创建站点编号
     */
    private Integer startSiteCode;

    /**
     * 始发网点名称
     */
    private String startSiteName;

    /**
     * 目的网点名称
     */
    private String destSiteName;

    /**
     * 接收站点编号
     */
    private Integer destSiteCode;

    /**
     * 中转站点或分拣中心等，不包含始发和目的站点
     */
    private List<String> router;

    /**
     * 路由经过的站点或分拣中心字符串拼接，以“-”拼接，包含始发和目的地
     */
    private String routerText;

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }

    public Integer getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(Integer startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
    }

    public List<String> getRouter() {
        return router;
    }

    public void setRouter(List<String> router) {
        this.router = router;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getDestSiteName() {
        return destSiteName;
    }

    public void setDestSiteName(String destSiteName) {
        this.destSiteName = destSiteName;
    }
}
