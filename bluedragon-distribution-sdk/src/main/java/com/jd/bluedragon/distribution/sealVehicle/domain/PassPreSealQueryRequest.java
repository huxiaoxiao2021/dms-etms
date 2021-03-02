package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
/**
 * 传摆预封车看板查询条件
 * @author wuyoude
 *
 */
public class PassPreSealQueryRequest extends BasePagerCondition implements Serializable{

	private static final long serialVersionUID = -1988125431070900175L;
	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	/**
	 * 始发地编码
	 */
	private Integer originalSiteCode;
	/**
	 * 目的地编码|名称
	 */
	private String destinationSiteCodeOrName;
	/**
	 * 目的站点编码
	 */
	private Integer destinationSiteCode;
	/**
	 * 目的站点名称
	 */
	private String destinationSiteName;
	/**
	 * 目的滑道号
	 */
	private String destinationCrossCode;

	/**
	 * 最近小时数
	 */
	private Integer recentHours;
	/**
	 * 查询数据条数
	 */
	private Integer limitNum = 10;
	/**
	 * 发货关系开始时间
	 */
	private Date effectStartTime;
	/**
	 * 发车时间-开始
	 */
	private Date departStartTime;
	/**
	 * 发车时间-结束
	 */
	private Date departEndTime;
	/**
	 * 任务创建时间-开始
	 */
	private Date jobCreateStartTime;
	/**
	 * 任务创建时间-结束
	 */
	private Date jobCreateEndTime;	
	/**
	 * 排序信息
	 */
	private List<Map<String,String>> orders;
	/**
	 * 过滤线路类型
	 */
	private List<Integer> lineTypes;
	/**
	 * 排序信息
	 */
	private String orderBy;
	
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public Integer getOriginalSiteCode() {
		return originalSiteCode;
	}
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
	}
	public String getDestinationSiteCodeOrName() {
		return destinationSiteCodeOrName;
	}
	public void setDestinationSiteCodeOrName(String destinationSiteCodeOrName) {
		this.destinationSiteCodeOrName = destinationSiteCodeOrName;
	}
	public String getDestinationCrossCode() {
		return destinationCrossCode;
	}
	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}
	public Integer getRecentHours() {
		return recentHours;
	}
	public void setRecentHours(Integer recentHours) {
		this.recentHours = recentHours;
	}
	public Integer getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}
	public Integer getDestinationSiteCode() {
		return destinationSiteCode;
	}
	public void setDestinationSiteCode(Integer destinationSiteCode) {
		this.destinationSiteCode = destinationSiteCode;
	}
	public String getDestinationSiteName() {
		return destinationSiteName;
	}
	public void setDestinationSiteName(String destinationSiteName) {
		this.destinationSiteName = destinationSiteName;
	}
	public Date getEffectStartTime() {
		return effectStartTime;
	}
	public void setEffectStartTime(Date effectStartTime) {
		this.effectStartTime = effectStartTime;
	}
	public Date getDepartStartTime() {
		return departStartTime;
	}
	public void setDepartStartTime(Date departStartTime) {
		this.departStartTime = departStartTime;
	}
	public Date getDepartEndTime() {
		return departEndTime;
	}
	public void setDepartEndTime(Date departEndTime) {
		this.departEndTime = departEndTime;
	}
	public Date getJobCreateStartTime() {
		return jobCreateStartTime;
	}
	public void setJobCreateStartTime(Date jobCreateStartTime) {
		this.jobCreateStartTime = jobCreateStartTime;
	}
	public Date getJobCreateEndTime() {
		return jobCreateEndTime;
	}
	public void setJobCreateEndTime(Date jobCreateEndTime) {
		this.jobCreateEndTime = jobCreateEndTime;
	}
	public List<Map<String, String>> getOrders() {
		return orders;
	}
	public void setOrders(List<Map<String, String>> orders) {
		this.orders = orders;
	}
	public List<Integer> getLineTypes() {
		return lineTypes;
	}
	public void setLineTypes(List<Integer> lineTypes) {
		this.lineTypes = lineTypes;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
