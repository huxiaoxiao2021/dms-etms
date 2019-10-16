package com.jd.bluedragon.external.crossbow.pdd.manager;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.external.DMSExternalClient;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDRequest;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     拼多多公司的服务对接接口，manager层
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public abstract class PDDManager {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 拼多多公司给京东物流公司定义的物流公司编码，用于接口的合法调用
     */
    private String wpCode;

    /**
     * 用于生成拼多多公司的请求前面的秘钥
     */
    private String secret;

    /**
     * 拼多多在物流网关外部服务域中配置的服务域
     */
    private String domain;

    /**
     * 拼多多在物流网关外部服务域中配置的api对应的url
     */
    private String api;

    /**
     * 拼多多订单在外部服务域中配置的appKey（在物流开放平台中入驻）
     */
    private String appKey;

    /**
     * 客户标识
     */
    private String customerId;

    @Autowired
    private DMSExternalClient dmsExternalClient;

    /**
     * 根据拼多多的电子面单号获取拼多多的电子面单打印信息
     * @param request 拼多多接口实现
     * @return 返回电子面单打印对象
     */
    public abstract PDDResponse<PDDWaybillDetailDto> queryWaybillDetailByWaybillCode(PDDWaybillQueryDto request);

    /**
     * 拼多多公司对接的通用请求处理类
     * @param request 实际请求体
     * @param typeReference 返回类型
     * @param <R> 返回类型
     * @return
     */
    protected  <R> R pddExecutor(Object request, TypeReference<R> typeReference)  {
        PDDRequest pddRequest = new PDDRequest();
        pddRequest.setWpCode(this.wpCode);
        pddRequest.setLogisticsInterface(JsonHelper.toJson(request));
        String dataDigest = new String(Base64.encodeBase64(Md5Helper.getMD5(pddRequest.getLogisticsInterface() + secret)), StandardCharsets.UTF_8);
        pddRequest.setDataDigest(dataDigest);
        CallerInfo callerInfo = Profiler.registerInfo("dms.core.PDDManager.pddExecutor", Constants.UMP_APP_NAME_DMSWEB, false, false);
        try {
            return dmsExternalClient.executor(this.domain, this.api, this.appKey, this.customerId, pddRequest, typeReference);
        } catch (RuntimeException e) {
            Profiler.functionError(callerInfo);
            logger.warn("调用物流网关crossBow组件执行调用拼多多的接口异常:", e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    public String getWpCode() {
        return wpCode;
    }

    public void setWpCode(String wpCode) {
        this.wpCode = wpCode;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
