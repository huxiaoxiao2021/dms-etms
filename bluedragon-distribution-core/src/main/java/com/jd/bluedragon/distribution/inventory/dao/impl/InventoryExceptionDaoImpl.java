package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @ClassName: InventoryExceptionDaoImpl
 * @Description: 盘点异常表--Dao接口实现
 *
 */
@Repository("inventoryExceptionDao")
public class InventoryExceptionDaoImpl extends BaseDao<InventoryException> implements InventoryExceptionDao {
}
