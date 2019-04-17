package com.jd.bluedragon.distribution.web.abnormalOrder;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.abnormalorder.service.AbnormalOrderService;
import com.jd.bluedragon.distribution.api.request.AbnormalOrderRequest;
import com.jd.bluedragon.distribution.api.response.RefundReason;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;


@Controller
@RequestMapping("/abnormalorder")
public class AbnormalOrderController {
	
	static final String IS_CANCEL = "客服取消订单成功，可退货";
	
	static final String IS_WAIT = "客服正在取消订单，请等待";
	
	static final String IS_NOT_CANCEL = "客服取消订单失败，再次申请取消";
	
	static final String IS_NEW = "提交申请消息成功，等待客服反馈";
	
	@Autowired
	AbnormalOrderService abnormalOrderService;

	@Authorization(Constants.DMS_WEB_ABNORMAL_ORDER_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String queryOperateLog(AbnormalOrderRequest abnormalOrderRequest, Pager pager, Model model){
		RefundReason[] refundReasons = abnormalOrderService.queryRefundReason();
		
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		
		model.addAttribute("refundReasons",refundReasons);
		model.addAttribute("refundReasonsJson",JsonHelper.toJson(refundReasons));
		if(erpUser==null){
			model.addAttribute("errorMsg","请登录ERP帐号");
			return "abnormalOrder/abnormalOrderError";
		}
		return "abnormalOrder/abnormalOrderList";
	}
	@Authorization(Constants.DMS_WEB_ABNORMAL_ORDER_R)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String queryOperateLog(AbnormalOrderRequest abnormalOrderRequest, Pager pager, Model model,Integer[] row,HttpServletRequest request){
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		if(erpUser==null){
			model.addAttribute("errorMsg","请登录ERP帐号");
			return "abnormalOrder/abnormalOrderError";
		}
		
		AbnormalOrder[] abnormalOrder = createData(row,request,erpUser);
		HashMap<String/*运单号*/,Integer/*操作结果*/> result = abnormalOrderService.pushNewDataFromPDA(abnormalOrder);
		ArrayList<AbnormalOrder> responses = createResponse(result);
		model.addAttribute("responses",responses);
		return "abnormalOrder/abnormalOrderResultList";
	}
	
	private ArrayList<AbnormalOrder> createResponse(HashMap<String/*运单号*/,Integer/*操作结果*/> result){
		ArrayList<AbnormalOrder> al = new ArrayList<AbnormalOrder>();
		String[] keys = result.keySet().toArray(new String[0]);
		for(String key:keys){
			AbnormalOrder abnormalOrder = new AbnormalOrder();
			Integer code = result.get(key);
			String msg = "";
			if(code.equals(AbnormalOrder.CANCEL)){
				msg = IS_CANCEL;
			}else if(code.equals(AbnormalOrder.NEW)){
				msg = IS_NEW;
			}else if(code.equals(AbnormalOrder.NOTCANCEL)){
				msg = IS_NEW;
			}else{
				msg = IS_WAIT;
			}
			
			abnormalOrder.setOrderId(key);
			abnormalOrder.setMemo(msg);
			
			al.add(abnormalOrder);
		}
		
		return al;
	}
	
	private AbnormalOrder[] createData(Integer[] rows,HttpServletRequest request,ErpUserClient.ErpUser erpUser){
		ArrayList<AbnormalOrder> al = new ArrayList<AbnormalOrder>();
		
		for(Integer row : rows){
			AbnormalOrder abnormalOrder = new AbnormalOrder();
			String waybillcode = request.getParameter("waybill" + row);
			String packageNo = request.getParameter("packageNo" + row);
			String reason = request.getParameter("reason" + row);
			
			String abnormalCodeF = request.getParameter("abnormalCodeF" + row);
			String abnormalCodeFText = request.getParameter("abnormalCodeFText" + row);
			String abnormalCodeS = request.getParameter("abnormalCodeS" + row);
			String abnormalCodeSText = request.getParameter("abnormalCodeSText" + row);
			
			Integer AbnormalCode1 = Integer.valueOf(abnormalCodeF);
			Integer AbnormalCode2 = NumberHelper.convertToInteger(abnormalCodeS,-1);
			abnormalOrder.setOrderId(waybillcode);
			
			abnormalOrder.setAbnormalCode1(AbnormalCode1);
			abnormalOrder.setAbnormalCode2(AbnormalCode2);
			abnormalOrder.setAbnormalReason1(abnormalCodeFText);
			abnormalOrder.setAbnormalReason2(abnormalCodeSText);
			abnormalOrder.setCreateSiteCode(-1);
			abnormalOrder.setCreateSiteName("erp");
			abnormalOrder.setCreateTime(DateHelper.toDate(System.currentTimeMillis()));
			abnormalOrder.setCreateUser(erpUser.getUserName());
			
			abnormalOrder.setCreateUserCode(-1);
			abnormalOrder.setCreateUserErp(erpUser.getUserCode());
			abnormalOrder.setOperateTime(DateHelper.toDate(System.currentTimeMillis()));
			
			al.add(abnormalOrder);
		}
		
		return al.toArray(new AbnormalOrder[0]);
	}

}
