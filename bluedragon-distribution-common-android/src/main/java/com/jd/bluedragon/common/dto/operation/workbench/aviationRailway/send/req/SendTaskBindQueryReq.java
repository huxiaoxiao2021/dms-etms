package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 18:17
 * @Description
 */
public class SendTaskBindQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;

    private String detailBizId;

    private String vehicleNumber;

    /**
     * 绑定任务业务场景分类
     * com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum
     */
    private Integer type;

    /**
     * 摆渡发车任务查询来源
     * ShuttleQuerySourceEnum
     */
    private Integer shuttleQuerySource;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getShuttleQuerySource() {
        return shuttleQuerySource;
    }

    public void setShuttleQuerySource(Integer shuttleQuerySource) {
        this.shuttleQuerySource = shuttleQuerySource;
    }
}
