package com.jd.bluedragon.distribution.goodsLoadScan.domain;


import java.io.Serializable;
import java.util.Date;

/**
 * 装车扫描明细表
 * @author lvyuan21
 * @date 2020-10-15 22:05
 */
public class GoodsLoadScan implements Serializable {
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
     * 总包裹数量
     */
    private Integer packageAmount;

    /**
     * 库存数量
     */
    private Integer goodsAmount;

    /**
     * 已装车数量
     */
    private Integer loadAmount;

    /**
     * 未装车数量
     */
    private Integer unloadAmount;

    /**
     * 强制下发数量
     */
    private Integer forceAmount;

    /**
     * 运单总重量
     */
    private Double weight;

    /**
     * 运单总体积
     */
    private Double volume;


    /**
     * 运单颜色状态--0无特殊颜色,1绿色,2橙色,3黄色,4红色
     */
    private Integer status;

    /**
     * 创建人账号
     */
    private Integer createUserCode;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人账号
     */
    private Integer updateUserCode;

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

    public Integer getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Integer getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(Integer goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public Integer getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(Integer loadAmount) {
        this.loadAmount = loadAmount;
    }

    public Integer getUnloadAmount() {
        return unloadAmount;
    }

    public void setUnloadAmount(Integer unloadAmount) {
        this.unloadAmount = unloadAmount;
    }

    public Integer getForceAmount() {
        return forceAmount;
    }

    public void setForceAmount(Integer forceAmount) {
        this.forceAmount = forceAmount;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
