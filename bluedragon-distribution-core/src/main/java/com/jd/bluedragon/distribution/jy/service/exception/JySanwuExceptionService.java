package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpSignUserReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskStatisticsReq;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpSignUserResp;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskOfWaitReceiveDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 21:25
 * @Description: 三无
 */
public interface JySanwuExceptionService {

    /**
     * 获取三无超时未领取任务列表
     * @param req
     * @return
     */
    JdCResponse<List<ExpTaskOfWaitReceiveDto>> getWaitReceiveSanwuExceptionByPage(ExpTaskStatisticsReq req);

    /**
     * 获取异常岗签到用户
     * @param req
     * @return
     */
    JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req);

}
