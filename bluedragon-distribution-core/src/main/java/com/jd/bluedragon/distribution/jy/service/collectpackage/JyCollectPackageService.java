package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;

public interface JyCollectPackageService {

    /**
     * 集包
     */
    InvokeResult<CollectPackageResp>  collectPackage(CollectPackageReq request);

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


    /**
     * 检索集包任务
     * @param request
     * @return
     */
    InvokeResult<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request);

    /**
     * 查询任务下的统计信息
     *
     * @param request 统计信息查询请求对象
     * @return 统计信息查询响应对象
     */

    InvokeResult<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request);

    /**
     * 查询任务流向下统计数据
     *
     * @param request 统计信息查询请求对象
     * @return 响应结果对象，包含任务流向下统计数据
     */
    InvokeResult<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request);
}
