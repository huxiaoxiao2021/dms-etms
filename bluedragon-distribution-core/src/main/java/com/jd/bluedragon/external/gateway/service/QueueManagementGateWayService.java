package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.queueManagement.response.PdaPlatformInfoDto;
import com.jd.bluedragon.common.dto.queueManagement.request.PdaPlatformReq;
import com.jd.bluedragon.common.dto.queueManagement.response.PlatformQueueTaskDto;
import com.jd.bluedragon.common.dto.queueManagement.request.PlatformCallNumReq;
import com.jd.bluedragon.common.dto.queueManagement.response.PlatformCallNumDto;
import com.jd.bluedragon.common.dto.queueManagement.request.PlatformWorkReq;


import java.util.List;

public interface QueueManagementGateWayService {

    /**
     * 获取月台、流向、车型信息
     * @param request
     * @return
     */
    JdCResponse<List<PdaPlatformInfoDto>> getPlatformInfoList(PdaPlatformReq request);

    /**
     * 获取排队任务信息列表
     * @param request
     * @return
     */
    JdCResponse<List<PlatformQueueTaskDto>> getPlatformQueueTaskList(PdaPlatformReq request);

    /**
     * 校验月台是否空闲
     * @param request
     * @return
     */
    JdCResponse<Boolean> isCoccupyPlatform(PlatformCallNumReq request);

    /**
     * 叫号业务
     * @param request
     * @return
     */
    JdCResponse<PlatformCallNumDto> callNum(PlatformCallNumReq request);

    /**
     * 作业状态修改
     * @param request
     * @return
     */
    JdCResponse<Boolean> platformWorkFeedback(PlatformWorkReq request);







}
