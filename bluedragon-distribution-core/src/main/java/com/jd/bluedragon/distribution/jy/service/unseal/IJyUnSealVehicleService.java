package com.jd.bluedragon.distribution.jy.service.unseal;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnSealDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.Date;

/**
 * @ClassName IJyUnSealVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
public interface IJyUnSealVehicleService {

    /**
     * 拉取封车任务
     * @param request
     * @return
     */
    @Deprecated
    InvokeResult<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request);

    /**
     * 拉取待解封车任务
     * @param request
     * @return
     */
    InvokeResult<SealVehicleTaskResponse> fetchUnSealTask(SealVehicleTaskRequest request);

    /**
     * 封车任务明细
     * @param request
     * @return
     */
    InvokeResult<SealTaskInfo> taskInfo(SealTaskInfoRequest request);


    /**
     * 封签号列表
     * @param request
     * @return
     */
    InvokeResult<SealCodeResponse> sealCodeList(SealCodeRequest request);


    /**
     * 创建解封车任务
     * @param dto
     * @return
     */
    boolean createUnSealTask(JyBizTaskUnSealDto dto);

    /**
     * 取消解封车任务
     * @param dto
     * @return
     */
    boolean cancelUnSealTask(JyBizTaskUnSealDto dto);

    /**
     * 获取待解封车数据详情
     * @param request
     * @return
     */
    InvokeResult<SealTaskInfo> getSealTaskInfo(SealTaskInfoRequest request);

    /**
     * 获取待解封车数据详情
     * @param request 请求参数
     * @return 详情
     * @author fanggang7
     * @time 2023-03-02 21:37:32 周四
     */
    Result<SealTaskInfo> getUnSealTaskInfo(SealTaskInfoRequest request);

    /**
     * 记录实际解封车顺序
     * @param bizId 解封车业务主键 上游封车编码
     * @param  unSealTime 解封车时间
     * @return
     */
    void saveRealUnSealRanking(String bizId, Date unSealTime);
}
