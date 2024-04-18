package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeSealCarDto;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeShuttleSealDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingSendDestinationService;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Date 2023/8/7 10:52
 * @Description 拣运封车批次
 */
@Service("jySealCarBatchCodesConsume")
public class JySealCarBatchCodesConsume extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JySealCarBatchCodesConsume.class);
    @Autowired
    private JyAviationRailwaySendSealService jyAviationRailwaySendSealService;
    @Autowired
    private JyPickingSendDestinationService jyPickingSendDestinationService;
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JySealCarBatchCodesConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JySealCarBatchCodesConsume consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JySealCarBatchCodesConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BatchCodeSealCarDto mqBody = JsonHelper.fromJson(message.getText(), BatchCodeSealCarDto.class);
        if(mqBody == null){
            log.error("JySealCarBatchCodesConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(CollectionUtils.isEmpty(mqBody.getSuccessSealBatchCodeList())) {
            log.error("JySealCarBatchCodesConsume consume -->JSON转换后为空，批次号内容为【{}】", message.getText());
            return;
        }

        if(log.isInfoEnabled()){
            log.info("消费处理JySealCarBatchCodesConsume开始，内容{}",message.getText());
        }
        try{
            if(JyFuncCodeEnum.AVIATION_RAILWAY_PICKING_GOOD_POSITION.getCode().equals(mqBody.getPost())) {
                this.aviationRailwayPickingSendSealService(mqBody);
            }else if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(mqBody.getPost())) {
                this.aviationRailwaySendSealService(mqBody);
            }
        }catch (Exception e) {
            Long time = System.currentTimeMillis();
            log.error("JySealCarBatchCodesConsume consume -->消费处理异常，businessId={},异常时间={}，内容为【{}】", message.getBusinessId(), time, message.getText(),e);
            throw new JyBizException(String.format("拣运封车批次消费异常：%s,异常时间:%s",message.getBusinessId(), time));
        }
    }

    //空铁发货岗支线已封（原摆渡已封）相关逻辑
    private void aviationRailwaySendSealService(BatchCodeSealCarDto mqBody) {
        BatchCodeShuttleSealDto dto = new BatchCodeShuttleSealDto();
        dto.setTransportCode(mqBody.getTransportCode());
        dto.setSuccessSealBatchCodeList(mqBody.getSuccessSealBatchCodeList());
        dto.setOperateTime(mqBody.getOperateTime());
        dto.setOperatorErp(mqBody.getOperatorErp());
        dto.setOperateSiteId(mqBody.getOperateSiteId());
        jyAviationRailwaySendSealService.batchCodeShuttleSealMark(dto);
    }

    private void aviationRailwayPickingSendSealService(BatchCodeSealCarDto mqBody) {
        jyPickingSendDestinationService.batchUpdateBatchCodeSealCarStatus(mqBody);
    }
}
