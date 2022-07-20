package com.jd.bluedragon.distribution.aicv;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRRequestDto;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRResponseDto;
import com.jd.wl.ai.cv.center.outter.api.dto.IDCRStatusEnum;
import com.jd.wl.ai.cv.center.outter.api.service.IDCRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.aicv
 * @ClassName: IDCRServiceProxy
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/7/11 23:39
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Slf4j
@Component
public class IDCRServiceProxy {

    @Autowired
    private IDCRService idcrService;

    public IDCRResponseDto recognisePhoto(IDCRRequestDto idcrRequestDto) {

        log.info("调用AI识别身份证信息，调用链ID:{}", idcrRequestDto.getServiceUUID());
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.IDCRServiceProxy.recognisePhoto", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            IDCRResponseDto idcrResponseDto = idcrService.recognisePhoto(idcrRequestDto);
            log.info("调用AI识别身份证信息，请求参数：{}，返回信息: {}", JsonHelper.toJson(idcrRequestDto), JsonHelper.toJson(idcrResponseDto));
            if (idcrResponseDto == null || !IDCRStatusEnum.OK.getCode().equals(idcrResponseDto.getStatus())) {
                log.error("调用AI识别身份证信息失败，请求参数：{}，返回值：{}", JsonHelper.toJson(idcrRequestDto), JsonHelper.toJson(idcrResponseDto));
            }
            return idcrResponseDto;
        } catch (RuntimeException e) {
            Profiler.functionError(callerInfo);
            log.error("调用AI识别身份证信息异常，请求参数：{}", JsonHelper.toJson(idcrRequestDto), e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }

    }

}
