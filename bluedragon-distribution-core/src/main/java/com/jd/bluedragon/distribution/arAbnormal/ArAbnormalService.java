package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReason;
import com.jd.jmq.common.exception.JMQException;

import java.util.List;

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
    /**
     * 查询运输方式变更的原因
     * @param 
     */
    public List<ArContrabandReason> getArContrabandReasonList();
}
