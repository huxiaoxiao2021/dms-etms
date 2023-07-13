package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;
/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerCountData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * @See com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum
	 * 异常状态:0：待分配 1：未完成 2：处理中 3：已完成  4:超时未完成
	 */
	private Integer status;	
	/**
	 * 异常状态-名称
	 */
	private String statusName;		
	/**
	 * 数量
	 */
	private Integer dataNum;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Integer getDataNum() {
		return dataNum;
	}
	public void setDataNum(Integer dataNum) {
		this.dataNum = dataNum;
	}
}
