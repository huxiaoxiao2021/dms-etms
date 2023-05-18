package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.send.JyWarehouseSendVehicleServiceImpl;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.jy.constants.JyPostEnum.SEND_SEAL_WAREHOUSE;
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
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BoxService boxService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    private void checkUser(User user) {
        if(Objects.isNull(user) || StringUtils.isBlank(user.getUserErp())) {
            throw new JyBizException("操作人ero为空");
        }

    }
    private void checkCurrentOperate(CurrentOperate currentOperate) {
        if(Objects.isNull(currentOperate) || Objects.isNull(currentOperate.getSiteCode())) {
            throw new JyBizException("操作场地编码为空");
        }
    }

    @Override
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.fetchSendVehicleTaskPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTaskPage(SendVehicleTaskRequest request) {
        final String methodDesc = "JySendVehicleGatewayService.fetchSendVehicleTaskPage:接货仓发货岗按状态查询派车任务列表：";
        try{
            //参数校验
            if(Objects.isNull(request)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            //车辆状态合法性校验
            JdCResponse illegalVehicleStatusRes =legalVehicleStatusCheck(request);
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
        List<SelectOption> optionList = getVehicleStatusEnums();
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
    public JdCResponse<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request) {
        final String methodDesc = "JySendVehicleGatewayService.fetchToSendAndSendingTaskPage:接货仓发货岗查询待发货和发货中派车任务信息：";
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
            if(Objects.isNull(request.getPageNo()) || Objects.isNull(request.getPageSize())
                    || request.getPageNo() <= 0 || request.getPageSize() <= 0 || DEFAULT_PAGE_MAXSIZE > DEFAULT_PAGE_MAXSIZE) {
                res.toFail("分页参数错误");
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
    public JdCResponse<SendVehicleProgress> loadStatistics(SendVehicleProgressRequest request) {
        return null;
    }

    @Override
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {
        return null;
    }

    @Override
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
            log.error("JyGroupSortCrossDetailServiceImpl deleteMixScanTask 创建混扫任务异常 {}", JsonHelper.toJson(createMixScanTaskReq), e);
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
            endSiteSet.add(detailDto.getEndSiteId());
        }
    }

    @Override
    public JdCResponse<Void> appendMixScanTaskFlow(AppendMixScanTaskFlowReq appendMixScanTaskFlowReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(appendMixScanTaskFlowReq.getUser());
            checkCurrentOperate(appendMixScanTaskFlowReq.getCurrentOperate());
            checkGroupCode(appendMixScanTaskFlowReq.getGroupCode());
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
            endSiteSet.add(item.getEndSiteId());
        });
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.deleteMixScanTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> deleteMixScanTask(DeleteMixScanTaskReq deleteMixScanTaskReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(deleteMixScanTaskReq.getUser());
            checkCurrentOperate(deleteMixScanTaskReq.getCurrentOperate());
            checkGroupCode(deleteMixScanTaskReq.getGroupCode());
            if (!jyGroupSortCrossDetailService.deleteMixScanTask(deleteMixScanTaskReq)) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"删除混扫任务失败");
            }
        } catch (JyBizException e) {
            log.info("删除混扫任务失败：{}", JsonHelper.toJson(deleteMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl deleteMixScanTask 删除混扫任务失败 {}", JsonHelper.toJson(deleteMixScanTaskReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "删除混扫任务异常！");
        }
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
    public JdCResponse<Void> removeMixScanTaskFlow(RemoveMixScanTaskFlowReq removeMixScanTaskFlowReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(removeMixScanTaskFlowReq.getUser());
            checkCurrentOperate(removeMixScanTaskFlowReq.getCurrentOperate());
            checkGroupCode(removeMixScanTaskFlowReq.getGroupCode());
            if (!jyGroupSortCrossDetailService.removeMixScanTaskFlow(removeMixScanTaskFlowReq)) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"移除流向失败！");
            }
        } catch (JyBizException e) {
            log.info("移除流向失败：{}", JsonHelper.toJson(removeMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 移除流向失败 {}", JsonHelper.toJson(removeMixScanTaskFlowReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "移除流向异常！");
        }
        return response;
    }

    @Override
    public JdCResponse<MixScanTaskRes> mixScanTaskComplete(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<Void> mixScanTaskFocus(MixScanTaskFocusReq mixScanTaskFocusReq) {
        JdCResponse<Void> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(mixScanTaskFocusReq.getUser());
            checkCurrentOperate(mixScanTaskFocusReq.getCurrentOperate());
            checkGroupCode(mixScanTaskFocusReq.getGroupCode());
            if (mixScanTaskFocusReq.getFocus() == null || mixScanTaskFocusReq.getFocus() < 0) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL,"缺少关注参数!");
            }
            
            if (!jyGroupSortCrossDetailService.mixScanTaskFocus(mixScanTaskFocusReq)){
                return new JdCResponse<>(JdCResponse.CODE_FAIL,mixScanTaskFocusReq.getFocus() == 1 ? "关注失败！" : "取消关注失败！");
            }
        } catch (JyBizException e) {
            log.info("更新关注状态失败：{}", JsonHelper.toJson(mixScanTaskFocusReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 更新关注状态异常 {}", JsonHelper.toJson(mixScanTaskFocusReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "更新关注状态异常！");
        }
        return response;
    }

    @Override
    public JdCResponse<MixScanTaskQueryRes> getMixScanTaskListPage(MixScanTaskListQueryReq mixScanTaskListQueryReq) {
        JdCResponse<MixScanTaskQueryRes> response = new JdCResponse<>();
        response.toSucceed();
        try{
            checkUser(mixScanTaskListQueryReq.getUser());
            checkCurrentOperate(mixScanTaskListQueryReq.getCurrentOperate());
            checkGroupCode(mixScanTaskListQueryReq.getGroupCode());
            MixScanTaskQueryRes result = this.getMixScanTaskPage(mixScanTaskListQueryReq);
            response.setData(result);
        } catch (JyBizException e) {
            log.info("查询混扫任务失败：{}", JsonHelper.toJson(mixScanTaskListQueryReq), e);
            return new JdCResponse<>(JdCResponse.CODE_FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("JyGroupSortCrossDetailServiceImpl removeMixScanTaskFlow 查询混扫任务异常 {}", JsonHelper.toJson(mixScanTaskListQueryReq), e);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询混扫任务异常！");
        }
        return response;
    }

    private MixScanTaskQueryRes getMixScanTaskPage(MixScanTaskListQueryReq mixScanTaskListQueryReq) {
        MixScanTaskQueryRes mixScanTaskQueryRes = new MixScanTaskQueryRes();
        CTTGroupDataResp groupDataResp = jyGroupSortCrossDetailService.getMixScanTaskListPage(assembleCondition(mixScanTaskListQueryReq));
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

        if (WaybillUtil.isPackageCode(barCode)) {
            final Waybill waybill = waybillQueryManager
                    .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
            if (waybill == null) {
                throw new JyBizException("未查找到运单数据");
            }
            if (waybill.getOldSiteId() == null) {
                throw new JyBizException("运单对应的预分拣站点为空");
            }
            condition.setEndSiteId(waybill.getOldSiteId().longValue());
        } else if (BusinessHelper.isBoxcode(barCode)) {
            final Box box = boxService.findBoxByCode(barCode);
            if (box == null) {
                throw new JyBizException("未找到对应箱号，请检查");
            }
            condition.setEndSiteId(box.getReceiveSiteCode().longValue());
        } else if (checkCTTCode(barCode)) {
            int index = barCode.indexOf("-");
            condition.setCrossCode(barCode.substring(0, index));
            condition.setTabletrolleyCode(barCode.substring(index + 1));
        }else if (SerialRuleUtil.isMatchNumeric(barCode)) {
            // 目的地站点
            condition.setEndSiteId(Long.valueOf(barCode));
        }

        condition.setGroupCode(mixScanTaskListQueryReq.getGroupCode());
        condition.setStartSiteId(Long.valueOf(mixScanTaskListQueryReq.getCurrentOperate().getSiteCode()));
        condition.setFuncType(SEND_SEAL_WAREHOUSE.getCode());
        if (!StringUtils.isEmpty(mixScanTaskListQueryReq.getSendVehicleDetailBizId())) {
            condition.setSendVehicleDetailBizId(mixScanTaskListQueryReq.getSendVehicleDetailBizId());
        }
        return condition;
    }

    @Override
    public JdCResponse<MixScanTaskFlowDetailRes> getMixScanTaskFlowDetailList(MixScanTaskFlowDetailReq mixScanTaskFlowReq) {
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.getMixScanTaskDetailList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request) {
        JdCResponse<MixScanTaskDetailRes> res = new JdCResponse<>();
        res.toSucceed();
        final String methodDesc = "JySendVehicleGatewayService.getMixScanTaskFlowList:查询混扫任务下流向信息：";
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
            if(StringUtils.isBlank(request.getGroupCode())) {
                res.toFail("岗位小组参数为空");
                return res;
            }
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
    public JdCResponse<ToSealDestAgg> selectMixScanTaskSealDest(SelectSealDestRequest request) {
        return null;
    }

    @Override
    public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return null;
    }
}
