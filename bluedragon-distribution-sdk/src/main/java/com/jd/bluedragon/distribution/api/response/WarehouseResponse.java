package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.Date;

/**
 * @author dudong
 * @date 2015/5/15
 */
public class WarehouseResponse extends JdResponse{
    private static final long serialVersionUID = 2634821223366724116L;
    private String dmsStoreId;
    private String dmsStoreName;
    private String dsmStoreType;
    private Integer dmsSiteId;
    private String dmsCode;
    private Integer storeId;
    private Integer orgId;
    private String orgName;
    private String siteNamePym;

    public String getDmsStoreId() {
        return dmsStoreId;
    }

    public void setDmsStoreId(String dmsStoreId) {
        this.dmsStoreId = dmsStoreId;
    }

    public String getDmsStoreName() {
        return dmsStoreName;
    }

    public void setDmsStoreName(String dmsStoreName) {
        this.dmsStoreName = dmsStoreName;
    }

    public String getDsmStoreType() {
        return dsmStoreType;
    }

    public void setDsmStoreType(String dsmStoreType) {
        this.dsmStoreType = dsmStoreType;
    }

    public Integer getDmsSiteId() {
        return dmsSiteId;
    }

    public void setDmsSiteId(Integer dmsSiteId) {
        this.dmsSiteId = dmsSiteId;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSiteNamePym() {
        return siteNamePym;
    }

    public void setSiteNamePym(String siteNamePym) {
        this.siteNamePym = siteNamePym;
    }
}
