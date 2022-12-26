package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jd.bluedragon.distribution.jy.send.SendVehicleCommonReq;
import com.jd.bluedragon.distribution.jy.send.SendVehicleToScanPackageDetailDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/7 15:09
 * @Description: 拣运发货岗相关服务
 */
public interface JySendVehicleTysJsfService {

    /**
     * 获取分拣发车岗待扫产品类型列表
     * @param request
     * @return
     */
    InvokeResult<List<JySendVehicleProductType>> getSendVehicleToScanProductType(SendVehicleCommonReq request);

    /**
     * 按产品类型获取分拣发车岗待扫包裹列表
     * @param request
     * @return
     */
    InvokeResult<SendVehicleToScanPackageDetailDto> getSendVehicleToScanPackages(SendVehicleCommonReq request);
}
