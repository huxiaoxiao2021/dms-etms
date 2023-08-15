package com.jd.bluedragon.distribution.jy.dto.work;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: JyBizTaskWorkGridManagerBatchUpdate
 * @Description: 巡检任务-分配
 * @author wuyoude
 * @date 2023年06月14日 17:33:11
 *
 */
public class JyBizTaskWorkGridManagerBatchUpdate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private JyBizTaskWorkGridManager data;
	/**
	 * 分配任务列表
	 */
	private List<String> bizIdList;
	/**
	 * 状态列表
	 */
	private List<Integer> statusList;

	public JyBizTaskWorkGridManager getData() {
		return data;
	}

	public void setData(JyBizTaskWorkGridManager data) {
		this.data = data;
	}

	public List<String> getBizIdList() {
		return bizIdList;
	}

	public void setBizIdList(List<String> bizIdList) {
		this.bizIdList = bizIdList;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

}
