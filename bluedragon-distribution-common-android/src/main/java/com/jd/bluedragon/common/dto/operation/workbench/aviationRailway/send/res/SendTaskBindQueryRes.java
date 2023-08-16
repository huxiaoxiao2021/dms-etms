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

    private List<SendTaskBindQueryDto> sendTaskBindQueryDtoList;


    public List<SendTaskBindQueryDto> getSendTaskBindDtoList() {
        return sendTaskBindQueryDtoList;
    }

    public void setSendTaskBindDtoList(List<SendTaskBindQueryDto> sendTaskBindQueryDtoList) {
        this.sendTaskBindQueryDtoList = sendTaskBindQueryDtoList;
    }
}
