package com.jd.bluedragon.distribution.boxlimit.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;

import java.util.List;

public class BoxLimitConfigDao extends BaseDao<BoxLimitConfig> {
    public static final String namespace = BoxLimitConfigDao.class.getName();

    public int deleteByPrimaryKey(Integer id){
        return this.getSqlSession().delete(namespace+".deleteByPrimaryKey", id);
    }

    public int insert(BoxLimitConfig record){
        return this.getSqlSession().insert(namespace+".insert", record);
    }


    public int insertSelective(BoxLimitConfig record){
        return this.getSqlSession().insert(namespace+".insertSelective", record);
    }

    public BoxLimitConfig selectByPrimaryKey(Integer id){
        return this.getSqlSession().selectOne(namespace+".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(BoxLimitConfig record){
        return this.getSqlSession().update(namespace+".updateByPrimaryKeySelective", record);
    }

    public int updateByPrimaryKey(BoxLimitConfig record){
        return this.getSqlSession().update(namespace+".updateByPrimaryKey", record);
    }

    /**
     * 批量新增
     */
    public int batchInsert(List<BoxLimitConfig> dataList){
        return this.getSqlSession().insert(namespace+".batchInsert", dataList);
    }

    /**
     * 根据条件查询
     */
    public List<BoxLimitConfig> queryByCondition(BoxLimitQueryDTO dto){
        return this.getSqlSession().selectList(namespace+".queryByCondition", dto);
    }

    /**
     * 根据条件查询
     */
    public Integer countByCondition(BoxLimitQueryDTO dto){
        return this.getSqlSession().selectOne(namespace+".countByCondition", dto);
    }

    /**
     * 根据机构ID查询记录是否存在
     */
    public List<BoxLimitConfig> queryBySiteIds(List<Integer> siteIds){
        return this.getSqlSession().selectList(namespace+".queryBySiteIds", siteIds);
    }

    /**
     * 根据ID 批量删除
     */
    public int batchDelete(List<Integer> ids){
        return this.getSqlSession().delete(namespace+".batchDelete", ids);
    }
}