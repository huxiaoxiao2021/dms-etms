package com.jd.bluedragon.distribution.stock.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalorder.dao.AbnormalOrderDao;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;

import java.util.List;
import java.util.Map;

/**
 * 库存盘点DAO
 *
 * @author hujiping
 * @date 2021/6/4 2:47 下午
 */
public class StockInventoryDao extends BaseDao<StockInventory> {

    public static final String NAMESPACE = StockInventoryDao.class.getName();

    public int add(StockInventory stockInventory){
        return super.getSqlSession().insert(NAMESPACE + ".add" , stockInventory);
    }

    public int batchAdd(List<StockInventory> list){
        return super.getSqlSession().insert(NAMESPACE + ".batchAdd" , list);
    }

    public int queryInventoryNum(StockInventory stockInventory) {
        return super.getSqlSession().selectOne(NAMESPACE + ".queryInventoryNum" , stockInventory);
    }

    /**
     * 查询相同波次相同包裹的盘点记录
     * @param stockInventory
     * @return
     */
    public List<StockInventory> queryRecordsByPack(StockInventory stockInventory) {
        return super.getSqlSession().selectList(NAMESPACE + ".queryRecordsByPack", stockInventory);
    }

    public List<StockInventory> queryInventoryUnSendPacks(StockInventory stockInventory, List<String> list) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("list", list);
        param.put("waveCode", stockInventory.getWaveCode());
        param.put("operateSiteCode", stockInventory.getOperateSiteCode());
        param.put("inventoryStatus", stockInventory.getInventoryStatus());
        return super.getSqlSession().selectList(NAMESPACE + ".queryInventoryUnSendPacks", param);
    }

    public int updateStatusByErp(StockInventory queryCondition, List<String> list) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("packageCode", queryCondition.getPackageCode());
        param.put("waveCode", queryCondition.getWaveCode());
        param.put("operateSiteCode", queryCondition.getOperateSiteCode());
        param.put("operateUserErp", queryCondition.getOperateUserErp());
        param.put("list", list);
        return super.getSqlSession().update(NAMESPACE + ".updateStatusByErp", param);
    }

}
