package com.jd.bluedragon.distribution.jy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RedisHashColumn {

    /**
     * hash key fieldName
     * @return
     */
    String hashField() default "";

}
