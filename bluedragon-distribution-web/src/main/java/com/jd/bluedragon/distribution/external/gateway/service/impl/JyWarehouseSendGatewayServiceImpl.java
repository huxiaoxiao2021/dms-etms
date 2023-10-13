package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JySendFlowConfigEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.enums.FocusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.request.ValidSendCodeReq;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntityQueryDto;
import com.jd.bluedragon.distribution.jy.constants.JyMixScanTaskCompleteEnum;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.comboard.impl.JyGroupSortCrossDetailCacheService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JyWarehouseSendVehicleServiceImpl;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendServiceImpl.checkCTTCode;

@Service
public class JyWarehouseSendGatewayServiceImpl implements JyWarehouseSendGatewayService {
    private static final Logger log = LoggerFactory.getLogger(JyWarehouseSendGatewayServiceImpl.class);


    private static final String GROUP_NAME_PREFIX= "混扫%s";
    private static final Integer DEFAULT_PAGE_MAXSIZE = 100;

    @Autowired
    private JyWarehouseSendVehicleServiceImpl jyWarehouseSendVehicleService;
    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    
    @Autowired
    JimDbLock jimDbLock;
    @Autowired
    private JyGroupSortCrossDetailCacheService jyGroupSortCrossDetailCacheService;
    
    @Autowired
    @Qualifier("jySealVehicleService")
    private JySealVehicleService jySealVehicleService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    private void checkUser(User user) {
        if(Objects.isNull(user) || StringUtils.isBlank(user.getUserErp())) {
            throw new JyBizException("操作人erp为空");
        }

    }
    private void checkCurrentOperate(CurrentOperate currentOperate) {
        if(Objects.isNull(currentOperate) || Objects.isNull(currentOperate.getSiteCode())) {
            throw new JyBizException("操作场地编码为空");
        }
        if(Objects.isNull(currentOperate.getOperateTime())) {
            throw new JyBizException("操作时间为空");
        }
    }

    private void checkPage(Integer pageNo, Integer pageSize) {
        if(Objects.isNull(pageNo) || Objects.isNull(pageSize) || pageNo < 1 || pageSize < 1) {
            throw new JyBizException("页码参数错误");
        }
        if(pageSize > DEFAULT_PAGE_MAXSIZE) {//当前岗位分页信息pageSize不建议设置太大
            throw new JyBizException("每页查询数量超过最大值" + DEFAULT_PAGE_MAXSIZE);
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.vehicleStatusOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> vehicleStatusOptions() {
        List<SelectOption> optionList =getVehicleStatusEnums();
        if(CollectionUtils.isEmpty(optionList)) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "车辆状态为空", null);
        }
        Collections.sort(optionList, new SelectOption.OrderComparator());
        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    private List<SelectOption> getVehicleStatusEnums() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JySendVehicleStatusEnum _enum : JySendVehicleStatusEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getOrder());
            optionList.add(option);
        }
        return optionList;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.scanTypeOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.fetchSendVehicleTaskPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTaskPage(SendVehicleTaskRequest request) {
        final String methodDesc = "JyWarehouseSendGatewayServiceImpl.fetchSendVehicleTaskPage:接货仓发货岗按状态查询派车任务列表：";
        try{
            //参数校验
            if(Objects.isNull(request)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            checkPage(request.getPageNumber(), request.getPageSize());
            //车辆状态合法性校验
            JdCResponse illegalVehicleStatusRes = this.legalVehicleStatusCheck(request);
            if(!illegalVehicleStatusRes.isSucceed()) {
                return new JdCResponse<>(illegalVehicleStatusRes.getCode(), illegalVehicleStatusRes.getMessage(), null);
            }
            return retJdCResponse(jyWarehouseSendVehicleService.fetchSendVehicleTask(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR, null);//500+非自定义异常
        }
    }

    /**
     * 车辆状态合法性校验
     */
    private JdCResponse legalVehicleStatusCheck(SendVehicleTaskRequest request) {
        if(Objects.isNull(request.getVehicleStatus())) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "车辆状态参数为空");
        }
        List<SelectOption> optionList = this.getVehicleStatusEnums();
        if(CollectionUtils.isEmpty(optionList)) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "未查到该岗位车辆状态信息");
        }
        for (SelectOption selectOption : optionList) {
            if(request.getVehicleStatus().equals(selectOption.getCode())) {
                return new JdCResponse(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
            }
        }
        return new JdCResponse(JdCResponse.CODE_FAIL, "车辆状态参数不合法");
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.fetchToSendAndSendingTaskPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request) {
        final String methodDesc = "JyWarehouseSendGatewayServiceImpl.fetchToSendAndSendingTaskPage:接货仓发货岗查询待发货和发货中派车任务信息：";
        try{
            //参数校验
            if(Objects.isNull(request)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            checkCurrentOperate(request.getCurrentOperate());
            checkUser(request.getUser());
            checkPage(request.getPageNo(), request.getPageSize());
            JdCResponse<AppendSendVehicleTaskQueryRes> res = new JdCResponse<>();
            res.toSucceed();

            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码参数为空");
                return res;
            }
            if(StringUtils.isBlank(request.getGroupCode())) {
                res.toFail("岗位小组参数为空");
                return res;
            }
            return retJdCResponse(jyWarehouseSendVehicleService.fetchToSendAndSendingTaskPage(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR, null);//500+非自定义异常
        }
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.sendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanRes> sendScan(SendScanReq request) {
        final String methodDesc = "JyWarehouseSendGatewayServiceImpl.sendScan:接货仓发货岗扫描：";
        try{
            //参数校验
            if(Objects.isNull(request)){
                return new JdVerifyResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            checkCurrentOperate(request.getCurrentOperate());
            checkUser(request.getUser());
            if(Objects.isNull(request.getOperateType())) {
                request.setOperateType(JySendFlowConfigEnum.GANTRY.getCode());
            }

            JdVerifyResponse<SendScanRes> res = new JdVerifyResponse<>();
            SendScanRes resData = new SendScanRes();
            res.setData(resData);
            res.toSuccess();
            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码参数为空");
                return res;
            }

            if(StringUtils.isBlank(request.getMachineCode())) {
                res.toFail("设备编码参数为空");
                return res;
            }

            if(!Objects.isNull(request.getUnfocusedFlowForceSend()) && request.getUnfocusedFlowForceSend()
                && (StringUtils.isBlank(request.getSendVehicleBizId()) || StringUtils.isBlank(request.getSendVehicleDetailBizId()))) {
                res.toFail("强发时未指定发货任务");
                return res;
            }

            request.setPost(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            return jyWarehouseSendVehicleService.scan(request, res);
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdVerifyResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdVerifyResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR, null);//500+非自定义异常
        }
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.findByQiWaybillPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<BuQiWaybillRes> findByQiWaybillPage(BuQiWaybillReq request) {
        String methodDesc = "JyWarehouseSendGatewayServiceImpl.findByQiWaybillPage:查询不齐运单数量：";
        JdCResponse<BuQiWaybillRes> res = new JdCResponse<>();
        res.toSucceed();
        try{
            if(Objects.isNull(request)) {
                res.toFail("请求信息为空");
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("{}开始，request={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            checkPage(request.getPageNo(), request.getPageSize());
            if(StringUtils.isBlank(request.getGroupCode())) {
                res.toFail("岗位组为空");
                return res;
            }
            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码为空");
                return res;
            }
            if(StringUtils.isBlank(request.getSendVehicleBizId())) {
                res.toFail("派车任务编码为空");
                return res;
            }

            return retJdCResponse(jyWarehouseSendVehicleService.findByQiWaybillPage(request));
        }catch (JyBizException jyex) {
            log.error("{}服务失败，errMsg={},请求={}", methodDesc, jyex.getMessage(), JsonHelper.toJson(request));
            res.setMessage("查询混扫任务不齐运单数量服务失败");
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，errMsg={},请求={}", methodDesc, e.getMessage(), JsonHelper.toJson(request), e);
            res.setMessage("查询混扫任务不齐运单数量服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.findByQiPackagePage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<BuQiPackageRes> findByQiPackagePage(BuQiWaybillReq request) {
        String methodDesc = "JyWarehouseSendGatewayServiceImpl.findByQiPackagePage:查询不齐运单明细：";
        JdCResponse<BuQiPackageRes> res = new JdCResponse<>();
        res.toSucceed();
        try{
            if(Objects.isNull(request)) {
                res.toFail("请求信息为空");
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("{}开始，request={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            checkPage(request.getPageNo(), request.getPageSize());
            if(StringUtils.isBlank(request.getGroupCode())) {
                res.toFail("岗位组为空");
                return res;
            }
            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码为空");
                return res;
            }
            if(StringUtils.isBlank(request.getSendVehicleBizId())) {
                res.toFail("派车任务编码为空");
                return res;
            }

            return retJdCResponse(jyWarehouseSendVehicleService.findByQiPackagePage(request));
        }catch (JyBizException jyex) {
            log.error("{}服务失败，errMsg={},请求={}", methodDesc, jyex.getMessage(), JsonHelper.toJson(request));
            res.setMessage("查询混扫任务不齐运单数量服务失败");
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，errMsg={},请求={}", methodDesc, e.getMessage(), JsonHelper.toJson(request), e);
            res.setMessage("查询混扫任务不齐运单数量服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.buQiCancelSendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> buQiCancelSendScan(BuQiCancelSendScanReq request) {
        String methodDesc = "JyWarehouseSendGatewayServiceImpl.buQiCancelSendScan:不齐取消扫描：";
        JdCResponse<Void> res = new JdCResponse<>();
        res.toSucceed();
        try{
            if(Objects.isNull(request)) {
                res.toFail("请求信息为空");
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("{}开始，request={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            if(StringUtils.isBlank(request.getGroupCode())) {
                res.toFail("岗位组为空");
                return res;
            }
            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码为空");
                return res;
            }
            if(StringUtils.isBlank(request.getSendVehicleBizId())) {
                res.toFail("派车任务编码为空");
                return res;
            }
            if(CollectionUtils.isEmpty(request.getPackList()) && CollectionUtils.isEmpty(request.getWaybillCodeList())) {
                res.toFail("不齐取消数据为空");
                return res;
            }

            return retJdCResponse(jyWarehouseSendVehicleService.buQiCancelSendScan(request));
        }catch (JyBizException jyex) {
            log.error("{}服务失败，errMsg={},请求={}", methodDesc, jyex.getMessage(), JsonHelper.toJson(request));
            res.setMessage("不齐取消扫描服务失败");
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，errMsg={},请求={}", methodDesc, e.getMessage(), JsonHelper.toJson(request), e);
            res.setMessage("不齐取消扫描服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.getMixScanTaskDefaultName",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<String> getMixScanTaskDefaultName(MixScanTaskDefaultNameQueryReq request) {
        JdCResponse<String> res = new JdCResponse<>();
        res.toSucceed();
        try{
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            res.setData(jyGroupSortCrossDetailService.getMixScanTaskDefaultName(GROUP_NAME_PREFIX));
            return res;
        }catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl.getMixScanTaskDefaultName:获取混扫任务名称失败，errMsg={}", e.getMessage(), e);
            res.setData("混扫");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.createMixScanTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<CreateMixScanTaskRes> createMixScanTask(CreateMixScanTaskReq createMixScanTaskReq) {
        JdCResponse<CreateMixScanTaskRes> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(createMixScanTaskReq.getUser());
            checkCurrentOperate(createMixScanTaskReq.getCurrentOperate());
            checkGroupCode(createMixScanTaskReq.getGroupCode());
            // 混扫任务流向校验
            checkFlowMaxAndEndSite(createMixScanTaskReq);
            
            String templateCode = jyGroupSortCrossDetailService.createMixScanTask(createMixScanTaskReq);
            if (templateCode == null) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"创建混扫任务失败");
            }
            
            CreateMixScanTaskRes res = new CreateMixScanTaskRes();
            res.setTemplateCode(templateCode);
            response.setData(res);
            return response;
        } catch (JyBizException e) {
            log.info("创建混扫任务失败：{}", JsonHelper.toJson(createMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl createMixScanTask 创建混扫任务异常 {}", JsonHelper.toJson(createMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "创建混扫任务异常！");
        }
    }

    private void checkFlowMaxAndEndSite(CreateMixScanTaskReq createMixScanTaskReq) {
        
        Integer max = jyWarehouseSendVehicleService.getFlowMaxBySiteCode(createMixScanTaskReq.getCurrentOperate().getSiteCode());
        if (max < createMixScanTaskReq.getSendFlowList().size()) {
            throw new JyBizException("流向不能超过" + max + "个,请重新选择!");
        }
        
        // 混扫任务不能存在相同流向
        HashSet<Long> endSiteSet = new HashSet<>();
        for (MixScanTaskDetailDto detailDto : createMixScanTaskReq.getSendFlowList()) {
            if (endSiteSet.contains(detailDto.getEndSiteId())) {
                throw new JyBizException("混扫任务不能包含相同流向: " + detailDto.getEndSiteName());
            }
            // 如果滑道笼车为空 不能创建混扫任务
            if (detailDto.getCrossCode() == null || detailDto.getTabletrolleyCode() == null) {
                JdResult<TableTrolleyJsfResp> tableTrolleyRes = jyWarehouseSendVehicleService.fetchCrossTableTrolley(createMixScanTaskReq.getCurrentOperate().getSiteCode(), detailDto.getEndSiteId().intValue());
                if (!Objects.isNull(tableTrolleyRes) && tableTrolleyRes.isSucceed() &&
                        !Objects.isNull(tableTrolleyRes.getData()) && CollectionUtils.isNotEmpty(tableTrolleyRes.getData().getTableTrolleyDtoJsfList())) {
                    String crossCode = tableTrolleyRes.getData().getTableTrolleyDtoJsfList().get(0).getCrossCode();
                    String tableTrolleyCode = tableTrolleyRes.getData().getTableTrolleyDtoJsfList().get(0).getTableTrolleyCode();
                    detailDto.setCrossCode(crossCode);
                    detailDto.setTabletrolleyCode(tableTrolleyCode);
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("fillWareHouseFocusField:滑道笼车查询为空或者查询失败，request={}", JsonHelper.toJson(detailDto));
                    }
                    throw new JyBizException("流向" + detailDto.getEndSiteName() + "未获取到滑道笼车信息，创建混扫任务失败: ");
                }
            }
            endSiteSet.add(detailDto.getEndSiteId());
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.appendMixScanTaskFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> appendMixScanTaskFlow(AppendMixScanTaskFlowReq appendMixScanTaskFlowReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(appendMixScanTaskFlowReq.getUser());
            checkCurrentOperate(appendMixScanTaskFlowReq.getCurrentOperate());
            checkGroupCode(appendMixScanTaskFlowReq.getGroupCode());
            if(StringUtils.isEmpty(appendMixScanTaskFlowReq.getTemplateCode())) {
                response.toFail("入参没有混扫任务编号");
                return response;
            }
            
            if(!jyGroupSortCrossDetailCacheService.getMixScanTaskCompleteLock(appendMixScanTaskFlowReq.getGroupCode(), appendMixScanTaskFlowReq.getTemplateCode())) {
                response.toFail("系统繁忙，请稍后再试！");
                return response;
            }

            JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
            queryDto.setGroupCode(appendMixScanTaskFlowReq.getGroupCode());
            queryDto.setTemplateCode(appendMixScanTaskFlowReq.getTemplateCode());
            queryDto.setStartSiteId((long)appendMixScanTaskFlowReq.getCurrentOperate().getSiteCode());
            queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿操作");
                return response;
            }
            
            // 混扫任务流向校验
            appendCheckMaxAndFlow(appendMixScanTaskFlowReq);

            if (!jyGroupSortCrossDetailService.appendMixScanTaskFlow(appendMixScanTaskFlowReq)) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"新增流向失败！");
            }
            return response;
        } catch (JyBizException e) {
            log.info("新增流向失败：{}", JsonHelper.toJson(appendMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl deleteMixScanTask 新增流向异常 {}", JsonHelper.toJson(appendMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "新增流向异常！");
        }finally {
            jyGroupSortCrossDetailCacheService.delMixScanTaskCompleteLock(appendMixScanTaskFlowReq.getGroupCode(), appendMixScanTaskFlowReq.getTemplateCode());
        }
    }

    private void appendCheckMaxAndFlow(AppendMixScanTaskFlowReq appendMixScanTaskFlowReq) {
        
        if (CollectionUtils.isEmpty(appendMixScanTaskFlowReq.getSendFlowList())) {
            throw new JyBizException("未获取到流向信息！");
        }
        
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        condition.setGroupCode(appendMixScanTaskFlowReq.getGroupCode());
        condition.setStartSiteId(Long.valueOf(appendMixScanTaskFlowReq.getCurrentOperate().getSiteCode()));
        condition.setTemplateCode(appendMixScanTaskFlowReq.getTemplateCode());
        List<JyGroupSortCrossDetailEntity> detailList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(condition);
        if (CollectionUtils.isEmpty(detailList)) {
            throw new JyBizException("未查询到该混扫任务信息");
        }
        appendMixScanTaskFlowReq.setTemplateName(detailList.get(0).getTemplateName());
        
        Integer max = jyWarehouseSendVehicleService.getFlowMaxBySiteCode(appendMixScanTaskFlowReq.getCurrentOperate().getSiteCode());
        if (max < (appendMixScanTaskFlowReq.getSendFlowList().size() + detailList.size() )) {
            throw new JyBizException("流向不能超过" + max + "个,请重新选择!");
        }
        
        HashSet<Long> endSiteSet = new HashSet<>();
        detailList.forEach(item -> endSiteSet.add(item.getEndSiteId()));
        
        appendMixScanTaskFlowReq.getSendFlowList().forEach(item -> {
            if (endSiteSet.contains(item.getEndSiteId())) {
                throw new JyBizException("包含重复流向！请重新选择新增流向");
            }
            if(StringUtils.isBlank(item.getCrossCode()) || StringUtils.isBlank(item.getTabletrolleyCode())) {
                throw new JyBizException("未知滑道笼车号无法添加混扫任务");
            }
            endSiteSet.add(item.getEndSiteId());
        });
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.deleteMixScanTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<String> deleteMixScanTask(DeleteMixScanTaskReq deleteMixScanTaskReq) {
        JdCResponse<String> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(deleteMixScanTaskReq.getUser());
            checkCurrentOperate(deleteMixScanTaskReq.getCurrentOperate());
            checkGroupCode(deleteMixScanTaskReq.getGroupCode());

            if(!jyGroupSortCrossDetailCacheService.getMixScanTaskCompleteLock(deleteMixScanTaskReq.getGroupCode(), deleteMixScanTaskReq.getTemplateCode())) {
                response.toFail("系统繁忙，请稍后再试！");
                return response;
            }

            JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
            queryDto.setGroupCode(deleteMixScanTaskReq.getGroupCode());
            queryDto.setTemplateCode(deleteMixScanTaskReq.getTemplateCode());
            queryDto.setStartSiteId((long)deleteMixScanTaskReq.getCurrentOperate().getSiteCode());
            queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿重新操作");
                return response;
            }
            
            if (!jyGroupSortCrossDetailService.deleteMixScanTask(deleteMixScanTaskReq)) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"删除混扫任务失败");
            }
        } catch (JyBizException e) {
            log.info("删除混扫任务失败：{}", JsonHelper.toJson(deleteMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl deleteMixScanTask 删除混扫任务失败 {}", JsonHelper.toJson(deleteMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "删除混扫任务异常！");
        }finally {
            jyGroupSortCrossDetailCacheService.delMixScanTaskCompleteLock(deleteMixScanTaskReq.getGroupCode(), deleteMixScanTaskReq.getTemplateCode());
        }
        response.setData(deleteMixScanTaskReq.getTemplateCode());
        return response;   
    }

    /**
     * 校验组号
     * @param groupCode
     */
    private void checkGroupCode(String groupCode) {
        if (StringUtils.isEmpty(groupCode)) {
            throw  new JyBizException("操作人组号为空");
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.removeMixScanTaskFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> removeMixScanTaskFlow(RemoveMixScanTaskFlowReq removeMixScanTaskFlowReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(removeMixScanTaskFlowReq.getUser());
            checkCurrentOperate(removeMixScanTaskFlowReq.getCurrentOperate());
            checkGroupCode(removeMixScanTaskFlowReq.getGroupCode());
//            if(!jyGroupSortCrossDetailCacheService.getMixScanTaskCompleteLock(removeMixScanTaskFlowReq.getGroupCode(), removeMixScanTaskFlowReq.getTemplateCode())) {
//                response.toFail("系统繁忙，请稍后再试！");
//                return response;
//            }
            JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
            queryDto.setGroupCode(removeMixScanTaskFlowReq.getGroupCode());
            queryDto.setTemplateCode(removeMixScanTaskFlowReq.getTemplateCode());
            queryDto.setStartSiteId((long)removeMixScanTaskFlowReq.getCurrentOperate().getSiteCode());
            queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿重新操作");
                return response;
            }
            
            jyGroupSortCrossDetailService.removeMixScanTaskFlow(removeMixScanTaskFlowReq);
        } catch (JyBizException e) {
            log.info("移除流向失败：{}", JsonHelper.toJson(removeMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 移除流向失败 {}", JsonHelper.toJson(removeMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "移除流向异常！");
        }
//        finally {
//            jyGroupSortCrossDetailCacheService.delMixScanTaskCompleteLock(removeMixScanTaskFlowReq.getGroupCode(), removeMixScanTaskFlowReq.getTemplateCode());
//        }
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.mixScanTaskComplete",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> mixScanTaskComplete(MixScanTaskCompleteReq mixScanTaskReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try {
            checkUser(mixScanTaskReq.getUser());
            checkCurrentOperate(mixScanTaskReq.getCurrentOperate());
            checkGroupCode(mixScanTaskReq.getGroupCode());
            if (StringUtils.isEmpty(mixScanTaskReq.getTemplateCode())) {
                response.toFail("未获取到混扫任务编号！");
                return response;
            }

            JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
            queryDto.setGroupCode(mixScanTaskReq.getGroupCode());
            queryDto.setTemplateCode(mixScanTaskReq.getTemplateCode());
            queryDto.setStartSiteId((long)mixScanTaskReq.getCurrentOperate().getSiteCode());
            queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿重新操作");
                return response;
            }
            if(!jyGroupSortCrossDetailCacheService.getMixScanTaskCompleteLock(mixScanTaskReq.getGroupCode(), mixScanTaskReq.getTemplateCode())) {
                response.toFail("系统繁忙，请稍后再试！");
                return response;
            }
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿重新操作");
                return response;
            }

            // 完成混扫任务 修改派车任务状态
            if (!jyWarehouseSendVehicleService.mixScanTaskComplete(mixScanTaskReq)){
                response.toFail("完成混扫任务失败！");
                return response;
            }
            
            jyGroupSortCrossDetailCacheService.saveMixScanTaskCompleteCache(mixScanTaskReq.getGroupCode(), mixScanTaskReq.getTemplateCode());

        } catch (JyBizException e) {
            log.info("完成混扫任务失败：error={},req={}", e.getMessage(), JsonHelper.toJson(mixScanTaskReq));
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl mixScanTaskComplete 完成混扫任务异常 {}", JsonHelper.toJson(mixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "完成混扫任务异常！");
        }finally {
            jyGroupSortCrossDetailCacheService.delMixScanTaskCompleteLock(mixScanTaskReq.getGroupCode(), mixScanTaskReq.getTemplateCode());
        }
        return response;
    }
    
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.mixScanTaskFocus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Integer> mixScanTaskFocus(MixScanTaskFocusReq mixScanTaskFocusReq) {
        JdCResponse<Integer> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(mixScanTaskFocusReq.getUser());
            checkCurrentOperate(mixScanTaskFocusReq.getCurrentOperate());
            checkGroupCode(mixScanTaskFocusReq.getGroupCode());
            if (mixScanTaskFocusReq.getFocus() == null || mixScanTaskFocusReq.getFocus() < 0) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"缺少关注参数!");
            }

            JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
            queryDto.setGroupCode(mixScanTaskFocusReq.getGroupCode());
            queryDto.setTemplateCode(mixScanTaskFocusReq.getTemplateCode());
            queryDto.setStartSiteId((long)mixScanTaskFocusReq.getCurrentOperate().getSiteCode());
            queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
            queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
            if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
                response.toFail("该混扫任务已经完成，请勿重新操作");
                return response;
            }
            
            if (!jyGroupSortCrossDetailService.mixScanTaskFocus(mixScanTaskFocusReq)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL,mixScanTaskFocusReq.getFocus() == FocusEnum.FOCUS.getCode() ? "关注失败！" : "取消关注失败！");
            }
        } catch (JyBizException e) {
            log.info("更新关注状态失败：{}", JsonHelper.toJson(mixScanTaskFocusReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 更新关注状态异常 {}", JsonHelper.toJson(mixScanTaskFocusReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "更新关注状态异常！");
        }
        response.setData(mixScanTaskFocusReq.getFocus());
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.getMixScanTaskListPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MixScanTaskQueryRes> getMixScanTaskListPage(MixScanTaskListQueryReq mixScanTaskListQueryReq) {
        JdCResponse<MixScanTaskQueryRes> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(mixScanTaskListQueryReq.getUser());
            checkCurrentOperate(mixScanTaskListQueryReq.getCurrentOperate());
            checkPage(mixScanTaskListQueryReq.getPageNo(), mixScanTaskListQueryReq.getPageSize());
            checkGroupCode(mixScanTaskListQueryReq.getGroupCode());
            MixScanTaskQueryRes result = this.getMixScanTaskPage(mixScanTaskListQueryReq);
            response.setData(result);
        } catch (JyBizException e) {
            log.info("查询混扫任务失败：{}", JsonHelper.toJson(mixScanTaskListQueryReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.info("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 查询混扫任务异常 {}", JsonHelper.toJson(mixScanTaskListQueryReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询混扫任务异常！");
        }
        return response;
    }

    private MixScanTaskQueryRes getMixScanTaskPage(MixScanTaskListQueryReq mixScanTaskListQueryReq) {
        MixScanTaskQueryRes mixScanTaskQueryRes = new MixScanTaskQueryRes();
        CTTGroupDataResp groupDataResp = jyGroupSortCrossDetailService.getMixScanTaskListPage(this.assembleCondition(mixScanTaskListQueryReq));
        if (groupDataResp != null && !CollectionUtils.isEmpty(groupDataResp.getCttGroupDtolist())) {
            List<MixScanTaskDto> mixScanTaskDtoList = groupDataResp.getCttGroupDtolist().stream()
                    .map(item -> BeanUtils.copy(item, MixScanTaskDto.class)).collect(Collectors.toList());
            mixScanTaskQueryRes.setMixScanTaskDtoList(mixScanTaskDtoList);
        }
        return mixScanTaskQueryRes;
    }

    private JyGroupSortCrossDetailEntity assembleCondition(MixScanTaskListQueryReq mixScanTaskListQueryReq) {
        String barCode = mixScanTaskListQueryReq.getBarCode();
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        
        if (StringUtils.isNotBlank(barCode)) {
            // 获取目的地站点或者滑道笼车号
            if (WaybillUtil.isPackageCode(barCode) || BusinessHelper.isBoxcode(barCode)) {
                SendScanReq sendScanReq = new SendScanReq();
                sendScanReq.setOperateType(JySendFlowConfigEnum.GANTRY.getCode());
                sendScanReq.setCurrentOperate(mixScanTaskListQueryReq.getCurrentOperate());
                InvokeResult<List<Integer>> result = jyWarehouseSendVehicleService.fetchNextSiteId(sendScanReq);
                
                if (result.codeSuccess() && !CollectionUtils.isEmpty(result.getData())) {
                    condition.setEndSiteId(result.getData().get(0).longValue());
                }
            } else if (checkCTTCode(barCode)) {
                int index = barCode.indexOf("-");
                condition.setCrossCode(barCode.substring(0, index));
                condition.setTabletrolleyCode(barCode.substring(index + 1));
            }else if (SerialRuleUtil.isMatchNumeric(barCode)) {
                // 目的地站点
                condition.setEndSiteId(Long.valueOf(barCode));
            }else {
                throw new JyBizException("条码格式不匹配，请重新扫描！");
            }
        }
        
        condition.setGroupCode(mixScanTaskListQueryReq.getGroupCode());
        condition.setStartSiteId(Long.valueOf(mixScanTaskListQueryReq.getCurrentOperate().getSiteCode()));
        condition.setCompleteStatus(JyMixScanTaskCompleteEnum.DOING.getCode());
        condition.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        if (StringUtils.isNotBlank(mixScanTaskListQueryReq.getSendVehicleDetailBizId())) {
            condition.setSendVehicleDetailBizId(mixScanTaskListQueryReq.getSendVehicleDetailBizId());
        }
        return condition;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.getMixScanTaskFlowDetailList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MixScanTaskFlowDetailRes> getMixScanTaskFlowDetailList(MixScanTaskFlowDetailReq mixScanTaskFlowReq) {
        JdCResponse<MixScanTaskFlowDetailRes> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(mixScanTaskFlowReq.getUser());
            checkCurrentOperate(mixScanTaskFlowReq.getCurrentOperate());
            checkGroupCode(mixScanTaskFlowReq.getGroupCode());

            if(StringUtils.isBlank(mixScanTaskFlowReq.getTemplateCode())) {
                response.toFail("混扫任务编码参数为空");
                return response;
            }

            MixScanTaskFlowDetailRes res = jyWarehouseSendVehicleService.getMixScanTaskFlowDetailList(mixScanTaskFlowReq);
            response.setData(res);
        }catch (JyBizException e) {
            log.info("获取混扫任务流向详情失败：{}",JsonHelper.toJson(mixScanTaskFlowReq) ,e );
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        }catch (Exception e) {
            log.error("获取混扫任务流向详情异常：{}", JsonHelper.toJson(mixScanTaskFlowReq),e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR);
        }
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.getMixScanTaskDetailList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request) {
        JdCResponse<MixScanTaskDetailRes> res = new JdCResponse<>();
        res.toSucceed();
        final String methodDesc = "JyWarehouseSendGatewayServiceImpl.getMixScanTaskFlowList:查询混扫任务下流向信息：";
        try{
            //参数校验
            if(Objects.isNull(request)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            checkGroupCode(request.getGroupCode());
            if(StringUtils.isBlank(request.getTemplateCode())) {
                res.toFail("混扫任务编码参数为空");
                return res;
            }
            return retJdCResponse(jyWarehouseSendVehicleService.getMixScanTaskDetailList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR, null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.selectMixScanTaskSealDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MixScanTaskToSealDestAgg> selectMixScanTaskSealDest(SelectMixScanTaskSealDestReq request) {
        JdCResponse<MixScanTaskToSealDestAgg> response = new JdCResponse<>();
        response.toSucceed();
        try {
            // 查询混扫任务下的所有流向信息
            JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
            condition.setTemplateCode(request.getTemplateCode());
            condition.setGroupCode(request.getGroupCode());
            condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            List<JyGroupSortCrossDetailEntity> list = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(condition);
            
            if (CollectionUtils.isEmpty(list)) {
                response.toFail("未查询好混扫任务下的流向信息！");
                return response;
            }
            // 获取流向详情
            MixScanTaskToSealDestAgg destAgg = jyWarehouseSendVehicleService.selectMixScanTaskSealDest(request, list);
            response.setData(destAgg);
        }catch (JyBizException e) {
            log.error("混扫任务查询封车流向失败：{}",JsonHelper.toJson(request),e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL,"混扫任务查询封车流向失败！");
        }catch (Exception e) {
            log.error("混扫任务查询封车流向异常：{}",JsonHelper.toJson(request),e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR,"混扫任务查询封车流向异常！");
        }
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.getSealVehicleInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return retJdCResponse(jySealVehicleService.getSealVehicleInfo(sealVehicleInfoReq));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.checkTransCode",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<TransportResp> checkTransCode(CheckTransportReq checkTransportReq) {
        return retJdCResponse(jySealVehicleService.checkTransCode(checkTransportReq));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.saveSealVehicle",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq) {
        return retJdCResponse(jySealVehicleService.saveSealVehicle(sealVehicleReq));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.validateTranCodeAndSendCode",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq request) {
        return retJdCResponse(jySealVehicleService.validateTranCodeAndSendCode(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.sealVehicle",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> sealVehicle(SealVehicleReq sealVehicleReq) {
        return retJdCResponse(jySealVehicleService.czSealVehicle(sealVehicleReq));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendGatewayServiceImpl.selectSealDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ToSealDestAgg> selectSealDest(SelectSealDestRequest request) {
        return retJdCResponse(jyWarehouseSendVehicleService.selectSealDest(request));
    }

    @Override
    public JdCResponse<Void> checkBeforeSealCar(SealCarCheckDtoReq request) {
        JdCResponse<Void> res = new JdCResponse<>();
        res.toSucceed();
        final String methodDesc = "JyWarehouseSendGatewayServiceImpl.checkBeforeSealCar:接货仓发货岗前往封车前置校验";
        try{
            //参数校验
            if(Objects.isNull(request)){
                res.toFail("请求为空");
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            checkUser(request.getUser());
            checkCurrentOperate(request.getCurrentOperate());
            checkGroupCode(request.getGroupCode());
            if(StringUtils.isBlank(request.getMixScanTaskCode())) {
                res.toFail("混扫任务编码参数为空");
                return res;
            }
            return retJdCResponse(jyWarehouseSendVehicleService.checkBeforeSealCar(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JdCResponse.MESSAGE_ERROR, null);//500+非自定义异常
        }
    }


}
