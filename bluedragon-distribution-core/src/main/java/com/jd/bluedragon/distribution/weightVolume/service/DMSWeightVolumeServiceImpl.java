package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.WeightVolumeHandlerFactory;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣称重量方处理逻辑
 *
 * @author wuzuxiang
 * @since 2020/1/9
 **/
@Service("dmsWeightVolumeService")
public class DMSWeightVolumeServiceImpl implements DMSWeightVolumeService {

    private static final Logger logger = LoggerFactory.getLogger(DMSWeightVolumeServiceImpl.class);

    @Override
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity, boolean isSync) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);
        if (null == entity || StringHelper.isEmpty(entity.getBarCode())) {
            logger.error("称重量方操作单号为空：{}", JsonHelper.toJson(entity));
            result.parameterError(InvokeResult.PARAM_ERROR);
            result.setData(Boolean.FALSE);
            return result;
        }
        if (isSync) {
            //todo async
            return result;
        } else {
            return WeightVolumeHandlerFactory.getHandler(entity.getBarCode(),
                    WeightVolumeBusinessTypeEnum.TERMINAL_SITE_HANDOVER.equals(entity.getBusinessType())
                            || WeightVolumeBusinessTypeEnum.DMS_HANDOVER.equals(entity.getBusinessType())
            ).handlerOperateWeightVolume(entity);
        }
    }

    @Override
    public InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity) {
        /* 同步处理 */
        return this.dealWeightAndVolume(entity,true);
    }
}
