package com.jd.bluedragon.distribution.departure.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.departure.domain.DepartureLog;

/**
 * Created by dudong on 2014/11/20.
 */
public class DepartureLogDao extends BaseDao<DepartureLog> {
    private static final String namespace = DepartureLogDao.class.getName();

    public int insert(DepartureLog departureLog) {
        return this.getSqlSession().insert(namespace + ".insert", departureLog);
    }

    public int findByFingerPrint(String fingerPrint) {
        return (Integer) this.getSqlSession().selectOne(namespace + ".findByFingerPrint", fingerPrint);
    }
}
