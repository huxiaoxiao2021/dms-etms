package com.jd.bluedragon.distribution.cyclebox.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public BoxMaterialRelation getDataByMaterialCode(String materialCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getDataByMaterialCode", materialCode);
    }

    /**
     * 根据集包袋编号查询固定条数数据
     * @param materialCode
     * @param limit
     * @return
     */
    public List<BoxMaterialRelation> getLimitDataByMaterialCode(String materialCode, Integer limit){
        Map<String,Object> param = new HashMap<>();
        param.put("materialCode",materialCode);
        param.put("limitNum",limit);
        return this.getSqlSession().selectList(NAMESPACE + ".getLimitDataByMaterialCode", param);

    }
    /**
     * 除了当前箱号，其他和此集包袋的绑定关系都解绑
     */
    public int updateUnBindByMaterialCode(BoxMaterialRelation boxMaterialRelation){
        return this.getSqlSession().update(NAMESPACE + ".updateUnBindByMaterialCode", boxMaterialRelation);

    }
    /**
     * 解绑此箱号和集包袋绑定关系
     * **/
    public int updateUnBindByMaterialCodeAndBoxCode(BoxMaterialRelation boxMaterialRelation){
        return this.getSqlSession().update(NAMESPACE + ".updateUnBindByMaterialCodeAndBoxCode", boxMaterialRelation);

    }


}
