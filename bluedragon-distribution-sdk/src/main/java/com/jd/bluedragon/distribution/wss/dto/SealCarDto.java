package com.jd.bluedragon.distribution.wss.dto;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanglei51 on 2017/5/19.
 */
public class SealCarDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String sealCarCode;
    private Integer status;
    private Integer source;
    private String vehicleNumber;
    private String transportCode;
    private String startOrgCode;
    private String startOrgName;
    private Integer startSiteId;
    private String startSiteCode;
    private String startSiteName;
    private String endOrgCode;
    private String endOrgName;
    private Integer endSiteId;
    private String endSiteCode;
    private String endSiteName;


    private String sealCarTime;

    private Integer sealSiteId;
    private String sealSiteCode;
    private String sealSiteName;
    private String sealUserCode;
    private String sealUserName;

    private String desealCarTime;

    private Integer desealSiteId;
    private String desealSiteCode;
    private String desealSiteName;
    private String desealUserCode;
    private String desealUserName;
    private String remark;
    private String createTime;
    private String updateTime;
    private Integer yn;
    private String sealCode;
    private List<String> batchCodes;
    private List<String> sealCodes;
    private List<String> desealCodes;

    public SealCarDto() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSealCarCode() {
        return this.sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSource() {
        return this.source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTransportCode() {
        return this.transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getStartOrgCode() {
        return this.startOrgCode;
    }

    public void setStartOrgCode(String startOrgCode) {
        this.startOrgCode = startOrgCode;
    }

    public String getStartOrgName() {
        return this.startOrgName;
    }

    public void setStartOrgName(String startOrgName) {
        this.startOrgName = startOrgName;
    }

    public Integer getStartSiteId() {
        return this.startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteCode() {
        return this.startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getStartSiteName() {
        return this.startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getEndOrgCode() {
        return this.endOrgCode;
    }

    public void setEndOrgCode(String endOrgCode) {
        this.endOrgCode = endOrgCode;
    }

    public String getEndOrgName() {
        return this.endOrgName;
    }

    public void setEndOrgName(String endOrgName) {
        this.endOrgName = endOrgName;
    }

    public Integer getEndSiteId() {
        return this.endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteCode() {
        return this.endSiteCode;
    }

    public void setEndSiteCode(String endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return this.endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }


    public Integer getSealSiteId() {
        return this.sealSiteId;
    }

    public void setSealSiteId(Integer sealSiteId) {
        this.sealSiteId = sealSiteId;
    }

    public String getSealSiteCode() {
        return this.sealSiteCode;
    }

    public void setSealSiteCode(String sealSiteCode) {
        this.sealSiteCode = sealSiteCode;
    }

    public String getSealSiteName() {
        return this.sealSiteName;
    }

    public void setSealSiteName(String sealSiteName) {
        this.sealSiteName = sealSiteName;
    }

    public String getSealUserCode() {
        return this.sealUserCode;
    }

    public void setSealUserCode(String sealUserCode) {
        this.sealUserCode = sealUserCode;
    }

    public String getSealUserName() {
        return this.sealUserName;
    }

    public void setSealUserName(String sealUserName) {
        this.sealUserName = sealUserName;
    }


    public Integer getDesealSiteId() {
        return this.desealSiteId;
    }

    public void setDesealSiteId(Integer desealSiteId) {
        this.desealSiteId = desealSiteId;
    }

    public String getDesealSiteCode() {
        return this.desealSiteCode;
    }

    public void setDesealSiteCode(String desealSiteCode) {
        this.desealSiteCode = desealSiteCode;
    }

    public String getDesealSiteName() {
        return this.desealSiteName;
    }

    public void setDesealSiteName(String desealSiteName) {
        this.desealSiteName = desealSiteName;
    }

    public String getDesealUserCode() {
        return this.desealUserCode;
    }

    public void setDesealUserCode(String desealUserCode) {
        this.desealUserCode = desealUserCode;
    }

    public String getDesealUserName() {
        return this.desealUserName;
    }

    public void setDesealUserName(String desealUserName) {
        this.desealUserName = desealUserName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getSealCode() {
        return this.sealCode;
    }

    public List<String> getBatchCodes() {
        return this.batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public List<String> getSealCodes() {
        return this.sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
    }

    public List<String> getDesealCodes() {
        return this.desealCodes;
    }

    public void setDesealCodes(List<String> desealCodes) {
        this.desealCodes = desealCodes;
    }

    public String getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(String sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public String getDesealCarTime() {
        return desealCarTime;
    }

    public void setDesealCarTime(String desealCarTime) {
        this.desealCarTime = desealCarTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
