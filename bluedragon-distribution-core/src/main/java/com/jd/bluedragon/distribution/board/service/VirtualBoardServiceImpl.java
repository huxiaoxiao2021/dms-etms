package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.*;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.common.dto.board.response.UnbindVirtualBoardResultDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.IVirtualBoardJsfManager;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.ucc.client.config.UccPropertyConfig;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.dms.workbench.utils.sdk.constants.ResultCodeConstant;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BoardBarcodeTypeEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private IVirtualBoardJsfManager virtualBoardJsfManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 获取组板已存在的未完成数据
     * @param operatorInfo 操作人信息
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-14 18:25:31 周六
     */
    @Override
    public JdCResponse<List<VirtualBoardResultDto>> getBoardUnFinishInfo(OperatorInfo operatorInfo) {
        log.info("VirtualBoardServiceImpl.getBoardUnFinishInfo--start-- param {}", JsonHelper.toJson(operatorInfo));
        List<VirtualBoardResultDto> virtualBoardResultDtoList = new ArrayList<>();
        JdCResponse<List<VirtualBoardResultDto>> result = new JdCResponse<>(ResponseEnum.SUCCESS.getIndex(), null, virtualBoardResultDtoList);
        try {
            // 存入缓存，防止并发
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkBaseParam(operatorInfo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
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

    private com.jd.transboard.api.dto.base.OperatorInfo getConvertToTcParam(OperatorInfo operatorInfo) {
        final com.jd.transboard.api.dto.base.OperatorInfo operatorInfoTarget = new com.jd.transboard.api.dto.base.OperatorInfo();
        BeanUtils.copyProperties(operatorInfo, operatorInfoTarget);
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
            // 存入缓存，防止并发
            // 1. 参数验证
            final Result<Void> baseCheckResult = this.checkParam4CreateOrGetBoard(addOrGetVirtualBoardPo);
            if(!baseCheckResult.isSuccess()){
                result.setCode(baseCheckResult.getCode());
                result.setMessage(baseCheckResult.getMessage());
                return result;
            }
            addOrGetVirtualBoardPo.setMaxDestinationCount(uccPropertyConfiguration.getVirtualBoardMaxDestinationCount());
            final Response<com.jd.transboard.api.dto.VirtualBoardResultDto> handleResult = virtualBoardJsfManager.createOrGetBoard(this.getConvertToTcParam(addOrGetVirtualBoardPo));
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.createOrGetBoard--fail-- param {} result {}", JsonHelper.toJson(addOrGetVirtualBoardPo), JsonHelper.toJson(handleResult));
                result.toFail(handleResult.getMesseage());
                return result;
            }
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.createOrGetBoard--exception param {} exception {}", JsonHelper.toJson(addOrGetVirtualBoardPo), e.getMessage(), e);
        } finally {
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
            final OperatorInfo operatorInfo = bindToVirtualBoardPo.getOperatorInfo();
            Integer destinationId = null;
            if(isPackageCode){
                final Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
                if(waybill == null){
                    result.toFail("未查找到运单数据");
                    return result;
                }
                if(waybill.getOldSiteId() == null){
                    result.toFail("运单对应的预分拣站点为空");
                    return result;
                }
                destinationId = waybill.getOldSiteId();
                // 拦截链校验
                final PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
                pdaOperateRequest.setPackageCode(bindToVirtualBoardPo.getBarCode());
                pdaOperateRequest.setBoxCode(bindToVirtualBoardPo.getBarCode());
                pdaOperateRequest.setReceiveSiteCode(waybill.getOldSiteId());
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
            if(isBoxCode){
                final Box boxExist = boxService.findBoxByCode(bindToVirtualBoardPo.getBarCode());
                if (boxExist == null) {
                    result.toFail("未找到对应箱号，请检查");
                    return result;
                }
                destinationId = boxExist.getReceiveSiteCode();
            }
            // 调板号服务绑定到板号
            bindToVirtualBoardPo.setMaxItemCount(uccPropertyConfiguration.getVirtualBoardMaxItemCount());
            final com.jd.transboard.api.dto.BindToVirtualBoardPo convertToTcParam = this.getConvertToTcParam(bindToVirtualBoardPo);
            convertToTcParam.setDestinationId(destinationId);
            convertToTcParam.setBarcodeType(barCodeTypeEnumName.getCode());
            final Response<com.jd.transboard.api.dto.VirtualBoardResultDto> handleResult = virtualBoardJsfManager.bindToBoard(convertToTcParam);
            if(!Objects.equals(handleResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
                log.error("VirtualBoardServiceImpl.bindToBoard--fail-- param {} result {}", JsonHelper.toJson(bindToVirtualBoardPo), JsonHelper.toJson(handleResult));
                result.toFail(handleResult.getMesseage());
                return result;
            }
            // 发送组板全程跟踪
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("VirtualBoardServiceImpl.bindToBoard--exception param {} exception {}", JsonHelper.toJson(bindToVirtualBoardPo), e.getMessage(), e);
        } finally {
        }
        return result;
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
        if(bindToVirtualBoardPo.getBoardCodeList().size() > 5){
            return result.toFail(String.format("最多可传入%s个板号", 5), ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        return result;
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
                result.toFail(handleResult.getMesseage());
                return result;
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
     * 处理定时完结板号
     * @param closeVirtualBoardPo 完结板请求参数
     * @return 返回板结果
     * @author fanggang7
     * @time 2021-08-22 17:39:26 周日
     */
    @Override
    public JdCResponse<Void> handleTimingCloseBoard(CloseVirtualBoardPo closeVirtualBoardPo) {
        // 托盘号生成24小时后，自动完结
        // 查询创建时间已过24小时，且板号为未完结状态的板号
        return null;
    }
}
