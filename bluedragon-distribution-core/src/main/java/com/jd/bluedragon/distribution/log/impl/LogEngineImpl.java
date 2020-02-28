package com.jd.bluedragon.distribution.log.impl;

import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.logger.external.LogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;

/**
 * 此接口保存的日志会发送到jdq
 * 然后再由 Spark Streaming 保存到business.log.jd.com 里； http://bdp.jd.com/jrdw/jrctask/sparkNew/list.html
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
     *      如何设置请参考com.jd.bluedragon.distribution.log.BizOperateTypeConstants#BizTypeCode
     * com.jd.dms.logger.external.BusinessLogProfiler#operateType：
     *      如何设置请参考 com.jd.bluedragon.distribution.log.BizOperateTypeConstants#OperateTypeCode
     *      使用前请在 business.log.jd.com 中添加对应的operateType值。理论上没一条写日志的地方operateType值都不一样；这样方便查找
     * @param log
     */
    @Override
    public void addLog(BusinessLogProfiler log) {
        logWriter.addLog(log);
    }
}
