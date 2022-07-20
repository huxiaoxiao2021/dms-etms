package com.jd.bluedragon.core.jsf.waybill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.reverse.domain.DmsPackageDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillAddress;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.cp.wbms.client.api.ReverseWaybillApi;
import com.jd.cp.wbms.client.dto.ConsigneeDto;
import com.jd.cp.wbms.client.dto.ReverseWaybillRequest;
import com.jd.cp.wbms.client.dto.SubmitWaybillResponse;
import com.jd.cp.wbms.client.dto.WaybillConsigneeDto;
import com.jd.cp.wbms.client.dto.WaybillConsignorDto;
import com.jd.cp.wbms.client.dto.WaybillPackageDto;
import com.jd.cp.wbms.client.dto.WbmsApiResult;
import com.jd.cp.wbms.client.dto.WbmsRequestProfile;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import com.jd.etms.receive.api.saf.GrossReturnSaf;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.business.api.BackAddressInfoApi;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.receive.api.dto.OrderMsgDTO;
import com.jd.ql.dms.receive.api.jsf.GetOrderMsgServiceJsf;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 外单 jsf接口 包装类
 */
@Service("waybillReverseManager")
public class WaybillReverseManagerImpl implements WaybillReverseManager {
    private final Logger log = LoggerFactory.getLogger(WaybillReverseManagerImpl.class);
    
    ////退货方式 0 - 按照商家配置 1 - 退库房 2 - 退寄件人 3 - 退备件库 4 - 物流损退备件库 5 - 退指定地址
    private static final int RETURN_TYPE_0 = 0;
    private static final int RETURN_TYPE_1 = 1;
    private static final int RETURN_TYPE_2 = 2;
    private static final int RETURN_TYPE_3 = 3;
    private static final int RETURN_TYPE_4 = 4;
    private static final int RETURN_TYPE_5 = 5;
    /**
     * 计费-2-不计费
     */
    public static final int CHARGE_TYPE_2 = 2;
    /**
     * 代收货款-1
     */
    private static final Integer PAYMENT_TYPE_1 = 1;
    /**
     * 代收货款-0
     */
    private static final Integer PAYMENT_TYPE_0 = 0;
    /**
     * 标识逆向业务
     */
    private static final String BIZ_REVERSE = "REVERSE";
    /**
     * 运单类型-1
     */
    private static final Integer WAYBILL_TYPE_1 = 1;
    /**
     * 操作来源-2-分拣中心
     */
    public static final Integer SORT_CENTER = 2;
    
    private static final String UMP_KEY_WBMS_PREFIX = "dmsWeb.jsf.client.wbms.reverseWaybillApi";

    private static final String UMP_KEY_RECEIVE_PREFIX = "dmsWeb.jsf.client.receive.";
    
    @Autowired
    private LogEngine logEngine;

    @Autowired
    @Qualifier("obcsManager")
    private OBCSManager obcsManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier("backAddressInfoApi")
    private BackAddressInfoApi backAddressInfoApi;
    @Autowired
    private LDOPManager lDOPManager;
    @Autowired
    private GetOrderMsgServiceJsf getOrderMsgServiceJsf;
	/**
     * 接货中心换单接口
     */
    @Autowired
    private GrossReturnSaf grossReturnSaf;
    
    @Autowired
    private ReverseWaybillApi reverseWaybillApi;
    @Autowired
    private WaybillCommonService waybillCommonService;
	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;
    @Autowired
    private WaybillCancelService waybillCancelService;
    /**
     * 二次换单限制次数
     */
	@Value("${beans.WaybillReverseManagerImpl.twiceExchangeMaxTimes:3}")
    private int twiceExchangeMaxTimes;
	
    /**
     * wbmsRequestProfile配置信息-systemSource
     */
	@Value("${dms.wbmsRequestProfile.systemSource}")
    private String systemSource;
    /**
     * wbmsRequestProfile配置信息-tenantId
     */
	@Value("${dms.wbmsRequestProfile.tenantId}")
    private String tenantId;
    /**
     * wbmsRequestProfile配置信息-appSource
     */
	@Value("${dms.wbmsRequestProfile.appSource}")
    private String appSource;
    /**
     * wbmsRequestProfile配置信息-token
     */
	@Value("${dms.wbmsRequestProfile.token}")
    private String token;
    /**
     * 触发外单逆向换单提交接口
     * @param waybillReverseDTO
     * @return
     */
    public boolean waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,JdResponse<Boolean> rest){
        if(needUseNewReverseApi(dmsWaybillReverseDTO.getWaybillCode())) {
        	return this.waybillReverseNew(dmsWaybillReverseDTO, rest);
        }else {
        	return lDOPManager.waybillReverse(dmsWaybillReverseDTO, rest);
        }
    }
    private boolean waybillReverseNew(DmsWaybillReverseDTO dmsWaybillReverseDTO,JdResponse<Boolean> rest){
    	JdResult<SubmitWaybillResponse> rpcResult = this.submitWaybill(dmsWaybillReverseDTO);
    	if(rpcResult != null
    			&& rpcResult.isSucceed()) {
    		return true;
    	}else {
    		rest.setMessage(rpcResult.getMessage());
    	}
    	return false;
    }
    @Override
    public DmsWaybillReverseResult waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
        if(needUseNewReverseApi(dmsWaybillReverseDTO.getWaybillCode())) {
        	return this.waybillReverseNew(dmsWaybillReverseDTO, errorMessage);
        }else {
        	return lDOPManager.waybillReverse(dmsWaybillReverseDTO, errorMessage);
        }
    }
    /**
     * 调用新逻辑
     * @param dmsWaybillReverseDTO
     * @param errorMessage
     * @return
     */
    private DmsWaybillReverseResult waybillReverseNew(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
    	JdResult<SubmitWaybillResponse> rpcResult = this.submitWaybill(dmsWaybillReverseDTO);
    	if(rpcResult != null
    			&& rpcResult.isSucceed()) {
    		return this.convertDmsWaybillReverseResult(rpcResult.getData());
    	}else {
    		errorMessage.append(rpcResult.getMessage());
    	}
    	return null;
    }
    /**
     * 结果对象转换
     * @param submitWaybillResponse
     * @return
     */
    private DmsWaybillReverseResult convertDmsWaybillReverseResult(SubmitWaybillResponse submitWaybillResponse) {
		if(submitWaybillResponse != null) {
			DmsWaybillReverseResult dmsWaybillReverseResult = new DmsWaybillReverseResult();
			dmsWaybillReverseResult.setWaybillCode(submitWaybillResponse.getWaybillCode());
			return dmsWaybillReverseResult;
		}
    	return null;
	}
    /**
     * 换单前获取原单信息接口
     */
	@Override
    public DmsWaybillReverseResponseDTO queryReverseWaybill(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
        if(needUseNewReverseApi(dmsWaybillReverseDTO.getWaybillCode())) {
        	return this.queryReverseWaybillNew(dmsWaybillReverseDTO, errorMessage);
        }else {
        	return lDOPManager.queryReverseWaybill(dmsWaybillReverseDTO, errorMessage);
        }
     }
    /**
     * 换单前获取原单信息接口-百川逻辑
     * @param dmsWaybillReverseDTO
     * @param errorMessage
     * @return
     */
    private DmsWaybillReverseResponseDTO queryReverseWaybillNew(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
    	JdResult<SubmitWaybillResponse> rpcResult = this.queryWaybill(dmsWaybillReverseDTO);
    	if(rpcResult != null
    			&& rpcResult.isSucceed()) {
    		return this.convertDmsWaybillReverseResponseDTO(rpcResult.getData());
    	}else {
    		errorMessage.append(rpcResult.getMessage());
    	}
    	return null;
    }
    /**
     * 对象转换
     * @param data
     * @return
     */
    private DmsWaybillReverseResponseDTO convertDmsWaybillReverseResponseDTO(SubmitWaybillResponse submitWaybillResponse) {
		if(submitWaybillResponse != null) {
			DmsWaybillReverseResponseDTO dmsWaybillReverseResponseDTO = new DmsWaybillReverseResponseDTO();
			dmsWaybillReverseResponseDTO.setWaybillCode(submitWaybillResponse.getWaybillCode());
			//收货人信息
			WaybillConsigneeDto consigneeDto = submitWaybillResponse.getWaybillConsigneeDto();
			if(consigneeDto != null) {
				dmsWaybillReverseResponseDTO.setReceiveAddress(consigneeDto.getConsigneeAddress());
				dmsWaybillReverseResponseDTO.setReceiveMobile(consigneeDto.getConsigneeMobile());
				dmsWaybillReverseResponseDTO.setReceiveTel(consigneeDto.getConsigneePhone());
				dmsWaybillReverseResponseDTO.setReceiveName(consigneeDto.getConsigneeName());
				
				dmsWaybillReverseResponseDTO.setProvince(consigneeDto.getConsigneeProvinceName());
				dmsWaybillReverseResponseDTO.setCity(consigneeDto.getConsigneeCityName());
				dmsWaybillReverseResponseDTO.setCounty(consigneeDto.getConsigneeCountyName());
				dmsWaybillReverseResponseDTO.setTown(consigneeDto.getConsigneeTownName());
			}
			//发货人信息
			WaybillConsignorDto consignorDto = submitWaybillResponse.getWaybillConsignorDto();
			if(consignorDto != null) {
				dmsWaybillReverseResponseDTO.setSenderAddress(consignorDto.getConsignorAddress());
				dmsWaybillReverseResponseDTO.setSenderName(consignorDto.getConsignorName());
				dmsWaybillReverseResponseDTO.setSenderMobile(consignorDto.getConsignorMobile());
				dmsWaybillReverseResponseDTO.setSenderTel(consignorDto.getConsignorPhone());
			}
			//包裹信息
			if(submitWaybillResponse.getWaybillPackageInfoDto() != null) {
				dmsWaybillReverseResponseDTO.setPackageCount(submitWaybillResponse.getWaybillPackageInfoDto().getPackageQuantity());
				List<WaybillPackageDto> packList = submitWaybillResponse.getWaybillPackageInfoDto().getPackageDTOList();
				if(packList != null) {
					List<DmsPackageDTO> packageDTOList = new ArrayList<DmsPackageDTO>();
					for(WaybillPackageDto pack : packList) {
						DmsPackageDTO newPack = new DmsPackageDTO();
						newPack.setDeliveryId(pack.getWaybillCode());
						newPack.setPackageCode(pack.getPackageCode());
						if(pack.getWeight() != null) {
							newPack.setWeight(pack.getWeight().doubleValue());
						}
						packageDTOList.add(newPack);
					}
					dmsWaybillReverseResponseDTO.setPackageDTOList(packageDTOList);
				}
			}
			return dmsWaybillReverseResponseDTO;
		}
		return null;
	}
    /**
     * 调用外部接口：加入监控和日志
     * @param dmsWaybillReverseDTO
     * @return
     */
	private JdResult<SubmitWaybillResponse> queryWaybill(DmsWaybillReverseDTO dmsWaybillReverseDTO){
		JdResult<SubmitWaybillResponse> result = new JdResult<SubmitWaybillResponse>();
		WbmsApiResult<SubmitWaybillResponse> rpcResult = null;
        CallerInfo info = null;
    	WbmsRequestProfile profile = null;
    	ReverseWaybillRequest requestData = null;
    	try {
    		info = ProfilerHelper.registerInfo( UMP_KEY_WBMS_PREFIX + ".queryWaybill");
    		//初始化参数
    		profile= getWbmsRequestProfile(dmsWaybillReverseDTO.getWaybillCode());
    		requestData = convertReverseWaybillRequest(dmsWaybillReverseDTO);
    		infoLog("换单前查询运单数据-百川接口reverseWaybillApi.queryWaybill:{},{}",profile,requestData);
			rpcResult = this.reverseWaybillApi.queryWaybill(profile,requestData);
			infoLog("换单前查询运单数据-百川接口reverseWaybillApi.queryWaybill-返回:{}",rpcResult);
    		if(rpcResult != null
    				&& rpcResult.isSuccess()) {
    			result.toSuccess(rpcResult.getMessage());
    			result.setData(rpcResult.getData());
    		}else if(rpcResult != null){
    			log.warn("调用运单换单查询接口失败reverseWaybillApi.queryWaybill,入参：{}  返回结果：{}",JsonHelper.toJson(dmsWaybillReverseDTO),JsonHelper.toJson(rpcResult));
    			result.toFail(rpcResult.getMessage());
    		}else {
    			result.toFail("调用运单换单提交接口返回值为空！");
    		}
		} catch (Exception e) {
			log.error("调用运单换单查询接口异常reverseWaybillApi.queryWaybill,入参：{}  失败原因：{}",JsonHelper.toJson(dmsWaybillReverseDTO),e.getMessage(),e);
			result.toError("调用运单换单查询接口异常！");
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
			BusinessLogProfiler log = new BusinessLogProfiler();
			this.logEngine.addLog(log);
		}
    	return result;
    }
    /**
     * 调用外部接口：加入监控和日志
     * @param dmsWaybillReverseDTO
     * @return
     */
    private JdResult<SubmitWaybillResponse> submitWaybill(DmsWaybillReverseDTO dmsWaybillReverseDTO){
    	JdResult<SubmitWaybillResponse> result = new JdResult<SubmitWaybillResponse>();
    	WbmsApiResult<SubmitWaybillResponse> rpcResult = null;
    	CallerInfo info = null;
    	WbmsRequestProfile profile = null;
    	ReverseWaybillRequest requestData = null;
    	long startTime=new Date().getTime();
    	try {
    		info = ProfilerHelper.registerInfo(UMP_KEY_WBMS_PREFIX + ".submitWaybill");
    		//初始化参数
    		profile= getWbmsRequestProfile(dmsWaybillReverseDTO.getWaybillCode());
    		requestData = convertReverseWaybillRequest(dmsWaybillReverseDTO);
    		infoLog("换单提交-百川接口reverseWaybillApi.submitWaybill-入参:{},{}",profile,requestData);
    		rpcResult = this.reverseWaybillApi.submitWaybill(profile,requestData);
    		infoLog("换单提交-百川接口reverseWaybillApi.submitWaybill-返回:{}",rpcResult);
    		if(rpcResult != null 
    				&& rpcResult.isSuccess()) {
    			result.toSuccess(rpcResult.getMessage());
    			result.setData(rpcResult.getData());
    		}else if(rpcResult != null){
    			log.warn("调用运单换单提交接口失败reverseWaybillApi.submitWaybill,入参：{}  返回结果：{}",JsonHelper.toJson(dmsWaybillReverseDTO),JsonHelper.toJson(rpcResult));
    			result.toFail(rpcResult.getMessage());
    		}else {
    			result.toFail("调用运单换单提交接口返回值为空！");
    		}
		} catch (Exception e) {
			log.error("调用运单换单提交接口异常reverseWaybillApi.submitWaybill,入参：{}  失败原因：{}",JsonHelper.toJson(dmsWaybillReverseDTO),e.getMessage(),e);
			result.toError("调用运单换单提交接口异常！");
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
            long endTime= new Date().getTime();
            JSONObject request=new JSONObject();
            request.put("waybillCode", dmsWaybillReverseDTO.getWaybillCode());
            request.put("param1", JsonHelper.toJson(profile));
            request.put("param2", JsonHelper.toJson(requestData));
            JSONObject response=new JSONObject();
            response.put("result", JsonHelper.toJson(rpcResult));

            BusinessLogProfiler log=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.OUTER_WAYBILL_EXCHANGE_WAYBILL_NEW)
                    .methodName("reverseWaybillApi#submitWaybill")
                    .operateRequest(request)
                    .operateResponse(response)
                    .processTime(endTime,startTime)
                    .build();
			this.logEngine.addLog(log);
		}
    	return result;
    }
    /**
     *
     * @param waybillCode 运单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人
     * @param operateTime 操作时间
     * @param packageCount 拒收包裹数量
     * @param isTotal 是否是整单拒收
     * @return
     */
    public DmsWaybillReverseDTO makeWaybillReverseDTO(String waybillCode, Integer operatorId, String operatorName, Date operateTime , Integer packageCount, Integer orgId, Integer createSiteCode, boolean isTotal){
    	DmsWaybillReverseDTO waybillReverseDTO = new DmsWaybillReverseDTO();
        waybillReverseDTO.setSource(SORT_CENTER); //分拣中心
        if(isTotal){
            waybillReverseDTO.setReverseType(1);// 整单拒收
        }else{
            waybillReverseDTO.setReverseType(2);// 包裹拒收
        }

        waybillReverseDTO.setWaybillCode(waybillCode);
        waybillReverseDTO.setOperateUserId(operatorId);
        waybillReverseDTO.setOperateUser(operatorName);
        waybillReverseDTO.setOrgId(orgId);
        waybillReverseDTO.setSortCenterId(createSiteCode);
        waybillReverseDTO.setOperateTime(operateTime);
        waybillReverseDTO.setReturnType(RETURN_TYPE_0);//默认
        if(!new Integer(0).equals(packageCount)){
            waybillReverseDTO.setPackageCount(packageCount);
        }

        return waybillReverseDTO;
    }
    
	/**
     * 组装换单对象支持二次换单
     * @param exchangeWaybillDto
     * @return
     */
    public DmsWaybillReverseDTO makeWaybillReverseDTOCanTwiceExchange(ExchangeWaybillDto exchangeWaybillDto){
    	DmsWaybillReverseDTO waybillReverseDTO = new DmsWaybillReverseDTO();
        waybillReverseDTO.setLimitReverseFlag(Boolean.FALSE);
        waybillReverseDTO.setSource(SORT_CENTER); //分拣中心
        if(exchangeWaybillDto.getIsTotalout()){
            waybillReverseDTO.setReverseType(1);// 整单拒收
        }else{
            waybillReverseDTO.setReverseType(2);// 包裹拒收
        }
        //二次换单时设置换单次数限制
        if(Boolean.TRUE.equals(exchangeWaybillDto.getTwiceExchangeFlag())){
        	waybillReverseDTO.setLimitReverseFlag(Boolean.TRUE);
        	waybillReverseDTO.setAllowReverseCount(twiceExchangeMaxTimes);
        }
        waybillReverseDTO.setWaybillCode(exchangeWaybillDto.getWaybillCode());
        waybillReverseDTO.setOperateUserId(exchangeWaybillDto.getOperatorId());
        waybillReverseDTO.setOperateUser(exchangeWaybillDto.getOperatorName());
        waybillReverseDTO.setOrgId(exchangeWaybillDto.getOrgId());
        waybillReverseDTO.setSortCenterId(exchangeWaybillDto.getCreateSiteCode());
        waybillReverseDTO.setOperateTime(DateHelper.parseDateTime(exchangeWaybillDto.getOperateTime()));
        waybillReverseDTO.setReturnType(RETURN_TYPE_0);//默认
        if(exchangeWaybillDto.getReturnType()!=null){
            waybillReverseDTO.setReturnType(exchangeWaybillDto.getReturnType());
        }
        if(!new Integer(0).equals(exchangeWaybillDto.getPackageCount())){
            waybillReverseDTO.setPackageCount(exchangeWaybillDto.getPackageCount());
        }
        if(checkIsPureMatch(exchangeWaybillDto.getWaybillCode(),null)){
            //是纯配一次换单  理赔状态满足  一定退备件库
            waybillReverseDTO.setReturnType(RETURN_TYPE_4);
        }
        //自定义地址
        if(StringUtils.isNotBlank(exchangeWaybillDto.getAddress())){
        	DmsWaybillAddress waybillAddress = new DmsWaybillAddress();
            waybillAddress.setAddress(exchangeWaybillDto.getAddress());
            waybillAddress.setContact(exchangeWaybillDto.getContact());
            waybillAddress.setPhone(exchangeWaybillDto.getPhone());
            waybillReverseDTO.setWaybillAddress(waybillAddress);
        }
        //判断是否拦截14，设置计费字段：2-不计费
        boolean isFullOrderFail = waybillCancelService.isFullOrderFail(exchangeWaybillDto.getWaybillCode());
        if(isFullOrderFail) {
            waybillReverseDTO.setChargeType(CHARGE_TYPE_2);
        }        
        return waybillReverseDTO;
    }

    /**
     * 纯配一次换单判断
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    private boolean checkIsPureMatch(String waybillCode,String waybillSign){

        if(StringUtils.isEmpty(waybillSign)){
            //外部未传入waybillSign 自己再去调用一次
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,true,true,false);
            if(baseEntity!=null && baseEntity.getData()!=null && baseEntity.getData().getWaybill() != null && StringUtils.isNotBlank(baseEntity.getData().getWaybill().getWaybillSign())){
                waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
            }
        }
        //纯配外单一次换单理赔完成且物权归京东-退备件库
        if(BusinessUtil.isPurematch(waybillSign)){
            LocalClaimInfoRespDTO claimInfoRespDTO =  obcsManager.getClaimListByClueInfo(1,waybillCode);
            if(claimInfoRespDTO != null){
                if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(claimInfoRespDTO.getStatusDesc())
                        && LocalClaimInfoRespDTO.GOOD_OWNER_JD.equals(claimInfoRespDTO.getGoodOwner())){
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * 百川接口切换-判断运单号是否需要调用新逆向接口，根据waybill里的waybillExt的omcOrderCode判断
	 * @param waybillCode
	 * @return
	 */
	private boolean needUseNewReverseApi(String waybillCode){
		//先判断是否开启ucc配置
		if(!uccPropertyConfiguration.isNeedUseNewReverseApi()) {
			log.info("百川接口切换-0：不调百川接口{}",waybillCode);
			return false;
		}
        if(StringHelper.isNotEmpty(waybillCode)){
        	WChoice wChoice = new WChoice();
        	wChoice.setQueryWaybillC(true);
        	wChoice.setQueryWaybillExtend(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if(baseEntity != null
                    && baseEntity.getData() != null
                    && baseEntity.getData().getWaybill() != null
                    && baseEntity.getData().getWaybill().getWaybillExt() != null){
	            if(StringHelper.isNotEmpty(baseEntity.getData().getWaybill().getWaybillExt().getOmcOrderCode())){
	            	log.info("百川接口切换-1：调用百川接口{}",waybillCode);
	            	return true;
	            }
            }
        }
        log.info("百川接口切换-0：不调百川接口{}",waybillCode);
		return false;
	}

	@Override
	public Waybill getQuickProduceWabillFromDrec(String waybillCode) {
        if(needUseNewReverseApi(waybillCode)) {
        	return getQuickProduceWabillFromDrecNew(waybillCode);
        }else {
        	return getQuickProduceWabillFromDrecOld(waybillCode);
        }
	}
	/**
	 * 新运单数据接口
	 * @param waybillCode
	 * @return
	 */
	private Waybill getQuickProduceWabillFromDrecNew(String waybillCode){
		Waybill waybill= null;
		com.jd.etms.waybill.domain.Waybill orderMsgDTO= this.waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
        if(orderMsgDTO==null) {
            log.warn("闪购从外单获取运单数据为空，单号为：{}",waybillCode);
            return waybill;
        }
        waybill=new Waybill();
        waybill.setReceiverName(orderMsgDTO.getReceiverName());
        waybill.setReceiverMobile(orderMsgDTO.getReceiverMobile());
        waybill.setReceiverTel(orderMsgDTO.getReceiverTel());
        if (orderMsgDTO.getCodMoney()!=null) {
        	BigDecimal codMoneyDecimal = NumberHelper.parseBigDecimalNullToZero(orderMsgDTO.getCodMoney());
        	if(codMoneyDecimal != null) {
        		waybill.setRecMoney(codMoneyDecimal.doubleValue());
        	}
        }
        waybill.setAddress(orderMsgDTO.getReceiverAddress());
        waybill.setWaybillCode(waybillCode);
        if(NumberHelper.gt0(waybill.getRecMoney())) {
        	waybill.setPaymentType(PAYMENT_TYPE_1);
        }else {
        	waybill.setPaymentType(PAYMENT_TYPE_0);
        }
        waybill.setSiteCode(orderMsgDTO.getOldSiteId());
        return waybill;
	}
	private Waybill getQuickProduceWabillFromDrecOld(String waybillCode){
		Waybill waybill= null;
        OrderMsgDTO orderMsgDTO = null;
        CallerInfo info = ProfilerHelper.registerInfo( UMP_KEY_RECEIVE_PREFIX + "GetOrderMsgServiceJsf.getOrderAllMsgByDeliveryId");
        try {
			orderMsgDTO = getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(waybillCode);
		} catch (Exception e) {
			log.error("调用getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId接口异常，单号为：{}",waybillCode,e);
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
        if(orderMsgDTO==null) {
            log.warn("闪购从外单获取运单数据为空，单号为：{}",waybillCode);
            return waybill;
        }
        waybill=new Waybill();
        waybill.setReceiverName(orderMsgDTO.getReceiveName());
        waybill.setReceiverMobile(orderMsgDTO.getReceiveMobile());
        waybill.setReceiverTel(orderMsgDTO.getReceiveTel());
        if (orderMsgDTO.getCollectionMoney()!=null) {
            waybill.setRecMoney(orderMsgDTO.getCollectionMoney());
        }
        waybill.setAddress(orderMsgDTO.getReceiveAdress());
        waybill.setWaybillCode(waybillCode);
        waybill.setPaymentType(orderMsgDTO.getCollectionValue());
        if (orderMsgDTO.getPreallocation() != null)
            waybill.setSiteCode(orderMsgDTO.getPreallocation().getSiteId());
        return waybill;
	}
	/**
	 * 根据旧单号查询新单号
	 */
	@Override
	public JdResult<String> queryWaybillCodeByOldWaybillCode(String oldWaybillCode) {
        if(needUseNewReverseApi(oldWaybillCode)) {
        	return queryWaybillCodeByOldWaybillCodeNew(oldWaybillCode);
        }else {
        	return queryWaybillCodeByOldWaybillCodeOld(oldWaybillCode);
        }
	}
	/**
	 * 根据旧单号查询新单号-现有逻辑(加入监控)
	 * @param oldWaybillCode
	 * @return
	 */
	private JdResult<String> queryWaybillCodeByOldWaybillCodeOld(String oldWaybillCode) {
		GrossReturnResponse grossReturnResponse = null;
		grossReturnResponse = null;
		JdResult<String> result = new JdResult<String>();
		CallerInfo info = ProfilerHelper.registerInfo( UMP_KEY_RECEIVE_PREFIX + "GrossReturnSaf.queryDeliveryIdByOldDeliveryId");
		try {
			grossReturnResponse = this.grossReturnSaf.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
		} catch (Exception e) {
			log.error("调用GrossReturnSaf.queryDeliveryIdByOldDeliveryId接口异常，单号为：{}",oldWaybillCode,e);
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		if(grossReturnResponse != null) {
			result.toSuccess(grossReturnResponse.getResultCode(), grossReturnResponse.getResultMsg());
			result.setData(grossReturnResponse.getDeliveryId());
		}else {
			result.toFail(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
		}
		return result;
	}
	/**
	 * 新逻辑
	 * @param oldWaybillCode
	 * @return
	 */
	private JdResult<String> queryWaybillCodeByOldWaybillCodeNew(String oldWaybillCode) {
		DmsWaybillReverseDTO dmsWaybillReverseDTO = new DmsWaybillReverseDTO();
		dmsWaybillReverseDTO.setWaybillCode(oldWaybillCode);
		InvokeResult<Waybill> rpcResult = waybillCommonService.getReverseWaybill(oldWaybillCode);
		JdResult<String> result = new JdResult<String>();
		if(rpcResult != null && 
				rpcResult.getData() != null) {
			result.toSuccess(rpcResult.getCode(),rpcResult.getMessage());
			result.setData(rpcResult.getData().getWaybillCode());
		}else {
			result.toFail(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
		}
		return null;
	}
	private WbmsRequestProfile getWbmsRequestProfile(String key) {
		WbmsRequestProfile wbmsRequestProfile = new WbmsRequestProfile();
		wbmsRequestProfile.setSystemSource(systemSource);
		wbmsRequestProfile.setTenantId(tenantId);
		wbmsRequestProfile.setAppSource(appSource);
		wbmsRequestProfile.setToken(token);
		wbmsRequestProfile.setLocale(Constants.LOCALE_ZH_CN);
		wbmsRequestProfile.setTimezone(Constants.TIME_ZONE8);
		wbmsRequestProfile.setTraceId(StringHelper.append(key, UUID.randomUUID().toString()));
		return wbmsRequestProfile;
	}
	private ReverseWaybillRequest convertReverseWaybillRequest(DmsWaybillReverseDTO dmsWaybillReverseDTO) {
		if(dmsWaybillReverseDTO != null) {
			ReverseWaybillRequest reverseWaybillRequest = new ReverseWaybillRequest();
			reverseWaybillRequest.setBizIdentity(BIZ_REVERSE);
			reverseWaybillRequest.setWaybillType(WAYBILL_TYPE_1);
			reverseWaybillRequest.setOperateSource(dmsWaybillReverseDTO.getSource());
			reverseWaybillRequest.setSourceSiteId(dmsWaybillReverseDTO.getSortCenterId());
			reverseWaybillRequest.setOperateUserName(dmsWaybillReverseDTO.getOperateUser());
			reverseWaybillRequest.setOperateUserCode(dmsWaybillReverseDTO.getOperateUserId());
			reverseWaybillRequest.setSubmitTime(dmsWaybillReverseDTO.getOperateTime());
			
			reverseWaybillRequest.setForwardWaybillCode(dmsWaybillReverseDTO.getWaybillCode());
			//reverseType：对应原逻辑的returnType
			reverseWaybillRequest.setReverseType(dmsWaybillReverseDTO.getReturnType());
			reverseWaybillRequest.setPackageQuantity(dmsWaybillReverseDTO.getPackageCount());
			//rejectionType：对应原逻辑的reverseType 1: 整单拒收    2: 包裹维度拒收
			reverseWaybillRequest.setRejectionType(dmsWaybillReverseDTO.getReverseType());
			reverseWaybillRequest.setChargeType(dmsWaybillReverseDTO.getChargeType());
			//二次换单字段
			reverseWaybillRequest.setAllowReverseCount(dmsWaybillReverseDTO.getAllowReverseCount());
			reverseWaybillRequest.setLimitReverseFlag(dmsWaybillReverseDTO.getLimitReverseFlag());
			//地址信息
			DmsWaybillAddress waybillAddress= dmsWaybillReverseDTO.getWaybillAddress();
			if(waybillAddress != null) {
				ConsigneeDto consigneeDto = new ConsigneeDto();
				consigneeDto.setConsigneeName(waybillAddress.getContact ());
				consigneeDto.setConsigneePhone(waybillAddress.getPhone ());
				consigneeDto.setConsigneeAddress(waybillAddress.getAddress ());
				reverseWaybillRequest.setConsigneeDto(consigneeDto);
			}
			return reverseWaybillRequest;
		}
		return null;
	}
	/**
	 * 打印info日志
	 * @param format
	 * @param params1
	 */
	private void infoLog(String format, Object params1) {
		if(log.isInfoEnabled()) {
			log.info(format, JsonHelper.toJson(params1));
		}
	}
	/**
	 * 打印info日志
	 * @param format
	 * @param params1
	 * @param params2
	 */
	private void infoLog(String format, Object params1,Object params2) {
		if(log.isInfoEnabled()) {
			log.info(format, JsonHelper.toJson(params1),JsonHelper.toJson(params2));
		}
	}
}
