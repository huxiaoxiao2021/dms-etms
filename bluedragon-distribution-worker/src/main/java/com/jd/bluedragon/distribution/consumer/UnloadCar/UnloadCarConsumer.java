package com.jd.bluedragon.distribution.consumer.UnloadCar;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.loadAndUnload.TmsSealCar;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author lijie
 * @Description 卸车任务消费逻辑
 * @date 2020/6/25 21:30
 */
@Service("unloadCarConsumer")
public class UnloadCarConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarService unloadCarService;

    //操作状态：10-封车，20-解封，30-出围栏，40-进围栏
    private static final Integer SEAL_CAR_STATUS = 10;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("封车解封车状态变化下发消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        try {
            TmsSealCar tmsSealCar = JsonHelper.fromJsonUseGson(message.getText(), TmsSealCar.class);

            if (StringUtils.isEmpty(tmsSealCar.getOperateSiteCode()) || CollectionUtils.isEmpty(tmsSealCar.getBatchCodes())
                    || StringUtils.isEmpty(tmsSealCar.getSealCarCode())){
                log.warn("封车解封车状态变化下发消息体缺少必要字段，内容为【{}】", message.getText());
                return;
            }

            if (!SEAL_CAR_STATUS.equals(tmsSealCar.getStatus())) {
                log.warn("封车解封车状态变化下发消息体为非封车，不需要消费，内容为【{}】", message.getText());
                return;
            }

            unloadCarService.insertUnloadCar(tmsSealCar);
        } catch (Exception e) {
            log.error("[卸车任务]消费封车解封车状态变化MQ时发生异常,内容为【{}】", message.getText(), e);
            throw new RuntimeException(e);
        }
    }
}
