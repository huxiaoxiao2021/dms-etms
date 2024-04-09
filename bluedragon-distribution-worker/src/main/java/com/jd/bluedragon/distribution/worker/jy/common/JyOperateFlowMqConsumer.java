
package com.jd.bluedragon.distribution.worker.jy.common;

import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 分拣操作流水mq消费
 *
 * @author wuyoude
 * @date 2023/8/03 5:45 PM
 */
@Service("jyOperateFlowMqConsumer")
public class JyOperateFlowMqConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(JyOperateFlowMqConsumer.class);
    
    @Autowired
	@Qualifier("jyOperateFlowService")
    private JyOperateFlowService jyOperateFlowService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("jyOperateFlowMqConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("JyOperateFlowMq消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyOperateFlowMqData jyOperateFlowMqData = JsonHelper.fromJson(message.getText(), JyOperateFlowMqData.class);
            if (logger.isInfoEnabled()) {
                logger.info("JyOperateFlowMqConsumer:消息text={},jyOperateFlowMqData={}", message.getText(), JsonHelper.toJsonMs(jyOperateFlowMqData));
            }
            if (jyOperateFlowMqData == null) {
                logger.warn("JyOperateFlowMq消息内容无效，内容为【{}】", message.getText());
                return;
            }
            JyOperateFlowDto jyOperateFlowDto = BeanConverter.convertToJyOperateFlowDto(jyOperateFlowMqData);
            if (logger.isInfoEnabled()) {
                logger.info("JyOperateFlowMqConsumer:实体jyOperateFlowDto={}", JsonHelper.toJsonMs(jyOperateFlowDto));
            }
            jyOperateFlowService.insert(jyOperateFlowDto);
        } catch (Exception e) {
            // 由于mq底层机制导致消息可能会重复消费，一直报主键冲突异常。如果用幂等方式解决可能会影响性能。此处采用只记录日志不重试的方式
            if (isConstraintViolationException(e, message.getText())) {
                logger.warn("JyOperateFlowMq-重复消费,消息体:{}", message.getText());
                return;
            }
            Profiler.functionError(info);
            logger.error("JyOperateFlowMq-消费异常-e, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 判断异常是否是由于MySQLIntegrityConstraintViolationException引起
     */
    private boolean isConstraintViolationException(Exception e, String messageText) {
        try {
            // 为了避免堆栈太深造成死循环，此处加了次数限制。
            int attempts = 0;
            // 由于此异常可能是多种原因导致的，所以此处需要继续判断caused by的异常类型是否为主键冲突异常
            Throwable rootCause = e;
            // 如果caused by不为空，并且没有超过最大次数10
            while (rootCause.getCause() != null && attempts < Constants.CONSTANT_NUMBER_TEN) {
                rootCause = rootCause.getCause();
                // 如果是主键冲突异常则直接打印日志，不重试
                if (rootCause instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
                    return true;
                }
                attempts ++;
            }
        } catch (Exception ex) {
            logger.error("JyOperateFlowMqConsumer|判断是否是主键冲突异常时出现错误:messageText={},ex=", messageText, ex);
        }
        return false;
    }
}
