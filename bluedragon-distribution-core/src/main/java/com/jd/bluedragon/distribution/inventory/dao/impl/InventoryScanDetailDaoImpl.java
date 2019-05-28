package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @ClassName: InventoryScanDetailDaoImpl
 * @Description: 盘点明细表--Dao接口实现
 *
 */
@Repository("inventoryScanDetailDao")
public class InventoryScanDetailDaoImpl extends BaseDao<InventoryScanDetail> implements InventoryScanDetailDao {
}
