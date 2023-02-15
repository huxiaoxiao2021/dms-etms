package com.jd.bluedragon.distribution.jy.manager;


import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.seal.SealVehicleTaskResponse;

/**
 * @ClassName IJyUnSealVehicleManager
 * @Description
 * @Author wyh
 * @Date 2022/3/9 18:48
 **/
public interface IJyUnSealVehicleManager {

    /**
     * 查询封车任务
     * @param pager
     * @return
     */
    SealVehicleTaskResponse querySealTask(Pager<SealVehicleTaskQuery> pager);

    /**
     * 查询封车任务详情
     * @param request
     * @return
     */
    SealCarMonitor querySealTaskInfo(SealTaskInfoRequest request);

    /**
     * 获取封车数据信息
     * @param sealCarCode
     * @return
     */
    SealCarMonitor querySealCarData(String sealCarCode);
}
