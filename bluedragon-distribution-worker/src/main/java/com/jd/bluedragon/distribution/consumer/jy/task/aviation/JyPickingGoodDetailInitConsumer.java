package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsService;
import com.jd.bluedragon.distribution.jy.service.picking.bridge.PickingGoodDetailInitService;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service("jyPickingGoodDetailInitConsumer")
public class JyPickingGoodDetailInitConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodDetailInitConsumer.class);

    @Autowired
    private JyAviationRailwayPickingGoodsService pickingGoodsService;

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
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodDetailInitConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyPickingGoodDetailInitConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodDetailInitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        PickingGoodTaskDetailInitDto mqBody = JsonHelper.fromJson(message.getText(), PickingGoodTaskDetailInitDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodDetailInitConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("航空提货待提明细初始化开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        }

        try{
            this.deal(mqBody);
            log.info("航空提货待提明细初始化结束，businessId={}，任务BizId={}", message.getBusinessId(), mqBody.getBizId());
        }catch (Exception ex) {
            log.error("航空提货扫描异步消费异常,businessId={},mqBody={}", message.getBusinessId(), message.getText());
            throw new JyBizException(String.format("航空提货扫描异步消费异常,businessId：%s", message.getBusinessId()));
        }

    }

    private void deal(PickingGoodTaskDetailInitDto mqBody) {
        PickingGoodDetailInitService detailInitService;
        if(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getSource().equals(mqBody.getStartSiteType())) {
            detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode());
        }else {
            detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode());
        }

        detailInitService.pickingGoodDetailInitSplit(mqBody);
    }
}
