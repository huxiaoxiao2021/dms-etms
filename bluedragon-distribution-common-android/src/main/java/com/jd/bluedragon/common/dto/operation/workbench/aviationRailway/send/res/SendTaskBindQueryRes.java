package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 18:17
 * @Description
 */
public class SendTaskBindQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;
    /**
     * 绑定任务数量
     */
    private Integer bindTaskNum;

    private List<SendTaskBindQueryDto> sendTaskBindQueryDtoList;

    public Integer getBindTaskNum() {
        return bindTaskNum;
    }

    public void setBindTaskNum(Integer bindTaskNum) {
        this.bindTaskNum = bindTaskNum;
    }

    public List<SendTaskBindQueryDto> getSendTaskBindQueryDtoList() {
        return sendTaskBindQueryDtoList;
    }

    public void setSendTaskBindQueryDtoList(List<SendTaskBindQueryDto> sendTaskBindQueryDtoList) {
        this.sendTaskBindQueryDtoList = sendTaskBindQueryDtoList;
    }
}
