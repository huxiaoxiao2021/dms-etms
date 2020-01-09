package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;

/**
 * <p>
 *     分拣操作称重量方的处理接口类
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public interface IWeightVolumeHandler {

    InvokeResult<Boolean> handlerOperateWeightVolume(WeightVolumeEntity entity);
}
