package com.jd.bluedragon.distribution.business.entity;

import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: BusinessReturnAdressCondition
 * @Description: 商家退货地址-查询条件
 * @author wuyoude
 * @date 2020年07月28日 13:56:21
 *
 */
public class BusinessReturnAdressCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	/**
	 * 上次操作换单开始时间
	 */
	private Date lastOperateTimeLt;
	/**
	 * 上次操作换单开始时间
	 */
	private String lastOperateTimeLtStr;	
	/**
	 * 上次操作换单结束时间
	 */
	private Date lastOperateTimeGte;
	/**
	 * 上次操作换单结束时间
	 */
	private String lastOperateTimeGteStr;
	
	public Date getLastOperateTimeLt() {
		return lastOperateTimeLt;
	}
	public void setLastOperateTimeLt(Date lastOperateTimeLt) {
		this.lastOperateTimeLt = lastOperateTimeLt;
	}
	public String getLastOperateTimeLtStr() {
		return lastOperateTimeLtStr;
	}
	public void setLastOperateTimeLtStr(String lastOperateTimeLtStr) {
		this.lastOperateTimeLtStr = lastOperateTimeLtStr;
	}
	public Date getLastOperateTimeGte() {
		return lastOperateTimeGte;
	}
	public void setLastOperateTimeGte(Date lastOperateTimeGte) {
		this.lastOperateTimeGte = lastOperateTimeGte;
	}
	public String getLastOperateTimeGteStr() {
		return lastOperateTimeGteStr;
	}
	public void setLastOperateTimeGteStr(String lastOperateTimeGteStr) {
		this.lastOperateTimeGteStr = lastOperateTimeGteStr;
	}
}
