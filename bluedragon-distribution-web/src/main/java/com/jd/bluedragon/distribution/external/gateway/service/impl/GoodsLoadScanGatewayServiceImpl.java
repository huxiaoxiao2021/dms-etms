package com.jd.bluedragon.distribution.external.gateway.service.impl;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.List;

public class GoodsLoadScanGatewayServiceImpl implements GoodsLoadScanGatewayService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ExceptionScanService exceptionScanService;

    @Resource
    private LoadScanService loadScanService;

    @Autowired
    private LoadService loadService;

    @Resource
    private LoadScanCacheService loadScanCacheService;

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Autowired
    private DmsConfigManager dmsConfigManager ;

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsRemoveScanning",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsRemoveScanning(GoodsExceptionScanningReq req) {
        JdCResponse response = new JdCResponse<Boolean>();

        try {
            if(req.getTaskId() == null){
                response.toFail("任务号不能为空");
                return response;
            }

            LoadCar loadCar = loadScanService.findTaskStatus(req.getTaskId());
            if(loadCar == null) {
                if(log.isInfoEnabled()) {
                    log.info("操作任务【{}】时，查不到该任务信息", req.getTaskId());
                }
                response.toFail("无法查询到该任务信息，请确认任务是否存在");
                return response;
            }

            Integer taskStatus = loadCar.getStatus();
            if(taskStatus == null) {
                response.toFail("该任务状态存在异常,无法操作取消扫描动作");
                return response;

            }else if(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(taskStatus)) {
                response.toFail("该任务已经完成发货，无法操作取消扫描动作");
                return response;

            } else if(!GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskStatus)){
                response.toFail("只有【已开始】任务可操作发货，请检查任务状态");
                return response;
            }

            //校验删除任务人的权限
//            if (!loadCarHelperService.checkUserPermission(req.getTaskId(), req.getUser().getUserErp())) {
//                response.toFail("非任务创建人或协助人不可取消包裹！");
//                return response;
//            }

            if(StringUtils.isBlank(req.getPackageCode())){
                response.toFail("包裹号不能为空");
                return response;
            }

            if(req.getUser() == null) {
                response.toFail("当前操作用户信息不能为空");
                return response;
            }else {
                if(req.getUser().getUserName() == null) {
                    response.toFail("当前操作用户名称不能为空");
                    return response;
                }
            }

            if(req.getCurrentOperate() == null) {
                response.toFail("当前分拣中心信息不能为空");
                return response;
            } else {
                if(req.getCurrentOperate().getSiteName() == null) {
                    response.toFail("当前分拣中心名称不能为空");
                    return response;
                }
            }

            GoodsLoadScanRecord record = new GoodsLoadScanRecord();
            record.setTaskId(req.getTaskId());
            record.setPackageCode(req.getPackageCode());

            ExceptionScanDto exceptionScanDto = exceptionScanService.findExceptionGoodsScan(record);

            if(exceptionScanDto == null) {
                if(log.isDebugEnabled()) {
                    log.debug("取消包裹扫描查询包裹失败，包裹信息【{}】", record);
                }
                response.toFail("此包裹号未操作装车，无法取消");
                return response;

            }else if(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_Y.equals(exceptionScanDto.getForceStatus())){
                if(log.isDebugEnabled()) {
                    log.debug("取消包裹扫描查询该包裹已被强发，包裹信息【{}】", record);
                }
                response.toFail("此包裹已被操作强发，无法取消");
                return response;
            }

            exceptionScanDto.setOperator(req.getUser().getUserName());
            exceptionScanDto.setOperatorCode(req.getUser().getUserCode());
            exceptionScanDto.setCurrentSiteCode(req.getCurrentOperate().getSiteCode());
            exceptionScanDto.setCurrentSiteName(req.getCurrentOperate().getSiteName());
            boolean removeRes =  exceptionScanService.removeGoodsScan(exceptionScanDto);

            if(!removeRes) {
                response.toError("取消包裹扫描失败");
                return response;
            }

            response.toSucceed("取消包裹扫描成功");
        }catch (GoodsLoadScanException e) {
            log.error("取消发货系统异常--error--【{}】", e);
            response.toError("取消发货系统异常");
        }
        return response;
    }


    /**
     * 该方法已经舍弃，业务需要，不需货物要强制下发
     * @param req
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsCompulsoryDeliver",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {

        JdCResponse response = new JdCResponse<Boolean>();
        try {
            if(req.getTaskId() == null) {
                response.toFail("任务号不能为空");
                return response;
            }

            LoadCar loadCar = loadScanService.findTaskStatus(req.getTaskId());
            if(loadCar == null) {
                if(log.isDebugEnabled()) {
                    log.debug("操作任务【{}】时，查不到该任务信息", req.getTaskId());
                }
                response.toFail("无法查询到该任务信息，请确认任务是否存在");
                return response;
            }

            Integer taskStatus = loadCar.getStatus();
            if(taskStatus == null) {
                response.toFail("该任务状态存在异常,无法操作强发");
                return response;

            }else if(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(taskStatus)) {
                response.toFail("该任务已经完成发货，请勿操作强发动作");
                return response;
            } else if(!GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskStatus)){
                response.toFail("只有【已开始】任务可操作发货，请检查任务状态");
                return response;
            }

            if(req.getWaybillCode() == null || req.getWaybillCode().size() <=0) {
                response.toFail("运单号不能为空");
                return response;
            }

            if(req.getUser() == null) {
                response.toFail("当前操作用户信息不能为空");
                return response;
            }else {
                if(req.getUser().getUserName() == null) {
                    response.toFail("当前操作用户名称不能为空");
                    return response;
                }
            }

            if(req.getCurrentOperate() == null) {
                response.toFail("当前分拣中心信息不能为空");
                return response;
            }else {
                if(req.getCurrentOperate().getSiteName() == null) {
                    response.toFail("当前分拣中心名称不能为空");
                    return response;
                }
            }

            boolean res = exceptionScanService.goodsCompulsoryDeliver(req);

            if(!res) {
                response.toError("强制下发失败");
                return response;
            }
            response.toSucceed("强制下发成功");

        }catch (GoodsLoadScanException e) {
            log.error("装车强制下发出错--error--【{}】：", e);
            response.toError("强制下发异常");
        }

        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.findExceptionGoodsLoading",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req) {
        JdCResponse<List<GoodsExceptionScanningDto>> response = new JdCResponse<>();

        try {
            if(req.getTaskId() == null) {
                response.toFail("任务号不能为空");
                return response;
            }

            LoadCar loadCar = loadScanService.findTaskStatus(req.getTaskId());
            if(loadCar == null) {
                if(log.isDebugEnabled()) {
                    log.debug("操作任务【{}】时，查不到该任务信息", req.getTaskId());
                }
                response.toFail("无法查询到该任务信息，请确认任务是否存在");
                return response;
            }

            Integer taskStatus = loadCar.getStatus();
            if(taskStatus == null) {
                log.error("装车发货不齐异常数据查询，任务【{}】状态为null", req.getTaskId());
                response.toFail("该任务状态存在异常,无法操作查询");
                return response;

            }else if(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(taskStatus)) {
                response.toFail("该任务已经完成发货，请勿操作异常查询");
                return response;

            } else if(!GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskStatus)){
                response.toFail("只有【已开始】任务可操作发货，请检查任务状态");
                return response;
            }

            if(req.getUser() == null) {
                response.toFail("当前操作用户信息不能为空");
                return response;

            }else {
                if(req.getUser().getUserName() == null) {
                    response.toFail("当前操作用户名称不能为空");
                    return response;
                }
            }

            if(req.getCurrentOperate() == null) {
                response.toFail("当前分拣中心信息不能为空");
                return response;

            }else {
                if(req.getCurrentOperate().getSiteName() == null) {
                    response.toFail("当前分拣中心名称不能为空");
                    return response;
                }
            }

            List<GoodsExceptionScanningDto> list = exceptionScanService.findAllExceptionGoodsScan(req.getTaskId());
            response.toSucceed("不齐异常数据查找成功");
            response.setData(list);

        }catch(GoodsLoadScanException e){
            log.error("查询不齐异常错误--error--【{}】",e);
            response.toError("不齐异常数据查找异常");
        }

        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsLoadingDeliver",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsLoadingDeliver(GoodsLoadingReq req) {

        JdCResponse response = new JdCResponse();
        String lock = null;
        try {
            if(req.getTaskId() == null) {
                response.toFail("任务号不能为空");
                return response;
            }

            lock = req.getTaskId().toString();
            //锁超市单位：分钟
            if(!loadScanCacheService.lock(lock, 1)){
                log.error("任务发货【{}】重复提交", req.getTaskId());
                response.toFail("任务正在发货中，请勿重复提交");
            }

            //防止PDA-1用户在发货页面停留过久，期间PDA-2用户操作了发货，此时发货状态已经改变为已完成，PDA不能再进行发货动作
            LoadCar loadCar = loadService.findLoadCarById(req.getTaskId());
            if(loadCar == null) {
                if(log.isDebugEnabled()) {
                    log.debug("操作任务【{}】时，查不到该任务信息", req.getTaskId());
                }
                response.toFail("无法查询到该任务信息，请确认任务是否存在");
                return response;
            }

            Integer taskStatus = loadCar.getStatus();
            if(taskStatus == null) {
//                throw new GoodsLoadScanException("该任务状态存在异常,无法发货");
                response.toFail("该任务状态存在异常,无法发货");
                return response;

            }else if(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(taskStatus)) {
                response.toFail("该任务已经完成发货，请勿重复发货");
                return response;
            } else if(!GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskStatus)){
//                throw new GoodsLoadScanException("任务【" + req.getTaskId() + "】 状态异常，状态值为" + taskStatus + ",仅状态为1(已开始)的任务可进行发货");
                response.toFail("仅已开始任务可以执行发货操作，请检查任务状态");
                return response;
            }
//        }

            if(req.getSendCode() == null) {
                response.toFail("发货批次号不能为空");
                return response;
            }

            if(req.getReceiveSiteCode() == null) {
                response.toFail("收货单位编码不能为空");
                return response;
            }

            if(req.getUser() == null) {
                response.toFail("当前操作用户信息不能为空");
                return response;
            }else {
                if(req.getUser().getUserName() == null) {
                    response.toFail("当前操作用户名称不能为空");
                    return response;
                }
            }

            if(req.getCurrentOperate() == null) {
                response.toFail("当前分拣中心信息不能为空");
                return response;
            }else {
                if(StringUtils.isBlank(req.getCurrentOperate().getSiteName())) {
                    response.toFail("发货单位名称不能为空");
                    return response;
                }
                if (req.getCurrentOperate().getSiteCode() == 0) {
                    response.toFail("发货站点编码不合法");
                    return response;
                }
                //如果前端操作场地和任务创建场地不同，以任务创建场地为主（避免操作人员所属场地变更但是组织架构尚未变更）
                if(req.getCurrentOperate().getSiteCode() != loadCar.getCreateSiteCode().intValue()) {
                    CurrentOperate currentOperate = new CurrentOperate();
                    currentOperate.setSiteCode(loadCar.getCreateSiteCode().intValue());
                    currentOperate.setSiteName(loadCar.getCreateSiteName());
                    req.setCurrentOperate(currentOperate);
                }
            }

            if(exceptionScanService.checkException(req.getTaskId())) {
                response.toFail("本次装车存在不齐运单，请点击不齐异常处理操作");
                return response;
            }

            JdCResponse<Boolean> checkPhotoRes = loadScanService.uploadPhotoCheck(req);
            if(!checkPhotoRes.getCode().equals(200)) {
                response.toFail(checkPhotoRes.getMessage());
                return response;
            }else if(checkPhotoRes.getData() != null && checkPhotoRes.getData()) {
                response.toFail("操作装车完成，请先上传装车照片");
                return response;
            }

            try{
                response = loadScanService.goodsLoadingDeliver(req);
            } catch(GoodsLoadScanException e) {
                response.toFail("发货失败");
                return response;
            }

        }catch (GoodsLoadScanException e) {
            log.error("装车完成发货出错--error--【{}】：", e);
            response.toError("装车发货完成异常");
        }finally {
            loadScanCacheService.unLock(lock);
        }

        return response;
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsLoadingScan",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<LoadScanDetailDto> goodsLoadingScan(GoodsLoadingScanningReq req) {
        
        if (req == null || req.getTaskId() == null) {
            log.warn("扫描入口--任务ID为空");
            JdCResponse<LoadScanDetailDto> response = new JdCResponse<>();
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("参数为空");
            return response;
        }
        log.info("根据任务ID查找装车扫描列表，taskId={}", req.getTaskId());

        return loadScanService.goodsLoadingScan(req);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.checkBatchCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req) {

        JdCResponse<Void> response = new JdCResponse<>();
        if (req == null || req.getTaskId() == null || req.getUser() == null) {
            log.warn("扫描批次号--参数为空");
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("参数为空");
            return response;
        }

        log.info("根据任务ID和批次号开始检验：taskId={},batchCode={}", req.getTaskId(), req.getBatchCode());

        if (StringUtils.isBlank(req.getBatchCode())) {
            response.setCode(JdVerifyResponse.CODE_FAIL);
            response.setMessage("参数校验错误，请检查批次号是否填写");
            return response;
        }

        // 如果批次号不为空，校验批次号
       return loadScanService.checkBatchCode(req, response);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.checkPackageCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req) {

        JdVerifyResponse<Void> response = new JdVerifyResponse<>();
        if (req == null || req.getTaskId() == null) {
            log.warn("扫描包裹号--任务ID为空");
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("参数为空");
            return response;
        }
        log.info("根据任务ID和包裹号开始检验：taskId={},packageCode={},transfer={}", req.getTaskId(), req.getPackageCode(), req.getTransfer());

        if (StringUtils.isBlank(req.getPackageCode())) {
            response.setCode(JdVerifyResponse.CODE_FAIL);
            response.setMessage("参数校验错误，请检查包裹号是否填写");
            return response;
        }

        if (req.getTransfer() != null) {
            // 勾选【包裹号转板号】
            if (GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_BOARD.equals(req.getTransfer())) {
                log.info("校验包裹--包裹号转板号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
                return loadScanService.checkBoardCode(req, response);
            }
            // 勾选【包裹号转大宗】
            if (GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL.equals(req.getTransfer())) {
                log.info("校验包裹--包裹号转大宗：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
                return loadScanService.checkWaybillCode(req, response);
            }
        }
        log.info("校验包裹--常规包裹号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
        return loadScanService.checkPackageCode(req, response);
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.saveByPackageCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> saveByPackageCode(GoodsLoadingScanningReq req) {

        JdCResponse<Void> response = new JdCResponse<>();
        if (req == null || req.getTaskId() == null) {
            log.warn("包裹暂存接口--任务ID为空");
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("参数为空");
            return response;
        }
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();

        log.info("开始调用暂存接口：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);

        if (StringUtils.isBlank(packageCode)) {
            log.warn("开始调用暂存接口--参数校验--包裹号不能为空：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("包裹号不能为空");
            return response;
        }

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadService.findLoadCarById(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }

        if (req.getTransfer() != null) {
            // 勾选【包裹号转板号】
            if (GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_BOARD.equals(req.getTransfer())) {
                log.info("暂存包裹--包裹号转板号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
                return loadScanService.saveLoadScanByBoardCode(req, response, loadCar);
            }
            // 勾选【包裹号转大宗】
            if (GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL.equals(req.getTransfer())) {
                log.info("暂存包裹--包裹号转大宗：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
                int packageNum = WaybillUtil.getPackNumByPackCode(packageCode);
                if(packageNum < dmsConfigManager.getPropertyConfig().getDazongPackageOperateMax()){
                    response.setCode(JdCResponse.CODE_FAIL);
                    response.setMessage("此单非大宗超量运单，请进行逐包裹扫描操作！");
                    return response;
                }
                return loadScanService.saveLoadScanByWaybillCode(req, response, loadCar);
            }
        }
        log.info("暂存包裹--常规包裹号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
        return loadScanService.checkInspectAndSave(req, response, loadCar);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.findUnloadPackages",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<String>> findUnloadPackages(GoodsLoadingScanningReq req) {
        JdCResponse<List<String>> response = new JdCResponse<>();
        if (req == null || req.getTaskId() == null) {
            log.warn("运单未装包裹明细接口--任务ID为空");
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("参数为空");
            return response;
        }
        if (StringUtils.isBlank(req.getWayBillCode())) {
            log.warn("开始调用运单未装包裹明细接口--参数校验--运单号不能为空：taskId={}", req.getTaskId());
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("运单号不能为空");
            return response;
        }
        return loadScanService.findUnloadPackages(req, response);
    }

    //跟进传入信息，判断对应的运单是否已经妥投，使用场景新老订单误扫问题现场判断是否继续操作，后续使用请加入当前知道的使用场景，谢谢。
    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.checkWaybillIsFinish",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdVerifyResponse<Void> checkWaybillIsFinish(String scanBarString) {
        JdVerifyResponse<Void> response = new JdVerifyResponse<>();
        response.setCode(JdCResponse.CODE_SUCCESS);
        //返回信息的类型 共4种，便于app匹配接下来的动作.
        //参数校验缺一不可.
        if(StringUtils.isEmpty(scanBarString)){
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("判断运单是否妥投，缺少相应的包裹号或者运单号,请检查是否扫描.");
            return response;
        }

        Boolean  isPackage = WaybillUtil.isPackageCode(scanBarString);
        Boolean  isWaybillCode = WaybillUtil.isWaybillCode(scanBarString);
        String  waybillCode = null;
        //
        if(isPackage){
            waybillCode = WaybillUtil.getWaybillCode(scanBarString);
        }else if(isWaybillCode){
            waybillCode = scanBarString;
        }else{
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("判断运单是否妥投，输入的信息非法,请输入包裹号或者运单号!传入信息为:"+scanBarString);
            return response;
        }
        if(StringUtils.isEmpty(waybillCode)){
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("判断运单是否妥投，根据入参获取到的运单号为空!传入信息为:"+scanBarString);
            return response;
        }
        Boolean isFinished = waybillTraceManager.isWaybillFinished(waybillCode);
        if(isFinished){
            JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
            msgBox.setType(MsgBoxTypeEnum.CONFIRM);
//            response.setCode(JdCResponse.CODE_CONFIRM);
            msgBox.setMsg("运单已妥投,请确认是否继续操作.传入信息:"+scanBarString);
            response.addBox(msgBox);
        }
        return response;
    }

    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.getInspectNoSendNoLoadWaybillDetail",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public JdCResponse<LoadScanDetailDto> getInspectNoSendNoLoadWaybillDetail(GoodsLoadingScanningReq req) {
        JdCResponse<LoadScanDetailDto> res = new JdCResponse<>();
        try{
            if(dmsConfigManager.getPropertyConfig().getInspectNoSendNoLoadWaybillDemotion()){
                res.setCode(JdCResponse.CODE_FAIL);
                res.setMessage("该服务已操作降级处理，暂时不支持查询，请联系研发处理");
                return res;
            }
            if(req == null) {
                res.setCode(JdCResponse.CODE_FAIL);
                res.setMessage("请求参数不能为空");
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("GoodsLoadScanGatewayServiceImpl.getInspectNoSendNoLoadWaybillDetail---begin---parameter=【{}】", JsonHelper.toJson(req));
            }
            if(req.getUser() == null || StringUtils.isBlank(req.getUser().getUserErp())) {
                res.setCode(JdCResponse.CODE_FAIL);
                res.setMessage("请求人Erp不能为空");
                return res;
            }
            if(req.getTaskId() == null) {
                res.setCode(JdCResponse.CODE_FAIL);
                res.setMessage("请求任务ID不能为空");
                return res;
            }
            if(req.getCreateSiteCode() == null) {
                res.setCode(JdCResponse.CODE_FAIL);
                res.setMessage("当前操作场地ID不能为空");
                return res;
            }

            return loadScanService.getInspectNoSendNoLoadWaybillDetail(req);

        }catch (Exception e) {
            log.error("GoodsLoadScanGatewayServiceImpl.getInspectNoSendNoLoadWaybillDetail---error---parameter=【{}】", JsonHelper.toJson(req), e);
            res.setCode(JdCResponse.CODE_ERROR);
            res.setMessage("操作失败");
            return res;
        }
    }


}
