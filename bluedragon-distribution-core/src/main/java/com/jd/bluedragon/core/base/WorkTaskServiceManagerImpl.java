package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ql.erp.domain.OrderDeliverWorkTask;
import com.jd.ql.erp.jsf.WorkTaskService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("workTaskServiceManager")
public class WorkTaskServiceManagerImpl implements WorkTaskServiceManager{
    @Autowired
    private WorkTaskService workTaskService;
    /**
     * 终端接口：妥投调度任务录入
     * @param orderDeliverWorkTask
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.WorkTaskServiceManagerImpl.orderDeliverWorkTaskEntry", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean orderDeliverWorkTaskEntry(OrderDeliverWorkTask orderDeliverWorkTask){
        return workTaskService.orderDeliverWorkTaskEntry(orderDeliverWorkTask);
    }
}
