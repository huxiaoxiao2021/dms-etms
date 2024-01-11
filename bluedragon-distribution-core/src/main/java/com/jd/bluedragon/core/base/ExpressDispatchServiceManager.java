package com.jd.bluedragon.core.base;


import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.preseparate.vo.B2bVehicleTeamMatchRequest;
import com.jd.preseparate.vo.B2bVehicleTeamMatchResult;
import com.jd.preseparate.vo.ServiceResponse;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 16:24
 * @Description:
 */
public interface ExpressDispatchServiceManager {


    B2bVehicleTeamMatchResult getStandardB2bSupportMatch(B2bVehicleTeamMatchRequest request);


    /**
     * 地址匹配
     * @param request
     * @return
     */
    JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request);
}
