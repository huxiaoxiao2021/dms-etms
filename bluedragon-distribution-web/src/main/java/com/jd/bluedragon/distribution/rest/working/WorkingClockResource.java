package com.jd.bluedragon.distribution.rest.working;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.working.WorkingClockRecordVo;
import com.jd.bluedragon.common.dto.working.WorkingClockRequest;
import com.jd.bluedragon.common.dto.working.WorkingPage;
import com.jd.bluedragon.external.gateway.service.WorkingClockService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WorkingClockResource {

    @Autowired
    private WorkingClockService workingClockService;

    /**
     * PDA 查询在本小组签到的记录（含支援人员在本组的签到）
     */
    @POST
    @Path("/workingClock/pdaLocalClockRecord")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.pdaLocalClockRecord", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<WorkingPage<WorkingClockRecordVo>> pdaLocalClockRecord(WorkingClockRequest dto) {
        return workingClockService.pdaLocalClockRecord(dto);
    }

    /**
     * PDA 查询在本小组组员的签到记录（含本组组员支援其他组的签到记录）
     */
    @POST
    @Path("/workingClock/pdaLocalClockRecord")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.pdaLocalClockRecord", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<WorkingPage<WorkingClockRecordVo>> pdaGroupMembersClockRecord(WorkingClockRequest dto) {
        return workingClockService.pdaGroupMembersClockRecord(dto);
    }


    /**
     * PDA 打卡
     */
    @POST
    @Path("/workingClock/clock")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.clock", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> clock(WorkingClockRequest dto){
        return workingClockService.clock(dto);
    }

    /**
     * 作废打卡记录
     */
    @POST
    @Path("/workingClock/disabledClock")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.disabledClock", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> disabledClock(WorkingClockRequest dto) {
        return workingClockService.disabledClock(dto);
    }

    /**
     * 更新打卡时间
     */
    @POST
    @Path("/workingClock/updateClockTime")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.updateClockTime", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> updateClockTime(WorkingClockRequest dto) {
        return workingClockService.updateClockTime(dto);
    }

    /**
     * 小组工时确认
     */
    @POST
    @Path("/workingClock/confirmClockHours")
    @JProfiler(jKey = "DMS.WEB.WorkingClockResource.confirmClockHours", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> confirmClockHours(WorkingClockRequest dto){
        return workingClockService.confirmClockHours(dto);
    }
}