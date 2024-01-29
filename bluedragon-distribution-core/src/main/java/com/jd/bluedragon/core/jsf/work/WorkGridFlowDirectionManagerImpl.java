package com.jd.bluedragon.core.jsf.work;

import com.jdl.basic.api.domain.workStation.WorkGridFlowDirection;
import com.jdl.basic.api.domain.workStation.WorkGridFlowDirectionQuery;
import com.jdl.basic.api.service.workStation.WorkGridFlowDirectionJsfService;
import com.jdl.basic.common.utils.JsonHelper;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("workGridFlowDirectionManager")
public class WorkGridFlowDirectionManagerImpl implements WorkGridFlowDirectionManager{

    @Autowired
    private WorkGridFlowDirectionJsfService workGridFlowDirectionJsfService;

    @Override
    public Result<List<WorkGridFlowDirection>> queryFlowByCondition(WorkGridFlowDirectionQuery query) {
        try {
            return workGridFlowDirectionJsfService.queryFlowByPositionCode(query);
        }catch (Exception e) {
            log.error("获取网格流向信息异常：{}", JsonHelper.toJSONString(query));
            return null;
        }
    }
}
