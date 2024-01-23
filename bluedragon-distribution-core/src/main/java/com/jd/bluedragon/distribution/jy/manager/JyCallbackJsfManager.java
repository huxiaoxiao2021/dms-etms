package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackRespDto;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.distribution.jy.manager
 * @Description:
 * @date Date : 2024年01月23日 10:34
 */
public interface JyCallbackJsfManager {

    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanCheckOfCallback(UnloadScanCallbackReqDto request);

    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanOfCallback(UnloadScanCallbackReqDto request);
}
