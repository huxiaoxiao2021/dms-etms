package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weight.domain.OpeSendObject;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * <p>
 *     安包裹称重的处理类：
 *     调用运单的包裹称重的处理waybillPackageApi.uploadOpe()
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("packageWeightVolumeHandler")
public class PackageWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("dmsWeightSendMQ")
    private DefaultJMQProducer dmsWeightSendMQ;

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        entity.setPackageCode(entity.getBarCode());
        if (entity.getLength() != null && entity.getHeight() != null && entity.getVolume() != null && entity.getVolume() == null) {
            entity.setVolume(entity.getHeight() * entity.getLength() * entity.getWidth());
        }

        PackOpeDto packOpeDto = new PackOpeDto();
        packOpeDto.setWaybillCode(entity.getWaybillCode());
        packOpeDto.setOpeType(1);//分拣操作环节赋值：1

        PackOpeDetail packOpeDetail = new PackOpeDetail();
        packOpeDetail.setPackageCode(entity.getPackageCode());
        packOpeDetail.setOpeSiteId(entity.getOperateSiteCode());
        packOpeDetail.setOpeSiteName(entity.getOperateSiteName());
        packOpeDetail.setOpeUserId(entity.getOperatorId());
        packOpeDetail.setOpeUserName(entity.getOperatorName());
        packOpeDetail.setpHigh(entity.getHeight());
        packOpeDetail.setpLength(entity.getLength());
        packOpeDetail.setpWidth(entity.getWidth());
        packOpeDetail.setpWeight(entity.getWeight());
        packOpeDetail.setOpeTime(DateHelper.formatDateTime(entity.getOperateTime()));
        packOpeDto.setOpeDetails(Collections.singletonList(packOpeDetail));
        try {
            Map<String, Object> resultMap = waybillPackageManager.uploadOpe(JsonHelper.toJson(packOpeDto));
            if (resultMap != null && resultMap.containsKey("code")
                    && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(resultMap.get("code").toString())) {
                logger.info("向运单系统回传包裹称重信息成功：{}", entity.getPackageCode());
            } else {
                logger.warn("向运单系统回传包裹称重信息失败：{}，运单返回值：{}", entity.getPackageCode(), JsonHelper.toJson(resultMap));
            }

            /* 原始逻辑：发送MQ，在未来的日志里看看能否合并 */
            OpeSendObject opeSend = new OpeSendObject();
            opeSend.setPackage_code(entity.getPackageCode());
            opeSend.setDms_site_id(entity.getOperateSiteCode());
            opeSend.setThisUpdateTime(entity.getOperateTime().getTime());
            opeSend.setWeight(entity.getWeight() == null? 0f : (float)(double)entity.getWeight());//精度丢失问题
            opeSend.setLength(entity.getLength() == null? 0f :(float)(double)entity.getLength());//精度丢失问题
            opeSend.setWidth(entity.getWidth() == null? 0f :(float)(double)entity.getWidth());//精度丢失问题
            opeSend.setHigh(entity.getHeight() == null? 0f :(float)(double)entity.getHeight());//精度丢失问题
            opeSend.setOpeUserId(entity.getOperatorId());
            opeSend.setOpeUserName(entity.getOperatorName());
            if (opeSend.getHigh() != null && opeSend.getLength() != null && opeSend.getWidth() != null) {
                //计算体积
                opeSend.setVolume(opeSend.getHigh() * opeSend.getLength() * opeSend.getWidth());
            }
            dmsWeightSendMQ.send(entity.getPackageCode(),JsonHelper.toJson(opeSend));
        } catch (RuntimeException | JMQException e) {
            logger.warn("按包裹称重量方发生异常，处理失败：{}",JsonHelper.toJson(entity));
        }
    }

}
