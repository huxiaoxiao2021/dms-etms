package com.jd.bluedragon.distribution.reflowPackage.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reflowPackage.domain.ReflowPackage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ReflowPackageDao extends BaseDao<ReflowPackage> {

    private final static Log logger = LogFactory.getLog(ReflowPackageDao.class);
    private final static String NAMESPACE = ReflowPackageDao.class.getName();

    /**
     * 新增
     * @param
     * @return
     */
    public int add(ReflowPackage mode) {
        return this.getSqlSession().insert(NAMESPACE + ".add", mode);
    }

    /**
     * 更新
     * @param mode
     * @return
     */
    public int update(ReflowPackage mode){
        return this.getSqlSession().update(NAMESPACE + ".update", mode);
    }

    /**
     * 根据对象获取数据条数
     * @param mode
     * @return
     */
    public int getCount(ReflowPackage mode){
        return this.getSqlSession().selectOne(NAMESPACE + ".getCount", mode);
    }

    /**
     *根据对象查询符合条件的数据
     * @param mode
     * @return
     */
    public List<ReflowPackage> getDataByBean(ReflowPackage mode) {
        return this.getSqlSession().selectList(NAMESPACE + ".getDataByBean", mode);
    }

}
