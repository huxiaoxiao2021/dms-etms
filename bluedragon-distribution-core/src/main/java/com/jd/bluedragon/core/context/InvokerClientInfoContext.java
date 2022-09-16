package com.jd.bluedragon.core.context;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.context
 * @ClassName: InvokerClientInfoContext
 * @Description: 用于存放 rest和 jsf调用进来的客户端的信息
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 20:04
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Slf4j
public class InvokerClientInfoContext {


    private static final ThreadLocal<ClientInfo> invokerClientInfoTreadLocal = new ThreadLocal<>();


    public static void add(String clientIp) {
        ClientInfo clientInfo = invokerClientInfoTreadLocal.get();
        if (clientInfo == null) {
            clientInfo = new ClientInfo();
        }
        clientInfo.setClientIp(clientIp);
        invokerClientInfoTreadLocal.set(clientInfo);
    }

    public static void add(ClientInfo clientInfo) {
        if (clientInfo == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("设置ClientInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(clientInfo));
        }

        invokerClientInfoTreadLocal.set(clientInfo);
    }

    public static void clear() {
        if (log.isDebugEnabled()) {
            ClientInfo clientInfo = invokerClientInfoTreadLocal.get();
            log.debug("清除ClientInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(clientInfo));
        }
        invokerClientInfoTreadLocal.remove();
    }

    public static ClientInfo get() {
        ClientInfo clientInfo = invokerClientInfoTreadLocal.get();
        if (log.isDebugEnabled()) {
            log.debug("获取ClientInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(clientInfo));
        }
        return clientInfo == null? new ClientInfo() : clientInfo;
    }

    @Data
    public static class ClientInfo {

        private String clientIp;

        /**
         * 登录人账号
         */
        private String user;

    }

}
