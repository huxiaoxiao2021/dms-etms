package com.jd.bluedragon.distribution.site.dao;


import com.jd.bluedragon.common.dto.abnormal.response.Site;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteMapper {

    List<Site> getByOrgIdAnd(@Param("orgId") String orgId, @Param("siteName") String siteName, @Param("siteCode") String siteCode, @Param("siteTypeList") List<String> siteTypeList);

}
