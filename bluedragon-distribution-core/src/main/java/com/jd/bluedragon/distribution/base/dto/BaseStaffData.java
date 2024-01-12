package com.jd.bluedragon.distribution.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户基础数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-12-26 18:41:33 周四
 */
@Data
public class BaseStaffData implements Serializable {
    private static final long serialVersionUID = -8375216014949187962L;

    private String staffName;

    private Integer staffNo;
    private Integer role;
    private String phone;
    private String mail;
    private String siteName;
    private Integer siteCode;
    private Integer siteType;
    private Integer subType;
    private String sitePhone;
    private Integer targetType;
    private Integer orgId;
    private String address;
    private String orgName;
    private String dmsSiteCode;
    private String customCode;
    private String storeCode;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private Integer countryId;
    private String countryName;
    private Integer countrySideId;
    private String countrySideName;
    private Integer dmsId;
    private String dmsName;
    private long areaId;
    private String areaName;
    private Integer parentSiteCode;
    private String parentSiteName;
    private String subTypes;
    private String memo;
    private Integer traderTypeEbs;
    private Integer accountingOrg;
    private String airTransport;
    private String staffPhoto;
    private String bigStaffPhoto;
    private Integer siteBusinessType;
    private Integer isMiniWarehouse;
    private Double dispatchRange;
    private Integer fixTime;
    private Integer subSiteCode;
    private String subSiteName;
    private String dmsShortName;
    private Integer yn;
    private Date createTime;
    private Date updateTime;
    private Integer operateState;
    private String siteNamePym;
    private String siteMail;
    private String siteContact;
    private String businessHoursStart;
    private String businessHoursEnd;
    private String accountNumber;
    private String roleName;
    private Integer countyId;
    private Integer townId;
    private String connector;
    private String telephone;
    private String outerOrderProduct;
    private String orderTimeEffective;
    private Integer isResign;
    private Integer userType;
    private String erp;
    private String jdAccount;
    private Integer thirdType;
    private String route;
    private String userCode;
    private String provinceCompanyCode;
    private String provinceCompanyName;
    private String areaCode;
    private String partitionCode;
    private String partitionName;
    private String staffSign;
    private String siteSign;
    private String organCode;
    private BigDecimal siteArea;
    private String mobilePhone1;
    private String mobilePhone2;
    private String thirdWebsite;
    private String expand1;
    private String expand2;
    private String displaySiteName;
    private String gbProvinceCode;
    private String gbProvinceName;
    private String gbCityCode;
    private String gbCityName;
    private String gbDistrictCode;
    private String gbDistrictName;
    private String gbTownCode;
    private String gbTownName;
    private String dmsStoreId;
    private Integer collectionDmsId;
    private String bmOrgCode;
    private String organizationFullName;
    private String distributeCode;
    private String organizationCode;
    private String personIdCard;
    private String collectHeavyTransitNo;
    private String collectHeavyTransitName;
    private Integer collectHeavyTransitId;
    private Integer enterpriseDistributeCenter;
    private Integer sortType;
    private Integer sortSubType;
    private Integer sortThirdType;
    private String disableReason;
    private String dpAccount;
    private Integer workMode;
    private String provinceAgencyCode;
    private String provinceAgencyName;
    private Integer internationalFlag;
    private String organizationFullPath;
}
