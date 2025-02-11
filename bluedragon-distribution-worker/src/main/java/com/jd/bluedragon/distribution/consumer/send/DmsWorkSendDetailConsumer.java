package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.PrintHandoverListManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 消费发货消息，组装【发货交接清单打印】功能的实体
 * @author hujiping
 */
@Service("dmsWorkSendDetailConsumer")
public class DmsWorkSendDetailConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsWorkSendDetailConsumer.class);

    @Autowired
    private SendPrintService sendPrintService;

    @Autowired
    private PrintHandoverListManager printHandoverListManager;

    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("DmsWorkSendDetailConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("发货明细消息MQDmsWorkSendDetail-消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            SendDetail sendDetailMQ = JsonHelper.fromJson(message.getText(), SendDetail.class);
            if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
                log.warn("DmsWorkSendDetailConsumer:消息体[{}]转换实体失败或没有合法的包裹号",message.getText());
                return;
            }
            // 组装basicQueryEntity对象，写入es
            PrintHandoverListDto dto = sendPrintService.buildPrintHandoverListDto(sendDetailMQ);
            if(dto == null){
                log.warn("构建打印交接清单数据为空! {}",message.getText());
                return;
            }
            recordPrintHandoverListData(dto);
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("消费发货消息转换成BasicQueryEntity失败:"+message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private void recordPrintHandoverListData(PrintHandoverListDto dto) {
        Boolean isSuccess = printHandoverListManager.recordForPrintHandoverList(dto);
        if(Objects.equals(isSuccess, true)){
            if(log.isInfoEnabled()){
                log.info("消费发货消息前置打印交接清单数据落es成功!{}",dto.getWaybillCode());
            }
            return;
        }
        log.warn("消费发货消息前置打印交接清单数据落es异常,es实体：{}",JsonHelper.toJson(dto));
    }

}
