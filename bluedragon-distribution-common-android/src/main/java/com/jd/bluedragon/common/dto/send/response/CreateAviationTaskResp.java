package com.jd.bluedragon.common.dto.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendTaskDto;

import java.io.Serializable;

public class CreateAviationTaskResp implements Serializable {
    private static final long serialVersionUID = 9046473864156990011L;

    public static final Integer EXIST_SAME_DESTINATION_TASK_CODE = 200001;
    public static final String EXIST_SAME_DESTINATION_TASK_MSG = "存在同流向任务";


    /**
     * 业务主键(主任务)
     */
    private String bizId;
    private String detailBizId;

    /**
     * 同流向已存在的发货任务
     */
    private AviationSendTaskDto aviationSendTaskDto;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public AviationSendTaskDto getAviationSendTaskDto() {
        return aviationSendTaskDto;
    }

    public void setAviationSendTaskDto(AviationSendTaskDto aviationSendTaskDto) {
        this.aviationSendTaskDto = aviationSendTaskDto;
    }
}
