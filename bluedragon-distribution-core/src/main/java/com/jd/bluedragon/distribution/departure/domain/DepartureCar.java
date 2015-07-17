package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.Date;

public class DepartureCar implements Serializable {
	
	private static final long serialVersionUID = -8501753291667877699L;
    /** 以下是基础资料运力编码相关可以区分的运输类型 */
    public static final int TYPE_TRUNK_LINE = 1;  //干线
    public static final int TYPE_BRANCH_LINE = 2; //支线
    public static final int TYPE_LDTS_LINE = 3; //长途传站
    public static final int TYPE_URBANTS_LINE = 4; //市内传站
    public static final int TYPE_FREEY_LINE = 5; //摆渡
    public static final int TYPE_OTHER_LINE = 6; //其他


	/** 封签ID  */ 
	private Long shieldsCarId;

	/** 车号  */ 
    private String carCode;
    
    /** 封签号  */ 
    private String shieldsCarCode;
    
    /** 司机 */ 
    private String sendUser;

    /** 司机编码 */ 
    private Integer sendUserCode;

    /** 创建人单位编码 */ 
    private Integer createSiteCode;

    /** 创建人 */ 
    private String createUser;

    /** 创建人编码 */ 
    private Integer createUserCode;
    
    /** 创建时间 */ 
    private Date createTime;
    
    /** 修改时间 */ 
    private Date updateTime;

    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;
    
	/** 重量   */
	private Double weight;
	
	/** 体积   */
	private Double volume;
	
	/** 承运人类型 */
	private Integer sendUserType;
	
	/** 转出车号  */ 
    private String oldCarCode;
    
     /** 信息指纹 */
    private String fingerprint;
    
    /** 发车类型 1发车 2转车 */
    private Integer departType;
    
    private Integer runNumber;//班次
    
    /** 打印时间 */
    private Date printTime;
	
    /**
     *  运力编码（改为其他含义，区分干支线。1：干线；2：支线）
     *
     *  */
	private String capacityCode;
	
	public String receiveSiteCodes;
    
    public String getOldCarCode() {
		return oldCarCode;
	}

	public void setOldCarCode(String oldCarCode) {
		this.oldCarCode = oldCarCode;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public Integer getDepartType() {
		return departType;
	}

	public void setDepartType(Integer departType) {
		this.departType = departType;
	}


    public Long getShieldsCarId() {
        return shieldsCarId;
    }

    public void setShieldsCarId(Long shieldsCarId) {
        this.shieldsCarId = shieldsCarId;
    }
    
    public Integer getSendUserType() {
		return sendUserType;
	}

	public void setSendUserType(Integer sendUserType) {
		this.sendUserType = sendUserType;
	}

	public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getShieldsCarCode() {
        return shieldsCarCode;
    }

    public void setShieldsCarCode(String shieldsCarCode) {
        this.shieldsCarCode = shieldsCarCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    public Date getPrintTime() {
    	return printTime!=null?(Date)printTime.clone():null;
	}

	public void setPrintTime(Date printTime) {
		this.printTime = printTime!=null?(Date)printTime.clone():null;
	}

	public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
    
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public Integer getSendUserCode() {
		return sendUserCode;
	}

	public void setSendUserCode(Integer sendUserCode) {
		this.sendUserCode = sendUserCode;
	}

	public Integer getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(Integer runNumber) {
		this.runNumber = runNumber;
	}

	public String getCapacityCode() {
		return capacityCode;
	}

	public void setCapacityCode(String capacityCode) {
		this.capacityCode = capacityCode;
	}

	public String getReceiveSiteCodes() {
		return receiveSiteCodes;
	}

	public void setReceiveSiteCodes(String receiveSiteCodes) {
		this.receiveSiteCodes = receiveSiteCodes;
	}

	@Override
	public String toString() {
		return "DepartureCar [shieldsCarId=" + shieldsCarId + ", carCode="
				+ carCode + ", shieldsCarCode=" + shieldsCarCode
				+ ", sendUser=" + sendUser + ", sendUserCode=" + sendUserCode
				+ ", createSiteCode=" + createSiteCode + ", createUser="
				+ createUser + ", createUserCode=" + createUserCode
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", yn=" + yn + ", weight=" + weight + ", volume=" + volume
				+ "]";
	}

}