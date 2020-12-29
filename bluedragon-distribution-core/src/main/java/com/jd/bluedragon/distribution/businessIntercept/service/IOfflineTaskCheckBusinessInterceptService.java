package com.jd.bluedragon.distribution.businessIntercept.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;

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
}
