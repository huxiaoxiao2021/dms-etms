package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigJsfRequest;
import com.jd.bluedragon.distribution.api.request.UploadDataJsfRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendSummaryResponse;

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

    /**
     * 根据sendCode 计算批次号的包裹数量和包裹总体积
     * @param sendCodes 批次号
     * @param packageNumFlag 是否计算包裹数量的标识
     * @param volumeFlag 是否计算体积的标识
     * @return
     */
    BatchSendSummaryResponse countSendCode(String[] sendCodes, boolean packageNumFlag, boolean volumeFlag);
}
