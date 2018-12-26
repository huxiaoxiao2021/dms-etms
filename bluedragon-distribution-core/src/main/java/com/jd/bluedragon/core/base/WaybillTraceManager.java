package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.PackageState;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 全程跟踪工具
 * @date 2018年12月25日 16时:42分
 */
public interface WaybillTraceManager {
    /**
     *查运单的 某个状态的全程跟踪
     * @param waybillCode
     * @param state
     * @return
     */
    List<PackageState> getPkStateByWCodeAndState(String waybillCode, String state);
}
