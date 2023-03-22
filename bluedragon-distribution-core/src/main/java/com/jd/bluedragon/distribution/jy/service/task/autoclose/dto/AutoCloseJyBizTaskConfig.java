package com.jd.bluedragon.distribution.jy.service.task.autoclose.dto;

import java.io.Serializable;

/**
 * 自动关闭作业工作台作业任务配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-21 16:40:54 周二
 */
public class AutoCloseJyBizTaskConfig implements Serializable {
    private static final long serialVersionUID = -1336367837141577207L;

    /**
     * 延迟时间，单位分钟
     */
    private Integer waitUnloadNotFinishLazyTime;

    /**
     * 延迟时间，单位分钟
     */
    private Integer unloadingNotFinishLazyTime;

    public Integer getWaitUnloadNotFinishLazyTime() {
        return waitUnloadNotFinishLazyTime;
    }

    public void setWaitUnloadNotFinishLazyTime(Integer waitUnloadNotFinishLazyTime) {
        this.waitUnloadNotFinishLazyTime = waitUnloadNotFinishLazyTime;
    }

    public Integer getUnloadingNotFinishLazyTime() {
        return unloadingNotFinishLazyTime;
    }

    public void setUnloadingNotFinishLazyTime(Integer unloadingNotFinishLazyTime) {
        this.unloadingNotFinishLazyTime = unloadingNotFinishLazyTime;
    }
}
