package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.jsf.domain.SiteQuery;
import com.jd.bluedragon.distribution.jsf.domain.SiteResponse;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;


/**
 * 站点jsf接口
 */
public interface SiteJsfService {
    /**
     * @param siteQuery
     * @return
     */
    JdResponse<PageDto<SiteResponse>> querySitePage(PageDto<SiteQuery> siteQuery);


}
