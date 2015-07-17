package com.jd.bluedragon.distribution.departure.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.DepartureTmpRequest;

import java.util.List;

/**
 * Created by dudong on 2015/1/12.
 */
public class DepartureTmpDao extends BaseDao<DepartureTmpRequest> {
    private static final String namespace = DepartureTmpDao.class.getName();

    public int insert(DepartureTmpRequest request) {
        return this.getSqlSession().insert(namespace + ".insert", request);
    }

    public List<DepartureTmpRequest> queryDepartureTmpByBatchCode(String batchKey) {
        return (List<DepartureTmpRequest>) this.getSqlSession().selectList(namespace + ".queryDepartureTmpByBatchCode", batchKey);
    }
}
