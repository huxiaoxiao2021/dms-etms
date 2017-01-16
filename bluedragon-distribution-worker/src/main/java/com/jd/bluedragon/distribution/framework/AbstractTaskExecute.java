package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by wangtingwei on 2017/1/13.
 */
public abstract class AbstractTaskExecute<T extends  TaskExecuteContext> {

    @Autowired
    private List<TaskHook<T>> hooks;

    @Autowired
    private WaybillService waybillService;

    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @return
     */
    protected BigWaybillDto getWaybill(String waybillCode){
        return waybillService.getWaybill(waybillCode);
    }

    /**
     * 实现任务JSON解析，远程接口调用补全数据,数据验证
     * @param domain Fix wtw 远程调用 添加条件判断
     * @param <T> 任务执行上下文
     * @return    任务执行上下文
     */
    protected abstract T prepare(Task domain);

    protected abstract boolean executeCoreFlow(T t);

    /**
     * 添加单元测试
     * @param domain
     * @return
     */
    public boolean execute(Task domain){
        T context= prepare(domain);
        if(context.isPassCheck()){
            executeCoreFlow(context);
            if(null!=hooks){
                for (TaskHook<T> hook :hooks){
                    hook.hook(context);
                }
            }
        }
        return  true;
    }

}
