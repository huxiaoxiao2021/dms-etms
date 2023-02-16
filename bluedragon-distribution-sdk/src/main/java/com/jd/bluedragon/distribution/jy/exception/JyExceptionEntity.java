package com.jd.bluedragon.distribution.jy.exception;


import java.io.Serializable;
import java.util.Date;

/**
 * 异常任务明细表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyExceptionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JyExceptionEntity() {}

    public JyExceptionEntity(String barCode, Long siteCode, String bizId) {
        this.barCode = barCode;
        this.siteCode = siteCode;
        this.bizId = bizId;
    }

    public JyExceptionEntity(Long siteCode, String bizId) {
        this.siteCode = siteCode;
        this.bizId = bizId;
    }

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     *  异常条码
     */
    private String barCode;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 场地ID
     */
    private Long siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 上架人erp
     */
    private String shelfErp;
    /**
     * 上架货区编号
     */
    private String shelfNo;
    /**
     * 分配时间
     */
    private Date shelfTime;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 更新人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

    /**
     *照片地址 多个逗号分割
     */
    private String imageUrls;

    /**
     * 运单号
     */
    private String waybillCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getShelfErp() {
        return shelfErp;
    }

    public void setShelfErp(String shelfErp) {
        this.shelfErp = shelfErp;
    }

    public String getShelfNo() {
        return shelfNo;
    }

    public void setShelfNo(String shelfNo) {
        this.shelfNo = shelfNo;
    }

    public Date getShelfTime() {
        return shelfTime;
    }

    public void setShelfTime(Date shelfTime) {
        this.shelfTime = shelfTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
