package com.jd.bluedragon.distribution.batchForward.service;

import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * Created by hujiping on 2018/8/1.
 */
public interface BatchForwardService {

    /**
     * 整批转发
     * */
    SendResult batchSend(BatchForwardRequest request);

    boolean dealBatchForwardTask(Task task);
}
