package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.ql.dms.report.domain.LoadScanDto;

import java.util.List;

public interface DmsDisSendService {

    /**
     * 根据运单号获取装车扫描运单明细
     * @param scanDtoList   查询条件列表
     * @param currentSiteId 当前网点id
     * @return 包裹详情集合
     */
    List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId);

    /**
     * 根据运单号、当前网点、下一网点获取装车扫描列表
     * @param waybillCodes  运单号列表
     * @param currentSiteId 当前网点id
     * @param nextSiteId 下一网点id
     * @param rows 查询数量
     * @return 包裹详情集合
     */
    List<LoadScanDto> getLoadScanByWaybillCodes(List<String> waybillCodes, Integer currentSiteId,
                                                Integer nextSiteId, Integer rows);

    /**
     * 根据运单号获取装车扫描
     * @param loadScanDto 查询条件列表
     * @return 包裹详情
     */
    LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto);

    /**
     * 根据运单号、当前网点、已装包裹号查询未装包裹号列表
     * @param waybillCode 运单号
     * @param createSiteId 当前网点ID
     * @param packageCodes 已装包裹号列表
     * @return 未装包裹号列表
     */
    List<String> getUnloadPackageCodesByWaybillCode(String waybillCode, Integer createSiteId,
                                                    List<String> packageCodes);
    /**
     * 根据运单号、当前网点查询库存包裹号列表
     * @param waybillCode 运单号
     * @param createSiteId 当前网点ID
     * @return 库存包裹号列表
     */
    LoadScanDto getPackageCodesByWaybillCode(String waybillCode, Integer createSiteId);

}
