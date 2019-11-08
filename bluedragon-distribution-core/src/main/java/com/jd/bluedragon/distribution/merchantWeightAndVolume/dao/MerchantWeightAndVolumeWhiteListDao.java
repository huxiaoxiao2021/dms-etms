package com.jd.bluedragon.distribution.merchantWeightAndVolume.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;

import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/11/5 14:12
 */
public class MerchantWeightAndVolumeWhiteListDao extends BaseDao<MerchantWeightAndVolumeDetail> {

    public static final String namespace = MerchantWeightAndVolumeWhiteListDao.class.getName();

    /**
     * 获取商家、站点
     * @return
     */
    public List<MerchantWeightAndVolumeDetail> getAllMerchantAndSite() {
        return this.getSqlSession().selectList(namespace + ".getAllMerchantAndSite");
    }

    /**
     * 新增
     * @param detail
     * @return*/
    public int insert(MerchantWeightAndVolumeDetail detail){
        return this.getSqlSession().insert(namespace + ".insert",detail);
    }

    /**
     * 批量新增
     * @param detailList
     * @return*/
    public int batchInsert(List<MerchantWeightAndVolumeDetail> detailList){
        return this.getSqlSession().insert(namespace + ".batchInsert",detailList);
    }

    /**
     * 查询
     * @param condition
     * @return*/
    public List<MerchantWeightAndVolumeDetail> queryByCondition(MerchantWeightAndVolumeCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    /**
     * 查询总数
     * @param condition
     * @return*/
    public Integer queryCountByCondition(MerchantWeightAndVolumeCondition condition){
        return this.getSqlSession().selectOne(namespace + ".queryCountByCondition",condition);
    }

    /**
     * 根据商家编码、站点查询
     * @param detail
     * @return*/
    public MerchantWeightAndVolumeDetail queryByMerchantCodeAndSiteCode(MerchantWeightAndVolumeDetail detail){
        return this.getSqlSession().selectOne(namespace + ".queryByMerchantCodeAndSiteCode",detail);
    }

    /**
     * 导出
     * @param condition
     * @return*/
    public List<MerchantWeightAndVolumeDetail> exportByCondition(MerchantWeightAndVolumeCondition condition){
        return this.getSqlSession().selectList(namespace + ".exportByCondition",condition);
    }

    /**
     * 删除
     * @param detail
     * @return*/
    public int delete(MerchantWeightAndVolumeDetail detail) {
        return this.getSqlSession().insert(namespace + ".delete",detail);
    }

}
