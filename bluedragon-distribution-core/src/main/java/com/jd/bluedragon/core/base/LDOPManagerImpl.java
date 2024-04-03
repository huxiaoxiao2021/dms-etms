package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.BackAddressDTOExt;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareEclp;
import com.jd.bluedragon.distribution.reverse.domain.DmsDetailReverseReasonDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsPackageDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillAddress;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.alibaba.fastjson.JSONObject;
import com.jd.ldop.business.api.BackAddressInfoApi;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
import com.jd.ldop.business.api.dto.response.ResponseStatus;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.print.WaybillPrintApi;
import com.jd.ldop.center.api.print.dto.PrintResultDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintRequestDTO;
import com.jd.ldop.center.api.refund.RefundApi;
import com.jd.ldop.center.api.refund.dto.RefundApplyDTO;
import com.jd.ldop.center.api.receive.dto.DetailReverseReasonDTO;
import com.jd.ldop.center.api.receive.dto.PackageDTO;
import com.jd.ldop.center.api.reverse.WaybillReturnSignatureApi;
import com.jd.ldop.center.api.reverse.WaybillReverseApi;
import com.jd.ldop.center.api.reverse.dto.*;
import com.jd.ldop.center.api.update.dto.WaybillAddress;
import com.jd.ldop.center.api.waybill.GeneralWaybillQueryApi;
import com.jd.ldop.center.api.waybill.dto.OrderInfoDTO;
import com.jd.ldop.center.api.waybill.dto.WaybillQueryByOrderIdDTO;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.receive.api.dto.OrderInfoPrintDTO;
import com.jd.ql.dms.receive.api.dto.OrderInfoQueryDTO;
import com.jd.ql.dms.receive.api.dto.PackDTO;
import com.jd.ql.dms.receive.api.dto.ResultObject;
import com.jd.ql.dms.receive.api.jsf.OrderInfoServiceJsf;
import com.jd.ql.dms.receive.api.util.PageUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 外单 jsf接口 包装类
 */
@Service("ldopManager")
public class LDOPManagerImpl implements LDOPManager {

    ////退货方式 0 - 按照商家配置 1 - 退库房 2 - 退寄件人 3 - 退备件库 4 - 物流损退备件库 5 - 退指定地址
    public static final int RETURN_TYPE_0 = 0;
    public static final int RETURN_TYPE_1 = 1;
    public static final int RETURN_TYPE_2 = 2;
    public static final int RETURN_TYPE_3 = 3;
    public static final int RETURN_TYPE_4 = 4;
    public static final int RETURN_TYPE_5 = 5;

    //系统来源:  1:城配，2:一体机，3:冷链调度，4:分拣
    public static final int SYSTEM_SOURCE_4 = 4;

    /**
     * 计费-2-不计费
     */
    public static final int CHARGE_TYPE_2 = 2;
    
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.ldop.";
    private static final String UMP_KEY_REFUNDAPI_PREFIX = "dmsWeb.jsf.client.ldop.";

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private WaybillReverseApi waybillReverseApi;

    @Autowired
    private WaybillPrintApi waybillPrintApi;

    @Autowired
    private WaybillReturnSignatureApi waybillReturnSignatureApi;

    @Autowired
    private OrderInfoServiceJsf orderInfoServiceJsf ;

    @Autowired
    private GeneralWaybillQueryApi generalWaybillQueryApi;

    @Autowired
    private ReverseSpareEclp reverseSpareEclp;

    /*用于记录操作日志*/
    @Autowired
    private GoddessService goddessService;

    @Autowired
    @Qualifier("obcsManager")
    private OBCSManager obcsManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier("backAddressInfoApi")
    private BackAddressInfoApi backAddressInfoApi;
    @Autowired
    private BaseService baseService;
    /**
     * 二次换单限制次数
     */
	@Value("${beans.LDOPManagerImpl.twiceExchangeMaxTimes}")
    private int twiceExchangeMaxTimes;
	
    @Autowired
    private RefundApi refundApi;
    
    @Autowired
    private WaybillCancelService waybillCancelService;
    
    private final Logger log = LoggerFactory.getLogger(LDOPManagerImpl.class);
    /**
     * 触发外单逆向换单接口
     * @param waybillReverseDTO
     * @return
     */
    public boolean waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,JdResponse<Boolean> rest){
    	WaybillReverseDTO waybillReverseDTO = this.convertWaybillReverseDTO(dmsWaybillReverseDTO);
        long startTime=new Date().getTime();
        CallerInfo info = null;
        ResponseDTO responseDTO = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.waybillReverse",false, true);

            responseDTO = waybillReverseApi.waybillReverse(waybillReverseDTO);
            if(!responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                //失败
                rest.setMessage("外单自动换单接口失败 "+responseDTO.getStatusMessage());
                log.warn("触发逆向换单失败,入参：{}  失败原因：{}", JsonHelper.toJson(waybillReverseDTO),responseDTO.getStatusMessage());
                return false;
            }

            return true;
        }catch (Exception e){
            log.error("触发逆向换单失败,入参：{}  失败原因：{}",JsonHelper.toJson(waybillReverseDTO),e.getMessage(),e);
            Profiler.functionError(info);
            return false;
        }finally{
            long endTime= new Date().getTime();


            JSONObject response=new JSONObject();
            response.put("waybillReverseDTO", JsonHelper.toJson(waybillReverseDTO));
            response.put("responseDTO", JsonHelper.toJson(responseDTO));

            JSONObject request=new JSONObject();
            request.put("waybillCode", waybillReverseDTO.getWaybillCode());

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .methodName("LDOPManagerImpl#waybillReverse")
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.OUTER_WAYBILL_EXCHANGE_WAYBILL)
                    .processTime(endTime,startTime)
                    .operateResponse(response)
                    .operateRequest(request)
                    .build();

            logEngine.addLog(businessLogProfiler);

            insertLog(waybillReverseDTO,responseDTO);
            Profiler.registerInfoEnd(info);
        }

    }

    @Override
    public DmsWaybillReverseResult waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
        long startTime=new Date().getTime();
        WaybillReverseDTO waybillReverseDTO = this.convertWaybillReverseDTO(dmsWaybillReverseDTO);
        CallerInfo info = null;
        ResponseDTO<WaybillReverseResult> responseDTO = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.waybillReverse",false, true);

            responseDTO = waybillReverseApi.waybillReverse(waybillReverseDTO);
            if(responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                return this.convertDmsWaybillReverseResult(responseDTO.getData());
            }else {
                //失败
                errorMessage.append("外单自动换单接口失败 "+responseDTO.getStatusMessage());
                log.warn("触发逆向换单失败,入参：{}  失败原因：{}",JsonHelper.toJson(waybillReverseDTO),responseDTO.getStatusMessage());
            }
            return null;
        }catch (Exception e){
            log.error("触发逆向换单失败,入参：{}  失败原因：",JsonHelper.toJson(waybillReverseDTO),e.getMessage(),e);
            Profiler.functionError(info);
            return null;
        }finally{

            long endTime= new Date().getTime();

            JSONObject request=new JSONObject();
            request.put("waybillCode", waybillReverseDTO.getWaybillCode());

            JSONObject response=new JSONObject();
            response.put("waybillReverseDTO", JsonHelper.toJson(waybillReverseDTO));
            response.put("responseDTO", JsonHelper.toJson(responseDTO));

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.OUTER_WAYBILL_EXCHANGE_WAYBILL)
                    .methodName("LDOPManagerImpl#waybillReverse")
                    .operateRequest(request)
                    .operateResponse(response)
                    .processTime(endTime,startTime)
                    .build();

            logEngine.addLog(businessLogProfiler);

            insertLog(waybillReverseDTO,responseDTO);
            Profiler.registerInfoEnd(info);

        }
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
    public WaybillReverseDTO makeWaybillReverseDTO(String waybillCode, Integer operatorId, String operatorName, Date operateTime , Integer packageCount, Integer orgId, Integer createSiteCode, boolean isTotal){
        WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
        waybillReverseDTO.setSource(2); //分拣中心
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


    public DmsWaybillReverseResponseDTO queryReverseWaybill(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage) {
    	WaybillReverseDTO waybillReverseDTO = this.convertWaybillReverseDTO(dmsWaybillReverseDTO);
        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.queryReverseWaybill",false, true);

            ResponseDTO<WaybillReverseResponseDTO> responseDTO = waybillReverseApi.queryReverseWaybill(waybillReverseDTO);
            if(responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                return this.convertDmsWaybillReverseResponseDTO(responseDTO.getData());
            }else {
                //失败
                errorMessage.append("换单前获取外单信息接口失败 "+responseDTO.getStatusMessage());
                log.info("换单前获取外单信息失败,入参：{}  失败原因：{}",JsonHelper.toJson(waybillReverseDTO),responseDTO.getStatusMessage());
            }
            return null;
        }catch (Exception e){
            log.error("换单前获取外单信息失败,入参：{}  失败原因：{}",JsonHelper.toJson(waybillReverseDTO),e.getMessage(),e);
            Profiler.functionError(info);
            throw e;
        }finally{
            Profiler.registerInfoEnd(info);

        }
    }

    /**
     * 根据商家编码和运单号调用外单接口获取打印信息
     * @param customerCode
     * @param waybillCode
     * @return
     */
    public List<WaybillPrintDataDTO> getPrintDataForCityOrder(String customerCode, String waybillCode){
        WaybillPrintRequestDTO waybillPrintRequestDTO = new WaybillPrintRequestDTO();
        waybillPrintRequestDTO.setCustomerCode(customerCode);
        waybillPrintRequestDTO.setWaybillCode(waybillCode);

        CallerInfo info = null;
        try {
            info = Profiler.registerInfo("DMS.BASE.LDOPManagerImpl.getPrintDataForCityOrder",false,true);
            PrintResultDTO printResultDto= waybillPrintApi.getPrintDataForCityOrder(waybillPrintRequestDTO);

            if(printResultDto == null){
                log.warn("根据商家编码和运单号调用外单接口获取打印信息为空.商家编码:{}, 运单号:{}", customerCode , waybillCode);
                return Collections.emptyList();
            }
            if(printResultDto.getData() == null){
                log.warn("根据商家编码和运单号调用外单接口获取打印信息为空.商家编码:{}, 运单号:{},返回值:{}-{}"
                        , customerCode ,waybillCode , printResultDto.getStatusCode(), printResultDto.getStatusMessage());
                return Collections.emptyList();
            }
            return printResultDto.getData();
        }catch (Exception e){
            log.error("根据商家编码和运单号调用外单接口获取打印信息异常.商家编码:{}, 运单号:{}", customerCode , waybillCode,e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return Collections.emptyList();
    }

    /**
     * 组装换单对象支持二次换单
     * @param exchangeWaybillDto
     * @return
     */
    public WaybillReverseDTO makeWaybillReverseDTOCanTwiceExchange(ExchangeWaybillDto exchangeWaybillDto){
        WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
        waybillReverseDTO.setLimitReverseFlag(Boolean.FALSE);
        waybillReverseDTO.setSource(2); //分拣中心
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
            WaybillAddress waybillAddress = new WaybillAddress();
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
    public Boolean checkIsPureMatch(String waybillCode,String waybillSign){

        if(StringUtils.isEmpty(waybillSign)){
            //外部未传入waybillSign 自己再去调用一次
        	com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,true,true,false);
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
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 根据商家ID和商家单号获取运单号
     *
     * @param busiId   商家ID
     * @param busiCode 商家单号
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.findWaybillCodeByBusiIdAndBusiCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<String> findWaybillCodeByBusiIdAndBusiCode(String busiId, String busiCode) {
        InvokeResult<String> result = new InvokeResult<String>();
        //检查
        if(StringUtils.isBlank(busiId) || StringUtils.isBlank(busiCode) || !SerialRuleUtil.isMatchNumeric(busiId)){
            result.setMessage("商家ID或商家单号参数不正确！");
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            return result;
        }

        //组装外单JSF接口入参。 业务定义同一个商家ID和商家单号只会对应一个运单号
        OrderInfoQueryDTO params = new OrderInfoQueryDTO();
        params.setCustomerId(Integer.valueOf(busiId));
        params.setOrderId(busiCode);
        PageUtil< OrderInfoPrintDTO > pageParam = new PageUtil<OrderInfoPrintDTO>();
        pageParam.setCurPage(1);
        pageParam.setPageSize(10);

        PageUtil<OrderInfoPrintDTO> jsfResult = orderInfoServiceJsf.queryOrderInfo4PrintByCondition(params, pageParam);
        if(jsfResult.getTotalRow()==0){
            //未查询到数据
            result.setMessage("未获取到运单数据。请确认商家ID和商家单号是否正确！");
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
        }else if(jsfResult.getTotalRow()==1){
            List<OrderInfoPrintDTO> datas = jsfResult.getContents();
            if(datas==null || datas.size()!=1 || StringUtils.isBlank(datas.get(0).getDeliveryId())){
                result.setMessage("外单接口数据返回异常。请联系运营处理！");
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                log.warn("根据商家ID和商家单号获取运单号失败:{}|{}|{}",busiId,busiCode,JsonHelper.toJson(jsfResult));
            }else {
                //返回运单号
                result.setData(datas.get(0).getDeliveryId());
            }
        }else {
            //违背义务定义
            result.setMessage("通过商家ID和商家单号匹配到多个运单。请联系运营处理！");
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
        }
        return result;
    }

    /**
     * 根据商家ID和商家单号获取运单号
     *
     * @param busiId   商家ID
     * @param busiCode 商家单号
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.queryWaybillCodeByOrderIdAndCustomerCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public String queryWaybillCodeByOrderIdAndCustomerCode(Integer busiId, String busiCode) {
        try{
            WaybillQueryByOrderIdDTO queryByOrderIdDTO = new WaybillQueryByOrderIdDTO();
            queryByOrderIdDTO.setOrderId(busiCode);
            queryByOrderIdDTO.setCustomerId(busiId);

            ResponseDTO<OrderInfoDTO> responseDTO = generalWaybillQueryApi.queryOrderInfoByOrderIdAndCustomerCode(queryByOrderIdDTO);
            if(responseDTO != null && ResponseDTO.SUCCESS_CODE.equals(responseDTO.getStatusCode()) && responseDTO.getData()!=null){
                return responseDTO.getData().getDeliveryId();
            }
        }catch (Exception e){
           log.error("根据商家ID和商家单号获取运单号异常!req:{},{}",busiId,busiCode,e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 根据旧单号获取新单号
     * @param dto 旧单号对象
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.waybillReturnSignature",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public ResponseDTO<ReturnSignatureResult> waybillReturnSignature(WaybillReturnSignatureDTO dto){
        return waybillReturnSignatureApi.waybillReturnSignature(dto);
    }

    /**
     * 根据运单号获得运单信息
     * @param waybillCode 运单号
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.queryReturnSignatureMessage",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public ResponseDTO<ReturnSignatureMessageDTO> queryReturnSignatureMessage(String waybillCode){
        return waybillReturnSignatureApi.queryReturnSignatureMessage(waybillCode);
    }

	@Override
	public JdResult<List<BackAddressDTO>> queryBackAddressByType(Integer backType,String busiCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "backAddressInfoApi.queryBackAddressByType");
		JdResult<List<BackAddressDTO>> result = new JdResult<List<BackAddressDTO>>();
		try {
			com.jd.ldop.business.api.dto.response.Response<List<BackAddressDTO>>   rpcResult = backAddressInfoApi.queryBackAddressByType(backType, busiCode, null, null);
			if(rpcResult != null
					&& ResponseStatus.SUCCESS.equals(rpcResult.getStatus())){
				result.setData(rpcResult.getResult());
				result.toSuccess();
			}else{
				log.warn("调用ldop获取退货地址接口失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用ldop获取退货地址接口失败！");
			}
		} catch (Exception e) {
			log.error("调用ldop获取退货地址接口异常！",e);
			result.toError("调用ldop获取退货地址接口异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
    /**
     * 记录日志
     *
     * @param dto 操作消息对象
     */
    private void insertLog(WaybillReverseDTO dto,ResponseDTO responseDTO) {
        try {
            Goddess goddess = new Goddess();
            goddess.setKey(dto.getWaybillCode());
            goddess.setBody(JsonHelper.toJson(dto)+"|"+JsonHelper.toJson(responseDTO));
            goddess.setHead("reverse_waybill");
            goddessService.save(goddess);
        } catch (Exception e) {
            log.error("逆向换单：cassandra操作日志记录失败：" ,e);
        }
    }

	@Override
	public BackAddressDTOExt getBackAddressDTOExt(BackAddressDTO backAddress) {
    	BackAddressDTOExt addressData = new BackAddressDTOExt();
    	if(backAddress != null){
    		String fullBackAddress = "";
    		if(backAddress.getProvinceId() != null){
    			Assort provinceInfo = baseService.getOneAssortById(backAddress.getProvinceId());
    			if(provinceInfo != null && provinceInfo.getAssDis() != null){
    				fullBackAddress += provinceInfo.getAssDis();
    			}
    		}
    		if(backAddress.getCityId() != null){
    			Assort cityInfo = baseService.getOneAssortById(backAddress.getCityId());
    			if(cityInfo != null && cityInfo.getAssDis() != null){
    				fullBackAddress += cityInfo.getAssDis();
    			}
    		}
    		if(backAddress.getAreaId() != null){
    			Assort areaInfo = baseService.getOneAssortById(backAddress.getAreaId());
    			if(areaInfo != null && areaInfo.getAssDis() != null){
    				fullBackAddress += areaInfo.getAssDis();
    			}
    		}
    		if(backAddress.getBackAddress() != null){
    			fullBackAddress += backAddress.getBackAddress();
    		}
    		addressData.setFullBackAddress(fullBackAddress);
    		addressData.setBackAddress(backAddress.getBackAddress());
    		addressData.setContractMobile(backAddress.getContractMobile());
    		addressData.setContractPhone(backAddress.getContractPhone());
    		addressData.setContractName(backAddress.getContractName());
    		return addressData;
    	}
    	return null;
	}
    private WaybillReverseDTO convertWaybillReverseDTO(DmsWaybillReverseDTO dmsWaybillReverseDTO) {
    	if(dmsWaybillReverseDTO != null) {
    		WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
    		waybillReverseDTO.setWeight(dmsWaybillReverseDTO.getWeight());
    		waybillReverseDTO.setOperateTime(dmsWaybillReverseDTO.getOperateTime());
    		waybillReverseDTO.setReverseType(dmsWaybillReverseDTO.getReverseType());
    		waybillReverseDTO.setAllowReverseCount(dmsWaybillReverseDTO.getAllowReverseCount());
    		waybillReverseDTO.setRemark(dmsWaybillReverseDTO.getRemark());
    		waybillReverseDTO.setPackageCount(dmsWaybillReverseDTO.getPackageCount());
    		waybillReverseDTO.setReturnType(dmsWaybillReverseDTO.getReturnType());
    		waybillReverseDTO.setOperateUser(dmsWaybillReverseDTO.getOperateUser());
    		waybillReverseDTO.setWaybillCode(dmsWaybillReverseDTO.getWaybillCode());
    		waybillReverseDTO.setReverseReason(dmsWaybillReverseDTO.getReverseReason());
    		waybillReverseDTO.setSortCenterId(dmsWaybillReverseDTO.getSortCenterId());
    		waybillReverseDTO.setOrgId(dmsWaybillReverseDTO.getOrgId());
    		waybillReverseDTO.setAttributionToJD(dmsWaybillReverseDTO.getAttributionToJD());
    		waybillReverseDTO.setSiteId(dmsWaybillReverseDTO.getSiteId());
    		waybillReverseDTO.setSource(dmsWaybillReverseDTO.getSource());
    		waybillReverseDTO.setCustomerCode(dmsWaybillReverseDTO.getCustomerCode());
    		waybillReverseDTO.setVolume(dmsWaybillReverseDTO.getVolume());
    		waybillReverseDTO.setLimitReverseFlag(dmsWaybillReverseDTO.getLimitReverseFlag());
    		waybillReverseDTO.setOperateUserId(dmsWaybillReverseDTO.getOperateUserId());
    		waybillReverseDTO.setChargeType(dmsWaybillReverseDTO.getChargeType());
    		DmsWaybillAddress dmsWaybillAddress= dmsWaybillReverseDTO.getWaybillAddress();
    		if(dmsWaybillAddress != null) {
    			WaybillAddress waybillAddress = new WaybillAddress();
    			waybillAddress.setCountryId(dmsWaybillAddress.getCountryId());
    			waybillAddress.setProvinceId(dmsWaybillAddress.getProvinceId());
    			waybillAddress.setCredentialsType(dmsWaybillAddress.getCredentialsType());
    			waybillAddress.setPhone(dmsWaybillAddress.getPhone());
    			waybillAddress.setCountryName(dmsWaybillAddress.getCountryName());
    			waybillAddress.setCountrysideId(dmsWaybillAddress.getCountrysideId());
    			waybillAddress.setCityId(dmsWaybillAddress.getCityId());
    			waybillAddress.setCityName(dmsWaybillAddress.getCityName());
    			waybillAddress.setProvinceName(dmsWaybillAddress.getProvinceName());
    			waybillAddress.setCompanyName(dmsWaybillAddress.getCompanyName());
    			waybillAddress.setContact(dmsWaybillAddress.getContact());
    			waybillAddress.setAddress(dmsWaybillAddress.getAddress());
    			waybillAddress.setCredentialsNumber(dmsWaybillAddress.getCredentialsNumber());
    			waybillAddress.setLongitude(dmsWaybillAddress.getLongitude());
    			waybillAddress.setCoordinateType(dmsWaybillAddress.getCoordinateType());
    			waybillAddress.setCountrysideName(dmsWaybillAddress.getCountrysideName());
    			waybillAddress.setLatitude(dmsWaybillAddress.getLatitude());
    			waybillAddress.setMobile(dmsWaybillAddress.getMobile());
    			waybillReverseDTO.setWaybillAddress(waybillAddress);
    		}
    		if(dmsWaybillReverseDTO.getDetailReverseReasonDTOList() != null) {
    			List<DetailReverseReasonDTO> detailReverseReasonDTOList = new ArrayList<DetailReverseReasonDTO>();
    			for(DmsDetailReverseReasonDTO dmsDetailReverseReasonDTO :dmsWaybillReverseDTO.getDetailReverseReasonDTOList()) {
    				DetailReverseReasonDTO detailReverseReasonDTO = new DetailReverseReasonDTO();
    				detailReverseReasonDTO.setSerialNo(dmsDetailReverseReasonDTO.getSerialNo());
    				detailReverseReasonDTO.setReasonType(dmsDetailReverseReasonDTO.getReasonType());
    				detailReverseReasonDTO.setDeliveryId(dmsDetailReverseReasonDTO.getDeliveryId());
    				detailReverseReasonDTO.setCount(dmsDetailReverseReasonDTO.getCount());
    				detailReverseReasonDTO.setReasonName(dmsDetailReverseReasonDTO.getReasonName());
    				detailReverseReasonDTO.setProductName(dmsDetailReverseReasonDTO.getProductName());
    				detailReverseReasonDTO.setProductId(dmsDetailReverseReasonDTO.getProductId());
    				
    				detailReverseReasonDTOList.add(detailReverseReasonDTO);
    			}
    			waybillReverseDTO.setDetailReverseReasonDTOList(detailReverseReasonDTOList);
    		}
    		return waybillReverseDTO;
    	}
    	return null;
	}
    /**
     * 对象转换->分拣对象
     * @param waybillReverseResult
     * @return
     */
    private DmsWaybillReverseResult convertDmsWaybillReverseResult(WaybillReverseResult waybillReverseResult) {
    	if(waybillReverseResult != null) {
    		DmsWaybillReverseResult result = new DmsWaybillReverseResult();
    		result.setWaybillCode(waybillReverseResult.getWaybillCode());
    		return result;
    	}
    	return null;
    }
    private DmsWaybillReverseResponseDTO convertDmsWaybillReverseResponseDTO(WaybillReverseResponseDTO waybillReverseResponseDTO) {
    	if(waybillReverseResponseDTO != null) {
    		DmsWaybillReverseResponseDTO dmsWaybillReverseResponseDTO = new DmsWaybillReverseResponseDTO();
    		dmsWaybillReverseResponseDTO.setProvinceId(waybillReverseResponseDTO.getProvinceId());
    		dmsWaybillReverseResponseDTO.setSenderAddress(waybillReverseResponseDTO.getSenderAddress());
    		dmsWaybillReverseResponseDTO.setCityId(waybillReverseResponseDTO.getCityId());
    		dmsWaybillReverseResponseDTO.setReceiveMobile(waybillReverseResponseDTO.getReceiveMobile());
    		dmsWaybillReverseResponseDTO.setReceiveTel(waybillReverseResponseDTO.getReceiveTel());
    		dmsWaybillReverseResponseDTO.setPackageCount(waybillReverseResponseDTO.getPackageCount());
    		dmsWaybillReverseResponseDTO.setTownId(waybillReverseResponseDTO.getTownId());
    		dmsWaybillReverseResponseDTO.setSenderTel(waybillReverseResponseDTO.getSenderTel());
    		dmsWaybillReverseResponseDTO.setWaybillCode(waybillReverseResponseDTO.getWaybillCode());
    		dmsWaybillReverseResponseDTO.setCity(waybillReverseResponseDTO.getCity());
    		dmsWaybillReverseResponseDTO.setSenderName(waybillReverseResponseDTO.getSenderName());
    		dmsWaybillReverseResponseDTO.setCounty(waybillReverseResponseDTO.getCounty());
    		dmsWaybillReverseResponseDTO.setProvince(waybillReverseResponseDTO.getProvince());
    		dmsWaybillReverseResponseDTO.setReceiveName(waybillReverseResponseDTO.getReceiveName());
    		dmsWaybillReverseResponseDTO.setReceiveAddress(waybillReverseResponseDTO.getReceiveAddress());
    		dmsWaybillReverseResponseDTO.setSenderMobile(waybillReverseResponseDTO.getSenderMobile());
    		dmsWaybillReverseResponseDTO.setTown(waybillReverseResponseDTO.getTown());
    		dmsWaybillReverseResponseDTO.setCountyId(waybillReverseResponseDTO.getCountyId());
    		if(waybillReverseResponseDTO.getPackageDTOList() != null) {
    			List<DmsPackageDTO> packageDTOList = new ArrayList<DmsPackageDTO>();
    			for(PackageDTO temp :waybillReverseResponseDTO.getPackageDTOList()) {
    				DmsPackageDTO dmsPackageDTO = new DmsPackageDTO();
    				dmsPackageDTO.setVolumn(temp.getVolumn());
    				dmsPackageDTO.setCustomerId(temp.getCustomerId());
    				dmsPackageDTO.setDeliveryId(temp.getDeliveryId());
    				dmsPackageDTO.setWeight(temp.getWeight());
    				dmsPackageDTO.setVloumWidth(temp.getVloumWidth());
    				dmsPackageDTO.setVloumHeight(temp.getVloumHeight());
    				dmsPackageDTO.setVloumLong(temp.getVloumLong());
    				dmsPackageDTO.setPackageCode(temp.getPackageCode());
    				packageDTOList.add(dmsPackageDTO);
    			}
    			dmsWaybillReverseResponseDTO.setPackageDTOList(packageDTOList);
    		}
    		return dmsWaybillReverseResponseDTO;
    	}
		return null;
	}
	@Override
	public JdResult<String> refundApply(RefundApplyDTO refundApplyDTO) {
		CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_REFUNDAPI_PREFIX + "refundApi.refundApply");
    	JdResult<String> result = new JdResult<String>();
    	try {
    		ResponseDTO<String> rest = refundApi.refundApply(refundApplyDTO);
	        if(null != rest 
	        		&& ResponseDTO.SUCCESS_CODE.equals(rest.getStatusCode())){
	            result.setData(rest.getData());
	            result.toSuccess();
	        }else if(null != rest){
				log.warn("调用台账退款申请接口失败！请求参数：{},返回值：{}",JsonHelper.toJson(refundApplyDTO),JsonHelper.toJson(rest));
				result.toFail(rest.getStatusMessage());
	        }else {
				log.warn("调用台账退款申请接口失败！请求参数：{},返回值：{}",JsonHelper.toJson(refundApplyDTO),JsonHelper.toJson(rest));
				result.toFail("调用台账退款申请接口失败！");
	        }
		} catch (Exception e) {
			log.error("调用台账退款申请接口异常！请求参数：{}",e,JsonHelper.toJson(refundApplyDTO));
			result.toError("调用台账退款申请接口异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
	}

    @Override
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.getPackLists",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public ResultObject<List<PackDTO>> getPackLists(String deliveryId) {
        return orderInfoServiceJsf.getPackLists(deliveryId, null);
    }
}
