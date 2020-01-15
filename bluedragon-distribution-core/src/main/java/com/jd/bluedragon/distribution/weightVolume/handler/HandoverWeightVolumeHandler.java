package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDetailDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * <p>
 *     交接称重处理类：
 *     交接称重定义为运单那边第一次会记录交接称重流水，不更新负重信息，后续的会记录正常的负重信息
 *     目前属于交接称重的业务范畴有：1.加盟商称重（在平台打印和站点平台打印操作）
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("handoverWeightVolumeHandler")
public class HandoverWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    @Qualifier("opeWeightProducer")
    private DefaultJMQProducer opeWeightProducer;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        if (WaybillUtil.isWaybillCode(entity.getBarCode())) {
            entity.setWaybillCode(entity.getBarCode());
        }
        if (WaybillUtil.isPackageCode(entity.getBarCode())) {
            entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
            entity.setPackageCode(entity.getBarCode());
        }
        if (entity.getLength() != null && entity.getWidth() != null && entity.getHeight() != null) {
            entity.setVolume(entity.getHeight() * entity.getLength() * entity.getWidth());
        }

        /* 调用原始的加盟商的逻辑 */
        AllianceBusiDeliveryDto allianceBusiDeliveryDto = new AllianceBusiDeliveryDto();
        allianceBusiDeliveryDto.setForce(Boolean.FALSE);
        allianceBusiDeliveryDto.setOperatorId(entity.getOperatorId());
        allianceBusiDeliveryDto.setOperatorName(entity.getOperatorName());
        allianceBusiDeliveryDto.setOperatorSiteCode(entity.getOperateSiteCode());
        allianceBusiDeliveryDto.setOperatorSiteName(entity.getOperateSiteName());
        allianceBusiDeliveryDto.setOpeType(4);//todo 什么是揽收 ，什么是派送
        AllianceBusiDeliveryDetailDto detailDto = new AllianceBusiDeliveryDetailDto();
        detailDto.setOpeCode(entity.getBarCode());
        detailDto.setHeight(entity.getHeight());
        detailDto.setLength(entity.getLength());
        detailDto.setWidth(entity.getWidth());
        detailDto.setVolume(entity.getVolume());
        detailDto.setWeight(entity.getWeight());
        detailDto.setOperateTimeMillis(entity.getOperateTime().getTime());
        allianceBusiDeliveryDto.setDatas(Collections.singletonList(detailDto));

        allianceBusiDeliveryDetailService.allianceBusiDelivery(allianceBusiDeliveryDto);
    }

    @Override
    protected InvokeResult weighVolumeIntercept(WeightVolumeEntity entity) {
        InvokeResult result = new InvokeResult();
        result.success();
        //TODO 加盟商
        return result;
    }
}
