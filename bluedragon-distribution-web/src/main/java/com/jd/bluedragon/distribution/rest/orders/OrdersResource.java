package com.jd.bluedragon.distribution.rest.orders;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.send.domain.OrderEntityResponse;
import com.jd.bluedragon.distribution.send.domain.OrdersEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.sorting.domain.OrdersDetailEntity;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.DateHelper;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrdersResource {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	DeliveryService deliveryService;
	
	@Autowired
	SortingService sortingService;
	
	@GET
	@Path("/qinglong/orders/details")
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.OrdersResource.getOrdersDetails", mState = JProEnum.TP)
	public OrderDetailEntityResponse getOrdersDetails(@QueryParam("boxCode") String boxCode,
			@QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime,
			@QueryParam("createSiteCode") String createSiteCode,
			@QueryParam("receiveSiteCode") String receiveSiteCode) {
		this.logger.info("getOrdersDetails开始验证箱号信息");
		
		this.logger.info("boxCode is " + boxCode);
		this.logger.info("startTime is " + startTime);
		this.logger.info("endTime is " + endTime);
		this.logger.info("createSiteCode is " + createSiteCode);
		this.logger.info("receiveSiteCode is " + receiveSiteCode);
		
		if (boxCode == null || createSiteCode == null || createSiteCode.isEmpty()
				|| startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
			return new OrderDetailEntityResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		
		Sorting param = new Sorting();
		param.setBoxCode(boxCode);
		param.setCreateTime(DateHelper.parseDateTime(startTime));
		param.setUpdateTime(DateHelper.parseDateTime(endTime));
		param.setCreateSiteCode(Integer.parseInt(createSiteCode));
		param.setReceiveSiteCode(Integer.parseInt(receiveSiteCode));
		
		List<Sorting> sortings = this.sortingService.findOrderDetail(param);
		
		List<OrdersDetailEntity> data = new ArrayList<OrdersDetailEntity>();
		for (Sorting sorting : sortings) {
			OrdersDetailEntity order = new OrdersDetailEntity();
			order.setBoxCode(sorting.getBoxCode());
			order.setPackNo(sorting.getPackageCode());
			order.setSiteCode(String.valueOf(sorting.getReceiveSiteCode()));
			order.setSortingTime(DateHelper.formatDateTime(sorting.getOperateTime()));
			order.setUserName(sorting.getCreateUser());
			order.setWaybill(sorting.getWaybillCode());
			data.add(order);
		}
		
		this.logger.info("结束验证箱号信息");
		
		return new OrderDetailEntityResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, data);
	}
	
	@GET
	@Path("/qinglong/orders")
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.OrdersResource.getOrders", mState = JProEnum.TP)
    public OrderEntityResponse getOrders(@QueryParam("type") String type,
			@QueryParam("startTime") String startTime,
			@QueryParam("createSiteCode") String createSiteCode,
			@QueryParam("receiveSiteCode") String receiveSiteCode) {
		this.logger.info("getOrders开始验证箱号信息");
		this.logger.info("startTime is " + startTime);
		this.logger.info("type is " + type);
		this.logger.info("createSiteCode is " + createSiteCode);
		this.logger.info("receiveSiteCode is " + receiveSiteCode);

		if (type == null || startTime == null || createSiteCode == null) {
			return new OrderEntityResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR, null);
		}
		
		SendDetail sendDatail = new SendDetail();
		sendDatail.setOperateTime(DateHelper.parseDateTime(startTime));
		sendDatail.setCreateSiteCode(Integer.parseInt(createSiteCode));

		if (receiveSiteCode != null && !receiveSiteCode.isEmpty()) {
			sendDatail.setReceiveSiteCode(Integer.parseInt(receiveSiteCode));
		}
		
		Sorting sorting = new Sorting();
		sorting.setCreateTime(DateHelper.parseDateTime(startTime));
		sorting.setCreateSiteCode(Integer.parseInt(createSiteCode));

		if (receiveSiteCode != null && !receiveSiteCode.isEmpty()) {
			sorting.setReceiveSiteCode(Integer.parseInt(receiveSiteCode));
		}
		
		if (type.equals("1")) {
			List<Sorting> tOrderList = this.sortingService.findOrderDetail(sorting);
			List<OrdersEntity> data = new ArrayList<OrdersEntity>();
			
			if (tOrderList != null && !tOrderList.isEmpty()) {
				
				for (Sorting sort : tOrderList) {
					OrdersEntity order = new OrdersEntity();
					order.setPackNo(sort.getPackageCode());
					data.add(order);
				}
			}
			this.logger.info("结束验证箱号信息");
			return new OrderEntityResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, data);
		} else if (type.equals("2")) {
			List<SendDetail> tOrderList = this.deliveryService.findOrder(sendDatail);
			List<OrdersEntity> data = new ArrayList<OrdersEntity>();
			
			if (tOrderList != null && !tOrderList.isEmpty()) {
				
				for (SendDetail send : tOrderList) {
					OrdersEntity order = new OrdersEntity();
					order.setPackNo(send.getPackageBarcode());
					data.add(order);
				}
			}
			this.logger.info("结束验证箱号信息");
			return new OrderEntityResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, data);
		} else {
			return new OrderEntityResponse(JdResponse.CODE_NOT_FOUND,
					JdResponse.MESSAGE_SERVICE_ERROR, null);
		}
	}
}
