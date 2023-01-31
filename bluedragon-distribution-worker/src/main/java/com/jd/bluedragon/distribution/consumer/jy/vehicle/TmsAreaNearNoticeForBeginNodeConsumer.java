package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 始发网点即将到达
 jmq4
 topic：tms_jdi_area_near_notice_for_begin_node

 {
 "transWorkCode":"TW22120551202165",
 "transWorkItemCode":"TW22120551202165-001",
 "beginNodeCode":"668F001",
 "beginNodeName":"茂名分拣中心",
 "eventTime":"2022-12-05 10:01:29",
 "vehicleNumber":"粤K26015"
 }
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/12/11
 * @Description:
 */
@Service("tmsAreaNearNoticeForBeginNodeConsumer")
public class TmsAreaNearNoticeForBeginNodeConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsAreaNearNoticeForBeginNodeConsumer.class);

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsAreaNearNoticeForBeginNodeConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsAreaNearNoticeForBeginNodeConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsAreaNearNoticeForBeginNodeConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsAreaNearNoticeForBeginNodeMQBody mqBody = JsonHelper.fromJson(message.getText(), TmsAreaNearNoticeForBeginNodeMQBody.class);
        if(mqBody == null){
            logger.error("TmsAreaNearNoticeForBeginNodeConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isEmpty(mqBody.getTransWorkItemCode()) || StringUtils.isEmpty(mqBody.getEventTime()) || StringUtils.isEmpty(mqBody.getTransWorkCode())){
            logger.error("TmsAreaNearNoticeForBeginNodeConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }

        //目前仅记录当前始发场地车辆到来时间使用
        if(saveNearComeTime(mqBody)){
            if(logger.isInfoEnabled()){
                logger.info("TmsAreaNearNoticeForBeginNodeConsumer saveNearComeTime success!,{}",JsonHelper.toJson(mqBody));
            }
        }else{
            //理论上没有失败场景，如有存在失败暂时只记录日志，即使重试也没有什么意义
            logger.error("TmsAreaNearNoticeForBeginNodeConsumer" +
                    " saveNearComeTime fail!,{}",JsonHelper.toJson(mqBody));
        }

    }

    /**
     * 记录车辆始发场地即将到来时间
     * @param mqBody
     * @return
     */
    private boolean saveNearComeTime(TmsAreaNearNoticeForBeginNodeMQBody mqBody){
        JyBizTaskSendVehicleEntity saveComeOrNearComeTimeParam = new JyBizTaskSendVehicleEntity();
        saveComeOrNearComeTimeParam.setBizId(mqBody.getTransWorkItemCode());
        saveComeOrNearComeTimeParam.setNearComeTime(DateHelper.parseAllFormatDateTime(mqBody.getEventTime()));
        return sendVehicleTransactionManager.saveComeOrNearComeTime(saveComeOrNearComeTimeParam);
    }

    private class TmsAreaNearNoticeForBeginNodeMQBody implements Serializable {
        static final long serialVersionUID = 1L;

        private String transWorkCode;

        private String transWorkItemCode;

        private String beginNodeCode;

        private String beginNodeName;

        private String eventTime;

        private String vehicleNumber;

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

        public String getBeginNodeCode() {
            return beginNodeCode;
        }

        public void setBeginNodeCode(String beginNodeCode) {
            this.beginNodeCode = beginNodeCode;
        }

        public String getBeginNodeName() {
            return beginNodeName;
        }

        public void setBeginNodeName(String beginNodeName) {
            this.beginNodeName = beginNodeName;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }
    }
}
