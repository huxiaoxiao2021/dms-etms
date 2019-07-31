package com.jd.bluedragon.distribution.rest.reverse;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReverseSpareDto;
import com.jd.bluedragon.distribution.api.request.ReverseSpareRequest;
import com.jd.bluedragon.distribution.api.response.PopJoinResponse;
import com.jd.bluedragon.distribution.api.response.ReverseSpareResponse;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ReverseSpareResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseSpareService reverseSpareService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@GET
	@Path("/reverseSpare/getBySpareCode/{createSiteCode}/{spareCode}")
	public ReverseSpareResponse<ReverseSpareRequest> getBySpareCode(
			@PathParam("createSiteCode") Integer createSiteCode,
			@PathParam("spareCode") String spareCode) {
		if (StringUtils.isBlank(spareCode) || createSiteCode == null
				|| createSiteCode.equals(0)) {
			this.logger
					.error("ReverseSpareResource getBySpareCode--> 传入参数非法");
			return new ReverseSpareResponse<ReverseSpareRequest>(
					JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			Sorting sorting = new Sorting();
			sorting.setCreateSiteCode(createSiteCode);
			sorting.setPackageCode(spareCode);
			sorting.setType(Constants.BUSSINESS_TYPE_REVERSE);
			if (sorting != null) {
				sorting = this.reverseSpareService
						.querySortingBySpareCode(sorting);
				ReverseSpareRequest request = toReverseSpareRequest(sorting);
				ReverseSpare reverseSpare = this.reverseSpareService
						.queryBySpareCode(spareCode);
				if (reverseSpare != null) {
					request.setData(Arrays
							.asList(toReverseSpareDto(reverseSpare)));
				}
				return new ReverseSpareResponse<ReverseSpareRequest>(
						ReverseSpareResponse.CODE_OK,
						ReverseSpareResponse.MESSAGE_OK, request);
			} else {
				return new ReverseSpareResponse<ReverseSpareRequest>(
						ReverseSpareResponse.CODE_OK_NULL,
						ReverseSpareResponse.MESSAGE_OK_NULL_SORTING);
			}

		} catch (Exception e) {
			this.logger.error("ReverseSpareResource getBySpareCode异常：", e);
			return new ReverseSpareResponse<ReverseSpareRequest>(
					PopJoinResponse.CODE_SERVICE_ERROR,
					PopJoinResponse.MESSAGE_SERVICE_ERROR);
		}
	}

	private ReverseSpareRequest toReverseSpareRequest(Sorting sorting) {
		if (sorting == null) {
			this.logger
					.debug("ReverseRejectSpareTask toReverseSpareRequest--> 传入分拣对象为空");
			return null;
		}

		ReverseSpareRequest request = new ReverseSpareRequest();
		request.setSiteCode(sorting.getCreateSiteCode());
		request.setSiteName(sorting.getCreateSiteName());
		Integer receiveSiteCode = sorting.getReceiveSiteCode();
		request.setReceiveSiteCode(receiveSiteCode);
		try {
			this.logger
					.info("ReverseRejectSpareTask toReverseSpareRequest--> 获取包裹分拣目的站点名称开始，站点号【"
							+ receiveSiteCode + "】");
			BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager
					.getBaseSiteBySiteId(receiveSiteCode);
			if (baseStaffSiteOrgDto != null) {
				request.setReceiveSiteName(baseStaffSiteOrgDto.getSiteName());
				this.logger
						.info("ReverseRejectSpareTask toReverseSpareRequest--> 获取包裹分拣目的站点名称，站点号【"
								+ receiveSiteCode
								+ "-"
								+ baseStaffSiteOrgDto.getSiteName() + "】,结束");
			} else {
				this.logger
						.error("ReverseRejectSpareTask toReverseSpareRequest--> 获取包裹分拣目的站点名称为空，站点号【"
								+ receiveSiteCode + "】");
			}
		} catch (Exception e) {
			this.logger.error(
					"ReverseRejectSpareTask toReverseSpareRequest--> 根据目的站点Code【"
							+ receiveSiteCode + "】 获取站点名称接口异常", e);
		}
		request.setUserName(sorting.getUpdateUser());
		request.setUserCode(sorting.getUpdateUserCode());
		request.setIsCancel(Sorting.IS_NOT_CANCEL);
		request.setBusinessType(sorting.getType());
		request.setOperateTime(DateHelper.formatDate(sorting.getCreateTime(),
				Constants.DATE_TIME_FORMAT));
		request.setBoxCode(sorting.getBoxCode());
		request.setWaybillCode(sorting.getWaybillCode());
		request.setSpareReason(sorting.getSpareReason());
		return request;
	}

	private ReverseSpareDto toReverseSpareDto(ReverseSpare reverseSpare) {
		if (reverseSpare == null
				|| StringUtils.isBlank(reverseSpare.getSpareCode())) {
			this.logger
					.debug("ReverseRejectSpareTask toReverseSpareDto-->传入对象或备件条码为空");
			return null;
		}
		ReverseSpareDto spareDto = new ReverseSpareDto();
		spareDto.setSpareCode(reverseSpare.getSpareCode());
		spareDto.setProductId(reverseSpare.getProductId());
		spareDto.setProductCode(reverseSpare.getProductCode());
		spareDto.setProductName(reverseSpare.getProductName());
		spareDto.setProductPrice(reverseSpare.getProductPrice());
		spareDto.setArrtCode1(reverseSpare.getArrtCode1());
		spareDto.setArrtDesc1(reverseSpare.getArrtDesc1());
		spareDto.setArrtCode2(reverseSpare.getArrtCode2());
		spareDto.setArrtDesc2(reverseSpare.getArrtDesc2());
		spareDto.setArrtCode3(reverseSpare.getArrtCode3());
		spareDto.setArrtDesc3(reverseSpare.getArrtDesc3());
		spareDto.setArrtCode4(reverseSpare.getArrtCode4());
		spareDto.setArrtDesc4(reverseSpare.getArrtDesc4());
		return spareDto;
	}

	public static class DeliveryVO {

		private Integer orgId;

		private Integer storeId;

		private Integer num;

		public DeliveryVO() {

		}

		public DeliveryVO(Integer orgId, Integer storeId, Integer num) {
			this.orgId = orgId;
			this.storeId = storeId;
			this.num = num;
		}

		public Integer getOrgId() {
			return orgId;
		}

		public void setOrgId(Integer orgId) {
			this.orgId = orgId;
		}

		public Integer getStoreId() {
			return storeId;
		}

		public void setStoreId(Integer storeId) {
			this.storeId = storeId;
		}

		public Integer getNum() {
			return num;
		}

		public void setNum(Integer num) {
			this.num = num;
		}
	}
}
