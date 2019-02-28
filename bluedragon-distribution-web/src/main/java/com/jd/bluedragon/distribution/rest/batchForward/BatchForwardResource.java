package com.jd.bluedragon.distribution.rest.batchForward;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by hujiping on 2018/7/31.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BatchForwardResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BatchForwardService batchForwardService;
    @Autowired
    private SiteService siteService;


    @GET
    @Path("/batchForward/checkSendCode/{sendCode}/{flage}")
    public InvokeResult<CreateAndReceiveSiteInfo> checkSendCode(
            @PathParam("sendCode") String sendCode,@PathParam("flage") Integer flage){

        InvokeResult<CreateAndReceiveSiteInfo> result = new InvokeResult<CreateAndReceiveSiteInfo>();
        //验证sendCode
        if(StringHelper.isEmpty(sendCode)){
            logger.error("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
            result.error("根据批次号获取始发和目的分拣信息失败，参数批次号为空");
            return result;
        }
        //查询批次下是否有包裹和箱
        Boolean isHaveBox = batchForwardService.isHaveBox(sendCode);
        if(flage == 1 && isHaveBox){
            result.error("新批次下有箱或包裹，请换批次");
            return result;
        }
        try{
            //解析批次号，获取始发分拣中心id和目的分拣中心id，0是始发，1是目的
            Integer[] siteCodes = this.siteService.getSiteCodeBySendCode(sendCode);
            if (siteCodes[0] == -1 || siteCodes[1] == -1) {
                logger.error("根据批次号获取始发和目的分拣信息失败，批次号:" + sendCode + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
                result.error("根据批次号获取始发和目的分拣信息失败，批次号：" + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
                return result;
            }

            //根据站点id获取站点信息，并将始发站点信息和目的站点信息映射到CreateAndReceiveSiteInfo对象中
            CreateAndReceiveSiteInfo createAndReceiveSite = new CreateAndReceiveSiteInfo();
            BaseStaffSiteOrgDto createSite = siteService.getSite(siteCodes[0]);
            BaseStaffSiteOrgDto receiveSite = siteService.getSite(siteCodes[1]);

            //始发站点信息的映射
            if(createSite != null){
                createAndReceiveSite.setCreateSiteCode(createSite.getSiteCode());
                createAndReceiveSite.setCreateSiteName(createSite.getSiteName());
                createAndReceiveSite.setCreateSiteType(createSite.getSiteType());
                createAndReceiveSite.setCreateSiteSubType(createSite.getSubType());
            }

            //目的站点信息的映射
            if(receiveSite != null){
                createAndReceiveSite.setReceiveSiteCode(receiveSite.getSiteCode());
                createAndReceiveSite.setReceiveSiteName(receiveSite.getSiteName());
                createAndReceiveSite.setReceiveSiteType(receiveSite.getSiteType());
                createAndReceiveSite.setReceiveSiteSubType(receiveSite.getSubType());
            }

            result.setMessage("success");
            result.setData(createAndReceiveSite);
        }catch (Exception e){
            logger.error("根据批次号获取始发和目的分拣信息失败，批次号：" + sendCode);
            result.error("根据批次号获取始发和目的分拣信息出现异常，请联系孔春飞");
        }

        return result;
    }

    /**
     * 批次号整批转发
     * @param request
     * @return
     */
    @POST
    @Path("/batchForward/batchForwardSend")
    public InvokeResult batchForwardSend(BatchForwardRequest request){
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(request));
        }
        InvokeResult result = new InvokeResult();
        try{

            result = batchForwardService.batchSend(request);
        }catch (Exception e){
            result.error(e);
            this.logger.error("整批转发",e);
        }
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(result));
        }
        return result;
    }
}
