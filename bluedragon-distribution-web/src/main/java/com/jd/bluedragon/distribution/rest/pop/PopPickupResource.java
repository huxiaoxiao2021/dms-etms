package com.jd.bluedragon.distribution.rest.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.api.response.PopPickupResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ObjectMapHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The boundary of pop request
 * 
 * @author wangzichen
 * 
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PopPickupResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	WaybillPackageBarcodeService waybillPackageBarcodeService;

	@Autowired
	BaseService baseService;

	@Autowired
	private PopPickupService popPickupService;

	/**
	 * 通过运单号获取包裹数量
	 * 
	 * @param popPickupRequest
	 * @return
	 */

	@POST
	@Path("/pop/pickUp/getPackageNumbers")
	@SuppressWarnings("rawtypes")
	public PopPickupResponse waybillNumbers(PopPickupRequest popPickupRequest) {
		if (null == popPickupRequest || null == popPickupRequest.getWaybillCode()) {
			return null;
		} else {
			Integer pakcageNumbers = waybillPackageBarcodeService.getPackageNumbersByWaybill(popPickupRequest
					.getWaybillCode());
			if (null == pakcageNumbers || pakcageNumbers <= 0) {
				return new PopPickupResponse(PopPickupResponse.CODE_NOT_MATCH, PopPickupResponse.MESSAGE_NOT_MATCH);
			}
			PopPickupResponse popPickupResponse = new PopPickupResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			popPickupResponse.setPackageNumbers(pakcageNumbers);
			return popPickupResponse;
		}
	}

	/**
	 * 根据pop id获取pop商家名称
	 * 
	 * @param popPickupRequest
	 * @return
	 */

	@POST
	@Path("/pop/pickUp/getPopBusinessName")
	@SuppressWarnings("rawtypes")
	public PopPickupResponse getPopBusinessNameByCode(PopPickupRequest popPickupRequest) {
		if (null == popPickupRequest || null == popPickupRequest.getPopBusinessCode()) {
			return null;
		} else {
			String popBusinessName = baseService.getPopBusinessNameByCode(popPickupRequest.getPopBusinessCode());
			if (StringUtils.isBlank(popBusinessName)) {
				return new PopPickupResponse(PopPickupResponse.CODE_NOT_MATCH, PopPickupResponse.MESSAGE_NOT_MATCH);
			}
			PopPickupResponse PopPickupResponse = new PopPickupResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			PopPickupResponse.setPopBusinessCode(popPickupRequest.getPopBusinessCode());
			PopPickupResponse.setPopBusinessName(popBusinessName);
			return PopPickupResponse;
		}
	}

	/**
	 * 按条件查询POP上门接货交接清单
	 * 
	 * @param popPickupQuery
	 *            分页对象
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Path("/popPickup/queryPopPickupList")
	public PopPickupResponse<PopPickupQuery> queryPopPickupList(PopPickupQuery popPickupQuery) {
		Boolean checkParam = Boolean.FALSE;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			if (popPickupQuery == null) {
				checkParam = Boolean.TRUE;
			}
			if (popPickupQuery == null || popPickupQuery.getCreateSiteCode() == 0
					|| popPickupQuery.getStartTime() == null || popPickupQuery.getEndTime() == null) {
				checkParam = Boolean.TRUE;
			}
			
			if (popPickupQuery != null) {
				// 设置默认类型为驻厂接货
				popPickupQuery.setPickupType(Constants.POP_QUEUE_PICKUP);
				paramMap = ObjectMapHelper.makeObject2Map(popPickupQuery);
			}
		} catch (Exception e) {
			checkParam = Boolean.TRUE;
		}

		if (Boolean.TRUE.equals(checkParam)) {
			this.log.warn("按条件查询POP上门接货交接清单 --> 传入参数非法");
			return new PopPickupResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR, popPickupQuery);
		}

		try {
			Pager pager = popPickupQuery.getPager();

			// 设置分页参数
			if (pager == null) {
				pager = new Pager(Pager.DEFAULT_PAGE_NO, Pager.DEFAULT_POP_PAGE_SIZE);
			} else {
				if (pager.getPageNo() == null || pager.getPageNo() <= 0) {
					pager.setPageNo(Pager.DEFAULT_PAGE_NO);
				}
				if (pager.getPageSize() == null || pager.getPageSize() <= 0
						|| pager.getPageSize() > Pager.DEFAULT_POP_PAGE_SIZE) {
					pager.setPageSize(Pager.DEFAULT_POP_PAGE_SIZE);
				}
			}

			pager = new Pager(pager.getPageNo(), pager.getPageSize());

			popPickupQuery.setPager(pager);

			paramMap.put("pageNo", pager.getPageNo());
			paramMap.put("pageSize", pager.getPageSize());
			paramMap.put("startIndex", pager.getStartIndex());
			paramMap.put("endIndex", pager.getEndIndex());

			List<PopPickup> popPickups = null;

			if (!paramMap.isEmpty()) {
				// 获取总数量
				int totalSize = this.popPickupService.findPopPickupTotalCount(paramMap);
				this.log.info("按条件查询POP上门接货交接清单 --> 获取总数量为：{}", totalSize);
				if (totalSize > 0) {
					pager.setTotalSize(totalSize);
					popPickups = this.popPickupService.findPopPickupList(paramMap);
					pager.setData(popPickups);
				} else {
					this.log.info("按条件查询POP上门接货交接清单 --> paramMap：{}, 调用服务成功，数据为空",paramMap);
					return new PopPickupResponse(PopPickupResponse.CODE_OK_NULL, PopPickupResponse.MESSAGE_OK_NULL,
							popPickupQuery);
				}
			}
			return new PopPickupResponse(PopPickupResponse.CODE_OK, PopPickupResponse.MESSAGE_OK, popPickupQuery);

		} catch (Exception e) {
			this.log.error("按条件查询POP上门接货交接清单异常：", e);
			return new PopPickupResponse(PopPickupResponse.CODE_SERVICE_ERROR, PopPickupResponse.MESSAGE_SERVICE_ERROR,
					popPickupQuery);
		}
	}

	/**
	 * 根据pop id获取pop商家名称
	 * 
	 * @param popPickupRequest
	 * @return
	 */
	@POST
	@Path("/pop/popPickup")
	@SuppressWarnings("rawtypes")
	public PopPickupResponse popPickup(PopPickupRequest popPickupRequest) {
		if (null == popPickupRequest/*
									 * || null ==
									 * popPickupRequest.getPopBusinessCode()
									 */) {
			return null;
		}

		try {
			/*包裹号处理*/
			if(popPickupRequest.getWaybillCode()==null){
				popPickupRequest.setWaybillCode(WaybillUtil.getWaybillCode(popPickupRequest.getPackageBarcode()));
			}else if(popPickupRequest.getPackageBarcode()==null){
				popPickupRequest.setPackageBarcode(popPickupRequest.getWaybillCode());
			}
			
			popPickupService.pushPopPickupRequest(popPickupRequest);
			PopPickupResponse popPickupResponse = new PopPickupResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			return popPickupResponse;
		} catch (Exception e) {
			PopPickupResponse popPickupResponse = new PopPickupResponse(JdResponse.CODE_SERVICE_ERROR, e.getMessage());
			return popPickupResponse;
		}
	}

	public static class PopPickupQuery {
		/**
		 * 操作站点编号
		 */
		private Integer createSiteCode;

		/**
		 * 运单号
		 */
		private String waybillCode;
		
		private String packageBarcode;

		/**
		 * 运单类型
		 */
		private Integer waybillType;

		/**
		 * 开始时间
		 */
		private Date startTime;

		/**
		 * 结束时间
		 */
		private Date endTime;

		/**
		 * POP商家ID
		 */
		private String popBusinessCode;

		/**
		 * POP商家名称
		 */
		private String popBusinessName;

		/* 操作人code */
		private Integer createUserCode;

		/* 操作人 */
		private String createUser;

		/**
		 * 接货类型： 驻厂接货：5
		 */
		private Integer pickupType;

		private Pager<List<PopPickup>> pager;

		public Integer getCreateSiteCode() {
			return createSiteCode;
		}

		public void setCreateSiteCode(Integer createSiteCode) {
			this.createSiteCode = createSiteCode;
		}

		public Integer getWaybillType() {
			return waybillType;
		}

		public void setWaybillType(Integer waybillType) {
			this.waybillType = waybillType;
		}

		public Date getStartTime() {
			return startTime != null ? (Date) startTime.clone() : null;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime != null ? (Date) startTime.clone() : null;
		}

		public Date getEndTime() {
			return endTime != null ? (Date) endTime.clone() : null;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime != null ? (Date) endTime.clone() : null;
		}

		public Pager<List<PopPickup>> getPager() {
			return pager;
		}

		public void setPager(Pager<List<PopPickup>> pager) {
			this.pager = pager;
		}

		public Integer getCreateUserCode() {
			return createUserCode;
		}

		public void setCreateUserCode(Integer createUserCode) {
			this.createUserCode = createUserCode;
		}

		public String getCreateUser() {
			return createUser;
		}

		public void setCreateUser(String createUser) {
			this.createUser = createUser;
		}

		public String getWaybillCode() {
			return waybillCode;
		}

		public void setWaybillCode(String waybillCode) {
			this.waybillCode = waybillCode;
		}

		public String getPopBusinessCode() {
			return popBusinessCode;
		}

		public void setPopBusinessCode(String popBusinessCode) {
			this.popBusinessCode = popBusinessCode;
		}

		public String getPopBusinessName() {
			return popBusinessName;
		}

		public void setPopBusinessName(String popBusinessName) {
			this.popBusinessName = popBusinessName;
		}

		public Integer getPickupType() {
			return pickupType;
		}

		public void setPickupType(Integer pickupType) {
			this.pickupType = pickupType;
		}

		public String getPackageBarcode() {
			return packageBarcode;
		}

		public void setPackageBarcode(String packageBarcode) {
			this.packageBarcode = packageBarcode;
		}
	}
}
