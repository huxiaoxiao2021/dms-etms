package com.jd.bluedragon.distribution.finance.service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_SendDatail;
import com.jd.bluedragon.distribution.failqueue.service.DataTranTool;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.finance.wss.WaybillDataServiceWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xumei3 on 2017/6/12.
 */

@Service("dataToFinanceService")
public class DataToFinanceImpl implements DataToFinance{
    private final static Logger log = LoggerFactory.getLogger(DataToFinanceImpl.class);

    @Autowired
    private DefaultJMQProducer deliveryToFinanceMQ;

    @Autowired
    WaybillDataServiceWS waybillDataServiceWS;

    public boolean delivery2Finance(Task task){
        try {
            DealData_SendDatail dealData_sendDatail = JsonHelper.fromJson(task.getBody(), DealData_SendDatail.class);
            com.jd.etms.finance.wss.pojo.SortingOrder sortingOrder = DataTranTool.
                    transDealDataSendDatailToFinanceData(dealData_sendDatail);
            //发送消息到mq
            deliveryToFinanceMQ.sendOnFailPersistent(String.valueOf(sortingOrder.getWaybillCode())
                    ,JsonHelper.toJsonUseGson(sortingOrder));

        } catch (Exception e) {
            log.error("[DataToFinanceImpl.delivery2Finance]发送到mq失败,失败单号为：{}", task.getKeyword2());
            return false;
        }
        return true;
    }
}
