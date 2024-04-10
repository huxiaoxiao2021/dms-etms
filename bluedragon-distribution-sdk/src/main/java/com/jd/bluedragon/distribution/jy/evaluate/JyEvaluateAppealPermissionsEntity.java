package com.jd.bluedragon.distribution.jy.evaluate;


import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author pengchong28
 * @email pengchong28@jd.com
 * @date 2024-03-01 15:59:15
 */
public class JyEvaluateAppealPermissionsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	private Long id;
	/**
	 * 场地编码
	 */
	private Long siteCode;
	/**
	 * 评价权限，0-关闭， 1-开启
	 */
	private Integer evaluate;
	/**
	 * 申诉权限，0-关闭， 1-开启
	 */
	private Integer appeal;
	/**
	 * 评价权限关闭生效时间
	 */
	private Date evaluateClosureDate;
	/**
	 * 申诉权限关闭生效时间
	 */
	private Date appealClosureDate;
	/**
	 * 创建人ERP
	 */
	private String createUserErp;
	/**
	 * 创建人姓名
	 */
	private String createUserName;
	/**
	 * 修改人ERP
	 */
	private String updateUserErp;
	/**
	 * 更新人姓名
	 */
	private String updateUserName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 数据变更时间戳
	 */
	private Date ts;
	/**
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;

	public Long setId(Long id){
		return this.id = id;
	}

		public Long getId(){
		return this.id;
	}
	public Long setSiteCode(Long siteCode){
		return this.siteCode = siteCode;
	}

		public Long getSiteCode(){
		return this.siteCode;
	}
	public Integer setEvaluate(Integer evaluate){
		return this.evaluate = evaluate;
	}

		public Integer getEvaluate(){
		return this.evaluate;
	}
	public Integer setAppeal(Integer appeal){
		return this.appeal = appeal;
	}

		public Integer getAppeal(){
		return this.appeal;
	}
	public Date setEvaluateClosureDate(Date evaluateClosureDate){
		return this.evaluateClosureDate = evaluateClosureDate;
	}

		public Date getEvaluateClosureDate(){
		return this.evaluateClosureDate;
	}
	public Date setAppealClosureDate(Date appealClosureDate){
		return this.appealClosureDate = appealClosureDate;
	}

		public Date getAppealClosureDate(){
		return this.appealClosureDate;
	}
	public String setCreateUserErp(String createUserErp){
		return this.createUserErp = createUserErp;
	}

		public String getCreateUserErp(){
		return this.createUserErp;
	}
	public String setCreateUserName(String createUserName){
		return this.createUserName = createUserName;
	}

		public String getCreateUserName(){
		return this.createUserName;
	}
	public String setUpdateUserErp(String updateUserErp){
		return this.updateUserErp = updateUserErp;
	}

		public String getUpdateUserErp(){
		return this.updateUserErp;
	}
	public String setUpdateUserName(String updateUserName){
		return this.updateUserName = updateUserName;
	}

		public String getUpdateUserName(){
		return this.updateUserName;
	}
	public Date setCreateTime(Date createTime){
		return this.createTime = createTime;
	}

		public Date getCreateTime(){
		return this.createTime;
	}
	public Date setUpdateTime(Date updateTime){
		return this.updateTime = updateTime;
	}

		public Date getUpdateTime(){
		return this.updateTime;
	}
	public Date setTs(Date ts){
		return this.ts = ts;
	}

		public Date getTs(){
		return this.ts;
	}
	public Integer setYn(Integer yn){
		return this.yn = yn;
	}

		public Integer getYn(){
		return this.yn;
	}

}
