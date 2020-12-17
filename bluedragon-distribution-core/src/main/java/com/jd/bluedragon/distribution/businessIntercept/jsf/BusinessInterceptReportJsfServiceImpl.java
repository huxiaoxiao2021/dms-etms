package com.jd.bluedragon.distribution.businessIntercept.jsf;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportJsfService;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分拣拦截报表服务接口
 *
 * @author fanggang7
 * @time 2020-12-16 21:34:40 周三
 */
@Service
public class BusinessInterceptReportJsfServiceImpl implements IBusinessInterceptReportJsfService {

    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    /**
     * 发送拦截消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:38:50 周五
     */
    @Override
    public Response<Boolean> sendInterceptMsg(SaveInterceptMsgDto msgDto) {
        return businessInterceptReportService.sendInterceptMsg(msgDto);
    }

    /**
     * 发送拦截后处理消息
     *
     * @param msgDto 消息数据
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-11 17:39:15 周五
     */
    @Override
    public Response<Boolean> sendDisposeAfterInterceptMsg(SaveDisposeAfterInterceptMsgDto msgDto) {
        return businessInterceptReportService.sendDisposeAfterInterceptMsg(msgDto);
    }
}
