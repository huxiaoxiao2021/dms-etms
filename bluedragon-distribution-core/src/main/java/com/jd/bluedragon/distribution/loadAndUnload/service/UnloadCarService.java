package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.common.dto.unloadcar.UnloadCarDetailScanResult;
import com.jd.bluedragon.common.dto.unloadcar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadcar.UnloadCarScanResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:05
 */
public interface UnloadCarService {

    /**
     * 根据封车编码获取卸车任务
     *
     * @param sealCarCode
     * @return
     */
    InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode);

    /**
     * 卸车扫描
     *
     * @param request
     * @return
     */
    InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request);

    /**
     * 卸车扫描明细
     *
     * @param sealCarCode
     * @return
     */
    InvokeResult<List<UnloadCarDetailScanResult>> searchUnloadDetail(String sealCarCode);
}
