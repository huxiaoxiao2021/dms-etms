package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/3 10:53
 * @Description
 */
public class SendTaskUnbindReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 被绑定任务bizId
     */
    private String bizId;
    private String detailBizId;
    /**
     * 需解绑的任务信息
     */
    private String unbindBizId;
    private String unbindDetailBizId;
    /**
     * 绑定业务类型（服务端默认）
     * TaskBindTypeEnum
     */
    private Integer type;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getUnbindBizId() {
        return unbindBizId;
    }

    public void setUnbindBizId(String unbindBizId) {
        this.unbindBizId = unbindBizId;
    }

    public String getUnbindDetailBizId() {
        return unbindDetailBizId;
    }

    public void setUnbindDetailBizId(String unbindDetailBizId) {
        this.unbindDetailBizId = unbindDetailBizId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }
}
