package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.bluedragon.distribution.inventory.domain.InventoryWaybillDetail;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.report.inventory.InventoryJsfService;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("inventoryExceptionService")
public class InventoryExceptionServiceImpl extends BaseService<InventoryException> implements InventoryExceptionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("inventoryExceptionDao")
    private InventoryExceptionDao inventoryExceptionDao;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Override
    public Dao<InventoryException> getDao() {
        return this.inventoryExceptionDao;
    }

    @Override
    public void generateInventoryException(InventoryBaseRequest inventoryBaseRequest) {

        List<InventoryWaybillDetail> scannedWaybillDetailList = new ArrayList<>();
        //暂存包裹状态
        Set<String> competeInventoryPackageCodeSet = new HashSet<>();
        Set<String> scanPackageCodeSet = null;
        String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
        //从数据库中取出当前任务号下，所有包裹扫描记录的包裹号
        List<String> scanPackageCodeList = inventoryScanDetailDao.getScanPackageCodeByParam(inventoryTaskId);
        if (scanPackageCodeList.size() > 0) {
            scanPackageCodeSet = new HashSet<>(scanPackageCodeList);
        } else {
            logger.warn("任务号【" + inventoryTaskId + "】下扫描记录数据为空！");
        }

        //es获取该运单的待盘信息
        List<InventoryPackage> inventoryPackageList = inventoryInfoService.queryNeedInventoryPackageList(inventoryBaseRequest);
        //遍历结果，组装参数
        for (InventoryPackage inventoryPackage : inventoryPackageList) {
            InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
            String packageCode = inventoryPackage.getPackageCode();
            inventoryWaybillDetail.setPackageCode(packageCode);

            //存在扫描记录，将实体添加到已扫的map中，后续统计已扫描信息时根据map拿到相应状态信息
            if (scanPackageCodeSet != null && scanPackageCodeSet.contains(packageCode)) {
                //正常已盘
                competeInventoryPackageCodeSet.add(packageCode);
            } else {
                //少货，组装异常数据插入数据库
                //已验货无实物
                //已收货无实物
                //已分拣无实物
                //包裹补打无实物
                //取消发货无实物
                //取消分拣无实物
            }
        }

        //从数据库中取出该运单下的所有扫描记录
        List<InventoryScanDetail> inventoryScanDetailList = inventoryScanDetailDao.getScanDetailByParam(inventoryTaskId);
        //遍历所有结果组装返回值
        for (InventoryScanDetail inventoryScanDetail : inventoryScanDetailList) {
            InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
            String packageCode = inventoryScanDetail.getPackageCode();
            String operateUserErp = inventoryScanDetail.getOperatorErp();
            inventoryWaybillDetail.setPackageCode(packageCode);
            //扫描结果过滤掉已完成盘点的记录，剩下的为多货情况
            if (! competeInventoryPackageCodeSet.contains(packageCode)) {
                //多货，组装异常数据插入数据库
                //1.已发货有实物
                //2.无任何扫描有实物
                //3.流向异常有实物
            }
        }
    }
}

