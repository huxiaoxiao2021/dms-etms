package com.jd.bluedragon.distribution.cyclebox.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BoxMaterialRelationDao  extends BaseDao<BoxMaterialRelation> {

    private final static Log logger = LogFactory.getLog(BoxMaterialRelationDao.class);
    private final static String NAMESPACE = BoxMaterialRelationDao.class.getName();

    /**
     * 新增
     * @param
     * @return
     */
    public int add(BoxMaterialRelation mode) {
        return this.getSqlSession().insert(NAMESPACE + ".add", mode);
    }

    /**
     * 更新
     * @param mode
     * @return
     */
    public int update(BoxMaterialRelation mode){
        return this.getSqlSession().update(NAMESPACE + ".update", mode);
    }

    /**
     * 根据箱号获取数据条数
     * @param boxCode
     * @return
     */
    public int getCountByBoxCode(String boxCode){
        return this.getSqlSession().selectOne(NAMESPACE + ".getCountByBoxCode", boxCode);
    }

    /**
     * 根据箱号查询单条数据
     * @param boxCode
     * @return
     */
    public BoxMaterialRelation getDataByBoxCode(String boxCode){
        return this.getSqlSession().selectOne(NAMESPACE + ".getDataByBoxCode", boxCode);
    }

    /**
     *根据对象查询符合条件的数据
     * @param parameter
     * @return
     */
    public BoxMaterialRelation getDataByBean(BoxMaterialRelation parameter) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getDataByBean", parameter);
    }

}
