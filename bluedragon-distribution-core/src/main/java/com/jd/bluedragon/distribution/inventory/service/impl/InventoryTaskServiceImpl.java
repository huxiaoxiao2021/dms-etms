package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("inventoryTaskService")
public class InventoryTaskServiceImpl extends BaseService<InventoryTask> implements InventoryTaskService {
    @Autowired
    @Qualifier("inventoryTaskDao")
    private InventoryTaskDao inventoryTaskDao;

    @Override
    public Dao<InventoryTask> getDao() {
        return this.inventoryTaskDao;
    }
}
