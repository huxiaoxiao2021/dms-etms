package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDTO;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceCondition;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceService
 * @Description: 集货位表--Service接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsPlaceService extends Service<CollectGoodsPlace> {


    boolean saveAll(List<CollectGoodsPlace> collectGoodsPlaces);

    boolean savePalceType(CollectGoodsPlaceCondition condition);



    /**
     * 校验 集货位是否是空闲
     * @param collectGoodsPlace
     * @return
     */
    boolean checkPalceIsEmpty(CollectGoodsPlace collectGoodsPlace);


    boolean updateStatus(CollectGoodsPlace collectGoodsPlace);

    /**
     * 获得推荐货位
     * @param packageCode
     * @param createSiteCode
     * @return
     */
    CollectGoodsPlace recommendPlace(String packageCode,String areaCode, Integer createSiteCode,InvokeResult<CollectGoodsDTO> result);

    /**
     * 获取异常货位
     * @param createSiteCode
     * @param areaCode
     * @return
     */
    CollectGoodsPlace findAbnormalPlace(Integer createSiteCode,String areaCode);

    /**
     * 获取货位
     * @param createSiteCode
     * @param placeCode
     * @return
     */
    CollectGoodsPlace findPlaceByCode(Integer createSiteCode,String placeCode);
}
