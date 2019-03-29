package com.jd.bluedragon.core.base;

import com.jd.ql.erp.domain.OrderDeliverWorkTask;

public interface WorkTaskServiceManager {
    /**
     * 终端接口：妥投调度任务录入
     * @param orderDeliverWorkTask
     * @return
     */
    boolean orderDeliverWorkTaskEntry(OrderDeliverWorkTask orderDeliverWorkTask);
}
