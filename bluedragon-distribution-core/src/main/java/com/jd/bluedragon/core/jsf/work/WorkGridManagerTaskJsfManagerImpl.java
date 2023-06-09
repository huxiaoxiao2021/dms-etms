
package com.jd.bluedragon.core.jsf.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.api.service.work.WorkGridManagerTaskJsfService;
import com.jdl.basic.common.utils.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wuyoude
 *
 */
@Slf4j
@Service("workGridManagerTaskJsfManager")
public class WorkGridManagerTaskJsfManagerImpl implements WorkGridManagerTaskJsfManager {

    @Autowired
    private WorkGridManagerTaskJsfService workGridManagerTaskJsfService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridManagerTaskJsfManagerImpl.queryByTaskCode",mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public Result<WorkGridManagerTask> queryByTaskCode(String taskCode) {
        Result<WorkGridManagerTask> result = new Result<>();
        result.toFail("查询任务失败");
        try {
        	result.toSuccess("查询任务成功！");
            return workGridManagerTaskJsfService.queryByTaskCode(taskCode);
        } catch (Exception e) {
            log.error("queryByTaskCode-error! {}",  e.getMessage(),e);
            result.toFail("查询任务异常!");
        }
        return result;
	}
}
