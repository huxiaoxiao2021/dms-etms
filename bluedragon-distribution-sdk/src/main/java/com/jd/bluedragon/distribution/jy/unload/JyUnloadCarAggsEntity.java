package com.jd.bluedragon.distribution.jy.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 15:51
 * @Description: 卸车进度汇总
 */
public class JyUnloadCarAggsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    // 业务主键
    private String bizId;

    // 操作场地ID
    private Integer operateSiteId;

    // 封车编码
    private String sealCarCode;

    // 产品类型
    private String productType;

    // 应扫数量
    private Integer shouldScanCount;

    // 已扫数量
    private Integer actualScanCount;

    // 拦截应扫数量
    private Integer interceptShouldScanCount;

    // 拦截已扫数量
    private Integer interceptActualScanCount;

    // 拦截未扫
    private Integer interceptNotScanCount;

    // 本场地多扫数量
    private Integer moreScanLocalCount;

    // 非本场地多扫数量
    private Integer moreScanOutCount;

    // 多扫总计
    private Integer moreScanTotalCount;

    // 应卸包裹总数(不含多扫)--用于计算总进度
    private Integer totalSealPackageCount;

    // 扫描总数量：含多扫
    private Integer totalWithMoreScanCount;

    // 本场地扫描总数量：含多扫
    private Integer totalLocalWithMoreScanCount;

    // 已卸包裹总数-用于计算总进度
    private Integer totalScannedPackageCount;

    // 创建时间
    private Date createTime;

    // 是否删除：1-有效，0-删除
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Integer actualScanCount) {
        this.actualScanCount = actualScanCount;
    }

    public Integer getInterceptShouldScanCount() {
        return interceptShouldScanCount;
    }

    public void setInterceptShouldScanCount(Integer interceptShouldScanCount) {
        this.interceptShouldScanCount = interceptShouldScanCount;
    }

    public Integer getInterceptActualScanCount() {
        return interceptActualScanCount;
    }

    public void setInterceptActualScanCount(Integer interceptActualScanCount) {
        this.interceptActualScanCount = interceptActualScanCount;
    }

    public Integer getInterceptNotScanCount() {
        return interceptNotScanCount;
    }

    public void setInterceptNotScanCount(Integer interceptNotScanCount) {
        this.interceptNotScanCount = interceptNotScanCount;
    }

    public Integer getMoreScanLocalCount() {
        return moreScanLocalCount;
    }

    public void setMoreScanLocalCount(Integer moreScanLocalCount) {
        this.moreScanLocalCount = moreScanLocalCount;
    }

    public Integer getMoreScanOutCount() {
        return moreScanOutCount;
    }

    public void setMoreScanOutCount(Integer moreScanOutCount) {
        this.moreScanOutCount = moreScanOutCount;
    }

    public Integer getMoreScanTotalCount() {
        return moreScanTotalCount;
    }

    public void setMoreScanTotalCount(Integer moreScanTotalCount) {
        this.moreScanTotalCount = moreScanTotalCount;
    }

    public Integer getTotalSealPackageCount() {
        return totalSealPackageCount;
    }

    public void setTotalSealPackageCount(Integer totalSealPackageCount) {
        this.totalSealPackageCount = totalSealPackageCount;
    }

    public Integer getTotalWithMoreScanCount() {
        return totalWithMoreScanCount;
    }

    public void setTotalWithMoreScanCount(Integer totalWithMoreScanCount) {
        this.totalWithMoreScanCount = totalWithMoreScanCount;
    }

    public Integer getTotalLocalWithMoreScanCount() {
        return totalLocalWithMoreScanCount;
    }

    public void setTotalLocalWithMoreScanCount(Integer totalLocalWithMoreScanCount) {
        this.totalLocalWithMoreScanCount = totalLocalWithMoreScanCount;
    }

    public Integer getTotalScannedPackageCount() {
        return totalScannedPackageCount;
    }

    public void setTotalScannedPackageCount(Integer totalScannedPackageCount) {
        this.totalScannedPackageCount = totalScannedPackageCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
