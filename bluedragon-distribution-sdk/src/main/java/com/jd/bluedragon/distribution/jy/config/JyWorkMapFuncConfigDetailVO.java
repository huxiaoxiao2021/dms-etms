package com.jd.bluedragon.distribution.jy.config;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运功能与工序映射明细
 *
 * @author hujiping
 * @date 2022/4/7 3:17 PM
 */
public class JyWorkMapFuncConfigDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    // 功能编码
    private String funcCode;
    // 工序表业务主键
    private String refWorkKey;
    // 作业区编码
    private String areaCode;
    // 作业区名称
    private String areaName;
    // 网格编码
    private String workCode;
    // 网格名称
    private String workName;
    // 创建人
    private String createUser;
    // 创建时间
    private Date createTime;
    // 修改人
    private String updateUser;
    // 修改时间
    private Date updateTime;
    private Integer yn;
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public String getRefWorkKey() {
        return refWorkKey;
    }

    public void setRefWorkKey(String refWorkKey) {
        this.refWorkKey = refWorkKey;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
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

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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
