package com.jd.bluedragon.distribution.weightAndMeasure.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.common.util.StringUtils;

import java.util.List;
import java.util.Map;

public class DmsOutWeightAndVolumeDao extends BaseDao<DmsOutWeightAndVolume>{
    private static final String namespace = DmsOutWeightAndVolumeDao.class.getName();
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

    /**
     * 根据箱号/包裹号查询重量和体积信息
     * @param dmsOutWeightAndVolume
     * @return
     */
    public List<DmsOutWeightAndVolume> queryByBarCode(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        if(dmsOutWeightAndVolume == null ||
                dmsOutWeightAndVolume.getCreateSiteCode() == null ||
                StringUtils.isBlank(dmsOutWeightAndVolume.getBarCode())){
            throw new IllegalArgumentException("站点和barCode不能为空.");
        }
        return this.getSqlSession().selectList(namespace + ".queryByBarCode", dmsOutWeightAndVolume);
    }

    /**
     * 根据箱号/包裹号查询重量和体积的已一条记录
     * @param dmsOutWeightAndVolume
     * @return
     */
    public DmsOutWeightAndVolume queryOneByBarCode(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        if(dmsOutWeightAndVolume == null ||
                dmsOutWeightAndVolume.getCreateSiteCode() == null ||
                StringUtils.isBlank(dmsOutWeightAndVolume.getBarCode())){
            throw new IllegalArgumentException("站点和barCode不能为空.");
        }
        return this.getSqlSession().selectOne(namespace + ".queryOneByBarCode", dmsOutWeightAndVolume);
    }
}