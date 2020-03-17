package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.jmq.common.exception.JMQException;

/**
 * @author jinjingcheng
 * @date 2020/3/12
 */
public interface StrandService {
    /**
     * 滞留上报
     * @param request
     * @return
     * @throws JMQException
     */
    InvokeResult reportStrandDetail(StrandReportRequest request) throws JMQException;
}
