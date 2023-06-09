package com.jd.bluedragon.distribution.jy.dto.work;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: JyBizTaskWorkGridManagerCount
 * @Description: 巡检任务表-实体类
 * @author wuyoude
 * @date 2023年06月08日 11:27:02
 *
 */
public class JyBizTaskWorkGridManagerCount implements Serializable {

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
