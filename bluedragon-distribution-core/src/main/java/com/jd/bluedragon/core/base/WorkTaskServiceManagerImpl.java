package com.jd.bluedragon.core.base;

import com.jd.ql.erp.domain.OrderDeliverWorkTask;
import org.springframework.stereotype.Service;

@Service("workTaskServiceManager")
public class WorkTaskServiceManagerImpl implements WorkTaskServiceManager{
    /**
     * 终端接口：妥投调度任务录入
     * @param orderDeliverWorkTask
     * @return
     */
    public boolean orderDeliverWorkTaskEntry(OrderDeliverWorkTask orderDeliverWorkTask){
        return true;
    }
}
