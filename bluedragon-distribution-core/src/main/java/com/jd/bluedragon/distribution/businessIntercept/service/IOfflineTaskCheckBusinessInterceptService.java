package com.jd.bluedragon.distribution.businessIntercept.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;

/**
 * 离线任务处理，校验是否有拦截
 *
 * @author fanggang7
 * @time 2020-12-22 16:19:39 周二
 */
public interface IOfflineTaskCheckBusinessInterceptService {

    /**
     * 处理离线任务，校验是否有拦截
     * @param offlineLogRequest 离线请求
     * @return 处理结果
     */
    Response<Boolean> handleOfflineTask(OfflineLogRequest offlineLogRequest);

    /**
     * 发出离线分拣处理消息
     * @param offlineLogRequests 离线数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-23 15:39:43 周二
     */
    Response<Boolean> batchSendOfflineTaskMq(List<OfflineLogRequest> offlineLogRequests);

    /**
     * 发出离线分拣处理消息
     * @param offlineLogRequest 离线数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-23 15:39:43 周二
     */
    Response<Boolean> sendOfflineTaskMq(OfflineLogRequest offlineLogRequest);

    /**
     * 发出离线分拣处理消息
     * @param task 离线数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-23 15:39:43 周二
     */
    Response<Boolean> sendOfflineTaskMq(Task task);

}
