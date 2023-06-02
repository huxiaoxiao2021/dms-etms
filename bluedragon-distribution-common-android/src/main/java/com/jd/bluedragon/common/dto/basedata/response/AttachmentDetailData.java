package com.jd.bluedragon.common.dto.basedata.response;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运-附件数据库实体
 *
 * @author hujiping
 * @date 2023/3/28 3:49 PM
 */
public class AttachmentDetailData implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 编辑类型
	 * @See com.jd.bluedragon.distribution.jy.enums.EditTypeEnum
	 */
	private Integer editType;
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 附件类型
     */
    private Integer attachmentType;

    /**
     * 附件链接
     */
    private String attachmentUrl;

    /**
     * 创建人ERP
     */
    private String createUserErp;

    /**
     * 更新人ERP
     */
    private String updateUserErp;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;

	public Integer getEditType() {
		return editType;
	}

	public void setEditType(Integer editType) {
		this.editType = editType;
	}

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

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public Integer getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(Integer attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getCreateUserErp() {
		return createUserErp;
	}

	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	public String getUpdateUserErp() {
		return updateUserErp;
	}

	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
