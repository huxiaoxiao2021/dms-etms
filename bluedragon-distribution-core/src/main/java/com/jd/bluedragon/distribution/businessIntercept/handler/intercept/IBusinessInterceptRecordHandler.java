package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;

/**
 * 拦截记录处理接口
 *
 * @author fanggang7
 * @since 2020-12-23 09:34:12 周三
 **/
public interface IBusinessInterceptRecordHandler {

    /**
     * 处理拦截提交数据
     * @param msgDto 提交数据
     * @return 处理结果
     * @author fanggang7
     * @time 2020-12-23 09:36:46 周三
     */
    Response<Boolean> handle(SaveInterceptMsgDto msgDto);

}
