package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.FinishSendTaskReq;
import com.jd.bluedragon.distribution.jy.constants.JyPickingSendTaskEnum;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendDestinationDetailDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 空铁提货发货服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
@Service
public class JyPickingSendDestinationServiceImpl implements JyPickingSendDestinationService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendDestinationServiceImpl.class);

    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private JyPickingSendDestinationDetailDao jyPickingSendDestinationDetailDao;


    @Override
    public String fetchSendingBatchCode(Integer curSiteId, Long nextSiteId) {

        //todo zcf
        return null;
    }

    @Override
    public Boolean finishSendTask(FinishSendTaskReq req) {
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setStatus(JyPickingSendTaskEnum.TO_SEND.getCode());
        entity.setSendCode(req.getSendCode());
        entity.setCreateSiteId((long) req.getCurrentOperate().getSiteCode());
        entity.setNextSiteId((long) req.getNextSiteId());
        entity.setCompleteTime(new Date());
        jyPickingSendDestinationDetailDao.updateSendTaskStatus(entity);
        return true;
    }
}
