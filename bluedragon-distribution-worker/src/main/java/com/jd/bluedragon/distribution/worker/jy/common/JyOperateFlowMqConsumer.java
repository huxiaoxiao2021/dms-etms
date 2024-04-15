
package com.jd.bluedragon.distribution.worker.jy.common;

import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.jim.cli.Cluster;
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

import java.util.concurrent.TimeUnit;

/**
 * 分拣操作流水mq消费
 *
 * @author wuyoude
 * @date 2023/8/03 5:45 PM
 */
@Service("jyOperateFlowMqConsumer")
public class JyOperateFlowMqConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(JyOperateFlowMqConsumer.class);

    /**
     * 操作流水锁前缀
     */
    private static final String JY_OPERATE_FLOW_PREFIX = "jy:operate:flow:";
    
    @Autowired
	@Qualifier("jyOperateFlowService")
    private JyOperateFlowService jyOperateFlowService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("jyOperateFlowMqConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);

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

        // 操作流水表主键
        Long id = jyOperateFlowDto.getId();
        // 操作流水锁前缀
        String mutexKey = JY_OPERATE_FLOW_PREFIX + id;
        // 运单加锁防止并发问题
        if (!redisClientOfJy.set(mutexKey, Constants.EMPTY_FILL, Constants.CONSTANT_NUMBER_ONE, TimeUnit.MINUTES, false)) {
            logger.warn("操作流水保存正在处理中,稍后重试:jyOperateFlowDto={}", JsonHelper.toJsonMs(jyOperateFlowDto));
            throw new JyBizException("id:" + id + "-操作流水保存正在处理中,稍后重试");
        }

        try {
            // 根据分区键和id查询一条记录
            if (jyOperateFlowService.findByOperateBizKeyAndId(jyOperateFlowDto) != null) {
                // 如果不为空则为重复消费
                logger.warn("操作流水重复消费忽略:jyOperateFlowDto={}", JsonHelper.toJsonMs(jyOperateFlowDto));
                return;
            }
            // 如果查不到则新增
            jyOperateFlowService.insert(jyOperateFlowDto);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("JyOperateFlowMq-消费异常-e, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            redisClientOfJy.del(mutexKey);
            Profiler.registerInfoEnd(info);
        }
    }
}
