package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodBoxSplitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsCacheService;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodTransactionManager;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service("jyPickingGoodScanConsumer")
public class JyPickingGoodScanConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodScanConsumer.class);

    @Autowired
    private JyBizTaskPickingGoodTransactionManager jyBizTaskPickingGoodTransactionManager;
    @Autowired
    private JyAviationRailwayPickingGoodsCacheService cacheService;
    @Autowired
    private BoxService boxService;
    @Autowired
    @Qualifier(value = "jyPickingGoodSplitPackageProducer")
    private DefaultJMQProducer jyPickingGoodSplitPackageProducer;
    @Autowired
    private DeliveryService deliveryService;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodScanConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyPickingGoodScanConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodScanConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyPickingGoodScanDto mqBody = JsonHelper.fromJson(message.getText(), JyPickingGoodScanDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodScanConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            log.error("航空提货扫描异步：消息丢弃，msg={}", JsonHelper.toJson(mqBody));
            return;
        }
        if(log.isInfoEnabled()){
            log.info("航空提货扫描异步开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        }
        mqBody.setBusinessId(message.getBusinessId());
        this.deal(mqBody);
        if(log.isInfoEnabled()){
            log.info("航空提货扫描异步消费结束，businessId={}", message.getBusinessId());
        }
    }

    private void deal(JyPickingGoodScanDto mqBody) {
        if(!cacheService.lockPickingGoodBizIdSiteId(mqBody.getBizId(), mqBody.getPickingSiteId())) {
            logWarn("提货岗扫描异步货物任务场地锁失败，操作重试, mqBody={}", JsonHelper.toJson(mqBody));
            throw new JyBizException("提货岗扫描异步货物任务场地锁失败:businessId=" + mqBody.getBusinessId());
        }
        try{
            //首单扫描逻辑
            CallerInfo info0 = Profiler.registerInfo("DMSWORKER.JyPickingGoodScanConsumer.deal0", Constants.UMP_APP_NAME_DMSWEB,false, true);
            jyBizTaskPickingGoodTransactionManager.startPickingGoodTask(mqBody);
            Profiler.registerInfoEnd(info0);
            //扫描记录存储
            this.sendScanSplitBoxDetailMq(mqBody);

            //agg计数统计自增
            CallerInfo info1 = Profiler.registerInfo("DMSWORKER.JyPickingGoodScanConsumer.deal1", Constants.UMP_APP_NAME_DMSWEB,false, true);
            jyBizTaskPickingGoodTransactionManager.updateAggScanStatistics(mqBody);
            Profiler.registerInfoEnd(info1);
        }catch (Exception ex) {
            log.error("提货扫描异步处理消费异常，errMsg={}, mqBody={}", ex.getMessage(), JsonHelper.toJson(mqBody), ex);
            throw new JyBizException(String.format("航空提货扫描异步消费异常,businessId：%s", mqBody.getBusinessId()));
        } finally {
            cacheService.unlockPickingGoodBizIdSiteId(mqBody.getBizId(), mqBody.getPickingSiteId());
        }
    }


    private void sendScanSplitBoxDetailMq(JyPickingGoodScanDto mqBody) {
        if(BusinessUtil.isBoxcode(mqBody.getBarCode())) {
            logInfo("按箱号提货扫描拆分包裹数据，mqBody={}", JsonHelper.toJson(mqBody));
            Box box = this.boxService.findBoxByCode(mqBody.getBarCode());
            if (Objects.isNull(box)) {
                logInfo("按箱号提货扫描拆分包裹数据，箱号查询为空，boxCode={}", mqBody.getBarCode());
                return ;
            }
            //查询分拣集包数据
            List<SendDetail> sendDetailList = deliveryService.getCancelSendByBox(mqBody.getBarCode());
            if (CollectionUtils.isEmpty(sendDetailList)) {
                logInfo("按箱号提货扫描拆分包裹数据，箱内包裹查询为空，boxCode={}, mqBody={}", mqBody.getBarCode(), JsonHelper.toJson(mqBody));
                return ;
            }
            List<Message> messageList = new ArrayList<>();
            sendDetailList.forEach(sendDetail -> {
                PickingGoodBoxSplitDto boxSplitDto = new PickingGoodBoxSplitDto();
                BeanUtils.copyProperties(mqBody, boxSplitDto);
                boxSplitDto.setPackageCode(sendDetail.getPackageBarcode());
                boxSplitDto.setSysTime(System.currentTimeMillis());

                String msgText = JsonHelper.toJson(boxSplitDto);
                logInfo("提货扫描箱号拆分包裹数据，businessId={},msg={}", sendDetail.getPackageBarcode(), msgText);
                messageList.add(new Message(jyPickingGoodSplitPackageProducer.getTopic(), msgText, sendDetail.getPackageBarcode()));
            });
            jyPickingGoodSplitPackageProducer.batchSendOnFailPersistent(messageList);

        }else if(WaybillUtil.isPackageCode(mqBody.getBarCode())) {
            PickingGoodBoxSplitDto boxSplitDto = new PickingGoodBoxSplitDto();
            BeanUtils.copyProperties(mqBody, boxSplitDto);
            boxSplitDto.setPackageCode(mqBody.getBarCode());
            boxSplitDto.setSysTime(System.currentTimeMillis());

            String msgText = JsonHelper.toJson(boxSplitDto);
            jyPickingGoodSplitPackageProducer.sendOnFailPersistent(boxSplitDto.getPackageCode(), msgText);
            logInfo("提货扫描包裹号，businessId={},msg={}", boxSplitDto.getPackageCode(), msgText);
        }

    }




    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(JyPickingGoodScanDto mqBody) {
        return true;
    }

}
