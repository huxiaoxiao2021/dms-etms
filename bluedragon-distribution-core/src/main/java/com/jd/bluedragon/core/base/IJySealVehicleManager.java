package com.jd.bluedragon.core.base;


import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.seal.SealVehicleTaskResponse;

/**
 * @ClassName IJySealVehicleManager
 * @Description
 * @Author wyh
 * @Date 2022/3/9 18:48
 **/
public interface IJySealVehicleManager {

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
}
