package com.jd.bluedragon.distribution.jy.attachment;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运-附件数据库-查询实体
 *
 * @author wuyoude
 * @date 2023/6/8 3:49 PM
 */
public class JyAttachmentDetailQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 业务类型-区分业务场景
     */
    private String bizType;
    /**
     * 业务子类型-区分业务子场景
     */
    private String bizSubType;
    /**
     * 场地编码-数据库拆分键
     */
    private Integer siteCode;

    /**
     * 附件类型
     */
    private Integer attachmentType;
    
	/**
	 * 分页参数-开始值
	 */
	private int offset = 0;
	/**
	 * 分页参数-数据条数
	 */
	private int limit = 10;
	
	private Integer pageNumber = 1;
	
	private Integer pageSize = 10;
	
	private Date queryStartTs;
	private Date queryEndTs;
	private Long id;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getBizSubType() {
		return bizSubType;
	}

	public void setBizSubType(String bizSubType) {
		this.bizSubType = bizSubType;
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Date getQueryStartTs() {
		return queryStartTs;
	}

	public void setQueryStartTs(Date queryStartTs) {
		this.queryStartTs = queryStartTs;
	}

	public Date getQueryEndTs() {
		return queryEndTs;
	}

	public void setQueryEndTs(Date queryEndTs) {
		this.queryEndTs = queryEndTs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
