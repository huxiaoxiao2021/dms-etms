package com.jd.bluedragon.common.mutilthread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;


public class ThreadPoolExecutorConfig  implements FactoryBean<ExecutorService>, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolExecutorConfig.class);
    private ThreadPoolExecutor  executor;
    private int corePoolSize = Runtime.getRuntime().availableProcessors()*2;;
    private int maxPoolSize = Runtime.getRuntime().availableProcessors()*3;
    private int keepAliveSeconds = 60;
    private int queueCapacity = 500;
    private String threadName = "dms-task-executor";


    @Override
    public void destroy() throws Exception {
        if (!executor.isTerminated()){
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                executor.shutdownNow();
            }
        }
        System.out.println("ThreadPoolExecutor destroyed !");

    }

    @Override
    public ExecutorService getObject() throws Exception {
        return executor;
    }

    @Override
    public Class<?> getObjectType() {
        return this.executor != null ? this.executor.getClass() : ExecutorService.class;
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
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                keepAliveSeconds, TimeUnit.SECONDS, queue, namedThreadFactory,new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
