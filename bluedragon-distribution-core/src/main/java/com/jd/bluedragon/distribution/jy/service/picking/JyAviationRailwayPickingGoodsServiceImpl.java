package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingSendGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendGoodsRes;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author zhengchengfa
 * @Date 2023/12/4 13:29
 * @Description
 */

@Service
public class JyAviationRailwayPickingGoodsServiceImpl implements JyAviationRailwayPickingGoodsService{

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsServiceImpl.class);
    //空铁提货缓存服务
    @Autowired
    private JyAviationRailwayPickingGoodsCacheService pickingGoodsCacheService;
    //空铁提货任务服务
    @Autowired
    private JyBizTaskPickingGoodService jyBizTaskPickingGoodService;
    //空铁提货统计层服务
    @Autowired
    private JyPickingTaskAggsService jyPickingTaskAggsService;
    //空铁提货发货服务
    @Autowired
    private JyPickingSendDestinationService jyPickingSendDestinationService;
    //空铁提发记录服务
    @Autowired
    private JyPickingSendRecordService jyPickingSendRecordService;


    @Override
    public InvokeResult<PickingSendGoodsRes> pickingSendGoods(PickingSendGoodsReq request) {
        return null;
    }
}
