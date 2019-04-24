package com.jd.bluedragon.distribution.weightAndVolumeCheck.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;

import java.util.List;

/**
 * @ClassName: ReviewWeightSpotCheckDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/23 10:41
 */
public class ReviewWeightSpotCheckDao extends BaseDao<WeightAndVolumeCheck> {

    public static final String namespace = ReviewWeightSpotCheckDao.class.getName();

    /**
     * 批量新增
     * @param dataList
     * @return
     */
    public int batchInsert(List<SpotCheckInfo> dataList) {
        return this.getSqlSession().insert(namespace + ".batchInsert", dataList);
    }

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    public List<SpotCheckInfo> queryByCondition(WeightAndVolumeCheckCondition condition) {
        return this.getSqlSession().selectList(namespace + ".queryByCondition", condition);
    }

    /**
     * 根据机构编码查询
     * @param siteCode
     * @return
     */
    public SpotCheckInfo queryBySiteCode(Integer siteCode) {
        return this.getSqlSession().selectOne(namespace + ".queryBySiteCode", siteCode);
    }

    /**
     * 根据机构编码更新
     * @param spotCheckInfo
     * @return
     */
    public int updateBySiteCode(SpotCheckInfo spotCheckInfo) {
        return this.getSqlSession().update(namespace + ".updateBySiteCode", spotCheckInfo);
    }

    /**
     * 新增一条数据
     * @param spotCheckInfo
     * @return
     */
    public int insert(SpotCheckInfo spotCheckInfo) {
        return this.getSqlSession().insert(namespace + ".insert", spotCheckInfo);
    }
}
