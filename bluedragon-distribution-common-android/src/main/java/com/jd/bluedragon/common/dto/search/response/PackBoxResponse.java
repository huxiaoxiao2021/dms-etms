package com.jd.bluedragon.common.dto.search.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 获取包裹、箱信息出参对象
 */
public class PackBoxResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 箱号 */
    private String boxCode;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /**
     * 箱内包裹总数
     */
    private int packageTotal;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

    /** pda操作时间 */
    private Date operateTime;

    /** 分拣类型 '10' 自营 '20' 退货 '30'三方 */
    private Integer type;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public int getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(int packageTotal) {
        this.packageTotal = packageTotal;
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

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
