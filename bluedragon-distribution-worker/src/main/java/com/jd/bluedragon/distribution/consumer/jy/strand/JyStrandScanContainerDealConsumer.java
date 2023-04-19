package com.jd.bluedragon.distribution.consumer.jy.strand;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportDealService;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运-滞留扫描容器处理消费
 *
 * @author hujiping
 * @date 2023/3/31 4:48 PM
 */
@Service("jyStrandScanContainerDealConsumer")
public class JyStrandScanContainerDealConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(JyStrandScanContainerDealConsumer.class);
    
    @Autowired
    private JyBizTaskStrandReportDealService jyBizTaskStrandReportDealService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("JyStrandScanContainerDealConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("拣运滞留扫描容器处理消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyBizStrandReportDetailEntity jyBizStrandReportDetail = JsonHelper.fromJson(message.getText(), JyBizStrandReportDetailEntity.class);
            if(jyBizStrandReportDetail == null) {
                logger.warn("拣运滞留扫描容器处理消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            if(StringUtils.isEmpty(jyBizStrandReportDetail.getBizId())
                    || StringUtils.isEmpty(jyBizStrandReportDetail.getContainerCode())){
                logger.warn("拣运滞留扫描容器处理消息体缺少必要参数，内容为【{}】", message.getText());
                return;
            }
            
            // 针对容器内的扫描件进行滞留
            jyBizTaskStrandReportDealService.scanContainerDeal(jyBizStrandReportDetail);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("拣运滞留扫描容器处理消息消费异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
