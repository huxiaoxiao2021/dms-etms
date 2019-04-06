package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;

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

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 冷链发货接口
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    @POST
    @Path("/delivery/coldChain/getTransPlan")
    public DeliveryResponse getTransPlan(Integer createSiteCode, Integer receiveSiteCode) {
        if (createSiteCode != null && receiveSiteCode != null) {
            Date beginPlanDepartTime = this.getCurrentDateTimeByParam(0, 0, 0);
            Date endPlanDepartTime = this.getCurrentDateTimeByParam(23, 59, 59);
            BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
            BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
            if (createSiteDto != null && receiveSiteDto != null) {
                String beginNodeCode = createSiteDto.getDmsSiteCode();
                String endNodeCode = receiveSiteDto.getDmsSiteCode();
            }
        } else {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }
    }

    /**
     * 根据时分秒获取当天的时间
     *
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    private Date getCurrentDateTimeByParam(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

}
