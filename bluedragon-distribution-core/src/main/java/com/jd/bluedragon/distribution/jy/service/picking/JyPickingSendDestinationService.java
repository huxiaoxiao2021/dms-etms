package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendBatchCodeDetailRes;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.SendFlowDto;

import java.util.List;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeSealCarDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
public interface JyPickingSendDestinationService {

    /**
     * 获取该流向未完成的批次号,查询不到生成新的批次号
     * @param curSiteId
     * @param nextSiteId
     * @return
     */
    String findOrGenerateBatchCode(Long curSiteId, Long nextSiteId, User user, Integer taskType);

    /**
     * 校验发货流向是否存在
     * @param curSiteId
     * @param nextSiteId
     * @return  true: 存在
     */
    boolean existSendNextSite(Long curSiteId, Long nextSiteId, Integer taskType);

    Boolean finishSendTask(FinishSendTaskReq req);
    //批量修改提货岗发货批次为已封状态
    Boolean batchUpdateBatchCodeSealCarStatus(BatchCodeSealCarDto req);

    /**
     * 查询已维护流向信息
     * @param req
     * @return
     */
    List<SendFlowDto> listSendFlowInfo(SendFlowReq req);

    /**
     * 添加流向
     * @param req
     * @return
     */
     InvokeResult<Boolean> addSendFlow(SendFlowAddReq req);

    /**
     * 删除流向
     * @param req
     * @return
     */
    boolean deleteSendFlow(SendFlowDeleteReq req);

    /**
     * 查询流向信息
     */
    JyPickingSendDestinationDetailEntity getSendDetailBySiteId(Long createSiteId, Long nextSiteId, Integer taskType);

    /**
     * 查询流向最后一个未发货完成的批次
     * @param curSiteId
     * @param nextSiteId
     * @param taskType
     * @return
     */
    String fetchLatestNoCompleteBatchCode(Long curSiteId, Long nextSiteId, Integer taskType);
    //批次列表查询
    InvokeResult<PickingSendBatchCodeDetailRes> pageFetchSendBatchCodeDetailList(PickingSendBatchCodeDetailReq req);
    //删除批次
    void delBatchCodes(DelBatchCodesReq req);
}
