package com.jd.bluedragon.distribution.fastRefund.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.api.request.FastRefundRequest;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefund;
import com.jd.bluedragon.distribution.fastRefund.domain.OrderCancelReq;
import com.jd.bluedragon.distribution.fastRefund.domain.QrderCancelResult;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.fa.orderrefund.RefundWebService;
import com.jd.fa.orderrefund.XmlMessage;
import com.jd.fa.refundService.CustomerRequestNew;
import com.jd.fa.refundService.RefundServiceNewSoap;
import com.jd.fa.refundService.ValidRequest;

@Service
public class FastRefundServiceImpl implements FastRefundService{

	@Autowired
	WaybillService waybillService;
	
	@Autowired
	RefundWebService refundWebService;
	
	@Autowired
	RefundServiceNewSoap refundServiceSoap;
	
    @Autowired
    private OperationLogService operationLogService;
	
    @Autowired
    private IPushPackageToMqService mqService;
    
	private static final int IS_GOODS = 1;
	private static final int IS_MONEY = 2;
	private static final int IS_OTHER = 3;
	
	private static final Log logger = LogFactory.getLog(FastRefundServiceImpl.class);
	
	private static final String MQ_KEY = "orbrefundRq";
	
    @Profiled(tag = "FastRefundServiceImpl.execRefundMq")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String execRefund(FastRefundRequest fastRefundRequest) throws Exception{
    	FastRefund fastRefund = toFastRefund(fastRefundRequest);
    	
    	String waybillCode = fastRefundRequest.getWaybillCode().toString();
		logger.info("FastRefundServiceImpl.execRefund 开始查询运单[" + waybillCode + "]");
		BigWaybillDto waybill = queryWaybillByCode(waybillCode);
		
		if(isCancel(waybillCode)){
			logger.info("FastRefundServiceImpl.execRefund 运单为取消锁定订单[" + waybillCode + "]");
			return FastRefundService.WAYBILL_IS_CANCEL;
		}
		 
		if(waybill.getWaybill() == null){
			logger.info("FastRefundServiceImpl.execRefund 查询运单信息为空[" + waybillCode + "]");
			return FastRefundService.WAYBILL_NOT_FIND;
		}
		
		if(null!=waybill.getWaybill().getWaybillSign() && waybill.getWaybill().getWaybillSign().length()>0){
//			1、 京东自营订单 
//			2、 SOP订单 
//			3、 纯外单 
//			4、 售后绑定的面单

			Integer waybillsign = Integer.valueOf(waybill.getWaybill().getWaybillSign().charAt(0));
			fastRefund.setWaybillSign(waybillsign);
		}

    	String json = JsonHelper.toJson(fastRefund);
    	logger.info("FastRefundServiceImpl.execRefund(mq) 调用FXM接口 body:" + json);
    	mqService.pubshMq(MQ_KEY, json, waybillCode);
    	logger.info("FastRefundServiceImpl.execRefund(mq) 调用FXM FINISH");
    	return FastRefundService.SUCCESS;
	}
    
    private FastRefund toFastRefund(FastRefundRequest fastRefundRequest){
    	FastRefund fastRefund = new FastRefund();
    	fastRefund.setOrderId(fastRefundRequest.getWaybillCode());//订单号　   	 
    	fastRefund.setApplyReason("分拣中心快速退款");//申请原因    	 
    	fastRefund.setReqErp(fastRefundRequest.getUserCode().toString());//申请人erp账号    	 
    	fastRefund.setReqName(fastRefundRequest.getUserName());//申请人name    	 
    	fastRefund.setSystemId(13);//分拣中心 13    	 
    	fastRefund.setApplyDate(DateHelper.parseDate(fastRefundRequest.getOperateTime()).getTime());//申请时间    	 
    	fastRefund.setReqDMSId(fastRefundRequest.getSiteCode());//分拣中心id    	 
    	fastRefund.setReqDMSName(fastRefundRequest.getSiteName());//分拣中心名称
    	
    	return fastRefund;
    }
	
	
    @Profiled(tag = "FastRefundServiceImpl.execRefund")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String execRefund(String waybillCode) throws Exception{
			logger.info("FastRefundServiceImpl.execRefund 开始查询运单[" + waybillCode + "]");
			 BigWaybillDto waybill = queryWaybillByCode(waybillCode);
			 if(isCancel(waybillCode)){
				 logger.info("FastRefundServiceImpl.execRefund 运单为取消锁定订单[" + waybillCode + "]");
				 return FastRefundService.WAYBILL_IS_CANCEL;
			 }
			 
			 if(waybill.getWaybill() == null){
				 logger.info("FastRefundServiceImpl.execRefund 查询运单信息为空[" + waybillCode + "]");
				 return FastRefundService.WAYBILL_NOT_FIND;
			 }

			 Integer type = getWaybillType(waybill);		
			 String result = "";
			 switch(type){
			 	case IS_GOODS:
			 		logger.info("FastRefundServiceImpl.execRefund 先货类型运单 [" + waybillCode + "]");
			 		result = fastRefundByGoods(waybillCode, waybill);
			 		addLog(waybillCode, type,result);
			 		break;
			 	case IS_MONEY:
			 		logger.info("FastRefundServiceImpl.execRefund 先款类型运单 [" + waybillCode + "]");
			 		result =  fastRefundByMoney(waybillCode);
			 		addLog(waybillCode, type,result);
			 		break;
			 	case IS_OTHER:
			 		logger.info("FastRefundServiceImpl.execRefund 其他类型运单 [" + waybillCode + "]");
			 		result =  FastRefundService.OTHER;
			 		addLog(waybillCode, type,result);
			 		break;
			 	default: 
				 	logger.info("FastRefundServiceImpl.execRefund 其他类型运单 [" + waybillCode + "]");
			 		result =  FastRefundService.OTHER;
			 		addLog(waybillCode, type,result);
			 		break;
			 }
			 if(result.length()>0){
				 return result;
			 }
			 
			 return FastRefundService.OTHER;
	}
	
	private void addLog(String waybillCode,Integer type,String msg){
		this.operationLogService.add(parseOperationLog(waybillCode, type,msg));
	}
	
	@Profiled(tag = "FastRefundServiceImpl.parseOperationLog")
    public OperationLog parseOperationLog(String waybillCode,Integer type,String msg) {
        OperationLog operationLog = new OperationLog();
        operationLog.setBoxCode("");
        operationLog.setWaybillCode(waybillCode);
        operationLog.setPackageCode("");
        operationLog.setCreateSiteCode(0);
        operationLog.setCreateSiteName("");
        operationLog.setReceiveSiteCode(0);
        operationLog.setReceiveSiteName("");
        operationLog.setCreateUser("DMS");
        operationLog.setCreateUserCode(0);
		operationLog.setCreateTime(new Date());
        operationLog.setOperateTime(new Date());
        
        operationLog.setLogType(OperationLog.LOG_TYPE_FASTREFUND);
        if(type.equals(IS_GOODS)){
        	operationLog.setRemark("先货退款[" + msg + "]");
        }else{
        	operationLog.setRemark("先款退款[" + msg + "]");
        }
        return operationLog;
    }
	
	/**
	 * 先货
	 * @param waybillCode
	 */
    @Profiled(tag = "FastRefundServiceImpl.fastRefundByGoods")
    public String fastRefundByGoods(String waybillCode,BigWaybillDto waybill) throws Exception {
		WaybillManageDomain waybillState = waybill.getWaybillState();
		OrderCancelReq req = new OrderCancelReq(waybillCode, waybillState.getStoreId(), waybillState.getCky2(), waybill.getWaybill().getArriveAreaId());
			
		String msg = XmlHelper.toXml(req, OrderCancelReq.class);
			
		XmlMessage xmlMessage = new XmlMessage();
		
		xmlMessage.setMessage(msg);
		xmlMessage.setMessageType("ordercancel_apply");
		
		logger.info("FastRefundServiceImpl.fastRefundByGoods 准备调用财务接口，BODY： [" + msg + "]");
		
		String resultXml = null;
		
		try{
		
			resultXml = refundWebService.sendXmlMessage(xmlMessage);

		}catch(Exception e){
			logger.error("FastRefundServiceImpl.fastRefundByGoods waybillCode[" + waybillCode + "] 调用财务接口，xml[" + xmlMessage + "]",e);
			throw e;
		}
		
		QrderCancelResult result = (QrderCancelResult)XmlHelper.toObject(resultXml, QrderCancelResult.class);
		
		logger.info("FastRefundServiceImpl.fastRefundByGoods 调用财务接口完毕，result.getFlag() [" + result.getFlag() + "] result.getErrorCode() [" + result.getErrorCode() + "]" + result.getErrorMessage());
		/*0：参数错误；1：订单取消受理中,不能重复取消；2：订单已取消,不能重复取消*/
		if(result.getFlag()){
			return FastRefundService.SUCCESS_GOODS;
		}
		
		logger.error("FastRefundServiceImpl.fastRefundByGoods waybillCode[" + waybillCode + "] 调用财务接口完毕，result.getErrorCode() [" + result.getErrorCode() + "]" + result.getErrorMessage() + " xml[" + xmlMessage + "]");
		
		if(result.getErrorCode()!=null){
			switch(result.getErrorCode()){
				case QrderCancelResult.CODE_PARAM:
					return FastRefundService.FAIL_GOODS_PARAM;
				case QrderCancelResult.CODE_CANCEL:
					return FastRefundService.FAIL_GOODS_REPEAT;
				case QrderCancelResult.CODE_REPEAT:
					return FastRefundService.FAIL_GOODS_REPEAT;
			}
		}
			
		return FastRefundService.FAIL_GOODS_ERROR;
	}

	/**
	 * 先款
	 * @param waybillCode
	 */
    @Profiled(tag = "FastRefundServiceImpl.fastRefundByMoney")
    public String fastRefundByMoney(String waybillCode) throws Exception{
			CustomerRequestNew customer = new  CustomerRequestNew();
			customer.setOrderId(Long.parseLong(waybillCode));//订单号
			customer.setRequestType(3);//退款方式（1.返余额 2.返借记卡 3.原返）
			customer.setRemark("拒收退款 ");//申请退款原因
			
			int businessType = 4;/*业务类型  1. FXM系统申请退款，客服外呼项目
								          2. 24小时自动对账功能申请退款，先删除后对账申请退款
								          3. 服务市场，申请退款
								          4. 分拣系统&青龙系统发起的退款申请*/
	
			
			logger.info("FastRefundServiceImpl.fastRefundByMoney 准备调用财务接口");
			
			try{
			
				ValidRequest validRequest = refundServiceSoap.innerSystemApplyForCheckWithType(customer, businessType);
				
				logger.info("FastRefundServiceImpl.fastRefundByMoney 调用财务接口完毕，result.getFlag() [" + validRequest.isIsValid() + "] result.getErrorCode() [" + validRequest.getMessage() + "]");
				
				if(validRequest.isIsValid()){
					return FastRefundService.SUCCESS_MONEY;
				}
				
				return validRequest.getMessage();//return FastRefundService.FAIL_MONEY_ERROR;
			}catch(Exception e){
				logger.error("FastRefundServiceImpl.fastRefundByMoney 调用财务接口，waybillCode [" + waybillCode + "]",e);
				throw e;
			}
	}

    @Profiled(tag = "FastRefundServiceImpl.queryWaybillByCode")
    public BigWaybillDto queryWaybillByCode(String waybillCode){
		BigWaybillDto dto = waybillService.getWaybill(waybillCode);
		if(dto!=null){
			return dto;
		}else{
			return null;
		}
	}
	
	private Integer getWaybillType(BigWaybillDto waybill){
		Integer payment = waybill.getWaybill().getPayment();
		logger.info("FastRefundServiceImpl.execRefund 运单 payment : [" + payment + "]");
		switch(payment){
			case 1:
			case 3:
			case 15:
				return IS_GOODS;
//			case 4:
//				return IS_MONEY;
			default:
				//return IS_OTHER;
				return IS_MONEY;
		}
	}

	@Profiled(tag = "FastRefundServiceImpl.isCancel")
	public boolean isCancel(String waybillCode){
		return WaybillCancelClient.isWaybillCancel(waybillCode);
	}
}
