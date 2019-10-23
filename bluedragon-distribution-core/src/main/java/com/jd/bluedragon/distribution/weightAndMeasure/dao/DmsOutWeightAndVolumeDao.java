package com.jd.bluedragon.distribution.weightAndMeasure.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.common.util.StringUtils;

import java.util.HashMap;
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

    /**
     * 批量查询分拣中心对该箱号/包裹的记录
     *
     * @param barCodeList
     * @param createSiteCode
     * @return
     */
    public List<DmsOutWeightAndVolume> queryListByBarCodes(List<String> barCodeList, Integer createSiteCode) {
        if (barCodeList == null || barCodeList.size() == 0 || createSiteCode == null) {
            throw new IllegalArgumentException("站点和barCodeList不能为空。");
        }

        Map<String, Object> parameter = new HashMap<>(2);
        parameter.put("barCodeList", barCodeList);
        parameter.put("createSiteCode", createSiteCode);
        return this.getSqlSession().selectList(namespace + ".queryListByBarCodes", parameter);
    }

}