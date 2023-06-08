package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;
import java.util.List;

import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerCaseData implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	 * case列表
	 */
	private List<JyWorkGridManagerCaseItemData> itemList;
	/**
	 * 文件列表
	 */
	private List<AttachmentDetailData> attachmentList;
	
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
	public List<JyWorkGridManagerCaseItemData> getItemList() {
		return itemList;
	}
	public void setItemList(List<JyWorkGridManagerCaseItemData> itemList) {
		this.itemList = itemList;
	}
	public List<AttachmentDetailData> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<AttachmentDetailData> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public Integer getEditStatus() {
		return editStatus;
	}
	public void setEditStatus(Integer editStatus) {
		this.editStatus = editStatus;
	}
}
