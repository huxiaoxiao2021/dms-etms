package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;

/**
 * 航空提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/7 21:35
 * @Description
 */
public class AviationPickingGoodTask extends PickingGoodTask {


    @Override
    public void setPickingGoodDetailInitService(PickingGoodDetailInitService pickingGoodDetailInitService) {
        super.setPickingGoodDetailInitService(pickingGoodDetailInitService);
    }

    @Override
    public boolean generatePickingGoodTask(PickingGoodTaskInitDto obj) {

        pickingGoodDetailInitService.pickingGoodDetailInit(null);

        return true;
    }
}
