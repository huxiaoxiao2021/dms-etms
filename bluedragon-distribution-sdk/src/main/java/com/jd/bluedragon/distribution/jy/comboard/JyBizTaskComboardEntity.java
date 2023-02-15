package com.jd.bluedragon.distribution.jy.comboard;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JyBizTaskComboardEntity implements Serializable {

    public static final String BIZ_PREFIX = "CB%s";
    private Long id;

    private String bizId;

    private String boardCode;

    private String sendCode;

    private Integer boardStatus;

    private Long startSiteId;

    private String startSiteName;

    private Long endSiteId;

    private String endSiteName;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    private Boolean bulkFlag;

    private Integer haveScanCount;

    private Date sealTime;

    private Date unsealTime;

    private Integer comboardSource;

    /**
     * 未封车状态
     */
    private transient List<Integer> statusList;

    /**
     * 已封车状态
     */
    private transient List<Integer> sealStatusList;
    
    private transient List<String> sendCodeList;
    
    private transient List<Integer> comboardSourceList;

    public Date getSealTime() {
        return sealTime;
    }

    public void setSealTime(Date sealTime) {
        this.sealTime = sealTime;
    }

    public Date getUnsealTime() {
        return unsealTime;
    }

    public void setUnsealTime(Date unsealTime) {
        this.unsealTime = unsealTime;
    }

    public List<String> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<String> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }

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

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Boolean getBulkFlag() {
        return bulkFlag;
    }

    public void setBulkFlag(Boolean bulkFlag) {
        this.bulkFlag = bulkFlag;
    }
    

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
    }

    public Integer getBoardStatus() {
        return boardStatus;
    }

    public void setBoardStatus(Integer boardStatus) {
        this.boardStatus = boardStatus;
    }

    public Integer getComboardSource() {
        return comboardSource;
    }

    public void setComboardSource(Integer comboardSource) {
        this.comboardSource = comboardSource;
    }

    public List<Integer> getComboardSourceList() {
        return comboardSourceList;
    }

    public void setComboardSourceList(List<Integer> comboardSourceList) {
        this.comboardSourceList = comboardSourceList;
    }

    public List<Integer> getSealStatusList() {
        return sealStatusList;
    }

    public void setSealStatusList(List<Integer> sealStatusList) {
        this.sealStatusList = sealStatusList;
    }
}
