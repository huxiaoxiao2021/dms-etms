package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.jmq.common.exception.JMQException;

import java.util.List;

/**
 * @author jinjingcheng
 * @date 2020/3/12
 */
public interface StrandService {

    /**
     * 滞留上报原因
     *  查询默认
     * @return
     */
    InvokeResult<List<ConfigStrandReasonData>> queryReasonList();

    /**
     * 滞留上报原因
     *  默认 + 冷链
     * @return
     */
    InvokeResult<List<ConfigStrandReasonData>> queryAllReasonList();

    /**
     * 滞留上报提交
     * 
     * @param request
     * @return
     */
    InvokeResult<Boolean> report(StrandReportRequest request);
    
    /**
     * 滞留上报
     * @param request
     * @return
     * @throws JMQException
     */
    InvokeResult reportStrandDetail(StrandReportRequest request) throws JMQException;

    boolean hasSenddetail(StrandReportRequest request);

    void strandReportAndCancelDelivery(StrandReportRequest request) throws JMQException;

    InvokeResult<Boolean> sendStrandReportJmq(StrandReportRequest request) throws JMQException;
}
