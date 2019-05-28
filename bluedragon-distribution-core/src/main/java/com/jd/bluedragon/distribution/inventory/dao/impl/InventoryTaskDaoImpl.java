package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @ClassName: InventoryTaskDaoImpl
 * @Description: 盘点任务表--Dao接口实现
 *
 */
@Repository("inventoryTaskDao")
public class InventoryTaskDaoImpl extends BaseDao<InventoryTask> implements InventoryTaskDao {
}
