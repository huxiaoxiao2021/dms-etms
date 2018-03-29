package com.jd.bluedragon.distribution.api.response;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * UPDATED BY wangtingwei@jd.com
 * EDIT：站点添加拼音码字段
 */
public class BaseResponse extends JdResponse {

    private static final long serialVersionUID = 591571244835688354L;

    /**
     * 拼音码
     */
    private String pinyinCode;

    /** 司机编号 */
    private Integer driverCode;

    //司机id
    private Integer driverId;

    /** 司机姓名 */
    private String driver;

    //站点id
    private Integer siteId;

	/** 站点编号 */
    private Integer siteCode;

    /** 站点名称 */
    private String siteName;

    private Integer siteType;

    private Integer siteType2;
	/** 站点子类型 */
	private Integer subType;
    /** ERP帐号 */
    private String erpAccount;

    /** ERP登录密码 */
    private String password;

	/** 用户ID */
	private Integer staffId;

	/** 用户名称 */
	private String staffName;

	//车牌号
	private String carId;

	//车牌号条码
	private String carCode;

	//gps编号
	private String gpsNo;

	//Sim卡号
	private String simNo;

	//车辆类型
	private Integer carType;

	//承载量
	private Double loadCapacity;

	//所属车队
	private Integer  carTeam;

	//机构号
	private Integer orgId;

	//机构名称
	public String orgName;

	//dmscode
	private String dmsCode;

	private String dmsName;

	//data分类名称
	private String typeName;

	//data分类编号
	private Integer typeCode;

	//data分类备注
	private String memo;

	//dtat分类parentId
	private Integer parentId;

	//data节点级别
	private Integer nodeLevel;

	//datat类型分类
	private Integer typeGroup;

	//datat更新时间
	private Date dataUpdate;

	//系统时间
	private String serverDate;

	//车牌号
	private String license;

	//承运商部分
	private Long carrierId;

    private String carrierCode;

    private String carrierName;

    private String contacter;

    private String address;

	/**
	 * 速递id
	 */
	private Integer parentSiteCode;//速递id
	/**
	 * 速递名称
	 */
	private String parentSiteName;//速递名称

	/** 承运人类型 */
	private Integer sendUserType;

	/** 站点业务类型 */
	private Integer siteBusinessType;

	public Integer getSiteBusinessType() {
		return siteBusinessType;
	}

	public void setSiteBusinessType(Integer siteBusinessType) {
		this.siteBusinessType = siteBusinessType;
	}

	public Integer getParentSiteCode() {
		return parentSiteCode;
	}

	public void setParentSiteCode(Integer parentSiteCode) {
		this.parentSiteCode = parentSiteCode;
	}

	public String getParentSiteName() {
		return parentSiteName;
	}

	public void setParentSiteName(String parentSiteName) {
		this.parentSiteName = parentSiteName;
	}


	/**
	 * 分拣中心信息
	 */

	//三方id (站点CODE)
	Integer partnerId;

	//三方code (站点DMSCODE)
	String partnerCode;

	/** 运输类型 1：干线；2：支线； */
	private int routeType;

	public String getPinyinCode() {
		return pinyinCode;
	}

    public void setPinyinCode(String pinyinCode) {
        this.pinyinCode = pinyinCode;
    }

	public int getRouteType() {
		return routeType;
	}

	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public Date getDataUpdate() {
		return dataUpdate!=null?(Date)dataUpdate.clone():null;
	}

	public void setDataUpdate(Date dataUpdate) {
		this.dataUpdate = dataUpdate!=null?(Date)dataUpdate.clone():null;
	}

	public Integer getSiteType() {
		return siteType;
	}

	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public String getDmsCode() {
		return dmsCode;
	}

	public void setDmsCode(String dmsCode) {
		this.dmsCode = dmsCode;
	}

	public String getDmsName() {
		return dmsName;
	}

	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	public String getCarId() {
		return carId;
	}

	public String getGpsNo() {
		return gpsNo;
	}

	public void setGpsNo(String gpsNo) {
		this.gpsNo = gpsNo;
	}

	public String getSimNo() {
		return simNo;
	}

	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}

	public Integer getCarType() {
		return carType;
	}

	public void setCarType(Integer carType) {
		this.carType = carType;
	}

	public Double getLoadCapacity() {
		return loadCapacity;
	}

	public void setLoadCapacity(Double loadCapacity) {
		this.loadCapacity = loadCapacity;
	}

	public Integer getCarTeam() {
		return carTeam;
	}

	public void setCarTeam(Integer carTeam) {
		this.carTeam = carTeam;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(Integer typeCode) {
		this.typeCode = typeCode;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(Integer nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public Integer getTypeGroup() {
		return typeGroup;
	}

	public void setTypeGroup(Integer typeGroup) {
		this.typeGroup = typeGroup;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}


    public Integer getSiteCode() {
        return this.siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getErpAccount() {
        return this.erpAccount;
    }

    public void setErpAccount(String erpAccount) {
        this.erpAccount = erpAccount;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDriverCode() {
        return this.driverCode;
    }

    public void setDriverCode(Integer driverCode) {
        this.driverCode = driverCode;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public java.lang.Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(java.lang.Integer staffId) {
		this.staffId = staffId;
	}

	public java.lang.String getStaffName() {
		return staffName;
	}

	public void setStaffName(java.lang.String staffName) {
		this.staffName = staffName;
	}

	public BaseResponse() {
    }

    public BaseResponse(Integer code, String message) {
        super(code, message);
    }

    public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getServerDate() {
		return serverDate;
	}

	public void setServerDate(String serverDate) {
		this.serverDate = serverDate;
	}

	public Integer getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public Integer getSiteType2() {
		return siteType2;
	}

	public void setSiteType2(Integer siteType2) {
		this.siteType2 = siteType2;
	}

	public Integer getSendUserType() {
		return sendUserType;
	}

	public void setSendUserType(Integer sendUserType) {
		this.sendUserType = sendUserType;
	}

	public Long getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}
}
