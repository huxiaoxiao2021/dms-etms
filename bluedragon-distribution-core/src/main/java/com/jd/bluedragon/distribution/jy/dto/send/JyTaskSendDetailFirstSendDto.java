package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;

import java.io.Serializable;

/**
 * 流向纬度发货首次扫描消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-14 18:01:43 周一
 */
public class JyTaskSendDetailFirstSendDto implements Serializable {
    private static final long serialVersionUID = 4280118124368496255L;

    /**
     * 主ID
     */
    private String bizId;

    /**
     * 明细ID
     */
    private String sendVehicleDetailBizId;

    /**
     * 自建标识
     */
    private Integer manualCreate;

    /**
     * 操作人信息
     */
    private OperateUser operateUser;

    /**
     * 绑定的小组
     */
    private String groupCode;

    private OperatorData operatorData;

    public JyTaskSendDetailFirstSendDto() {
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }

    public Integer getManualCreate() {
        return manualCreate;
    }

    public void setManualCreate(Integer manualCreate) {
        this.manualCreate = manualCreate;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }
}
