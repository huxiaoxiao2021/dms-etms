package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: UserSignQueryRequest
 * @Description: 人员签到查询请求
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class UserSignQueryRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 场地编码
     */
    private Integer siteCode;
    /**
     * 岗位编码
     */
    private String positionCode;
	/**
	 * 签到日期
	 */
	private Date signDate;
	/**
	 * 签到日期-开始
	 */
	private Date signDateStart;
	/**
	 * 签到日期-结束
	 */
	private Date signDateEnd;
	/**
	 * 签到日期-开始
	 */
	private Date signInTimeStart;
	/**
	 * 签到日期-结束
	 */
	private Date signInTimeEnd;
	
    /**
     * 岗位编码
     */
    private String refGridKey;
    /**
     * 用户编码
     */
    private String userCode;
	/**
	 * 分页参数-开始值
	 */
	private int offset = 0;
	/**
	 * 分页参数-数据条数
	 */
	private int limit = 10;
	
	private Integer pageNumber;
	
	private Integer pageSize;
	/**
	 * 创建人ERP
	 */
	private String createUser;
	
	/**
	 * 签到日期
	 */
	private String signDateStr;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public Date getSignDateStart() {
		return signDateStart;
	}
	public void setSignDateStart(Date signDateStart) {
		this.signDateStart = signDateStart;
	}
	public Date getSignDateEnd() {
		return signDateEnd;
	}
	public void setSignDateEnd(Date signDateEnd) {
		this.signDateEnd = signDateEnd;
	}
	public Date getSignInTimeStart() {
		return signInTimeStart;
	}
	public void setSignInTimeStart(Date signInTimeStart) {
		this.signInTimeStart = signInTimeStart;
	}
	public Date getSignInTimeEnd() {
		return signInTimeEnd;
	}
	public void setSignInTimeEnd(Date signInTimeEnd) {
		this.signInTimeEnd = signInTimeEnd;
	}
	public String getRefGridKey() {
		return refGridKey;
	}
	public void setRefGridKey(String refGridKey) {
		this.refGridKey = refGridKey;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getSignDateStr() {
		return signDateStr;
	}
	public void setSignDateStr(String signDateStr) {
		this.signDateStr = signDateStr;
	}
}
