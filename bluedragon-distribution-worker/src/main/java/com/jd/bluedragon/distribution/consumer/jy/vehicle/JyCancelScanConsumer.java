package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordStatistics;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectService;
import com.jd.bluedragon.distribution.jy.constants.JyCollectScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyCancelScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCancelScanDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectNew.strategy.JyScanCollectStrategy;
import com.jd.bluedragon.distribution.jy.service.send.JyWarehouseSendVehicleServiceImpl;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.Constants.BUSSINESS_TYPE_POSITIVE;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 19:59
 * @Description
 */

@Service("jyCancelScanConsumer")
public class JyCancelScanConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyCancelScanConsumer.class);



    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    @Qualifier("jyCancelScanProducer")
    private DefaultJMQProducer jyCancelScanProducer;
    @Autowired
    private JyScanCollectService jyScanCollectService;
    @Autowired
    private JyScanCollectStrategy jyScanCollectStrategy;
    @Autowired
    @Qualifier("jyCancelScanCollectProducer")
    private DefaultJMQProducer jyCancelScanCollectProducer;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyCancelScanConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        String methodDesc = "JyCancelScanConsumer.consume：拣运取消扫描：";
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyCancelScanConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyCancelScanConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JySendCancelScanDto mqBody = JsonHelper.fromJson(message.getText(), JySendCancelScanDto.class);
        if(mqBody == null){
            log.error("JyCancelScanConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理JyCancelScanConsumer开始，内容{}",message.getText());
        }

        if(StringUtils.isBlank(mqBody.getJyPostType())) {
            return;
        }

        boolean consumeRes;
        try {
            if(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode().equals(mqBody.getJyPostType())) {
                consumeRes = warehouseSendCancelScanDeal(mqBody, message.getBusinessId());
            }else {
                log.warn("{}，当前仅支持接货仓发货岗取消扫描，不支持当前岗位【{}】，msg={}", JyFuncCodeEnum.getDescByCode(mqBody.getJyPostType()), message.getText());
                return;
            }
        } catch (Exception e) {
            log.error("JyCancelScanConsumer.deal服务异常，businessId={}, errMsg={},内容{}", message.getBusinessId(), e.getMessage(), message.getText(), e);
            throw new JyBizException("JyCancelScanConsumer拣运取消扫描异常：" + message.getBusinessId());
        }
        if (!consumeRes) {
            //处理失败 重试
            log.error("{}失败，businessId={}, 内容{}", methodDesc, message.getBusinessId(), message.getText());
            throw new JyBizException("JyCancelScanConsumer拣运取消扫描处理失败" + message.getBusinessId());
        } else {
            if (log.isInfoEnabled()) {
                log.info("{}成功，businessId={}, 内容{}", methodDesc, message.getBusinessId(), message.getText());
            }
        }
    }


    /**
     * 接货仓取消发货扫描处理
     * @param mqBody
     * @param businessId
     * @return
     */
    private boolean warehouseSendCancelScanDeal(JySendCancelScanDto mqBody, String businessId) {

        if(StringUtils.isBlank(mqBody.getBarCode())){
            return true;
        }
        //按件处理
        SendM sendM = generateSendM(mqBody.getBarCode(), mqBody);
        boolean cancelFlag = singleCancelDeliverHandler(sendM, businessId);
        if(cancelFlag) {
            //取消扫描处理集齐数据
            sendCancelScanCollectHandlerMQ(mqBody);
        }
        return cancelFlag;

    }


    public SendM generateSendM(String barCode, JySendCancelScanDto mqBody) {
        SendM sendM = new SendM();
        sendM.setBoxCode(barCode);
        sendM.setCreateSiteCode(mqBody.getOperateSiteId());
        sendM.setUpdaterUser(mqBody.getOperatorName());
        sendM.setUpdateUserCode(mqBody.getUpdateUserCode());
        sendM.setSendType(BUSSINESS_TYPE_POSITIVE);
        sendM.setOperateTime(new Date(mqBody.getOperateTime()));
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 按件处理取消发货
     * @param sendM
     * @param businessId
     * @return
     */
    public boolean singleCancelDeliverHandler(SendM sendM, String businessId){
        String methodDesc = String.format("singleCancelDeliverHandler处理取消发货:businessId=%s,code=%s", businessId, sendM.getBoxCode());
        //        //执行取消发货动作
            ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessage(sendM, true);
            if(Objects.isNull(tDResponse)) {
                log.error("{}取消发货失败，返回null，req={}", methodDesc, JsonHelper.toJson(sendM));
                return false;
            }
            if (!JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())) {
                if(DeliveryResponse.CODE_DELIVERY_BY_WAYBILL_PROCESSING.equals(tDResponse.getCode())) {
                    log.warn("{}正在取消发货中，req={}，res={}", methodDesc, JsonHelper.toJson(sendM), JsonHelper.toJson(tDResponse));
                    return true;
                }
                if(DeliveryResponse.CODE_Delivery_NO_MESAGE.equals(tDResponse.getCode())) {
                    log.warn("{}未查到包裹的发货数据，不做处理，req={}，res={}", methodDesc, JsonHelper.toJson(sendM), JsonHelper.toJson(tDResponse));
                    return true;
                }
                //todo 此处失败不做重试，后续改造同步，把异常给PDA,理论上取消就需要成功
                log.error("{}取消发货失败,该失败未做重试，req={}，res={}", methodDesc, JsonHelper.toJson(sendM), JsonHelper.toJson(tDResponse));
            }
            return true;
    }


    /**
     * 取消扫描运单
     * (当前消息的producer)
     */
    private void sendCancelScanWaybillMq(JySendCancelScanDto jySendCancelScanDto, List<JyCollectRecordStatistics> jyCollectRecordPoList) {
        String methodDesc = "JyCancelScanConsumer:sendCancelScanMq:接货仓发货岗取消发货MQ发送：";
        JySendCancelScanDto mqDto = new JySendCancelScanDto();
        BeanUtils.copyProperties(jySendCancelScanDto, mqDto);
        mqDto.setBizSource(JyWarehouseSendVehicleServiceImpl.OPERATE_SOURCE_MQ);
        mqDto.setBuQiAllSelectFlag(false);//todo 此处该字段一定不能true 否则会消息消费-生产 死循环
        List<Message> messageList = new ArrayList<>();
        for(JyCollectRecordStatistics statistics : jyCollectRecordPoList) {
            mqDto.setBarCode(statistics.getAggCode());
            mqDto.setBarCodeType(JyCollectScanCodeTypeEnum.WAYBILL.getCode());
            String businessId = String.format("%s:%s:%s:%s", statistics.getAggCode(), mqDto.getMainTaskBizId(), mqDto.getJyPostType(), mqDto.getBizSource());
            String msg = JsonHelper.toJson(mqDto);
            if(log.isInfoEnabled()) {
                log.info("{}按运单取消：businessId={},msx={}", methodDesc, businessId, msg);
            }
            messageList.add(new Message(jyCancelScanProducer.getTopic(), msg, businessId));
        }
        jyCancelScanProducer.batchSendOnFailPersistent(messageList);
    }


    private void sendCancelScanCollectHandlerMQ(JySendCancelScanDto mqBody) {
        JyCancelScanCollectMqDto mqDto = new JyCancelScanCollectMqDto();
        mqDto.setOperatorErp(mqBody.getOperatorErp());
        mqDto.setOperatorName(mqBody.getOperatorName());
        mqDto.setOperateSiteId(mqBody.getOperateSiteId());
        mqDto.setOperateSiteName(mqBody.getOperateSiteName());
        mqDto.setOperateTime(mqBody.getOperateTime());
        //
        mqDto.setMainTaskBizId(mqBody.getMainTaskBizId());
        mqDto.setJyPostType(mqBody.getJyPostType());
        mqDto.setBarCode(mqBody.getBarCode());
        mqDto.setBarCodeType(mqBody.getBarCodeType());
        //运单号+操作任务+岗位类型+触发节点
        String businessId = jyScanCollectStrategy.getCancelScanBusinessId(mqDto);
        String msg = JsonHelper.toJson(mqDto);
        if(log.isInfoEnabled()) {
            log.info("JyWarehouseSendVehicleServiceImpl.sendCancelScanCollectHandlerMQ:接货仓发货岗取消扫描集齐处理：businessId={}，msg={}", businessId, msg);
        }
        jyCancelScanCollectProducer.sendOnFailPersistent(businessId, msg);

    }

}

