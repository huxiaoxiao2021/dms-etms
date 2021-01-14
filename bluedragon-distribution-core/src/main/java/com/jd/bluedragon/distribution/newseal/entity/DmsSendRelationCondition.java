package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * @ClassName: DmsSendRelation
 * @Description: 分拣发货关系表-查询条件实体类
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public class DmsSendRelationCondition implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 始发站点编码
	 */
	private Integer originalSiteCode;
	/**
	 * 目的站点编码
	 */
	private Integer destinationSiteCode;
	/**
	 * 目的站点名称
	 */
	private String destinationSiteName;
	/**
	 * 目的道口号
	 */
	private String destinationCrossCode;

	/**
	 * 线路类型列表
	 */
	private List<Integer> lineTypes;
	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 查询数据条数
	 */
	private Integer limitNum = 10;
	
	public Integer getOriginalSiteCode() {
		return originalSiteCode;
	}
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
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
	public String getDestinationCrossCode() {
		return destinationCrossCode;
	}
	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}
	public List<Integer> getLineTypes() {
		return lineTypes;
	}
	public void setLineTypes(List<Integer> lineTypes) {
		this.lineTypes = lineTypes;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Integer getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}
}
