package com.jd.bluedragon.distribution.schedule.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* B2B-TMS企配仓运单调度MQ
* https://cf.jd.com/pages/viewpage.action?pageId=744365013
*@author wengguoqi
*@date 2022/3/10 19:36
*/
@Data
public class B2bWaybillScheduleMq implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 调度单号
	 */
	private String transJobCode;
	/**
	 * 承运商名称
	 */
	private String carrierName;

	/**
	 * 包裹数
	 */
	private Integer boxCount;

	/**
	 * 企配仓网点编码
	 */
	private String siteNodeCode;
	private String beginNodeCode;

	/**
	 * 调度时间
	 */
	private Date scheduleTime;
}
