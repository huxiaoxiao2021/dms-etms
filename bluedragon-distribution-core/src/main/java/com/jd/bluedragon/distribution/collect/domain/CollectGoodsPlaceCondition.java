package com.jd.bluedragon.distribution.collect.domain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;

/**
 *
 * @ClassName: CollectGoodsPlaceCondition
 * @Description: 集货位表-查询条件
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public class CollectGoodsPlaceCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 集货位编号 */
	private String collectGoodsPlaceCode;

	 /** 集货位名称 */
	private String collectGoodsPlaceName;

	 /** 集货区编码 */
	private String collectGoodsAreaCode;

	 /** 集货位类型 */
	private Integer collectGoodsPlaceType;

	 /** 集货位状态 0-空闲 1-非空闲 */
	private Integer collectGoodsPlaceStatus;

	 /** 所属站点编码 */
	private Integer createSiteCode;

	 /** 所属站点名称 */
	private String createSiteName;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

	/** 小单最大包裹数*/
	private Integer smallPackMaxNum;
	/** 中单最大包裹数*/
	private Integer middlePackMaxNum;
	/** 小单单最大运单数*/
	private Integer smallWaybillMaxNum;
	/** 中单最大运单数*/
	private Integer middleWaybillMaxNum;
	/** 大单最大运单数*/
	private Integer bigWaybillMaxNum;

	private Date createTimeGE;
	private Date createTimeLE;
	private Date updateTimeGE;
	private Date updateTimeLE;
	private String createTimeGEStr;
	private String createTimeLEStr;
	private String updateTimeGEStr;
	private String updateTimeLEStr;

	/**
	 * The set method for collectGoodsPlaceCode.
	 * @param collectGoodsPlaceCode
	 */
	public void setCollectGoodsPlaceCode(String collectGoodsPlaceCode) {
		this.collectGoodsPlaceCode = collectGoodsPlaceCode;
	}

	/**
	 * The get method for collectGoodsPlaceCode.
	 * @return this.collectGoodsPlaceCode
	 */
	public String getCollectGoodsPlaceCode() {
		return this.collectGoodsPlaceCode;
	}

	/**
	 * The set method for collectGoodsPlaceName.
	 * @param collectGoodsPlaceName
	 */
	public void setCollectGoodsPlaceName(String collectGoodsPlaceName) {
		this.collectGoodsPlaceName = collectGoodsPlaceName;
	}

	/**
	 * The get method for collectGoodsPlaceName.
	 * @return this.collectGoodsPlaceName
	 */
	public String getCollectGoodsPlaceName() {
		return this.collectGoodsPlaceName;
	}

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
	 * The set method for collectGoodsPlaceStatus.
	 * @param collectGoodsPlaceStatus
	 */
	public void setCollectGoodsPlaceStatus(Integer collectGoodsPlaceStatus) {
		this.collectGoodsPlaceStatus = collectGoodsPlaceStatus;
	}

	/**
	 * The get method for collectGoodsPlaceStatus.
	 * @return this.collectGoodsPlaceStatus
	 */
	public Integer getCollectGoodsPlaceStatus() {
		return this.collectGoodsPlaceStatus;
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

	public Integer getSmallPackMaxNum() {
		return smallPackMaxNum;
	}

	public void setSmallPackMaxNum(Integer smallPackMaxNum) {
		this.smallPackMaxNum = smallPackMaxNum;
	}

	public Integer getMiddlePackMaxNum() {
		return middlePackMaxNum;
	}

	public void setMiddlePackMaxNum(Integer middlePackMaxNum) {
		this.middlePackMaxNum = middlePackMaxNum;
	}

	public Integer getSmallWaybillMaxNum() {
		return smallWaybillMaxNum;
	}

	public void setSmallWaybillMaxNum(Integer smallWaybillMaxNum) {
		this.smallWaybillMaxNum = smallWaybillMaxNum;
	}

	public Integer getMiddleWaybillMaxNum() {
		return middleWaybillMaxNum;
	}

	public void setMiddleWaybillMaxNum(Integer middleWaybillMaxNum) {
		this.middleWaybillMaxNum = middleWaybillMaxNum;
	}

	public Integer getBigWaybillMaxNum() {
		return bigWaybillMaxNum;
	}

	public void setBigWaybillMaxNum(Integer bigWaybillMaxNum) {
		this.bigWaybillMaxNum = bigWaybillMaxNum;
	}

	public String getCreateTimeGEStr() {
		return createTimeGEStr;
	}

	public void setCreateTimeGEStr(String createTimeGEStr) {
		this.createTimeGEStr = createTimeGEStr;
		setCreateTimeGE(DateHelper.parseDate(createTimeGEStr, Constants.DATE_TIME_FORMAT));
	}

	public String getCreateTimeLEStr() {
		return createTimeLEStr;
	}

	public void setCreateTimeLEStr(String createTimeLEStr) {
		this.createTimeLEStr = createTimeLEStr;
		setCreateTimeLE(DateHelper.parseDate(createTimeLEStr, Constants.DATE_TIME_FORMAT));
	}

	public String getUpdateTimeGEStr() {
		return updateTimeGEStr;
	}

	public void setUpdateTimeGEStr(String updateTimeGEStr) {
		this.updateTimeGEStr = updateTimeGEStr;
		setUpdateTimeGE(DateHelper.parseDate(updateTimeGEStr, Constants.DATE_TIME_FORMAT));
	}

	public String getUpdateTimeLEStr() {
		return updateTimeLEStr;
	}

	public void setUpdateTimeLEStr(String updateTimeLEStr) {
		this.updateTimeLEStr = updateTimeLEStr;
		setUpdateTimeLE(DateHelper.parseDate(updateTimeLEStr, Constants.DATE_TIME_FORMAT));
	}

	public Date getCreateTimeGE() {
		return createTimeGE;
	}

	public void setCreateTimeGE(Date createTimeGE) {
		this.createTimeGE = createTimeGE;
	}

	public Date getCreateTimeLE() {
		return createTimeLE;
	}

	public void setCreateTimeLE(Date createTimeLE) {
		this.createTimeLE = createTimeLE;
	}

	public Date getUpdateTimeGE() {
		return updateTimeGE;
	}

	public void setUpdateTimeGE(Date updateTimeGE) {
		this.updateTimeGE = updateTimeGE;
	}

	public Date getUpdateTimeLE() {
		return updateTimeLE;
	}

	public void setUpdateTimeLE(Date updateTimeLE) {
		this.updateTimeLE = updateTimeLE;
	}
}
