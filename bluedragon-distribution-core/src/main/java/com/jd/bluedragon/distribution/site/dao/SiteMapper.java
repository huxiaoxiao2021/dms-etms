package com.jd.bluedragon.distribution.site.dao;


import com.jd.bluedragon.common.dto.abnormal.response.SiteDto;
import com.jd.bluedragon.distribution.ver.domain.Site;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SiteMapper {

    Site get(Integer siteCode);

}
