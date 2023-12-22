package com.jd.bluedragon.utils;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public class SiteDesensitization {

    public static void desensitizeBaseStaffSiteOrgDto(BaseStaffSiteOrgDto baseStaffSiteOrgDto) {
        if (baseStaffSiteOrgDto == null) {
            return;
        }
        baseStaffSiteOrgDto.setAddress(null);
        baseStaffSiteOrgDto.setSitePhone(null);
        baseStaffSiteOrgDto.setMobilePhone1(null);
//        baseStaffSiteOrgDto.setStaffName(null);
        baseStaffSiteOrgDto.setAccountNumber(null);
//        baseStaffSiteOrgDto.setErp(null);
        baseStaffSiteOrgDto.setSitePhone(null);
        baseStaffSiteOrgDto.setSiteContact(null);
        baseStaffSiteOrgDto.setSitePhone(null);
        baseStaffSiteOrgDto.setPhone(null);
    }
}