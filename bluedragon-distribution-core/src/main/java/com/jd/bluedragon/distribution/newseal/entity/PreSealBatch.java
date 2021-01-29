package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: PreSealBatch
 * @Description: 预封车批次数据表-实体类
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public class PreSealBatch implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 预封车UUID,对应pre_seal_vehicle表
	 */
	private String preSealUuid;

	/**
	 * 批次号
	 */
	private String batchCode;

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
	 * @param preSealUuid
	 */
	public void setPreSealUuid(String preSealUuid) {
		this.preSealUuid = preSealUuid;
	}

	/**
	 *
	 * @return preSealUuid
	 */
	public String getPreSealUuid() {
		return this.preSealUuid;
	}

	/**
	 *
	 * @param batchCode
	 */
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	/**
	 *
	 * @return batchCode
	 */
	public String getBatchCode() {
		return this.batchCode;
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
