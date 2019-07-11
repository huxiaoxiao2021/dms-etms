package com.jd.bluedragon.distribution.worker.sorting;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.middleend.sorting.service.MiddleEndSortingService;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SortingSuccessTask extends DBSingleScheduler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private MiddleEndSortingService middleEndSortingService;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return executeSortingSuccess(task);
    }

    /**
     * 分拣核心操作成功后的补充操作
     *
     * @param task
     * @return
     */
    public boolean executeSortingSuccess(Task task) {
        try {
            SortingObjectExtend sorting = JsonHelper.jsonToObject(task.getBody(), SortingObjectExtend.class);

            if (sorting.getPackagePageIndex() == 0) {
                doSortingAfter(sorting);

            } else {
                //分批后的任务需要调用运单接口获取包裹数据
                BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(sorting.getDmsSorting().getWaybillCode(), sorting.getPackagePageIndex(), sorting.getPackagePageSize());

                List<DeliveryPackageD> packageDList = baseEntity.getData();

                for (DeliveryPackageD packageD : packageDList) {
                    sorting.getDmsSorting().setPackageCode(packageD.getPackageBarcode());
                    doSortingAfter(sorting);
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("执行executeSortingSuccess任务异常.参数：" + JSON.toJSONString(task), e);
            return false;
        }
    }

    private void doSortingAfter(SortingObjectExtend sorting) {
        middleEndSortingService.fillSortingIfPickup(sorting);
        middleEndSortingService.sortingAddInspection(sorting);
        middleEndSortingService.sortingAddSend(sorting);
        middleEndSortingService.sortingAddOperationLog(sorting);
    }
}
