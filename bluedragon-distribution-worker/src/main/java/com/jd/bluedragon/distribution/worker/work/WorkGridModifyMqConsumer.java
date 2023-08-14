package com.jd.bluedragon.distribution.worker.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfig;
import com.jdl.basic.api.domain.workStation.WorkGridModifyMqData;

/**
 * 网格数据变更mq
 *
 * @author wuyoude
 * @date 2023/8/03 5:45 PM
 */
@Service("workGridModifyMqConsumer")
public class WorkGridModifyMqConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(WorkGridModifyMqConsumer.class);
    
    @Autowired
	@Qualifier("jyWorkGridManagerBusinessService")
    private JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("workGridModifyMqConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("网格数据变更，消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            WorkGridModifyMqData workGridModifyMqData = JsonHelper.fromJsonUseGson(message.getText(), WorkGridModifyMqData.class);
            if(workGridModifyMqData == null) {
                logger.warn("网格数据变更-消息内容无效，内容为【{}】", message.getText());
                return;
            }
            jyWorkGridManagerBusinessService.dealWorkGridModifyTask(workGridModifyMqData);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("网格数据变更-消费异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
