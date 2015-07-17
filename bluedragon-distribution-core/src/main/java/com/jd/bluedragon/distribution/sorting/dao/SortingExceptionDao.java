package com.jd.bluedragon.distribution.sorting.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtingwei on 2014/10/21.
 */
public class SortingExceptionDao extends BaseDao<SortingException> {
    private static final String NAMESPACE=SortingExceptionDao.class.getName();

    public int add(SortingException entity){
        return super.add(SortingExceptionDao.NAMESPACE,entity);
    }

    public List<SortingException> search(String batchCode,Integer siteCode){
        Map map=new HashMap(2);
        map.put("batchCode",batchCode);
        map.put("siteCode",siteCode);
        return super.getSqlSession().selectList(SortingExceptionDao.NAMESPACE+".getListByBatchCodeAndSiteCode",map);
    }
}
