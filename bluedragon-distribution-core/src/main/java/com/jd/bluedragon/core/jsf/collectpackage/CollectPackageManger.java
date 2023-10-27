package com.jd.bluedragon.core.jsf.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.StatisticsUnderFlowQueryReq;
import com.jd.bluedragon.common.dto.collectpackage.response.StatisticsUnderFlowQueryResp;
import com.jd.bluedragon.core.jsf.collectpackage.dto.ListTaskStatisticDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.ListTaskStatisticQueryDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskQueryDto;

public interface CollectPackageManger {

    /**
     * 查询单个集包任务统计信息
     *
     * @param dto 查询任务统计请求
     * @return 查询任务统计响应
     */
    StatisticsUnderTaskDto queryTaskStatistic(StatisticsUnderTaskQueryDto dto);

    /**
     * 查询任务下的流向聚合数据（按照流向group分组统计）
     * @param dto
     * @return
     */

    StatisticsUnderTaskDto queryTaskFlowStatistic(StatisticsUnderTaskQueryDto dto);

    /**
     * 查询多个集包任务统计信息
     *
     * @param dto 查询任务统计请求
     * @return 查询任务统计响应
     */
    ListTaskStatisticDto listTaskStatistic(ListTaskStatisticQueryDto dto);

    /**
     * 查询流向下包裹明细
     *
     * @param request 查询请求对象
     * @return 查询响应对象
     */
    StatisticsUnderFlowQueryResp listPackageUnderFlow(StatisticsUnderFlowQueryReq request);


}
