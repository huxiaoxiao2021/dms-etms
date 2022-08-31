package com.jd.bluedragon.distribution.label.api;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;

/**
 * 标签对外jsf接口
 *
 * @author hujiping
 * @date 2022/8/22 5:08 PM
 */
public interface LabelPrintJsfService {

    /**
     * 根据编码查询砝码信息
     *
     * @param farmarCode
     * @return
     */
    InvokeResult<FarmarEntity> getFarmarInfoByCode(String farmarCode);
}
