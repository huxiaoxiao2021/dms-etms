package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.enums.JyBizDriverTagEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.tms.jdi.constans.TransDriverNodeNotifyTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Service("tmsTransWorkDriverNodeConsumer")
public class TmsTransWorkDriverNodeConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(TmsTransWorkDriverNodeConsumer.class);

    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;

    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;

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
        if(StringUtils.isEmpty(mqBody.getTransWorkItemCode()) || StringUtils.isEmpty(mqBody.getType())){
            logger.error("TmsTransWorkDriverNodeConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }

        JyBizTaskSendVehicleDetailEntity detailRequest = new JyBizTaskSendVehicleDetailEntity();
        detailRequest.setTransWorkItemCode(mqBody.getTransWorkItemCode());
        // 任务明细
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByTransWorkItemCode(detailRequest);
        if (detailEntity == null) {
            logger.warn("找不到派车单明细对应的发货任务明细, 派车单号: {}",mqBody.getTransWorkItemCode());
            return;
        }
        // 任务主记录
        JyBizTaskSendVehicleEntity entity = jyBizTaskSendVehicleService.findByBizId(detailEntity.getSendVehicleBizId());
        if (entity == null) {
            logger.warn("找不到bizId对应的发货任务, BizId: {}", detailEntity.getSendVehicleBizId());
            return;
        }
        JyBizDriverTagEnum driverTagEnum = JyBizDriverTagEnum.getTagEnumByNodeCode(mqBody.getType());

        if (driverTagEnum == null) {
            logger.warn("根据type没找到司机动作枚举{}", mqBody.getType());
            return;
        }
        // 只能按照顺序更新 但每个节点并不会一定有 比如不一定会超时未进
        // 超时未进->扫码靠台->超时未离
        if (driverTagEnum.getTag() > entity.getDriverTag()) {
            // 超时未离只有已封车的才会有 不是已封车状态则丢弃
            if (driverTagEnum.getDriverNodeCode().equals(JyBizDriverTagEnum.LEAVE_TIMEOUT.getDriverNodeCode())
                    && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getVehicleStatus())) {
                logger.warn("超时未离节点, 发货状态不为已封车 bizId{}", entity.getBizId());
                return;
            }
            entity.setDriverTag(driverTagEnum.getTag());
            jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
            return;
        }

        logger.warn("节点逆序，消息被丢弃 {}", mqBody.getType());
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
