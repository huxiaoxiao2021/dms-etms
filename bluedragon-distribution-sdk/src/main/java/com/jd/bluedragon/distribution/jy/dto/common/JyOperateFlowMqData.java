package com.jd.bluedragon.distribution.jy.dto.common;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: JyOperateFlowMqData
 * @Description: 分拣-业务操作流水表-消息体
 * @author wuyoude
 * @date 2023年09月12日 17:07:43
 *
 */
public class JyOperateFlowMqData implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	 * 操作业务数据
	 */
	private JyOperateFlowData jyOperateFlowData;

	/**
	 * 操作时间
	 */
	private Date operateTime;

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

	public JyOperateFlowData getJyOperateFlowData() {
		return jyOperateFlowData;
	}

	public void setJyOperateFlowData(JyOperateFlowData jyOperateFlowData) {
		this.jyOperateFlowData = jyOperateFlowData;
	}
	
}
