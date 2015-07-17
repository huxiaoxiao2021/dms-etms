package com.jd.bluedragon.distribution.popReveice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPrint.dao.PopSigninDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;
import com.jd.bluedragon.distribution.popReveice.dao.TaskPopRecieveCountDao;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.domain.TaskPopRecieveCount;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.message.produce.client.MessageClient;

/**
 * 
* 类描述： POP实收中间表操作类
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-4-8 下午1:52:32
* 版本号： v1.0
 */
@Service
public class TaskPopRecieveCountServiceImpl implements TaskPopRecieveCountService {
	//	private static Log log = LogFactory.getLog(TaskPopRecieveCountServiceImpl.class);
	private static Log messageLog = LogFactory.getLog("com.jd.etms.message");
	@Autowired
	private TaskPopRecieveCountDao taskPopRecieveCountDao;
	@Autowired
	PopSigninDao popSigninDao;
	@Autowired
	private MessageClient messageClient;
	private String sendKey = "bd_pop_receivecount";

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	@Profiled(tag = "TaskPopRecieveCountService.insert(taskPopRecieveCount)")
	public int insert(TaskPopRecieveCount taskPopRecieveCount) {
		return this.taskPopRecieveCountDao.insert(taskPopRecieveCount);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	@Profiled(tag = "TaskPopRecieveCountService.update")
	public int update(TaskPopRecieveCount taskPopRecieveCount) {
		return this.taskPopRecieveCountDao.update(taskPopRecieveCount);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Profiled(tag = "TaskPopRecieveCountService.getTaskPopRevieveCountById")
	public TaskPopRecieveCount getTaskPopRevieveCountById(Long taskId) {
		return this.taskPopRecieveCountDao.getTaskPopRevieveCountById(taskId);
	}

	@Override
	@Profiled(tag = "TaskPopRecieveCountService.getTaskPopRevieveCountByWaybillCode")
	public List<TaskPopRecieveCount> getTaskPopRevieveCountByWaybillCode(String waybillCode) {
		return this.taskPopRecieveCountDao.getTaskPopRevieveCountByWaybillCode(waybillCode);
	}

	/**
	 * 
	* 方法描述 : POP托寄收货
	* 创建者：libin 
	* 类名： TaskPopRecieveCountServiceImpl.java
	* 创建时间： 2013-2-28 上午11:14:33
	* @param popReceive
	* @return int
	 */
	@Override
	@Profiled(tag = "TaskPopRecieveCountService.insert(popReceive)")
	public int insert(PopReceive popReceive) {
		int n = 0;
		TaskPopRecieveCount taskPopRecieveCount = new TaskPopRecieveCount();
		taskPopRecieveCount.setWaybillCode(popReceive.getWaybillCode());
		PopSigninDto popSigninDto = new PopSigninDto();
		popSigninDto.setThirdWaybillCode(popReceive.getThirdWaybillCode());
		List<PopSignin> signList = popSigninDao.getPopSigninList(popSigninDto);
		Integer originalNum = popReceive.getOriginalNum();//应收包裹数
		if(originalNum==null){
			originalNum=0;
		}
		PopSignin popSignin = null;
		if (signList != null && (!signList.isEmpty())) {
			popSignin = signList.get(0);
		}
		if (popSignin != null) {
			taskPopRecieveCount.setExpressCode(popSignin.getExpressCode());
			taskPopRecieveCount.setExpressName(popSignin.getExpressName());
		}
		taskPopRecieveCount.setOperateTime(popReceive.getOperateTime());

		List<TaskPopRecieveCount> oldDataList = this.getTaskPopRevieveCountByWaybillCode(popReceive.getWaybillCode());
		if (oldDataList == null || oldDataList.size() < 1) {
			taskPopRecieveCount.setTaskStatus(0);
			taskPopRecieveCount.setExecuteCount(0);
			taskPopRecieveCount.setTaskType(1040);

			taskPopRecieveCount.setOwnSign(getOwnSign());
			taskPopRecieveCount.setActualNum(popReceive.getActualNum());
			if (originalNum != null) {
				taskPopRecieveCount.setThirdWaybillCode(popReceive.getThirdWaybillCode() + "#"
						+ popReceive.getActualNum());
			} 
			n = this.insert(taskPopRecieveCount);
		} else {
			TaskPopRecieveCount oldObj = oldDataList.get(0);
			Integer actualNum = 0;
			String[] thirdWaybillCodeArray = oldObj.getThirdWaybillCode().split(",");
			boolean flag = true;
			String newThirdWaybillCode = null;
			for (String thirdWaybillCodeStr : thirdWaybillCodeArray) {
				String[] str = thirdWaybillCodeStr.split("#");
				String thirdWaybillCode = str[0];
				String num = str[1];
				if (thirdWaybillCode.equals(popReceive.getThirdWaybillCode())) {
					newThirdWaybillCode = oldObj.getThirdWaybillCode().replace(thirdWaybillCode + "#" + num,
							thirdWaybillCode + "#" + popReceive.getActualNum());
					flag = false;
					actualNum = oldObj.getActualNum() - Integer.parseInt(num);
					if (actualNum > 0) {
						actualNum = actualNum + popReceive.getActualNum();
					} else {
						actualNum = popReceive.getActualNum();
					}
					break;
				}
			}
			if (flag) {
				newThirdWaybillCode = oldObj.getThirdWaybillCode() + "," + popReceive.getThirdWaybillCode() + "#"
						+ popReceive.getActualNum();
				actualNum = oldObj.getActualNum() + popReceive.getActualNum();
			}
			taskPopRecieveCount.setActualNum(actualNum);
			taskPopRecieveCount.setThirdWaybillCode(newThirdWaybillCode);
			Long taskId = oldObj.getTaskId();
			taskPopRecieveCount.setTaskId(taskId);
			n = this.update(taskPopRecieveCount);
		}
		return n;
	}

	@Override
	@Profiled(tag = "TaskPopRecieveCountService.insert(inspection)")
	public int insert(Inspection inspection) {
		int n = 0;
		TaskPopRecieveCount taskPopRecieveCount = new TaskPopRecieveCount();
		taskPopRecieveCount.setWaybillCode(inspection.getWaybillCode());
		taskPopRecieveCount.setOperateTime(inspection.getCreateTime());
		taskPopRecieveCount.setActualNum(inspection.getQuantity());
		List<TaskPopRecieveCount> oldDataList = this.getTaskPopRevieveCountByWaybillCode(inspection.getWaybillCode());
		if (oldDataList == null || oldDataList.size() < 1) {
			taskPopRecieveCount.setTaskStatus(0);
			taskPopRecieveCount.setExecuteCount(0);
			taskPopRecieveCount.setTaskType(1040);
			taskPopRecieveCount.setOwnSign(getOwnSign());
			n = this.insert(taskPopRecieveCount);
		} else {
			TaskPopRecieveCount oldObj = oldDataList.get(0);
			Long taskId = oldObj.getTaskId();
			taskPopRecieveCount.setTaskId(taskId);
			n = this.update(taskPopRecieveCount);
		}
		return n;
	}

	@Override
	@Profiled(tag = "TaskPopRecieveCountService.findLimitedTasks")
	public List<TaskPopRecieveCount> findLimitedTasks(Integer type, Integer fetchNum, String ownSign) {
		List<TaskPopRecieveCount> dataList = this.taskPopRecieveCountDao.findLimitedTasks(type, fetchNum, ownSign);
		return dataList;
	}

	public void sendMessage(List<TaskPopRecieveCount> data) {
		if (data == null) {
			return;
		}
		List<PopRecieveCountMessage> messageList = new ArrayList<PopRecieveCountMessage>();
		for (TaskPopRecieveCount taskPopRecieveCount : data) {
			PopRecieveCountMessage message = new PopRecieveCountMessage();
			message.setSerialNumber(taskPopRecieveCount.getTaskId());
			message.setWaybillCode(taskPopRecieveCount.getWaybillCode());
			message.setActualNum(taskPopRecieveCount.getActualNum());
			if (StringUtils.isEmpty(taskPopRecieveCount.getExpressName())) {
				message.setExpressName("");
			} else {
				message.setExpressName(taskPopRecieveCount.getExpressName());
			}
			message.setOperateTime(DateHelper.formatDateTime(taskPopRecieveCount.getOperateTime()));
			String thirdWaybillCodeStr = taskPopRecieveCount.getThirdWaybillCode();
			if (StringUtils.isNotEmpty(thirdWaybillCodeStr)) {
				String[] str = thirdWaybillCodeStr.split(",");
				if (str.length == 1) {
					String[] tempArray = str[0].split("#");
					message.setThirdWaybillCode(tempArray[0]);
				} else {
					StringBuffer temp = new StringBuffer();
					for (String s : str) {
						String ss = s.split("#")[0];
						temp.append(ss).append(",");
					}
					String thirdWaybillCodes = temp.toString();
					if (thirdWaybillCodes.endsWith(",")) {
						thirdWaybillCodes = thirdWaybillCodes.substring(0, temp.length() - 1);
					}
					message.setThirdWaybillCode(thirdWaybillCodes);
				}

			} else {
				message.setThirdWaybillCode("");
			}

			messageList.add(message);
		}
		String jsonStr = JsonHelper.toJson(messageList);
		messageLog.info("向POP回传收货信息【" + jsonStr + "】,key：" + this.sendKey);
		messageClient.sendMessage(this.sendKey, jsonStr, null);
		messageLog.info("向POP回传收货信息【" + jsonStr + "】成功");

	}

	public String getOwnSign() {
		String ownSign = BusinessHelper.getOwnSign();
		if (StringUtils.isEmpty(ownSign)) {
			ownSign = "DMS";
		}
		return ownSign;

	}

}

class PopRecieveCountMessage {
	Long serialNumber;
	public String waybillCode;
	public String expressName;
	public String thirdWaybillCode;
	public Integer actualNum;
	public String operateTime;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public Integer getActualNum() {
		return actualNum;
	}

	public void setActualNum(Integer actualNum) {
		this.actualNum = actualNum;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public Long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}

}
