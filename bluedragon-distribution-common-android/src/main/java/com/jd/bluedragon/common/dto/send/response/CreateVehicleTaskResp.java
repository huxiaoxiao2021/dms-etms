package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class CreateVehicleTaskResp implements Serializable {
    private static final long serialVersionUID = 9046473864156990011L;
    /**
     * 业务主键(主任务)
     */
    private String bizId;
    /**
     * 自建类型任务编号（页面展示自建 1、自建2...）
     */
    private String bizNo;


}
