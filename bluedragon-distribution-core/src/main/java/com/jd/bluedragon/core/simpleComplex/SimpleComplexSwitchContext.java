package com.jd.bluedragon.core.simpleComplex;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 简繁切换上下文
 *
 * @author hujiping
 * @date 2023/7/28 10:36 AM
 */
@Slf4j
public class SimpleComplexSwitchContext {

    // 简繁切换标识
    public static final String SIMPLE_COMPLEX_SWITCH_FLAG = "simpleComplexSwitchFlag";
    // 简体繁体类型
    public static final Integer SIMPLE_TYPE = 1;
    public static final Integer COMPLEX_TYPE = 2;
    // 简繁切换标识对应value
    public static final String SIMPLE = "simple";
    public static final String COMPLEX = "complex";
    
    private static final ThreadLocal<SimpleComplexGatewayInfo> gatewayTreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<SimpleComplexRestInfo> restTreadLocal = new ThreadLocal<>();

    public static void addJsfThreadInfo(SimpleComplexGatewayInfo simpleComplexInfo) {
        if (simpleComplexInfo == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("设置simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        gatewayTreadLocal.set(simpleComplexInfo);
    }

    public static void addRestThreadInfo(SimpleComplexRestInfo simpleComplexInfo) {
        if (simpleComplexInfo == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("设置simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        restTreadLocal.set(simpleComplexInfo);
    }

    public static void clearJsfThreadInfo() {
        if (log.isDebugEnabled()) {
            SimpleComplexGatewayInfo simpleComplexInfo = gatewayTreadLocal.get();
            log.debug("清除simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        gatewayTreadLocal.remove();
    }

    public static void clearRestThreadInfo() {
        if (log.isDebugEnabled()) {
            SimpleComplexRestInfo simpleComplexInfo = restTreadLocal.get();
            log.debug("清除simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        restTreadLocal.remove();
    }

    public static SimpleComplexGatewayInfo getJsfThreadInfo() {
        SimpleComplexGatewayInfo simpleComplexInfo = gatewayTreadLocal.get();
        if (log.isDebugEnabled()) {
            log.debug("获取simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        return simpleComplexInfo;
    }

    public static SimpleComplexRestInfo getRestThreadInfo() {
        SimpleComplexRestInfo simpleComplexInfo = restTreadLocal.get();
        if (log.isDebugEnabled()) {
            log.debug("获取simpleComplexInfo 线程：{} 内容：{}", Thread.currentThread().getName(), JSON.toJSONString(simpleComplexInfo));
        }
        return simpleComplexInfo;
    }

    @Data
    public static class SimpleComplexGatewayInfo {
        // 简繁切换标识
        private String simpleComplexFlag;
    }

    @Data
    public static class SimpleComplexRestInfo {
        // 简繁切换标识
        private String simpleComplexFlag;
    }
}
