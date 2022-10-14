package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class TransferSendTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 360399368478325772L;
    /**
     * 迁移前的主任务编号
     */
    private String fromSendVehicleBizId;
    /**
     * 迁移前的子任务编号
     */
    private String fromSendVehicleDetailBizId;

    /**
     * 迁移后的主任务编号
     */
    private String toSendVehicleBizId;
    /**
     * 迁移后的子任务编号
     */
    private String toSendVehicleDetailBizId;

    /**
     * 迁移前后是否同流向
     */
    private Boolean sameWayFlag;

    /**
     * 是否全部批次迁移 true 是 false 否
     */
    private boolean totalTransFlag;
    /**
     * 部分迁移批次列表
     */
    private List<String> sendCodeList;

    public String getFromSendVehicleBizId() {
        return fromSendVehicleBizId;
    }

    public void setFromSendVehicleBizId(String fromSendVehicleBizId) {
        this.fromSendVehicleBizId = fromSendVehicleBizId;
    }

    public String getFromSendVehicleDetailBizId() {
        return fromSendVehicleDetailBizId;
    }

    public void setFromSendVehicleDetailBizId(String fromSendVehicleDetailBizId) {
        this.fromSendVehicleDetailBizId = fromSendVehicleDetailBizId;
    }

    public String getToSendVehicleBizId() {
        return toSendVehicleBizId;
    }

    public void setToSendVehicleBizId(String toSendVehicleBizId) {
        this.toSendVehicleBizId = toSendVehicleBizId;
    }

    public String getToSendVehicleDetailBizId() {
        return toSendVehicleDetailBizId;
    }

    public void setToSendVehicleDetailBizId(String toSendVehicleDetailBizId) {
        this.toSendVehicleDetailBizId = toSendVehicleDetailBizId;
    }

    public Boolean getSameWayFlag() {
        return sameWayFlag;
    }

    public void setSameWayFlag(Boolean sameWayFlag) {
        this.sameWayFlag = sameWayFlag;
    }

    public boolean getTotalTransFlag() {
        return totalTransFlag;
    }

    public void setTotalTransFlag(boolean totalTransFlag) {
        this.totalTransFlag = totalTransFlag;
    }

    public List<String> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<String> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }
}
