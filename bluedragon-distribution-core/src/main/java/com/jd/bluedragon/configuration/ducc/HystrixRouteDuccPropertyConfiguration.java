package com.jd.bluedragon.configuration.ducc;

/**
 * @program: bluedragon-distribution
 * @description: hystrix 动态配置
 * @author: xumigen
 * @create: 2021-08-10 17:33
 **/
public class HystrixRouteDuccPropertyConfiguration {


    private int executionTimeoutInMilliseconds;

    private boolean fallbackEnabled;

    private boolean circuitBreakerEnabled;

    private int circuitBreakerErrorThresholdPercentage;

    private boolean circuitBreakerForceOpen;

    private int circuitBreakerSleepWindowInMilliseconds;

    private int maximumSize;

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
