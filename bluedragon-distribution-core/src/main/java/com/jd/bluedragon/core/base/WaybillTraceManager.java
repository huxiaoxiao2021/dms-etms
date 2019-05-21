package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.BaseEntity;
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

    /**
     * 判断运单是否已经妥投
     * @param waybillCode 运单号
     * @return true表示已经妥投，false表示还没有妥投
     */
    boolean isWaybillFinished(String waybillCode);

    /**
     * 判断运单是否被拒收
     * @param waybillCode 运单号
     * @return true表示被拒收，false表示还没有拒收
     */
    boolean isWaybillRejected(String waybillCode);

    /**
     * 获取包裹的全程跟踪状态
     * @param packageCode
     * @return
     */
    BaseEntity<List<PackageState>> getPkStateByPCode(String packageCode);
}
