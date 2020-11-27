package com.jd.bluedragon.core.crossbow;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.crossbow.security.CrossbowSecurityProcessorSelector;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.lop.crossbow.dto.LopRequest;
import com.jd.lop.crossbow.dto.LopResponse;
import com.jd.lop.crossbow.sdk.LopClient;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * <p>
 *     分拣中心调用外部接口的代理类，主要涉及到一些三方公司需要走外网的接口业务
 *     用到的是架构部的crossBow组件：该组件实现了对三方公司接口的统一调用，屏蔽了内部因三方差异二带来的接口协议和网络权限的差异。
 *
 * @see com.jd.bluedragon.external.crossbow.AbstractCrossbowManager 内部对crossbow组件的统一调用封装
 * @link http://lcp.jdwl.com/#/docSoftwareSystem/17/44780 crossbow的外部服务域接入文档
 * @author wuzuxiang
 * @since 2019/10/17
 **/
public class DMSCrossbowClient {

    private static final Logger log = LoggerFactory.getLogger(DMSCrossbowClient.class);

    /* crossbow调用地址 （不同环境使用的url不同,规则: 网关调用地址/execute。示例为测试环境地址） */
    private String serverUrl;

    /* 超时时间 单位毫秒 */
    private Integer timeout;

    /* 服务调用编码 （在网关“接入方维护”功能中配置） */
    private String partnerId;

    /* 接入方密钥 （在网关“接入方维护”功能中配置） */
    private String partnerSecret;

    /* 调用物流网关的客户端 */
    private LopClient client;

    /***
     * 调用crossbow组件客户端的执行方法 目前不涉及加密，如果有需要对接加密的请完善该接口
     * @param crossBowConfig 需要的配置参数
     * @param parameterStr 三方的公司需要用的请求的体body
     * @param urlArgs url参数
     * @return 返回 String类型 根据需要进行转换
     * @throws RuntimeException 如果crossbow调用失败则抛出相应的异常由调用方处理
     */
    @JProfiler(jKey = "dms.core.DMSCrossBowClient.executor", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public String executor(@NotNull CrossbowConfig crossBowConfig, String parameterStr, Map<String, String> urlArgs) throws RuntimeException {

        /* 1. 组装基本参数 */
        LopRequest request = new LopRequest();
        request.setDomain(crossBowConfig.getDomain());
        request.setCustomerId(crossBowConfig.getCustomerId());
        request.setApi(crossBowConfig.getApi());
        request.addUrlArgs(urlArgs);
        request.setAppKey(crossBowConfig.getAppKey());
        request.setBody(parameterStr);

        /* 2. 设置安全插件对应的内容 */
        request = CrossbowSecurityProcessorSelector.getProcessor(crossBowConfig.getSecurityEnum())
                .handleSecurityContent(request, crossBowConfig);

        /* 3. 请求物流网关代理，调用外部接口 */
        LopResponse response = client.execute(request);
        if (response.getStatusCode() != 200) {
            log.warn("调用物流网关crossBow组件失败，参数为：{}, 返回值为: {}", JsonHelper.toJson(request), JsonHelper.toJson(response));
            throw new RuntimeException("调用物流网关crossBow组件失败,返回值：" + JsonHelper.toJson(response));
        }
        return response.getBody();
    }

    /**
     * 默认的构造器
     */
    public DMSCrossbowClient() {}

    /**
     * 构造器
     */
    public DMSCrossbowClient(String serverUrl, Integer timeout, String partnerId, String partnerSecret) {
        this.serverUrl = serverUrl;
        this.timeout = timeout;
        this.partnerId = partnerId;
        this.partnerSecret = partnerSecret;
    }

    /**
     * 初始化 LopClient 的实例
     */
    public void initClient() {
        client = LopClient.getLopClient(serverUrl, timeout, partnerId, partnerSecret);
    }
}
