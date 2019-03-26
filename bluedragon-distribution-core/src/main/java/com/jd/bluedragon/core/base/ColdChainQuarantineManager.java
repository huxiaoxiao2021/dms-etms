package com.jd.bluedragon.core.base;

public interface ColdChainQuarantineManager {
    /**
     * 运单在当前操作单位是否需要录入检疫证票号
     * @param waybillCode
     * @param siteCode
     * @return
     */
    Boolean isWaybillNeedAddQuarantine(String waybillCode, Integer siteCode);
}
