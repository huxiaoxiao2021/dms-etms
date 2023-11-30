package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerCaseDataVO implements Serializable {

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
	 * 编辑类型
	 * @See com.jd.bluedragon.distribution.jy.enums.EditTypeEnum
	 */
	private Integer editType;
	/**
	 * 编辑状态
	 * @See com.jd.bluedragon.distribution.jy.enums.EditStatusEnum
	 */
	private Integer editStatus;
	/**
	 * 任务编码
	 */
	private String taskCode;

	/**
	 * 场景编码
	 */
	private String caseCode;

	/**
	 * 场景名称
	 */
	private String caseName;

	/**
	 * 场景标题
	 */
	private String caseTitle;

	/**
	 * 场景内容
	 */
	private String caseContent;

	/**
	 * @See com.jd.bluedragon.distribution.jy.work.enums.WorkCheckResultEnum
	 * 检查结果,0-未选择,1-符合 2-不符合
	 */
	private Integer checkResult;

	/**
	 * 需要拍照,0-否,1-是
	 */
	private Integer needUploadPhoto;
	/**
	 * 排序值，1-100
	 */
	private Integer orderNum;	
	/**
	 * case列表
	 */
	private List<JyWorkGridManagerCaseItemDataVO> itemList;
	/**
	 * 文件列表
	 */
	private List<AttachmentDetailDataVO> attachmentList;

	/**
	 * 改善附件
	 * @return
	 */
	private List<AttachmentDetailDataVO> improveAttachmentList;

	/**
	 * 指标改善截止时间
	 * 只有指标改善任务有
	 * @return
	 */
	private Date improveEndTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public Integer getEditType() {
		return editType;
	}
	public void setEditType(Integer editType) {
		this.editType = editType;
	}
	public String getTaskCode() {
		return taskCode;
	}
	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}
	public String getCaseCode() {
		return caseCode;
	}
	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String getCaseTitle() {
		return caseTitle;
	}
	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}
	public String getCaseContent() {
		return caseContent;
	}
	public void setCaseContent(String caseContent) {
		this.caseContent = caseContent;
	}
	public Integer getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(Integer checkResult) {
		this.checkResult = checkResult;
	}
	public Integer getNeedUploadPhoto() {
		return needUploadPhoto;
	}
	public void setNeedUploadPhoto(Integer needUploadPhoto) {
		this.needUploadPhoto = needUploadPhoto;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public List<JyWorkGridManagerCaseItemDataVO> getItemList() {
		return itemList;
	}
	public void setItemList(List<JyWorkGridManagerCaseItemDataVO> itemList) {
		this.itemList = itemList;
	}
	public List<AttachmentDetailDataVO> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<AttachmentDetailDataVO> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public Integer getEditStatus() {
		return editStatus;
	}
	public void setEditStatus(Integer editStatus) {
		this.editStatus = editStatus;
	}

	public List<AttachmentDetailDataVO> getImproveAttachmentList() {
		return improveAttachmentList;
	}

	public void setImproveAttachmentList(List<AttachmentDetailDataVO> improveAttachmentList) {
		this.improveAttachmentList = improveAttachmentList;
	}

	public Date getImproveEndTime() {
		return improveEndTime;
	}

	public void setImproveEndTime(Date improveEndTime) {
		this.improveEndTime = improveEndTime;
	}
}
