package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.TmsSealCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;

import java.util.List;

/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:05
 */
public interface UnloadCarService {

    /**
     * 根据封车编码获取卸车任务
     *
     * @param sealCarCode
     * @return
     */
    InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode);

    /**
     * 卸车扫描
     *
     * @param request
     * @return
     */
    InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request);

    /**
     * 卸车扫描明细
     *
     * @param sealCarCode
     * @return
     */
    InvokeResult<List<UnloadCarDetailScanResult>> searchUnloadDetail(String sealCarCode);

    /**
     * 根据条件查询卸车任务
     *
     * @param condition
     * @return
     */
    List<UnloadCarTask> queryByCondition(UnloadCarCondition condition);

    /**
     * 分配卸车任务
     *
     * @param request
     * @return
     */
    boolean distributeTask(DistributeTaskRequest request);

    /**
     * 插入卸车任务
     *
     * @param tmsSealCar
     * @return
     */
    boolean insertUnloadCar(TmsSealCar tmsSealCar);

    /**
     * 获取分配给责任人的任务
     *
     * @param unloadCarTaskReq
     * @return
     */
    InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq);

    /**
     * 修改卸车任务状态
     *
     * @param unloadCarTaskReq
     * @return
     */
    InvokeResult<List<UnloadCarTaskDto>> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq);

    /**
     * 获取任务协助人列表
     *
     * @param sealCarCode 任务编码(封车编码)
     * @return
     */
    InvokeResult<List<HelperDto>> getUnloadCarTaskHelpers(String sealCarCode);

    /**
     * 添加、删除任务协助人
     *
     * @param taskHelpersReq
     * @return
     */
    InvokeResult<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq);

    /**
     * 获取已开始未完成的任务
     *
     * @param taskHelpersReq
     * @return
     */
    InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq);

}
