package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jd.wlai.center.service.outter.domian.AutoReportingRequest;
import com.jd.wlai.center.service.outter.domian.AutoReportingResponse;
import com.jd.wlai.center.service.outter.service.IAutoReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * AI图片识别
 *
 * @author hujiping
 * @date 2021/11/29 3:54 下午
 */
@Service("autoReportingManager")
public class AutoReportingManagerImpl implements AutoReportingManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("autoReportingService")
    private IAutoReportingService autoReportingService;

    @Override
    public AutoReportingResponse reportingCheck(AutoReportingRequest autoReportingRequest){
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.AutoReportingManager.reportingCheck",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return autoReportingService.reportingCheck(autoReportingRequest);
        }catch (Exception e){
            logger.error("根据条件:{}提交AI识别异常!", JsonHelper.toJson(autoReportingRequest), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
