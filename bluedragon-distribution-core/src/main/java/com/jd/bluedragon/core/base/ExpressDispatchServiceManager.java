package com.jd.bluedragon.core.base;


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
}
