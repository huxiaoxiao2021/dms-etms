package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy.JYBizTaskCloseServiceStrategy;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务自动关闭服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
@Service("jyBizTaskAutoCloseService")
@Slf4j
public class JyBizTaskAutoCloseServiceImpl implements JyBizTaskAutoCloseService {

    @Autowired
    private JYBizTaskCloseServiceStrategy jyBizTaskCloseServiceStrategy;

    /**
     * 自动关闭任务
     *
     * @param task 任务数据
     * @return 处理结果
     */
    @Override
    public boolean handleTimingCloseTask(Task task) {
        AutoCloseTaskPo autoCloseTaskPo = JsonHelper.fromJson(task.getBody(), AutoCloseTaskPo.class);
        if(autoCloseTaskPo == null){
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskBody is null");
            return false;
        }
        if (StringUtils.isBlank(autoCloseTaskPo.getBizId())) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail bizId is null");
            return false;
        }
        if (autoCloseTaskPo.getTaskBusinessType() == null) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskType is null");
            return false;
        }
        final Result<Void> result = jyBizTaskCloseServiceStrategy.closeTask(autoCloseTaskPo);
        return result.isSuccess();
    }
}
