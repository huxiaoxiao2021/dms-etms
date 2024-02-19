package com.jd.bluedragon.common.dto.jyJobType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pengchong28
 * @description 拣运工种
 * @date 2024/2/19
 */
public class JyJobTypeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    private Long id;
    /**
     * 工种类型
     * */
    private String name;
    /**
     * 工种编码
     * */
    private Integer code;
    /**
     * 工种启用状态,0-停用,1-启用
     */
    private Integer status;
    /**
     * 工种归属：自有员工，三方员工
     * */
    private String belong;
    /**
     * 是否支持拍照签到，不支持-0，支持-1
     * */
    private Integer photoSupport;
    /**
     * 工种自动签退时间-默认18小时
     * */
    private Integer autoSignOutHour;
    /**
     * 创建人ERP
     * */
    private String createUser;
    /**
     * 修改人ERP
     * */
    private String updateUser;
    /**
     * 创建时间
     * */
    private Date createTime;
    /**
     * 修改时间
     * */
    private Date updateTime;
    /**
     * 是否存在
     * */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public Integer getPhotoSupport() {
        return photoSupport;
    }

    public void setPhotoSupport(Integer photoSupport) {
        this.photoSupport = photoSupport;
    }

    public Integer getAutoSignOutHour() {
        return autoSignOutHour;
    }

    public void setAutoSignOutHour(Integer autoSignOutHour) {
        this.autoSignOutHour = autoSignOutHour;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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
}
