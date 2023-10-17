package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/3 10:53
 * @Description
 */
public class SendTaskBindReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 被绑定任务bizId
     */
    private String bizId;
    private String detailBizId;
    /**
     * 需绑定或解绑任务bizId
     */
    private List<SendTaskBindDto> sendTaskBindDtoList;
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

    public List<SendTaskBindDto> getSendTaskBindDtoList() {
        return sendTaskBindDtoList;
    }

    public void setSendTaskBindDtoList(List<SendTaskBindDto> sendTaskBindDtoList) {
        this.sendTaskBindDtoList = sendTaskBindDtoList;
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
