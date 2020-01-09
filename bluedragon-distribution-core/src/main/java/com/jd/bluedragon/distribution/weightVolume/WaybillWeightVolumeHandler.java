package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     发送MQ:dms_waybill_weight
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("waybillWeightVolumeHandler")
public class WaybillWeightVolumeHandler extends AbstractWeightVolumeHandler {

    /* MQ消息生产者： topic:dms_waybill_weight*/
    @Autowired
    @Qualifier("weighByWaybillProducer")
    private DefaultJMQProducer weighByWaybillProducer;

    @Override
    protected boolean checkWeightVolumeParam(WeightVolumeEntity entity) {
        if (super.checkWeightVolumeParam(entity)) {
            return WaybillUtil.isWaybillCode(entity.getBarCode()) || WaybillUtil.isWaybillCode(entity.getWaybillCode());
        }
        return Boolean.FALSE;
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(entity.getBarCode());

        WaybillWeightDTO weightDTO = new WaybillWeightDTO();
        weightDTO.setWaybillCode(entity.getWaybillCode());
        weightDTO.setOperatorSiteCode(entity.getOperateSiteCode());
        weightDTO.setOperatorSiteName(entity.getOperateSiteName());
        weightDTO.setOperatorId(entity.getOperatorId());
        weightDTO.setOperatorName(entity.getOperatorName());
        weightDTO.setWeight(entity.getWeight());
        if (entity.getLength() != null && entity.getWidth() != null && entity.getHeight() != null) {
            weightDTO.setVolume(entity.getHeight() * entity.getWidth() * entity.getLength());
        }
        weightDTO.setOperateTimeMillis(entity.getOperateTime().getTime());
        weightDTO.setStatus(10);
        try {
            weighByWaybillProducer.send(entity.getWaybillCode(), JsonHelper.toJson(weightDTO));
        } catch (JMQException e) {
            logger.error("发送MQ-TOPIC【{}】消息失败，消息体为：{}",weighByWaybillProducer.getTopic(),JsonHelper.toJson(weightDTO));
        }
    }
}
