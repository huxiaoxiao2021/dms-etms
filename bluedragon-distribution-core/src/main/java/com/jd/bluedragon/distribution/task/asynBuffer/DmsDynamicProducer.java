package com.jd.bluedragon.distribution.task.asynBuffer;

import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.producer.AbstractProducer;
import com.jd.ql.framework.asynBuffer.producer.DynamicProducer;
import com.jd.ql.framework.asynBuffer.producer.ProducerType;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * dms系统的动态消息生产者扩展，支持动态配置更新。
 * @author yangwubing
 */
public class DmsDynamicProducer extends DynamicProducer<Task> {

	@Resource
	private DmsConfigManager dmsConfigManager;

	/**
	 * 正式启用了AsynBuffer组建的任务类型列表，若不在其中的任务类型则还是采用原来的Tbschedule方式处理任务。
	 */
	public String getEnabledTypes() {
		return dmsConfigManager.getPropertyConfig().getAsynbufferEnabledTaskType();
	}


	/**
	 * 获取不开启jmq模式的task类型
	 * 配置规则：taskType-keyword1；taskType-keyword1
	 * 如：1300-1；1300-2；1400-5
	 * @return
     */
	public Set<String> getNotEnabledKeyWord(){
		String [] notEnabledKeyWords = dmsConfigManager.getPropertyConfig().getAsynBufferNotenabledTaskKeyword1().trim().split(";");
		Set<String> type_keyword = new HashSet<String>();
		for(String s : notEnabledKeyWords){
			type_keyword.add(s);
		}
		return type_keyword;
	}

	@Override
	public ProducerType getProducerType() {
		ProducerType producerType = ProducerType.valueOf(dmsConfigManager.getPropertyConfig().getAsynBufferDynamicProducerProducerType());
		this.setProducerType(producerType);
		return producerType;
	}


	public DmsDynamicProducer() {
		super();
	}

	@Override
	public AbstractProducer<Task> selectProducer(Task message) {
		if(checkEnabled(message))
		{
			return super.selectProducer(message);
			
		}
		//未启用的任务类型还采用原始的tbschedule生产者。
		return this.getTbscheduleProducer();
	}

		private boolean checkEnabled(Task message){
		String types = this.getEnabledTypes();
		Set<String> type_keyword = this.getNotEnabledKeyWord();

		if(types==null || types.length()==0 || message==null || message.getType()==null){
			return false;
		}

		String[] sa = types.split(";");
		for(String s:sa){
			String taskType = message.getType().toString();
			if(s.equals(taskType)){
				if(type_keyword.contains(message.getType()+"-"+message.getKeyword1())){
					return false;
				}
				return true;
			}
		}
		return false;
	}
}
