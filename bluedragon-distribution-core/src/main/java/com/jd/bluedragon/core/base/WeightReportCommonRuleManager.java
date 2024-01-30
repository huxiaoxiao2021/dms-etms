package com.jd.bluedragon.core.base;

import com.jdl.express.weight.report.api.CommonDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoQuery;
import com.jdl.express.weight.report.api.rule.queries.terminal.StandardResultAndDutyBodyDTO;
import com.jdl.express.weight.report.api.rule.queries.terminal.StandardResultAndDutyBodyQuery;

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
     * 称重再造系统提供的三合一规则接口
     * https://joyspace.jd.com/pages/iQyOl8mkdXhcUKSLB1ZB
     */
    CommonDTO<ReportInfoDTO> getReportInfo(ReportInfoQuery reportInfoQuery);

    /**
     * 获取超标判断结果和责任主体
     * 判断是否超标有两种方式：
     *    方法一是直接调用三合一规则接口，既可以拿到核对重量和核对体积，还可以拿到超标结果。
     *    方法二是分步调用2个子接口，先调用第1个子接口获取核对重量和核对体积，再调用第2个子接口拿着实测重量、体积以及第1个子接口返回的核对重量、体积这4个入参判断是否超标
     * 此接口是方法二中的第2个子接口：<a href="https://joyspace.jd.com/pages/cB48i4tXnY7NFLubKBT5">...</a>
     *    方法二中的第1个子接口：<a href="https://joyspace.jd.com/pages/kc06MrvczwQtitP9rAoG">...</a>
     */
    CommonDTO<StandardResultAndDutyBodyDTO> getOverStandardResultAndDutyBody(StandardResultAndDutyBodyQuery queryDto);

}
