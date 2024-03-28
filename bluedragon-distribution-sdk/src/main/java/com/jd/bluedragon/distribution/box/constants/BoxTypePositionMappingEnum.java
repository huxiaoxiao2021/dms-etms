package com.jd.bluedragon.distribution.box.constants;

import java.util.Arrays;
import java.util.List;

/**
 *
 *  @author weixiaofeng12
 *  @description 岗位和能支撑的箱号类型映射枚举
 *  @date 2024-03-11 17:56
 *
 */

public enum BoxTypePositionMappingEnum {
    /**
     * 集包岗能支撑的箱号类型
     */
    COLLECT_PACKAGE("集包岗", Arrays.asList("BC","BW","BX","TA","TC","WJ")),
    /**
     * 集装岗能支撑的箱号类型
     */
    COLLECT_LOADING("集装岗", Arrays.asList("LL"));

    /**
     * 岗位名称
     */

    private String name;


    /**
     * 支持的箱号类型
     */
    private List<String> supportBoxTypes;

    BoxTypePositionMappingEnum(String name, List<String> supportBoxTypes) {
        this.name = name;
        this.supportBoxTypes = supportBoxTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSupportBoxTypes() {
        return supportBoxTypes;
    }

    public void setSupportBoxTypes(List<String> supportBoxTypes) {
        this.supportBoxTypes = supportBoxTypes;
    }
}
