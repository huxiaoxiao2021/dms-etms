package com.jd.bluedragon.distribution.jy.api.callback;

import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackReqDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanCallbackRespDto;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/18
 * @Description:
 *
 *  拣运作业工作台 卸车验货 回调 协议
 */
public interface JyUnloadVehicleCallbackJsfService {

    /**
     * 拣运作业工作台 卸车验货 校验环节回调
     * @param request
     * @return
     *
     * InvokeResult 中的code 返回非成功的时候（不等于200）认为服务异常。会阻断后续的代码执行逻辑。
     * 如果只是为了做提醒类的场景，则放在msgbox中处理，依赖特定的msgbox code来做处理。
     */
    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanCheckOfCallback(UnloadScanCallbackReqDto request);

    /**
     * 拣运作业工作台 卸车验货 执行环节回调
     * @param request
     * @return
     */
    InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> unloadScanOfCallback(UnloadScanCallbackReqDto request);

}
