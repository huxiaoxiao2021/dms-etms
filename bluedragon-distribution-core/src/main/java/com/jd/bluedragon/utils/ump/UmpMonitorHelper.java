package com.jd.bluedragon.utils.ump;

import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

public class UmpMonitorHelper {

    /**
     * 执行业务处理，自动添加ump监控
     *
     * @param umpKey umpKey
     */
    public static void doWithUmpMonitor(String umpKey, String appName, UmpMonitorHandler handler) {
        CallerInfo callerInfo = null;
        try {
            callerInfo = Profiler.registerInfo(umpKey, appName, false, true);
            handler.process();
        }
        catch (Exception e) {
            Profiler.functionError(callerInfo);
            throw new RuntimeException(e);
        }
        finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
}
