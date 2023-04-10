package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.SendVehicleDetailTaskDto;
import com.jd.bluedragon.distribution.jy.dto.send.TmsItemSimpleCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransWorkItemDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.SendTaskExcepLabelEnum;
import com.jd.bluedragon.distribution.jy.enums.TmsLineTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkDto;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service("tmsItemSimpleCodeConsumer")
public class TmsItemSimpleCodeConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TmsItemSimpleCodeConsumer.class);

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsItemSimpleCodeConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsItemSimpleCodeConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        logInfo("消费运输派车明细-自动推送任务简码消息. {}", message.getText());

        TmsItemSimpleCodeDto simpleCodeDto = JsonHelper.fromJson(message.getText(), TmsItemSimpleCodeDto.class);
        JyBizTaskSendVehicleDetailEntity condition = new JyBizTaskSendVehicleDetailEntity();
        condition.setTransWorkItemCode(simpleCodeDto.getTransWorkItemCode());
        JyBizTaskSendVehicleDetailEntity entity =taskSendVehicleDetailService.findByTransWorkItemCode(condition);

        if (!ObjectHelper.isNotNull(entity)){
          logger.error("{} 任务还未创建，不执行更新",simpleCodeDto.getSimpleCode());
          throw new JyBizException("{} 任务还未创建");
        }
        if (ObjectHelper.isNotNull(entity.getTaskSimpleCode()) && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getVehicleStatus())){
          logInfo("{} 已经封车，不再接收任务简码变更",simpleCodeDto.getSimpleCode());
          return;
        }

        JyBizTaskSendVehicleDetailEntity detailEntity =new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setBizId(entity.getBizId());
        detailEntity.setTaskSimpleCode(simpleCodeDto.getSimpleCode());
        taskSendVehicleDetailService.updateByBiz(detailEntity);
    }
    private void logInfo(String message, Object ...objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
}
