package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;

/**
 * 提货任务待提明细初始化服务
 * @Author zhengchengfa
 * @Date 2023/12/7 21:30
 * @Description
 */
public interface PickingGoodDetailInitService {

    /**
     * 待提明细初始化
     * @param detailInitDto
     * @return
     */
    public boolean pickingGoodDetailInit(PickingGoodTaskDetailInitDto detailInitDto);
}
