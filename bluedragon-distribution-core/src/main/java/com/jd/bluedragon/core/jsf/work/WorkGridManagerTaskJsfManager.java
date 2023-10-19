package com.jd.bluedragon.core.jsf.work;

import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.common.utils.Result;

import java.util.List;

/**
 * 
 * @author wuyoude
 *
 */
public interface WorkGridManagerTaskJsfManager {

    /**
     * 根据taskCode查询
     * @param taskCode
     * @return
     */
    Result<WorkGridManagerTask> queryByTaskCode(String taskCode);

    Result<List<WorkGridManagerTask>> queryByBizType(Integer taskBizType);
}
