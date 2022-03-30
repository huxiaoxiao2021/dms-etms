package com.jd.bluedragon.core.base;

import com.jdl.express.weight.report.api.CommonDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoQuery;

/**
 * 抽检核对服务
 *
 * @author hujiping
 * @date 2021/12/3 6:46 下午
 */
public interface WeightReportCommonRuleManager {

    /**
     * 获取抽检核对数据
     * @see https://cf.jd.com/pages/viewpage.action?pageId=641321355
     *
     * @param reportInfoQuery
     * @return
     */
    CommonDTO<ReportInfoDTO> getReportInfo(ReportInfoQuery reportInfoQuery);
}
