package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;

import java.util.List;

/**
 * @ClassName BoxRelationService
 * @Description
 * @Author wyh
 * @Date 2020/12/14 16:47
 **/
public interface BoxRelationService {

    /**
     *
     * @param relation
     * @return
     */
    InvokeResult<List<BoxRelation>> queryBoxRelation(BoxRelation relation);

    /**
     * 保存箱号绑定关系，保留旧的绑定关系，新增新绑定
     * @param relation
     * @return
     */
    InvokeResult<Boolean> saveBoxRelation(BoxRelation relation);

    InvokeResult<Boolean> saveBoxRelationWithoutCheck(BoxRelation relation);

    /**
     * 解除绑定关系
     * @param relation
     * @return
     */
    InvokeResult<Boolean> releaseBoxRelation(BoxRelation relation);

    /**
     * 根据BC箱号获取绑定关系
     * @param boxCode
     * @return
     */
    InvokeResult<List<BoxRelation>> getRelationsByBoxCode(String boxCode);

    int countByBoxCode(BoxRelation relation);

}
