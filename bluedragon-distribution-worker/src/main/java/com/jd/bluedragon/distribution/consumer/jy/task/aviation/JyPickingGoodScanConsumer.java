package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;
import com.jd.bluedragon.distribution.jy.service.picking.*;
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
    private JyBizTaskPickingGoodTransactionManager jyBizTaskPickingGoodTransactionManager;
    @Autowired
    private JyAviationRailwayPickingGoodsCacheService cacheService;
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
        if(!cacheService.lockPickingGoodBizIdSiteId(mqBody.getBizId(), mqBody.getSiteId())) {
            logWarn("提货岗扫描异步货物任务场地锁失败，操作重试, mqBody={}", JsonHelper.toJson(mqBody));
            throw new JyBizException("提货岗扫描异步货物任务场地锁失败:businessId=" + mqBody.getBusinessId());
        }
        try{
            //首单扫描逻辑【方法幂等】
            jyBizTaskPickingGoodTransactionManager.startPickingGoodTask(mqBody);

            //按箱扫描拆包存储明细  todo zcf

            //agg计数统计自增【非幂等】
            jyBizTaskPickingGoodTransactionManager.updateAggScanStatistics(mqBody);
        }catch (Exception ex) {
            cacheService.unlockPickingGoodBizIdSiteId(mqBody.getBizId(), mqBody.getSiteId());
            throw new JyBizException(String.format("航空提货扫描异步消费异常,businessId：%s", mqBody.getBusinessId()));
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
