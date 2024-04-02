package com.jd.bluedragon.distribution.router.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;

import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2023-11-12 14:09:38 周日
 */
public class RouterDynamicLineReplacePlanDao extends BaseDao<RouterDynamicLineReplacePlanQuery> {

    final static String NAMESPACE = RouterDynamicLineReplacePlanDao.class.getName();

    public int insertSelective(RouterDynamicLineReplacePlanQuery jyRouterDynamicLineReplacePlanQuery) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", jyRouterDynamicLineReplacePlanQuery);
    }

    public RouterDynamicLineReplacePlan selectOne(RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOne", routerDynamicLineReplacePlanQuery);
    }

    public List<RouterDynamicLineReplacePlan> queryList(RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryList", routerDynamicLineReplacePlanQuery);
    }

    public RouterDynamicLineReplacePlan updateByBizId(RouterDynamicLineReplacePlanQuery jyRouterDynamicLineReplacePlanQuery) {
        return this.getSqlSession().selectOne(NAMESPACE + ".updateByBizId", jyRouterDynamicLineReplacePlanQuery);
    }

}
