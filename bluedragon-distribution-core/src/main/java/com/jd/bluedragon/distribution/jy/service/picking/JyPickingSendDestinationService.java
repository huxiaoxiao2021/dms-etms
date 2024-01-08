package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.FinishSendTaskReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowAddReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowDeleteReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.SendFlowReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.SendFlowDto;

import java.util.List;

import com.jd.bluedragon.common.dto.base.request.User;
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
    String findOrGenerateBatchCode(Long curSiteId, Long nextSiteId, User user);

    /**
     * 校验发货流向是否存在
     * @param curSiteId
     * @param nextSiteId
     * @return  true: 存在
     */
    boolean existSendNextSite(Long curSiteId, Long nextSiteId);

    Boolean finishSendTask(FinishSendTaskReq req);

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
    boolean addSendFlow(SendFlowAddReq req);

    /**
     * 删除流向
     * @param req
     * @return
     */
    boolean deleteSendFlow(SendFlowDeleteReq req);

    List<JyPickingSendDestinationDetailEntity> listSendCodesByCreateSiteId(Long createSiteId, List<Long> destinationSiteId);
}
