package com.jd.bluedragon.distribution.weightAndMeasure.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;

import java.util.List;
import java.util.Map;

public class DmsOutWeightAndVolumeDao extends BaseDao<DmsOutWeightAndVolume>{
    private static final String namespace = DmsOutWeightAndVolumeDao.class.getName();

    /**
     * 根据箱号/包裹号查询重量和体积信息
     * @param condition
     * @return
     */
    public List<DmsOutWeightAndVolume> queryByBarCode(Map<String,Object> condition){
        return this.getSqlSession().selectOne(namespace + ".queryByBarCode", condition);
    }

    /**
     * 新增一条记录
     * @param dmsOutWeightAndVolume
     * @return
     */
    public int add(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        return this.getSqlSession().insert(namespace+".add",dmsOutWeightAndVolume);
    }

    /**
     * 更新一条记录
     * @param dmsOutWeightAndVolume
     * @return
     */
    public int update(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        return this.getSqlSession().insert(namespace+".update",dmsOutWeightAndVolume);
    }
}