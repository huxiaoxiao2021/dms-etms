package com.jd.bluedragon.configuration.ducc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jd.laf.config.spring.annotation.LafUcc;

/**
 * 配置解释 https://cf.jd.com/pages/viewpage.action?pageId=575172590
 * @program: bluedragon-distribution
 * @description: hystrix 动态配置
 * @author: xumigen
 * @create: 2021-08-10 17:33
 **/
@Component("duccHystrixRoutePropertyConfig")
public class DuccHystrixRoutePropertyConfig{

	@Value("${duccHystrixRoutePropertyConfig.executionTimeoutInMilliseconds:2000}")
	@LafUcc
    private int executionTimeoutInMilliseconds;
	@Value("${duccHystrixRoutePropertyConfig.circuitBreakerEnabled:true}")
	@LafUcc
    private boolean fallbackEnabled;
	@Value("${duccHystrixRoutePropertyConfig.fallbackEnabled:true}")
	@LafUcc
    private boolean circuitBreakerEnabled;
	@Value("${duccHystrixRoutePropertyConfig.circuitBreakerErrorThresholdPercentage:100}")
	@LafUcc
    private int circuitBreakerErrorThresholdPercentage;
	@Value("${duccHystrixRoutePropertyConfig.circuitBreakerForceOpen:false}")
	@LafUcc
    private boolean circuitBreakerForceOpen;
	@Value("${duccHystrixRoutePropertyConfig.circuitBreakerSleepWindowInMilliseconds:120000}")
	@LafUcc
    private int circuitBreakerSleepWindowInMilliseconds;
	@Value("${duccHystrixRoutePropertyConfig.maximumSize:4}")
	@LafUcc
    private int maximumSize;
	@Value("${duccHystrixRoutePropertyConfig.circuitBreakerRequestVolumeThreshold:6}")
	@LafUcc
    private int circuitBreakerRequestVolumeThreshold;


    public int getExecutionTimeoutInMilliseconds() {
        return executionTimeoutInMilliseconds;
    }

    public void setExecutionTimeoutInMilliseconds(int executionTimeoutInMilliseconds) {
        this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }

    public boolean isCircuitBreakerEnabled() {
        return circuitBreakerEnabled;
    }

    public void setCircuitBreakerEnabled(boolean circuitBreakerEnabled) {
        this.circuitBreakerEnabled = circuitBreakerEnabled;
    }

    public int getCircuitBreakerErrorThresholdPercentage() {
        return circuitBreakerErrorThresholdPercentage;
    }

    public void setCircuitBreakerErrorThresholdPercentage(int circuitBreakerErrorThresholdPercentage) {
        this.circuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
    }

    public boolean isCircuitBreakerForceOpen() {
        return circuitBreakerForceOpen;
    }

    public void setCircuitBreakerForceOpen(boolean circuitBreakerForceOpen) {
        this.circuitBreakerForceOpen = circuitBreakerForceOpen;
    }

    public int getCircuitBreakerSleepWindowInMilliseconds() {
        return circuitBreakerSleepWindowInMilliseconds;
    }

    public void setCircuitBreakerSleepWindowInMilliseconds(int circuitBreakerSleepWindowInMilliseconds) {
        this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public int getCircuitBreakerRequestVolumeThreshold() {
        return circuitBreakerRequestVolumeThreshold;
    }

    public void setCircuitBreakerRequestVolumeThreshold(int circuitBreakerRequestVolumeThreshold) {
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
    }
}
