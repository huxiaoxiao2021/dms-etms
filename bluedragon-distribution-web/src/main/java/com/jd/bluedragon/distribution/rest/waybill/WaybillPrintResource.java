package com.jd.bluedragon.distribution.rest.waybill;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.InvoiceParameters;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.service.InvoicePrintService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;

/**
 * 根据运单打印相关RESTful接口
 * Created by wangtingwei on 2016/4/8.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WaybillPrintResource {

    private static final Log logger= LogFactory.getLog(WaybillPrintResource.class);


    @Autowired
    private WaybillPrintService waybillPrintService;

    @Autowired
    @Qualifier("invoicePrintService")
    private InvoicePrintService invoicePrintService;

    /**
     * 运单包裹标签打印
     * @param dmsCode           始发分拣中心
     * @param waybillCode       运单号
     * @param targetSiteCode    目的站点【>0时为反调度站点】
     * @return
     */
    @GET
    @GZIP
    @Path("/waybill/getPrintWaybill/{dmsCode}/{waybillCode}/{targetSiteCode}")
    public InvokeResult<WaybillPrintResponse> getPrintWaybill(@PathParam("dmsCode") Integer dmsCode,
                                                      @PathParam("waybillCode")String waybillCode,
                                                      @PathParam("targetSiteCode")Integer targetSiteCode){
        //return printService.
        return waybillPrintService.getPrintWaybill(dmsCode,waybillCode,targetSiteCode);
    }

    /**
     * 生成发票
     * @param parameters 发票请求五常委
     * @return
     */
    @POST
    @GZIP
    @Path("/waybill/generateInvoice")
    public InvokeResult<String> generateInvoice(InvoiceParameters parameters){
        return invoicePrintService.generateInvoice(parameters);

    }
}
