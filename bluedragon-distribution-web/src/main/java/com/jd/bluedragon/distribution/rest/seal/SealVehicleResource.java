package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SealVehicleResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final int SEAL_CODE_UNSAME = 20; 
	
	@Autowired
	private SealVehicleService sealVehicleService;

	@GET
	@Path("/seal/vehicle/{sealCode}")
	public SealVehicleResponse findSealByCode(
			@PathParam("sealCode") String sealCode) {
		if (StringUtils.isBlank(sealCode)) {
			this.log.warn("SealVehicleResponse findSealByCode --> 传入参数非法");
			return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			this.log.info("SealVehicleResponse findSealByCode --> sealCode is {}", sealCode);
			SealVehicle sealVehicle = this.sealVehicleService
					.findBySealCode(sealCode);
			if (sealVehicle != null) {
				return SealVehicle.toSealVehicleResponse(sealVehicle);
			} else {
				this.log.info("SealVehicleResponse findSealByCode --> 根据封车号【{}】查询无数据！",sealCode);
				return new SealVehicleResponse(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			this.log.error("SealVehicleResponse findSealByCode --> 根据封车号【{}】获取封车信息 --> 调用服务异常：",sealCode, e);
			return new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}
	}

	@POST
	@Path("/seal/vehicle")
	public SealVehicleResponse add(SealVehicleRequest request) {
		if (request == null || StringUtils.isBlank(request.getSealCode())
				|| StringUtils.isBlank(request.getVehicleCode())
				|| request.getSiteCode() == null
				|| request.getUserCode() == null
				|| StringUtils.isBlank(request.getUserName())) {
			this.log.warn("SealVehicleResource add --> 传入参数非法");
			return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		this.log.info("SealVehicleResource add --> sealCode is [{}] vehicleCode is[{}]",request.getSealCode(), request.getVehicleCode());
		SealVehicle sealVehicle = SealVehicle.toSealVehicle(request);
		if (Constants.RESULT_SUCCESS == this.sealVehicleService
				.addSealVehicle(sealVehicle)) {
			return new SealVehicleResponse(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK);
		} else {
			return new SealVehicleResponse(2006, "请使用新封签");
		}

	}
	
	/**
	 * 一车一单的封车功能,添加了发货批次号,体积,重量,件数四个字段
	 * 
	 */
	@POST
	@Path("/seal/vehicle2")
	public SealVehicleResponse add2(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null || StringUtils.isBlank(request.getSealCode())
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())
					|| StringUtils.isBlank(request.getSendCode())) {
				log.warn("SealVehicleResource add --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			SealVehicle sealVehicle = SealVehicle.toSealVehicle3(request);
			if (Constants.RESULT_SUCCESS == this.sealVehicleService.addSealVehicle2(sealVehicle)) {
				sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} else {
				sealVehicleResponse = new SealVehicleResponse(2006, "请使用新封签");
			}
		}catch(Exception e){
			this.log.error("SealVehicleResource.add2-error", e);
		}
		return sealVehicleResponse;
	}

	@PUT
	@Path("/seal/vehicle")
	public SealVehicleResponse update(SealVehicleRequest request) {
		if (request == null || StringUtils.isBlank(request.getSealCode())
				|| StringUtils.isBlank(request.getVehicleCode())
				|| request.getSiteCode() == null
				|| request.getUserCode() == null
				|| StringUtils.isBlank(request.getUserName())) {
			this.log.warn("SealVehicleResource update --> 传入参数非法");
			return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		SealVehicle sealVehicle = SealVehicle.toSealVehicle2(request);
		if (Constants.RESULT_SUCCESS == this.sealVehicleService
				.updateSealVehicle(sealVehicle)) {
			return new SealVehicleResponse(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK);
		} else {
			return new SealVehicleResponse(2006, "此封签异常,请核实");
		}

	}

	@PUT
	@Path("/seal/vehicle2")
	public SealVehicleResponse update2(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null || StringUtils.isBlank(request.getSealCode())
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())
					|| StringUtils.isBlank(request.getSendCode())) {
				this.log.warn("SealVehicleResource update --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			SealVehicle sealVehicle = SealVehicle.toSealVehicle4(request);
			int result = this.sealVehicleService.updateSealVehicle2(sealVehicle);
			if (Constants.RESULT_SUCCESS == result) {
				return new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} else if(SealVehicleResource.SEAL_CODE_UNSAME == result) {
				return new SealVehicleResponse(2006, "当前批次号下,封车号不一致,请核实");
			} else {
				return new SealVehicleResponse(2006, "此封签异常,请核实");
			}
		}catch(Exception e){
			this.log.error("SealVehicleResource.update2-error", e);
		}
		return sealVehicleResponse;
	}

	/******************* 封车与解封车:封车号与批次号多对多关系版本 [开始]  ***************************/
	
	/**
	 * 一车一单的封车功能,添加了发货批次号,体积,重量,件数四个字段
	 */
	@POST
	@Path("/seal/vehicle3")
	public SealVehicleResponse add3(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null || request.getSealCodeList().size() < 1
					|| request.getSendCodeList().size() < 1
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())) {
				this.log.warn("SealVehicleResource add --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			List<SealVehicle> sealVehicleList = SealVehicle.toSomeSealVehicle(request);
			if (this.sealVehicleService.addSealVehicle3(sealVehicleList)) {
				sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} else {
				sealVehicleResponse = new SealVehicleResponse(2006, "请使用新封签");
			}
		}catch(Exception e){
			this.log.error("SealVehicleResource.add3-error", e);
		}
		return sealVehicleResponse;
	}
	
	@PUT
	@Path("/seal/vehicle3")
	public SealVehicleResponse update3(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null || request.getSealCodeList().size() < 1
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSiteCode() == null
					|| request.getUserCode() == null
					|| StringUtils.isBlank(request.getUserName())) {
				this.log.warn("SealVehicleResource update --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			SealVehicle sealVehicle = SealVehicle.toSealVehicle4(request);   
			if (this.sealVehicleService.updateSealVehicle3(sealVehicle, request.getSealCodes())) {
				return new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			} else {
				return new SealVehicleResponse(2006, "车牌号或封签异常,请核实");
			}
		}catch(Exception e){
			this.log.error("SealVehicleResource.update3-error", e);
		}
		return sealVehicleResponse;
	}
	
	@GET
	@Path("/seal/{vehicleCode}")   
	public SealVehicleResponse findByVehicleCode(@PathParam("vehicleCode") String vehicleCode) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		if (StringUtils.isBlank(vehicleCode)) {
			this.log.warn("SealVehicleResponse findByVehicleCode --> 传入参数非法");
			return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
		}
		try{
			List<SealVehicle> sealVehicleList =  sealVehicleService.findByVehicleCode(vehicleCode);
			if(sealVehicleList == null || sealVehicleList.size() < 1){
				return new SealVehicleResponse(JdResponse.CODE_OK_NULL, JdResponse.MESSAGE_OK_NULL);
			}
			sealVehicleResponse.setSealCodes(getSealCodes(sealVehicleList));
			sealVehicleResponse.setCode(JdResponse.CODE_OK);
			sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
		}catch(Exception e){
			this.log.error("SealVehicleResource.findByVehicleCode-error", e);
		}
		return sealVehicleResponse;
	}
	
	public static String getSealCodes(List<SealVehicle> sealVehicleList){
		String sealCodes = "";
		boolean isFirst = true;
		Set<String> sealCodeSet = new HashSet<String>();
		for(SealVehicle sv : sealVehicleList){
			sealCodeSet.add(sv.getCode());
		}
		for(String str : sealCodeSet){
			if(isFirst){
				sealCodes += str;
				isFirst = false;
			}else{
				sealCodes += "," + str;
			}
		}
		return sealCodes;
	}
	
	/******************* 封车与解封车:封车号与批次号多对多关系版本 [结束]  ***************************/
	
	@POST
	@Path("/seal/vehicle/cancel")
	public SealVehicleResponse cancel(SealVehicleRequest request) {
		SealVehicleResponse sealVehicleResponse = new SealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
		try{
			if (request == null
					|| StringUtils.isBlank(request.getVehicleCode())
					|| request.getSealCode() == null
					|| request.getSendCode() == null) {
				this.log.warn("SealVehicleResource cancel --> 传入参数非法");
				return new SealVehicleResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			}
			SealVehicle sealVehicle = SealVehicle.toSealVehicle3(request); 
			
			SealVehicleResponse response = this.sealVehicleService.cancelSealVehicle(sealVehicle);
			
			return response;
		}catch(Exception e){
			this.log.error("SealVehicleResource.cancel-error", e);
		}
		return sealVehicleResponse;
	}
}
