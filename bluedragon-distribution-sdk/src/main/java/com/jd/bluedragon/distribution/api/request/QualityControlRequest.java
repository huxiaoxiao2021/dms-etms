package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

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
    private Integer qcCode;         // 质控异常类型ID
    private String qcName;          // 质控异常名称
    private boolean isSortingReturn; //是否生成分拣退货数据
    private String trackContent;     // 发送全程跟踪内容
    private String waveBusinessId;//版次号，路由系统的字段
    private Integer qcVersionFlag;//对接质控版本，1是老质控，2是新质控，null默认为老质控

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
}
