
package com.jd.bluedragon.core.jsf.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.api.service.work.WorkGridManagerTaskConfigJsfService;
import com.jdl.basic.common.utils.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author wuyoude
 *
 */
@Slf4j
@Service("workGridManagerTaskConfigJsfManager")
public class WorkGridManagerTaskConfigJsfManagerImpl implements WorkGridManagerTaskConfigJsfManager {

    @Autowired
    private WorkGridManagerTaskConfigJsfService workGridManagerTaskConfigJsfService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridManagerTaskConfigJsfManagerImpl.queryByTaskCode",mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public Result<WorkGridManagerTaskConfigVo> queryByTaskConfigCode(String taskConfigCode) {
        Result<WorkGridManagerTaskConfigVo> result = new Result<>();
        result.toFail("查询任务失败");
        try {
        	result.toSuccess("查询任务配置成功！");
            return workGridManagerTaskConfigJsfService.queryByTaskConfigCode(taskConfigCode);
        } catch (Exception e) {
            log.error("queryByTaskCode-error! {}",  e.getMessage(),e);
            result.toFail("查询任务异常!");
        }
        return result;
	}
}
