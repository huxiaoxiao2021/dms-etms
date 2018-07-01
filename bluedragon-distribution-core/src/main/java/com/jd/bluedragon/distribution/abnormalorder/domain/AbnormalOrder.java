package com.jd.bluedragon.distribution.abnormalorder.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.api.request.AbnormalOrderRequest;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.handler.WaybillSyncParameter;

public class AbnormalOrder {
	public static final Integer NEW = -2;/*仅用来表示新增操作状态，数据库中没有此状态*/
	public static final Integer WAIT = -1;
	public static final Integer NOTCANCEL = 0;
	public static final Integer CANCEL = 1;

	
	/**
	 * 主键
	 */
	Long systemId;
	/**
	 * 唯一码
	 */
	String fingerprint;
	/**
	 * 订单号
	 */
	String orderId;
	/**
	 * 一级原因编号
	 */
	Integer abnormalCode1;
	/**
	 * 一级原因描述
	 */
	String abnormalReason1;
	/**
	 * 二级原因编号
	 */
	Integer abnormalCode2;
	/**
	 * 二级原因描述
	 */
	String abnormalReason2;
	/**
	 * 申请人帐号
	 */
	Integer createUserCode;
	/**
	 * 申请人ERP帐号
	 */
	String createUserErp;
	/**
	 * 申请人
	 */
	String createUser;
	/**
	 * 申请时间
	 */
	Date operateTime;
	/**
	 * 创建时间
	 */
	Date createTime;
	/**
	 * 更新时间
	 */
	Date updateTime;
	/**
	 * 分拣中心编号
	 */
	Integer createSiteCode;
	/**
	 * 分拣中心名称
	 */
	String createSiteName;
	/**
	 * 是否取消  '0' 否 '1'是 
	 * '-1'为推送MQ，等待结果（初始新增为-1）
	 */
	Integer isCancel;
	/**
	 * 备注（取消原因）
	 */
	String memo;
	/**
	 * 是否删除
	 */
	Integer yn;

	/**
	 *  全程跟踪显示内容
	 * */
	String trackContent;

	String waveBusinessId;//版次号，路由系统的字段
	
	public AbnormalOrder(){
		
	}
	
	public AbnormalOrder(AbnormalOrderRequest request){
		this.orderId = request.getOrderId();
		this.abnormalCode1 = request.getAbnormalCode1();
		this.abnormalReason1 = request.getAbnormalReason1();
		this.abnormalCode2 = request.getAbnormalCode2();
		this.abnormalReason2 = request.getAbnormalReason2();
		this.createUserCode = request.getUserCode();
		this.createUserErp = request.getCreateUserErp();
		this.createUser = request.getUserName();
		this.operateTime = DateHelper.parseDate(request.getOperateTime(),com.jd.bluedragon.Constants.DATE_TIME_FORMAT);
		this.createSiteCode = request.getSiteCode();
		this.createSiteName = request.getSiteName();
		this.trackContent = request.getTrackContent();
	}

	public String getWaveBusinessId() {
		return waveBusinessId;
	}

	public void setWaveBusinessId(String waveBusinessId) {
		this.waveBusinessId = waveBusinessId;
	}

	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public String getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getAbnormalCode1() {
		return abnormalCode1;
	}
	public void setAbnormalCode1(Integer abnormalCode1) {
		this.abnormalCode1 = abnormalCode1;
	}
	public String getAbnormalReason1() {
		return abnormalReason1;
	}
	public void setAbnormalReason1(String abnormalReason1) {
		this.abnormalReason1 = abnormalReason1;
	}
	public Integer getAbnormalCode2() {
		return abnormalCode2;
	}
	public void setAbnormalCode2(Integer abnormalCode2) {
		this.abnormalCode2 = abnormalCode2;
	}
	public String getAbnormalReason2() {
		return abnormalReason2;
	}
	public void setAbnormalReason2(String abnormalReason2) {
		this.abnormalReason2 = abnormalReason2;
	}
	public Integer getCreateUserCode() {
		return createUserCode;
	}
	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}
	public String getCreateUserErp() {
		return createUserErp;
	}
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}
	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getCreateSiteName() {
		return createSiteName;
	}
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}
	public Integer getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(Integer isCancel) {
		this.isCancel = isCancel;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public String getTrackContent() {
		return trackContent;
	}

	public void setTrackContent(String trackContent) {
		this.trackContent = trackContent;
	}


}
