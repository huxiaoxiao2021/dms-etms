package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskByIdReq;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpScrappedDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionScrappedTypeDto;
import com.jd.lsb.flow.domain.HistoryApprove;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/10 17:20
 * @Description:
 */
public interface JyScrappedExceptionService {

    /**
     * 获取报废异常列表接口
     *
     */
    JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList();

    /**
     * 处理任务接口-报废
     */
    JdCResponse<Boolean> processTaskOfscrapped(ExpScrappedDetailReq req);

    JdCResponse<ExpScrappedDetailDto> getTaskDetailOfscrapped(ExpTaskByIdReq req);

    /**
     * 审批结果处理
     * 
     * @param historyApprove
     */
    void dealApproveResult(HistoryApprove historyApprove);
}
