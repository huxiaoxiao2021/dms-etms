package com.jd.bluedragon.distribution.rest.fastrefund;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.FastRefundRequest;
import com.jd.bluedragon.distribution.fastRefund.service.FastRefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class FastRefundResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FastRefundService fastRefundService;
	
	@GET
	@Path("/fastrefund")
	public JdResponse fastRefund(@QueryParam("waybillCode") String waybillCode) {
		try{
			String result = fastRefundService.execRefund(waybillCode);
			if(result.equals(FastRefundService.SUCCESS_GOODS) 
					|| result.equals(FastRefundService.SUCCESS_MONEY)
					|| result.equals(FastRefundService.OTHER)
					|| result.equals(FastRefundService.FAIL_GOODS_REPEAT)
					|| result.equals(FastRefundService.WAYBILL_IS_CANCEL)){				
				return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			}else{
				if(result.equals(FastRefundService.FAIL_GOODS_ERROR)){
					return error("调用财务先货服务异常");
				}else if(result.equals(FastRefundService.FAIL_GOODS_PARAM)){
					return error("调用财务先货参数错误");
				}else if(result.equals(FastRefundService.FAIL_MONEY_ERROR)){
					return error("调用财务先款服务异常");
				}else if(result.equals(FastRefundService.WAYBILL_NOT_FIND)){
					return msg("没有找到对应的运单，请检查运单号是否正确");
				}else{
					return msg("财务接口返回信息[" + result + "]");
				}
			}
		}catch(Exception e){
			log.error("FastRefundResource.fastRefund error",e);
			return msg("服务异常：" + e.getMessage());
		}
	}
	
	@POST
	@Path("/fastrefundmq")
	public JdResponse fastRefund(FastRefundRequest fastRefundRequest) {
		try{
			String result = fastRefundService.execRefund(fastRefundRequest);
			if(result.equals(FastRefundService.SUCCESS_GOODS) 
					|| result.equals(FastRefundService.SUCCESS)
					|| result.equals(FastRefundService.SUCCESS_MONEY)
					|| result.equals(FastRefundService.OTHER)
					|| result.equals(FastRefundService.FAIL_GOODS_REPEAT)
					|| result.equals(FastRefundService.WAYBILL_IS_CANCEL)){				
				return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			}else{
				if(result.equals(FastRefundService.FAIL_GOODS_ERROR)){
					return error("调用财务先货服务异常");
				}else if(result.equals(FastRefundService.FAIL_GOODS_PARAM)){
					return error("调用财务先货参数错误");
				}else if(result.equals(FastRefundService.FAIL_MONEY_ERROR)){
					return error("调用财务先款服务异常");
				}else if(result.equals(FastRefundService.WAYBILL_NOT_FIND)){
					return msg("没有找到对应的运单，请检查运单号是否正确");
				}else{
					return msg("财务接口返回信息[" + result + "]");
				}
			}
		}catch(Exception e){
			log.error("FastRefundResource.fastRefund error",e);
			return msg("服务异常：" + e.getMessage());
		}
	}

	private JdResponse error(String msg){
		return new JdResponse(JdResponse.CODE_OK, msg);
	}
	
	private JdResponse msg(String msg){
		return new JdResponse(JdResponse.CODE_OK, msg);
	}
}
