package com.jd.bluedragon.distribution.box.constants;

import java.util.Arrays;
import java.util.List;

public enum BoxTypePositionMappingEnum {
    COLLECT_PACKAGE("集包岗", Arrays.asList("BC","BW","BX","TA","TC","WJ")),
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
