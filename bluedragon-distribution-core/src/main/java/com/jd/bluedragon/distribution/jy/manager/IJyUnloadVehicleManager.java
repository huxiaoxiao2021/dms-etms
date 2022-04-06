package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.realtime.base.Pager;
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
     * 分页查询卸车包裹
     * @param pager
     * @return
     */
    List<JyVehicleTaskUnloadDetail> queryByCondition(Pager<JyVehicleTaskUnloadDetail> pager);

    /**
     * 查询一条卸车包裹数据
     * @param query
     * @return
     */
    JyVehicleTaskUnloadDetail findOneUnloadDetail(JyVehicleTaskUnloadDetail query);
}
