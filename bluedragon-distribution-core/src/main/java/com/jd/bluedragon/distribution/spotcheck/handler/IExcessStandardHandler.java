package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessRequest;
import com.jd.bluedragon.distribution.spotcheck.domain.CheckExcessResult;

/**
 * 抽检标准处理器
 *
 * @author hujiping
 * @date 2021/8/23 2:49 下午
 */
public interface IExcessStandardHandler {

    /**
     * 校验是否超标
     * @param checkExcessRequest
     * @return
     */
    InvokeResult<CheckExcessResult> checkIsExcess(CheckExcessRequest checkExcessRequest);
}
