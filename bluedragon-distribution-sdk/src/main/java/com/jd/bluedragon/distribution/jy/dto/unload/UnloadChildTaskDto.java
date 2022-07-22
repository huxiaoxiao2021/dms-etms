package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class UnloadChildTaskDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;

    private String bizId;
    /**
     * 父任务BizID
     */
    private String unloadVehicleBizId;
    /**
     * 子任务类型： 1 补扫任务 2...
      */
    private Integer type;
    /**
     * 子任务状态： 1 进行中 2完
     */
    private Integer status;
    /**
     * 子任务开始时间
     */
    private Date startTime;
    /**
     * 子任务结束时间
     */
    private Date endTime;

    private String createUserErp;
    private String createUserName;
    private String updateUserErp;
    private String updateUserName;
    private Date createTime;
    private Date updateTime;



}
