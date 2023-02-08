package com.jd.bluedragon.distribution.jy.annotation;

import com.jd.bluedragon.distribution.jy.constants.JyAggsTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional
public @interface JyAggsType {
    JyAggsTypeEnum[] value() default {};
}
