package com.jd.bluedragon.distribution.dock.entity;

import java.io.Serializable;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.entity
 * @ClassName: AllowedVehicleEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/29 10:50
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class AllowedVehicleEntity implements Serializable {

    /**
     * 车类型编码
     */
    private String code;

    /**
     * 车类型名称
     */
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
