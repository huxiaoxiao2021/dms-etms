package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 关闭任务逻辑
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 17:05:14 周二
 */
@Slf4j
@Service
public class JyBizTaskCloseServiceStrategy {

    @Resource
    @Qualifier("jyBizTaskCloseUnloadTaskService")
    private JyBizTaskCloseUnloadTaskServiceImpl jyBizTaskCloseUnloadTaskService;

    @Resource
    @Qualifier("jyBizTaskCloseSendTaskService")
    private JyBizTaskCloseSendTaskServiceImpl jyBizTaskCloseSendTaskService;

    @Resource
    @Qualifier("jyBizTaskCloseStrandService")
    private JyBizTaskCloseStrandServiceImpl jyBizTaskCloseStrandService;

    @Resource
    @Qualifier("jySendVehicleService")
    private IJySendVehicleService jySendVehicleService;

    /**
     * 关闭任务接口
     *
     * @param autoCloseTaskPo 关闭入参
     * @return 处理结果
     * @author fanggang7
     * @time 2023-01-31 17:00:46 周二
     */
    public Result<Void> closeTask(AutoCloseTaskPo autoCloseTaskPo) {
        log.info("JyBizTaskCloseServiceStrategy.closeTask param {}", JSON.toJSONString(autoCloseTaskPo));
        Result<Void> result = Result.success();
        try {
            // 解封车任务
            if(JyAutoCloseTaskBusinessTypeEnum.WAIT_UNLOAD_NOT_FINISH.getCode().equals(autoCloseTaskPo.getTaskBusinessType())){
                return jyBizTaskCloseUnloadTaskService.closeTask(autoCloseTaskPo);
            }
            // 卸车任务
            if(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode().equals(autoCloseTaskPo.getTaskBusinessType())){
                return jyBizTaskCloseUnloadTaskService.closeTask(autoCloseTaskPo);
            }
            // 滞留任务
            if(JyAutoCloseTaskBusinessTypeEnum.STRAND_NOT_SUBMIT.getCode().equals(autoCloseTaskPo.getTaskBusinessType())){
                return jyBizTaskCloseStrandService.closeTask(autoCloseTaskPo);
            }
            //发车任务
            if(JyAutoCloseTaskBusinessTypeEnum.CREATE_SEND_VEHICLE_TASK.getCode().equals(autoCloseTaskPo.getTaskBusinessType())){
                return jySendVehicleService.noticeToCanTEANPackage(autoCloseTaskPo);
            }
        } catch (Exception e) {
            log.error("JyBizTaskCloseServiceStrategy.closeTask exception {}", JSON.toJSONString(autoCloseTaskPo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
