package com.jd.bluedragon.distribution.consumer.print;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.recycle.material.service.RecycleMaterialService;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 周转筐批量作废消息处理
 *
 * @author hujiping
 * @date 2023/4/14 4:40 PM
 */
@Service("recycleBasketAbolishConsumer")
public class RecycleBasketAbolishConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RecycleBasketAbolishConsumer.class);
    
    @Autowired
    private RecycleMaterialService recycleMaterialService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("RecycleBasketAbolishConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("周转筐批量作废消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            MaterialAbolishReq materialAbolishReq = JsonHelper.fromJsonUseGson(message.getText(), MaterialAbolishReq.class);
            if(materialAbolishReq == null) {
                logger.warn("周转筐批量作废消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            
            // 批量作废
            recycleMaterialService.syncAbolishRecycleBasket(materialAbolishReq);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("周转筐批量作废异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
