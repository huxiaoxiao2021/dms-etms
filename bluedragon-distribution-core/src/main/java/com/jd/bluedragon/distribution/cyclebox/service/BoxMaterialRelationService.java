package com.jd.bluedragon.distribution.cyclebox.service;

import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;

public interface BoxMaterialRelationService {

    /**
     * 新增
     * @param mode
     * @return
     */
    int add(BoxMaterialRelation mode);

    /**
     * 更新
     * @param mode
     * @return
     */
    int update(BoxMaterialRelation mode);

    /**
     * 根据箱号获取数据条数
     * @param boxCode
     * @return
     */
    int getCountByBoxCode(String boxCode);

    /**
     * 根据箱号查询单条数据
     * @param boxCode
     * @return
     */
    BoxMaterialRelation getDataByBoxCode(String boxCode);
}
