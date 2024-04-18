package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseWithoutUATConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.print.domain.WaybillExchangePrintPackageDto;
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

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 换单打印时的包裹纬度打印消息消费
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-18 16:26:13 周四
 */
@Service("waybillExchangePrintPackageConsumer")
public class WaybillExchangePrintPackageConsumer extends MessageBaseWithoutUATConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WaybillExchangePrintPackageConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("WaybillExchangePrintPackageConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("WaybillExchangePrintPackageConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("WaybillExchangePrintPackageConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        WaybillExchangePrintPackageDto waybillExchangePrintPackageDto = JsonHelper.fromJson(message.getText(), WaybillExchangePrintPackageDto.class);
        if (waybillExchangePrintPackageDto == null) {
            logger.warn("WaybillExchangePrintPackageConsumer consume --> JSON解析为空，内容为【{}】", message.getText());
            return;
        }
        String lockKey = String.format(CacheKeyConstants.CACHE_KEY_CHANGE_ORDER_PRINT_KEY, waybillExchangePrintPackageDto.getPackageCodeOld());
        try{
            Boolean result = redisClientOfJy.set(lockKey, "1", 20, TimeUnit.SECONDS, false);
            if(!result){
                return ;
            }
            JyExceptionPrintDto dto = getJyExceptionPrintDto(waybillExchangePrintPackageDto);
            jyExceptionService.printSuccess(dto);
            jyDamageExceptionService.dealDamageExpTaskStatus(waybillExchangePrintPackageDto.getPackageCodeNew(), waybillExchangePrintPackageDto.getSiteId());
        }finally {
            redisClientOfJy.del(lockKey);
        }

    }

    /**
     * 获取三无单换单打印实体
     *
     * @return
     */
    private JyExceptionPrintDto getJyExceptionPrintDto(WaybillExchangePrintPackageDto mq) {
        JyExceptionPrintDto dto = new JyExceptionPrintDto();
        dto.setOperateType(mq.getOperateType());
        dto.setSiteCode(mq.getSiteId());
        dto.setSiteName(mq.getSiteName());
        dto.setWaybillCode(mq.getWaybillCodeOld());
        dto.setPackageCode(mq.getPackageCodeOld());
        dto.setOperateTime(new Date(mq.getOperateUnixTime()));
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(mq.getOperateUserId());
        if(baseStaff != null){
            dto.setUserErp(baseStaff.getErp());
        }
        return dto;
    }
}
