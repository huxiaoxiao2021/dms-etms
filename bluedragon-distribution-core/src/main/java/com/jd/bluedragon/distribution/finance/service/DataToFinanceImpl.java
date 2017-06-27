package com.jd.bluedragon.distribution.finance.service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_SendDatail;
import com.jd.bluedragon.distribution.failqueue.service.DataTranTool;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.finance.wss.WaybillDataServiceWS;
import com.jd.etms.finance.wss.pojo.ResponseMessage;
import com.jd.etms.finance.wss.pojo.SortingOrder;
import com.jd.etms.finance.wss.pojo.SortingCar;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2017/6/12.
 */

@Service("dataToFinanceService")
public class DataToFinanceImpl implements DataToFinance{
    private final static Logger logger = Logger.getLogger(DataToFinanceImpl.class);

    @Autowired
    private DefaultJMQProducer departureToFinanceMQ;

    @Autowired
    private DefaultJMQProducer deliveryToFinanceMQ;

    @Autowired
    WaybillDataServiceWS waybillDataServiceWS;

    /**
     * 第三方发车信息发送到mq 供财务消费
     * @param task
     * @return
     */
    public boolean departure2Finance(Task task){
        DealData_Departure departure = null;
        SortingCar sortingCar = null;
        try {
            departure = JsonHelper.fromJson(task.getBody(), DealData_Departure.class);
            sortingCar = DataTranTool.transDealDataDepartureToFinanceData(departure);
            //发送消息到mq
            departureToFinanceMQ.sendOnFailPersistent(String.valueOf(sortingCar.getSortCarId())
                        ,JsonHelper.toJsonUseGson(sortingCar));
        } catch (Exception e) {
            logger.error("[DataToFinanceImpl.departure2Finance]发送到mq失败", e);
            logger.error("调用外单接口推送");
            List<SortingCar >  sortingCars = new ArrayList<SortingCar>();
            sortingCars.add(sortingCar);
            ResponseMessage responseMessage = waybillDataServiceWS.sendSortingCar(sortingCars);
            if(responseMessage.getIsSuccess()== 1){
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean delivery2Finance(Task task){
        DealData_SendDatail departure  =null;
        com.jd.etms.finance.wss.pojo.SortingOrder sortingOrder = null;
        try {
            departure = JsonHelper.fromJson(task.getBody(), DealData_SendDatail.class);
            sortingOrder = DataTranTool.
                    transDealDataSendDatailToFinanceData(departure);
            //发送消息到mq
            deliveryToFinanceMQ.sendOnFailPersistent(String.valueOf(sortingOrder.getWaybillCode())
                    ,JsonHelper.toJsonUseGson(sortingOrder));

        } catch (Exception e) {
            logger.error("[DataToFinanceImpl.delivery2Finance]发送到mq失败", e);
            logger.error("调用外单接口推送");
            List<SortingOrder>  sortingOrders = new ArrayList<SortingOrder>();
            sortingOrders.add(sortingOrder);
            ResponseMessage responseMessage = waybillDataServiceWS.sendSortingOrder(sortingOrders);
            if(responseMessage.getIsSuccess()== 1){
                return true;
            }
            return false;
        }
        return true;
    }
}
