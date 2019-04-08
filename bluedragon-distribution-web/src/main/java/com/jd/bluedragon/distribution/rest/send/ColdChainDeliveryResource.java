package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ColdChainSendResponse;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainDeliveryResource
 * @date 2019/4/6
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ColdChainDeliveryResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ColdChainSendService coldChainSendService;

    /**
     * 冷链发货接口
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    @POST
    @Path("/delivery/coldChain/getTransPlan")
    public ColdChainSendResponse<List<TransPlanDetailResult>> getTransPlan(Integer createSiteCode, Integer receiveSiteCode) {
        if (createSiteCode != null && receiveSiteCode != null) {
            try {
                List<TransPlanDetailResult> resultList = coldChainSendService.getTransPlanDetail(createSiteCode, receiveSiteCode);
                if (resultList != null) {
                    return new ColdChainSendResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, resultList);
                } else {
                    return new ColdChainSendResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } catch (Exception e) {
                logger.error("[冷链发货]根据始发分拣中心和目的分拣中心编号获取当日的运输计划明细信息时发生异常", e);
                return new ColdChainSendResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        return new ColdChainSendResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

}
