package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.express.weight.report.api.CommonDTO;
import com.jdl.express.weight.report.api.rule.WeightReportCommonRuleApi;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 抽检核对服务
 *
 * @author hujiping
 * @date 2021/12/3 6:46 下午
 */
@Service("weightReportCommonRuleManager")
public class WeightReportCommonRuleManagerImpl implements WeightReportCommonRuleManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WeightReportCommonRuleApi weightReportCommonRuleApi;

    @Override
    public CommonDTO<ReportInfoDTO> getReportInfo(ReportInfoQuery reportInfoQuery){
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.WeightReportCommonRuleManager.getReportInfo",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return weightReportCommonRuleApi.getReportInfo(reportInfoQuery);
        }catch (Exception e){
            logger.error("根据条件:{}获取核对数据异常!", JsonHelper.toJson(reportInfoQuery), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
