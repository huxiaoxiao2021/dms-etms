package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;

/**
 * 工序统计信息
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class WorkStationCountVo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 作业区数量
	 */
	private Integer areaNum;
	/**
	 * 工序数量
	 */
	private Integer workNum;
	
	public Integer getAreaNum() {
		return areaNum;
	}
	public void setAreaNum(Integer areaNum) {
		this.areaNum = areaNum;
	}
	public Integer getWorkNum() {
		return workNum;
	}
	public void setWorkNum(Integer workNum) {
		this.workNum = workNum;
	}
}
