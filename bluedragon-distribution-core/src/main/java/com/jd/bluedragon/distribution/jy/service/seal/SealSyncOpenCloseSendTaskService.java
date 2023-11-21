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
     *
     * 按批次取消封车时，同流向多批次场景（绑定或迁移产生），部分批次取消时，无法处理该场景
     * 目前按TW号处理，只有TW任务取消后，才会回退任务状态
     * @param param
     * @return
     */
    @Deprecated
    boolean dealCancelSeal(SealSyncOpenCloseSendTaskDto param);
}
