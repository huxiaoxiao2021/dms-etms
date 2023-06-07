package com.jd.bluedragon.distribution.logTrack;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.LogDto;

import java.util.List;

/**
 * @author liwenji
 * @description 日志追踪服务
 * @date 2023-06-07 14:41
 */
public interface LogTrackService {

    /**
     * 打印校验滑道笼车信息
     * @param printRequest
     * @return
     */
    JdResult<List<LogDto>> checkPrintCrossTableTrolley(String printRequest);
}
