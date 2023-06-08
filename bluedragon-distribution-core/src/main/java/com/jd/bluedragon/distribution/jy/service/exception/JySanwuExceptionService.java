package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpSignUserResp;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskStatisticsOfWaitReceiveDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 21:25
 * @Description: 三无
 */
public interface JySanwuExceptionService {

    /**
     * 获取三无超时未领取统计列表
     * @param req
     * @return
     */
    JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> getExpTaskStatisticsOfWaitReceiveByPage(ExpTaskStatisticsReq req);

    /**
     * 获取超时未领取任务列表
     * @param req
     * @return
     */
    JdCResponse<List<ExpTaskDto>> getWaitReceiveSanwuExpTaskByPage(ExpTaskStatisticsDetailReq req);

    /**
     * 获取异常岗签到用户
     * @param req
     * @return
     */
    JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req);

    /**
     * 指派任务
     * @param req
     * @return
     */
    JdCResponse<Boolean> assignExpTask(ExpTaskAssignRequest req);


    /**
     * 获取指派任务数
     * @param req
     * @return
     */
    JdCResponse<Integer> getAssignExpTaskCount(ExpBaseReq req);
}
