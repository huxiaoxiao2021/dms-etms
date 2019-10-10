package com.jd.bluedragon.distribution.collect.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeCondition
 * @Description: 集货位类型表-查询条件
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public class CollectGoodsPlaceTypeCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 集货位类型 1-小单 2-中单 3-大单 4-异常 */
	private Integer collectGoodsPlaceType;

	 /** 集货最小数量 */
	private Integer minPackNum;

	 /** 最大集货数量 */
	private Integer maxPackNum;

	 /** 最大存放运单数量 */
	private Integer maxWaybillNum;

	 /** 所属站点编码 */
	private Integer createSiteCode;

	 /** 所属站点名称 */
	private String createSiteName;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

	/**
	 * The set method for collectGoodsPlaceType.
	 * @param collectGoodsPlaceType
	 */
	public void setCollectGoodsPlaceType(Integer collectGoodsPlaceType) {
		this.collectGoodsPlaceType = collectGoodsPlaceType;
	}

	/**
	 * The get method for collectGoodsPlaceType.
	 * @return this.collectGoodsPlaceType
	 */
	public Integer getCollectGoodsPlaceType() {
		return this.collectGoodsPlaceType;
	}

	/**
	 * The set method for minPackNum.
	 * @param minPackNum
	 */
	public void setMinPackNum(Integer minPackNum) {
		this.minPackNum = minPackNum;
	}

	/**
	 * The get method for minPackNum.
	 * @return this.minPackNum
	 */
	public Integer getMinPackNum() {
		return this.minPackNum;
	}

	/**
	 * The set method for maxPackNum.
	 * @param maxPackNum
	 */
	public void setMaxPackNum(Integer maxPackNum) {
		this.maxPackNum = maxPackNum;
	}

	/**
	 * The get method for maxPackNum.
	 * @return this.maxPackNum
	 */
	public Integer getMaxPackNum() {
		return this.maxPackNum;
	}

	/**
	 * The set method for maxWaybillNum.
	 * @param maxWaybillNum
	 */
	public void setMaxWaybillNum(Integer maxWaybillNum) {
		this.maxWaybillNum = maxWaybillNum;
	}

	/**
	 * The get method for maxWaybillNum.
	 * @return this.maxWaybillNum
	 */
	public Integer getMaxWaybillNum() {
		return this.maxWaybillNum;
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
