package com.jd.bluedragon.distribution.asynbuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jd.ql.framework.asynBuffer.comsumer.DynamicJmqComsumer;

/**
 * 
 * @ClassName: AsynBufferTaskManager
 * @Description: 异步缓冲任务管理,监听spring事件，启动、停止任务
 * @author: wuyoude
 * @date: 2020年3月9日 下午4:34:28
 *
 */
public class AsynBufferTaskManager implements ApplicationListener{
	
	private static final Logger logger = LoggerFactory.getLogger(AsynBufferTaskManager.class);
	
	private DynamicJmqComsumer dynamicJmqComsumer;
	
	public AsynBufferTaskManager(){
		logger.info("AsynBufferTask build...");
	}
	public void init(){
		logger.info("AsynBufferTask init...");
	}
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			//监听初始化、刷新事件，启动异步缓冲任务
			if (event instanceof ContextRefreshedEvent) {
				logger.warn("AsynBufferTask start...");
				dynamicJmqComsumer.start();
				logger.warn("AsynBufferTask start end!");
			}
		} catch (Exception e) {
			logger.error("AsynBufferTask start error，原因：" + e.getMessage(),e);
		}
		try {
			//监听关闭事件，停止异步缓冲任务
			if (event instanceof ContextClosedEvent) {
				logger.warn("AsynBufferTask stop...");
				dynamicJmqComsumer.stop();
				logger.warn("AsynBufferTask stop end!");
			}
		} catch (Exception e) {
			logger.error("AsynBufferTask stop error，原因：" + e.getMessage(),e);
		}
	}
	
	/**
	 * @return the dynamicJmqComsumer
	 */
	public DynamicJmqComsumer getDynamicJmqComsumer() {
		return dynamicJmqComsumer;
	}
	/**
	 * @param dynamicJmqComsumer the dynamicJmqComsumer to set
	 */
	public void setDynamicJmqComsumer(DynamicJmqComsumer dynamicJmqComsumer) {
		this.dynamicJmqComsumer = dynamicJmqComsumer;
	}
}
