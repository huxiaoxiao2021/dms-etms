package com.jd.bluedragon.distribution.jy.dto.work;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: JyWorkGridManagerCase
 * @Description: 巡检任务表-检查项-实体类
 * @author wuyoude
 * @date 2023年06月14日 17:33:11
 *
 */
public class JyWorkGridManagerCase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务主键
	 */
	private String bizId;

	/**
	 * 场景编码
	 */
	private String caseCode;

	/**
	 * 场景标题
	 */
	private String caseTitle;

	/**
	 * 场景内容
	 */
	private String caseContent;

	/**
	 * 编辑状态,0-未处理,1-处理完成 2-处理中
	 */
	private Integer editStatus;

	/**
	 * 检查结果,0-未选择,1-符合 2-不符合
	 */
	private Integer checkResult;

	/**
	 * 需要拍照,0-否,1-是
	 */
	private Integer needUploadPhoto;

	/**
	 * 创建人ERP
	 */
	private String createUser;

	/**
	 * 创建人姓名
	 */
	private String createUserName;

	/**
	 * 修改人ERP
	 */
	private String updateUser;

	/**
	 * 更新人姓名
	 */
	private String updateUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 * 计划改善完成时间
	 */
	private Date improveEndTime;

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
	 * @param bizId
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	/**
	 *
	 * @return bizId
	 */
	public String getBizId() {
		return this.bizId;
	}

	/**
	 *
	 * @param caseCode
	 */
	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	/**
	 *
	 * @return caseCode
	 */
	public String getCaseCode() {
		return this.caseCode;
	}

	/**
	 *
	 * @param caseTitle
	 */
	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}

	/**
	 *
	 * @return caseTitle
	 */
	public String getCaseTitle() {
		return this.caseTitle;
	}

	/**
	 *
	 * @param caseContent
	 */
	public void setCaseContent(String caseContent) {
		this.caseContent = caseContent;
	}

	/**
	 *
	 * @return caseContent
	 */
	public String getCaseContent() {
		return this.caseContent;
	}

	/**
	 *
	 * @param editStatus
	 */
	public void setEditStatus(Integer editStatus) {
		this.editStatus = editStatus;
	}

	/**
	 *
	 * @return editStatus
	 */
	public Integer getEditStatus() {
		return this.editStatus;
	}

	/**
	 *
	 * @param checkResult
	 */
	public void setCheckResult(Integer checkResult) {
		this.checkResult = checkResult;
	}

	/**
	 *
	 * @return checkResult
	 */
	public Integer getCheckResult() {
		return this.checkResult;
	}

	/**
	 *
	 * @param needUploadPhoto
	 */
	public void setNeedUploadPhoto(Integer needUploadPhoto) {
		this.needUploadPhoto = needUploadPhoto;
	}

	/**
	 *
	 * @return needUploadPhoto
	 */
	public Integer getNeedUploadPhoto() {
		return this.needUploadPhoto;
	}

	/**
	 *
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 *
	 * @return createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 *
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 *
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 *
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 *
	 * @return updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 *
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 *
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
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
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
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

	public Date getImproveEndTime() {
		return improveEndTime;
	}

	public void setImproveEndTime(Date improveEndTime) {
		this.improveEndTime = improveEndTime;
	}
}
