package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 任务关闭抽象处理类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 20:13:12 周二
 */
@Slf4j
@Service
public class JYBizTaskCloseAbstractService implements JYBizTaskCloseService {

    protected String sysOperateUser = "sys";
    protected String sysOperateUserName = "sys";

    /**
     * 关闭任务接口
     *
     * @param autoCloseTaskPo 关闭入参
     * @return 处理结果
     * @author fanggang7
     * @time 2023-01-31 17:00:46 周二
     */
    @Override
    public Result<Void> closeTask(AutoCloseTaskPo autoCloseTaskPo) {
        log.info("JYBizTaskCloseAbstractService.closeTask param {}", JSON.toJSONString(autoCloseTaskPo));
        Result<Void> result = Result.success();
        try {
            // 关闭主任务
            // 关闭调度任务
            // 签退任务关联的签到记录
        } catch (Exception e) {
            log.error("JYBizTaskCloseAbstractService.closeTask exception {}", JSON.toJSONString(autoCloseTaskPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

}
