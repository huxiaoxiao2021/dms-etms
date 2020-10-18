package com.jd.bluedragon.distribution.goodsLoadScan.domain;

import java.io.Serializable;
import java.util.Date;

public class GoodsLoadScanRecord implements Serializable {
    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务号
     */
    private Long taskId;


    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 扫描动作 1是装车扫描，0是取消扫描
     */
    private Integer scanAction;

    /**
     * 是否将包裹号转板号：1是，0否
     */
    private Integer transfer;


    /**
     * 创建人账号
     */
    private String createUserCode;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人账号
     */
    private String updateUserCode;

    /**
     * 更新人名称
     */
    private String updateUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 可用状态: 1 可用, 0 不可用
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWayBillCode() {
        return wayBillCode;
    }

    public void setWayBillCode(String wayBillCode) {
        this.wayBillCode = wayBillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getScanAction() {
        return scanAction;
    }

    public void setScanAction(Integer scanAction) {
        this.scanAction = scanAction;
    }

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
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
}
