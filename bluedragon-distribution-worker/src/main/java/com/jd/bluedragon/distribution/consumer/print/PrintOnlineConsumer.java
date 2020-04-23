package com.jd.bluedragon.distribution.consumer.print;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDTO;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("printOnlineConsumer")
public class PrintOnlineConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(PrintOnlineConsumer.class);

    private static final String PRINT_ONLINE_KEY = "PRINT-ONLINE-KEY-";

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private IPrintOnlineService printOnlineService;

    @Override
    public void consume(Message message) throws Exception {
        if(message == null || "".equals(message.getText()) || null == message.getText()){
            this.log.warn("线上签推送的消息体内容为空");
            return;
        }
        //筛选重复 以KEY为准 这样如果有问题时重推数据可修改key不修改body
        if(check(message.getBusinessId())){
            if(!printOnlineService.reversePrintOnline(message.getText())){
                del(message.getBusinessId());
                throw new RuntimeException("线上签推送失败转重试"+message.getText());
            }
        }


    }

    /**
     * 滤重插入KEY  如果REIDS异常则抛出重试
     * @param key
     * @return
     */
    private boolean check(String key){
        try{
            String printOnlineKey = PRINT_ONLINE_KEY + key;
            if(!cacheService.setNx(printOnlineKey, StringUtils.EMPTY,60, TimeUnit.DAYS)){
                log.warn("线上签滤重:{}", key);
                return false;
            }
        }catch (Exception e){
            log.error("线上签滤重异常:{}", key,e);
            throw new RuntimeException("线上签滤重失败，转重试"+key);
        }
        return true;
    }

    /**
     * 删除缓存KEY
     * 如果此时redis异常暂定人工参与处理
     * @param key
     * @return
     */
    private void del(String key){
        try{
            String printOnlineKey = PRINT_ONLINE_KEY + key;
            cacheService.del(printOnlineKey);
        }catch (Exception e){
            log.error("线上签删除异常:{}", key,e);
        }
    }

}
