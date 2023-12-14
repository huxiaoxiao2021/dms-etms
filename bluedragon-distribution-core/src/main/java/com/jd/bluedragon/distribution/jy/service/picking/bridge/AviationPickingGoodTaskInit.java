package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsCacheService;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 航空提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/7 21:35
 * @Description
 */
@Service
public class AviationPickingGoodTaskInit extends PickingGoodTaskInit {
    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);

    @Autowired
    private JyPickingTaskAggsCacheService jyPickingTaskAggsCacheService;

    @Override
    public void setPickingGoodDetailInitService(PickingGoodDetailInitService pickingGoodDetailInitService) {
        super.setPickingGoodDetailInitService(pickingGoodDetailInitService);
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    @Override
    protected boolean generatePickingGoodTask(PickingGoodTaskInitDto obj) {


        pickingGoodDetailInitService.pickingGoodDetailInit(null);


        Integer waitPickingItem = 0;//此时待提取航空任务中的件数
        PickingGoodAggsDto aggDto = new PickingGoodAggsDto();
        aggDto.setWaitPickingTotalNum(waitPickingItem);
        //
        Integer curSiteId = null;
        String bizId = "";
        jyPickingTaskAggsCacheService.saveCacheTaskPickingAgg(curSiteId, bizId, aggDto);

        return true;
    }

    @Override
    protected boolean pickingGoodDetailInit(PickingGoodTaskInitDto initDto) {
        //根据场地类型获取待提明细初始化服务实例
        Integer startSiteType = 64; //上游场地类型
        PickingGoodTaskDetailInitServiceEnum detailInitServiceEnum = PickingGoodTaskDetailInitServiceEnum.getEnumBySource(startSiteType);
        if(!Objects.isNull(detailInitServiceEnum)) {
            PickingGoodDetailInitService pickingGoodDetailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(detailInitServiceEnum.getTargetCode());
            if(!Objects.isNull(pickingGoodDetailInitService)) {
                this.setPickingGoodDetailInitService(pickingGoodDetailInitService);
            }else {
                logWarn("航空提货计划消费，未查到待提明细初始化服务，initDto={}, 上游场地类型为{}", JsonHelper.toJson(initDto), startSiteType);
            }
        }

        pickingGoodDetailInitService.pickingGoodDetailInit(null);
        return true;
    }

    @Override
    protected boolean initDetailSwitch(PickingGoodTaskInitDto pickingGoodTaskInitDto) {
        //todo zcf  根据到达时间做初始化
        return super.initDetailSwitch(pickingGoodTaskInitDto);
    }
}
