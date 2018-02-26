package com.jd.bluedragon.distribution.base.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: DmsBaseDict
 * @Description: 数据字典-实体类
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
public class DmsBaseDict extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 分类名称 */
	private String typeName;

	 /** 分类编号 */
	private Integer typeCode;

	 /** 备注 */
	private String memo;

	 /** 与type_code进行关联 */
	private Integer parentId;

	 /** 节点级别 */
	private Integer nodeLevel;

	 /** 类型分类 */
	private Integer typeGroup;

	/**
	 * The set method for typeName.
	 * @param typeName
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * The get method for typeName.
	 * @return this.typeName
	 */
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * The set method for typeCode.
	 * @param typeCode
	 */
	public void setTypeCode(Integer typeCode) {
		this.typeCode = typeCode;
	}

	/**
	 * The get method for typeCode.
	 * @return this.typeCode
	 */
	public Integer getTypeCode() {
		return this.typeCode;
	}

	/**
	 * The set method for memo.
	 * @param memo
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * The get method for memo.
	 * @return this.memo
	 */
	public String getMemo() {
		return this.memo;
	}

	/**
	 * The set method for parentId.
	 * @param parentId
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * The get method for parentId.
	 * @return this.parentId
	 */
	public Integer getParentId() {
		return this.parentId;
	}

	/**
	 * The set method for nodeLevel.
	 * @param nodeLevel
	 */
	public void setNodeLevel(Integer nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	/**
	 * The get method for nodeLevel.
	 * @return this.nodeLevel
	 */
	public Integer getNodeLevel() {
		return this.nodeLevel;
	}

	/**
	 * The set method for typeGroup.
	 * @param typeGroup
	 */
	public void setTypeGroup(Integer typeGroup) {
		this.typeGroup = typeGroup;
	}

	/**
	 * The get method for typeGroup.
	 * @return this.typeGroup
	 */
	public Integer getTypeGroup() {
		return this.typeGroup;
	}


}
