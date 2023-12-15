package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskStatisticsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:16
 * @Description
 */
@Service
public class JyPickingTaskAggsTransactionManager {

    /**
     * 提货统计修改
     * @param pickingTaskAggsEntity
     * @param pickingTaskSendAggsEntity
     */
    public void updatePickingGoodAggs(PickingGoodTaskStatisticsDto statisticsDto) {
        //todo zcf 统计字段修改

    }
}
