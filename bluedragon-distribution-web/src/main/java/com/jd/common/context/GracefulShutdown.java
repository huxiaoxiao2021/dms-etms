package com.jd.common.context;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.jsf.gd.util.JSFContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class GracefulShutdown implements ApplicationListener<ContextRefreshedEvent> {
    public static final Logger logger = LoggerFactory.getLogger(GracefulShutdown.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("ContextRefreshedEvent...");
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){ //root application context 没有parent
            /**
             * 优雅停机主要处理的资源：
             *  池化资源的释放：线程池等，保证线程池的任务执行完毕
             * 在处理线程的释放：已经被连接的HTTP请求：servlet请求
             * mq消费者的处理：正在处理的消息
             * RPC(JSF)的优雅关闭
             */
            final ThreadPoolTaskExecutor threadPoolTaskExecutor =(ThreadPoolTaskExecutor) SpringHelper.getBean("taskExecutor");


            Thread thread =new Thread(){
                @Override
                public void run() {
                    logger.info("dms web system will exit ...");
                    threadPoolTaskExecutor.shutdown();//线程池拒接收新提交的任务，同时等待线程池里的任务执行完毕后关闭线程池。
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //JSFContext.destroy(false);杰夫自己已经借助钩子实现
                    logger.info("dms web system  exited ...");
                }
            };
            thread.setPriority(10);
            Runtime.getRuntime().addShutdownHook(thread);
        }
    }
}
