package com.jd.bluedragon.core.base;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/4
 * @Description:
 */
public interface WaybillRepaireManager {

    /**
     * 临时接口，处理运单异常数据获取运单
     * @param waybillRegionDto
     * @return
     */
    com.jd.etms.waybill.domain.BaseEntity<com.jd.etms.waybill.dto.WaybillRegionDto> getPackageCodeByOrgId(com.jd.etms.waybill.dto.WaybillRegionDto waybillRegionDto);

}
