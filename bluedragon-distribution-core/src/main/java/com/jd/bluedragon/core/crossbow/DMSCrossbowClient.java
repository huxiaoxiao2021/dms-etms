package com.jd.bluedragon.core.crossbow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.lop.crossbow.dto.LopRequest;
import com.jd.lop.crossbow.dto.LopResponse;
import com.jd.lsb.component.exception.ExecErrorException;
import com.jd.lsb.component.service.CrossbowService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;

/**
 * <p>
 *     分拣中心调用外部接口的代理类，主要涉及到一些三方公司需要走外网的接口业务
 *     用到的是架构部的crossBow组件：该组件实现了对三方公司接口的统一调用，屏蔽了内部因三方差异二带来的接口协议和网络权限的差异。
 *
 * @see com.jd.bluedragon.external.crossbow.AbstractCrossbowManager 内部对crossbow组件的统一调用封装
 * @link http://lft.jd.com/docCenter?docId=2978 组件化接入文档
 * @author wuzuxiang
 * @since 2019/10/17
 **/
@Component("dmsCrossbowClient")
public class DMSCrossbowClient {

    private static final Logger logger = LoggerFactory.getLogger(DMSCrossbowClient.class);

    private CrossbowService crossbowService;

    /***
     * 调用crossbow组件客户端的执行方法 目前不涉及加密，如果有需要对接加密的请完善该接口
     * @param crossBowConfig 需要的配置参数
     * @param parameterStr 三方的公司需要用的请求的体body
     * @param typeReference 三方公司提供的接口返回值的类型
     * @param <R> 三方公司提供的接口返回值的类型的泛型
     * @return 返回 R类型
     * @throws RuntimeException 如果crossbow调用失败则抛出相应的异常由调用方处理
     */
    @JProfiler(jKey = "dms.core.DMSCrossBowClient.executor", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public <R> R executor(@NotNull CrossbowConfig crossBowConfig, String parameterStr, Type typeReference) throws RuntimeException {
        LopRequest request = new LopRequest();
        request.setDomain(crossBowConfig.getDomain());
        request.setCustomerId(crossBowConfig.getCustomerId());
        request.setApi(crossBowConfig.getApi());
        request.setAppKey(crossBowConfig.getAppKey());
        request.setBody(parameterStr);
        try {
            crossbowService = getCrossbowService();
            LopResponse response = crossbowService.execute(request);
            if (response.getStatusCode() != 200) {
                logger.error("调用物流网关crossBow组件失败，参数为：{}, 返回值为: {}", JsonHelper.toJson(request), JsonHelper.toJson(response));
                throw new RuntimeException("调用物流网关crossBow组件失败,返回值：" + JsonHelper.toJson(response));
            }
            return JSON.parseObject(response.getBody(), typeReference);
        } catch (ExecErrorException e) {
            logger.error("调用物流网关的接口异常，请求参数: " + JsonHelper.toJson(parameterStr), e);
            throw new RuntimeException("物流网关crossBow组件调用异常", e);
        }
    }

    /**
     * 从Spring的容器中获取组件“crossbow”，从crossbow组件中获取原子组件crossbowService
     * @return crossbowService组件的实例
     */
    private CrossbowService getCrossbowService() {
        if (crossbowService == null) {
            com.jd.lsb.component.api.component.Component component  = (com.jd.lsb.component.api.component.Component) SpringHelper.getBean("crossbow");
            crossbowService= (CrossbowService)component.getServiceImpl("crossbowService");
        }
        return crossbowService;
    }
}
