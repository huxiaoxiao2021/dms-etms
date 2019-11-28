package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsDetailService
 * @Description: --Service接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsDetailService extends Service<CollectGoodsDetail> {



    /**
     * 转移集货位
     *  包裹 转移 整个运单
     *
     *  货位 转移整个货位
     * @param
     * @return
     */
    boolean transfer(String sourcePlaceCode,String targetPlaceCode,Integer targetPlaceType,Integer createSiteCode,String packageCode);

    /**
     * 清空数据（物理删除，防止表数据过多）
     * @param collectGoodsDetail
     * @return
     */
    boolean clean(CollectGoodsDetail collectGoodsDetail);



    /**
     * 根据条件获取数据 不分页
     * @param collectGoodsDetail
     * @return
     */
    List<CollectGoodsDetail> findNoPage(CollectGoodsDetail collectGoodsDetail);

    /**
     * 查找已集货数据
     * 按运单维度返回
     * @param collectGoodsDetail
     * @return
     */
    List<CollectGoodsDetailCondition> findScanWaybill(CollectGoodsDetail collectGoodsDetail);

    /**
     * 查找已集货数据
     * @param collectGoodsDetail
     * @return
     */
    List<CollectGoodsDetail> queryByCondition(CollectGoodsDetailCondition collectGoodsDetailCondition);


    /**
     * 检查是否存在集货记录
     * @param pakcageCode 包裹号
     * @param areaCode 集货区
     * @param placeCode 集货位
     * @param createSiteCode 操作分拣中心
     * @return
     */
    boolean checkExist(String pakcageCode,String areaCode,String placeCode, Integer createSiteCode);

    /**
     * 导出记录
     * @param CollectGoodsDetailCondition
     * @return
     */
    public List<List<Object>> getExportData(CollectGoodsDetailCondition CollectGoodsDetailCondition);

    /**
     * 根据包裹号获取包裹的集货信息
     */
    CollectGoodsDetail findCollectGoodsDetailByPackageCode(String packageCode);
}
