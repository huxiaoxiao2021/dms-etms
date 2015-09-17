package com.jd.bluedragon.distribution.reverse.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd.oom.client.clientbean.Order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.core.exception.OrderCallTimeoutException;
import com.jd.bluedragon.core.message.producer.MessageProducer;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.basic.domain.BaseOrg;
import com.jd.iwmss.stock.client.ArrayOfStock;
import com.jd.iwmss.stock.client.Stock;
import com.jd.iwmss.stock.client.StockDetail;
import com.jd.iwmss.stock.client.StockParamter;
import com.jd.iwmss.stock.client.StockWebServiceSoap;
import com.jd.stock.iwms.export.param.StockVOParam;
import com.jd.stock.iwms.export.vo.StockDetailVO;
import com.jd.stock.iwms.export.vo.StockExtVO;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 备件库收货推出管  
 * @author huangliang
 *
 */
@Service("reverseReceiveNotifyStockService")
public class ReverseReceiveNotifyStockService {

	private final Log logger = LogFactory.getLog(this.getClass());

	// 先款支付类型
	private static final Integer PAY_TYPE_PRE = 1;
	// 后款支付类型
	private static final Integer PAY_TYPE_POST = 2;
	// 不明确支付类型
	private static final Integer PAY_TYPE_UNKNOWN = 3;
	// 获得支付类型错误
	private static final Integer PAY_TYPE_ERROR = 4;
			
	private static final Integer STOCK_TYPE_1601 = 1601; // 逆向物流后款退货-虚入
	private static final Integer STOCK_TYPE_1602 = 1602; // 逆向物流-虚出
	private static final Integer STOCK_TYPE_1603 = 1603; // 逆向物流先款退货-虚入

	private final static String MQ_KEY_STOCK = "wms_stock_chuguanmsg";

	private static final List<Integer> needRetunWaybillTypes = Lists.newArrayList(11, 13, 15, 16, 18, 19, 42, 56, 61);

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	private ProductService productService;

	@Autowired
	private StockWebServiceSoap stockWebService;

	@Autowired
	private OrderBankService orderBankService;

	@Autowired
	private MessageProducer messageProducer;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private StockExportManager stockExportManager;
	
	public Long receive(String message) {
		if (JsonHelper.isJson(message, ReverseReceiveRequest.class)) {
			return -1L;
		}

		ReverseReceiveRequest request = (ReverseReceiveRequest) XmlHelper
				.toObject(message, ReverseReceiveRequest.class);

		if (request == null) {
			this.logger.warn("消息序列化出现异常, 消息：" + message);
		} else if (ReverseReceive.RECEIVE_TYPE_SPWMS.equals(request.getReceiveType())
				&& ReverseReceive.RECEIVE.equals(request.getCanReceive())) {
			return new Long(request.getOrderId());
		} else {
			this.logger.info("消息来源：" + request.getReceiveType());
		}

		return -1L;
	}

	public Boolean nodifyStock(Long waybillCode) throws Exception {
		this.logger.info("运单号：" + waybillCode);

		if (!NumberHelper.isPositiveNumber(waybillCode)) {
			this.logger.warn("运单号非法, 运单号 为：" + waybillCode);
			return Boolean.TRUE;
		}
		SystemLog sysLog = new SystemLog();//日志对象
		sysLog.setKeyword1(waybillCode.toString());
		sysLog.setType(SystemLogContants.TYPE_REVERSE_STOCK);//设置日志类型
		
		try{
			Order order = this.orderWebService.getOrder(waybillCode);
			List<Product> products = this.productService.getOrderProducts(waybillCode);
			
			//修改逻辑当order获取不到时，取归档历史信息。
			//原抛异常逻辑if(order==null || products==null) 即有一项为空即抛出，更改后的逻辑等价于if( (order==null&&hisOrder==null) || products==null )
			if (products.size() == 0) {
				this.logger.warn("无商品信息!");
				sysLog.setContent("无商品信息");
				throw new OrderCallTimeoutException("order has no products.");
			}else if(order == null){//为空时，取一下历史的订单信息
				this.logger.info("订单可能转历史，获取历史订单, 运单号 为：" + waybillCode);
				jd.oom.client.orderfile.Order hisOrder = this.orderWebService.getHistoryOrder(waybillCode);
				
				if (hisOrder == null) {
					this.logger.warn("运单信息为空!订单号:"+waybillCode);
					sysLog.setContent("运单信息为空");
					throw new OrderCallTimeoutException("order is not exist.");
				}else {//如果历史订单信息不为空，则拷贝属性值
					order = new Order();
					order.setId(hisOrder.getId());
					order.setIdCompanyBranchName(hisOrder.getIdCompanyBranchName());
					order.setIdCompanyBranch(hisOrder.getIdCompanyBranch());
					order.setTotalFee(hisOrder.getTotalFee());
					order.setCustomerName(hisOrder.getCustomerName());
					order.setDeliveryCenterID(hisOrder.getDeliveryCenterID());
					order.setStoreId(hisOrder.getStoreId());
					order.setOrderType(hisOrder.getOrderType());
					
					this.logger.info("历史订单信息orderId："+waybillCode+", IdCompanyBranchName:"+order.getIdCompanyBranchName()+
							",IdCompanyBranch:"+order.getIdCompanyBranch()+",CustomerName:"+order.getCustomerName()+
							",DeliveryCenterID:"+order.getDeliveryCenterID()+",StoreId:"+order.getStoreId()+
							",OrderType:"+order.getOrderType()+",TotalFee:"+order.getTotalFee());
				}
			}
			
			{
				//FIXME:======================================增加获得财务部机构名的逻辑，需要缓存的支持=================================
			}
			
			//校验机构名称是否有效，如无效则从基础资料获得
			if (StringUtils.isBlank(order.getIdCompanyBranchName())) {
	            BaseOrg bo = baseMajorManager.getBaseOrgByOrgId(order.getIdCompanyBranch());
	            if (bo != null) {
	            	order.setIdCompanyBranchName(bo.getOrgName());//需要调用基本资料接口根据机构ID获取机构Name
	            }
	            this.logger.info("原机构名为空，从基础资料重新获得订单"+waybillCode+"机构名 IdCompanyBranchName:"+order.getIdCompanyBranchName());
	        }
			
			sysLog.setKeyword2(String.valueOf(order.getOrderType()));//设置订单的类型
			
			if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())) {
				// Long financeCode =
				// this.financeWebService.getFinancialOrderByOrderId(waybillCode);
				Integer payType = getPayType(waybillCode);
				// 判断先款后款时出错，日志记录，后续人工介入
				if (payType.equals(PAY_TYPE_ERROR)
						|| payType.equals(PAY_TYPE_UNKNOWN)) {
					this.logger.error("先款后款不明确" + waybillCode);
					
					sysLog.setContent("先款后款不明确");
					return false;
				}
				long result = 0;
				OrderBankResponse orderBank = orderBankService.getOrderBankResponse(String.valueOf(waybillCode));

				this.logger.info("运单号：" + waybillCode + ", 使用推库管新接口");
				/** 新逻辑开始 */
				Date creatDate = new Date();//给扩展属性使用的创建时间
				//设置扩展属性
				StockExtVO stockExtVO0 = new StockExtVO();
				stockExtVO0.setTypeID(isPrePay(payType) ? 1402 : 1401);
				stockExtVO0.setBusiType("订单退货");
				stockExtVO0.setSubType(isPrePay(payType) ? "先款拒收" : "先货拒收");
				stockExtVO0.setExtType("逆向物流");
				stockExtVO0.setBusiNo(String.valueOf(waybillCode));
				stockExtVO0.setSys("ql.dms");
				stockExtVO0.setCreatDate(creatDate);
				stockExtVO0.setAdjust(0);
				stockExtVO0.setVirtual(1);
				
				StockVOParam inWmsStock0 = new StockVOParam();
				inWmsStock0.setRfId(String.valueOf(waybillCode));
				inWmsStock0.setRfType(6);
				inWmsStock0.setOrderId(waybillCode);
				inWmsStock0.setMoneyn(1);
				inWmsStock0.setCity(DateHelper.formatDateTime(new Date()));
				inWmsStock0.setJigou(order.getIdCompanyBranchName());
				inWmsStock0.setLaiyuan(order.getIdCompanyBranchName());
				inWmsStock0.setShanghai(order.getIdCompanyBranch());
				inWmsStock0.setActor(BigDecimal.valueOf(0));
				inWmsStock0.setQianzi(1);
				inWmsStock0.setYun(order.getTotalFee());
				inWmsStock0.setYouhui(orderBank.getDiscount());
				inWmsStock0.setQite(BigDecimal.valueOf(1));//与潘文华确认改为1，已不用区分先款先货
				inWmsStock0.setPhdanhao(0);
				inWmsStock0.setCgdanhao(0);
				inWmsStock0.setLaiyuancode(order.getCustomerName());
				inWmsStock0.setQtfs("逆向物流");
				inWmsStock0.setOrgId(order.getDeliveryCenterID());
				inWmsStock0.setSid(order.getStoreId());
				inWmsStock0.setChuruId(isPrePay(payType) ? 1 : 2);
				inWmsStock0.setFeileiId(isPrePay(payType) ? 2 : 1);
				inWmsStock0.setKuanx(isPrePay(payType) ? "已收" : "未收");
				inWmsStock0.setZjine(isPrePay(payType) ? orderBank.getShouldPay()
						: orderBank.getShouldPay().negate());
				inWmsStock0.setStockDetails(this.getStockDetailVO(products, payType));
				inWmsStock0.setStockExtVO(stockExtVO0);

				//设置扩展属性
				StockExtVO stockExtVO1 = new StockExtVO();
				stockExtVO1.setTypeID(1403);
				stockExtVO1.setBusiType("大库出备件库");
//						stockExtVO1.setSubType(isPrePay(payType) ? "先款拒收" : "先货拒收");
				stockExtVO1.setExtType("逆向物流");
				stockExtVO1.setBusiNo(String.valueOf(waybillCode));
				stockExtVO1.setSys("ql.dms");
				stockExtVO1.setCreatDate(creatDate);
				stockExtVO1.setAdjust(0);
				stockExtVO1.setVirtual(1);
				
				StockVOParam outSpwmsStock0 = new StockVOParam();
				outSpwmsStock0.setRfId(String.valueOf(waybillCode));
				outSpwmsStock0.setRfType(2);
				outSpwmsStock0.setOrderId(waybillCode);
				outSpwmsStock0.setMoneyn(1);
				outSpwmsStock0.setCity(DateHelper.formatDateTime(new Date()));
				outSpwmsStock0.setJigou(order.getIdCompanyBranchName());
				outSpwmsStock0.setLaiyuan(order.getIdCompanyBranchName());
				outSpwmsStock0.setShanghai(order.getIdCompanyBranch());
				outSpwmsStock0.setActor(BigDecimal.valueOf(0));
				outSpwmsStock0.setQianzi(1);
				outSpwmsStock0.setYun(order.getTotalFee());
				outSpwmsStock0.setYouhui(orderBank.getDiscount());
				outSpwmsStock0.setQite(BigDecimal.valueOf(0));
				outSpwmsStock0.setPhdanhao(0);
				outSpwmsStock0.setCgdanhao(0);
				outSpwmsStock0.setLaiyuancode(order.getCustomerName());
				outSpwmsStock0.setQtfs("逆向物流");
				outSpwmsStock0.setOrgId(order.getDeliveryCenterID());
				outSpwmsStock0.setSid(order.getStoreId());
				outSpwmsStock0.setChuruId(2);
				outSpwmsStock0.setFeileiId(6);
				outSpwmsStock0.setKuanx("未知 ");
				outSpwmsStock0.setZjine(orderBank.getShouldPay());
				outSpwmsStock0.setStockDetails(this.getStockDetailVO(products));
				outSpwmsStock0.setStockExtVO(stockExtVO0);
				
				result = stockExportManager.insertStockVirtualIntOut(inWmsStock0, outSpwmsStock0);
				/** 新逻辑结束 */
				
				try {
					//业务流程监控, 备件库埋点
					Map<String, String> data = new HashMap<String, String>();
					data.put("orderId", waybillCode.toString());
					Profiler.bizNode("Reverse_mq_dms2stock", data);
				} catch (Exception e) {
					this.logger.error("推送UMP发生异常.", e);
				}
				
				this.logger.info("运单号：" + waybillCode + ", 库存中间件返回结果：" + result);
				
				sysLog.setKeyword3("WEBSERVICE");
				sysLog.setKeyword4(result);
				if(result!=0)
					sysLog.setContent("推出管成功!");
				else
					sysLog.setContent("推出管失败!");
			} else if (needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType()))) {
				//Long financeCode = this.financeWebService.getFinancialOrderByOrderId(waybillCode);
				Integer payType = getPayType(waybillCode);

				if (isPrePay(payType)) {
					this.messageProducer.send(MQ_KEY_STOCK,
							this.stockMessage(order, products, STOCK_TYPE_1602, payType), String.valueOf(waybillCode));
					this.messageProducer.send(MQ_KEY_STOCK,
							this.stockMessage(order, products, STOCK_TYPE_1603, payType), String.valueOf(waybillCode));
				} else {
					this.messageProducer.send(MQ_KEY_STOCK,
							this.stockMessage(order, products, STOCK_TYPE_1601, payType), String.valueOf(waybillCode));
					this.messageProducer.send(MQ_KEY_STOCK,
							this.stockMessage(order, products, STOCK_TYPE_1602, payType), String.valueOf(waybillCode));
				}
				
				sysLog.setKeyword3("MQ");
				sysLog.setContent("推出管成功!");
			} else {
				sysLog.setContent("订单类型不需要回传库存中间件");
				this.logger.info("运单号：" + waybillCode + ", 不需要回传库存中间件。");
			}
		}catch(Exception e){
			this.logger.error("运单号：" + waybillCode + ", 推出管失败。", e);
			if(StringHelper.isEmpty(sysLog.getContent())){
				sysLog.setContent(e.getMessage());
			}
		}finally{
			SystemLogUtil.log(sysLog);
		}
		
		return Boolean.TRUE;
	}

	public String stockMessage(Order order, List<Product> products, Integer stockType, Integer payType) {
		StringBuilder message = new StringBuilder();

		message.append("<ChuguanMsg><ChuguanInfo><RfType>16</RfType><Qtfs>5</Qtfs><Flag>1</Flag>"
				+ "<JingBan>ql.dms</JingBan><SysTag>ql.dms</SysTag><LaiYuan>ql.dms</LaiYuan><Ydanhao>0</Ydanhao><RfKey>"
				+ order.getId() + "</RfKey><SubType>" + stockType + "</SubType><RfNo>" + order.getId()
				+ "</RfNo><RfDate>" + DateHelper.formatDateTime(new Date()) + "</RfDate><ChuRu>"
				+ this.getStockChuRu(stockType) + "</ChuRu><LocalId>" + order.getDeliveryCenterID() + "</LocalId><SId>"
				+ order.getStoreId() + "</SId></ChuguanInfo><QingdanInfos>");

		for (Product product : products) {
			message.append("<QingdanInfo><Wid>" + product.getProductId() + "</Wid><Ware>" + product.getName()
					+ "</Ware><Num>" + this.getQuantity(stockType, payType, product.getQuantity())
					+ "</Num></QingdanInfo>");
		}

		message.append("</QingdanInfos></ChuguanMsg>");

		return message.toString();
	}

	private Integer getQuantity(Integer stockType, Integer payType, Integer quantity) {
		if (STOCK_TYPE_1601.equals(stockType) && !this.isPrePay(payType)) {
			return this.negate(quantity);
		}

		return quantity;
	}

	private Integer getStockChuRu(Integer stockType) {
		Assert.notNull(stockType, "stockType must not be null");

		if (STOCK_TYPE_1601.equals(stockType) || STOCK_TYPE_1602.equals(stockType)) {
			return 2;
		} else if (STOCK_TYPE_1603.equals(stockType)) {
			return 1;
		} else {
			return -1;
		}
	}

	private List<StockDetailVO> getStockDetailVO(List<Product> products, Integer paidType) {
		List<StockDetailVO> stockDetailVOs = new ArrayList<StockDetailVO>();
		for (Product product : products) {
			StockDetailVO sdVO = new StockDetailVO();
			sdVO.setBilv(1);
			sdVO.setWareId(new Long(product.getProductId()));
			sdVO.setWare(product.getName());
			sdVO.setJiage(product.getPrice());
			sdVO.setNum(this.isPrePay(paidType) ? product.getQuantity() : this.negate(product
					.getQuantity()));
			sdVO.setZjine(this.isPrePay(paidType) ? product.getPrice().multiply(
					new BigDecimal(product.getQuantity())) : product.getPrice()
					.multiply(new BigDecimal(product.getQuantity())).negate());
			stockDetailVOs.add(sdVO);
		}

		return stockDetailVOs;
	}

	private List<StockDetail> getStockDetail(List<Product> products, Integer paidType) {
		List<StockDetail> stockDetails = new ArrayList<StockDetail>();
		for (Product product : products) {
			StockDetail stockDetail = new StockDetail();
			stockDetail.setBilv(1);
			stockDetail.setWareid(new Integer(product.getProductId()));
			stockDetail.setWare(product.getName());
			stockDetail.setJiage(product.getPrice());
			stockDetail.setNum(this.isPrePay(paidType) ? product.getQuantity() : this.negate(product
					.getQuantity()));
			stockDetail.setZjine(this.isPrePay(paidType) ? product.getPrice().multiply(
					new BigDecimal(product.getQuantity())) : product.getPrice()
					.multiply(new BigDecimal(product.getQuantity())).negate());
			stockDetails.add(stockDetail);
		}

		return stockDetails;
	}
	
	private List<StockDetail> getStockDetail(List<Product> products) {
		List<StockDetail> stockDetails = new ArrayList<StockDetail>();
		for (Product product : products) {
			StockDetail stockDetail = new StockDetail();
			stockDetail.setBilv(1);
			stockDetail.setWareid(new Integer(product.getProductId()));
			stockDetail.setWare(product.getName());
			stockDetail.setJiage(product.getPrice());
			stockDetail.setNum(product.getQuantity());
			stockDetail.setZjine(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
			stockDetails.add(stockDetail);
		}

		return stockDetails;
	}

	/**
	 * 将商品明细转换
	 * @param products
	 * @return
	 */
	private List<StockDetailVO> getStockDetailVO(List<Product> products) {
		List<StockDetailVO> stockDetailVOs = new ArrayList<StockDetailVO>();
		for (Product product : products) {
			StockDetailVO sdVO = new StockDetailVO();
			sdVO.setBilv(1);
			sdVO.setWareId(new Long(product.getProductId()));
			sdVO.setWare(product.getName());
			sdVO.setJiage(product.getPrice());
			sdVO.setNum(product.getQuantity());
			sdVO.setZjine(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
			stockDetailVOs.add(sdVO);
		}

		return stockDetailVOs;
	}
	
	private Integer negate(Integer num) {
		return num == null ? 0 : 0 - num;
	}
	
	/**
	 * 根据出管判断先款还是后款 public List<Stock> GetStocks(StockParamter stock) 使用方法：
	 * 入参StockParamter 对象的Orderid 属性赋值要查询的订单号 该方法会返回这个订单号的所有出管记录，然后请在返回的出管记录中匹配：
	 * 如果 churu=‘出库’ 且 feilei=‘放货’ 且 qite=0 即为后款订单（先货后款）， 如果 churu=‘出库’ 且
	 * feilei=‘销售’ 即为先款订单
	 * 
	 * @return
	 */
	public Integer getPayType(Long waybillCode) {
		StockParamter request = new StockParamter();
		request.setOrderid(waybillCode);

		Integer result = PAY_TYPE_UNKNOWN;
		String churu = null;
		String feifei = null;
		BigDecimal qite = null;
		try {
			ArrayOfStock arrayOfStock = stockWebService.getStocks(request);
			List<Stock> stocks = arrayOfStock.getStock();
			Stock stock = stocks.get(0);
			churu = stock.getChuru();
			feifei = stock.getFeilei();
			qite = stock.getQite();
			if (churu.equals("出库") && feifei.equals("放货")
					&& (qite.compareTo(new BigDecimal(0)) == 0)) {
				result = PAY_TYPE_POST;
			} else if (churu.equals("出库") && feifei.equals("销售")) {
				result = PAY_TYPE_PRE;
			}
			// 异常情况日志记录方便定位问题
			logger.info("getPayType waybillCode:" + waybillCode + "detail: churu: " + churu + ",feifei" + feifei
					+ ",qite" + qite);
		} catch (Exception e) {
			result = PAY_TYPE_ERROR;
			logger.error("isPrePaid error: " + waybillCode, e);
		}
		if (result.equals(PAY_TYPE_UNKNOWN) || result.equals(PAY_TYPE_ERROR)) {
			// 异常情况日志记录方便定位问题
			logger.error("getPayType waybillCode:" + waybillCode + "detail: churu: " + churu + ",feifei" + feifei
					+ ",qite" + qite);
			logException(waybillCode);
		}
		return result;
	}

	private void logException(Long waybillCode) {
		logger.error("不能判断订单是先款还是后款: " + waybillCode);
	}
	
	private boolean isPrePay(Integer payType) {
		return payType.equals(PAY_TYPE_PRE);
	}
	
}
