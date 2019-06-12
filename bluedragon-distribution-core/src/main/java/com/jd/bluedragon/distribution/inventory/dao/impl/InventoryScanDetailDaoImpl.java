package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InventoryScanDetailDaoImpl
 * @Description: 盘点明细表--Dao接口实现
 *
 */
@Repository("inventoryScanDetailDao")
public class InventoryScanDetailDaoImpl extends BaseDao<InventoryScanDetail> implements InventoryScanDetailDao {

    @Override
    public List<InventoryScanDetail> getScanDetailByParam(String inventoryTaskId) {
        Map<String, Object> params = new HashMap<>();
        params.put("inventoryTaskId", inventoryTaskId);
        return sqlSession.selectList(nameSpace+".getScanDetailByParam", params);
    }

    @Override
    public List<InventoryScanDetail> getScanDetailByParam(String inventoryTaskId, String waybillCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("inventoryTaskId", inventoryTaskId);
        params.put("waybillCode", waybillCode);
        return sqlSession.selectList(nameSpace+".getScanDetailByParam", params);
    }

    @Override
    public List<String> getScanPackageCodeByParam(String inventoryTaskId) {
        Map<String, Object> params = new HashMap<>();
        params.put("inventoryTaskId", inventoryTaskId);
        return sqlSession.selectList(nameSpace+".getScanPackageCodeByParam", params);

    }

    @Override
    public List<String> getScanPackageCodeByParam(String inventoryTaskId, String waybillCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("inventoryTaskId", inventoryTaskId);
        params.put("waybillCode", waybillCode);
        return sqlSession.selectList(nameSpace+".getScanPackageCodeByParam", params);

    }

    @Override
    public int getScanCountByParam(String inventoryTaskId, String packageCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("inventoryTaskId", inventoryTaskId);
        params.put("packageCode", packageCode);
        return sqlSession.selectOne(nameSpace+".getScanCountByParam", params);
    }

    @Override
    public boolean saveOrUpdate(InventoryScanDetail inventoryScanDetail) {
        int count = this.getScanCountByParam(inventoryScanDetail.getInventoryTaskId(), inventoryScanDetail.getPackageCode());
        if (count > 0) {
            return this.update(inventoryScanDetail);
        } else {
            return this.insert(inventoryScanDetail);
        }
    }

}
