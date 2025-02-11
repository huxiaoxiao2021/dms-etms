package com.jd.bluedragon.distribution.rest.siteRetake;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeCondition;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeOperation;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.orm.page.Page;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂批量再取
 * @date 2018年08月02日 14时:53分
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class SiteRetakeResource {
    private static final Log logger = LogFactory.getLog(SiteRetakeResource.class);
    @Autowired
    private SiteRetakeService siteRetakeService;

    @GET
    @Path("/siteRetake/queryBasicTraderInfoByKey/{key}")
    @JProfiler(jKey = "DMS.WEB.SiteRetakeResource.queryBasicTraderInfoByKey", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(@PathParam("key") String key) {
        List<BasicTraderQueryDTO> result = Lists.newArrayList();
        if (StringHelper.isEmpty(key)) {
            return result;
        }
        List<BasicTraderQueryDTO> basicTraderQueryDTOS = siteRetakeService.queryBasicTraderInfoByKey(key);
        if (basicTraderQueryDTOS != null && basicTraderQueryDTOS.size() > 0) {
            for (BasicTraderQueryDTO dto : basicTraderQueryDTOS) {
                dto.setTraderName(dto.getTraderName() + Constants.PUNCTUATION_OPEN_BRACKET_SMALL + dto.getTraderCode() + Constants.PUNCTUATION_CLOSE_BRACKET_SMALL);
                result.add(dto);
            }
        }

        return result;
    }

    @POST
    @Path("/siteRetake/queryWaybillCode")
    @JProfiler(jKey = "DMS.WEB.SiteRetakeResource.queryWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Page<VendorOrder> queryWaybillCode(SiteRetakeCondition siteRetakeCondition) {
        Assert.notNull(siteRetakeCondition, "siteRetakeCondition must not be null");
        Assert.notNull(siteRetakeCondition.getSiteCode(), "sitecode type must not be null");

        Page page = new Page();
        page.setCurrentPage(siteRetakeCondition.getCurrentPage());
        page.setPageSize(siteRetakeCondition.getPageSize());
        return siteRetakeService.queryVendorOrderList(siteRetakeCondition, page);
    }

    @POST
    @Path("/siteRetake/updateOrderStatus")
    @JProfiler(jKey = "DMS.WEB.SiteRetakeResource.updateOrderStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1013, operateType = 101301)
    public JdResponse<String> updateOrderStatus(SiteRetakeOperation siteRetakeOperation) {
        Assert.notNull(siteRetakeOperation, "siteRetakeOperation must not be null");
        Assert.notNull(siteRetakeOperation.getSiteCode(), "sitecode type must not be null");
        Assert.notNull(siteRetakeOperation.getWaybillCode(), "waybillcode type must not be null");
        Assert.notNull(siteRetakeOperation.getStatus(), "status type must not be null");
        Assert.notNull(siteRetakeOperation.getOperatorId(), "operator type must not be null");
        Assert.notNull(siteRetakeOperation.getEndReason(), "endreson type must not be null");
        Assert.notNull(siteRetakeOperation.getOperatorTime(), "operatortime type must not be null");
        return siteRetakeService.updateCommonOrderStatus(siteRetakeOperation);

    }
}
