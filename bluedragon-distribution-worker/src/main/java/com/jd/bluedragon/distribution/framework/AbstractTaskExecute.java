package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangtingwei on 2017/1/13.
 */
public abstract class AbstractTaskExecute<T extends  TaskExecuteContext> {

    private static final Log LOGGER= LogFactory.getLog(AbstractTaskExecute.class);

    private List<TaskHook<T>> hooks;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BaseService baseService;
    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @return
     */
    protected BigWaybillDto getWaybill(String waybillCode){
        if(!SerialRuleUtil.isMatchCommonWaybillCode(waybillCode)){
            if(LOGGER.isInfoEnabled()){
                LOGGER.info(MessageFormat.format("运单号正则校验不通过{0}",waybillCode));
            }
            throw new WayBillCodeIllegalException(MessageFormat.format("运单号正则校验不通过{0}",waybillCode));
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info(MessageFormat.format("获取运单信息{0}",waybillCode));
        }
        BigWaybillDto result= waybillService.getWaybill(waybillCode);
        if(LOGGER.isInfoEnabled()){
            LOGGER.info(MessageFormat.format("获取运单信息{0},结果为{1}",waybillCode, JsonHelper.toJson(result)));
        }
        return result;
    }

    protected BaseStaffSiteOrgDto  getSite(Integer siteCode){
        return  baseService.getSiteBySiteID(siteCode);
    }

    /**
     * 实现任务JSON解析，远程接口调用补全数据,数据验证
     * @param domain 任务数据
     * @return    任务执行上下文
     */
    protected abstract T prepare(Task domain);

    /**
     * 执行核心业务流程
     * @param t
     * @return
     */
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
        }else{
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("任务执行失败，未通过验证{0}",JsonHelper.toJson(domain)));
            }
            return false;
        }
        return  true;
    }

    public List<TaskHook<T>> getHooks() {
        return hooks;
    }

    public void setHooks(List<TaskHook<T>> hooks) {
        this.hooks = hooks;
    }
}
