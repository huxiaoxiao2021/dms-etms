package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;

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
     * 查询一条卸车包裹数据
     * @param query
     * @return
     */
    JyVehicleTaskUnloadDetail findOneUnloadDetail(JyVehicleTaskUnloadDetail query);
}
