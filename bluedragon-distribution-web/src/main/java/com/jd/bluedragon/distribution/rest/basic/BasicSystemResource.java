package com.jd.bluedragon.distribution.rest.basic;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BaseSystemRequest;
import com.jd.bluedragon.distribution.api.response.BaseSystemResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: BasicSystemResource
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/1/7 21:18
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class BasicSystemResource {

    private Logger log = LoggerFactory.getLogger(BasicSystemResource.class);

    private static final String DEFAULTTIME = "BasicSystemResource.defaultTime";

    @Autowired
    private BaseMajorManager baseMajorManager;

    @POST
    @Path("/getSystemMessage")
    @JProfiler(jKey = "DMS.WEB.BasicSystemResource.getSystemMessage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<BaseSystemResponse> getSystemMessage(BaseSystemRequest request){

        InvokeResult result = new InvokeResult();
        BaseSystemResponse baseResponse = new BaseSystemResponse();
        Date systemTime = new Date();
        baseResponse.setSystemTime(new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(systemTime));
        result.setCode(JdResponse.CODE_OK);
        result.setMessage(JdResponse.MESSAGE_OK);
        result.setData(baseResponse);
        if(request.getOperateTime() == null || StringHelper.isEmpty(request.getPdaVersion()) ||
                StringHelper.isEmpty(request.getErpCode()) ){
            result.setCode(JdResponse.CODE_PARAM_ERROR);
            result.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return result;
        }
        try{
            if(!request.getErpCode().toLowerCase().contains(Constants.PDA_THIRDPL_TYPE)){
                BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseStaffIgnoreIsResignByErp(request.getErpCode());
                if(baseDto == null || baseDto.getIsResign() == null || baseDto.getIsResign() != 1){
                    result.setCode(JdResponse.CODE_RESIGNATION);
                    result.setMessage(JdResponse.MESSAGE_RESIGNATION);
                    return result;
                }
            }
            Date pdaTime = new SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(request.getOperateTime());
            if(diffTime(pdaTime,systemTime)){
                result.setCode(JdResponse.CODE_TIMEOUT);
                result.setMessage(JdResponse.MESSAGE_TIMEOUT + "[PDA时间:" + request.getOperateTime() + ",服务器时间:" + DateHelper.formatDate(systemTime, DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2) + "]");
                return result;
            }

        }catch (Exception e){
            this.log.error("通过{}查询基础资料失败!", request.getErpCode(), e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return result;
    }

    /**
     * 比较客户端时间和服务器时间是否相差5分钟以上
     * @param operateTime
     * @param systemTime
     * @return
     */
    public boolean diffTime(Date operateTime, Date systemTime){
        long diff = Math.abs(operateTime.getTime() - systemTime.getTime());
        String defaultTime = PropertiesHelper.newInstance().getValue(DEFAULTTIME);
        if(diff/(1000*60) >= Integer.valueOf(defaultTime)){
            return true;
        }
        return false;
    }
}
