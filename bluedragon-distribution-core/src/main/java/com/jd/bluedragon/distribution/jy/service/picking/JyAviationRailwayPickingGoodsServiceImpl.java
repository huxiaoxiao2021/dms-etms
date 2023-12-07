package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingSendGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendGoodsRes;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


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
    public InvokeResult<PickingSendGoodsRes> pickingSendGoodsScan(PickingSendGoodsReq request) {
        return null;
    }



    public InvokeResult<JyBizTaskPickingGoodEntity> fetchPickingTaskByBarCode(Long siteCode, String barCode) {
        InvokeResult<JyBizTaskPickingGoodEntity> res = new InvokeResult<>();
        if(Objects.isNull(siteCode) || StringUtils.isBlank(barCode)) {
            res.parameterError("查询待提任务参数不合法");
            return res;
        }
        InvokeResult<String> taskBizIdRes = jyPickingSendRecordService.fetchPickingBizIdByBarCode(siteCode, barCode);
        if(!taskBizIdRes.codeSuccess()) {
            res.error(taskBizIdRes.getMessage());
            return res;
        }
        if(StringUtils.isBlank(taskBizIdRes.getData())) {
            res.setMessage("未查到待提货任务BizId");
            return res;
        }

        JyBizTaskPickingGoodEntity pickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(taskBizIdRes.getData(), false);
        if(!PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(pickingGoodEntity) && !JyBizTaskPickingGoodEntity.INTERCEPT_FLAG.equals(pickingGoodEntity.getIntercept())) {
            res.setData(pickingGoodEntity);
        }else {
            res.setMessage("未查到待提货任务");
        }
        return res;
    }

}
