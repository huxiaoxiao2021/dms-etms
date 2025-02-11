package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务
 * @author: wuming
 * @create: 2020-10-15 20:21
 */
public class LoadCarDao extends BaseDao<LoadCar> {

    public static final String namespace = LoadCarDao.class.getName();


    public int insert(LoadCar detail) {
        return this.getSqlSession().insert(namespace + ".insert", detail);
    }

    /**
     * 根据创建人身份erp查看关联的任务
     *
     * @param list
     * @return
     */
    public List<LoadTaskListDto> selectByIds(List<Long> list) {
        return this.getSqlSession().selectList(namespace + ".selectByIds", list);
    }

    public int deleteById(LoadDeleteReq req) {
        return this.getSqlSession().update(namespace + ".deleteById", req);
    }

    public boolean updateLoadCarById(LoadCar loadCar) {
        return this.getSqlSession().update(namespace + ".updateByPrimaryKey", loadCar) > 0;
    }

    public List<Long> selectByCreateUserErp(String loginUserErp) {
        return this.getSqlSession().selectList(namespace + ".selectByCreateUserErp", loginUserErp);
    }

    public List<Long> findLoadCarByBatchCode(String batchCode) {
        return this.getSqlSession().selectList(namespace + ".findLoadCarByBatchCode", batchCode);
    }

    public List<Long> findTaskByBatchCode(String batchCode) {
        return this.getSqlSession().selectList(namespace + ".findTaskByBatchCode", batchCode);
    }

    public List<LoadCar> selectByEndSiteCode(LoadCar loadCar) {
        return this.getSqlSession().selectList(namespace + ".selectByEndSiteCode", loadCar);
    }

    public LoadCar findLoadCarByTaskId(Long id) {
        return this.getSqlSession().selectOne(namespace + ".findLoadCarByTaskId", id);
    }


    public List<String>selectCreateUserErpByTaskId(Long taskId){
        return this.getSqlSession().selectList(namespace + ".selectCreateUserErpByTaskId", taskId);
    }

    public List<Long> getIdsByCondition(LoadCar loadCar) {
        return this.getSqlSession().selectList(namespace + ".getIdsByCondition", loadCar);
    }
}
