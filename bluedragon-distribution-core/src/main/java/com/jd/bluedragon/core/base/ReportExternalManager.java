package com.jd.bluedragon.core.base;

import com.jd.ql.dms.report.domain.WaitSpotCheckQueryCondition;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/9/18 5:54 下午
 */
public interface ReportExternalManager {

    /**
     * 校验是否需要抽检
     *
     * @param condition
     * @return
     */
    boolean checkIsNeedSpotCheck(WaitSpotCheckQueryCondition condition);
}
