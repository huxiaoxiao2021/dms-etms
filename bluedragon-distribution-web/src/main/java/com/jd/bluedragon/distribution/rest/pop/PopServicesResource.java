package com.jd.bluedragon.distribution.rest.pop;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopServicesRequest;
import com.jd.bluedragon.distribution.api.response.PopJoinResponse;
import com.jd.bluedragon.distribution.api.response.PopServicesResponse;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.service.PopReceiveAbnormalService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.PropertiesHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午03:54:11
 * 
 *             验证POP商家是否可修改包裹数量
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class PopServicesResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PopReceiveAbnormalService popReceiveAbnormalService;

	@Autowired
	private PopPrintService popPrintService;

	/**
	 * 根据运单号查询包裹数量是否可以修改
	 * 
	 * @param popServicesRequest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/pop/checkModifyPackNum")
	public PopServicesResponse checkModifyPackNum(
			PopServicesRequest popServicesRequest) {
		if (popServicesRequest == null
				|| StringUtils.isBlank(popServicesRequest.getKey())
				|| !WaybillUtil.isWaybillCode(popServicesRequest
						.getWaybillCode())
				|| !popServicesRequest.getKey().equals(
						PropertiesHelper.newInstance().getValue(
								Constants.REST_KEY))) {
			this.logger.error("根据运单号查询包裹数量是否可以修改 --> 传入参数非法");
			return new PopServicesResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		String waybillCode = popServicesRequest.getWaybillCode();

		try {

			// 验证POP订单是否正在申请中
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("waybillCode", waybillCode);
			paramMap.put("mainType", "5");
			PopReceiveAbnormal tempAbnormal = this.popReceiveAbnormalService
					.findByMap(paramMap);

			if (tempAbnormal != null) {
				this.logger.error("根据运单号【" + waybillCode
						+ "】查询包裹数量是否可以修改 --> 已通过差异反馈提交");
				return new PopServicesResponse(
						PopServicesResponse.CODE_IS_ABNORMAL,
						PopServicesResponse.MESSAGE_IS_ABNORMAL);
			}

			PopPrint popPrint = this.popPrintService
					.findByWaybillCode(waybillCode);
			if (popPrint != null) {
				this.logger.error("根据运单号【" + waybillCode
						+ "】查询包裹数量是否可以修改 --> 订单已打印");
				return new PopServicesResponse(
						PopServicesResponse.CODE_IS_RECEIVE,
						PopServicesResponse.MESSAGE_IS_RECEIVE);
			}

			this.logger.info("根据运单号【" + waybillCode
					+ "】查询包裹数量是否可以修改 --> 正常，可以修改");
			return new PopServicesResponse(PopJoinResponse.CODE_OK,
					PopJoinResponse.MESSAGE_OK);

		} catch (Exception e) {
			this.logger.error("根据运单号【" + waybillCode + "】查询包裹数量是否可以修改异常：", e);
			return new PopServicesResponse(PopJoinResponse.CODE_SERVICE_ERROR,
					PopJoinResponse.MESSAGE_SERVICE_ERROR);
		}
	}
}
