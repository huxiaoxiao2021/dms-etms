package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;

/**
 * <p>
 *     分拣称重量方处理的核心处理逻辑，
 *     包裹称重，按运单称重，按箱号称重，交接称重的汇总处理
 *
 * @author wuzuxiang
 * @since 2020/1/9
 **/
public interface DMSWeightVolumeService {

    /**
     * 分拣称重量方的处理逻辑
     * @param entity 称重量方实体
     * @param isSync 是否同步处理
     * @return
     */
    InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity, boolean isSync);

    /**
     * 分拣称重量方的处理逻辑 重构方法，同步处理
     * @param entity 称重量方实体
     * @return
     */
    InvokeResult<Boolean> dealWeightAndVolume(WeightVolumeEntity entity);

}
