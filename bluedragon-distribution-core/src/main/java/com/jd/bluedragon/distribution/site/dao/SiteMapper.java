package com.jd.bluedragon.distribution.site.dao;


import com.jd.bluedragon.common.dto.abnormal.response.SiteDto;
import com.jd.bluedragon.distribution.ver.domain.Site;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteMapper {

    List<SiteDto> getByOrgIdAnd(@Param("orgId") String orgId, @Param("siteName") String siteName, @Param("siteCode") String siteCode, @Param("siteTypeList") List<String> siteTypeList);

    Site get(Integer siteCode);

    Site getByName(String name);

    List<Site> fuzzyGetByName(String name);

    List<Site> getPrintSite(Integer siteCode);

    List<Site> getAllDmsSite();

}
