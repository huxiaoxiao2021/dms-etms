package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDetailDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
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
    protected boolean checkWeightVolumeParam(WeightVolumeEntity entity) {
        if (super.checkWeightVolumeParam(entity)) {
            return WaybillUtil.isPackageCode(entity.getBarCode()) || WaybillUtil.isWaybillCode(entity.getWaybillCode());
        }
        return Boolean.FALSE;
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        int codeType = 0;
        if (WaybillUtil.isWaybillCode(entity.getBarCode())) {
            entity.setWaybillCode(entity.getBarCode());
            codeType = 1;
        }
        if (WaybillUtil.isPackageCode(entity.getBarCode())) {
            entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
            entity.setPackageCode(entity.getBarCode());
            codeType = 2;
        }
        if (entity.getLength() != null && entity.getWidth() != null && entity.getHeight() != null) {
            entity.setVolume(entity.getHeight() * entity.getLength() * entity.getWidth());
        }

//        WeightOpeDto weightOpeDto = new WeightOpeDto();
//        weightOpeDto.setOperateCode(entity.getBarCode());
//        weightOpeDto.setCodeType(codeType);
//        weightOpeDto.setOperateSite(entity.getOperateSiteName());
//        weightOpeDto.setOperateSiteId(entity.getOperateSiteCode());
//        weightOpeDto.setOperateUserId(entity.getOperatorId());
//        weightOpeDto.setOperateUser(entity.getOperatorName());
//        weightOpeDto.setOperateTime(entity.getOperateTime().getTime());
//        weightOpeDto.setWeight(entity.getWeight());
//        weightOpeDto.setVolumeHeight(entity.getHeight());
//        weightOpeDto.setVolumeLength(entity.getLength());
//        weightOpeDto.setVolumeWidth(entity.getWidth());
//        weightOpeDto.setVolume(entity.getVolume());
//        if (WeightVolumeBusinessTypeEnum.TERMINAL_SITE_HANDOVER.equals(entity.getBusinessType())) {
//            weightOpeDto.setOpeType(4);
//        } else {
//            weightOpeDto.setOpeType(5);
//        }
//        opeWeightProducer.sendOnFailPersistent(entity.getBarCode(), JsonHelper.toJson(weightOpeDto));

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
}
