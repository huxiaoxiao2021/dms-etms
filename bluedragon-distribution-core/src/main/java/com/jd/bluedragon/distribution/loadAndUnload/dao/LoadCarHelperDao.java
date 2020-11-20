package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务协助人
 * @author: wuming
 * @create: 2020-10-15 20:21
 */
public class LoadCarHelperDao extends BaseDao<LoadCarHelper> {

    public static final String namespace = LoadCarHelperDao.class.getName();

    public int insert(LoadCarHelper loadCarHelper) {
        return this.getSqlSession().insert(namespace + ".insert", loadCarHelper);
    }

    public int batchInsert(List<LoadCarHelper> dataList){
        return this.getSqlSession().insert(namespace+".batchInsert", dataList);
    }

    public List<Long> selectByCreateUserErp(String loginUserErp){
        return this.getSqlSession().selectList(namespace+".selectByCreateUserErp", loginUserErp);
    }

    public List<Long> selectByHelperErp(String loginUserErp) {
        return this.getSqlSession().selectList(namespace + ".selectByHelperErp", loginUserErp);
    }

    public List<Long> selectIdsByErp(String loginUserErp) {
        return this.getSqlSession().selectList(namespace + ".selectIdsByErp", loginUserErp);
    }

    public int deleteById(Long taskId){
        return this.getSqlSession().update(namespace+".deleteById",taskId);
    }

    public List<String> selectCreateUserErpByTaskId(Long taskId){
        return this.getSqlSession().selectList(namespace + ".selectCreateUserErpByTaskId", taskId);
    }

    public List<String> selectHelperErpByTaskId(Long taskId){
        return this.getSqlSession().selectList(namespace + ".selectHelperErpByTaskId", taskId);
    }
}
