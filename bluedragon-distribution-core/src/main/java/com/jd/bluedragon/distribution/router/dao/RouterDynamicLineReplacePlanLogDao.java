package com.jd.bluedragon.distribution.router.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlanLog;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanLogQuery;

import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class RouterDynamicLineReplacePlanLogDao extends BaseDao<RouterDynamicLineReplacePlanLog> {

    final static String NAMESPACE = RouterDynamicLineReplacePlanLogDao.class.getName();

    public int insertSelective(RouterDynamicLineReplacePlanLog routerDynamicLineReplacePlanLog) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", routerDynamicLineReplacePlanLog);
    }

    public RouterDynamicLineReplacePlanLog selectOne(RouterDynamicLineReplacePlanLogQuery routerDynamicLineReplacePlanLogQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOne", routerDynamicLineReplacePlanLogQuery);
    }

    public List<RouterDynamicLineReplacePlanLog> queryList(RouterDynamicLineReplacePlanLogQuery routerDynamicLineReplacePlanLogQuery) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryList", routerDynamicLineReplacePlanLogQuery);
    }
}
