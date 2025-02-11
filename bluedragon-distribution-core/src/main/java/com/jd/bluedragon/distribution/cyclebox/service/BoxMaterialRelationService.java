package com.jd.bluedragon.distribution.cyclebox.service;

import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据集包袋编号查询单条数据
     * @param materialCode
     * @return
     */
    BoxMaterialRelation getDataByMaterialCode(String materialCode);


    /**
     * 根据集包袋编号查询固定条数数据
     * @param materialCode
     * @return
     */
    List<BoxMaterialRelation> getLimitDataByMaterialCode(String materialCode, Integer limit);

    int updateUnBindByMaterialCode(BoxMaterialRelation boxMaterialRelation);

    int updateUnBindByMaterialCodeAndBoxCode(BoxMaterialRelation boxMaterialRelation);

    BoxMaterialRelation getBoxMaterialRelationByMaterialCodeAndBoxcode(String boxCode, String materialCode);

    List<BoxMaterialRelation> findByMaterialCodeAndBoxCode(Map<String, Object> map);

    int countByMaterialCodeAndBoxCode(Map<String, Object> map);

    /**
     * 根据箱号批量获取绑定信息
     * @param boxCodeList
     * @return
     */
    List<BoxMaterialRelation> getDataByBoxCodeList(List<String> boxCodeList);

    /**
     * @param boxMaterialRelation 绑定关系入参
     * @return 绑定结果包装类
     * @author fanggang7
     * @time 2024-02-23 17:35:49 周五
     */
    Result<Boolean> upsertBoxMaterialRelationBind(BoxMaterialRelation boxMaterialRelation);
}
