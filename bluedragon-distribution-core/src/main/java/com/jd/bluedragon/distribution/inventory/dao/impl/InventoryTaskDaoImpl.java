package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
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
    public List<InventoryTask> getInventoryTaskBySiteAndUser(Integer createSiteCode,Integer createUserCode){
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("createSiteCode",createSiteCode);
        param.put("createUserCode",createUserCode);
        return sqlSession.selectList(nameSpace+".getInventoryTaskBySiteAndUser", param);
    }


    /**
     * 批量写入盘点任务
     * @param inventoryTaskList
     * @return
     */
    public int addBatch(List<InventoryTask> inventoryTaskList) {
        return this.getSqlSession().insert(nameSpace + ".addBatch", inventoryTaskList);
    }

}
