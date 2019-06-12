package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryCustomException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.bluedragon.distribution.inventory.service.InventoryScanDetailService;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("inventoryScanDetailService")
public class InventoryScanDetailServiceImpl extends BaseService<InventoryScanDetail> implements InventoryScanDetailService{

    @Autowired
    @Qualifier("inventoryScanDetailDao")
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public Dao<InventoryScanDetail> getDao() {
        return this.inventoryScanDetailDao;
    }

    @Override
    public int insert(InventoryScanDetail inventoryScanDetail) throws InventoryCustomException {
        String waybillCode = inventoryScanDetail.getWaybillCode();
        String packageCode = inventoryScanDetail.getPackageCode();
        int packageNum = 1;
        if (packageCode != null) {
            //按包裹号扫描，直接存库
            inventoryScanDetailDao.saveOrUpdate(inventoryScanDetail);
        } else {
            //按运单号扫描，需要获取该运单的包裹列表，进行逐一拆解
            List<DeliveryPackageD> packList = waybillQueryManager.findWaybillPackList(waybillCode);
            if (packList == null || packList.size() == 0) {
                //抛出一个自定义的异常
                String message = "【" + waybillCode + "】无包裹信息，无法进行盘点！";
                logger.error(message);
                throw new InventoryCustomException(message);
            }
            packageNum = packList.size();
            for (DeliveryPackageD pack : packList) {
                inventoryScanDetail.setPackageCode(pack.getPackageBarcode());
                inventoryScanDetailDao.saveOrUpdate(inventoryScanDetail);
            }
        }
        return packageNum;
    }

    @Override
    public InventoryScanDetail convert2InventoryBaseRequest(InventoryBaseRequest inventoryBaseRequest) {
        InventoryScanDetail inventoryScanDetail = new InventoryScanDetail();
        inventoryScanDetail.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
        inventoryScanDetail.setCreateSiteCode(inventoryBaseRequest.getCreateSiteCode());
        inventoryScanDetail.setCreateSiteName(inventoryBaseRequest.getCreateSiteName());
        inventoryScanDetail.setOperatorCode(inventoryBaseRequest.getOperateUserCode());
        inventoryScanDetail.setOperatorName(inventoryBaseRequest.getOperateUserName());
        inventoryScanDetail.setOperatorErp(inventoryBaseRequest.getOperateUserErp());
        Date date = new Date();
        inventoryScanDetail.setOperateTime(date);
        inventoryScanDetail.setCreateTime(date);
        inventoryScanDetail.setUpdateTime(date);
        return inventoryScanDetail;
    }
}
