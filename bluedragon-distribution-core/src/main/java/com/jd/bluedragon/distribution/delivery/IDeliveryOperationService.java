package com.jd.bluedragon.distribution.delivery;

import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;

/**
 * @ClassName IDeliveryOperationService
 * @Description
 * @Author wyh
 * @Date 2021/8/4 16:48
 **/
public interface IDeliveryOperationService {

    /**
     * 按包裹、箱号、运单处理发货数据
     * @param requests
     * @param sourceEnum
     * @return
     */
    DeliveryResponse asyncHandleDelivery(List<SendM> requests, SendBizSourceEnum sourceEnum);

    /**
     * 执行发货任务
     * @param task
     */
    void dealDeliveryTask(Task task);

    /**
     * 版本升级-新老隔离
     * @param task
     */
    void dealDeliveryTaskV2(Task task);

    /**
     * 异步任务开关
     * @param createSiteCode
     * @return
     */
    boolean deliverySendAsyncSwitch(Integer createSiteCode);
}
