package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigJsfRequest;
import com.jd.bluedragon.distribution.api.request.UploadDataJsfRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendSummaryResponse;

/**
 * 龙门架分拣机发货的核心逻辑的处理接口 以及 批次号的统计查询接口
 * 发往物流网关的接口不要在此类中加方法
 * dealScannerFrameConsume
 * countSendCode
 *
 * Created by wuzuxiang on 2018/11/7.
 */
public interface DmsScannerFrameService {

     /**
     * 处理
     *      多批次龙门架验货、发货、量方、应付量方
     *      分拣机自动发货
     * @param uploadData
     * @param config
     * @return true 处理成功  false 处理失败
     */
    boolean dealScannerFrameConsume(UploadDataJsfRequest uploadData, GantryDeviceConfigJsfRequest config);

    /**
     * 根据sendCode 计算批次号的包裹数量和包裹总体积
     * 该接口增加了体积是否统计的标识位，因为统计体积的逻辑特别的耗时
     * <attention>
     *          等wuyoude的青龙打印客户端《交接清单打印》功能的批次号的查询统计接口上线之后，该接口下线统一使用交接清单打印的接口
     * <attention/>
     * @param sendCodes 批次号
     * @param packageNumFlag 是否计算包裹数量的标识
     * @param volumeFlag 是否计算体积的标识
     * @return BatchSendSummaryResponse
     */
    @Deprecated
    BatchSendSummaryResponse countSendCode(String[] sendCodes, boolean packageNumFlag, boolean volumeFlag);
}
