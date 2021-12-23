package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageNotScanItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.*;

import java.util.List;

/**
 * 快递弃件暂存网关接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 16:37:43 周四
 */
public interface DiscardedStorageGatewayService {


    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    JdCResponse<List<DiscardedWaybillScanResultItemDto>> queryUnSubmitDiscardedList(QueryUnSubmitDiscardedListPo paramObj);

    /**
     * 弃件暂存提交
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:43 周四
     */
    JdCResponse<List<DiscardedWaybillScanResultItemDto>> scanDiscardedPackage(ScanDiscardedPackagePo paramObj);

    /**
     * 弃件暂存提交已扫描弃件数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    JdCResponse<Boolean> submitDiscardedPackage(SubmitDiscardedPackagePo paramObj);

    /**
     * 查询未扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:55:24 周四
     */
    JdCResponse<List<DiscardedPackageNotScanItemDto>> queryUnScanDiscardedPackage(QueryUnScanDiscardedPackagePo paramObj);

}
