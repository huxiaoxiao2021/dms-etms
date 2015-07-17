package com.jd.bluedragon.distribution.rest.sendprint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BasicQueryEntityResponse;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;

import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SendPrintResource {

	@Autowired
	private SendPrintService sendPrintService;

	@POST
	@GZIP
	@Path("/sendprint/batchSummaryPrint")
	public SummaryPrintResultResponse batchSummaryPrint(PrintQueryCriteria criteria) {
		if(check(criteria)){
			SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();
			tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tSummaryPrintResultResponse.setMessage("查询参数不全");
            tSummaryPrintResultResponse.setData(null);
            return tSummaryPrintResultResponse;
		}
		return sendPrintService.batchSummaryPrintQuery(criteria);
	}

	@POST
	@GZIP
	@Path("/sendprint/basicPrintQuery")
	public BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria) {
		if(check(criteria)){
			BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
			tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
			tBasicQueryEntityResponse.setMessage("查询参数不全");
			tBasicQueryEntityResponse.setData(null);
            return tBasicQueryEntityResponse;
		}
		return sendPrintService.basicPrintQuery(criteria);
	}


	@POST
	@GZIP
	@Path("/sendprint/sopPrintQuery")
	public BasicQueryEntityResponse sopPrintQuery(PrintQueryCriteria criteria) {
		if(check(criteria)){
			BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
			tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
			tBasicQueryEntityResponse.setMessage("查询参数不全");
			tBasicQueryEntityResponse.setData(null);
            return tBasicQueryEntityResponse;
		}
		return sendPrintService.sopPrintQuery(criteria);
	}

	private boolean check(PrintQueryCriteria criteria) {
        if(criteria!=null &&criteria.getSiteCode()!=null && criteria.getReceiveSiteCode()!=null
        			&& criteria.getStartTime()!=null && criteria.getEndTime()!=null){
        	return false;
        }
       return true;
   }

	@POST
	@GZIP
	@Path("/sendprint/carrySendCarInfo")
	public BatchSendInfoResponse batchSendInfoPrint(List<BatchSend> batchSends) {

		if(batchSends==null||batchSends.size()==0){
			BatchSendInfoResponse tSummaryPrintResultResponse = new BatchSendInfoResponse();
			tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
			tSummaryPrintResultResponse.setMessage("查询参数不全");
			tSummaryPrintResultResponse.setData(null);
			return tSummaryPrintResultResponse;
		}
		for(int i=0;i<batchSends.size();i++){
			if (batchSends.get(i).getSendCode() == null || batchSends.get(i).getSendCode().isEmpty()){
				BatchSendInfoResponse tSummaryPrintResultResponse = new BatchSendInfoResponse();
				tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
				tSummaryPrintResultResponse.setMessage("查询参数不全");
				tSummaryPrintResultResponse.setData(null);
				return tSummaryPrintResultResponse;
			}
		}
		return sendPrintService.selectBoxBySendCode(batchSends);
	}
}
