package com.jd.bluedragon.distribution.jy.summary;

import java.io.Serializable;
import java.util.Date;

public class JyStatisticsSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String businessKey;
    /**
     * BusinessKeyTypeEnum
     */
    private String businessKeyType;

    private Integer operateSiteCode;

    /**
     * SummarySourceEnum
     */
    private String source;

    private Double weight;

    private Double volume;

    private Integer itemNum;
    /**
     * 托盘数
     */
    private Integer palletCount;
    /**
     * 封车批次号数量
     */
    private Integer sealBatchCodeNum;
    /**
     * 封车内绑定任务数量
     */
    private Integer sealBindAviationTaskNum;

    private Integer subBusinessNum;

    private String transportCode;

    private Date departTime;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Integer yn;



    public JyStatisticsSummaryEntity() {
    }

    public JyStatisticsSummaryEntity(String businessKey, String businessKeyType, Integer operateSiteCode, String source) {
        this.businessKey = businessKey;
        this.businessKeyType = businessKeyType;
        this.operateSiteCode = operateSiteCode;
        this.source = source;
        this.yn = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessKeyType() {
        return businessKeyType;
    }

    public void setBusinessKeyType(String businessKeyType) {
        this.businessKeyType = businessKeyType;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
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

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getSubBusinessNum() {
        return subBusinessNum;
    }

    public void setSubBusinessNum(Integer subBusinessNum) {
        this.subBusinessNum = subBusinessNum;
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

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getSealBatchCodeNum() {
        return sealBatchCodeNum;
    }

    public void setSealBatchCodeNum(Integer sealBatchCodeNum) {
        this.sealBatchCodeNum = sealBatchCodeNum;
    }

    public Integer getSealBindAviationTaskNum() {
        return sealBindAviationTaskNum;
    }

    public void setSealBindAviationTaskNum(Integer sealBindAviationTaskNum) {
        this.sealBindAviationTaskNum = sealBindAviationTaskNum;
    }

    public Integer getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(Integer palletCount) {
        this.palletCount = palletCount;
    }
}