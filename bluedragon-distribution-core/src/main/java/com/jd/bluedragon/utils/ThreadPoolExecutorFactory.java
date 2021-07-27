package com.jd.bluedragon.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.*;

public class ThreadPoolExecutorFactory  implements FactoryBean<ExecutorService>,InitializingBean, DisposableBean {

    private ExecutorService exposedExecutor;
    private int corePoolSize = 1;
    private int maxPoolSize = 500;
    private int keepAliveSeconds = 60;
    private int queueCapacity = 2147483647;
    private boolean exposeUnconfigurableExecutor = false;
    private String threadName = "dms-thread";

    @Override
    public ExecutorService getObject() throws Exception {
        return exposedExecutor;
    }

    @Override
    public Class<? extends ExecutorService> getObjectType() {
        return this.exposedExecutor != null ? this.exposedExecutor.getClass() : ExecutorService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadName.concat("-%d")).build();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue(this.queueCapacity);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue, namedThreadFactory);
        this.exposedExecutor = (this.exposeUnconfigurableExecutor ? Executors.unconfigurableExecutorService(executor) : executor);
    }

    @Override
    public void destroy() throws Exception {
        exposedExecutor.shutdownNow();
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
