package com.jd.bluedragon.distribution.weightvolume;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

/**
 * <p>
 *     分拣称重量方操作处理jsf接口
 *     内部系统调用
 *     目前有自动化系统
 *
 * @author wuzuxiang
 * @since 2020/1/10
 **/
public interface DMSWeightVolumeJSFService {

    /**
     * 同步方法处理称重量方
     */
    InvokeResult<Boolean> dealSyncWeightVolume(WeightVolumeJSFEntity entity);

    /**
     * 异步方式处理称重量方
     */
    InvokeResult<Boolean> dealAsyncWeightVolume(WeightVolumeJSFEntity entity);

}
