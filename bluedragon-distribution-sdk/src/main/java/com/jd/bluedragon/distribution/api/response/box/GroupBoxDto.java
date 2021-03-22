package com.jd.bluedragon.distribution.api.response.box;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description: 组中箱号信息
 * @Date: create in 2021/3/17 19:45
 */
public class GroupBoxDto implements Serializable {

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

    /**
     * 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输
     */
    private Integer transportType;

    /**
     * 站点类型
     **/
    private Integer siteType;

    /**
     * 箱号类型
     */
    private String  boxType;

    /**
     * 循环集包袋
     */
    private String materialCode;

    public GroupBoxDto() {
    }

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

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }


}
    
