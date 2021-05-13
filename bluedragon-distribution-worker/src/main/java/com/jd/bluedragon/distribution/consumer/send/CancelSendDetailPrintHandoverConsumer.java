package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.PrintHandoverListManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 消费取消发货消息更新发货交接清单数据
 *
 * @author hujiping
 * @date 2021/4/25 5:23 下午
 */
@Service("cancelSendDetailPrintHandoverConsumer")
public class CancelSendDetailPrintHandoverConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CancelSendDetailPrintHandoverConsumer.class);

    @Autowired
    private PrintHandoverListManager printHandoverListManager;

    @JProfiler(jKey = "CancelSendDetailPrintHandoverConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER,mState = {JProEnum.TP,JProEnum.Heartbeat})
    @Override
    public void consume(Message message) throws Exception {

        SendDetailMessage sendDetailMessage = null;
        try {
            sendDetailMessage = JsonHelper.jsonToObject(message.getText(), SendDetailMessage.class);
        }catch (Exception e){
            logger.error("消费发货取消消息反序列化异常!", e);
        }
        if(sendDetailMessage == null || StringUtils.isEmpty(sendDetailMessage.getSendCode())
                || StringUtils.isEmpty(sendDetailMessage.getPackageBarcode())){
            logger.warn("取消发货缺少必要字段!");
            return;
        }

        // 设置取消发货字段
        Boolean aBoolean = printHandoverListManager.updatePrintHandoverList(convertToPrintHandoverListDto(sendDetailMessage));
        if(logger.isInfoEnabled() && Objects.equals(aBoolean,true)){
            logger.info("消费取消发货消息更新打印交接清单成功!");
        }
    }

    private PrintHandoverListDto convertToPrintHandoverListDto(SendDetailMessage sendDetailMessage) {
        PrintHandoverListDto dto = new PrintHandoverListDto();
        dto.setPackageCode(sendDetailMessage.getPackageBarcode());
        dto.setSendCode(sendDetailMessage.getSendCode());
        Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(sendDetailMessage.getSendCode());
        if (siteCodes[0] == -1 || siteCodes[1] == -1) {
            return null;
        }
        dto.setCreateSiteCode(siteCodes[0]);
        dto.setReceiveSiteCode(siteCodes[1]);
        dto.setIsCancel(Byte.valueOf(String.valueOf(Constants.CONSTANT_NUMBER_ONE)));
        return dto;
    }
}
