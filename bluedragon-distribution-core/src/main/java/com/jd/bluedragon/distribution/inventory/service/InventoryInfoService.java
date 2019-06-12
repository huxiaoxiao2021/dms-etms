package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryWaybillDetail;
import com.jd.bluedragon.distribution.inventory.domain.InventoryWaybillResponse;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import com.jd.ql.dms.report.inventory.domain.InventoryWaybillSummary;

import java.util.List;

public interface InventoryInfoService {

    /*
    * 获取盘点统计信息列表
    * */
    List<InventoryWaybillSummary> queryNeedInventoryWaybillSummaryList(InventoryBaseRequest inventoryBaseRequest);

    /*
     * 获取盘点明细列表
     * */
    List<InventoryPackage> queryNeedInventoryPackageList(InventoryBaseRequest inventoryBaseRequest);

    /*
     * 初始化待盘运单信息
     * */
    InventoryWaybillResponse initNeedInventoryWaybillInfo(InventoryBaseRequest inventoryBaseRequest);

    /*
     * 刷新数据，更新待盘和已盘运单信息
     * */
    InventoryWaybillResponse syncCurrInventoryWaybillInfo(InventoryBaseRequest inventoryBaseRequest);

    /*
     * 获取运单盘点明细信息（已盘、未盘）
     * */
    List<InventoryWaybillDetail> getInventoryWaybillDetail(InventoryBaseRequest inventoryBaseRequest);

    /*
     * 判断任务是否完成
     * */
    boolean checkTaskIsComplete(String inventoryTaskId);

    /*
     * 任务完成功能实现
     * */
    void completeInventoryTask(InventoryBaseRequest inventoryBaseRequest);
}
