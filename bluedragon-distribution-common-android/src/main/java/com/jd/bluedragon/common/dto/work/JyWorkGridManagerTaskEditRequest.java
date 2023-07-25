package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

/**
 * @ClassName: JyWorkGridManagerTaskEditRequest
 * @Description: 任务管理-查询请求
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerTaskEditRequest extends BaseReq implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 任务数据
	 */
	private JyWorkGridManagerData taskData;
	
	public JyWorkGridManagerData getTaskData() {
		return taskData;
	}
	public void setTaskData(JyWorkGridManagerData taskData) {
		this.taskData = taskData;
	}

	
}
