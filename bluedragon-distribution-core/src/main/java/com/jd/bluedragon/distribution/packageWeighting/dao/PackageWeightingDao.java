package com.jd.bluedragon.distribution.packageWeighting.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.router.CacheTablePartition;
import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinjingcheng on 2018/4/22.
 */
public class PackageWeightingDao extends BaseDao<PackageWeighting> {
    public static final String namespace = PackageWeightingDao.class.getName();
    private static final Log logger = LogFactory.getLog(PackageWeightingDao.class);

    public List<PackageWeighting> findWeightVolume(String waybillCode, String packageCode, List<Integer> businessTypes) {
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("list", businessTypes);
        parameters.put("waybillCode", waybillCode);
        //扫的运单号分拣的话 sql里判断不一样，库里packagecode
        if (WaybillUtil.isPackageCode(packageCode)) {
            parameters.put("packageCode", packageCode);
        }
        return super.getSqlSession().selectList(PackageWeightingDao.namespace + ".findWeightVolume", parameters);
    }

    public int add(PackageWeighting packageWeighting) {
        return super.getSqlSession().insert(PackageWeightingDao.namespace + ".insert", packageWeighting);
    }

    private Map<String, Object> generateParamMap(String waybillCode) {
        Map<String, Object> paramMap = new HashMap<>();
        String tableName = null;
        paramMap.put("waybillCode", waybillCode);

        //查询前重新计算分配的db和tableName
        String db = CacheTablePartition.getDmsCacheDb(waybillCode);
        tableName = CacheTablePartition.getDmsPackageWeightingCacheTableName(waybillCode);
        paramMap.put("db", db);

        paramMap.put("tableName", tableName);
        return paramMap;
    }

    /**
     * 包裹维度-查询称重流水
     * @param waybillCode
     * @param packageCode
     * @param businessTypes
     */
    public List<PackageWeighting> findPackageWeightFlow(String waybillCode, String packageCode, List<Integer> businessTypes){
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("list", businessTypes);
        parameters.put("waybillCode", waybillCode);
        parameters.put("packageCode", packageCode);
        return super.getSqlSession().selectList(PackageWeightingDao.namespace + ".findPackageWeightFlow", parameters);
    }

    /**
     * 查询最近一条包裹维度的称重流水
     * 
     * @param waybillCode
     * @param packageCode
     * @return
     */
    public PackageWeighting queryPackWeightFlowRecentDetail(String waybillCode, String packageCode, List<Integer> businessTypes){
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("list", businessTypes);
        parameters.put("packageCode", packageCode);
        return super.getSqlSession().selectOne(PackageWeightingDao.namespace + ".queryPackWeightFlowRecentDetail", parameters);
    }

    /**
     * 运单维度-查询称重流水
     * @param waybillCode
     * @param businessTypes
     */
    public List<PackageWeighting> findWaybillWeightFlow(String waybillCode, List<Integer> businessTypes) {
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("list", businessTypes);
        parameters.put("waybillCode", waybillCode);
        return super.getSqlSession().selectList(PackageWeightingDao.namespace + ".findWaybillWeightFlow", parameters);
    }


    /**
     * 查询所有称重流水
     * @param waybillCode
     * @param businessTypes
     */
    public List<PackageWeighting> findAllPackageWeightFlow(String waybillCode, List<Integer> businessTypes) {
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("list", businessTypes);
        parameters.put("waybillCode", waybillCode);
        return super.getSqlSession().selectList(PackageWeightingDao.namespace + ".findAllPackageWeightFlow", parameters);
    }
}
