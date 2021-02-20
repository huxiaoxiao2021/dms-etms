package com.jd.bluedragon.distribution.consumer.weight;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.economic.service.IEconomicNetService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/21 16:36
 * @Description: 统一称重量方消息监听
 *  如果监听内容处理逻辑过多需要自行转发单独MQ再去处理对应业务逻辑
 */
@Service("dmsWeightVolumeFlowConsumer")
public class DmsWeightVolumeFlowConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(DmsWeightVolumeFlowConsumer.class);

    @Autowired
    private IEconomicNetService economicNetService;

    @Override
    public void consume(Message message) throws Exception {
        if(message == null || StringUtils.isBlank(message.getText())){
            return;
        }
        WeightVolumeEntity weightVolumeEntity = null;
        try{
            weightVolumeEntity = JsonHelper.fromJson(message.getText(),WeightVolumeEntity.class);
        }catch (Exception e){
            logger.error("DmsWeightVolumeFlowConsumer parse json error {}",message.getText(),e);
            return;
        }

        if(weightVolumeEntity == null){
            return;
        }

        //经济网单独称重需重新触发按箱称重均摊
        economicNetService.packageOrWaybillWeightVolumeListener(weightVolumeEntity);

        //经济网按箱称重需要触发均摊 并批量拉取数据
        economicNetService.boxWeightVolumeListener(weightVolumeEntity);

    }


}
