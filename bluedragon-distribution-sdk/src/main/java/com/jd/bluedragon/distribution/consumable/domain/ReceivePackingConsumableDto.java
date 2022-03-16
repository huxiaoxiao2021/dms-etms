package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: ReceivePackingConsumableDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/7 11:27
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class ReceivePackingConsumableDto implements Serializable {

    /**
     * 揽收站点对应的分拣中心的id
     */
    private Integer dmsCode;

    /**
     * 包装总价
     */
    private String boxTotalPrice;

    /**
     * 订单号
     */
    private String commandId;

    /**
     * 操作人id
     */
    private Integer entryId;

    /**
     * 非消息中的字段，
     * 操作人ERP
     */
    private String entryErp;

    /**
     * 操作人姓名
     */
    private String entryName;

    /**
     * 货物名称
     */
    private String goods;

    /**
     * 是否取消
     */
    private Integer isCancel;

    /**
     * 是否为有任务揽收1为有任务
     */
    private Integer isExistenceTask;

    /**
     * 机构id
     */
    private Integer orgId;

    /**
     * 包裹数量
     */
    private Integer packCount;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 一体机操作时间
     */
    private Long pdaTime;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 来源 1：配送员；2：司机/运输调度员
     */
    private Integer source;

    /**
     * 体积-高度cm
     */
    private Double vloum_height;

    /**
     * 体积-长度cm
     */
    private Double vloum_length;

    /**
     * 体积-宽度cm
     */
    private Double vloum_width;

    /**
     * 重量kg
     */
    private Double weight;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单标位
     */
    private String waybillSign;

    /**
     * 包装耗材明细
     */
    private List<BoxChargeDetail> boxChargeDetails;

    /**
     * 称重方式 0|null：包裹维度 ；1：运单维度
     */
    private Integer weighingType;

    public Integer getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(Integer dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getBoxTotalPrice() {
        return boxTotalPrice;
    }

    public void setBoxTotalPrice(String boxTotalPrice) {
        this.boxTotalPrice = boxTotalPrice;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public String getEntryErp() {
        return entryErp;
    }

    public void setEntryErp(String entryErp) {
        this.entryErp = entryErp;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getIsExistenceTask() {
        return isExistenceTask;
    }

    public void setIsExistenceTask(Integer isExistenceTask) {
        this.isExistenceTask = isExistenceTask;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getPackCount() {
        return packCount;
    }

    public void setPackCount(Integer packCount) {
        this.packCount = packCount;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getPdaTime() {
        return pdaTime;
    }

    public void setPdaTime(Long pdaTime) {
        this.pdaTime = pdaTime;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Double getVloum_height() {
        return vloum_height;
    }

    public void setVloum_height(Double vloum_height) {
        this.vloum_height = vloum_height;
    }

    public Double getVloum_length() {
        return vloum_length;
    }

    public void setVloum_length(Double vloum_length) {
        this.vloum_length = vloum_length;
    }

    public Double getVloum_width() {
        return vloum_width;
    }

    public void setVloum_width(Double vloum_width) {
        this.vloum_width = vloum_width;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public Integer getWeighingType() {
        return weighingType;
    }

    public void setWeighingType(Integer weighingType) {
        this.weighingType = weighingType;
    }

    public List<BoxChargeDetail> getBoxChargeDetails() {
        return boxChargeDetails;
    }

    public void setBoxChargeDetails(List<BoxChargeDetail> boxChargeDetails) {
        this.boxChargeDetails = boxChargeDetails;
    }
}
