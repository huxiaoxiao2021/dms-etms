package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 运输车辆GIS定位到达消息  始发场地
 *
 * 始发网点到达：tms_trans_work_car_come
 * {
 *     "transWorkCode":"TW22120551209711",
 *     "transWorkItemCode":"TW22120551209711-001",
 *     "comeTime":"2022-12-05 09:51:09",
 *     "sendTime":null,
 *     "arriveTime":null,
 *     "businessType":null,
 *     "transType":null,
 *     "transWay":null,
 *     "vehicleNumber":"粤SK9712",
 *     "operateType":1,
 *     "transNeedList":null,
 *     "vehicleUseType":null,
 *     "transJobCode":null,
 *     "transJobItemCode":null
 * }
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/12/11
 * @Description:
 */
@Service("tmsTransWorkCarComeConsumer")
public class TmsTransWorkCarComeConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsTransWorkCarComeConsumer.class);

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;


    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsTransWorkCarComeConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsTransWorkCarComeConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsTransWorkCarComeMQBody mqBody = JsonHelper.fromJson(message.getText(), TmsTransWorkCarComeMQBody.class);
        if(mqBody == null){
            logger.error("TmsTransWorkCarComeConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isEmpty(mqBody.getTransWorkItemCode()) || StringUtils.isEmpty(mqBody.getComeTime()) || StringUtils.isEmpty(mqBody.getTransWorkCode())){
            logger.error("TmsTransWorkCarComeConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }

        //目前仅记录当前始发场地车辆到来时间使用
        if(saveComeTime(mqBody)){
            if(logger.isInfoEnabled()){
                logger.info("TmsTransWorkCarComeConsumer saveComeTime success!,{}",JsonHelper.toJson(mqBody));
            }
        }else{
            //理论上没有失败场景，如有存在失败暂时只记录日志，即使重试也没有什么意义
            logger.error("TmsTransWorkCarComeConsumer saveComeTime fail!,{}",JsonHelper.toJson(mqBody));
        }

    }

    /**
     * 记录车辆始发场地到来时间
     * @param mqBody
     * @return
     */
    private boolean saveComeTime(TmsTransWorkCarComeMQBody mqBody){
        JyBizTaskSendVehicleEntity saveComeOrNearComeTimeParam = new JyBizTaskSendVehicleEntity();
        saveComeOrNearComeTimeParam.setBizId(mqBody.getTransWorkItemCode());
        saveComeOrNearComeTimeParam.setComeTime(DateHelper.parseAllFormatDateTime(mqBody.getComeTime()));
        return sendVehicleTransactionManager.saveComeOrNearComeTime(saveComeOrNearComeTimeParam);
    }

    private class TmsTransWorkCarComeMQBody implements Serializable {
        static final long serialVersionUID = 1L;

        private String transWorkCode;

        private String transWorkItemCode;

        private String comeTime;

        private Integer operateType;

        public String getTransWorkCode() {
            return transWorkCode;
        }

        public void setTransWorkCode(String transWorkCode) {
            this.transWorkCode = transWorkCode;
        }

        public String getTransWorkItemCode() {
            return transWorkItemCode;
        }

        public void setTransWorkItemCode(String transWorkItemCode) {
            this.transWorkItemCode = transWorkItemCode;
        }

        public String getComeTime() {
            return comeTime;
        }

        public void setComeTime(String comeTime) {
            this.comeTime = comeTime;
        }

        public Integer getOperateType() {
            return operateType;
        }

        public void setOperateType(Integer operateType) {
            this.operateType = operateType;
        }
    }
}
