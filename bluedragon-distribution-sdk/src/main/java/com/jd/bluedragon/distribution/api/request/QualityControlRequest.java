package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dudong on 2014/12/1.
 */
public class QualityControlRequest implements Serializable{

    private static final long serialVersionUID = 3018456756046445552L;
    private Integer userID;            // 操作人ID
    private String userName;        // 操作人名称
    private String userERP;         // 操作人ERP
    private Integer distCenterID;      // 分拣中心ID
    private String distCenterName;  // 分拣中心名称
    private Date operateTime;       // 操作时间
    private Integer qcType;         // 传过来的qcValue类型
    private String qcValue;         // 1:包裹号|2:运单号|3:箱号|4:批次号|5:车次号
    /**
     * 异常一级原因ID
     */
    private Long abnormalReasonFirstId;

    /**
     * 异常一级原因名称
     */
    private String abnormalReasonFirstName;

    /**
     * 异常二级原因ID
     */
    private Long abnormalReasonSecondId;

    /**
     * 异常二级原因名称
     */
    private String abnormalReasonSecondName;

    /**
     * 异常三级原因ID
     */
    private Long abnormalReasonThirdId;

    /**
     * 异常三级原因名称
     */
    private String abnormalReasonThirdName;
    private Integer qcCode;         // 质控异常类型ID
    private String qcName;          // 质控异常名称
    private boolean isSortingReturn; //是否生成分拣退货数据
    private String trackContent;     // 发送全程跟踪内容
    private String waveBusinessId;//版次号，路由系统的字段
    private Integer qcVersionFlag;//对接质控版本，1是老质控，2是新质控，null默认为老质控

    private Integer inletFlag; // 入口标识

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserERP() {
        return userERP;
    }

    public void setUserERP(String userERP) {
        this.userERP = userERP;
    }

    public Integer getDistCenterID() {
        return distCenterID;
    }

    public void setDistCenterID(Integer distCenterID) {
        this.distCenterID = distCenterID;
    }

    public String getDistCenterName() {
        return distCenterName;
    }

    public void setDistCenterName(String distCenterName) {
        this.distCenterName = distCenterName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getQcType() {
        return qcType;
    }

    public void setQcType(Integer qcType) {
        this.qcType = qcType;
    }

    public String getQcValue() {
        return qcValue;
    }

    public void setQcValue(String qcValue) {
        this.qcValue = qcValue;
    }

    public Long getAbnormalReasonFirstId() {
        return abnormalReasonFirstId;
    }

    public void setAbnormalReasonFirstId(Long abnormalReasonFirstId) {
        this.abnormalReasonFirstId = abnormalReasonFirstId;
    }

    public String getAbnormalReasonFirstName() {
        return abnormalReasonFirstName;
    }

    public void setAbnormalReasonFirstName(String abnormalReasonFirstName) {
        this.abnormalReasonFirstName = abnormalReasonFirstName;
    }

    public Long getAbnormalReasonSecondId() {
        return abnormalReasonSecondId;
    }

    public void setAbnormalReasonSecondId(Long abnormalReasonSecondId) {
        this.abnormalReasonSecondId = abnormalReasonSecondId;
    }

    public String getAbnormalReasonSecondName() {
        return abnormalReasonSecondName;
    }

    public void setAbnormalReasonSecondName(String abnormalReasonSecondName) {
        this.abnormalReasonSecondName = abnormalReasonSecondName;
    }

    public Long getAbnormalReasonThirdId() {
        return abnormalReasonThirdId;
    }

    public void setAbnormalReasonThirdId(Long abnormalReasonThirdId) {
        this.abnormalReasonThirdId = abnormalReasonThirdId;
    }

    public String getAbnormalReasonThirdName() {
        return abnormalReasonThirdName;
    }

    public void setAbnormalReasonThirdName(String abnormalReasonThirdName) {
        this.abnormalReasonThirdName = abnormalReasonThirdName;
    }

    public Integer getQcCode() {
        return qcCode;
    }

    public void setQcCode(Integer qcCode) {
        this.qcCode = qcCode;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public boolean getIsSortingReturn() {
        return isSortingReturn;
    }

    public void setIsSortingReturn(boolean isSortingReturn) {
        this.isSortingReturn = isSortingReturn;
    }

    public String getTrackContent() {
        return trackContent;
    }

    public void setTrackContent(String trackContent) {
        this.trackContent = trackContent;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

    public Integer getQcVersionFlag() {
        return qcVersionFlag;
    }

    public void setQcVersionFlag(Integer qcVersionFlag) {
        this.qcVersionFlag = qcVersionFlag;
    }

    public Integer getInletFlag() {
        return inletFlag;
    }

    public void setInletFlag(Integer inletFlag) {
        this.inletFlag = inletFlag;
    }
}
