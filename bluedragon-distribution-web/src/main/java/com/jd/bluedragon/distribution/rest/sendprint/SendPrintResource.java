package com.jd.bluedragon.distribution.rest.sendprint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.sendprint.domain.*;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jsf.gd.msg.ResponseFuture;
import com.jd.jsf.gd.util.RpcContext;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SendPrintResource {

    private static final Logger log = LoggerFactory.getLogger(SendPrintResource.class);
	@Autowired
	private SendPrintService sendPrintService;

    @Autowired
    WaybillQueryManager waybillQueryManager;

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

    @GET
    @Path("/sendprint/testasync")
    public Map<String,BigWaybillDto> getBatchWaybill(){
        WChoice queryWChoice = new WChoice();
        queryWChoice.setQueryWaybillC(true);
        queryWChoice.setQueryWaybillE(false);
        queryWChoice.setQueryWaybillM(false);
        queryWChoice.setQueryPackList(false);
        List<String> parameters=new ArrayList<String>(145);

        parameters.add("12338439357");
        parameters.add("13223525237");
        parameters.add("15576495839");
        parameters.add("15833255593");
        parameters.add("16516246616");
        parameters.add("17195339256");
        parameters.add("19451566843");
        parameters.add("32891486827");
        parameters.add("35671725429");
        parameters.add("35776222111");
        parameters.add("36393359173");
        parameters.add("42747186626");
        parameters.add("46491975395");
        parameters.add("46666704304");
        parameters.add("46666705500");
        parameters.add("46666707887");
        parameters.add("46666709744");
        parameters.add("46666710068");
        parameters.add("46666710214");
        parameters.add("46666710242");
        parameters.add("46666710281");
        parameters.add("46666710297");
        parameters.add("46666710311");
        parameters.add("46666710353");
        parameters.add("46666710394");
        parameters.add("46666715273");
        parameters.add("46666715735");
        parameters.add("46666721411");
        parameters.add("46666721419");
        parameters.add("46666721423");
        parameters.add("46666721484");
        parameters.add("46666722972");
        parameters.add("46666723032");
        parameters.add("46666728172");
        parameters.add("46666730576");
        parameters.add("46666733038");
        parameters.add("46666743889");
        parameters.add("46666743890");
        parameters.add("46666743893");
        parameters.add("46666744320");
        parameters.add("46666744321");
        parameters.add("46666744322");
        parameters.add("46666744323");
        parameters.add("46666744324");
        parameters.add("46666744460");
        parameters.add("46666744462");
        parameters.add("46666744464");
        parameters.add("46666744465");
        parameters.add("46666744466");
        parameters.add("46666744586");
        parameters.add("46666744587");
        parameters.add("46666744588");
        parameters.add("46666744589");
        parameters.add("46666744592");
        parameters.add("46666744710");
        parameters.add("46666744714");
        parameters.add("46666745760");
        parameters.add("46666745769");
        parameters.add("46666745770");
        parameters.add("46666745771");
        parameters.add("46666745772");
        parameters.add("46666746656");
        parameters.add("46666746657");
        parameters.add("46666746659");
        parameters.add("46666746660");
        parameters.add("46666746661");
        parameters.add("46666746662");
        parameters.add("46666746896");
        parameters.add("46666746901");
        parameters.add("46666746902");
        parameters.add("46666746957");
        parameters.add("46666746959");
        parameters.add("46666746960");
        parameters.add("46666746961");
        parameters.add("46666746962");
        parameters.add("46666747453");
        parameters.add("46666747455");
        parameters.add("46666747456");
        parameters.add("46666747457");
        parameters.add("46666747497");
        parameters.add("46666747498");
        parameters.add("46666747500");
        parameters.add("46666747511");
        parameters.add("46666747512");
        parameters.add("46666747571");
        parameters.add("46666747572");
        parameters.add("46666747574");
        parameters.add("46666747576");
        parameters.add("46666747578");
        parameters.add("46666747669");
        parameters.add("46666747670");
        parameters.add("46666747672");
        parameters.add("46666747695");
        parameters.add("46666747701");
        parameters.add("46666748363");
        parameters.add("46666748985");
        parameters.add("46666748993");
        parameters.add("46666748994");
        parameters.add("46666748996");
        parameters.add("46666749038");
        parameters.add("46666749049");
        parameters.add("46666760296");
        parameters.add("46666760297");
        parameters.add("46666760812");
        parameters.add("46666760813");
        parameters.add("46666760814");
        parameters.add("46666760815");
        parameters.add("49425232721");
        parameters.add("51294387587");
        parameters.add("54875361424");
        parameters.add("55823499915");
        parameters.add("59377424883");
        parameters.add("66823316896");
        parameters.add("66916885646");
        parameters.add("76541118738");
        parameters.add("79712322668");
        parameters.add("82519372473");
        parameters.add("87555787933");
        parameters.add("T46666707887");
        parameters.add("T46666709554");
        parameters.add("T46666709757");
        parameters.add("T46666710394");
        parameters.add("T46666710701");
        parameters.add("T46666715735");
        parameters.add("T46666760295");
        parameters.add("VA00010402696");
        parameters.add("VA00010404892");
        parameters.add("VA00012414393");
        parameters.add("VA00012415911");
        parameters.add("VA00015020666");
        parameters.add("VA00015497344");
        parameters.add("VB00010891296");
        parameters.add("VB00012495475");
        parameters.add("VY00010477935");
        parameters.add("VY00010479291");
        parameters.add("VY00010892173");
        parameters.add("WA803181096278704128");
        parameters.add("WA803438798368161792");
        parameters.add("WA814409699876159488");
        parameters.add("WA817226720473006080");
        parameters.add("WA818360735003328512");
        parameters.add("WA818365680372957184");
        parameters.add("WA818651614096146432");
        parameters.add("11787194861");
        parameters.add("15577272975");


        long start=System.currentTimeMillis();
        Map<String,BigWaybillDto> result=new HashMap<String,BigWaybillDto>(145);
        List<ResponseFuture<BaseEntity<BigWaybillDto>>> middles=new ArrayList<ResponseFuture<BaseEntity<BigWaybillDto>>>(145);
        for (String parameter:parameters) {
            //parameters.add(String.valueOf(index));
            waybillQueryManager.getDataByChoice(parameter,queryWChoice);
            ResponseFuture<BaseEntity<BigWaybillDto>> responseFuture= RpcContext.getContext().getFuture();
            middles.add(responseFuture);
        }
        int index=0;
        int errorNumber=0;
        long maxTime=0;
        for (ResponseFuture<BaseEntity<BigWaybillDto>> future:middles){
            BaseEntity<BigWaybillDto> baseEntity=null;
            long item=System.currentTimeMillis();
            try {
                baseEntity=future.get();
                if(null!=baseEntity){
                    result.put(parameters.get(index),baseEntity.getData());
                }
            }catch (Throwable throwable){
                log.error(throwable.getMessage(),throwable);
                result.put(parameters.get(index),null);
                errorNumber++;
            }
            item=System.currentTimeMillis()-item;
            if(item>maxTime){
                maxTime=item;
            }
            ++index;
        }
        result.put("错误数"+String.valueOf(errorNumber),null);
        long span=System.currentTimeMillis()-start;
        result.put("总时长"+String.valueOf(span),null);
        result.put("最大值"+String.valueOf(maxTime),null);
        return result;
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
    @Path("/sendprint/basicPrintQueryForPage")
    public BasicQueryEntityResponse basicPrintQueryForPage(PrintQueryCriteria criteria) {
        if (checkForPage(criteria)) {
            BasicQueryEntityResponse response = new BasicQueryEntityResponse();
            response.setCode(JdResponse.CODE_NOT_FOUND);
            response.setMessage("查询参数不全");
            return response;
        }
        return sendPrintService.basicPrintQueryForPage(criteria);
    }
	
	@POST
	@GZIP
	@Path("/sendprint/basicPrintQueryOffline")
	public BasicQueryEntityResponse basicPrintQueryOffline(PrintQueryCriteria criteria) {
		if(check(criteria)){
			BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
			tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
			tBasicQueryEntityResponse.setMessage("查询参数不全");
			tBasicQueryEntityResponse.setData(null);
            return tBasicQueryEntityResponse;
		}
		return sendPrintService.basicPrintQueryOffline(criteria);
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

    /**
     * 参数检查 true - 未通过；false - 通过
     *
     * @param criteria
     * @return
     */
    private boolean check(PrintQueryCriteria criteria) {
        if (criteria != null && criteria.getSiteCode() != null && criteria.getReceiveSiteCode() != null
                && criteria.getStartTime() != null && criteria.getEndTime() != null) {
            return false;
        }
        return true;
    }

    /**
     * 分页参数检查 true - 未通过；false - 通过
     *
     * @param criteria
     * @return
     */
    private boolean checkForPage(PrintQueryCriteria criteria) {
	    if (this.check(criteria)) {
	        return true;
        }
        if (criteria.getPageNo() != null && criteria.getPageSize() != null) {
            return false;
        }
        return true;
    }

	@POST
	@GZIP
	@Path("/sendprint/carrySendCarInfo")
	public BatchSendInfoResponse carrySendCarInfo(List<BatchSend> batchSends) {

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

    /**
     * 获取批次号打印时需要信息
     *
     * @param criteria
     * @return
     */
    @POST
    @GZIP
    @Path("/sendprint/getSendCodePrintInfo")
    public InvokeResult<SendCodePrintEntity> getSendCodePrintInfo(PrintQueryCriteria criteria) {
        InvokeResult<SendCodePrintEntity> result = new InvokeResult<>();
        if (criteria != null && criteria.getSiteCode() != null && criteria.getReceiveSiteCode() != null) {
            SendCodePrintEntity entity = sendPrintService.getSendCodePrintEntity(criteria);
            result.setData(entity);
        } else {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
        }
        return result;
    }

    @POST
    @GZIP
    @Path("/sendprint/newBasicPrintQuery")
    public BasicQueryEntityResponse newBasicPrintQuery(PrintQueryCriteria criteria) {
        if(check(criteria)){
            BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("查询参数不全");
            tBasicQueryEntityResponse.setData(null);
            return tBasicQueryEntityResponse;
        }
        return sendPrintService.newBasicPrintQuery(criteria);
    }


    @POST
    @GZIP
    @Path("/sendprint/newBatchSummaryPrint")
    public SummaryPrintResultResponse newBatchSummaryPrint(PrintQueryCriteria criteria) {
        if(check(criteria)){
            SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();
            tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tSummaryPrintResultResponse.setMessage("查询参数不全");
            tSummaryPrintResultResponse.setData(null);
            return tSummaryPrintResultResponse;
        }
        return sendPrintService.newBatchSummaryPrintQuery(criteria);
    }
}
