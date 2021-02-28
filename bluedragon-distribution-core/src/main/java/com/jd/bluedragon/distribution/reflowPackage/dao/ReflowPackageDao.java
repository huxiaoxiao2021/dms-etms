package com.jd.bluedragon.distribution.reflowPackage.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.request.ReflowPackageQuery;
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

    /**
     * 按条件统计查询条数
     * @param query 查询参数
     * @return 结果总条数
     * @author fanggang7
     * @time 2019-12-20 18:41:01 周五
     */
    public long selectCount(ReflowPackageQuery query){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectCount", query);
    }

    /**
     * 按条件查询列表
     *
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2019-12-20 19:21:37 周五
     */
    public List<ReflowPackage> selectList(ReflowPackageQuery query){
        return this.getSqlSession().selectList(NAMESPACE + ".selectList", query);
    }

}
