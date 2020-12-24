package com.jd.bluedragon.distribution.businessIntercept.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;

/**
 * 分拣拦截报表明细提交接口
 *
 * @author fanggang7
 * @time 2020-12-11 17:35:34 周五
 */
public interface IBusinessInterceptDetailReportService {

    /**
     * 发送拦截消息
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:38:50 周五
     */
    Response<Boolean> sendInterceptMsg(SaveInterceptMsgDto msgDto);

    /**
     * 发送拦截后处理消息
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:39:15 周五
     */
    Response<Boolean> sendDisposeAfterInterceptMsg(SaveDisposeAfterInterceptMsgDto msgDto);
}
