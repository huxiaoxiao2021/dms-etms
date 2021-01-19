package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: DmsSendRelation
 * @Description: 分拣发货关系表-实体类
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public class DmsSendRelation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 始发站点编码
	 */
	private Integer originalSiteCode;

	/**
	 * 始发站点名称
	 */
	private String originalSiteName;

	/**
	 * 始发道口号
	 */
	private String originalCrossCode;

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
	 * 线路类型:10-干线，20-支线，30-传站，31-长途传站，40-摆渡
	 */
	private Integer lineType;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 有效标识：1-有效 0-无效
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param originalSiteCode
	 */
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
	}

	/**
	 *
	 * @return originalSiteCode
	 */
	public Integer getOriginalSiteCode() {
		return this.originalSiteCode;
	}

	/**
	 *
	 * @param originalSiteName
	 */
	public void setOriginalSiteName(String originalSiteName) {
		this.originalSiteName = originalSiteName;
	}

	/**
	 *
	 * @return originalSiteName
	 */
	public String getOriginalSiteName() {
		return this.originalSiteName;
	}

	/**
	 *
	 * @param originalCrossCode
	 */
	public void setOriginalCrossCode(String originalCrossCode) {
		this.originalCrossCode = originalCrossCode;
	}

	/**
	 *
	 * @return originalCrossCode
	 */
	public String getOriginalCrossCode() {
		return this.originalCrossCode;
	}

	/**
	 *
	 * @param destinationSiteCode
	 */
	public void setDestinationSiteCode(Integer destinationSiteCode) {
		this.destinationSiteCode = destinationSiteCode;
	}

	/**
	 *
	 * @return destinationSiteCode
	 */
	public Integer getDestinationSiteCode() {
		return this.destinationSiteCode;
	}

	/**
	 *
	 * @param destinationSiteName
	 */
	public void setDestinationSiteName(String destinationSiteName) {
		this.destinationSiteName = destinationSiteName;
	}

	/**
	 *
	 * @return destinationSiteName
	 */
	public String getDestinationSiteName() {
		return this.destinationSiteName;
	}

	/**
	 *
	 * @param destinationCrossCode
	 */
	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}

	/**
	 *
	 * @return destinationCrossCode
	 */
	public String getDestinationCrossCode() {
		return this.destinationCrossCode;
	}

	/**
	 *
	 * @param lineType
	 */
	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	/**
	 *
	 * @return lineType
	 */
	public Integer getLineType() {
		return this.lineType;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
	}

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}


}
