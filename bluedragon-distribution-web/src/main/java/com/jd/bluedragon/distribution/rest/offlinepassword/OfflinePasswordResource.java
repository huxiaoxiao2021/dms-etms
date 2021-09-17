package com.jd.bluedragon.distribution.rest.offlinepassword;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.OfflinePasswordPrimeResponse;
import com.jd.dms.wb.sdk.api.IOfflinePasswordJsfService;
import com.jd.dms.wb.sdk.model.base.BaseEntity;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static com.jd.bluedragon.distribution.api.JdResponse.CODE_SITE_CODE_IS_NULL;
import static com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_SITE_CODE_IS_NULL;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OfflinePasswordResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("jsfOfflinePasswordService")
    IOfflinePasswordJsfService jsfOfflinePasswordService;

    @GET
    @Path("/offlinePassword/updatePrime")
    public OfflinePasswordPrimeResponse queryAbnormalorder(@QueryParam("siteCode")Integer siteCode){
        CallerInfo info = null;
        OfflinePasswordPrimeResponse response = new OfflinePasswordPrimeResponse();
        if(siteCode == null){
            response.setCode(CODE_SITE_CODE_IS_NULL);
            response.setMessage(MESSAGE_SITE_CODE_IS_NULL);
            return response;
        }
        try {
            info = Profiler.registerInfo( "DMS.WEB.OfflinePasswordResource.updatePrime",false, true);
            BaseEntity<Integer> baseEntity =  jsfOfflinePasswordService.updatePrime(siteCode);
            if(baseEntity.isSuccess()){
                response.setCode(JdResponse.CODE_OK);
                response.setPrime(baseEntity.getData());
                if(baseEntity.getData() != null){
                    log.info("场地id:{}，更新加密因子成功{}", siteCode, baseEntity.getData());
                }else {
                    log.info("场地id:{}，请求成功，本次不用更新加密因子", siteCode);
                }
                return response;
            }
            response.setCode(baseEntity.getCode());
            response.setMessage("更新加密因子失败");
            log.warn("场地id:{}，更新加密因子失败:{},code:{}", siteCode, response.getMessage(), baseEntity.getMessage());
            return response;
        } catch (Exception e) {
            response.setCode(500);
            log.error("场地id:{}，更新加密因子失败异常:{}",siteCode, e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return response;

    }
}
