package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;

/**
 * 提货任务抽象化
 * @Author zhengchengfa
 * @Date 2023/12/7 21:29
 * @Description
 */
public abstract class PickingGoodTask {
    /**
     * 待提明细初始化服务， 如不需要做明细初始化时允许为null, 业务层做好判空处理
     */
    PickingGoodDetailInitService pickingGoodDetailInitService;

    public void setPickingGoodDetailInitService(PickingGoodDetailInitService pickingGoodDetailInitService) {
        this.pickingGoodDetailInitService = pickingGoodDetailInitService;
    }

    /**
     * 生成提货任务
     * @param obj
     */
    public abstract boolean generatePickingGoodTask(PickingGoodTaskInitDto obj);
}
