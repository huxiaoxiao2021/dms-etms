package com.jd.bluedragon.distribution.task.asynBuffer.jmq;

import java.util.HashMap;
import java.util.Map;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.framework.asynBuffer.producer.jmq.JmqTopicRouter;

/**
 * 本系统中的Tbschedule的task type与jmq的topic对应关系的路由器。
 * @author yangwubing
 *
 */
public class TaskJmqTopicRouter implements JmqTopicRouter<Task> {
	
	/**
	 * task任务类型与jmq的topic映射关系。
	 */
	private Map<Integer /** task type **/, String /** topic **/> routerMap = new HashMap<Integer, String>();
	
	public TaskJmqTopicRouter(Map<Integer, String> routerMap) {
		super();
		this.routerMap = routerMap;
	}


	@Override
	public String getTopic(Task task) {
		//检查参数。
		if(task==null || task.getType()==null){
			return null;
		}
		return routerMap.get(task.getType());
	}

}
