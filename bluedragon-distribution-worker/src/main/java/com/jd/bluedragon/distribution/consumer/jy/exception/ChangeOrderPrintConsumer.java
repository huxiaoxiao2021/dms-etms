package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.popPrint.dto.PushPrintRecordDto;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/20 14:14
 * @Description:
 */
@Service("changeOrderPrintConsumer")
public class ChangeOrderPrintConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ChangeOrderPrintConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("ChangeOrderPrintConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("ChangeOrderPrintConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("ChangeOrderPrintConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        ChangeOrderPrintMq changeOrderPrintMq = JsonHelper.fromJson(message.getText(), ChangeOrderPrintMq.class);
        String lockKey =String.format(CacheKeyConstants.CACHE_KEY_CHANGE_ORDER_PRINT_KEY,changeOrderPrintMq.getWaybillCode());
        try{
            Boolean result = redisClientOfJy.set(lockKey, "1", 20, TimeUnit.SECONDS, false);
            if(!result){
                return ;
            }
            JyExceptionPrintDto dto = getJyExceptionPrintDto(changeOrderPrintMq);
            jyExceptionService.printSuccess(dto);
        }finally {
            redisClientOfJy.del(lockKey);
        }

    }

    /**
     * 获取三无单换单打印实体
     *
     * @return
     */
    private JyExceptionPrintDto getJyExceptionPrintDto(ChangeOrderPrintMq mq) {
        JyExceptionPrintDto dto = new JyExceptionPrintDto();
        dto.setOperateType(mq.getOperateType());
        dto.setSiteCode(mq.getSiteCode());
        dto.setWaybillCode(mq.getWaybillCode());
        dto.setOperateTime(mq.getOperateTime());
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(mq.getUserCode());
        if(baseStaff != null){
            dto.setUserErp(baseStaff.getErp());
        }
        return dto;
    }
}
