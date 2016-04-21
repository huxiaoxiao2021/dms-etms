package com.jd.bluedragon.distribution.popReveice.service;

import java.util.List;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.domain.TaskPopRecieveCount;
/**
 * 
* 类描述： POP收货回传消息service
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-2-28 上午10:42:58
 */
public interface TaskPopRecieveCountService {
	public int insert(TaskPopRecieveCount taskPopRecieveCount);
	public int insert(Inspection inspection);
	public int insert(PopReceive popReceive);
	public int update(TaskPopRecieveCount taskPopRecieveCount);
	public TaskPopRecieveCount getTaskPopRevieveCountById(Long taskId);
	public List<TaskPopRecieveCount> getTaskPopRevieveCountByWaybillCode(String waybillCode);
	public List<TaskPopRecieveCount> findLimitedTasks(Integer type, Integer fetchNum, String ownSign);
	public void sendMessage(List<TaskPopRecieveCount> data) throws Exception;
}
