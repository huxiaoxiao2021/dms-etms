package com.jd.bluedragon.distribution.log.impl;

import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.logger.external.LogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;

public class LogEngineImpl implements LogEngine {

    LogWriter logWriter;

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    @Override
    public void addLog(BusinessLogProfiler log) {
        logWriter.addLog(log);
    }
}
