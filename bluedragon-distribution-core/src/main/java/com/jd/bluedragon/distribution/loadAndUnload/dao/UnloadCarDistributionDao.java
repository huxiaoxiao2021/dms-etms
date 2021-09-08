package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;

import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/23 11:27
 */
public class UnloadCarDistributionDao extends BaseDao<UnloadCarDistribution> {

    public static final String namespace = UnloadCarDistributionDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCarDistribution detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

    public int updateUnloadUser(UnloadCarDistribution detail){
        return this.getSqlSession().insert(namespace + ".updateUnloadUser",detail);
    }

    public List<String> selectHelperBySealCarCode(String sealCarCode) {
        return this.getSqlSession().selectList(namespace + ".selectHelperBySealCarCode",sealCarCode);
    }

    public List<String> selectUnloadUserBySealCarCode(String sealCarCode) {
        return this.getSqlSession().selectList(namespace + ".selectUnloadUserBySealCarCode",sealCarCode);
    }

    public List<UnloadCarDistribution> selectUnloadCarTaskHelpers(String sealCarCode) {
        return this.getSqlSession().selectList(namespace + ".selectUnloadCarTaskHelpers",sealCarCode);
    }

    public boolean deleteUnloadCarTaskHelpers(UnloadCarDistribution params) {
        return this.getSqlSession().update(namespace + ".deleteUnloadCarTaskHelpers",params) > 0;
    }

    public List<String> selectTasksByUser(String unloadUserErp) {
        return this.getSqlSession().selectList(namespace + ".selectTasksByUser",unloadUserErp);
    }
}
