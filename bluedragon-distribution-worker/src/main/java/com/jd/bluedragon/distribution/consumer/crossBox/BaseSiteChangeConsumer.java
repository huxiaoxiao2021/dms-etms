package com.jd.bluedragon.distribution.consumer.crossBox;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("baseSiteChangeConsumer")
public class BaseSiteChangeConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(BaseSiteChangeConsumer.class);
    @Autowired
    private CrossBoxService crossBoxService;
    @Override
    @JProfiler(jKey = "DMS.BASE.BaseSiteChageConsumer.consume", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public void consume(Message message) throws Exception {
        //处理消息体
        log.info("SiteNameChageConsumer consume --> 消息Body为【{}】", message.getText());
        //反序列化
        SiteMessage mqData = JsonHelper.fromJson(message.getText(), SiteMessage.class);
        if (mqData == null) {
            log.warn("SiteNameChageConsumer consume --> 消息转换对象失败：{}", message.getText());
            return;
        }
		crossBoxService.updateSiteName(mqData.getSiteCode(),mqData.getSiteName());
    }

}
