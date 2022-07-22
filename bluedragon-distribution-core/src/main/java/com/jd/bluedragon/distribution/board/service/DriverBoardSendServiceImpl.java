package com.jd.bluedragon.distribution.board.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.common.KeyValueDto;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardCancelSendRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendCheckBatchCodeRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.response.base.ClientResult;
import com.jd.bluedragon.distribution.api.response.base.ResultCodeConstant;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.CarrierDriverDto;
import com.jd.tms.basic.dto.CarrierDriverParamDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.transboard.api.service.IVirtualBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 司机按板发货接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-12 17:21:03 周五
 */
@Service
public class DriverBoardSendServiceImpl implements DriverBoardSendService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IVirtualBoardService virtualBoardService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private DepartureService departureService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private LoadBillService loadBillService;

    @Autowired
    private BoxRelationService boxRelationService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private CarrierQueryWSManager carrierQueryWSManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 检查批次
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    @Override
    public ClientResult<KeyValueDto<Integer, String>> checkBatchCodeStatus(DriverBoardSendCheckBatchCodeRequest request) {
        if(log.isInfoEnabled()){
            log.info("DriverBoardSendServiceImpl.sendForWholeBoard param: {}", JsonHelper.toJson(request));
        }
        ClientResult<KeyValueDto<Integer, String>> result = ClientResult.success();

        try {
            final ClientResult<Void> checkResult = this.check4CheckBatchCodeStatusParam(request);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getBatchCode());
            final OperateUser operateUser = request.getOperateUser();
            operateUser.setSiteCode(createSiteCode);
            if(receiveSiteCode == null){//批次号是否符合编码规范，不合规范直接返回参数错误
                return result.toFail(HintService.getHint(HintCodeConstants.SEND_CODE_ILLEGAL), InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            }
            BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
            Integer siteType = 0;
            if (null != baseSiteInfo) {
                siteType = baseSiteInfo.getSiteType();
                String asmType = PropertiesHelper.newInstance().getValue("asm_type");//售后
                String wmsType = PropertiesHelper.newInstance().getValue("wms_type");//仓储
                String spwmsType = PropertiesHelper.newInstance().getValue("spwms_type");//备件库退货
                if (siteType == Integer.parseInt(asmType) || siteType == Integer.parseInt(wmsType) || siteType == Integer.parseInt(spwmsType)) {
                    return result.toFail("禁止逆向操作！", InvokeResult.RESULT_THIRD_ERROR_CODE);
                }
            }

            ServiceMessage<Boolean> departureResult = departureService.checkSendStatusFromVOS(request.getBatchCode());
            if (ServiceResultEnum.WRONG_STATUS.equals(departureResult.getResult())) {//已被封车
                return result.toFail(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED), InvokeResult.RESULT_THIRD_ERROR_CODE);
            } else if (ServiceResultEnum.SUCCESS.equals(departureResult.getResult())) {//未被封车
                BaseStaffSiteOrgDto site = siteService.getSite(receiveSiteCode);
                if(site == null){
                    return result.toFail("未获取到该站点名称", InvokeResult.RESULT_THIRD_ERROR_CODE);
                }
                result.setData(new KeyValueDto<>(1, site.getSiteName()));
            } else {
                return result.toFail(departureResult.getErrorMsg());
            }

            //判断加盟 给页面返回提示类型信息
            Integer receiveSite = BusinessUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());
            if(receiveSite == null){
                return result;
            }
            BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
            if(dto != null && BusinessUtil.isAllianceBusiSite(dto.getSiteType(),dto.getSubType())){
                result.addPromptBox(0,"派送至加盟商请复重！");
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DriverBoardSendServiceImpl.checkSendCodeStatus exception param {} exception {}", JsonHelper.toJson(request), e.getMessage(), e);
        }
        return result;
    }

    private ClientResult<Void> check4CheckBatchCodeStatusParam(DriverBoardSendCheckBatchCodeRequest request){
        ClientResult<Void> result = ClientResult.success();
        if(request == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final OperateUser operateUser = request.getOperateUser();
        if(StringUtils.isEmpty(operateUser.getUserCode())){
            return result.toFail("参数错误，userCode不能为空");
        }

        if(StringUtils.isEmpty(request.getCarrierCode())){
            return result.toFail("参数错误，carrierCode不能为空");
        }
        if(StringUtils.isEmpty(request.getBatchCode())){
            return result.toFail("参数错误，batchCode不能为空");
        }
        if(!BusinessUtil.isSendCode(request.getBatchCode())){
            return result.toFail("请扫描正确的批次号");
        }
        return result;
    }

    /**
     * 扫条码按整板发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:08 周五
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DriverBoardSendServiceImpl.sendForWholeBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ClientResult<Boolean> sendForWholeBoard(DriverBoardSendRequest request) {
        if(log.isInfoEnabled()){
            log.info("DriverBoardSendServiceImpl.sendForWholeBoard param: {}", JsonHelper.toJson(request));
        }
        ClientResult<Boolean> result = ClientResult.success();

        try {
            final ClientResult<Void> checkResult = this.checkSendForWholeBoardPram(request);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            InvokeResult<Boolean> batchCheckResult = sendCodeService.validateSendCodeEffective(request.getBatchCode());
            if (!batchCheckResult.codeSuccess()) {
                return result.toFail(batchCheckResult.getMessage(), batchCheckResult.getCode());
            }

            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getBatchCode());
            final OperateUser operateUser = request.getOperateUser();
            operateUser.setSiteCode(createSiteCode);
            operateUser.setUserId(-1L);
            request.setReceiveSiteCode(receiveSiteCode);

            // 获取司机信息
            final CarrierDriverParamDto carrierDriverParamDto = new CarrierDriverParamDto();
            carrierDriverParamDto.setAccountCode(operateUser.getUserCode());
            carrierDriverParamDto.setCarrierCode(request.getCarrierCode());
            final CommonDto<CarrierDriverDto> driverResult = carrierQueryWSManager.getCarrierDriverByParam(carrierDriverParamDto);
            if(driverResult == null || driverResult.getCode() != CommonDto.CODE_SUCCESS){
                log.error("DriverBoardSendServiceImpl.sendForWholeBoard getCarrierDriverByParam fail {} param: {}", JsonHelper.toJson(driverResult), JsonHelper.toJson(carrierDriverParamDto));
                return result.toFail("获取司机信息失败，请稍后重试");
            }
            final CarrierDriverDto driverDto = driverResult.getData();
            if(driverDto == null){
                log.error("DriverBoardSendServiceImpl.sendForWholeBoard getCarrierDriverByParam empty {} param: {}", JsonHelper.toJson(driverResult), JsonHelper.toJson(carrierDriverParamDto));
                return result.toFail("未获取到司机信息");
            }
            operateUser.setUserName(driverDto.getDriverName());

            final PackageSendRequest packageSendRequest = this.convertDriverBoardSendRequestToPackageSendRequest(request, driverDto);
            packageSendRequest.setBizSource(SendBizSourceEnum.BOARD_SEND.getCode());
            SendM sendM = this.toSendMDomain(packageSendRequest);
            sendM.setBoxCode(request.getBarCode());

            final ClientResult<SendResult> sendResultResult = this.handleSendByPackageOrBoxCodeForWholeBoard(packageSendRequest, sendM);
            SendResult sendResult = sendResultResult.getData();

            if(!sendResultResult.isSuccess()){
                return result.toFail(sendResult.getValue());
            }
            if (Objects.equals(sendResult.getKey(), SendResult.CODE_OK)) {
                return result.toSuccess(sendResult.getValue());
            }

            if (Objects.equals(sendResult.getKey(), SendResult.CODE_WARN)) {
                return result.addPromptBox(sendResult.getKey(), sendResult.getValue());
            }

            if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
                return result.addInterceptBox(sendResult.getKey(), sendResult.getValue());
            }

            if(Objects.equals(sendResult.getKey(),SendResult.CODE_CONFIRM)){
                if(sendResult.getInterceptCode()!= null && (sendResult.getInterceptCode() == ResponseCodeConstants.JdVerifyResponseMsgBox.SEND_WRONG_SITE.getCode() || sendResult.getInterceptCode() == ResponseCodeConstants.JdVerifyResponseMsgBox.CANCEL_LAST_SEND.getCode())){
                    ClientResult.MsgBox msgBox = new ClientResult.MsgBox();
                    msgBox.setType(ClientResult.MsgBoxTypeEnum.CONFIRM.getType());
                    msgBox.setCode(sendResult.getInterceptCode());
                    msgBox.setMsg(sendResult.getValue());
                    msgBox.setData(sendResult.getReceiveSiteCode());
                    result.addBox(msgBox);
                }else{
                    result.addConfirmBox(sendResult.getKey(),sendResult.getValue());
                }
                return result;
            }
            result.addInterceptBox(sendResult.getKey(),sendResult.getValue());
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.getBoardUnFinishInfo--exception param {} exception {}", JsonHelper.toJson(request), e.getMessage(), e);
        }

        return result;
    }

    private ClientResult<Void> checkSendForWholeBoardPram(DriverBoardSendRequest request){
        ClientResult<Void> result = ClientResult.success();
        if(request == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final OperateUser operateUser = request.getOperateUser();
        if(StringUtils.isEmpty(operateUser.getUserCode())){
            return result.toFail("参数错误，userCode不能为空");
        }
        if(StringUtils.isEmpty(request.getCarrierCode())){
            return result.toFail("参数错误，carrierCode不能为空");
        }
        if(StringUtils.isEmpty(request.getBatchCode())){
            return result.toFail("参数错误，batchCode不能为空");
        }
        if(!BusinessUtil.isSendCode(request.getBatchCode())){
            return result.toFail("请扫描正确的批次号");
        }
        if(StringUtils.isEmpty(request.getBarCode())){
            return result.toFail("参数错误，barCode不能为空");
        }
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        return result;
    }

    private PackageSendRequest convertDriverBoardSendRequestToPackageSendRequest(DriverBoardSendRequest request, CarrierDriverDto driverDto){
        final PackageSendRequest packageSendRequest = new PackageSendRequest();
        packageSendRequest.setReceiveSiteCode(request.getReceiveSiteCode());
        packageSendRequest.setBoxCode(request.getBarCode());
        packageSendRequest.setSendCode(request.getBatchCode());
        packageSendRequest.setIsForceSend(request.getIsForceSend());
        packageSendRequest.setIsCancelLastSend(request.getCancelLastSend());
        packageSendRequest.setSendForWholeBoard(request.getSendForWholeBoard());
        packageSendRequest.setBusinessType(request.getBusinessType());

        final OperateUser operateUser = request.getOperateUser();
        // 操作场地名称，按批次始发地取
        packageSendRequest.setSiteCode(operateUser.getSiteCode());
        packageSendRequest.setSiteName(operateUser.getSiteName());
        packageSendRequest.setUserCode(operateUser.getUserId().intValue());
        packageSendRequest.setUserName(operateUser.getUserName());
        return packageSendRequest;
    }

    /**
     * 请求拼装SendM发货对象
     * @param request
     * @return
     */
    private SendM toSendMDomain(PackageSendRequest request) {
        SendM domain = new SendM();
        domain.setReceiveSiteCode(request.getReceiveSiteCode());
        domain.setSendCode(request.getSendCode());
        domain.setCreateSiteCode(request.getSiteCode());

        String turnoverBoxCode = request.getTurnoverBoxCode();
        if (StringUtils.isNotBlank(turnoverBoxCode) && turnoverBoxCode.length() > 30) {
            domain.setTurnoverBoxCode(turnoverBoxCode.substring(0, 30));
        } else {
            domain.setTurnoverBoxCode(turnoverBoxCode);
        }
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setTransporttype(request.getTransporttype());

        domain.setBizSource(request.getBizSource());

        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis()));
        domain.setOperateTime(new Date(System.currentTimeMillis()));
        return domain;
    }

    /**
     * 按包裹号、箱号找到整板进行整板发货
     * @author fanggang7
     * @time 2021-08-24 18:31:56 周二
     */
    private ClientResult<SendResult> handleSendByPackageOrBoxCodeForWholeBoard(PackageSendRequest request, SendM domain) {
        ClientResult<SendResult> result = ClientResult.success();
        // 根据箱号找到板号
        final Response<Board> boardResult = virtualBoardService.getBoardByBarCode(request.getBoxCode(), request.getSiteCode());
        if(!Objects.equals(boardResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("handleSendByPackageOrBoxCodeForWholeBoard fail {}", JsonHelper.toJson(boardResult));
            return result.toFail("根据包裹或箱号查找板号数据异常", InvokeResult.SERVER_ERROR_CODE);
        }
        final Board board = boardResult.getData();
        if(board == null){
            SendResult sendResult = new SendResult(SendResult.CODE_SENDED, "根据包裹或箱号未找到对应板数据");
            result.toFail(sendResult.getValue());
            return result.setData(sendResult);
        }
        domain.setBoardCode(board.getCode());
        log.info("handleSendByPackageOrBoxCodeForWholeBoard param: {}", JsonHelper.toJson(domain));
        final SendResult sendResult = deliveryService.boardSend(domain, request.getIsForceSend());
        return result.setData(sendResult);
    }

    /**
     * 获取批次信息
     * @param request 请求参数
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    @Override
    public ClientResult<BaseResponse> getBatchInfo4CancelSend(DriverBoardSendCheckBatchCodeRequest request) {
        if(log.isInfoEnabled()){
            log.info("DriverBoardSendServiceImpl.getBatchInfo4CancelSend param: {}", JsonHelper.toJson(request));
        }
        ClientResult<BaseResponse> result = ClientResult.success();

        try {
            final ClientResult<Void> checkResult = this.check4GetBatchInfo4CancelSendParam(request);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getBatchCode());
            final OperateUser operateUser = request.getOperateUser();
            operateUser.setSiteCode(createSiteCode);
            if(receiveSiteCode == null){//批次号是否符合编码规范，不合规范直接返回参数错误
                return result.toFail(HintService.getHint(HintCodeConstants.SEND_CODE_ILLEGAL), InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            }
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);

            if (null == dto) {
                log.warn("没有对应站点:code={}" , receiveSiteCode);
                return result.toFail(String.format("没有对应id为%s站点", receiveSiteCode), JdResponse.CODE_NOT_FOUND);
            }
            BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
            response.setSiteCode(dto.getSiteCode());
            response.setSiteName(dto.getSiteName());
            String dmsSiteCode = dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "";
            response.setDmsCode(dmsSiteCode);
            response.setSiteType(dto.getSiteType());
            response.setSubType(dto.getSubType());
            response.setOrgId(dto.getOrgId());
            response.setSiteBusinessType(dto.getSiteBusinessType());
            result.setData(response);

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DriverBoardSendServiceImpl.getBatchInfo4CancelSend exception param {} exception {}", JsonHelper.toJson(request), e.getMessage(), e);
        }
        return result;
    }

    private ClientResult<Void> check4GetBatchInfo4CancelSendParam(DriverBoardSendCheckBatchCodeRequest request){
        ClientResult<Void> result = ClientResult.success();
        if(request == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final OperateUser operateUser = request.getOperateUser();
        if(StringUtils.isEmpty(operateUser.getUserCode())){
            return result.toFail("参数错误，userCode不能为空");
        }

        if(StringUtils.isEmpty(request.getCarrierCode())){
            return result.toFail("参数错误，carrierCode不能为空");
        }
        if(StringUtils.isEmpty(request.getBatchCode())){
            return result.toFail("参数错误，batchCode不能为空");
        }
        return result;
    }

    /**
     * 扫条码按整板取消发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:30 周五
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DriverBoardSendServiceImpl.cancelSendForWholeBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ClientResult<Boolean> cancelSendForWholeBoard(DriverBoardCancelSendRequest request) {
        if(log.isInfoEnabled()){
            log.info("DriverBoardSendServiceImpl.cancelSendForWholeBoard param: {}", JsonHelper.toJson(request));
        }
        ClientResult<Boolean> result = ClientResult.success();

        try {
            final ClientResult<Void> checkResult = this.checkCancelSendForWholeBoardPram(request);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getBatchCode());
            final OperateUser operateUser = request.getOperateUser();
            operateUser.setSiteCode(createSiteCode);
            request.setReceiveSiteCode(receiveSiteCode);
            operateUser.setUserId(-1L);

            // 获取司机信息
            final CarrierDriverParamDto carrierDriverParamDto = new CarrierDriverParamDto();
            carrierDriverParamDto.setAccountCode(operateUser.getUserCode());
            carrierDriverParamDto.setCarrierCode(request.getCarrierCode());
            final CommonDto<CarrierDriverDto> driverResult = carrierQueryWSManager.getCarrierDriverByParam(carrierDriverParamDto);
            if(driverResult == null || driverResult.getCode() != CommonDto.CODE_SUCCESS){
                log.error("DriverBoardSendServiceImpl.sendForWholeBoard getCarrierDriverByParam fail {} param: {}", JsonHelper.toJson(driverResult), JsonHelper.toJson(carrierDriverParamDto));
                return result.toFail("获取司机信息失败，请稍后重试");
            }
            final CarrierDriverDto driverDto = driverResult.getData();
            if(driverDto == null){
                log.error("DriverBoardSendServiceImpl.sendForWholeBoard getCarrierDriverByParam empty {} param: {}", JsonHelper.toJson(driverResult), JsonHelper.toJson(carrierDriverParamDto));
                return result.toFail("未获取到司机信息");
            }
            operateUser.setUserName(driverDto.getDriverName());

            final DeliveryRequest deliveryRequest = this.convertDriverBoardCancelSendRequestToDeliveryRequest(request);

            DeliveryResponse deliveryResponse = sendLoadBillCheck(deliveryRequest);
            if (!deliveryResponse.getCode().equals(JdResponse.CODE_OK)) {
                return result.toFail(HintService.getHint(HintCodeConstants.PRE_LOAD_CANNOT_CANCEL), JdResponse.CODE_UNLOADBILL);
            }

            // 如果是按包裹找整板进行发货
            final InvokeResult<Void> handleCancelSendByPackageOrBoxCodeForWholeBoardResult = this.handleCancelSendByPackageOrBoxCodeForWholeBoard(deliveryRequest);
            if(!Objects.equals(handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                return result.toFail(handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getMessage(), handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getCode());
            }

            SendM sendMDomain = toSendM(deliveryRequest);
            // 取消发货校验封车业务
            DeliveryResponse checkResponse = deliveryService.dellCancelDeliveryCheckSealCar(sendMDomain);
            if (checkResponse!=null && !JdResponse.CODE_OK.equals(checkResponse.getCode())) {
                return result.toFail(checkResponse.getMessage());
            }

            ThreeDeliveryResponse tDeliveryResponse = null;
            try {
                tDeliveryResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendMDomain, true);

                // BC箱号取消成功后，同步取消WJ箱号的发货
                if (ObjectUtils.equals(JdResponse.CODE_OK, tDeliveryResponse.getCode())) {
                    List<SendM> relationSendList = new DeliveryCancelSendMGen().createBoxRelationSendM(Collections.singletonList(deliveryRequest));
                    for (SendM sendM : relationSendList) {
                        long startTime = System.currentTimeMillis();
                        tDeliveryResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                        long endTime = System.currentTimeMillis();
                        this.addFileSendingBizLog(sendMDomain, sendM, JsonHelper.toJson(tDeliveryResponse), startTime, endTime);
                    }
                }

            } catch (Exception e) {
                this.log.error("写入取消发货信息失败：{}",JsonHelper.toJson(request), e);
            }
            if (tDeliveryResponse != null) {
                if (ObjectUtils.equals(JdResponse.CODE_OK, tDeliveryResponse.getCode())) {
                    return result;
                } else {
                    return result.toFail(tDeliveryResponse.getMessage(), tDeliveryResponse.getCode());
                }
            } else {
                return result.toFail(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
            }

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DriverBoardSendServiceImpl.cancelSendForWholeBoard exception param {} exception {}", JsonHelper.toJson(request), e.getMessage(), e);
        }
        return result;
    }

    private ClientResult<Void> checkCancelSendForWholeBoardPram(DriverBoardCancelSendRequest request){
        ClientResult<Void> result = ClientResult.success();
        if(request == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final OperateUser operateUser = request.getOperateUser();
        if(StringUtils.isEmpty(operateUser.getUserCode())){
            return result.toFail("参数错误，userCode不能为空");
        }

        if(StringUtils.isEmpty(request.getCarrierCode())){
            return result.toFail("参数错误，carrierCode不能为空");
        }
        if(StringUtils.isEmpty(request.getBatchCode())){
            return result.toFail("参数错误，batchCode不能为空");
        }
        if(StringUtils.isEmpty(request.getBarCode())){
            return result.toFail("参数错误，barCode不能为空");
        }
        return result;
    }

    private DeliveryRequest convertDriverBoardCancelSendRequestToDeliveryRequest(DriverBoardCancelSendRequest request){
        final DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setReceiveSiteCode(request.getReceiveSiteCode());
        deliveryRequest.setBoxCode(request.getBarCode());
        deliveryRequest.setSendCode(request.getBatchCode());
        deliveryRequest.setCancelWholeBoard(request.getCancelWholeBoard());
        deliveryRequest.setBusinessType(request.getBusinessType());

        final OperateUser operateUser = request.getOperateUser();
        // 操作场地名称，按批次始发地取
        deliveryRequest.setSiteCode(operateUser.getSiteCode());
        deliveryRequest.setSiteName(operateUser.getSiteName());
        deliveryRequest.setUserName(operateUser.getUserName());
        deliveryRequest.setUserCode(operateUser.getUserId().intValue());
        return deliveryRequest;
    }

    /**
     * 取消发货 要判断是否已经装载， 未装载和拒绝的可以 取消 发货\分拣
     */
    private DeliveryResponse sendLoadBillCheck(DeliveryRequest request) {
        LoadBillReport loadBillReport = new LoadBillReport();
        if (BusinessHelper.isBoxcode(request.getBoxCode())) {
            loadBillReport.setBoxCode(request.getBoxCode());
        } else {
            loadBillReport.setWaybillCode(request.getBoxCode());
        }
        log.debug("开始获取 装载数据");
        try {
            List<LoadBill> loadBillList = loadBillService.findWaybillInLoadBill(loadBillReport);

            /**  loadBillList 空时表示未装载 可以取消，
             *  10初始,20已申请,30已放行, 【40未放行】
             */
            if (loadBillList != null && !loadBillList.isEmpty()) {
                for (LoadBill bill : loadBillList) {
                    if (bill.getApprovalCode().equals(LoadBill.REDLIGHT) || !bill.getDmsCode().equals(request.getSiteCode())) {
                        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
                    }
                }
            }
            return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        } catch (Exception e) {
            this.log.error("开始获取 装载数据 失败 findWaybillInLoadBill:{}", JsonHelper.toJson(request), e);
        }
        return new DeliveryResponse(JdResponse.CODE_UNLOADBILL, HintService.getHint(HintCodeConstants.PRE_LOAD_CANNOT_CANCEL));
    }

    /**
     * 按包裹号、箱号找到整板进行整板取消发货
     * @author fanggang7
     * @time 2021-08-24 18:31:56 周二
     */
    private InvokeResult<Void> handleCancelSendByPackageOrBoxCodeForWholeBoard(DeliveryRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        final boolean isCancelPackageForWholeBoard = (WaybillUtil.isPackageCode(request.getBoxCode()) || BusinessUtil.isBoxcode(request.getBoxCode())) && Objects.equals(request.getCancelWholeBoard(), Constants.YN_YES);
        if(!isCancelPackageForWholeBoard){
            return result;
        }
        // 根据箱号找到板号
        final Response<Board> boardResult = virtualBoardService.getBoardByBarCode(request.getBoxCode(), request.getSiteCode());
        if(!Objects.equals(boardResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("handleSendByPackageOrBoxCodeForWholeBoard fail {}", JsonHelper.toJson(boardResult));
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("根据包裹或箱号查找板号数据异常");
            return result;
        }
        final Board board = boardResult.getData();
        if(board == null){
            result.setCode(InvokeResult.RESULT_NULL_CODE);
            result.setMessage("根据包裹或箱号未找到对应板数据");
            return result;
        }
        request.setBoxCode(board.getCode());
        return result;
    }

    private SendM toSendM(DeliveryRequest request) {
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getBoxCode());
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setUpdaterUser(request.getUserName());
        sendM.setSendType(request.getBusinessType());
        sendM.setUpdateUserCode(request.getUserCode());
        sendM.setSendCode(request.getSendCode());
        Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
        sendM.setOperateTime(operateTime);
        if (!BusinessHelper.isBoxcode(request.getBoxCode())) {
            sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        }
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 文件发货添加Business Log
     * @param BCSendM
     * @param fileSendM
     * @param result
     * @param startTime
     * @param endTime
     */
    private void addFileSendingBizLog(SendM BCSendM, SendM fileSendM, String result, Long startTime, Long endTime) {
        JSONObject operateRequest = new JSONObject();
        operateRequest.put("bcSendM", JsonHelper.toJson(BCSendM));
        operateRequest.put("wjSendM", JsonHelper.toJson(fileSendM));

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_FILE_BOX_CANCEL)
                .operateRequest(operateRequest)
                .operateResponse(result)
                .processTime(endTime, startTime)
                .methodName("DriverBoardSendServiceImpl#cancelSendForWholeBoard")
                .build();

        logEngine.addLog(businessLogProfiler);
    }

    public abstract class AbstractSendMGen {

        protected abstract SendM makeSendMFromRequest(BoxRelation relation, DeliveryRequest request);

        List<SendM> createBoxRelationSendM(List<DeliveryRequest> requests) {
            List<SendM> sendMList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(requests)) {

                for (DeliveryRequest request : requests) {
                    if (!BusinessUtil.isBoxcode(request.getBoxCode()) || null == request.getSiteCode()) {
                        continue;
                    }
                    InvokeResult<List<BoxRelation>> sr = boxRelationService.getRelationsByBoxCode(request.getBoxCode());
                    if (!sr.codeSuccess() || CollectionUtils.isEmpty(sr.getData())) {
                        continue;
                    }

                    for (BoxRelation relation : sr.getData()) {
                        if (StringUtils.isBlank(relation.getRelationBoxCode())) {
                            continue;
                        }
                        sendMList.add(makeSendMFromRequest(relation, request));
                    }
                }
            }

            return sendMList;
        }
    }

    protected class DeliveryCancelSendMGen extends AbstractSendMGen {

        @Override
        protected SendM makeSendMFromRequest(BoxRelation relation, DeliveryRequest request) {
            SendM sendM = toSendM(request);
            sendM.setBoxCode(relation.getRelationBoxCode());
            return sendM;
        }
    }

}
