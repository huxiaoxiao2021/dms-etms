package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dto.send.*;


/**
 * 异常扫描统计服务
 * （拦截 强发 不齐等包裹明细）
 */
public interface ScanStatisticsService {


    /**
     * 查询异常运单列表
     *
     * @param queryExcepWaybillDto
     * @return
     */
    ExcepWaybillDto queryExcepScanWaybill(QueryExcepWaybillDto queryExcepWaybillDto);

    /**
     * 查询异常包裹明细
     *
     * @param queryExcepPackageDto
     * @return
     */
    ExcepPackageDto queryExcepPackageUnderWaybill(QueryExcepPackageDto queryExcepPackageDto);

}
