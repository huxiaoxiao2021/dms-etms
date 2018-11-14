package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigJsfRequest;
import com.jd.bluedragon.distribution.api.request.UploadDataJsfRequest;

/**
 * Created by wuzuxiang on 2018/11/7.
 */
public interface DmsScannerFrameService {

     /**
     * 处理
     *      多批次龙门架验货、发货、量方、应付量方
     *      分拣机自动发货
     * @param uploadData
     * @param config
     * @return
     */
    boolean dealScannerFrameConsume(UploadDataJsfRequest uploadData, GantryDeviceConfigJsfRequest config);
}
