package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.preseparate.jsf.ExpressDispatchServiceAPI;
import com.jd.bluedragon.preseparate.jsf.StationMatchServiceApi;
import com.jd.preseparate.vo.B2bVehicleTeamMatchRequest;
import com.jd.preseparate.vo.B2bVehicleTeamMatchResult;
import com.jd.preseparate.vo.ServiceResponse;
import com.jd.preseparate.vo.stationmatch.StationMatchParameter;
import com.jd.preseparate.vo.stationmatch.StationMatchResult;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 16:25
 * @Description:
 */
@Service("expressDispatchServiceManager")
public class ExpressDispatchServiceManagerImpl implements ExpressDispatchServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ExpressDispatchServiceManagerImpl.class);

    @Value("${preseparate.config.systemCode}")
    private String systemCode;

    @Autowired
    StationMatchServiceApi stationMatchServiceApi;



    @Value("${jsf.preseparate.stationMatchServiceApi.ownerErp:xnpyddxm}")
    private String ownerErp;

    private static Integer SUC_CODE = 200;


    @Autowired
    private ExpressDispatchServiceAPI expressDispatchServiceAPI;

    @Override
    public B2bVehicleTeamMatchResult getStandardB2bSupportMatch(B2bVehicleTeamMatchRequest request) {

        CallerInfo callerInfo = ProfilerHelper.registerInfo("DMSWEB.ExpressDispatchServiceManagerImpl.getStandardB2bSupportMatch");

        try{
            //todo   补充其他必填字段
            request.setSystemCode(systemCode);
            request.setOrderBusinessType(Constants.B2BSUPPORT_ORDER_BUSINESS_TYPE);
            request.setIndustryType(Constants.B2BSUPPORT_INDUSTRY_TYPE);
            request.setVendorId(Constants.B2BSUPPORT_VENDOR_ID);
            request.setRequireTransMode(Constants.B2BSUPPORT_REQUIRE_TRANS_MODE);
            request.setColdChain(Constants.B2BSUPPORT_COLD_CHAIN);
            if(logger.isInfoEnabled()){
                logger.info("根据地址获取匹配站点信息入参-{}", JSON.toJSONString(request));
            }
            ServiceResponse<B2bVehicleTeamMatchResult> response = expressDispatchServiceAPI.getStandardB2bSupportMatch(request);
            if(logger.isInfoEnabled()){
                logger.info("根据地址获取匹配站点信息出参-{}",JSON.toJSONString(response));
            }
            if(ServiceResponse.CODE_OK_STRING.equals("200") && response.getData() != null){
                return response.getData();
            }
        }catch (Exception e){
            logger.error("根据地址获取匹配站点信息异常!入参-{},异常信息-{}",request,e.getMessage(),e);
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;




    }


    @Override
    public JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request) {
        JdResult<StationMatchResponse> result = new JdResult<StationMatchResponse>();
        result.toSuccess("匹配成功！");
        StationMatchParameter paramData = new StationMatchParameter();
        paramData.setRequestId(UUID.randomUUID().toString());
        paramData.setSystemCode(systemCode);
        paramData.setAddress(request.getAddress());

        ServiceResponse<StationMatchResult> apiResult = null;
        try {
            apiResult = stationMatchServiceApi.stationMatchByAddress(paramData);
        } catch (Exception e) {
            logger.error("调用预分拣-地址匹配服务异常，请联系配运调度小秘（xnpyddxm）",e);
            result.toFail("地址匹配服务异常，请联系配运调度小秘（"+ownerErp+"）");
            return result;
        }
        if(apiResult != null
                && SUC_CODE.equals(apiResult.getCode())
                && apiResult.getData() != null) {
            StationMatchResponse data = new StationMatchResponse();
            data.setSiteCode(apiResult.getData().getStationId());
            data.setSiteName(apiResult.getData().getStationName());
            result.setData(data);
        }else {
            result.toFail("未找到预分拣站点，请确认地址准确性");
        }
        return result;
    }

}
