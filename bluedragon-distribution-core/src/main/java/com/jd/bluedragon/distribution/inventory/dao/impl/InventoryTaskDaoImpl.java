package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InventoryTaskDaoImpl
 * @Description: 盘点任务表--Dao接口实现
 *
 */
@Repository("inventoryTaskDao")
public class InventoryTaskDaoImpl extends BaseDao<InventoryTask> implements InventoryTaskDao {

    /**
     * 批量写入盘点任务
     * @param inventoryTaskList
     * @return
     */
    public int addBatch(List<InventoryTask> inventoryTaskList) {
        if(inventoryTaskList == null || inventoryTaskList.size() < 1){
            return 1;
        }
        return this.getSqlSession().insert(nameSpace + ".addBatch", inventoryTaskList);
    }

    /**
     * 根据盘点任务id获取盘点任务
     * @param inventoryTaskId
     * @return
     */
    public List<InventoryTask> getInventoryTaskByTaskId(String inventoryTaskId){
        if(StringUtils.isBlank(inventoryTaskId)){
            return null;
        }
        return this.getSqlSession().selectList(nameSpace + ".getInventoryTaskByTaskId", inventoryTaskId);
    }

    public boolean updateTime(InventoryTask task) {
        return sqlSession.update(this.nameSpace+".updateTime", task) >0;
    }

    public boolean updateStatus(InventoryTask task) {
        return sqlSession.update(this.nameSpace+".updateStatus", task) > 0;
    }
    /**
     * 根据流向/盘点范围获取盘点任务
     * @param createSiteCode
     * @param directionCodeList
     * @param scope
     * @return
     */
    public List<InventoryTask> getInventoryTaskByDirectionOrScope(Integer createSiteCode, List<Integer> directionCodeList, Integer scope){
        Map<String,Object> param = new HashMap();
        param.put("createSiteCode",createSiteCode);
        param.put("directionCodeList",directionCodeList);
        param.put("inventoryScope",scope);
        return this.getSqlSession().selectList(nameSpace + ".getInventoryTaskByDirectionOrScope", param);
    }


    /**
     * 根据任务创建人编码获取盘点任务
     * @param createSiteCode
     * @param createUserCode
     * @return
     */
    public List<InventoryTask> getInventoryTaskByCreateUser(Integer createSiteCode,Integer createUserCode){
        Map<String,Object> param = new HashMap();
        param.put("createSiteCode",createSiteCode);
        param.put("createUserCode",createUserCode);
        return this.getSqlSession().selectList(nameSpace + ".getInventoryTaskByCreateUser", param);
    }

}
