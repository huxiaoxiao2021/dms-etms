package com.jd.bluedragon.distribution.spare.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.spare.domain.Spare;

public class SpareDao extends BaseDao<Spare> {
    
    public static final String namespace = SpareDao.class.getName();
    
    public Spare findBySpareCode(String code) {
        return (Spare) super.getSqlSession().selectOne(SpareDao.namespace + ".findBySpareCode",
                code);
    }
    
    @SuppressWarnings("unchecked")
    public List<Spare> findSpares(Spare spare) {
        return super.getSqlSession().selectList(SpareDao.namespace + ".findSpares", spare);
    }
    
}
