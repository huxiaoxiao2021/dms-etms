package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsService;
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
@Service("jyPickingGoodScanConsumer")
public class JyPickingGoodScanConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodScanConsumer.class);

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

        try{
            this.deal(mqBody);
            log.info("航空提货扫描异步结束，businessId={}，任务BizId={}", message.getBusinessId(), mqBody.getBizId());
        }catch (Exception ex) {
            log.error("航空提货扫描异步消费异常,businessId={},mqBody={}", message.getBusinessId(), message.getText());
            throw new JyBizException(String.format("航空提货扫描异步消费异常,businessId：%s", message.getBusinessId()));
        }

    }

    private void deal(JyPickingGoodScanDto mqBody) {

        if(pickingGoodsService.isFirstScanInTask(mqBody.getBizId(), mqBody.getSiteId())) {
            logInfo("提货任务{}首次扫描.{}", JsonHelper.toJson(mqBody));
            pickingGoodsService.startPickingGoodTask(mqBody.getSiteId(), mqBody.getBizId(), mqBody.getOperatorTime(), mqBody.getUser());
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
