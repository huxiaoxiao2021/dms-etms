package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;

/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerCaseItemDataVO implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 场景编码
	 */
	private String caseCode;
	/**
	 * 检查项编码
	 */
	private String caseItemCode;

	/**
	 * 检查项描述内容
	 */
	private String caseItemText;

	/**
	 * 是否可选,0-否,1-是
	 */
	private Integer canSelect;

	/**
	 * 选择标志,0-未选中,1-选中
	 */
	private Integer selectFlag;

	/**
	 * 排序值，1-100
	 */
	private Integer orderNum;

	/**
	 * 检查改进计划、方案/反馈
	 * 只有反馈任务类型
	 * @return
	 */
	private String feedbackContent;
	/**
	 * 用户自定义检查项名称
	 */
	private String userDefinedTitle;

	public String getCaseItemCode() {
		return caseItemCode;
	}

	public void setCaseItemCode(String caseItemCode) {
		this.caseItemCode = caseItemCode;
	}

	public String getCaseItemText() {
		return caseItemText;
	}

	public void setCaseItemText(String caseItemText) {
		this.caseItemText = caseItemText;
	}

	public Integer getCanSelect() {
		return canSelect;
	}

	public void setCanSelect(Integer canSelect) {
		this.canSelect = canSelect;
	}

	public Integer getSelectFlag() {
		return selectFlag;
	}
	public boolean checkIsSelected() {
		return selectFlag != null && selectFlag > 0;
	}
	public void setSelectFlag(Integer selectFlag) {
		this.selectFlag = selectFlag;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getCaseCode() {
		return caseCode;
	}

	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	public String getFeedbackContent() {
		return feedbackContent;
	}

	public void setFeedbackContent(String feedbackContent) {
		this.feedbackContent = feedbackContent;
	}

	public String getUserDefinedTitle() {
		return userDefinedTitle;
	}

	public void setUserDefinedTitle(String userDefinedTitle) {
		this.userDefinedTitle = userDefinedTitle;
	}
}
