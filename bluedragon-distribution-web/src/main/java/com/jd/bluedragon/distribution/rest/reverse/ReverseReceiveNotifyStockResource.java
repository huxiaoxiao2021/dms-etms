package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReverseReceiveNotifyStockResource {

	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
	
	@Autowired
	private StockExportManager stockExportManager;

    @Autowired
    private ChuguanExportManager chuguanExportManager;

	@GET
	@Path("/reverse/pop/nodify/{waybillCode}")
	public String sendMessageToPop(@PathParam("waybillCode") String waybillCode)
			throws Exception {
		boolean result = this.reverseSendPopMessageService.sendPopMessage(waybillCode);
		return result?JdResponse.MESSAGE_OK:"NOT"+JdResponse.MESSAGE_OK;
	}


    @GET
    @Path("/reverseReceiveNotifyStock/getChuGuanInfo/{waybillCode}")
    public String getChuGuanInfo(@PathParam("waybillCode") String waybillCode)
            throws Exception {
        KuGuanDomain newKu = chuguanExportManager.queryByWaybillCode(waybillCode);
        KuGuanDomain oldKu = stockExportManager.queryByWaybillCode(waybillCode);
	    StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("new[").append(JsonHelper.toJson(newKu)).append("]");
        stringBuilder.append("old[").append(JsonHelper.toJson(oldKu)).append("]");
        return stringBuilder.toString();
    }

}
