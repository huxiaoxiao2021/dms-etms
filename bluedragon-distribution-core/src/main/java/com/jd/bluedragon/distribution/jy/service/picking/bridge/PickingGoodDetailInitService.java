package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;

import java.util.Collection;

/**
 * 提货任务待提明细初始化服务
 * @Author zhengchengfa
 * @Date 2023/12/7 21:30
 * @Description
 */
public interface PickingGoodDetailInitService {


    /**
     * 待提明细初始化拆分
     * @return
     */
    public boolean pickingGoodDetailInitSplit(PickingGoodTaskDetailInitDto initDto);

    /**
     * 待提明细初始化
     * @return
     */
    public boolean pickingGoodDetailInit(Collection<PickingGoodTaskDetailInitDto> values);
}
