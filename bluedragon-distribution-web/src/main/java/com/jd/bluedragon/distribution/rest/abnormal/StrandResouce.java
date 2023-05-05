package com.jd.bluedragon.distribution.rest.abnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 包裹滞留
 * @date 2020/3/10.
 * @author jinjingcheng
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class StrandResouce {

    @Autowired
    private StrandService strandService;
    
    /**
     * 包裹滞留上报
     * @param request
     * @return
     */
    @POST
    @Path("strand/report")
    @JProfiler(jKey = "DMS.WEB.StrandResouce.report", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> report(StrandReportRequest request){
        return strandService.report(request);
    }

    /**
     * 查询原因列表
     * 查询默认
     * @param
     * @return
     */
    @POST
    @Path("strand/queryReasonList")
    public InvokeResult<List<ConfigStrandReasonData>> queryReasonList(){
        return strandService.queryReasonList();
    }

    /**
     * 查询原因列表
     * 默认 + 冷链
     * @return
     */
    @POST
    @Path("strand/queryAllReasonList")
    public InvokeResult<List<ConfigStrandReasonData>> queryAllReasonList(){
        return strandService.queryAllReasonList();
    }

}
