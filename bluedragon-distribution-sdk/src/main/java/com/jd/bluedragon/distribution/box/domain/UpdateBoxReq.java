package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.Date;

public class UpdateBoxReq implements Serializable {
    private static final long serialVersionUID = 6101708135429307763L;

    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 始发场地id （仓忽略）
     */
    private Integer createSiteCode;

    private String createSiteName;
    /**
     * 仓信息
     */
    private StoreInfo storeInfo;
    /**
     *  目的场地id
     */
    private Integer receiveSiteCode;

    private String receiveSiteName;
    /**
     *  操作用户id
     */
    private Integer userCode;

    private String userErp;
    /**
     * 操作用户名字
     */
    private String userName;
    /**
     * yyyy-MM-dd HH:mm:ss 操作时间
     */
    private Date opeateTime;
    /**
     * 0 不混装 1混装
     */
    private Integer mixBoxType;
    /**
     * 运输方式如公路、铁路、航空
     */
    private Integer transportType;

    private Long  boxId;

    /**
     * 物资编码
     */
    private String materialCode;

    public Long getBoxId() {
        return boxId;
    }

    public void setBoxId(Long boxId) {
        this.boxId = boxId;
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

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getOpeateTime() {
        return opeateTime;
    }

    public void setOpeateTime(Date opeateTime) {
        this.opeateTime = opeateTime;
    }

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public void setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
}
