package com.jd.bluedragon.distribution.log.impl;

import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.logger.external.LogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;

/**
 * 此接口保存的日志会发送到jdq
 * 然后再由 Spark Streaming 保存到business.log.jd.com 里； Spark Streaming 任务链接http://bdp.jd.com/jrdw/jrctask/sparkNew/list.html
 */
public class LogEngineImpl implements LogEngine {

    LogWriter logWriter;

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * BusinessLogProfiler 的属性bizType和operateType属性如何封装 使用前请看：
     * com.jd.dms.logger.external.BusinessLogProfiler#bizType：
     *      如何设置请参考com.jd.bluedragon.utils.log.BusinessLogConstans.BizTypeEnum
     * com.jd.dms.logger.external.BusinessLogProfiler#operateType：
     *      如何设置请参考 com.jd.bluedragon.utils.log.BusinessLogConstans.OperateTypeEnum
     * @param log
     */
    @Override
    public void addLog(BusinessLogProfiler log) {
        logWriter.addLog(log);
    }
}
