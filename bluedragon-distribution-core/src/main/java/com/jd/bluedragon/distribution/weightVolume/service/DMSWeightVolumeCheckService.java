package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/7/25 6:17 PM
 */
public interface DMSWeightVolumeCheckService {

    InvokeResult<Boolean> checkJPIsCanWeight(String barCode, Integer siteCode);
}
