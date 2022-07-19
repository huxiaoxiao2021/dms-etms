package com.jd.bluedragon.distribution.delivery.processor;

import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;

/**
 * @ClassName IDeliveryBaseHandler
 * @Description
 * @Author wyh
 * @Date 2021/8/9 11:41
 **/
public interface IDeliveryBaseHandler {

    /**
     * 生成该次发货操作的唯一标识
     * @param wrapper
     * @return
     */
    String genBatchTaskUniqKey(SendMWrapper wrapper);

    /**
     * 生成发货异步任务
     * @param wrapper
     * @return
     */
    DeliveryResponse initDeliveryTask(SendMWrapper wrapper);

    /**
     * 生成迁移异步任务
     * @param wrapper
     * @return
     */
    DeliveryResponse initTransferTask(SendMWrapper wrapper);

    /**
     * 锁定批次任务
     * @param batchUniqKey
     * @param pageTotal
     * @return
     */
    boolean lockPageDelivery(String batchUniqKey, int pageTotal);

    /**
     * 处理发货逻辑
     * @param wrapper
     * @return
     */
    boolean dealCoreDelivery(SendMWrapper wrapper);

    /**
     * 版本升级 -新老版本部署隔离
     * @param wrapper
     * @return
     */
    boolean dealCoreDeliveryV2(SendMWrapper wrapper);

    /**
     * 执行迁移任务
     * @param wrapper
     * @return
     */
    boolean dealSendTransfer(SendMWrapper wrapper);

    boolean competeTaskIncrCount(String batchUniqKey);
}
