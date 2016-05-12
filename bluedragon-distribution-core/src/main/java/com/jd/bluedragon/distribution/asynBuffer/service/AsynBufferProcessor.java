package com.jd.bluedragon.distribution.asynBuffer.service;

import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/5/12
 */
public interface AsynBufferProcessor {
    Boolean doTaskProcess(Task task) throws Exception;
}
