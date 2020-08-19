package com.jd.bluedragon.distribution.ver.domain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.product.domain.Product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ver用Waybill
 */
public class WaybillForVer extends Waybill implements Serializable {

	private static final long serialVersionUID = 5163001426091767256L;


	/**
	 * 运单类型,Cachem发送的CWaybill类MQ,是waybillType, 当前系统的是type, 都指运单类型
	 */
	private Integer waybillType;


	/**
	 * 体积
	 */
	private Double volume;

	/**
	 * 配送方式
	 */
	private Integer distributeType;



	/*路区号*/
	private String roadCode;


	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 当前运单数据归属的表名
	 */
	private String tableName;


	/** 任务特性专用字段 start **/

	/**
	 * 任务执行状态
	 **/
	private Integer taskStatus;

	/**
	 * 任务执行次数
	 **/
	private Integer executeCount;

	/**
	 * 任务执行时间
	 **/
	private Date executeTime;

	/**
	 * 当前发货批次对应的目的分拣中心  这个字段用于本地ver分拣中心过滤属于自己的运单数据  added by zhanglei at 20161009
	 */
	private String dmsDisCode;

	/**
	 * 复重
	 */
	private Double againWeight;

	/**
	 * 复量方
	 */
	private String spareColumn2;

	/**
	 * 运费
	 */
	private String freight;

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Integer getDistributeType() {
		return distributeType;
	}

	public void setDistributeType(Integer distributeType) {
		this.distributeType = distributeType;
	}

	public String getRoadCode() {
		return roadCode;
	}

	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getExecuteCount() {
		return executeCount;
	}

	public void setExecuteCount(Integer executeCount) {
		this.executeCount = executeCount;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getDmsDisCode() {
		return dmsDisCode;
	}

	public void setDmsDisCode(String dmsDisCode) {
		this.dmsDisCode = dmsDisCode;
	}

	public Double getAgainWeight() {
		return againWeight;
	}

	public void setAgainWeight(Double againWeight) {
		this.againWeight = againWeight;
	}

	public String getSpareColumn2() {
		return spareColumn2;
	}

	public void setSpareColumn2(String spareColumn2) {
		this.spareColumn2 = spareColumn2;
	}

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}


}
