package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
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

    @Override
    protected boolean checkWeightVolumeParam(WeightVolumeEntity entity) {
        if (super.checkWeightVolumeParam(entity)) {
            return WaybillUtil.isPackageCode(entity.getBarCode()) || WaybillUtil.isPackageCode(entity.getPackageCode());
        }
        return Boolean.FALSE;
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        entity.setPackageCode(entity.getBarCode());
        if (entity.getLength() != null && entity.getHeight() != null && entity.getVolume() != null) {
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
        Map<String, Object> resultMap = waybillPackageManager.uploadOpe(JsonHelper.toJson(packOpeDto));

    }
}
