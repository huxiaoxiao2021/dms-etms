package com.jd.bluedragon.distribution.delivery.processor;

import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.callback.IDeliveryProcessCallback;
import com.jd.bluedragon.distribution.send.domain.SendM;
import org.apache.poi.hssf.record.formula.functions.T;

import java.util.List;

/**
 * @ClassName IDeliveryCoreProcessor
 * @Description
 * @Author wyh
 * @Date 2021/8/3 21:33
 **/
public interface IDeliveryCoreProcessor {

    /**
     * 发货核心操作
     * @param sendMList
     * @param callback
     * @return
     */
    DeliveryResponse process(List<SendM> sendMList, IDeliveryProcessCallback<SendM> callback);
}
