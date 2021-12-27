package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.ZeroWeightVolumeCheckEntity;

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

    /**
     * 称重量方规则校验
     * @param condition
     * @return
     */
    InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition);

    /**
     * 重量体积长宽高超额处理
     *  返回备注字段：记录原始记录
     * @param condition
     * @return
     */
    String weightVolumeExcessDeal(WeightVolumeCondition condition);

    /**
     *
     * 无称重量方拦截
     * @param entity
     * @return
     */
    Boolean zeroWeightVolumeIntercept(ZeroWeightVolumeCheckEntity entity);

}
