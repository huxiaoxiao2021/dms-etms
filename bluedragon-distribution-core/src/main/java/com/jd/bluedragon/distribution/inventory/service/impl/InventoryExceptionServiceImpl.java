package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("inventoryExceptionService")
public class InventoryExceptionServiceImpl extends BaseService<InventoryException> implements InventoryExceptionService {

    @Autowired
    @Qualifier("inventoryExceptionDao")
    private InventoryExceptionDao inventoryExceptionDao;

    @Override
    public Dao<InventoryException> getDao() {
        return this.inventoryExceptionDao;
    }
}

