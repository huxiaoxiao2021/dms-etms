package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-09 17:00:36 周天
 */
public class BaseDmsStore implements Serializable {

    private static final long serialVersionUID = -5753843624379707435L;

    private Integer id;

    private Integer orgId;

    private Integer dmsId;

    private Integer cky2;

    private Integer storeId;

    private String storeType;
    private String storeName;
    private String memo;
    private String createOperatorAccount;
    private String updateOperatorAccount;

    private Date createTime;

    private Date updateTime;

    private String createOperatorName;

    private String updateOperatorName;

    private String remark;

    private int yn;
    private int startIndex;
    private int endIndex;

    public Integer getId() {
        return id;
    }

    public BaseDmsStore setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public BaseDmsStore setOrgId(Integer orgId) {
        this.orgId = orgId;
        return this;
    }

    public Integer getDmsId() {
        return dmsId;
    }

    public BaseDmsStore setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
        return this;
    }

    public Integer getCky2() {
        return cky2;
    }

    public BaseDmsStore setCky2(Integer cky2) {
        this.cky2 = cky2;
        return this;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public BaseDmsStore setStoreId(Integer storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getStoreType() {
        return storeType;
    }

    public BaseDmsStore setStoreType(String storeType) {
        this.storeType = storeType;
        return this;
    }

    public String getStoreName() {
        return storeName;
    }

    public BaseDmsStore setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public String getMemo() {
        return memo;
    }

    public BaseDmsStore setMemo(String memo) {
        this.memo = memo;
        return this;
    }

    public String getCreateOperatorAccount() {
        return createOperatorAccount;
    }

    public BaseDmsStore setCreateOperatorAccount(String createOperatorAccount) {
        this.createOperatorAccount = createOperatorAccount;
        return this;
    }

    public String getUpdateOperatorAccount() {
        return updateOperatorAccount;
    }

    public BaseDmsStore setUpdateOperatorAccount(String updateOperatorAccount) {
        this.updateOperatorAccount = updateOperatorAccount;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public BaseDmsStore setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public BaseDmsStore setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public BaseDmsStore setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
        return this;
    }

    public String getUpdateOperatorName() {
        return updateOperatorName;
    }

    public BaseDmsStore setUpdateOperatorName(String updateOperatorName) {
        this.updateOperatorName = updateOperatorName;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public BaseDmsStore setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public int getYn() {
        return yn;
    }

    public BaseDmsStore setYn(int yn) {
        this.yn = yn;
        return this;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public BaseDmsStore setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public BaseDmsStore setEndIndex(int endIndex) {
        this.endIndex = endIndex;
        return this;
    }
}
