package com.jd.bluedragon.distribution.rest.abnormalorder;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.abnormalorder.service.AbnormalOrderService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AbnormalOrderRequest;
import com.jd.bluedragon.distribution.api.response.AbnormalOrderResponse;
import com.jd.bluedragon.distribution.api.response.RefundReasonResponse;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AbnormalOrderResource {
	
	static final String IS_CANCEL = "客服取消订单成功，可退货";
	
	static final String IS_WAIT = "客服正在取消订单，请等待";
	
	static final String IS_NOT_CANCEL = "客服取消订单失败，再次申请取消";
	
	static final String IS_NEW = "提交申请消息成功，等待客服反馈";
	
	static final String THE_WAIT = "该订单取消操作失败，不可退货";
	
	static final String THE_EMPTY = "该订单没有申请记录，不可退货";
	
	static final String THE_NO = "该订单正在申请中，不可退货";
	/**
	 * 验货-数据重复验证
	 */
	static final Integer TYPE_REPEAT = 2;
	
	/**
	 * 退库-数据可执行
	 */
	static final Integer TYPE_DEAL = 1;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AbnormalOrderService abnormalOrderService;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@GET
	@Path("/abnormalorder/refundreason")
	public RefundReasonResponse queryRefundReason(){
		try{
			RefundReasonResponse response = new RefundReasonResponse();
			response.setRefundReason(Arrays.asList(abnormalOrderService.queryRefundReason()));
			response.setCode(JdResponse.CODE_OK);
			response.setMessage(JdResponse.MESSAGE_OK);
			return response;
		}catch (Exception e) {
			log.error("AbnormalOrderResource.queryRefundReason操作出现异常", e);
			RefundReasonResponse response = new RefundReasonResponse();
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);			
			return response;
		}
	}
	
	@GET
	@Path("/abnormalorder/query")
	public AbnormalOrderResponse queryAbnormalorder(@QueryParam("orderId")String orderId,@QueryParam("type")Integer type){
		AbnormalOrderResponse response = new AbnormalOrderResponse();
		try{			
			AbnormalOrder tmpvo = abnormalOrderService.queryAbnormalOrderByOrderId(orderId);
			if(TYPE_DEAL.equals(type)){
				/*退库执行*/
				if(tmpvo==null ){
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage(THE_EMPTY);
				}else if(tmpvo.getIsCancel().equals(AbnormalOrder.NOTCANCEL)){
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage(THE_WAIT);
				}else if(tmpvo.getIsCancel().equals(AbnormalOrder.CANCEL)){
					response.setCode(JdResponse.CODE_OK);
					response.setMessage(JdResponse.MESSAGE_OK);	
				}else /*AbnormalOrder.WAIT*/{
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage(THE_NO);	
				}
			}else/*TYPE_REPEAT*/{
				/*验货执行*/
				if(tmpvo==null || tmpvo.getIsCancel().equals(AbnormalOrder.CANCEL)){
					response.setCode(JdResponse.CODE_OK);
					response.setMessage(JdResponse.MESSAGE_OK);	
				}else if(tmpvo.getIsCancel().equals(AbnormalOrder.NOTCANCEL)){
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage(IS_NOT_CANCEL);	
				}else{
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage(IS_WAIT);	
				}
			}
			return response;
		}catch (Exception e) {
			log.error("AbnormalOrderResource.queryAbnormalorder操作出现异常", e);
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);			
			return response;
		}
	}
	
    @POST
    @Path("/abnormalorder/pushAbnormalOrder")
	public AbnormalOrderResponse pushAbnormalOrder(AbnormalOrderRequest request){
		AbnormalOrderResponse response = new AbnormalOrderResponse();
		try{
			HashMap<String/*运单号*/,Integer/*操作结果*/> result = abnormalOrderService.pushNewDataFromPDA(new AbnormalOrder[]{new AbnormalOrder(request)});

			if(result.get(request.getOrderId()).equals(AbnormalOrder.CANCEL)){
				response.setCode(JdResponse.CODE_OK);
				response.setMessage(IS_CANCEL);
			}else if(result.get(request.getOrderId()).equals(AbnormalOrder.NEW)){
				response.setCode(JdResponse.CODE_OK);
				response.setMessage(IS_NEW);
			}else if(result.get(request.getOrderId()).equals(AbnormalOrder.NOTCANCEL)){
				response.setCode(JdResponse.CODE_OK);
				response.setMessage(IS_NEW);
			}else{
				response.setCode(JdResponse.CODE_PARAM_ERROR);
				response.setMessage(IS_WAIT);
			}
		}catch (Exception e) {
			log.error("AbnormalOrderResource.pushAbnormalOrder操作出现异常", e);
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);			
		}
		
		return response;
	}
	@POST
	@Path("/abnormalorder/pushAbnormalOrders")
	public AbnormalOrderResponse pushAbnormalOrders(AbnormalOrderRequest request){
		AbnormalOrderResponse response = new AbnormalOrderResponse();
		response.setCode(JdResponse.CODE_OK);
		String waybillCodes=request.getOrderId();
		String[] waybillCodeArr=waybillCodes.split(Constants.SEPARATOR_COMMA);
		//获取操作人信息
		String usercode=request.getUserName();
		if (usercode==null){
			response.setCode(response.CODE_SERVICE_ERROR);
			response.setMessage(response.MESSAGE_SERVICE_ERROR);
			return response;
		}
		BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(usercode);
		String dateNow= DateHelper.formatDate(new Date(),Constants.DATE_TIME_FORMAT);

		request.setUserCode(userDto.getStaffNo());
		request.setCreateUserErp(userDto.getAccountNumber());
		request.setUserName(userDto.getStaffName());
		request.setOperateTime(dateNow);
		request.setSiteCode(userDto.getSiteCode());
		request.setSiteName(userDto.getSiteName());
		request.setTrackContent("订单扫描异常【"+ (StringUtils.isEmpty(request.getAbnormalReason2())?request.getAbnormalReason1():request.getAbnormalReason2())+"】。");
		for (String waybillCode:waybillCodeArr){
			request.setOrderId(waybillCode);
			AbnormalOrderResponse responseOne=pushAbnormalOrder(request);
			if(responseOne.getCode()!=JdResponse.CODE_OK){
				//如果有失败的
				response.setCode(responseOne.getCode());
				if (response.getMessage()==null){
					response.setMessage(waybillCode+":"+responseOne.getMessage());
				}else{
					response.setMessage(response.getMessage()+","+waybillCode+":"+responseOne.getMessage());
				}
			}
		}
		return response;
	}
	@GET
	@Path("/abnormalorder/clear")
	public AbnormalOrderResponse clear(){
		abnormalOrderService.clear();
		
		AbnormalOrderResponse response = new AbnormalOrderResponse();
		response.setCode(JdResponse.CODE_OK);
		return response;
	}
}
