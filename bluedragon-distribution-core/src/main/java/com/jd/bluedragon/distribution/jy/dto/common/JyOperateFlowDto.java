package com.jd.bluedragon.distribution.jy.dto.common;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: JyOperateFlow
 * @Description: 分拣-业务操作流水表-实体类
 * @author wuyoude
 * @date 2023年09月12日 17:07:43
 *
 */
public class JyOperateFlowDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务类型：目前是表名
	 */
	private String operateBizType;

	/**
	 * 业务子类型
	 */
	private String operateBizSubType;

	/**
	 * 操作业务主键,拆分键
	 */
	private String operateBizKey;

	/**
	 * 操作场地编码
	 */
	private Integer operateSiteCode;

	/**
	 * 操作主键:操作数据id,一般对应表数据的id
	 */
	private String operateKey;

	/**
	 * 操作业务信息,json格式
	 */
	private String operateValue;

	/**
	 * 操作时间
	 */
	private Date operateTime;

	/**
	 * 时间戳
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
	 * @param operateBizType
	 */
	public void setOperateBizType(String operateBizType) {
		this.operateBizType = operateBizType;
	}

	/**
	 *
	 * @return operateBizType
	 */
	public String getOperateBizType() {
		return this.operateBizType;
	}

	/**
	 *
	 * @param operateBizSubType
	 */
	public void setOperateBizSubType(String operateBizSubType) {
		this.operateBizSubType = operateBizSubType;
	}

	/**
	 *
	 * @return operateBizSubType
	 */
	public String getOperateBizSubType() {
		return this.operateBizSubType;
	}

	/**
	 *
	 * @param operateBizKey
	 */
	public void setOperateBizKey(String operateBizKey) {
		this.operateBizKey = operateBizKey;
	}

	/**
	 *
	 * @return operateBizKey
	 */
	public String getOperateBizKey() {
		return this.operateBizKey;
	}

	/**
	 *
	 * @param operateSiteCode
	 */
	public void setOperateSiteCode(Integer operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}

	/**
	 *
	 * @return operateSiteCode
	 */
	public Integer getOperateSiteCode() {
		return this.operateSiteCode;
	}

	/**
	 *
	 * @param operateKey
	 */
	public void setOperateKey(String operateKey) {
		this.operateKey = operateKey;
	}

	/**
	 *
	 * @return operateKey
	 */
	public String getOperateKey() {
		return this.operateKey;
	}

	/**
	 *
	 * @param operateValue
	 */
	public void setOperateValue(String operateValue) {
		this.operateValue = operateValue;
	}

	/**
	 *
	 * @return operateValue
	 */
	public String getOperateValue() {
		return this.operateValue;
	}

	/**
	 *
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 *
	 * @return operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
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
