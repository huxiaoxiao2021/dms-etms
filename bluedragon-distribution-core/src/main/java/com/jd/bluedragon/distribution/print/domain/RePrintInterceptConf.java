package com.jd.bluedragon.distribution.print.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author liwenji3
 * @Date 2024/5/7 18:18
 * @Description 补打拦截配置
 */
@Data
public class RePrintInterceptConf implements Serializable {

    /**
     * 场地配置
     */
    private List<String> siteCodeList;

    /**
     * 场地类型配置
     */
    private List<String> siteSortTypeList;

    /**
     * 打印时间限制，单位小时
     */
    private Integer printFromTimeHour;

    /**
     * 打印次数限制
     */
    private Long printCount;
}
