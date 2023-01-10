package com.jd.bluedragon.distribution.jy.send;


import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 发货数据统计表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendAggsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 任务主键
     */
    private String bizId;
    /**
     * 派车明细单号
     */
    private String transWorkItemCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 操作场地ID
     */
    private Long operateSiteId;
    /**
     * 目的场地ID
     */
    private Long receiveSiteId;
    /**
     * 当前流向应扫包裹总计
     */
    private Integer shouldScanCount;
    /**
     * 当前流向已扫包裹总计
     */
    private Integer actualScanCount;
    /**
     * 当前流向已扫总重量/吨
     */
    private BigDecimal actualScanWeight;
    /**
     * 当前流向已扫总体积/立方厘米
     */
    private BigDecimal actualScanVolume;
    /**
     * 当前流向拦截数量
     */
    private Integer interceptScanCount;
    /**
     * 当前流向强发数量
     */
    private Integer forceSendCount;
    /**
     * 按派车任务汇总的应扫包裹总计
     */
    private Integer totalShouldScanCount;
    /**
     * 按派车任务汇总的已扫包裹总计
     */
    private Integer totalScannedCount;
    /**
     * 按派车任务汇总的拦截总计
     */
    private Integer totalInterceptCount;
    /**
     * 按派车任务汇总的强发总计
     */
    private Integer totalForceSendCount;
    /**
     * 按派车任务汇总的已扫总量总计/千克
     */
    private BigDecimal totalScannedWeight;
    /**
     * 按派车任务汇总已扫体积总计/立方厘米
     */
    private BigDecimal totalScannedVolume;
    /**
     * 按派车任务汇总的已扫包裹条码总计(报表用)
     */
    private Integer totalScannedPackageCodeCount;
    /**
     * 按派车任务汇总的已扫箱号条码总计(报表用)
     */
    private Integer totalScannedBoxCodeCount;
    /**
     * 车辆可装载总重量/吨
     */
    private BigDecimal vehicleWeight;
    /**
     * 车辆可装载总体积/立方米
     */
    private BigDecimal vehicleVolume;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;
    /**
     * 当前流向已扫包裹条码总计
     */
    private Integer actualScanPackageCodeCount;
    /**
     * 当前流向已扫箱号条码总计
     */
    private Integer actualScanBoxCodeCount;
    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 版本号
     */
    private Long version;

    private Integer totalScannedWaybillCount;
    private Integer totalInterceptWaybillCount;
    private Integer totalIncompleteWaybillCount;
    private Integer totalNotScannedWaybillCount;
    private Integer totalForceWaybillCount;

    /**
     * 并发锁id
     */
    private String uid;

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setBizId(String bizId) {
        return this.bizId = bizId;
    }

    public String getBizId() {
        return this.bizId;
    }

    public String setTransWorkItemCode(String transWorkItemCode) {
        return this.transWorkItemCode = transWorkItemCode;
    }

    public String getTransWorkItemCode() {
        return this.transWorkItemCode;
    }

    public String setVehicleNumber(String vehicleNumber) {
        return this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public Long setOperateSiteId(Long operateSiteId) {
        return this.operateSiteId = operateSiteId;
    }

    public Long getOperateSiteId() {
        return this.operateSiteId;
    }

    public Long setReceiveSiteId(Long receiveSiteId) {
        return this.receiveSiteId = receiveSiteId;
    }

    public Long getReceiveSiteId() {
        return this.receiveSiteId;
    }

    public Integer setShouldScanCount(Integer shouldScanCount) {
        return this.shouldScanCount = shouldScanCount;
    }

    public Integer getShouldScanCount() {
        return this.shouldScanCount;
    }

    public Integer setActualScanCount(Integer actualScanCount) {
        return this.actualScanCount = actualScanCount;
    }

    public Integer getActualScanCount() {
        return this.actualScanCount;
    }

    public BigDecimal setActualScanWeight(BigDecimal actualScanWeight) {
        return this.actualScanWeight = actualScanWeight;
    }

    public BigDecimal getActualScanWeight() {
        return this.actualScanWeight;
    }

    public BigDecimal setActualScanVolume(BigDecimal actualScanVolume) {
        return this.actualScanVolume = actualScanVolume;
    }

    public BigDecimal getActualScanVolume() {
        return this.actualScanVolume;
    }

    public Integer setInterceptScanCount(Integer interceptScanCount) {
        return this.interceptScanCount = interceptScanCount;
    }

    public Integer getInterceptScanCount() {
        return this.interceptScanCount;
    }

    public Integer setForceSendCount(Integer forceSendCount) {
        return this.forceSendCount = forceSendCount;
    }

    public Integer getForceSendCount() {
        return this.forceSendCount;
    }

    public Integer setTotalShouldScanCount(Integer totalShouldScanCount) {
        return this.totalShouldScanCount = totalShouldScanCount;
    }

    public Integer getTotalShouldScanCount() {
        return this.totalShouldScanCount;
    }

    public Integer setTotalScannedCount(Integer totalScannedCount) {
        return this.totalScannedCount = totalScannedCount;
    }

    public Integer getTotalScannedCount() {
        return this.totalScannedCount;
    }

    public Integer setTotalInterceptCount(Integer totalInterceptCount) {
        return this.totalInterceptCount = totalInterceptCount;
    }

    public Integer getTotalInterceptCount() {
        return this.totalInterceptCount;
    }

    public Integer setTotalForceSendCount(Integer totalForceSendCount) {
        return this.totalForceSendCount = totalForceSendCount;
    }

    public Integer getTotalForceSendCount() {
        return this.totalForceSendCount;
    }

    public BigDecimal setTotalScannedWeight(BigDecimal totalScannedWeight) {
        return this.totalScannedWeight = totalScannedWeight;
    }

    public BigDecimal getTotalScannedWeight() {
        return this.totalScannedWeight;
    }

    public BigDecimal setTotalScannedVolume(BigDecimal totalScannedVolume) {
        return this.totalScannedVolume = totalScannedVolume;
    }

    public BigDecimal getTotalScannedVolume() {
        return this.totalScannedVolume;
    }

    public Integer setTotalScannedPackageCodeCount(Integer totalScannedPackageCodeCount) {
        return this.totalScannedPackageCodeCount = totalScannedPackageCodeCount;
    }

    public Integer getTotalScannedPackageCodeCount() {
        return this.totalScannedPackageCodeCount;
    }

    public Integer setTotalScannedBoxCodeCount(Integer totalScannedBoxCodeCount) {
        return this.totalScannedBoxCodeCount = totalScannedBoxCodeCount;
    }

    public Integer getTotalScannedBoxCodeCount() {
        return this.totalScannedBoxCodeCount;
    }

    public BigDecimal setVehicleWeight(BigDecimal vehicleWeight) {
        return this.vehicleWeight = vehicleWeight;
    }

    public BigDecimal getVehicleWeight() {
        return this.vehicleWeight;
    }

    public BigDecimal setVehicleVolume(BigDecimal vehicleVolume) {
        return this.vehicleVolume = vehicleVolume;
    }

    public BigDecimal getVehicleVolume() {
        return this.vehicleVolume;
    }

    public Date setCreateTime(Date createTime) {
        return this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
    }

    public Integer setActualScanPackageCodeCount(Integer actualScanPackageCodeCount) {
        return this.actualScanPackageCodeCount = actualScanPackageCodeCount;
    }

    public Integer getActualScanPackageCodeCount() {
        return this.actualScanPackageCodeCount;
    }

    public Integer setActualScanBoxCodeCount(Integer actualScanBoxCodeCount) {
        return this.actualScanBoxCodeCount = actualScanBoxCodeCount;
    }

    public Integer getActualScanBoxCodeCount() {
        return this.actualScanBoxCodeCount;
    }

    public String setSendVehicleBizId(String sendVehicleBizId) {
        return this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getSendVehicleBizId() {
        return this.sendVehicleBizId;
    }

    public Long getVersion() {
        return version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    public Integer getTotalScannedWaybillCount() {
        return totalScannedWaybillCount;
    }

    public void setTotalScannedWaybillCount(Integer totalScannedWaybillCount) {
        this.totalScannedWaybillCount = totalScannedWaybillCount;
    }

    public Integer getTotalInterceptWaybillCount() {
        return totalInterceptWaybillCount;
    }

    public void setTotalInterceptWaybillCount(Integer totalInterceptWaybillCount) {
        this.totalInterceptWaybillCount = totalInterceptWaybillCount;
    }

    public Integer getTotalIncompleteWaybillCount() {
        return totalIncompleteWaybillCount;
    }

    public void setTotalIncompleteWaybillCount(Integer totalIncompleteWaybillCount) {
        this.totalIncompleteWaybillCount = totalIncompleteWaybillCount;
    }

    public Integer getTotalNotScannedWaybillCount() {
        return totalNotScannedWaybillCount;
    }

    public void setTotalNotScannedWaybillCount(Integer totalNotScannedWaybillCount) {
        this.totalNotScannedWaybillCount = totalNotScannedWaybillCount;
    }

    public Integer getTotalForceWaybillCount() {
        return totalForceWaybillCount;
    }

    public void setTotalForceWaybillCount(Integer totalForceWaybillCount) {
        this.totalForceWaybillCount = totalForceWaybillCount;
    }


}
