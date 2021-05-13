package com.jd.bluedragon.distribution.loadAndUnload;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 封车任务下总包裹数量
     */
    private Integer taskPackageAmount;

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
     * 多卸数量
     */
    private Integer surplusAmount;

    /**
     * 未卸数量
     */
    private Integer unloadAmount;

    /**
     * 运单颜色状态--0无特殊颜色,1绿色,2橙色,3黄色,4红色
     */
    private Integer status;

    /**
     * 运单重量
     */
    private Double weight;

    /**
     * 运单体积
     */
    private Double volume;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人erp
     */
    private String updateUserErp;

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

    private List<WaybillPackageNumInfo> waybillPackageNumInfoList;



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

    public Integer getTaskPackageAmount() {
        return taskPackageAmount;
    }

    public void setTaskPackageAmount(Integer taskPackageAmount) {
        this.taskPackageAmount = taskPackageAmount;
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

    public List<WaybillPackageNumInfo> getWaybillPackageNumInfoList() {
        return waybillPackageNumInfoList;
    }

    public void setWaybillPackageNumInfoList(List<WaybillPackageNumInfo> waybillPackageNumInfoList) {
        this.waybillPackageNumInfoList = waybillPackageNumInfoList;
    }

    public Integer getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(Integer surplusAmount) {
        this.surplusAmount = surplusAmount;
    }
}
