package com.jd.bluedragon.distribution.bagException.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2020-09-20 22:59:19 周日
 */
public class CollectionBagExceptionReport implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键ID  db_column: id
     */
    private Long id;
    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;
    /**
     * 区域名称  db_column: org_code
     */
    private Integer orgCode;
    /**
     * 区域名称  db_column: org_name
     */
    private String orgName;
    /**
     * 分拣中心ID  db_column: site_code
     */
    private Integer siteCode;
    /**
     * 分拣中心名称  db_column: siteName
     */
    private String siteName;
    /**
     * 上游箱号  db_column: upstream_box_code
     */
    private String upstreamBoxCode;
    /**
     * 箱号始发ID  db_column: box_start_id
     */
    private Long boxStartId;
    /**
     * 箱号目的ID  db_column: box_end_id
     */
    private Long boxEndId;
    /**
     * 重量（单位：KG）  db_column: weight
     */
    private Double weight;
    /**
     * 长度（单位：cm）  db_column: length
     */
    private Double length;
    /**
     * 宽度（单位：cm）  db_column: width
     */
    private Double width;
    /**
     * 高度（单位：cm）  db_column: height
     */
    private Double height;
    /**
     * 举报类型；1：上游虚假集包；2：上游未集包；3：无异常  db_column: report_type
     */
    private Integer reportType;
    /**
     * 举报人ERP  db_column: operator_erp
     */
    private String operatorErp;
    /**
     * 操作人  db_column: operator_name
     */
    private String operatorName;
    /**
     * 操作时间  db_column: operate_time
     */
    private Date operateTime;
    /**
     * 举报图片URL  db_column: report_img
     */
    private String reportImg;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 修改时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 创建人ERP  db_column: create_user_erp
     */
    private String createUserErp;
    /**
     * 更新人ERP  db_column: update_user_erp
     */
    private String updateUserErp;
    /**
     * 创建人  db_column: create_user_name
     */
    private String createUserName;
    /**
     * 更新人  db_column: update_user_name
     */
    private String updateUserName;
    /**
     * 是否逻辑删除：0-已删除，1-已存在  db_column: yn
     */
    private Boolean yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUpstreamBoxCode() {
        return upstreamBoxCode;
    }

    public void setUpstreamBoxCode(String upstreamBoxCode) {
        this.upstreamBoxCode = upstreamBoxCode;
    }

    public Long getBoxStartId() {
        return boxStartId;
    }

    public void setBoxStartId(Long boxStartId) {
        this.boxStartId = boxStartId;
    }

    public Long getBoxEndId() {
        return boxEndId;
    }

    public void setBoxEndId(Long boxEndId) {
        this.boxEndId = boxEndId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getReportImg() {
        return reportImg;
    }

    public void setReportImg(String reportImg) {
        this.reportImg = reportImg;
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

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
