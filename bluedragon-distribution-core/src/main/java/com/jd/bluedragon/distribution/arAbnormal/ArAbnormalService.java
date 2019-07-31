package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.jmq.common.exception.JMQException;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
public interface ArAbnormalService {
    /**
     * PDA请求
     * @param arAbnormalRequest
     * @return
     */
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest);
    /**
     * 提报异常逻辑
     * @param arAbnormalRequest
     */
    public void dealArAbnormal(ArAbnormalRequest arAbnormalRequest) throws JMQException;
}
