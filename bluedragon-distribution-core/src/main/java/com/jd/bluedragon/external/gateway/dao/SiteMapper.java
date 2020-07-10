package com.jd.bluedragon.external.gateway.dao;


import com.jd.bluedragon.external.gateway.domain.Site;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteMapper {

    List<Site> getByOrgIdAnd(@Param("orgId") String orgId, @Param("siteName") String siteName, @Param("siteCode") String siteCode, @Param("siteTypeList") List<String> siteTypeList);

}
