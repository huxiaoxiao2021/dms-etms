package com.jd.bluedragon.distribution.api.response;

import java.util.List;
import java.util.Map;

/**
 * 自动分拣机箱号
 * Created by wangtingwei on 2015/10/22.
 */
public class AutoSortingBoxResult {



    /** 创建时间 */
    private String createTime;

    /** 箱号 */
    private List<String> boxCode;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    private String type;

    /** 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输 */
    private Integer transportType;

    /** 箱子的路由信息 */
    private List<Map.Entry<Integer,String>> routerInfo;

    public List<Map.Entry<Integer,String>> getRouterInfo() {
        return routerInfo;
    }

    public void setRouterInfo(List<Map.Entry<Integer,String>> routerInfo) {
        this.routerInfo = routerInfo;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(List<String> boxCode) {
        this.boxCode = boxCode;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }


}
