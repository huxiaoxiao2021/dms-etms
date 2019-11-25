package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.domain.OrderStockInfo;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
	
	@Autowired
	private StockExportManager stockExportManager;
	
    @Autowired
    private ReverseReceiveDao reverseReceiveDao;


    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Resource
    protected UccPropertyConfiguration uccPropertyConfiguration;

	/**
	 * 使用notify方法代替
	 * 
	 * @param waybillCode
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/reverse/stock/nodify/{waybillCode}")
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.ReverseReceiveNotifyStockResource.sendMessage", mState = JProEnum.TP)
	public String sendMessage(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		return JdResponse.MESSAGE_OK;
	}

	@GET
	@Path("/reverse/pop/nodify/{waybillCode}")
	public String sendMessageToPop(@PathParam("waybillCode") String waybillCode)
			throws Exception {
		boolean result = this.reverseSendPopMessageService.sendPopMessage(waybillCode);
		return result?JdResponse.MESSAGE_OK:"NOT"+JdResponse.MESSAGE_OK;
	}
	
	@GET
	@Path("/reverseReceiveNotifyStock/notify/{waybillCode}")
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.ReverseReceiveNotifyStockResource.notify", mState = JProEnum.TP)
	public String notify(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		String resultStr = check(waybillCode);
		if(JdResponse.MESSAGE_OK.equals(resultStr)){
			resultStr="CHECK:OK";
			boolean stockResult = this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
			boolean popResult = this.reverseSendPopMessageService.sendPopMessage(waybillCode.toString());
			if(!stockResult){
				resultStr+=",STOCK:NO";
			}
			if(!popResult){
				resultStr+=",POP:NO";
			}
		}
		return resultStr;
	}

	@GET
	@Path("/reverseReceiveNotifyStock/check/{waybillCode}")
	public String check(@PathParam("waybillCode") Long waybillCode)
			throws Exception {
		OrderStockInfo osi = null;
		String resultStr = null;
		
		ReverseReceive reverseReceive = reverseReceiveDao.findOneReverseReceive(String.valueOf(waybillCode), ReverseReceive.RECEIVE, ReverseReceive.RECEIVE_TYPE_SPWMS, null, null, null);
		if(reverseReceive==null){
			resultStr = "订单备件库未收货:"+waybillCode;
		}else{
			try{
				osi = getKeyKuguanInfo(waybillCode);
			}catch(Exception e){
				logger.error("获得库管判断信息失败:"+waybillCode, e);
			}
			if (osi == null)
				resultStr =  "获得库管判断信息失败";
			else
				resultStr = osi.judge();
		}
		return java.net.URLEncoder.encode(resultStr, "UTF-8");
	}

	private OrderStockInfo getKeyKuguanInfo(Long waybillCode) {

		// 1.定义订单类型、库管信息对应的字段
		String orderType = null;
		String fangshi = null;
		String fenlei = null;
		String qita = null;
		String qitafangshi = null;

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
		KuGuanDomain stockPageResp = queryKuguanDomainByWaybillCode(String.valueOf(waybillCode));

		if (stockPageResp != null) {
			fangshi = stockPageResp.getLblWay();
			fenlei = stockPageResp.getLblType();
			qita = stockPageResp.getLblOther();
			qitafangshi = stockPageResp.getLblOtherWay();
		}

		// 3. 生成订单对象自行判断
		OrderStockInfo osi = new OrderStockInfo(String.valueOf(waybillCode),
				orderType, fangshi, fenlei, qita, qitafangshi);
		return osi;
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

    private KuGuanDomain queryKuguanDomainByWaybillCode(String waybillCode){
        if(uccPropertyConfiguration.isChuguanNewInterfaceQuerySwitch()){
            return chuguanExportManager.queryByWaybillCode(waybillCode);
        }
        return stockExportManager.queryByWaybillCode(waybillCode);
    }
}
