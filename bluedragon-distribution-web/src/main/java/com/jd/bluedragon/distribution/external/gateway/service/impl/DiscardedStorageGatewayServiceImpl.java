package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageNotScanItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.*;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.external.gateway.service.DiscardedStorageGatewayService;
import com.jd.dms.workbench.utils.sdk.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 弃件暂存网关接口实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 18:11:58 周四
 */
@Service("discardedStorageGatewayServiceImpl")
public class DiscardedStorageGatewayServiceImpl implements DiscardedStorageGatewayService {

    @Autowired
    private DiscardedPackageStorageTempService discardedPackageStorageTempService;

    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    @Override
    public JdCResponse<List<DiscardedWaybillScanResultItemDto>> queryUnSubmitDiscardedList(QueryUnSubmitDiscardedListPo paramObj) {
        final Result<List<DiscardedWaybillScanResultItemDto>> result = discardedPackageStorageTempService.queryUnSubmitDiscardedList(paramObj);
        return convertResultToJdcResponse(result);
    }

    /**
     * 弃件暂存提交
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:43 周四
     */
    @Override
    public JdCResponse<List<DiscardedWaybillScanResultItemDto>> scanDiscardedPackage(ScanDiscardedPackagePo paramObj) {
        final Result<List<DiscardedWaybillScanResultItemDto>> result = discardedPackageStorageTempService.scanDiscardedPackage(paramObj);
        return convertResultToJdcResponse(result);
    }

    /**
     * 弃件暂存提交已扫描弃件数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    @Override
    public JdCResponse<Boolean> submitDiscardedPackage(SubmitDiscardedPackagePo paramObj) {
        final Result<Boolean> result = discardedPackageStorageTempService.submitDiscardedPackage(paramObj);
        return convertResultToJdcResponse(result);
    }

    /**
     * 查询未扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:55:24 周四
     */
    @Override
    public JdCResponse<List<DiscardedPackageNotScanItemDto>> queryUnScanDiscardedPackage(QueryUnScanDiscardedPackagePo paramObj) {
        final Result<List<DiscardedPackageNotScanItemDto>> result = discardedPackageStorageTempService.queryUnScanDiscardedPackage(paramObj);
        return convertResultToJdcResponse(result);
    }

    private <T> JdCResponse<T> convertResultToJdcResponse(Result<T> result){
        JdCResponse<T> response = new JdCResponse<>();
        response.toFail();
        if(result == null){
            return response;
        }
        if(!result.isSuccess()){
            response.init(result.getCode(), result.getMessage());
            return response;
        }
        if(result.isSuccess()){
            response.toSucceed(result.getMessage());
            response.setData(result.getData());
            return response;
        }
        return response;
    }
}
