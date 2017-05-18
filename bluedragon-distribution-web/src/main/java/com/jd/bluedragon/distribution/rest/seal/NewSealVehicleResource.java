package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.request.SealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * create by zhanglei 2017-05-10
 *
 * 新版封车解封车
 *
 * 主要功能点
 * 1、封车：回传TMS发车信息（通过jsf接口）
 * 2、解封车：回传TMS解封车信息（通过jsf接口）
 * 3、获取待解封列表(通过jsf接口)
 *
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class NewSealVehicleResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SealVehicleService sealVehicleService;


	/**
	 * 封车功能
	 */
	@POST
	@Path("/new/vehicle/seal")
	public SealVehicleResponse add(NewSealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())) {
				this.logger.error("SealVehicleResource add --> 传入参数非法");
				sealVehicleResponse =  new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
		}catch(Exception e){
			this.logger.error("SealVehicleResource.add3-error", e);
		}
		return sealVehicleResponse;
	}

	/**
	 * 获取待解封信息
	 * */
	@POST
	@Path("/new/vehicle/findSealInfo")
	public SealVehicleResponse findSealInfo( String sealCode) {
		if (StringUtils.isBlank(sealCode)) {
			this.logger.error("SealVehicleResponse findSealByCode --> 传入参数非法");
			return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			this.logger
					.info("SealVehicleResponse findSealByCode --> sealCode is "
							+ sealCode);
			SealVehicle sealVehicle = this.sealVehicleService
					.findBySealCode(sealCode);
			if (sealVehicle != null) {
				return SealVehicle.toSealVehicleResponse(sealVehicle);
			} else {
				this.logger
						.info("SealVehicleResponse findSealByCode --> 根据封车号【 "
								+ sealCode + "】查询无数据！");
				return new SealVehicleResponse(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			this.logger.error("SealVehicleResponse findSealByCode --> 根据封车号【"
					+ sealCode + "】获取封车信息 --> 调用服务异常：", e);
			return new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}
	}


	/**
	 * 解封车功能
	 */
	@POST
	@Path("/new/vehicle/unseal")
	public SealVehicleResponse update(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null || request.getSealCodeList().size() < 1
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())) {
				this.logger.error("SealVehicleResource update --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			SealVehicle sealVehicle = SealVehicle.toSealVehicle4(request);   
			if (this.sealVehicleService.updateSealVehicle3(sealVehicle, request.getSealCodes())) {
				return new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} else {
				return new SealVehicleResponse(2006, "车牌号或封签异常,请核实");
			}
		}catch(Exception e){
			this.logger.error("SealVehicleResource.update3-error", e);
		}
		return sealVehicleResponse;
	}
}
