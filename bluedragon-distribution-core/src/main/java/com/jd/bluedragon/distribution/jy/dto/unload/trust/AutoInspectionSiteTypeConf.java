package com.jd.bluedragon.distribution.jy.dto.unload.trust;

import lombok.Data;

import java.util.List;

/**
 *@Author liwenji3
 *@Date 2024/4/18 09:57
 *@Description 自动验货场地类型配置,配置到最后一层，例如所有二级类型为 123511 的所有分拣中心都允许操作，那么就配置 sortSubTypeList = ["123511"] 即可
 */
@Data
public class AutoInspectionSiteTypeConf {

    /**
     * 一级类型
     */
    private List<Integer> sortTypeList;

    /**
     * 二级类型
     */
    private List<Integer> sortSubTypeList;


    /**
     * 三级类型
     */
    private List<Integer> sortThirdTypeList;
}
