package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.board.request.*;
import com.jd.bluedragon.common.dto.board.response.UnbindVirtualBoardResultDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.jsf.dms.IVirtualBoardJsfManager;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.board.domain.BoardConstants;
import com.jd.bluedragon.distribution.board.domain.BoardFlowTypeDto;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.domain.OperatorData;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.dms.workbench.utils.sdk.constants.ResultCodeConstant;
import com.jd.eclp.common.util.DateUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BoardBarcodeTypeEnum;
import com.jd.transboard.api.enums.BoardStatus;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 虚拟组板服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-20 12:30:07 周五
 */
@Service("virtualBoardService")
public class VirtualBoardServiceImpl implements VirtualBoardService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CycleBoxService cycleBoxService;

    @Resource
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private IVirtualBoardJsfManager virtualBoardJsfManager;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private BoxService boxService;

    @Resource
    private CacheService jimdbCacheService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private SendMService sendMService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;
    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.getBoardUnFinishInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo) {
        log.info("VirtualBoardServiceImpl.getBoardUnFinishInfo--start-- param {}", JsonHelper.toJson(operatorInfo));
        List<VirtualBoardResultDto> virtualBoardResultDtoList = new ArrayList<>();
        JdCResponse<List<VirtualBoardResultDto>> result = new JdCResponse<>(ResponseEnum.SUCCESS.getIndex(), null, virtualBoardResultDtoList);
        try {
            final Response<List<com.jd.transboard.api.dto.VirtualBoardResultDto>> handleResult = virtualBoardJsfManager.getBoardUnFinishInfo(this.getConvertToTcParam(operatorInfo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.getBoardUnFinishInfo--fail-- param {} result {}", JsonHelper.toJson(operatorInfo), JsonHelper.toJson(handleResult));
                result.toFail("处理失败，请稍后再试");
                return result;
            }
            final List<com.jd.transboard.api.dto.VirtualBoardResultDto> virtualBoardResultDtoQueryData = handleResult.getData();
            if (CollectionUtils.isEmpty(virtualBoardResultDtoQueryData)) {
                return result;
            }
            for (com.jd.transboard.api.dto.VirtualBoardResultDto virtualBoardResultDtoQueryDatum : virtualBoardResultDtoQueryData) {
                //删除流向，不变更板状态的， 隐藏不展示
                if(virtualBoardResultDtoQueryDatum.getHideSwitch() != null && virtualBoardResultDtoQueryDatum.getHideSwitch()) {
                    continue;
                }
                VirtualBoardResultDto virtualBoardResultDto = new VirtualBoardResultDto();
                BeanUtils.copyProperties(virtualBoardResultDtoQueryDatum, virtualBoardResultDto);
                virtualBoardResultDtoList.add(virtualBoardResultDto);
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.getBoardUnFinishInfo--exception param {} exception {}", JsonHelper.toJson(operatorInfo), e.getMessage(), e);
        }
        return result;
    }
    @Override
    public JdCResponse<VirtualBoardDto> getBoardUnFinishInfoNew(OperatorInfo operatorInfo) {
        String descMethod = "VirtualBoardServiceImpl.getBoardUnFinishInfoNew--";
        JdCResponse<VirtualBoardDto> result = new JdCResponse<>();
        VirtualBoardDto virtualBoardDto = new VirtualBoardDto();
        result.setData(virtualBoardDto);
        result.setCode(JdCResponse.CODE_SUCCESS);

        try{
            log.info("{}--start-- param {}", descMethod, JsonHelper.toJson(operatorInfo));
            JdCResponse jdCResponse = flowTypeHandler(operatorInfo);
            if(!Objects.equals(jdCResponse.getCode(), JdCResponse.CODE_SUCCESS)) {
                result.toFail(jdCResponse.getMessage());
                return result;
            }
            virtualBoardDto.setFlowFlag(operatorInfo.getFlowFlag());
            JdCResponse<List<VirtualBoardResultDto>> jsfRes = this.getBoardUnFinishInfo(operatorInfo);
            if(!Objects.equals(jsfRes.getCode(), JdCResponse.CODE_SUCCESS)) {
                result.toFail(jsfRes.getMessage());
                return result;
            }
            virtualBoardDto.setVirtualBoardResultDtoList(jsfRes.getData());
            return result;
        }catch (Exception e) {
            result.toFail("接口异常：" + e.getMessage());
            log.error("{}exception param {} exception {}", descMethod, JsonHelper.toJson(operatorInfo), e.getMessage(), e);
            return result;
        }
    }

    /**
     * 流向模式处理
     * @param operatorInfo
     */
    private JdCResponse flowTypeHandler(OperatorInfo operatorInfo) {
        JdCResponse res = new JdCResponse();
        if(StringUtils.isBlank(operatorInfo.getUserErp())) {
            res.toFail("erp不能为空");
            return res;
        }

        String key = BoardConstants.CACHE_KEY_BOARD_FLOW_TYPE_ERP + operatorInfo.getUserErp();
        BoardFlowTypeDto boardFlowTypeDto = jimdbCacheService.get(key, BoardFlowTypeDto.class);
        log.info("VirtualBoardServiceImpl.flowTypeHandler--缓存查询分拣组板类型，key={},value{}", key, JsonHelper.toJson(boardFlowTypeDto));

        Integer defaultFlowType = BoardConstants.MULTI_FLOW_BOARD;
        if(operatorInfo.getFlowFlag() == null) {
            //老功能没有这个字段，走默认多模式
            boardFlowTypeDto.setFlowType(defaultFlowType);
        }else if(operatorInfo.getFlowFlag() == BoardConstants.INIT_FLOW_BOARD) {
            if(boardFlowTypeDto == null) {
                //首次登录该功能默认多流向模式
                boardFlowTypeDto = new BoardFlowTypeDto();
                boardFlowTypeDto.setFlowType(defaultFlowType);
                boardFlowTypeDto.setCreateTimeStamp(System.currentTimeMillis());
                jimdbCacheService.setEx(key, JsonHelper.toJson(boardFlowTypeDto), BoardConstants.CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME, TimeUnit.DAYS);
                //
                operatorInfo.setFlowFlag(boardFlowTypeDto.getFlowType());
            }else {
                // 之前有登陆保存缓存，退出页面重新进来
                operatorInfo.setFlowFlag(boardFlowTypeDto.getFlowType());
                boardFlowTypeCacheRenewal(boardFlowTypeDto, key);
            }
        }else {
            if(boardFlowTypeDto != null && boardFlowTypeDto.getFlowType() != null &&  boardFlowTypeDto.getFlowType() == operatorInfo.getFlowFlag()) {
                //页面给值和缓存相同时，redis续期
                boardFlowTypeCacheRenewal(boardFlowTypeDto, key);
            }else {
                //页面给值和缓存相同时，redis更新创建新的值
                boardFlowTypeDto = new BoardFlowTypeDto();
                boardFlowTypeDto.setFlowType(operatorInfo.getFlowFlag());
                boardFlowTypeDto.setCreateTimeStamp(System.currentTimeMillis());
                jimdbCacheService.setEx(key, JsonHelper.toJson(boardFlowTypeDto), BoardConstants.CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME, TimeUnit.DAYS);
            }

        }
        res.toSucceed();
        return res;
    }

    /**
     * 分拣组板流向记录缓存续期
     * @param boardFlowTypeDto
     * @param cacheKey
     */
    public void boardFlowTypeCacheRenewal(BoardFlowTypeDto boardFlowTypeDto, String cacheKey) {
        boardFlowTypeDto.setCreateTimeStamp(System.currentTimeMillis());
        jimdbCacheService.setEx(cacheKey, JsonHelper.toJson(boardFlowTypeDto), BoardConstants.CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME, TimeUnit.DAYS);
    }

    private com.jd.transboard.api.dto.base.OperatorInfo getConvertToTcParam(OperatorInfo operatorInfo) {
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        log.info("operatorInfoTarget={}", JsonHelper.toJson(operatorInfoTarget));
        return operatorInfoTarget;
    }

    /**
     * 根据目的地创建新的板或得到已有的可用的板，目的地的板已存在且未完结，则直接返回该板号
     * @param addOrGetVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.createOrGetBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> createOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        log.info("VirtualBoardServiceImpl.createOrGetBoard--start-- param {}", JsonHelper.toJson(addOrGetVirtualBoardPo));
        JdCResponse<VirtualBoardResultDto> result = new JdCResponse<>();
        result.toSucceed();
        try {
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkParam4CreateOrGetBoard(addOrGetVirtualBoardPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            // 存入缓存，防止并发
            final OperatorInfo operatorInfo = addOrGetVirtualBoardPo.getOperatorInfo();
            String keyTemplate = CacheKeyConstants.VIRTUAL_BOARD_CREATE_DESTINATION;
            String key = String.format(keyTemplate, operatorInfo.getSiteCode(), operatorInfo.getUserErp());
            try{
                boolean getLock = jimdbCacheService.setNx(key, 1 + "", CacheKeyConstants.VIRTUAL_BOARD_BIND_TIMEOUT, TimeUnit.SECONDS);
                if(!getLock){
                    result.setCode(JdCResponse.CODE_FAIL);
                    result.setMessage("操作太快，正在处理中");
                    return result;
                }
                addOrGetVirtualBoardPo.setMaxDestinationCount(uccPropertyConfiguration.getVirtualBoardMaxDestinationCount());
                final Response<com.jd.transboard.api.dto.VirtualBoardResultDto> handleResult = virtualBoardJsfManager.createOrGetBoard(this.getConvertToTcParam(addOrGetVirtualBoardPo));
                if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                    log.error("VirtualBoardServiceImpl.createOrGetBoard--fail-- param {} result {}", JsonHelper.toJson(addOrGetVirtualBoardPo), JsonHelper.toJson(handleResult));
                    result.toFail(handleResult.getMesseage());
                    return result;
                }
            } finally {
                jimdbCacheService.del(key);
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.createOrGetBoard--exception param {} exception {}", JsonHelper.toJson(addOrGetVirtualBoardPo), e.getMessage(), e);
        }
        return result;
    }

    private com.jd.transboard.api.dto.AddOrGetVirtualBoardPo getConvertToTcParam(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        com.jd.transboard.api.dto.AddOrGetVirtualBoardPo addOrGetVirtualBoardPoTc = new com.jd.transboard.api.dto.AddOrGetVirtualBoardPo();
        BeanUtils.copyProperties(addOrGetVirtualBoardPo, addOrGetVirtualBoardPoTc);
        OperatorInfo operatorInfo = addOrGetVirtualBoardPo.getOperatorInfo();
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        addOrGetVirtualBoardPoTc.setOperatorInfo(operatorInfoTarget);
        return addOrGetVirtualBoardPoTc;
    }

    private Result<Void> checkParam4CreateOrGetBoard(AddOrGetVirtualBoardPo addOrGetVirtualBoardPo) {
        Result<Void> result = Result.success();
        final Result<Void> operateInfoCheckResult = this.checkBaseParam(addOrGetVirtualBoardPo.getOperatorInfo());
        if(!operateInfoCheckResult.isSuccess()){
            return operateInfoCheckResult;
        }
        if(addOrGetVirtualBoardPo.getDestinationId() == null){
            return result.toFail("参数错误，destinationId不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(addOrGetVirtualBoardPo.getDestinationId() <= 0){
            return result.toFail("参数错误，destinationId值不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        return result;
    }

    private Result<Void> checkBaseParam(com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(operatorInfo.getUserErp())){
            return result.toFail("参数错误，userErp不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(StringUtils.isBlank(operatorInfo.getUserName())){
            return result.toFail("参数错误，userName不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(operatorInfo.getSiteCode() == null){
            return result.toFail("参数错误，siteCode不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(operatorInfo.getSiteCode() <= 0){
            return result.toFail("参数错误，siteCode值不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(StringUtils.isBlank(operatorInfo.getSiteName())){
            return result.toFail("参数错误，siteName不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    private Result<Void> checkBaseParam(User user, CurrentOperate currentOperate) {
        Result<Void> result = Result.success();
        final Result<Void> checkUserResult = this.checkBaseParam(user);
        if(!checkUserResult.isSuccess()){
            return checkUserResult;
        }
        final Result<Void> checkOperateSiteResult = this.checkBaseParam(currentOperate);
        if(!checkUserResult.isSuccess()){
            return checkUserResult;
        }
        return result;
    }

    private Result<Void> checkBaseParam(User user) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(user.getUserErp())){
            return result.toFail("参数错误，userErp不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(StringUtils.isBlank(user.getUserName())){
            return result.toFail("参数错误，userName不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    private Result<Void> checkBaseParam(CurrentOperate currentOperate) {
        Result<Void> result = Result.success();
        if(currentOperate.getSiteCode() <= 0){
            return result.toFail("参数错误，siteCode值不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(currentOperate.getSiteName() == null){
            return result.toFail("参数错误，siteName不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    /**
     * 组板
     * @param bindToVirtualBoardPo 新建板
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.bindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<VirtualBoardResultDto> bindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo) {
        log.info("VirtualBoardServiceImpl.bindToBoard--start-- param {}", JsonHelper.toJson(bindToVirtualBoardPo));
        JdCResponse<VirtualBoardResultDto> result = new JdCResponse<>();
        result.toSucceed();
        try {
            // 存入缓存，防止并发
            // 1. 参数验证
            // 基本校验，参数、板号总数等
            final Result<Void> baseCheckResult = this.checkParam4BindToBoard(bindToVirtualBoardPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            // 存入缓存，防止并发
            final OperatorInfo operatorInfo = bindToVirtualBoardPo.getOperatorInfo();
            String keyTemplate = CacheKeyConstants.VIRTUAL_BOARD_BIND;
            String key = String.format(keyTemplate, operatorInfo.getSiteCode(), operatorInfo.getUserErp());
            try {
                boolean getLock = jimdbCacheService.setNx(key, 1 + "", CacheKeyConstants.VIRTUAL_BOARD_BIND_TIMEOUT, TimeUnit.SECONDS);
                if(!getLock){
                    result.setCode(JdCResponse.CODE_FAIL);
                    result.setMessage("操作太快，正在处理中");
                    return result;
                }
                // 根据板号查询已有板号，校验板号数据，状态是否正确，并得到具体流向
                // 校验板号中已装数据是否达到上限
                // 查询包裹号预分拣数据，得到包裹流向，与传过来的板号匹配流向，匹配上一个则可以绑定
                // ---- 如果是包裹号，则根据包裹号得到运单数据
                final String barCode = bindToVirtualBoardPo.getBarCode();
                boolean isPackageCode = false, isBoxCode = false;
                final BarCodeType barCodeTypeEnumName = BusinessUtil.getBarCodeType(barCode);
                if (Objects.equals(barCodeTypeEnumName, BarCodeType.PACKAGE_CODE)) {
                    isPackageCode = true;
                } else if (Objects.equals(barCodeTypeEnumName, BarCodeType.BOX_CODE)) {
                    isBoxCode = true;
                } else {
                    result.toFail("请扫描包裹号或箱号");
                    return result;
                }
                Integer destinationId = null;
                if (isPackageCode) {
                    final Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
                    if (waybill == null) {
                        result.toFail("未查找到运单数据");
                        return result;
                    }
                    if (waybill.getOldSiteId() == null) {
                        result.toFail("运单对应的预分拣站点为空");
                        return result;
                    }
                    destinationId = waybill.getOldSiteId();

                    // 先校验已扫板流向
                    final Result<Integer> checkMatchBoardDestinationResult = this.checkAndGetMatchBoardDestination(bindToVirtualBoardPo, destinationId);
                    if(!checkMatchBoardDestinationResult.isSuccess() || checkMatchBoardDestinationResult.getData() == null){
                        result.toFail(checkMatchBoardDestinationResult.getMessage());
                        return result;
                    }
                    destinationId = checkMatchBoardDestinationResult.getData();
                    // 拦截链校验
                    final PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
                    pdaOperateRequest.setPackageCode(bindToVirtualBoardPo.getBarCode());
                    pdaOperateRequest.setBoxCode(bindToVirtualBoardPo.getBarCode());
                    pdaOperateRequest.setReceiveSiteCode(destinationId);
                    pdaOperateRequest.setCreateSiteCode(operatorInfo.getSiteCode());
                    pdaOperateRequest.setCreateSiteName(operatorInfo.getSiteName());
                    pdaOperateRequest.setOperateUserCode(operatorInfo.getUserCode());
                    pdaOperateRequest.setOperateUserName(operatorInfo.getUserName());
                    pdaOperateRequest.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());
                    final BoardCombinationJsfResponse interceptResult = sortingCheckService.virtualBoardCombinationCheck(pdaOperateRequest);
                    if (!interceptResult.getCode().equals(200)) {//如果校验不OK
                        result.toFail(interceptResult.getMessage());
                        return result;
                    }
                }
                // 如果是箱号，校验箱号流向
                if (isBoxCode) {
                    final Box boxExist = boxService.findBoxByCode(bindToVirtualBoardPo.getBarCode());
                    if (boxExist == null) {
                        result.toFail("未找到对应箱号，请检查");
                        return result;
                    }
                    destinationId = boxExist.getReceiveSiteCode();
                    // 先校验已扫板流向
                    final Result<Integer> checkMatchBoardDestinationResult = this.checkAndGetMatchBoardDestination(bindToVirtualBoardPo, destinationId);
                    if(!checkMatchBoardDestinationResult.isSuccess() || checkMatchBoardDestinationResult.getData() == null){
                        result.toFail(checkMatchBoardDestinationResult.getMessage());
                        return result;
                    }
                    destinationId = checkMatchBoardDestinationResult.getData();
                }
                // 已在同场地发货，不可再组板
                final SendM recentSendMByParam = getRecentSendMByParam(bindToVirtualBoardPo.getBarCode(), operatorInfo.getSiteCode(), null, null);
                log.info("test.log--【{}】bindToBoard-分拣组板已发货信息-【{}】", barCode, JsonHelper.toJson(recentSendMByParam));
                if (recentSendMByParam != null) {
                    //三小时内禁止再次发货，返调度再次发货问题处理
                    Date sendTime = recentSendMByParam.getOperateTime();
                    if(sendTime != null && System.currentTimeMillis() - sendTime.getTime() <= 3l * 3600l * 1000l) {
                        result.toFail("该包裹已发货");
                        return result;
                    }
                }
                // 校验循环集包袋
                if (isBoxCode&&bindToVirtualBoardPo.getSiteCode()!=null&&bindToVirtualBoardPo.getOperateType()!=null){
                    final Box box = boxService.findBoxByCode(bindToVirtualBoardPo.getBarCode());
                    if (!validationAndCheck(bindToVirtualBoardPo.getBarCode(),bindToVirtualBoardPo.getOperateType(),bindToVirtualBoardPo.getSiteCode(),box)){
                        result.setCode(BoxResponse.CODE_BC_BOX_NO_BINDING);
                        result.setMessage(BoxResponse.MESSAGE_BC_NO_BINDING);
                        return result;
                    }
                }
                //集包禁止组板拦截
                if(isPackageCode) {
                    Sorting sorting = new Sorting();
                    sorting.setCreateSiteCode(operatorInfo.getSiteCode());
                    sorting.setPackageCode(barCode);
                    List<Sorting> sortingList = dynamicSortingQueryDao.findByWaybillCodeOrPackageCode(sorting);
                    if(CollectionUtils.isNotEmpty(sortingList)) {
                        for (Sorting sortingTemp : sortingList) {
                            final BarCodeType barCodeType = BusinessUtil.getBarCodeType(sortingTemp.getBoxCode());
                            if (barCodeType != null && Objects.equals(barCodeType, BarCodeType.BOX_CODE)) {
                                log.info("VirtualBoardServiceImpl.bindToBoard--包裹【{}】分拣组板时校验拦截，该包裹已经集包【{}】,request=【{}】", barCode, JsonHelper.toJson(sortingTemp), JsonHelper.toJson(bindToVirtualBoardPo));
                                result.setCode(BoxResponse.CODE_PACKAGE_BOX);
                                result.setMessage(BoxResponse.MESSAGE_CODE_PACKAGE_BOX);
                                return result;
                            }
                        }
                    }
                }

                // 调板号服务绑定到板号
                bindToVirtualBoardPo.setMaxItemCount(uccPropertyConfiguration.getVirtualBoardMaxItemCount());
                final com.jd.transboard.api.dto.BindToVirtualBoardPo convertToTcParam = this.getConvertToTcParam(bindToVirtualBoardPo);
                convertToTcParam.setDestinationId(destinationId);
                convertToTcParam.setBarcodeType(isPackageCode ? BoardBarcodeTypeEnum.PACKAGE.getCode() : BoardBarcodeTypeEnum.BOX.getCode());
                final Response<com.jd.transboard.api.dto.VirtualBoardResultDto> handleResult = virtualBoardJsfManager.bindToBoard(convertToTcParam);
                if (!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())) {
                    log.error("VirtualBoardServiceImpl.bindToBoard--fail-- param {} result {}", JsonHelper.toJson(bindToVirtualBoardPo), JsonHelper.toJson(handleResult));
                    result.toFail(handleResult.getMesseage());
                    return result;
                }

                final com.jd.transboard.api.dto.VirtualBoardResultDto virtualBoardResultDtoData = handleResult.getData();
                if (virtualBoardResultDtoData == null) {
                    log.error("VirtualBoardServiceImpl.bindToBoard--fail-- param {} result {}", JsonHelper.toJson(bindToVirtualBoardPo), JsonHelper.toJson(handleResult));
                    result.toFail("组板异常");
                    return result;
                }
                VirtualBoardResultDto virtualBoardResultDto = new VirtualBoardResultDto();
                BeanUtils.copyProperties(virtualBoardResultDtoData, virtualBoardResultDto);
                result.setData(virtualBoardResultDto);
        		OperatorData operatorData = null;
        		if(bindToVirtualBoardPo.getOperatorInfo() != null) {
        			operatorData = new OperatorData();
            		operatorData.setOperatorTypeCode(bindToVirtualBoardPo.getOperatorInfo().getOperatorTypeCode());
            		operatorData.setOperatorId(bindToVirtualBoardPo.getOperatorInfo().getOperatorId()); 
        		}
        		
                // 发送全称跟踪，整板则按板中所有包裹号进行处理
                sendWaybillTrace(operatorData,bindToVirtualBoardPo.getBarCode(), bindToVirtualBoardPo.getOperatorInfo(),
                        virtualBoardResultDto.getBoardCode(), virtualBoardResultDto.getDestinationName(),
                        WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION, bindToVirtualBoardPo.getBizSource());

                // 写自动关闭板号任务
                if(virtualBoardResultDto.getNewBoardIsCreated() != null && virtualBoardResultDto.getNewBoardIsCreated()){
                    this.pushBoardAutoCloseTask(bindToVirtualBoardPo, virtualBoardResultDto, destinationId, Task.TASK_TYPE_VIRTUAL_BOARD_AUTO_CLOSE);
                }
            } finally {
                jimdbCacheService.del(key);
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.bindToBoard--exception param {} exception {}", JsonHelper.toJson(bindToVirtualBoardPo), e.getMessage(), e);
        } finally {
        }
        return result;
    }

    public boolean validationAndCheck(String boxCode, Integer operateType,Integer siteCode, Box box) {
        if (Constants.OPERATE_TYPE_SORTING.equals(operateType) || Constants.OPERATE_TYPE_INSPECTION.equals(operateType)) {
            BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(box.getReceiveSiteCode().toString());
            if (dto == null) {
                log.info("boxes/validation :{} baseService.queryDmsBaseSiteByCode 获取目的地信息 NULL", box.getReceiveSiteCode().toString());
                return false;
            }
            // 获取循环集包袋绑定信息
            String materialCode = cycleBoxService.getBoxMaterialRelation(boxCode);
            // 决定是否绑定循环集包袋
            if (!checkHaveBinDing(materialCode, box.getType(), siteCode)) {
                return false;
            }
        }
        return true;
    }
    /**
     * true 绑定了  false 未绑定
     * @param materialCode
     * @param boxType
     * @param siteCode
     * @return
     */
    private boolean checkHaveBinDing(String materialCode,String boxType,Integer siteCode){
        // 不是BC类型的不拦截
        if(!BusinessHelper.isBCBoxType(boxType)){
            return true;
        }

        // 开关关闭不拦截
        if(!funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(),siteCode)){
            return  true;
        }

        //有集包袋不拦截
        if(!org.springframework.util.StringUtils.isEmpty(materialCode)){
            return true;
        }
        return false;
    }
    /**
     * 推组板发货任务
     * @return
     */
    private boolean pushBoardAutoCloseTask(BindToVirtualBoardPo bindToVirtualBoardPo, VirtualBoardResultDto virtualBoardResultDto, Integer destinationId, Integer taskType) {
        try {
            Task tTask = new Task();
            final OperatorInfo operatorInfo = bindToVirtualBoardPo.getOperatorInfo();
            tTask.setCreateSiteCode(operatorInfo.getSiteCode());
            tTask.setKeyword1(String.valueOf(virtualBoardResultDto.getBoardCode()));
            tTask.setReceiveSiteCode(destinationId);
            tTask.setType(taskType);
            tTask.setTableName(Task.getTableName(taskType));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setKeyword2(operatorInfo.getSiteCode().toString());
            tTask.setFingerprint(Md5Helper.encode(operatorInfo.getSiteCode() + "_" + tTask.getKeyword1() + virtualBoardResultDto.getBoardCode() + tTask.getKeyword2()));
            final Integer virtualBoardAutoCloseDays = uccPropertyConfiguration.getVirtualBoardAutoCloseDays();
            tTask.setExecuteTime(DateUtil.addDate(new Date(), (virtualBoardAutoCloseDays != null && virtualBoardAutoCloseDays > 0) ? virtualBoardAutoCloseDays : 1));

            CloseVirtualBoardPo closeVirtualBoardPo = new CloseVirtualBoardPo();
            closeVirtualBoardPo.setOperatorInfo(operatorInfo);
            closeVirtualBoardPo.setBoardCode(virtualBoardResultDto.getBoardCode());
            tTask.setBoxCode(bindToVirtualBoardPo.getBarCode());
            tTask.setBody(JsonHelper.toJson(closeVirtualBoardPo));
            log.info("pushBoardAutoCloseTask 组板超时自动完结任务推送成功：板号={}", virtualBoardResultDto.getBoardCode());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("pushBoardAutoCloseTask exception ", e);
        }
        return true;
    }

    /**
     * 获取当前用户已扫描的流向信息列表
     * @param bindToVirtualBoardPo 请求参数
     * @return 结果
     * @author fanggang7
     * @time 2021-11-23 19:48:23 周二
     */
    private Result<List<com.jd.transboard.api.dto.VirtualBoardResultDto>> getExistEnableBoardList(BindToVirtualBoardPo bindToVirtualBoardPo){
        Result<List<com.jd.transboard.api.dto.VirtualBoardResultDto>> result = Result.success();
        final OperatorInfo operatorInfo = bindToVirtualBoardPo.getOperatorInfo();
        if(bindToVirtualBoardPo.getVersion() != null) {
            operatorInfo.setVersion(bindToVirtualBoardPo.getVersion());
        }
        if(bindToVirtualBoardPo.getBizSource() != null) {
            operatorInfo.setBizSource(bindToVirtualBoardPo.getBizSource());
        }
        final Response<List<com.jd.transboard.api.dto.VirtualBoardResultDto>> handleResult = virtualBoardJsfManager.getBoardUnFinishInfo(this.getConvertToTcParam(operatorInfo));
        if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("VirtualBoardServiceImpl.getBoardUnFinishInfo--fail-- param {} result {}", JsonHelper.toJson(operatorInfo), JsonHelper.toJson(handleResult));
            return result.toFail("获取已扫流向失败，请稍后再试");
        }
        final List<com.jd.transboard.api.dto.VirtualBoardResultDto> virtualBoardResultDtoQueryData = handleResult.getData();
        if (CollectionUtils.isEmpty(virtualBoardResultDtoQueryData)) {
            return result.toFail("请先扫描流向");
        }
        return result.setData(virtualBoardResultDtoQueryData);
    }

    private Result<Integer> checkAndGetMatchBoardDestination(BindToVirtualBoardPo bindToVirtualBoardPo, Integer destinationId) {
        Result<Integer> result = Result.success();

        Integer destinationIdMatch = destinationId;
        final Result<List<com.jd.transboard.api.dto.VirtualBoardResultDto>> existEnableBoardListResult = this.getExistEnableBoardList(bindToVirtualBoardPo);
        if(!existEnableBoardListResult.isSuccess()){
            return result.toFail(existEnableBoardListResult.getMessage());
        }
        final List<com.jd.transboard.api.dto.VirtualBoardResultDto> virtualBoardResultDtoQueryData = existEnableBoardListResult.getData();

        boolean hasMatchDestinationIdFlag = this.hasMatchDestinationIdFromBoardList(virtualBoardResultDtoQueryData, destinationId);;

        // 如果获取不到匹配流向，则获取大小站
        if(!hasMatchDestinationIdFlag){
            final Integer parentSiteId = baseService.getMappingSite(destinationId);
            if(parentSiteId != null){
                hasMatchDestinationIdFlag = this.hasMatchDestinationIdFromBoardList(virtualBoardResultDtoQueryData, parentSiteId);
                if(hasMatchDestinationIdFlag){
                    destinationIdMatch = parentSiteId;
                }
            }
        }
        return hasMatchDestinationIdFlag ? result.toSuccess(destinationIdMatch, null) : result.toFail("没有找到包裹或箱对应的板号，请确认包裹或箱的流向");
    }

    private Boolean hasMatchDestinationIdFromBoardList(List<com.jd.transboard.api.dto.VirtualBoardResultDto> virtualBoardDtoList, Integer destinationId){
        boolean hasMatchDestinationIdFlag = false;
        for (com.jd.transboard.api.dto.VirtualBoardResultDto virtualBoardResultDtoQueryDatum : virtualBoardDtoList) {
            if(Objects.equals(virtualBoardResultDtoQueryDatum.getDestinationId(), destinationId)){
                hasMatchDestinationIdFlag = true;
                break;
            }
        }
        return hasMatchDestinationIdFlag;
    }

    /**
     * 获取最近一次的发货信息
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    @Override
    public SendM getRecentSendMByParam(String boxCode, Integer createSiteCode, Integer receiveSiteCode, Date operateTime) {
        //查询箱子发货记录
        /* 不直接使用domain的原因，SELECT语句有[test="createUserId!=null"]等其它 */
        SendM queryPara = new SendM();
        queryPara.setBoxCode(boxCode);
        queryPara.setCreateSiteCode(createSiteCode);
        if (receiveSiteCode != null) {
            queryPara.setReceiveSiteCode(receiveSiteCode);
        }
        if (operateTime != null){
            queryPara.setUpdateTime(operateTime);
        }
        List<SendM> sendMList = sendMService.findByParams(queryPara);
        if (null != sendMList && sendMList.size() > 0) {
            return sendMList.get(0);
        }
        return null;
    }

    private com.jd.transboard.api.dto.BindToVirtualBoardPo getConvertToTcParam(BindToVirtualBoardPo bindToVirtualBoardPo) {
        com.jd.transboard.api.dto.BindToVirtualBoardPo bindToVirtualBoardPoTc = new com.jd.transboard.api.dto.BindToVirtualBoardPo();
        BeanUtils.copyProperties(bindToVirtualBoardPo, bindToVirtualBoardPoTc);
        OperatorInfo operatorInfo = bindToVirtualBoardPo.getOperatorInfo();
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        bindToVirtualBoardPoTc.setOperatorInfo(operatorInfoTarget);
        return bindToVirtualBoardPoTc;
    }

    private Result<Void> checkParam4BindToBoard(BindToVirtualBoardPo bindToVirtualBoardPo) {
        Result<Void> result = Result.success();
        final Result<Void> operateInfoCheckResult = this.checkBaseParam(bindToVirtualBoardPo.getOperatorInfo());
        if(!operateInfoCheckResult.isSuccess()){
            return operateInfoCheckResult;
        }
        if(StringUtils.isBlank(bindToVirtualBoardPo.getBarCode())){
            return result.toFail("参数错误，barCode不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        if(CollectionUtils.isEmpty(bindToVirtualBoardPo.getBoardCodeList())){
            return result.toFail("参数错误，boardCodeList不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        List<String> notBlankCodeList = new ArrayList<>();
        for (String boardCode : bindToVirtualBoardPo.getBoardCodeList()) {
            if(StringUtils.isNotBlank(boardCode)){
                notBlankCodeList.add(boardCode);
            }
        }
        bindToVirtualBoardPo.setBoardCodeList(notBlankCodeList);
        return result;
    }
    /**
     * 发送全程跟踪
     * @param operateType
     */
    @Override
    public void sendWaybillTrace(String barcode, OperatorInfo operatorInfo, String boardCode, String destinationName,
                                 Integer operateType, Integer bizSource) {
    	this.sendWaybillTrace(null, barcode, operatorInfo, boardCode, destinationName, operateType, bizSource);
	}
    /**
     * 发送全程跟踪
     * @param operateType
     */
    @Override
    public void sendWaybillTrace(OperatorData operatorData,String barcode, OperatorInfo operatorInfo, String boardCode, String destinationName,
                                 Integer operateType, Integer bizSource) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.boardSendTrace", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(barcode);

            waybillStatus.setCreateSiteCode(operatorInfo.getSiteCode());
            waybillStatus.setCreateSiteName(operatorInfo.getSiteName());

            waybillStatus.setOperatorId(operatorInfo.getUserCode());
            waybillStatus.setOperator(operatorInfo.getUserName());
            // 非自动化的 取当前系统时间
            if(bizSource == null || BizSourceEnum.SORTING_MACHINE.getValue() != bizSource){
                waybillStatus.setOperateTime(new Date());
            }else {
                //自动化取操作时间
                waybillStatus.setOperateTime(operatorInfo.getOperateTime());
            }

            waybillStatus.setOperateType(operateType);
            waybillStatus.setOperatorData(operatorData);
            if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
                waybillStatus.setRemark("包裹号：" + waybillStatus.getPackageCode() + "已进行组板，板号" + boardCode + "，等待送往" + destinationName);
            } else if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
                waybillStatus.setRemark("已取消组板，板号" + boardCode);
            }

            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("组板操作发送全称跟踪失败barcode:{},operatorInfo:{}",barcode, JsonHelper.toJson(operatorInfo), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 转换成全称跟踪的Task
     *
     * @param waybillStatus
     * @return
     */
    private Task toTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }

    /**
     * 组装回全称跟踪对象
     *
     * @param request
     * @return
     */
    private WaybillStatus getWaybillStatus(BoardCombinationRequest request, Integer operateType) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setPackageCode(request.getBoxOrPackageCode());

        tWaybillStatus.setCreateSiteCode(request.getSiteCode());
        tWaybillStatus.setCreateSiteName(request.getSiteName());

        tWaybillStatus.setOperatorId(request.getUserCode());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(new Date());
        tWaybillStatus.setOperateType(operateType);

        if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
            tWaybillStatus.setRemark("包裹号：" + tWaybillStatus.getPackageCode() + "已进行组板，板号" + request.getBoardCode() + "，等待送往" + request.getReceiveSiteName());
        } else if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
            tWaybillStatus.setRemark("已取消组板，板号" + request.getBoardCode());
        }

        return tWaybillStatus;
    }

    /**
     * 删除流向
     * @param removeDestinationPo 删除流向请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.removeDestination",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> removeDestination(RemoveDestinationPo removeDestinationPo) {
        log.info("VirtualBoardServiceImpl.removeDestination--start-- param {}", JsonHelper.toJson(removeDestinationPo));
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            // 存入缓存，防止并发
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkParam4RemoveDestination(removeDestinationPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            final Response<Void> handleResult = virtualBoardJsfManager.removeDestination(this.getConvertToTcParam(removeDestinationPo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.removeDestination--fail-- param {} result {}", JsonHelper.toJson(removeDestinationPo), JsonHelper.toJson(handleResult));
                //首次删除 && 仅1个流向 && 删除流向返回存在板未完结的状态码： 操作人员自选是否完结状态并删除流向
                if(Objects.equals(handleResult.getCode(), Response.CODE_CONFIRM)) {
                    result.toConfirm(handleResult.getMesseage());
                    return result;
                }else {
                    result.toFail(handleResult.getMesseage());
                    return result;
                }
            }

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.removeDestination--exception param {} exception {}", JsonHelper.toJson(removeDestinationPo), e.getMessage(), e);
        } finally {
        }
        return result;
    }

    private com.jd.transboard.api.dto.RemoveDestinationPo getConvertToTcParam(RemoveDestinationPo removeDestinationPo) {
        com.jd.transboard.api.dto.RemoveDestinationPo removeDestinationPoTc = new com.jd.transboard.api.dto.RemoveDestinationPo();
        BeanUtils.copyProperties(removeDestinationPo, removeDestinationPoTc);
        OperatorInfo operatorInfo = removeDestinationPo.getOperatorInfo();
        if(operatorInfo.getBizSource() == null){
            operatorInfo.setBizSource(BizSourceEnum.PDA.getValue());
        }
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        removeDestinationPoTc.setOperatorInfo(operatorInfoTarget);
        return removeDestinationPoTc;
    }

    private Result<Void> checkParam4RemoveDestination(RemoveDestinationPo removeDestinationPo) {
        Result<Void> result = Result.success();
        final Result<Void> operateInfoCheckResult = this.checkBaseParam(removeDestinationPo.getOperatorInfo());
        if(!operateInfoCheckResult.isSuccess()){
            return operateInfoCheckResult;
        }
        if(removeDestinationPo.getDestinationId() == null){
            return result.toFail("参数错误，destinationId不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    /**
     * 完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.closeBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> closeBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        log.info("VirtualBoardServiceImpl.closeBoard--start-- param {}", JsonHelper.toJson(closeVirtualBoardPo));
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            // 存入缓存，防止并发
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkParam4CloseBoard(closeVirtualBoardPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            final Response<Void> handleResult = virtualBoardJsfManager.closeBoard(this.getConvertToTcParam(closeVirtualBoardPo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.closeBoard--fail-- param {} result {}", JsonHelper.toJson(closeVirtualBoardPo), JsonHelper.toJson(handleResult));
                result.toFail(handleResult.getMesseage());
                return result;
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.closeBoard--exception param {} exception {}", JsonHelper.toJson(closeVirtualBoardPo), e.getMessage(), e);
        } finally {
        }
        return result;
    }

    private com.jd.transboard.api.dto.CloseVirtualBoardPo getConvertToTcParam(CloseVirtualBoardPo closeVirtualBoardPo) {
        com.jd.transboard.api.dto.CloseVirtualBoardPo closeVirtualBoardPoTc = new com.jd.transboard.api.dto.CloseVirtualBoardPo();
        BeanUtils.copyProperties(closeVirtualBoardPo, closeVirtualBoardPoTc);
        OperatorInfo operatorInfo = closeVirtualBoardPo.getOperatorInfo();
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        closeVirtualBoardPoTc.setOperatorInfo(operatorInfoTarget);
        return closeVirtualBoardPoTc;
    }

    private Result<Void> checkParam4CloseBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        Result<Void> result = Result.success();
        final Result<Void> operateInfoCheckResult = this.checkBaseParam(closeVirtualBoardPo.getOperatorInfo());
        if(!operateInfoCheckResult.isSuccess()){
            return operateInfoCheckResult;
        }
        if(StringUtils.isBlank(closeVirtualBoardPo.getBoardCode())){
            return result.toFail("参数错误，boardCode不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    /**
     * 取消组板
     * @param unbindToVirtualBoardPo 取消组板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.unbindToBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<UnbindVirtualBoardResultDto> unbindToBoard(UnbindToVirtualBoardPo unbindToVirtualBoardPo) {
        log.info("VirtualBoardServiceImpl.unbindToBoard--start-- param {}", JsonHelper.toJson(unbindToVirtualBoardPo));
        JdCResponse<UnbindVirtualBoardResultDto> result = new JdCResponse<>();
        result.toSucceed();
        try {
            // 存入缓存，防止并发
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkParam4UnbindToBoard(unbindToVirtualBoardPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            final OperatorInfo operatorInfo = unbindToVirtualBoardPo.getOperatorInfo();
            // 校验是否已封车，已封车不可取消
            final SendM recentSendM = getRecentSendMByParam(unbindToVirtualBoardPo.getBarCode(), operatorInfo.getSiteCode(), null, null);
            if (recentSendM != null) {
                if (newSealVehicleService.checkSendCodeIsSealed(recentSendM.getSendCode())) {
                    result.toFail("该箱/包裹已封车，不可取消组板");
                    return result;
                }
            }

            final Response<com.jd.transboard.api.dto.UnbindVirtualBoardResultDto> handleResult = virtualBoardJsfManager.unbindToBoard(this.getConvertToTcParam(unbindToVirtualBoardPo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.unbindToBoard--fail-- param {} result {}", JsonHelper.toJson(unbindToVirtualBoardPo), JsonHelper.toJson(handleResult));
                result.toFail(handleResult.getMesseage());
                return result;
            }
            final com.jd.transboard.api.dto.UnbindVirtualBoardResultDto unbindVirtualBoardResultData = handleResult.getData();
            UnbindVirtualBoardResultDto unbindVirtualBoardResultDto = new UnbindVirtualBoardResultDto();
            BeanUtils.copyProperties(unbindVirtualBoardResultData, unbindVirtualBoardResultDto);
            result.setData(unbindVirtualBoardResultDto);

            //发送取消组板的全称跟踪
            for (String barcode : unbindVirtualBoardResultDto.getCancelBarcodeList()) {
                UnbindToVirtualBoardPo unbindToVirtualBoardPoTemp = new UnbindToVirtualBoardPo();
                BeanUtils.copyProperties(unbindToVirtualBoardPo, unbindToVirtualBoardPoTemp);
                unbindToVirtualBoardPoTemp.setBarCode(barcode);
                sendWaybillTrace(unbindToVirtualBoardPoTemp, unbindVirtualBoardResultDto.getBoardCode(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL);
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.unbindToBoard--exception param {} exception {}", JsonHelper.toJson(unbindToVirtualBoardPo), e.getMessage(), e);
        } finally {
        }
        return result;
    }

    private com.jd.transboard.api.dto.UnbindToVirtualBoardPo getConvertToTcParam(UnbindToVirtualBoardPo unbindToVirtualBoardPo) {
        com.jd.transboard.api.dto.UnbindToVirtualBoardPo unBindToVirtualBoardPoTc = new com.jd.transboard.api.dto.UnbindToVirtualBoardPo();
        BeanUtils.copyProperties(unbindToVirtualBoardPo, unBindToVirtualBoardPoTc);
        OperatorInfo operatorInfo = unbindToVirtualBoardPo.getOperatorInfo();
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        unBindToVirtualBoardPoTc.setOperatorInfo(operatorInfoTarget);
        return unBindToVirtualBoardPoTc;
    }

    private Result<Void> checkParam4UnbindToBoard(UnbindToVirtualBoardPo unBindToVirtualBoardPo) {
        Result<Void> result = Result.success();
        final Result<Void> operateInfoCheckResult = this.checkBaseParam(unBindToVirtualBoardPo.getOperatorInfo());
        if(!operateInfoCheckResult.isSuccess()){
            return operateInfoCheckResult;
        }
        if(StringUtils.isBlank(unBindToVirtualBoardPo.getBarCode())){
            return result.toFail("参数错误，barCode不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        return result;
    }

    /**
     * 发送全程跟踪
     * @param operateType
     */
    private void sendWaybillTrace(UnbindToVirtualBoardPo unbindToVirtualBoardPo, String boardCode, Integer operateType) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.boardSendTrace", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(unbindToVirtualBoardPo.getBarCode());
            final OperatorInfo operatorInfo = unbindToVirtualBoardPo.getOperatorInfo();

            waybillStatus.setCreateSiteCode(operatorInfo.getSiteCode());
            waybillStatus.setCreateSiteName(operatorInfo.getSiteName());
            waybillStatus.setOperatorId(operatorInfo.getUserCode());
            waybillStatus.setOperator(operatorInfo.getUserName());
            waybillStatus.setOperateTime(new Date());
            waybillStatus.setOperateType(operateType);
            if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
                waybillStatus.setRemark("已取消组板，板号" + boardCode);
            }
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("组板操作发送全称跟踪失败:{}", JsonHelper.toJson(unbindToVirtualBoardPo), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 查询是否开通组板功能
     * @param operatorInfo 操作人信息
     * @return 返回是否能使用结果
     * @author fanggang7
     * @time 2021-09-14 11:22:19 周二
     */
    @Override
    public JdCResponse<Boolean> canUseMenu(OperatorInfo operatorInfo) {
        log.info("VirtualBoardServiceImpl.canUseMenu--start-- param {}", JsonHelper.toJson(operatorInfo));
        JdCResponse<Boolean> result = new JdCResponse<>();
        result.setData(false);
        result.toSucceed();
        try {
            final boolean matchVirtualSiteCanUseSite = uccPropertyConfiguration.matchVirtualSiteCanUseSite(operatorInfo.getSiteCode());
            result.setData(matchVirtualSiteCanUseSite);
            if(!matchVirtualSiteCanUseSite){
                result.setMessage(HintService.getHint(HintCodeConstants.YOUR_SITE_CAN_NOT_USE_FUNC));
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.canUseMenu--exception param {} exception {}", JsonHelper.toJson(operatorInfo), e.getMessage(), e);
        } finally {
        }
        return result;
    }

    /**
     * 处理定时完结板号任务
     * @param task 任务参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-22 17:39:26 周日
     */
    @Override
    public boolean handleTimingCloseBoard(Task task) {
        // 托盘号生成24小时后，自动完结
        // 查询创建时间已过24小时，且板号为未完结状态的板号
        CloseVirtualBoardPo closeVirtualBoardPo = JsonHelper.fromJson(task.getBody(), CloseVirtualBoardPo.class);
        if(closeVirtualBoardPo == null || StringUtils.isBlank(closeVirtualBoardPo.getBoardCode())){
            log.error("handleTimingCloseBoard--fail taskBody is null");
            return false;
        }
        handleTimingCloseBoard(closeVirtualBoardPo);
        return true;
    }

    /**
     * 处理定时完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-22 17:39:26 周日
     */
    @Override
    public Result<Void> handleTimingCloseBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        Result<Void> result = Result.success();
        // 托盘号生成24小时后，自动完结
        // 根据板号查询板状态和创建时间
        final Response<Board> boardResult = groupBoardManager.getBoard(closeVirtualBoardPo.getBoardCode());
        if(!Objects.equals(200, boardResult.getCode())) {
            return result.toFail("根据板号查询板数据异常");
        }
        final Board board = boardResult.getData();
        if(board == null){
            return result.toFail("根据板号未查询到板数据");
        }
        if(Objects.equals(board.getStatus(), BoardStatus.CLOSED.getIndex())){
            return result.toSuccess("已经是完结状态");
        }
        final Response<Boolean> closeResult = groupBoardManager.closeBoard(closeVirtualBoardPo.getBoardCode());
        if(!Objects.equals(200, closeResult.getCode())) {
            return result.toFail("关闭板异常");
        }
        return null;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.VirtualBoardServiceImpl.handoverBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> handoverBoard(HandoverVirtualBoardPo handoverVirtualBoardPo) {
        log.info("VirtualBoardServiceImpl.handoverBoard--start-- param {}", JsonHelper.toJson(handoverVirtualBoardPo));
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            final Response<Void> handleResult = virtualBoardJsfManager.handoverBoard(this.getConvertToTcParam(handoverVirtualBoardPo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.handoverBoard--fail-- param {} result {}", JsonHelper.toJson(handoverVirtualBoardPo), JsonHelper.toJson(handleResult));
                result.toFail(handleResult.getMesseage());
                return result;
            }
        } catch (RuntimeException e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.handoverBoard--exception param {} exception {}", JsonHelper.toJson(handoverVirtualBoardPo), e.getMessage(), e);
        }

        return result;
    }

    @Override
    public JdCResponse<VirtualBoardResultDto> getBoxCountByBoardCode(String boardCode) {
        JdCResponse<VirtualBoardResultDto> result = new JdCResponse<>();
        Response<com.jd.transboard.api.dto.VirtualBoardResultDto> handleResult = virtualBoardJsfManager.getBoxCountByBoardCode(boardCode);
        if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("VirtualBoardServiceImpl.getBoxCountByBoardCode--fail-- param {} result {}", boardCode, JsonHelper.toJson(handleResult));
            result.toFail("获取板号统计信息失败，请稍后再试");
            return result;
        }
        com.jd.transboard.api.dto.VirtualBoardResultDto virtualBoardResultDto = handleResult.getData();
        if (null == virtualBoardResultDto) {
            result.toFail("查询数据异常，请联系分拣小秘排查！");
            return result;
        }
        VirtualBoardResultDto dto = new VirtualBoardResultDto();
        BeanUtils.copyProperties(virtualBoardResultDto, dto);
        result.setData(dto);
        result.toSucceed();
        return result;
    }

    private com.jd.transboard.api.dto.HandoverVirtualBoardPo getConvertToTcParam(HandoverVirtualBoardPo handoverVirtualBoardPo) {
        com.jd.transboard.api.dto.HandoverVirtualBoardPo handoverVirtualBoardPoTc = new com.jd.transboard.api.dto.HandoverVirtualBoardPo();
        BeanUtils.copyProperties(handoverVirtualBoardPo, handoverVirtualBoardPoTc);
        OperatorInfo operatorInfo = handoverVirtualBoardPo.getOperatorInfo();
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
        handoverVirtualBoardPoTc.setOperatorInfo(operatorInfoTarget);
        return handoverVirtualBoardPoTc;
    }
}
