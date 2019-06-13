package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
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
            if (StringHelper.isEmpty(packageCode)) {
                logger.error("【generateInventoryException】包裹号为空，对象值：" + JsonHelper.toJson(inventoryPackage));
                continue;
            }
            inventoryWaybillDetail.setPackageCode(packageCode);

            //存在扫描记录，将实体添加到已扫的map中，后续统计已扫描信息时根据map拿到相应状态信息
            if (scanPackageCodeSet != null && scanPackageCodeSet.contains(packageCode)) {
                //正常已盘
                competeInventoryPackageCodeSet.add(packageCode);
            } else {
                //少货，组装异常数据插入数据库
                InventoryException inventoryException = this.convert2InventoryException(inventoryBaseRequest);
                inventoryException.setPackageCode(packageCode);
                inventoryException.setWaybillCode(inventoryPackage.getWaybillCode());
                inventoryException.setDirectionCode(inventoryPackage.getDirectionCode());
                inventoryException.setDirectionName(inventoryPackage.getDirectionName());
                inventoryException.setLatestPackStatus(inventoryPackage.getStatusDesc());
                inventoryException.setExpType(InventoryExpTypeEnum.INVENTORY_EXCEPT_TYPE_LOSS.getCode());
                inventoryException.setExpDesc(this.getInventoryExpDesc(inventoryPackage.getStatusCode()));
                try {
                    this.inventoryExceptionDao.insert(inventoryException);

                } catch (Exception e) {
                    logger.error("插入盘点异常表异常！",e);
                }
            }
        }

        //从数据库中取出该任务下所有扫描记录
        List<InventoryScanDetail> inventoryScanDetailList = inventoryScanDetailDao.getScanDetailByParam(inventoryTaskId);
        //遍历所有结果组装返回值
        for (InventoryScanDetail inventoryScanDetail : inventoryScanDetailList) {
            String packageCode = inventoryScanDetail.getPackageCode();
            //扫描结果过滤掉属于待盘的记录，剩下的为多货情况
            if (! competeInventoryPackageCodeSet.contains(packageCode)) {
                //多货，组装异常数据插入数据库
                InventoryException inventoryException = this.convert2InventoryException(inventoryBaseRequest);
                inventoryException.setPackageCode(packageCode);
                inventoryException.setWaybillCode(inventoryScanDetail.getWaybillCode());
                inventoryException.setDirectionCode(inventoryScanDetail.getDirectionCode());
                inventoryException.setDirectionName(inventoryScanDetail.getDirectionName());
                inventoryException.setInventoryUserCode(inventoryScanDetail.getOperatorCode());
                inventoryException.setInventoryUserName(inventoryScanDetail.getOperatorName());
                inventoryException.setInventoryUserErp(inventoryScanDetail.getOperatorErp());
                inventoryException.setExpType(InventoryExpTypeEnum.INVENTORY_EXCEPT_TYPE_MORE.getCode());

                List<String> packageCodeList = new ArrayList<>();
                packageCodeList.add(packageCode);
                List<InventoryPackage> inventoryPackages = inventoryInfoService.queryPackageStatusList(inventoryBaseRequest, packageCodeList);
                if (inventoryPackages != null && ! inventoryPackages.isEmpty()) {
                    Integer directionCode = inventoryPackages.get(0).getDirectionCode();
                    List<Integer> directionCodeList = inventoryBaseRequest.getDirectionCodeList();
                    if (InventoryScopeEnum.CUSTOMIZE.getCode().equals(inventoryBaseRequest.getInventoryScope()) && ! directionCodeList.contains(directionCode)) {
                        //流向异常有实物
                        inventoryException.setExpDesc(InventoryExpDescEnum.DIRECTION_EXCEPTION_MORE.getDesc());
                    } else {
                        Integer statusCode =  inventoryPackages.get(0).getStatusCode();
                        if (PackStatusEnum.SEND.getCode().equals(statusCode)) {
                            //已发货有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.SEND_MORE.getDesc());
                        } else {
                            logger.warn("包裹状态妈：" + statusCode.toString() + "，不是发货状态！");
                        }
                    }
                } else {
                    //无任何操作有实物
                    inventoryException.setExpDesc(InventoryExpDescEnum.NO_OPERATION_MORE.getDesc());
                }
                try {
                    this.inventoryExceptionDao.insert(inventoryException);
                } catch (Exception e) {
                    logger.error("插入盘点异常表异常！",e);
                }
            }
        }
    }

    private InventoryException convert2InventoryException(InventoryBaseRequest inventoryBaseRequest) {
        InventoryException inventoryException = new InventoryException();
        inventoryException.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
        inventoryException.setInventorySiteCode(inventoryBaseRequest.getCreateSiteCode());
        inventoryException.setInventorySiteName(inventoryBaseRequest.getCreateSiteName());
        inventoryException.setCreateTime(new Date());
        return inventoryException;
    }

    private String getInventoryExpDesc(Integer packStatus) {

        if (packStatus.equals(PackStatusEnum.RECEIVE.getCode())) {
            //已收货无实物
            return InventoryExpDescEnum.RECEIVE_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.INSPECTION.getCode())) {
            //已验货无实物
            return InventoryExpDescEnum.INSPECTION_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.REPRINT.getCode())) {
            //包裹补打无实物
            return InventoryExpDescEnum.REPRINT_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SORTING.getCode())) {
            //已分拣无实物
            return InventoryExpDescEnum.SORTING_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SORTING_CANCEL.getCode())) {
            //取消分拣无实物
            return InventoryExpDescEnum.SEND_CANCEL_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SEND_CANCEL.getCode())) {
            //取消发货无实物
            return InventoryExpDescEnum.SEND_CANCEL_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.EXCEPTION.getCode())) {
            //异常外呼无实物
            return InventoryExpDescEnum.EXCEPTION_LOSS.getDesc();
        } else {
            return null;
        }
    }

}

