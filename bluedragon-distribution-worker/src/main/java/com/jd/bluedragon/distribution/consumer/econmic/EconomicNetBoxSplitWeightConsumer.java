package com.jd.bluedragon.distribution.consumer.econmic;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.distribution.economic.service.IEconomicNetService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/20 10:49
 * @Description: 经济网按箱称重分页拆分包裹称重消息
 *
 */
@Service("economicNetBoxSplitWeightConsumer")
public class EconomicNetBoxSplitWeightConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(EconomicNetBoxSplitWeightConsumer.class);

    @Autowired
    private IEconomicNetService economicNetService;

    @Autowired
    private BoxService boxService;

    @Override
    public void consume(Message message) throws Exception {

        if(message == null || StringUtils.isBlank(message.getText()) ){
            return;
        }
        List<WeightVolumeEntity> splits = null;
        try{
            splits = JsonHelper.jsonToList(message.getText(), WeightVolumeEntity.class);
        }catch (Exception e){
            logger.error("economicNetBoxWeightConsumer parse json error! {}",message.getText(),e);
        }

        if(!economicNetService.dealBoxSplitWeightOfPage(splits)){
            throw new EconomicNetException("经济网分页处理包裹称重数据失败");
        }
    }

}
