package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价申诉请求对象
 * @date 2024/3/5
 */
public class JyEvaluateRecordAppealDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 被评价目标业务主键
     */
    private String targetBizId;
    /**
     * 评价来源业务主键
     */
    private String sourceBizId;
    /**
     * 被评价目标场地编码
     */
    private Long targetSiteCode;
    /**
     * 评价来源场地编码
     */
    private Long sourceSiteCode;
    /**
     * 申诉状态，0：未申诉，1：超时未申诉，2：待审核，3：已审核，4：超时未审核
     */
    private Integer appealStatus;
    /**
     * 申诉理由
     */
    private String reasons;
    /**
     * 评价维度编码
     */
    private Integer dimensionCode;
    /**
     * 申诉结果，0：未申诉，1：通过，2：驳回
     */
    private Integer appealResult;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 修改人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 数据变更时间戳
     */
    private Date ts;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 场地编码
     */
    private Integer siteCode;
    /**
     * 图片集合
     */
    private List<String> imgUrlList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
    }

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public Long getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(Long targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public Long getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Long sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public Integer getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(Integer dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public Integer getAppealResult() {
        return appealResult;
    }

    public void setAppealResult(Integer appealResult) {
        this.appealResult = appealResult;
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }
}
