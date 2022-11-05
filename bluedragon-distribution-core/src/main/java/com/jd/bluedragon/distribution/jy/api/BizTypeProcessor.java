package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.utils.SpringHelper;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class BizTypeProcessor {

    public BizTaskService processor(String taskType) {
        Map<String, BizTaskService> beans = SpringHelper.getBeans(BizTaskService.class);
        for (Map.Entry<String,BizTaskService> entry:beans.entrySet()){
            BizType annotation = entry.getValue().getClass().getAnnotation(BizType.class);
            for (JyScheduleTaskTypeEnum en:annotation.value()){
                if (Objects.equals(en.getCode(),taskType)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
