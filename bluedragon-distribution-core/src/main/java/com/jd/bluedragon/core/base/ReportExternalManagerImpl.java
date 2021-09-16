package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WaitSpotCheckQueryCondition;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 报表服务实现类
 *
 * @author hujiping
 * @date 2021/9/18 5:55 下午
 */
@Service("reportExternalManager")
public class ReportExternalManagerImpl implements ReportExternalManager {

    private static Logger logger = LoggerFactory.getLogger(ReportExternalManagerImpl.class);

    @Autowired
    private ReportExternalService reportExternalService;

    @Override
    public boolean checkIsNeedSpotCheck(WaitSpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.checkIsNeedSpotCheck",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<Boolean> baseEntity = reportExternalService.checkIsNeedSpotCheck(condition);
            if(baseEntity != null && baseEntity.getData() != null){
                return baseEntity.getData();
            }
        }catch (Exception e){
            logger.error("根据条件:{}校验是否需要抽检异常!", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return false;
    }
}
