package com.jd.bluedragon.distribution.reverse.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库明细实体
 * @author: liuduo8
 * @create: 2019-12-18 21:35
 **/

public class ReverseStockInDetail extends DbEntity {

    private static final long serialVersionUID = 1L;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /** 外部流水号 */
    private String externalCode;

    /** 批次号 */
    private String sendCode;

    /** 状态 1-发货 2-取消 3-收货 4-驳回 */
    private Integer status;

    /** 类型 1-c2c入备件库 */
    private Integer busiType;

    /** 所属站点编码 */
    private Integer createSiteCode;

    /** 所属站点名称 */
    private String createSiteName;

    /** 目的站点编码 */
    private Integer receiveSiteCode;

    /** 目的站点名称 */
    private String receiveSiteName;

    /** 创建用户 */
    private String createUser;

    /** 修改用户 */
    private String updateUser;

    /**
     * The set method for waybillCode.
     * @param waybillCode
     */
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    /**
     * The get method for waybillCode.
     * @return this.waybillCode
     */
    public String getWaybillCode() {
        return this.waybillCode;
    }

    /**
     * The set method for packageCode.
     * @param packageCode
     */
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    /**
     * The get method for packageCode.
     * @return this.packageCode
     */
    public String getPackageCode() {
        return this.packageCode;
    }

    /**
     * The set method for externalCode.
     * @param externalCode
     */
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    /**
     * The get method for externalCode.
     * @return this.externalCode
     */
    public String getExternalCode() {
        return this.externalCode;
    }

    /**
     * The set method for sendCode.
     * @param sendCode
     */
    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    /**
     * The get method for sendCode.
     * @return this.sendCode
     */
    public String getSendCode() {
        return this.sendCode;
    }

    /**
     * The set method for status.
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * The get method for status.
     * @return this.status
     */
    public Integer getStatus() {
        return this.status;
    }

    public Integer getBusiType() {
        return busiType;
    }

    public void setBusiType(Integer busiType) {
        this.busiType = busiType;
    }

    /**
     * The set method for createSiteCode.
     * @param createSiteCode
     */
    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    /**
     * The get method for createSiteCode.
     * @return this.createSiteCode
     */
    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    /**
     * The set method for createSiteName.
     * @param createSiteName
     */
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

    /**
     * The get method for createSiteName.
     * @return this.createSiteName
     */
    public String getCreateSiteName() {
        return this.createSiteName;
    }

    /**
     * The set method for createUser.
     * @param createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * The get method for createUser.
     * @return this.createUser
     */
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * The set method for updateUser.
     * @param updateUser
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * The get method for updateUser.
     * @return this.updateUser
     */
    public String getUpdateUser() {
        return this.updateUser;
    }


}
