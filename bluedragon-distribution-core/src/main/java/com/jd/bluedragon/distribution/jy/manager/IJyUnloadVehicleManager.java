package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.unload.JyUnloadTaskWaybillAgg;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;

import java.util.List;

/**
 * @ClassName IJyUnloadVehicleManager
 * @Description
 * @Author wyh
 * @Date 2022/4/6 19:44
 **/
public interface IJyUnloadVehicleManager {

    /**
     * 分页查询待扫包裹明细
     * @param query
     * @return
     */
    Pager<JyVehicleTaskUnloadDetail> queryToScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query);

    /**
     * 分页查询拦截包裹明细
     * @param query
     * @return
     */
    Pager<JyVehicleTaskUnloadDetail> queryInterceptBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query);

    /**
     * 分页查询多扫包裹明细
     * @param query
     * @return
     */
    Pager<JyVehicleTaskUnloadDetail> queryMoreScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query);

    /**
     * 分页查询多扫和待扫包裹明细
     * @param query
     * @return
     */
    Pager<JyVehicleTaskUnloadDetail> queryMoreScanAndToScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query);

    /**
     * 查询卸车包裹数据
     * @param query
     * @return
     */
    List<JyVehicleTaskUnloadDetail> findUnloadDetail(JyVehicleTaskUnloadDetail query);

    /**
     * 查询获取编码
     * @param currentSiteCode
     * @param nextSiteCode
     * @return
     */
    String getGoodsAreaCode(Integer currentSiteCode, Integer nextSiteCode) ;

    /**
     * 卸车岗查询统计下钻运单明细
     * @param query
     * @return
     */
    Pager<JyUnloadTaskWaybillAgg> queryUnloadTaskWaybill(Pager<JyVehicleTaskUnloadDetail> query);

    //Pager<JyUnloadTaskWaybillAgg> queryUnloadTaskWaybill(Pager<JyVehicleTaskUnloadDetail> query);
}
