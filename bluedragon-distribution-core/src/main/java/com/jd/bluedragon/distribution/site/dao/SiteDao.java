package com.jd.bluedragon.distribution.site.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jsf.domain.SiteQuery;
import com.jd.bluedragon.distribution.jsf.domain.SiteResponse;
import com.jd.bluedragon.distribution.ver.domain.Site;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/11/2 14:08
 */
public class SiteDao extends BaseDao<Site> {

    public static final String namespace = SiteDao.class.getName();

    public Site get(Integer siteCode) {
        return this.getSqlSession().selectOne(namespace + ".get",siteCode);
    }


    public List<Site> querySitePage(SiteQuery siteQuery, Integer startIndex, Integer pageSize){

        return getSqlSession().selectList(namespace + ".querySitePage", setParam(siteQuery, startIndex, pageSize));
    }

    public Integer countSitePage(SiteQuery siteQuery, Integer startIndex, Integer pageSize){
        return getSqlSession().selectOne(namespace + ".countSitePage", setParam(siteQuery, startIndex, pageSize));
    }

    private Map<String, Object> setParam(SiteQuery siteQuery, Integer startIndex, Integer pageSize){
        Map<String, Object> param = new HashMap<>();
        if(siteQuery != null){
            param.put("siteCode", siteQuery.getSiteCode());
            param.put("orgId", siteQuery.getOrgId());
            param.put("siteName", siteQuery.getSiteName());
            param.put("siteType", siteQuery.getSiteType());
            param.put("subType", siteQuery.getSubType());
            param.put("subTypes", siteQuery.getSubTypes());
        }
        param.put("startIndex", startIndex);
        param.put("pageSize", pageSize);
        return param;
    }
}
