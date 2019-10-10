package com.jd.bluedragon.distribution.collect.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: CollectGoodsArea
 * @Description: 集货区表-实体类
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public class CollectGoodsArea extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 集货区编号 */
	private String collectGoodsAreaCode;

	 /** 集货区名称 */
	private String collectGoodsAreaName;

	 /** 所属站点编码 */
	private Integer createSiteCode;

	 /** 所属站点名称 */
	private String createSiteName;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

	/**
	 * The set method for collectGoodsAreaCode.
	 * @param collectGoodsAreaCode
	 */
	public void setCollectGoodsAreaCode(String collectGoodsAreaCode) {
		this.collectGoodsAreaCode = collectGoodsAreaCode;
	}

	/**
	 * The get method for collectGoodsAreaCode.
	 * @return this.collectGoodsAreaCode
	 */
	public String getCollectGoodsAreaCode() {
		return this.collectGoodsAreaCode;
	}

	/**
	 * The set method for collectGoodsAreaName.
	 * @param collectGoodsAreaName
	 */
	public void setCollectGoodsAreaName(String collectGoodsAreaName) {
		this.collectGoodsAreaName = collectGoodsAreaName;
	}

	/**
	 * The get method for collectGoodsAreaName.
	 * @return this.collectGoodsAreaName
	 */
	public String getCollectGoodsAreaName() {
		return this.collectGoodsAreaName;
	}

	/**
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 * The set method for createSiteName.
	 * @param createSiteName
	 */
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	/**
	 * The get method for createSiteName.
	 * @return this.createSiteName
	 */
	public String getCreateSiteName() {
		return this.createSiteName;
	}

	/**
	 * The set method for createUser.
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * The get method for createUser.
	 * @return this.createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 * The set method for updateUser.
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * The get method for updateUser.
	 * @return this.updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}


}
