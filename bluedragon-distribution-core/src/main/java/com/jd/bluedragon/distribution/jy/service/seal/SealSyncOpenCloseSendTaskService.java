package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;

/**
 * @Author zhengchengfa
 * @Date 2023/11/13 11:23
 * @Description
 */
public interface SealSyncOpenCloseSendTaskService {
    /**
     * 封车同步关闭发货岗未封车任务
     * @param param
     * @return
     */
    boolean dealSeal(SealSyncOpenCloseSendTaskDto param);

    /**
     * 取消封车同步打开发货岗已封车任务
     * @param param
     * @return
     */
    boolean dealCancelSeal(SealSyncOpenCloseSendTaskDto param);
}
