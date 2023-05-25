package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 21:40
 * @Description: 待领取异常任务
 */
public class ExpTaskOfWaitReceiveDto implements Serializable {

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //待取任务数
    private Integer count;

    //
    private List<ExpTaskDto> taskDtos;
}
