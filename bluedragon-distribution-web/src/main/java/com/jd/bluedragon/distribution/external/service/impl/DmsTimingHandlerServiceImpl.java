package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.external.service.DmsTimingHandlerService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 定时任务接口实现（由外部触发）
 *
 * @author hujiping
 * @date 2023/3/9 5:32 PM
 */
@Service("dmsTimingHandlerService")
public class DmsTimingHandlerServiceImpl implements DmsTimingHandlerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    public void timingHandlerOverTimeException() {
        if(logger.isInfoEnabled()){
            logger.info("查询超时的报废任务详情并通知场地负责人的定时任务开始执行:{}", System.currentTimeMillis());
        }
        // 查询超时的生鲜报废任务并通知场地负责人
        jyExceptionService.queryOverTimeExceptionAndNotice();
    }

    @Override
    public void timingHandlerFreshScrapNotice() {
        if(logger.isInfoEnabled()){
            logger.info("已领取报废任务详情通知领取人的定时任务开始执行:{}", System.currentTimeMillis());
        }
        // 查询已领取的生鲜报废任务明细并通知领取人
        jyExceptionService.queryFreshScrapDetailAndNotice();
    }
}
