package com.jd.bluedragon.distribution.loadAndUnload.dao.tys;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;

import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/23 11:27
 */
public class UnloadCarForTysDao extends BaseDao<UnloadCar> {

    public static final String namespace = UnloadCarForTysDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCar detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

    public UnloadCar selectBySealCarCode(String sealCarCode) {
        return this.getSqlSession().selectOne(namespace + ".selectBySealCarCode",sealCarCode);
    }

    public UnloadCar selectBySealCarCodeWithStatus(String sealCarCode) {
        return this.getSqlSession().selectOne(namespace + ".selectBySealCarCodeWithStatus",sealCarCode);
    }

    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {
         return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    public int queryCountByCondition(UnloadCarCondition condition) {
         return this.getSqlSession().selectOne(namespace + ".queryCountByCondition",condition);
    }

    public Integer distributeTaskByParams(Map<String, Object> params) {
         return this.getSqlSession().update(namespace + ".distributeTaskByParams", params);
    }

    public List<UnloadCar> getUnloadCarTaskByParams(UnloadCar params) {
        return this.getSqlSession().selectList(namespace + ".getUnloadCarTaskByParams",params);
    }

    public int updateUnloadCarTaskStatus(UnloadCar params) {
        return this.getSqlSession().update(namespace + ".updateUnloadCarTaskStatus", params);
    }

    public List<UnloadCar> getUnloadCarTaskScan(List<String> sealCarCodes) {
        return this.getSqlSession().selectList(namespace + ".getUnloadCarTaskScan", sealCarCodes);
    }

    public List<UnloadCar> selectByUnloadCar(UnloadCar unloadCar) {
        return this.getSqlSession().selectList(namespace + ".selectByUnloadCar", unloadCar);
    }

    public List<UnloadCar> selectTaskByLicenseNumberAndSiteCode(UnloadCar params) {
        return this.getSqlSession().selectList(namespace + ".selectTaskByLicenseNumberAndSiteCode",params);
    }

    public int updateStartTime(UnloadCar params) {
        return this.getSqlSession().update(namespace + ".updateStartTime",params);
    }

    public List<UnloadCar> selectByCondition(UnloadCar unloadCar) {
        return this.getSqlSession().selectList(namespace + ".selectByCondition", unloadCar);
    }
    //
    public List<UnloadCar> getTaskInfoBySealCarCodes(List<String> sealCarCodes) {
        return this.getSqlSession().selectList(namespace + ".getTaskInfoBySealCarCodes", sealCarCodes);
    }


}
