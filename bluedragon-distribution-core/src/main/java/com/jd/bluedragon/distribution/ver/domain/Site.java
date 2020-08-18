package com.jd.bluedragon.distribution.ver.domain;

public class Site {

    public static Integer TYPE_4 = 4;

    public static final Integer BUSINESS_TYPE_JP_ZT = 1;    // 京配,自提业务
    public static final Integer BUSINESS_TYPE_JP = 2;       // 京配业务
    public static final Integer BUSINESS_TYPE_ZT = 3;       // 自提业务
    public static final Integer BUSINESS_TYPE_NJP = 4;      // 非京配业务
    public static final Integer BUSINESS_TYPE_JP_ZT_SHENGXIAN_ZT = 5;    // 京配,自提业务,生鲜自提 (包含1类型)
    public static final Integer BUSINESS_TYPE_ZT_SHENGXIAN_ZT = 6;       // 自提业务,生鲜自提(包含3类型)
    public static final Integer BUSINESS_TYPE_ZT_NJP = 7;       // 非京配 自提

    private Integer type;
    private String name;
    private Integer code;
    private Integer orgId;
    private Integer subType;
    private Integer cityId;
    private Integer provinceId;
    private Integer airSign;
    private Integer siteBusinessType;                       // 站点开通的业务
    private String dmsCode;                                //分拣中心七位编码

    public Integer getSiteBusinessType() {
        return siteBusinessType;
    }

    public void setSiteBusinessType(Integer siteBusinessType) {
        this.siteBusinessType = siteBusinessType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrgId() {
        return this.orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getAirSign() {
		return airSign;
	}

	public void setAirSign(Integer airSign) {
		this.airSign = airSign;
	}

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }
}
