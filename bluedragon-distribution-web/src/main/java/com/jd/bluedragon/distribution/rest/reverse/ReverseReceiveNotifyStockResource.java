package com.jd.bluedragon.distribution.rest.reverse;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jd.oom.client.clientbean.Order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.service.KuGuanService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.kuguan.domain.OrderStockInfo;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.utils.ObjectMapHelper;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReverseReceiveNotifyStockResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	KuGuanService tKuGuanService;

	/**
	 * 使用notify方法代替
	 * 
	 * @param waybillCode
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/reverse/stock/nodify/{waybillCode}")
	@Deprecated
	public String sendMessage(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		return JdResponse.MESSAGE_OK;
	}

	@GET
	@Path("/reverseReceiveNotifyStock/notify/{waybillCode}")
	public String notify(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		String resultStr = check(waybillCode);
		if("OK".equals(resultStr))
			this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		return resultStr;
	}

	@GET
	@Path("/reverseReceiveNotifyStock/check/{waybillCode}")
	public String check(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		OrderStockInfo osi = null;
		String resultStr = null;
		try{
			osi = getKeyKuguanInfo(waybillCode);
		}catch(Exception e){
			logger.error("获得库管判断信息失败:"+waybillCode, e);
		}
		if (osi == null)
			resultStr =  "获得库管判断信息失败";
		else
			resultStr = osi.judge();
		
		return java.net.URLEncoder.encode(resultStr, "UTF-8");
	}

	private OrderStockInfo getKeyKuguanInfo(Long waybillCode) {

		// 1.定义订单类型、库管信息对应的字段
		String orderType = null;
		String fangshi = null;
		String fenlei = null;
		String qita = null;
		String qitafangshi = null;
		StringBuffer resultStr = new StringBuffer();

		// 1.查运单类型
		Order order = this.orderWebService.getOrder(waybillCode);

		if (order == null) {// 为空时，取一下历史的订单信息
			jd.oom.client.orderfile.Order hisOrder = this.orderWebService
					.getHistoryOrder(waybillCode);
			if (hisOrder != null) {
				orderType = String.valueOf(hisOrder.getOrderType());
			}
		} else {
			orderType = String.valueOf(order.getOrderType());
		}

		// 2.查询页面上的值，用于判断先后款
		KuGuanDomain stockPageResp = getStockInfo(String.valueOf(waybillCode),
				"1");

		if (stockPageResp != null) {
			fangshi = stockPageResp.getLblWay();
			fenlei = stockPageResp.getLblType();
			qita = stockPageResp.getLblOther();
			qitafangshi = stockPageResp.getLblOther();
		}

		// 3. 生成订单对象自行判断
		OrderStockInfo osi = new OrderStockInfo(String.valueOf(waybillCode),
				orderType, fangshi, fenlei, qita, qitafangshi);
		return osi;
	}

	private KuGuanDomain getStockInfo(
			@PathParam("waybillCode") String waybillCode,
			@PathParam("ddlType") String ddlType) {
		KuGuanDomain kuGuanDomain = new KuGuanDomain();

		kuGuanDomain.setDdlType(ddlType);
		kuGuanDomain.setWaybillCode(waybillCode);

		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);

		try {
			logger.info("根据订单号获取库管单信息参数错误-queryByParams");
			kuGuanDomain = tKuGuanService.queryByParams(params);
		} catch (Exception e) {
			kuGuanDomain = new KuGuanDomain();
			kuGuanDomain.setDdlType(ddlType);
			kuGuanDomain.setWaybillCode(null);
			logger.info("根据订单号获取库管单信息服务异常" + e);
		}
		return kuGuanDomain;
	}
}
