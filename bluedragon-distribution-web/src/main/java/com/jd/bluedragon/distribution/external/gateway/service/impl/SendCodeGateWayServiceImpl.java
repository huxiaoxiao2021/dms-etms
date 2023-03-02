package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.CheckSendCodeRequest;
import com.jd.bluedragon.common.dto.sendcode.SendCodeStatusEnum;
import com.jd.bluedragon.common.dto.sendcode.request.SendCodeSealInfoQuery;
import com.jd.bluedragon.common.dto.sendcode.response.BatchSendCarInfoDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeInfoDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeSealInfoDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BasicSelectWsManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.rest.base.SiteResource;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.rest.sendprint.SendPrintResource;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendInfoResponse;
import com.jd.bluedragon.distribution.sendprint.domain.BatchSendResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.SendCodeGateWayService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.*;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.NOT_SUPPORT_MAIN_LINE_TASK_CODE;

/**
 * @author : xumigen
 * @date : 2019/7/27
 */
public class SendCodeGateWayServiceImpl implements SendCodeGateWayService {
    private static final Logger log = LoggerFactory.getLogger(SendCodeGateWayServiceImpl.class);

    @Resource
    private SendPrintResource sendPrintResource;

    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Autowired
    private DepartureService departureService;

    @Autowired
    @Qualifier("siteResource")
    private SiteResource siteResource;
    @Autowired
    private UccPropertyConfiguration uccConfig;
    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;
    
    @Autowired
    @Qualifier("sendVehicleTransactionManager")
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private BasicSelectWsManager basicSelectWsManager;
    
    @Autowired
    SendMService sendMService;
    
    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private NewSealVehicleService newsealVehicleService;
    
    private static final Integer PAGE_SIZE = 1000;
    
    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.carrySendCarInfoNew",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BatchSendCarInfoDto>> carrySendCarInfoNew(List<String> sendCodes) {
        JdCResponse<List<BatchSendCarInfoDto>> jdCResponse = new JdCResponse<>();
        if(CollectionUtils.isEmpty(sendCodes)){
            jdCResponse.toError("参数不能为空");
            return jdCResponse;
        }
        List<BatchSend> batchSendList = new ArrayList<>();
        for(String item : sendCodes){
            BatchSend batchSend = new BatchSend();
            batchSend.setSendCode(item);
            batchSendList.add(batchSend);
        }
        BatchSendInfoResponse batchSendInfoResponse = sendPrintResource.carrySendCarInfo(batchSendList);
        List<BatchSendResult> batchSendResultList = batchSendInfoResponse.getData();
        if(!Objects.equals(batchSendInfoResponse.getCode(), JdResponse.CODE_OK)){
            jdCResponse.toError(batchSendInfoResponse.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(batchSendInfoResponse.getMessage());
        if(CollectionUtils.isNotEmpty(batchSendResultList)){
            List<BatchSendCarInfoDto> infoDtoList = new ArrayList<>();
            for(BatchSendResult item : batchSendResultList){
                BatchSendCarInfoDto infoDto = new BatchSendCarInfoDto();
                infoDto.setPackageBarNum(item.getPackageBarNum());
                infoDto.setSendCode(item.getSendCode());
                infoDto.setTotalBoxNum(item.getTotalBoxNum());
                infoDtoList.add(infoDto);
            }
            jdCResponse.setData(infoDtoList);
        }
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeStatus",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeCheckDto> checkSendCodeStatus(String sendCode) {
        InvokeResult<AbstractMap.Entry<Integer, String>> invokeResult = deliveryResource.checkSendCodeStatus(sendCode);
        JdCResponse<SendCodeCheckDto> jdCResponse = new JdCResponse<>();
        if(invokeResult == null){
            jdCResponse.toError("接口返回错误！");
            return jdCResponse;
        }
        if(!Objects.equals(invokeResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        //成功也会返回数据
        AbstractMap.Entry<Integer, String> entry = invokeResult.getData();
        if(entry != null){
            SendCodeCheckDto dto = new SendCodeCheckDto();
            dto.setKey(entry.getKey());
            dto.setValue(entry.getValue());
            jdCResponse.setData(dto);
        }
        jdCResponse.toSucceed(invokeResult.getMessage());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.commonCheckSendCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse commonCheckSendCode(String sendCode) {
        JdCResponse<SendCodeCheckDto> jdCResponse = new JdCResponse<>();
        InvokeResult<Boolean> result = deliveryResource.commonCheckSendCode(sendCode);
        if(result == null){
            jdCResponse.toError("接口返回错误！");
            return jdCResponse;
        }
        jdCResponse.setCode(result.getCode());
        jdCResponse.setMessage(result.getMessage());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeAndAlliance",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAlliance(String sendCode) {
        JdCResponse<SendCodeCheckDto> jdCResponse = this.checkSendCodeStatus(sendCode);
        JdVerifyResponse<SendCodeCheckDto> jdVerifyResponse = new JdVerifyResponse<>();
        if(!Objects.equals(jdCResponse.getCode(),JdCResponse.CODE_SUCCESS)){
            jdVerifyResponse.toError(jdCResponse.getMessage());
            return jdVerifyResponse;
        }

        jdVerifyResponse.toSuccess(jdCResponse.getMessage());
        jdVerifyResponse.setData(jdCResponse.getData());
        //判断加盟 给页面返回提示类型信息
        Integer receiveSite = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
        if(receiveSite == null){
            return jdVerifyResponse;
        }
        BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
        if(dto != null && BusinessUtil.isAllianceBusiSite(dto.getSiteType(),dto.getSubType())){
            jdVerifyResponse.addPromptBox(0,"派送至加盟商请复重！");
        }
        return jdVerifyResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeAndAlliance",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})    
    public JdVerifyResponse<SendCodeCheckDto> checkSendCodeAndAllianceForJy(CheckSendCodeRequest request) {
        log.info("jy checkSendCodeAndAllianceForJy request:{}", JsonHelper.toJson(request));

        JdCResponse<SendCodeCheckDto> jdCResponse = this.checkSendCodeStatus(request.getSendCode());
        JdVerifyResponse<SendCodeCheckDto> jdVerifyResponse = new JdVerifyResponse<>();
        if(!Objects.equals(jdCResponse.getCode(),JdCResponse.CODE_SUCCESS)){
            jdVerifyResponse.toError(jdCResponse.getMessage());
            return jdVerifyResponse;
        }

    	Integer[] sites = BusinessUtil.getSiteCodeBySendCode(request.getSendCode());
        Integer createSite = sites[0];
    	Integer receiveSite = sites[1];
        BaseStaffSiteOrgDto receiveSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
        BaseStaffSiteOrgDto createSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(createSite));
        if(ObjectHelper.isNotNull(request.getBizSource()) && uccConfig.needValidateMainLine(request.getBizSource())){
	        try {
				MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
				menuUsageConfigRequestDto.setMenuCode(Constants.MENU_CODE_SEND_GZ);
				menuUsageConfigRequestDto.setCurrentOperate(request.getCurrentOperate());
				menuUsageConfigRequestDto.setUser(request.getUser());
				MenuUsageProcessDto menuUsageProcessDto = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
				if(menuUsageProcessDto != null && Constants.FLAG_OPRATE_OFF.equals(menuUsageProcessDto.getCanUse())) {
				    Long endSiteId =new Long(BusinessUtil.getReceiveSiteCodeFromSendCode(request.getSendCode()));
				    Long startSiteId =new Long(request.getCurrentOperate().getSiteCode());
				    boolean isTrunkOrBranch = sendVehicleTransactionManager.isTrunkOrBranchLine(startSiteId, endSiteId);
				    if (isTrunkOrBranch){
				        boolean needIntercept = Boolean.TRUE;
				        //补充判断运力的运输方式是否包含铁路或者航空
                        if(receiveSiteDto != null && createSiteDto != null){
                            TransportResourceDto transportResourceDto = new TransportResourceDto();
                            // 始发区域
                            transportResourceDto.setStartOrgCode(String.valueOf(createSiteDto.getOrgId()));
                            // 始发站
                            transportResourceDto.setStartNodeId(createSite);
                            // 目的区域
                            transportResourceDto.setEndOrgCode(String.valueOf(receiveSiteDto.getOrgId()));
                            // 目的站
                            transportResourceDto.setEndNodeId(receiveSite);
                            List<TransportResourceDto> transportResourceDtos = basicSelectWsManager.queryPageTransportResourceWithNodeId(transportResourceDto);
                            if(transportResourceDtos!=null){
                                for(TransportResourceDto trd: transportResourceDtos){
                                    if(uccConfig.notValidateTransType(trd.getTransWay())){
                                        needIntercept = Boolean.FALSE;
                                        break;
                                    }
                                }
                            }
                        }
                        if(needIntercept){
                            return new JdVerifyResponse(NOT_SUPPORT_MAIN_LINE_TASK_CODE,menuUsageProcessDto.getMsg());

                        }
				    }
				}
			} catch (Exception e) {
				log.error("checkSendCodeAndAllianceForJy：干支校验异常！", e);
			}
        }
        jdVerifyResponse.toSuccess(jdCResponse.getMessage());
        jdVerifyResponse.setData(jdCResponse.getData());
        //判断加盟 给页面返回提示类型信息
        if(receiveSite == null){
            return jdVerifyResponse;
        }
        if(receiveSiteDto != null && BusinessUtil.isAllianceBusiSite(receiveSiteDto.getSiteType(),receiveSiteDto.getSubType())){
            jdVerifyResponse.addPromptBox(0,"派送至加盟商请复重！");
        }
        return jdVerifyResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.checkSendCodeForPickupRegister",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeInfoDto> checkSendCodeForPickupRegister(String sendCode) {
        JdCResponse<SendCodeInfoDto> jdCResponse = new JdCResponse<>();
        if(StringUtils.isEmpty(sendCode)){
            jdCResponse.toError("请输入批次号！");
            return jdCResponse;
        }
        ServiceMessage<Boolean> sendCodeCheck = departureService.checkSendStatusFromVOS(sendCode);
        if(!ServiceResultEnum.SUCCESS.equals(sendCodeCheck.getResult())){//已经封车
            jdCResponse.toError("批次号已经封车，请更换批次！");
            return jdCResponse;
        }
        InvokeResult<CreateAndReceiveSiteInfo> invokeResult = siteResource.getSitesInfoBySendCode(sendCode);
        if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE || invokeResult.getData() == null){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed();
        SendCodeInfoDto sendCodeInfoDto = new SendCodeInfoDto();
        sendCodeInfoDto.setCreateSiteCode(invokeResult.getData().getCreateSiteCode());
        sendCodeInfoDto.setCreateSiteName(invokeResult.getData().getCreateSiteName());
        sendCodeInfoDto.setCreateSiteSubType(invokeResult.getData().getCreateSiteSubType());
        sendCodeInfoDto.setCreateSiteType(invokeResult.getData().getCreateSiteType());
        sendCodeInfoDto.setReceiveSiteCode(invokeResult.getData().getReceiveSiteCode());
        sendCodeInfoDto.setReceiveSiteName(invokeResult.getData().getReceiveSiteName());
        sendCodeInfoDto.setReceiveSiteSubType(invokeResult.getData().getReceiveSiteSubType());
        sendCodeInfoDto.setReceiveSiteType(invokeResult.getData().getReceiveSiteType());
        jdCResponse.setData(sendCodeInfoDto);
        return jdCResponse;
    }

	@Override
    @JProfiler(jKey = "DMSWEB.SendCodeGateWayServiceImpl.querySendCodeSealInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendCodeSealInfoDto> querySendCodeSealInfo(SendCodeSealInfoQuery query) {
		JdCResponse<SendCodeSealInfoDto> result = new JdCResponse<SendCodeSealInfoDto>();
		result.toSucceed();
		SendCodeSealInfoDto data = new SendCodeSealInfoDto();
		result.setData(data);
		
		if(query == null) {
			result.toFail("传入参数不能为空！");
			return result;
		}
		if(StringUtils.isBlank(query.getScanCode())) {
			result.toFail("请录入正确的包裹号|批次号！");
			return result;
		}
		String sendCode = null;
        Integer createSite = 0;
    	Integer receiveSite = 0;
		if(BusinessUtil.isSendCode(query.getScanCode())) {
			sendCode = query.getScanCode();
	    	Integer[] sites = BusinessUtil.getSiteCodeBySendCode(sendCode);
	        createSite = sites[0];
	    	receiveSite = sites[1];
			if(createSite <= 0 || receiveSite <=0) {
				result.toFail("批次号对应的始发|目的无效！");
				return result;
			}
		}else if(WaybillUtil.isPackageCode(query.getScanCode())) {
			//根据包裹和场地查询批次
			if(query.getCurrentOperate() == null || query.getCurrentOperate().getSiteCode() <=0) {
				result.toFail("按包裹查询，操作网点不能为空！！");
				return result;
			}
			createSite = query.getCurrentOperate().getSiteCode();
            List<SendDetail> sendDetailList=sendDetailService.findByWaybillCodeOrPackageCode(createSite,null,query.getScanCode());
            if (CollectionUtils.isNotEmpty(sendDetailList)){
            	sendCode = sendDetailList.get(0).getSendCode();
            	if(StringUtils.isBlank(sendCode)) {
        			result.toFail("该包裹在当前场地未发货！");
        			return result;
            	}
            	receiveSite = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
            }else {
    			result.toFail("该包裹在当前场地未发货！");
    			return result;
            }
		}else {
			result.toFail("请录入正确的包裹号|批次号！");
			return result;
		}
    	
        BaseStaffSiteOrgDto createSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(createSite));
        BaseStaffSiteOrgDto receiveSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
        if(createSiteDto != null) {
        	data.setCreateSiteCode(createSiteDto.getSiteCode());
        	data.setCreateSiteName(createSiteDto.getSiteName());
        }
        if(receiveSiteDto != null) {
        	data.setReceiveSiteCode(receiveSiteDto.getSiteCode());
        	data.setReceiveSiteName(receiveSiteDto.getSiteName());
        }
        data.setSendCode(sendCode);
        
        // 考虑到大批次问题，分页查询
        Set<String> packageCodes = new HashSet<String>();
        Set<String> boxCodes = new HashSet<String>();
        Set<String> boardCodes = new HashSet<String>();
        Integer pageNumber = 1;
        List<SendM> scanDataList = this.sendMService.selectBoxCodeBySiteAndSendCode(createSite, sendCode,pageNumber, PAGE_SIZE);
        while (!CollectionUtils.isEmpty(scanDataList)) {
            for (SendM scanData : scanDataList) {
                if (StringUtils.isNotBlank(scanData.getBoardCode())) {
                    boardCodes.add(scanData.getBoardCode());
                }
                if (BusinessHelper.isBoxcode(scanData.getBoxCode())) {
                    boxCodes.add(scanData.getBoxCode());
                } else {
                    packageCodes.add(scanData.getBoxCode());
                }
            }
            pageNumber++;
            scanDataList = this.sendMService.selectBoxCodeBySiteAndSendCode(createSite, sendCode, pageNumber, PAGE_SIZE);
        }
        data.setScanPackageNum(packageCodes.size());
        data.setScanBoxNum(boxCodes.size());
        data.setScanBoardNum(boardCodes.size());
        //查询封车状态信息
        if (newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
            data.setSealStatusCode(SendCodeStatusEnum.SEALED.getCode());
            data.setSealStatusName(SendCodeStatusEnum.SEALED.getName());
        }else {
            data.setSealStatusCode(SendCodeStatusEnum.UNSEAL.getCode());
            data.setSealStatusName(SendCodeStatusEnum.UNSEAL.getName());
        }
        //获取封车时间、封车操作人
		return result;
	}
}
