package com.jd.bluedragon.distribution.weightVolume.check;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;

/**
 * <p>
 *     称重的校验逻辑
 *     是否允许进行本次称重量方
 *
 * @author wuzuxiang
 * @since 2020/1/15
 **/
public interface IWeightVolumeCheck {

    /**
     * 校验参数，校验本次操作的合法性
     * @param entity 称重实体
     * @return
     */
    InvokeResult<Boolean> checkOperateWeightVolume(WeightVolumeEntity entity);
}
