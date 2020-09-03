package com.jd.bluedragon.distribution.boxlimit.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;

import java.util.List;

public class BoxLimitConfigDao extends BaseDao<SpotCheckInfo> {

    public static final String namespace = BoxLimitConfigDao.class.getName();


    /**
     * 新增一条数据
     */
    public int insert(BoxLimitConfig record) {
        return this.getSqlSession().insert(namespace + ".insert", record);
    }

    /**
     * 批量新增
     */
    public int batchInsert(List<BoxLimitConfig> dataList) {
        return this.getSqlSession().insert(namespace + ".batchInsert", dataList);
    }

    /**
     * 根据ID查询
     */
    public BoxLimitConfig queryById(Integer id) {
        return this.getSqlSession().selectOne(namespace + ".queryById", id);
    }
    /**
     * 根据条件查询
     */
    public List<BoxLimitConfig> queryByCondition(BoxLimitQueryDTO dto) {
        return this.getSqlSession().selectList(namespace + ".queryByCondition", dto);
    }

    /**
     * 根据条件查询
     */
    public Integer countByCondition(BoxLimitQueryDTO dto) {
        return this.getSqlSession().selectOne(namespace + ".countByCondition", dto);
    }

    /**
     * 根据机构ID查询记录是否存在
     */
    public List<BoxLimitConfig> queryBySiteIds(List<Integer> siteIds) {
        return this.getSqlSession().selectOne(namespace + ".queryBySiteIds", siteIds);
    }

    /**
     * 根据ID 修改记录
     */
    public int updateByIdSelective(BoxLimitConfig record) {
        return this.getSqlSession().update(namespace + ".updateByIdSelective", record);
    }

    /**
     * 根据ID 批量删除
     */
    public int batchDelete(List<Integer> ids) {
        return this.getSqlSession().insert(namespace + ".batchDelete", ids);
    }
}
