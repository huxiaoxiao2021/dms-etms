package com.jd.bluedragon.external.crossbow;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.crossbow.CrossbowConfig;
import com.jd.bluedragon.core.crossbow.DMSCrossbowClient;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *     实现对crossbow组件的调用封装，所有需要调用外部三方公司的接口都应该继承该类，成为该类的子类。
 *
 * <doc>
 *     每个子类的实例对应一个crossbowConfig 对应一个接口的调用
 *     多个三方商家的对接需要有多个子类进行对接，子类与三方商家存在一一对应关系，因为getMyRequestBody（）的组装可能一致
 * </doc>
 *
 * @link http://lft.jd.com/docCenter?docId=2967 组件crossbow对接流程
 * @see DMSCrossbowClient
 * @author wuzuxiang
 * @since 2019/10/17
 **/
public abstract class AbstractCrossbowManager<P,R> implements InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 调用crossbow组件需要用到的配置，在物流网关中配置，见： 物流网关 -> 外部服务域
     */
    private CrossbowConfig crossbowConfig;

    @Autowired
    private DMSCrossbowClient dmsCrossbowClient;

    /**
     * 接口执行方法，统一对外
     * @param condition
     * @return
     */
    public R doRestInterface(Object condition){
        return executor(getMyRequestBody(condition), new TypeReference<R>(){});
    }

    /**
     * 构建三方接口的请求体request对象
     * @param condition 相应的条件
     * @return 返回三方接口的请求参数request对象
     */
    protected abstract P getMyRequestBody(Object condition);

    /**
     * 调用物流基础组件crossbow的执行器
     * @param condition 三方公司的接口请求体
     * @param typeReference 三方公司的接口返回类型引用
     * @return 返回 R类型
     */
    private R executor(Object condition, TypeReference<R> typeReference) {
        CallerInfo callerInfo = Profiler.registerInfo("dms.core.AbstractCrossbowManager.pddExecutor",
                Constants.UMP_APP_NAME_DMSWEB, false, false);
        try {
            P parameter = getMyRequestBody(condition);
            return dmsCrossbowClient.executor(crossbowConfig, JsonHelper.toJson(parameter), typeReference);
        } catch (RuntimeException e) {
            Profiler.functionError(callerInfo);
            logger.warn("调用物流网关crossBow组件执行调用拼多多的接口异常:", e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    /**
     * 用于检查之类对crossbowConfig配置的检查
     * @throws Exception 配置检查失败
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == crossbowConfig) {
            throw new RuntimeException(this.getClass() + "缺少crossbowConfig配置，请检查相关的配置");
        }
        if (StringHelper.isEmpty(crossbowConfig.getDomain())) {
            throw new RuntimeException(this.getClass() + "缺少crossbowConfig配置中domain的内容，请检查相关配置");
        }
        if (StringHelper.isEmpty(crossbowConfig.getApi())) {
            throw new RuntimeException(this.getClass() + "缺少crossbowConfig配置中api的内容，请检查相关配置");
        }
        if (StringHelper.isEmpty(crossbowConfig.getAppKey())) {
            throw new RuntimeException(this.getClass() + "缺少crossbowConfig配置中appKey的内容，请检查相关配置");
        }
    }

    public CrossbowConfig getCrossbowConfig() {
        return crossbowConfig;
    }

    public void setCrossbowConfig(CrossbowConfig crossbowConfig) {
        this.crossbowConfig = crossbowConfig;
    }
}
