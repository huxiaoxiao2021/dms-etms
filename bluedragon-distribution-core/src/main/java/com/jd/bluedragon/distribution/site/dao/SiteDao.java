package com.jd.bluedragon.distribution.site.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.ver.domain.Site;

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
}
