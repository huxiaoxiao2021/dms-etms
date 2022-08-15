package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 任务列表
 */
public class ExpTaskDto implements Serializable {
    //任务id
    private String taskId;

    //提交条码
    private String barCode;

    //停留时间 hh:mm
    private String stayTime;

    //楼层
    private String floor;

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //提报人姓名
    private String reporterName;

    //标签列表
    private String tags;

    //是否保存过
    private String saved;
}
