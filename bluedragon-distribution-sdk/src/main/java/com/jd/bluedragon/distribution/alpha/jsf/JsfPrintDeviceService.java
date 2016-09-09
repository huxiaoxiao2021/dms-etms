package com.jd.bluedragon.distribution.alpha.jsf;

import com.jd.bluedragon.distribution.alpha.PrintDeviceRequest;
import com.jd.bluedragon.distribution.alpha.PrintDeviceResponse;

/**
 * Created by wuzuxiang on 2016/9/1.
 */
public interface JsfPrintDeviceService {

    /**
     * 校验IVS的版本信息，返回最新版本信息
     * @param request
     * @return
     */
    public PrintDeviceResponse PrintDeviceUpdata(PrintDeviceRequest request);
}
