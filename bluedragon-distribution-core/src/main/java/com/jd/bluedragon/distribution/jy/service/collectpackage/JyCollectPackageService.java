package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyCollectPackageService {

    /**
     * 集包
     */
    InvokeResult<CollectPackageResp> collectPackage(CollectPackageReq request);

    /**
     * 查询集包任务列表
     * @param request
     * @return
     */
    InvokeResult<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request);


    /**
     * 查询任务详情
     * @param request
     * @return
     */
    InvokeResult<TaskDetailResp> queryTaskDetail(TaskDetailReq request);


    /**
     * 封箱
     * @param request
     * @return
     */
    InvokeResult<SealingBoxResp> sealingBox(SealingBoxReq request);

    /**
     * 绑定集包袋
     * @param request
     * @return
     */
    InvokeResult bindCollectBag(BindCollectBagReq request);

    /**
     *  取消集包
     * @param request
     * @return
     */
    InvokeResult<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request);

}
