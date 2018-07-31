package com.jd.bluedragon.distribution.rest.batchForward;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.batchForward.SendCodeInfo;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jra.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
    private BaseService baseService;


    /**
     * 通过批次号获得始发站点和目的站点
     * @param sendCode
     * @return
     */
    @GET
    @Path("/batchForward/getCreateSiteAndReceiveSite")
    public InvokeResult<SendCodeInfo> getCreateSiteAndReceiveSite(String sendCode){
        this.logger.info("批次号"+ sendCode + "不能为空");
        InvokeResult<SendCodeInfo> result = new InvokeResult<SendCodeInfo>();
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        BaseStaffSiteOrgDto createSite = null;
        BaseStaffSiteOrgDto receiveSite = null;
        try{
            if(createSiteCode != null && receiveSiteCode != null){
                createSite = baseService.queryDmsBaseSiteByCode(createSiteCode.toString());
                receiveSite = baseService.queryDmsBaseSiteByCode(receiveSiteCode.toString());
                if(createSite != null && receiveSite != null){
                    SendCodeInfo sendCodeInfo = new SendCodeInfo();
                    sendCodeInfo.setCreateSiteCode(createSite.getSiteCode());
                    sendCodeInfo.setCreateSiteName(createSite.getSiteName());
                    sendCodeInfo.setReceiveSiteCode(receiveSite.getSiteCode());
                    sendCodeInfo.setCreateSiteName(receiveSite.getSiteName());
                    result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                    result.setData(sendCodeInfo);
                    return result;
                }
                result.customMessage(400,"不存在该站点");
                return result;
            }
            result.setCode(InvokeResult.RESULT_NULL_CODE);
            result.setMessage(InvokeResult.RESULT_NULL_MESSAGE);
        }catch (Exception e){
            this.logger.error("通过站点id获得站点信息失败",e);
            result.error(e);
            return result;
        }
        return result;
    }

    /**
     * 批次号整批转发
     * @param sendCode
     */
    @Post
    @Path("/batchForward/batchForwardSend")
    public void batchForwardSend(){

    }
}
