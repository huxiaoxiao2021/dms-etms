package com.jd.bluedragon.distribution.router.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class RouterDynamicLineReplacePlanLog implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键  db_column: id
     */
    private Long id;
    /**
     * 当前场地id  db_column: start_site_id
     */
    private Integer startSiteId;
    /**
     * 当前场地id  db_column: start_site_name
     */
    private String startSiteName;
    /**
     * 关联主数据ID  db_column: ref_id
     */
    private Long refId;
    /**
     * 线路状态-前一个值  db_column: status_prev
     */
    private Integer statusPrev;
    /**
     * 线路状态-目标值  db_column: status_target
     */
    private Integer statusTarget;
    /**
     * 创建时间  db_column: operate_time
     */
    private Date operateTime;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 创建人ERP  db_column: create_user_id
     */
    private Integer createUserId;
    /**
     * 创建人ERP  db_column: create_user_erp
     */
    private String createUserErp;
    /**
     * 创建人姓名  db_column: create_user_name
     */
    private String createUserName;
    /**
     * 有效标志  db_column: yn
     */
    private Boolean yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END

    public RouterDynamicLineReplacePlanLog(){
    }
    public RouterDynamicLineReplacePlanLog(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Integer getStatusPrev() {
        return statusPrev;
    }

    public void setStatusPrev(Integer statusPrev) {
        this.statusPrev = statusPrev;
    }

    public Integer getStatusTarget() {
        return statusTarget;
    }

    public void setStatusTarget(Integer statusTarget) {
        this.statusTarget = statusTarget;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
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
