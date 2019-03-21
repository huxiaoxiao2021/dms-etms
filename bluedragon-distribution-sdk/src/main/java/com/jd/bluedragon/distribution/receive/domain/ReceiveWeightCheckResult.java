package com.jd.bluedragon.distribution.receive.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @ClassName: ReceiveWeightCheckResult
 * @Description: 揽收重量校验统计-实体类
 * @author: hujiping
 * @date: 2019/2/27 13:48
 */
public class ReceiveWeightCheckResult extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     * */
    private Long id;
    /** 复核日期 */
    private Date reviewDate;
    /** 包裹号 */
    private String packageCode;
    /** 商家名称 */
    private String busiName;
    /** 复核区域 */
    private Integer reviewOrgCode;
    private String reviewOrg;
    /** 复核分拣 */
    private Integer reviewCreateSiteCode;
    private String reviewCreateSiteName;
    /** 复核人erp */
    private String reviewErp;
    /** 分拣重量kg */
    private Double reviewWeight;
    /** 复核长宽高cm */
    private String reviewLwh;
    /** 复核体积cm */
    private Double reviewVolume;
    /** 揽收区域 */
    private String receiveOrg;
    /** 揽收营业部 */
    private String receiveDepartment;
    /** 揽收人erp */
    private String receiveErp;
    /** 揽收重量kg */
    private Double receiveWeight;
    /** 揽收长宽高cm */
    private String receiveLwh;
    /** 揽收体积cm */
    private Double receiveVolume;
    /** 重量差异 */
    private String weightDiff;
    /** 体积重量差异 */
    private String volumeWeightDiff;
    /** 误差标准值 */
    private String diffStandard;
    /** 是否超标 */
    private Integer isExcess;
    /** 是否有效 1：有效 0：无效 */
    private Integer isDelete;
    /** 默认时间 */
    private Date ts;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public Integer getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(Integer reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
    }

    public String getReviewOrg() {
        return reviewOrg;
    }

    public void setReviewOrg(String reviewOrg) {
        this.reviewOrg = reviewOrg;
    }

    public Integer getReviewCreateSiteCode() {
        return reviewCreateSiteCode;
    }

    public void setReviewCreateSiteCode(Integer reviewCreateSiteCode) {
        this.reviewCreateSiteCode = reviewCreateSiteCode;
    }

    public String getReviewCreateSiteName() {
        return reviewCreateSiteName;
    }

    public void setReviewCreateSiteName(String reviewCreateSiteName) {
        this.reviewCreateSiteName = reviewCreateSiteName;
    }

    public String getReviewErp() {
        return reviewErp;
    }

    public void setReviewErp(String reviewErp) {
        this.reviewErp = reviewErp;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public String getReviewLwh() {
        return reviewLwh;
    }

    public void setReviewLwh(String reviewLwh) {
        this.reviewLwh = reviewLwh;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public String getReceiveOrg() {
        return receiveOrg;
    }

    public void setReceiveOrg(String receiveOrg) {
        this.receiveOrg = receiveOrg;
    }

    public String getReceiveDepartment() {
        return receiveDepartment;
    }

    public void setReceiveDepartment(String receiveDepartment) {
        this.receiveDepartment = receiveDepartment;
    }

    public String getReceiveErp() {
        return receiveErp;
    }

    public void setReceiveErp(String receiveErp) {
        this.receiveErp = receiveErp;
    }

    public Double getReceiveWeight() {
        return receiveWeight;
    }

    public void setReceiveWeight(Double receiveWeight) {
        this.receiveWeight = receiveWeight;
    }

    public String getReceiveLwh() {
        return receiveLwh;
    }

    public void setReceiveLwh(String receiveLwh) {
        this.receiveLwh = receiveLwh;
    }

    public Double getReceiveVolume() {
        return receiveVolume;
    }

    public void setReceiveVolume(Double receiveVolume) {
        this.receiveVolume = receiveVolume;
    }

    public String getWeightDiff() {
        return weightDiff;
    }

    public void setWeightDiff(String weightDiff) {
        this.weightDiff = weightDiff;
    }

    public String getVolumeWeightDiff() {
        return volumeWeightDiff;
    }

    public void setVolumeWeightDiff(String volumeWeightDiff) {
        this.volumeWeightDiff = volumeWeightDiff;
    }

    public String getDiffStandard() {
        return diffStandard;
    }

    public void setDiffStandard(String diffStandard) {
        this.diffStandard = diffStandard;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    @Override
    public Integer getIsDelete() {
        return isDelete;
    }

    @Override
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public Date getTs() {
        return ts;
    }

    @Override
    public void setTs(Date ts) {
        this.ts = ts;
    }
}
