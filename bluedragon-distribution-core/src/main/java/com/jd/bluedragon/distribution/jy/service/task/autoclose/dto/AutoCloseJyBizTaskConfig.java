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

    private Integer waitUnloadNotFinish;

    private Integer unloadingNotFinish;

    public Integer getWaitUnloadNotFinish() {
        return waitUnloadNotFinish;
    }

    public void setWaitUnloadNotFinish(Integer waitUnloadNotFinish) {
        this.waitUnloadNotFinish = waitUnloadNotFinish;
    }

    public Integer getUnloadingNotFinish() {
        return unloadingNotFinish;
    }

    public void setUnloadingNotFinish(Integer unloadingNotFinish) {
        this.unloadingNotFinish = unloadingNotFinish;
    }
}
