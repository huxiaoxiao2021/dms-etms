package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CancelWaybillDao
 * @Description
 * @Author wangyinhui3
 * @Date 2019/12/18
 **/
public class CancelWaybillDao extends BaseDao<CancelWaybill> {

    private static final String NAMESPACE = CancelWaybillDao.class.getName();

    public List<CancelWaybill> getByWaybillCode(String waybillCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        return super.getSqlSession().selectList(NAMESPACE + ".getByWaybillCode", paramMap);
    }

    public boolean add(CancelWaybill cancelWaybill) {
        cancelWaybill.setTs(System.currentTimeMillis());
        return super.getSqlSession().update(NAMESPACE + ".add", cancelWaybill) > 0 ? true : false;
    }

    /**
     * 根据单号和featureType查询运单拦截记录
     * @param waybillCode
     * @param featureType
     * @return
     */
    public CancelWaybill findWaybillCancelByCodeAndFeatureType(String waybillCode, Integer featureType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("featureType", featureType);
        return super.getSqlSession().selectOne(NAMESPACE + ".findWaybillCancelByCodeAndFeatureType", paramMap);
    }

    /**
     * 根据运单号查询未完成的包裹号
     * @param waybillCode
     * @param featureType
     * @param businessType
     * @param limitCount 最多查询数量
     * @return
     */
    public List<CancelWaybill> findPackageCodesByFeatureTypeAndWaybillCode(String waybillCode, Integer featureType, String businessType, Integer limitCount) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("featureType", featureType);
        paramMap.put("businessType", businessType);
        paramMap.put("limitCount", limitCount);
        return super.getSqlSession().selectList(NAMESPACE + ".findPackageCodesByFeatureTypeAndWaybillCode", paramMap);
    }
    /**
     * 根据运单号查询锁定状态为businessType的包裹号数量
     * @param waybillCode
     * @param featureType
     * @param businessType
     * @return
     */
    public Long findPackageCodeCountByFeatureTypeAndWaybillCode(String waybillCode, Integer featureType, String businessType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("featureType", featureType);
        paramMap.put("businessType", businessType);
        return super.getSqlSession().selectOne(NAMESPACE + ".findPackageCodeCountByFeatureTypeAndWaybillCode", paramMap);
    }

    /**
     * 根据单号和featureType查询运单拦截记录
     * @param packageCode
     * @param featureType
     * @return
     */
    public CancelWaybill findPackageBlockedByCodeAndFeatureType(String packageCode, Integer featureType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("packageCode", packageCode);
        paramMap.put("featureType", featureType);
        return super.getSqlSession().selectOne(NAMESPACE + ".findPackageBlockedByCodeAndFeatureType", paramMap);
    }
    /**
     * 根据运单号和interceptType查询运单拦截记录
     * @param waybillCode
     * @param interceptType
     * @return
     */
	public CancelWaybill getByWaybillCodeAndInterceptType(String waybillCode, Integer interceptType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("interceptType", interceptType);
        return super.getSqlSession().selectOne(NAMESPACE + ".getByWaybillCodeAndInterceptType", paramMap);
	}

    public List<CancelWaybill> findPackageBlockedByCodeAndFeatureTypes(String packageCode, String businessType, List<Integer> featureTypes) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("packageCode", packageCode);
        paramMap.put("businessType", businessType);
        paramMap.put("featureTypes", featureTypes);
        return super.getSqlSession().selectList(NAMESPACE + ".findPackageBlockedByCodeAndFeatureTypes", paramMap);
    }

    public List<CancelWaybill> findWaybillCancelByCodeAndFeatureTypes(String waybillCode, String businessType, List<Integer> featureTypes) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("businessType", businessType);
        paramMap.put("featureTypes", featureTypes);
        return super.getSqlSession().selectList(NAMESPACE + ".findWaybillCancelByCodeAndFeatureTypes", paramMap);
    }

    public int updateByWaybillCodeInterceptType99(String waybillCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        return super.getSqlSession().update(NAMESPACE + ".updateByWaybillCodeInterceptType99", paramMap);
    }

    public int delByWaybillCodeListInterceptType99(List<String> waybillCodeList) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCodeList", waybillCodeList);
        return super.getSqlSession().update(NAMESPACE + ".delByWaybillCodeListInterceptType99", paramMap);
    }

    public long countByWaybillAndBusinessType(String waybillCode, String businessType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        paramMap.put("businessType",businessType);
        return super.getSqlSession().selectOne(NAMESPACE + ".countByWaybillAndBusinessType", paramMap);
    }
}
