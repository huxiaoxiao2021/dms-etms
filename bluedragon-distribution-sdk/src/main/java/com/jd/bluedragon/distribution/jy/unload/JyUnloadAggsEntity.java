package com.jd.bluedragon.distribution.jy.unload;


import java.io.Serializable;
import java.util.Date;

/**
 * 卸车进度汇总表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyUnloadAggsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JyUnloadAggsEntity() {}

    public JyUnloadAggsEntity(String bizId) {
        this.bizId = bizId;
    }

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 任务主键
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 操作场地ID
     */
    private Long operateSiteId;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 应扫数量
     */
    private Integer shouldScanCount;
    /**
     * 已扫数量
     */
    private Integer actualScanCount;
    /**
     * 拦截应扫数量
     */
    private Integer interceptShouldScanCount;
    /**
     * 拦截已扫数量
     */
    private Integer interceptActualScanCount;
    /**
     * 拦截未扫
     */
    private Integer interceptNotScanCount;
    /**
     * 本场地多扫数量
     */
    private Integer moreScanLocalCount;
    /**
     * 非本场地多扫数量
     */
    private Integer moreScanOutCount;
    /**
     * 多扫总计
     */
    private Integer moreScanTotalCount;
    /**
     * 应卸包裹总数--用于计算总进度
     */
    private Integer totalSealPackageCount;
    /**
     * 已卸包裹总数-用于计算总进度
     */
    private Integer totalScannedPackageCount;
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

    private String boardCode;
    private Integer shouldScanWaybillCount;
    private Integer actualScanWaybillCount;
    private Integer interceptShouldScanWaybillCount;
    private Integer interceptActualScanWaybillCount;
    private Integer moreScanLocalWaybillCount;
    private Integer moreScanOutWaybillCount;
    private Integer totalScannedBoardCount;
    private Integer totalScannedInterceptCount;

    private Integer totalSealWaybillCount;
    private Integer totalScannedWaybillCount;
    private Integer totalScannedInterceptWaybillCount;
    private Integer totalShouldInterceptWaybillCount;
    private Integer totalMoreScanLocalWaybillCount;
    private Integer totalMoreScanOutWaybillCount;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getShouldScanWaybillCount() {
        return shouldScanWaybillCount;
    }

    public void setShouldScanWaybillCount(Integer shouldScanWaybillCount) {
        this.shouldScanWaybillCount = shouldScanWaybillCount;
    }

    public Integer getActualScanWaybillCount() {
        return actualScanWaybillCount;
    }

    public void setActualScanWaybillCount(Integer actualScanWaybillCount) {
        this.actualScanWaybillCount = actualScanWaybillCount;
    }

    public Integer getInterceptShouldScanWaybillCount() {
        return interceptShouldScanWaybillCount;
    }

    public void setInterceptShouldScanWaybillCount(Integer interceptShouldScanWaybillCount) {
        this.interceptShouldScanWaybillCount = interceptShouldScanWaybillCount;
    }

    public Integer getInterceptActualScanWaybillCount() {
        return interceptActualScanWaybillCount;
    }

    public void setInterceptActualScanWaybillCount(Integer interceptActualScanWaybillCount) {
        this.interceptActualScanWaybillCount = interceptActualScanWaybillCount;
    }

    public Integer getMoreScanLocalWaybillCount() {
        return moreScanLocalWaybillCount;
    }

    public void setMoreScanLocalWaybillCount(Integer moreScanLocalWaybillCount) {
        this.moreScanLocalWaybillCount = moreScanLocalWaybillCount;
    }

    public Integer getMoreScanOutWaybillCount() {
        return moreScanOutWaybillCount;
    }

    public void setMoreScanOutWaybillCount(Integer moreScanOutWaybillCount) {
        this.moreScanOutWaybillCount = moreScanOutWaybillCount;
    }

    public Integer getTotalScannedBoardCount() {
        return totalScannedBoardCount;
    }

    public void setTotalScannedBoardCount(Integer totalScannedBoardCount) {
        this.totalScannedBoardCount = totalScannedBoardCount;
    }

    public Integer getTotalScannedInterceptCount() {
        return totalScannedInterceptCount;
    }

    public void setTotalScannedInterceptCount(Integer totalScannedInterceptCount) {
        this.totalScannedInterceptCount = totalScannedInterceptCount;
    }

    /**
     * 版本号
     */
    private Long version;

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

    public String setSealCarCode(String sealCarCode) {
        return this.sealCarCode = sealCarCode;
    }

    public String getSealCarCode() {
        return this.sealCarCode;
    }

    public Long setOperateSiteId(Long operateSiteId) {
        return this.operateSiteId = operateSiteId;
    }

    public Long getOperateSiteId() {
        return this.operateSiteId;
    }

    public String setProductType(String productType) {
        return this.productType = productType;
    }

    public String getProductType() {
        return this.productType;
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

    public Integer setInterceptShouldScanCount(Integer interceptShouldScanCount) {
        return this.interceptShouldScanCount = interceptShouldScanCount;
    }

    public Integer getInterceptShouldScanCount() {
        return this.interceptShouldScanCount;
    }

    public Integer setInterceptActualScanCount(Integer interceptActualScanCount) {
        return this.interceptActualScanCount = interceptActualScanCount;
    }

    public Integer getInterceptActualScanCount() {
        return this.interceptActualScanCount;
    }

    public Integer setInterceptNotScanCount(Integer interceptNotScanCount) {
        return this.interceptNotScanCount = interceptNotScanCount;
    }

    public Integer getInterceptNotScanCount() {
        return this.interceptNotScanCount;
    }

    public Integer setMoreScanLocalCount(Integer moreScanLocalCount) {
        return this.moreScanLocalCount = moreScanLocalCount;
    }

    public Integer getMoreScanLocalCount() {
        return this.moreScanLocalCount;
    }

    public Integer setMoreScanOutCount(Integer moreScanOutCount) {
        return this.moreScanOutCount = moreScanOutCount;
    }

    public Integer getMoreScanOutCount() {
        return this.moreScanOutCount;
    }

    public Integer setMoreScanTotalCount(Integer moreScanTotalCount) {
        return this.moreScanTotalCount = moreScanTotalCount;
    }

    public Integer getMoreScanTotalCount() {
        return this.moreScanTotalCount;
    }

    public Integer setTotalSealPackageCount(Integer totalSealPackageCount) {
        return this.totalSealPackageCount = totalSealPackageCount;
    }

    public Integer getTotalSealPackageCount() {
        return this.totalSealPackageCount;
    }

    public Integer setTotalScannedPackageCount(Integer totalScannedPackageCount) {
        return this.totalScannedPackageCount = totalScannedPackageCount;
    }

    public Integer getTotalScannedPackageCount() {
        return this.totalScannedPackageCount;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    public Integer getTotalSealWaybillCount() {
        return totalSealWaybillCount;
    }

    public void setTotalSealWaybillCount(Integer totalSealWaybillCount) {
        this.totalSealWaybillCount = totalSealWaybillCount;
    }

    public Integer getTotalScannedWaybillCount() {
        return totalScannedWaybillCount;
    }

    public void setTotalScannedWaybillCount(Integer totalScannedWaybillCount) {
        this.totalScannedWaybillCount = totalScannedWaybillCount;
    }

    public Integer getTotalScannedInterceptWaybillCount() {
        return totalScannedInterceptWaybillCount;
    }

    public void setTotalScannedInterceptWaybillCount(Integer totalScannedInterceptWaybillCount) {
        this.totalScannedInterceptWaybillCount = totalScannedInterceptWaybillCount;
    }

    public Integer getTotalShouldInterceptWaybillCount() {
        return totalShouldInterceptWaybillCount;
    }

    public void setTotalShouldInterceptWaybillCount(Integer totalShouldInterceptWaybillCount) {
        this.totalShouldInterceptWaybillCount = totalShouldInterceptWaybillCount;
    }

    public Integer getTotalMoreScanLocalWaybillCount() {
        return totalMoreScanLocalWaybillCount;
    }

    public void setTotalMoreScanLocalWaybillCount(Integer totalMoreScanLocalWaybillCount) {
        this.totalMoreScanLocalWaybillCount = totalMoreScanLocalWaybillCount;
    }

    public Integer getTotalMoreScanOutWaybillCount() {
        return totalMoreScanOutWaybillCount;
    }

    public void setTotalMoreScanOutWaybillCount(Integer totalMoreScanOutWaybillCount) {
        this.totalMoreScanOutWaybillCount = totalMoreScanOutWaybillCount;
    }
}
