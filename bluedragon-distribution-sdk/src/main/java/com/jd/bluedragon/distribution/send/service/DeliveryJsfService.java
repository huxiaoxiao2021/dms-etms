package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

import java.util.List;
import java.util.Map;

public interface DeliveryJsfService {
    /**
     * 检验 条码是否已经在createSiteCode发货
     * @param barcode 包裹号 或箱号
     * @param createSiteCode 场地
     * @return
     */
    InvokeResult<Boolean> checkIsSend(String barcode, Integer createSiteCode);

    /**
     * 获取已发货批次下和指定运单下的包裹号
     * @param createSiteCode 操作站点
     * @param batchCode 批次号
     * @param waybillCode 运单号
     * @return
     */
    InvokeResult<List<String>> queryPackageCodeBySendAndWaybillCode(Integer createSiteCode, String batchCode, String waybillCode);

    /**
     * 获取已发货批次下的包裹号
     * @param createSiteCode 操作站点
     * @param batchCode 批次号
     * @return
     */
    InvokeResult<List<String>> queryPackageCodeBySendCode(Integer createSiteCode, String batchCode);

    /**
     * 获取已发货批次下的包裹号
     * @param createSiteCode 操作站点
     * @param receiveSiteCode 下一站点
     * @param barCode 包裹号或运单号
     * @return
     */
    InvokeResult<String> findByWaybillCodeOrPackageCode(Integer createSiteCode, Integer receiveSiteCode, String barCode);

    /**
     * 获取已发货批次下的运单总数和包裹总数
     * @param createSiteCode 操作站点
     * @param batchCodes 批次号集合
     * @return
     */
    InvokeResult<Map<String, Integer>> queryPackageAndWaybillNumByBatchCodes(Integer createSiteCode, List<String> batchCodes);

}
