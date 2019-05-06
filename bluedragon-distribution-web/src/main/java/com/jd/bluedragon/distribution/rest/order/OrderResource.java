package com.jd.bluedragon.distribution.rest.order;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.OrderPackage;
import com.jd.bluedragon.distribution.api.response.OrderResponse;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrderResource {

	private static final Integer CODE_WAYBILL_NOE_FOUND = 404;
	private static final String MESSAGE_WAYBILL_NOE_FOUND = "运单不存在";

	private static final String DEFAUIT_PACKAGE_WEIGHT = "0.0";

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private OrderWebService orderWebService;

	@GET
	@Path("/order")
	public OrderResponse getOrderResponse(@QueryParam("packageCode") String packageCode) {
		Boolean isIncludePackage = WaybillUtil.isWaybillCode(packageCode);
		BigWaybillDto waybillDto = this.waybillService.getWaybill(packageCode);
		if (waybillDto == null || waybillDto.getWaybill() == null || waybillDto.getWaybillState() == null) {
			return new OrderResponse(OrderResource.CODE_WAYBILL_NOE_FOUND, OrderResource.MESSAGE_WAYBILL_NOE_FOUND);
		}

		Waybill waybill = waybillDto.getWaybill();
		BaseStaffSiteOrgDto receiveSite = NumberHelper.isPositiveNumber(waybill.getOldSiteId()) ? this.baseMajorManager
				.getBaseSiteBySiteId(waybill.getOldSiteId()) : null;
		BaseStaffSiteOrgDto transferSite = NumberHelper.isPositiveNumber(waybill.getTransferStationId()) ? this.baseMajorManager
				.getBaseSiteBySiteId(waybill.getTransferStationId()) : null;

		OrderResponse response = new OrderResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setAddress(waybill.getReceiverAddress());
		response.setReassignAddress(waybill.getNewRecAddr());
		response.setCky2(waybillDto.getWaybillState().getCky2());
		response.setMobile(waybill.getReceiverMobile());
		response.setPackageQuantity(waybill.getGoodNumber());
		response.setPayment(waybill.getPayment());
		response.setWaybillCode(waybill.getWaybillCode());
		response.setWaybillType(waybill.getWaybillType());
		response.setSendPay(waybill.getSendPay());
		response.setSiteId(waybill.getOldSiteId());
		response.setSiteName(receiveSite != null ? receiveSite.getSiteName() : null);
		response.setTransferStationId(waybill.getTransferStationId());
		response.setTransferStationName(transferSite != null ? transferSite.getSiteName() : null);

		this.appendPackages(packageCode, isIncludePackage, waybillDto, response);
        response.setMobile(StringHelper.phoneEncrypt(response.getMobile()));
		return response;
	}

	private void appendPackages(String packageCode, Boolean isIncludePackage, BigWaybillDto waybillDto,
			OrderResponse response) {
		for (DeliveryPackageD waybillPackage : waybillDto.getPackageList()) {
			if (isIncludePackage || !isIncludePackage
					&& waybillPackage.getPackageBarcode().equalsIgnoreCase(packageCode)) {
				OrderPackage orderPackage = new OrderPackage();
				orderPackage.setPackageCode(waybillPackage.getPackageBarcode());
				orderPackage
						.setPackageWeight(waybillPackage.getAgainWeight() == null ? OrderResource.DEFAUIT_PACKAGE_WEIGHT
								: String.valueOf(waybillPackage.getAgainWeight()));
				response.addPackage(orderPackage);
			}
		}
	}

	@GET
	@Path("/order/{orderId}")
	public Order getOrder(@PathParam("orderId") long orderId) {
		return this.orderWebService.getOrder(orderId);
	}

	@GET
	@Path("/order/getHistoryOrder/{orderId}")
	public jd.oom.client.orderfile.Order getHistoryOrder(@PathParam("orderId") long orderId) {
		return this.orderWebService.getHistoryOrder(orderId);
	}
	
	@GET
	@Path("/waybill/product/{waybillCode}")
	public JdResponse getWaybillProducts(@PathParam("waybillCode") String waybillCode) {
		if (Strings.isNullOrEmpty(waybillCode) || Strings.isNullOrEmpty(waybillCode.trim())) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}
		
		BigWaybillDto waybill = this.waybillService.getWaybillProduct(waybillCode);
		if (waybill == null || waybill.getGoodsList() == null || waybill.getGoodsList().isEmpty()) {
			return new JdResponse(10001, "此运单无商品明细！");
		}
		
		List<Product> products = new ArrayList();
		for (Goods good : waybill.getGoodsList()) {
			Product product = new Product();
			product.setName(good.getGoodName());
			product.setPrice(BigDecimal.valueOf(Double.valueOf(good.getGoodPrice())));
			product.setProductId(String.valueOf(good.getGoodId()));
			product.setQuantity(good.getGoodCount());
			products.add(product);
		}

		return new JdResponse(JdResponse.CODE_OK, products.toString());
	}

	@GET
	@Path("/waybill/package/{waybillCode}")
	public JdResponse getWaybillPackages(@PathParam("waybillCode") String waybillCode) {
		if (Strings.isNullOrEmpty(waybillCode) || Strings.isNullOrEmpty(waybillCode.trim())) {
			return new JdResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}
		
		BigWaybillDto waybill = this.waybillService.getWaybill(waybillCode.trim());
		
		if (waybill == null || waybill.getPackageList() == null || waybill.getPackageList().isEmpty()) {
			return new JdResponse(10001, "此运单无包裹明细！");
		}
		
		Function<DeliveryPackageD, String> function = new Function<DeliveryPackageD, String>() {
			public String apply(DeliveryPackageD packageDetail) {
				return packageDetail.getPackageBarcode();
			}
		};
		String packageCodes = Joiner.on(",").skipNulls().join(Lists.transform(waybill.getPackageList(), function));

		return new JdResponse(JdResponse.CODE_OK, packageCodes); 
	}
	
	@GET
	@Path("/waybillStatus/{waybillCode}")
	public OrderResponse getOrderStatusResponse(@PathParam("waybillCode") String waybillCode) {
		BigWaybillDto waybillDto = this.waybillService.getWaybillState(waybillCode);
		if (waybillDto == null  || waybillDto.getWaybillState() == null) {
			return new OrderResponse(OrderResource.CODE_WAYBILL_NOE_FOUND, OrderResource.MESSAGE_WAYBILL_NOE_FOUND);
		}

		WaybillManageDomain domain = waybillDto.getWaybillState();
		Integer status = domain.getWaybillState();

		OrderResponse response = new OrderResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		response.setStatus(status);

		return response;
	}
}
