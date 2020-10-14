package com.jd.bluedragon.distribution.storage.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @ClassName: StoragePackageMCondition
 * @Description: 储位包裹主表-查询条件
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public class StoragePackageMCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 履约单号 */
	private String performanceCode;

	 /** 储位号 */
	private String storageCode;

	 /** 运单号 */
	private String waybillCode;

	 /** 系统包裹数 */
	private Long packageSum;

	 /** 上架包裹数 */
	private Long putawayPackageSum;

	 /** 状态（1-已上架，2-集齐可发货，3-强制可发货，4-已发货） */
	private Integer status;

	 /** 强制发货原因 */
	private String forceSendReason;

	 /** 计划配送时间 */
	private Date planDeliveryTime;

	 /** 所属分拣中心 */
	private Long createSiteCode;

	 /** 发货时间 */
	private Date sendTime;

	 /** 创建人 */
	private String createUser;

	 /** 更新人 */
	private String updateUser;

	private String putawayDateGEStr;

	private String putawayDateLEStr;

	private Date putawayDateGE;

	private Date putawayDateLE;

	/**
     * 预计送达时间
     * */
    private String planDeliveryTimeGEStr;
    private String planDeliveryTimeLEStr;
    private Date planDeliveryTimeGE;
    private Date planDeliveryTimeLE;

	/**
	 * The set method for performanceCode.
	 * @param performanceCode
	 */
	public void setPerformanceCode(String performanceCode) {
		this.performanceCode = performanceCode;
	}

	/**
	 * The get method for performanceCode.
	 * @return this.performanceCode
	 */
	public String getPerformanceCode() {
		return this.performanceCode;
	}

	/**
	 * The set method for storageCode.
	 * @param storageCode
	 */
	public void setStorageCode(String storageCode) {
		this.storageCode = storageCode;
	}

	/**
	 * The get method for storageCode.
	 * @return this.storageCode
	 */
	public String getStorageCode() {
		return this.storageCode;
	}

	/**
	 * The set method for waybillCode.
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * The get method for waybillCode.
	 * @return this.waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 * The set method for packageSum.
	 * @param packageSum
	 */
	public void setPackageSum(Long packageSum) {
		this.packageSum = packageSum;
	}

	/**
	 * The get method for packageSum.
	 * @return this.packageSum
	 */
	public Long getPackageSum() {
		return this.packageSum;
	}

	/**
	 * The set method for putawayPackageSum.
	 * @param putawayPackageSum
	 */
	public void setPutawayPackageSum(Long putawayPackageSum) {
		this.putawayPackageSum = putawayPackageSum;
	}

	/**
	 * The get method for putawayPackageSum.
	 * @return this.putawayPackageSum
	 */
	public Long getPutawayPackageSum() {
		return this.putawayPackageSum;
	}

	/**
	 * The set method for status.
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * The get method for status.
	 * @return this.status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * The set method for forceSendReason.
	 * @param forceSendReason
	 */
	public void setForceSendReason(String forceSendReason) {
		this.forceSendReason = forceSendReason;
	}

	/**
	 * The get method for forceSendReason.
	 * @return this.forceSendReason
	 */
	public String getForceSendReason() {
		return this.forceSendReason;
	}

	/**
	 * The set method for planDeliveryTime.
	 * @param planDeliveryTime
	 */
	public void setPlanDeliveryTime(Date planDeliveryTime) {
		this.planDeliveryTime = planDeliveryTime;
	}

	/**
	 * The get method for planDeliveryTime.
	 * @return this.planDeliveryTime
	 */
	public Date getPlanDeliveryTime() {
		return this.planDeliveryTime;
	}

	/**
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Long createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Long getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 * The set method for sendTime.
	 * @param sendTime
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * The get method for sendTime.
	 * @return this.sendTime
	 */
	public Date getSendTime() {
		return this.sendTime;
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

	public Date getPutawayDateGE() {
		return putawayDateGE;
	}

	public void setPutawayDateGE(Date putawayDateGE) {
		this.putawayDateGE = putawayDateGE;
	}

	public Date getPutawayDateLE() {
		return putawayDateLE;
	}

	public void setPutawayDateLE(Date putawayDateLE) {
		this.putawayDateLE = putawayDateLE;
	}

	public String getPutawayDateGEStr() {
		return putawayDateGEStr;
	}

	public void setPutawayDateGEStr(String putawayDateGEStr) {
		try {
			setPutawayDateGE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(putawayDateGEStr));
		} catch (ParseException e) {
		}
		this.putawayDateGEStr = putawayDateGEStr;
	}

	public String getPutawayDateLEStr() {
		return putawayDateLEStr;
	}

	public void setPutawayDateLEStr(String putawayDateLEStr) {
		try {
			setPutawayDateLE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(putawayDateLEStr));
		} catch (ParseException e) {
		}
		this.putawayDateLEStr = putawayDateLEStr;
	}

    public String getPlanDeliveryTimeGEStr() {
        return planDeliveryTimeGEStr;
    }

    public void setPlanDeliveryTimeGEStr(String planDeliveryTimeGEStr) {
        try {
            setPlanDeliveryTimeGE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(planDeliveryTimeGEStr));
        } catch (ParseException e) {
        }
        this.planDeliveryTimeGEStr = planDeliveryTimeGEStr;
    }

    public String getPlanDeliveryTimeLEStr() {
        return planDeliveryTimeLEStr;
    }

    public void setPlanDeliveryTimeLEStr(String planDeliveryTimeLEStr) {
        try {
            setPlanDeliveryTimeLE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(planDeliveryTimeLEStr));
        } catch (ParseException e) {
        }
        this.planDeliveryTimeLEStr = planDeliveryTimeLEStr;
    }

    public Date getPlanDeliveryTimeGE() {
        return planDeliveryTimeGE;
    }

    public void setPlanDeliveryTimeGE(Date planDeliveryTimeGE) {
        this.planDeliveryTimeGE = planDeliveryTimeGE;
    }

    public Date getPlanDeliveryTimeLE() {
        return planDeliveryTimeLE;
    }

    public void setPlanDeliveryTimeLE(Date planDeliveryTimeLE) {
        this.planDeliveryTimeLE = planDeliveryTimeLE;
    }
}
