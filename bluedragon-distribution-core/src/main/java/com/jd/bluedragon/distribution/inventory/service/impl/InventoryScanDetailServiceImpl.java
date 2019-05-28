package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.bluedragon.distribution.inventory.service.InventoryScanDetailService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("inventoryScanDetailService")
public class InventoryScanDetailServiceImpl extends BaseService<InventoryScanDetail> implements InventoryScanDetailService{

    @Autowired
    @Qualifier("inventoryScanDetailDao")
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Override
    public Dao<InventoryScanDetail> getDao() {
        return this.inventoryScanDetailDao;
    }
}
