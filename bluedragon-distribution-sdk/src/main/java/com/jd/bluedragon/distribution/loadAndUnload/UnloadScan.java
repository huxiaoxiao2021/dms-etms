package com.jd.bluedragon.distribution.loadAndUnload;


import java.io.Serializable;
import java.util.Date;

/**
 * 卸车扫描运单暂存表
 * @author lvyuan21
 * @date 2020-12-25 16:19
 */
public class UnloadScan implements Serializable {

    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 总包裹数量
     */
    private Integer packageAmount;

    /**
     * 应卸数量
     */
    private Integer forceAmount;

    /**
     * 已卸数量
     */
    private Integer loadAmount;

    /**
     * 未卸数量
     */
    private Integer unloadAmount;

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

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
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
