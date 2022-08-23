package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dto.BizTaskConstraint;
import com.jd.bluedragon.utils.SpringHelper;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service("jyBizTaskConstraintJsfService")
public class JyBizTaskConstraintJsfServiceImpl implements JyBizTaskConstraintJsfService{
    @Override
    public Result<BizTaskConstraint> getBizTaskConstraint(String bizId, String taskType) {
        Result<BizTaskConstraint> result = Result.success();
        BizTaskService bizTaskServoce = bizProcessorAssemble(taskType);
        if (bizTaskServoce == null){
            return result.toFail("未找到业务约束提供者");
        }
        BizTaskConstraint bizTaskConstraint = bizTaskServoce.bizConstraintAssemble(bizId);
        result.setData(bizTaskConstraint);
        return result;
    }

    private BizTaskService bizProcessorAssemble(String taskType) {
        Map<String, BizTaskService> beans = SpringHelper.getBeans(BizTaskService.class);
        for (Map.Entry<String,BizTaskService> entry:beans.entrySet()){
            BizType annotation = entry.getClass().getAnnotation(BizType.class);
            for (JyScheduleTaskTypeEnum en:annotation.value()){
                if (Objects.equals(en.getCode(),taskType)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
