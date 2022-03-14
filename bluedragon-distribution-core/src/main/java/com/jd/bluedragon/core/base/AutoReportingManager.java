package com.jd.bluedragon.core.base;

import com.jd.wlai.center.service.outter.domian.AutoReportingRequest;
import com.jd.wlai.center.service.outter.domian.AutoReportingResponse;

/**
 * AI识别
 *
 * @author hujiping
 * @date 2021/11/29 3:53 下午
 */
public interface AutoReportingManager {

    /**
     * AI图片批量识别
     *
     * @param autoReportingRequest
     * @return
     */
    AutoReportingResponse reportingCheck(AutoReportingRequest autoReportingRequest);
}
