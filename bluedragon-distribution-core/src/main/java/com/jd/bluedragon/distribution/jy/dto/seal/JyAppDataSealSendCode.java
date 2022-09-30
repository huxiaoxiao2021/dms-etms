package com.jd.bluedragon.distribution.jy.dto.seal;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: JyAppDataSealSendCode
 * @Description: 作业app-封车页面-批次号明细表-实体类
 * @author wuyoude
 * @date 2022年09月27日 18:17:19
 *
 */
public class JyAppDataSealSendCode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * send_detail业务主键
	 */
	private String sendDetailBizId;

	/**
	 * 发货批次号
	 */
	private String sendCode;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 是否删除：1-有效，0-删除
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
	 * @param sendDetailBizId
	 */
	public void setSendDetailBizId(String sendDetailBizId) {
		this.sendDetailBizId = sendDetailBizId;
	}

	/**
	 *
	 * @return sendDetailBizId
	 */
	public String getSendDetailBizId() {
		return this.sendDetailBizId;
	}

	/**
	 *
	 * @param sendCode
	 */
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	/**
	 *
	 * @return sendCode
	 */
	public String getSendCode() {
		return this.sendCode;
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
