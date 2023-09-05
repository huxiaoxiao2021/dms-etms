package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.tms.jdi.constans.TransDriverNodeNotifyTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Service("tmsTransWorkDriverNodeConsumer")
public class TmsTransWorkDriverNodeConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(TmsTransWorkDriverNodeConsumer.class);


    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsTransWorkDriverNodeConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsTransWorkDriverNodeConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsTransWorkDriverNodeConsumer.TmsTransWorkDriverNodeMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsTransWorkDriverNodeConsumer.TmsTransWorkDriverNodeMqBody.class);
        if(mqBody == null){
            logger.error("TmsTransWorkDriverNodeConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
//        if(StringUtils.isEmpty(mqBody.getTransWorkItemCode()) || StringUtils.isEmpty(mqBody.getEventTime()) || StringUtils.isEmpty(mqBody.getTransWorkCode())){
//            logger.error("TmsTransWorkDriverNodeConsumer consume -->关键数据为空，内容为【{}】", message.getText());
//            return;
//        }

        //目前仅记录当前始发场地车辆到来时间使用
//        if(saveNearComeTime(mqBody)){
//            if(logger.isInfoEnabled()){
//                logger.info("TmsAreaNearNoticeForBeginNodeConsumer saveNearComeTime success!,{}",JsonHelper.toJson(mqBody));
//            }
//        }else{
//            //理论上没有失败场景，如有存在失败暂时只记录日志，即使重试也没有什么意义
//            logger.error("TmsAreaNearNoticeForBeginNodeConsumer" +
//                    " saveNearComeTime fail!,{}",JsonHelper.toJson(mqBody));
//        }
    }

    private class TmsTransWorkDriverNodeMqBody implements Serializable {
        private static final long serialVersionUID = 6850609984597965111L;
        private String transWorkCode;

        private String transWorkItemCode;

        private String type;

        private Date beginRealArriveCarTime;

        private Integer dockScanType;

        private Date dockTime;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getBeginRealArriveCarTime() {
            return beginRealArriveCarTime;
        }

        public void setBeginRealArriveCarTime(Date beginRealArriveCarTime) {
            this.beginRealArriveCarTime = beginRealArriveCarTime;
        }

        public Integer getDockScanType() {
            return dockScanType;
        }

        public void setDockScanType(Integer dockScanType) {
            this.dockScanType = dockScanType;
        }

        public Date getDockTime() {
            return dockTime;
        }

        public void setDockTime(Date dockTime) {
            this.dockTime = dockTime;
        }
    }
}
