package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsTaskService {

    /**
     * 通过该接口把本地数据上传到服务端
     *
     * @param request
     * @return
     */
    TaskResponse add(TaskRequest request);

}
