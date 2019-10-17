package com.jd.bluedragon.core.external;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.lop.crossbow.dto.LopRequest;
import com.jd.lop.crossbow.dto.LopResponse;
import com.jd.lsb.component.api.component.Component;
import com.jd.lsb.component.exception.ExecErrorException;
import com.jd.lsb.component.service.CrossbowService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣中心调用外部接口的代理类，主要涉及到一些三方公司需要走外网的接口业务
 *     用到的是架构部的crossBow组件
 *
 * @link http://lcp.jd.com/services/queryMethodByServiceNameDetail.do?serviceName=com.jd.lsb.component.service.CrossbowService 组件接口文档地址
 * @link http://lft.jd.com/docCenter?docId=2967 接入流程地址
 * @author wuzuxiang
 * @since 2019/10/15
 **/
@Service("dmsExternalClient")
public class DMSExternalClient {

    private static final Logger logger = LoggerFactory.getLogger(DMSExternalClient.class);

    @JProfiler(jKey = "dms.core.DMSExternalClient.executor", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public <R> R executor(String domain, String api, String appKey, String customerId, Object parameter, TypeReference<R> typeReference) throws RuntimeException {
        LopRequest request = new LopRequest();
        request.setDomain(domain);
        request.setCustomerId(customerId);
        request.setApi(api);
        request.setAppKey(appKey);
        request.setBody(JsonHelper.toJson(parameter));
        try {
            Component component  = (Component) SpringHelper.getBean("crossbow");
            CrossbowService crossbowService= (CrossbowService)component.getServiceImpl("crossbowService");
            LopResponse response = crossbowService.execute(request);
            if (response.getStatusCode() != 200) {
                logger.error("调用物流网关crossBow组件失败，参数为：{}, 返回值为: {}", JsonHelper.toJson(request), JsonHelper.toJson(response));
                throw new RuntimeException("调用物流网关crossBow组件失败,返回值：" + JsonHelper.toJson(response));
            }
            return JSON.parseObject(response.getBody(), typeReference);
        } catch (ExecErrorException e) {
            logger.error("调用物流网关的接口异常，请求参数: " + JsonHelper.toJson(parameter), e);
            throw new RuntimeException("物流网关crossBow组件调用异常", e);
        }
    }
}
