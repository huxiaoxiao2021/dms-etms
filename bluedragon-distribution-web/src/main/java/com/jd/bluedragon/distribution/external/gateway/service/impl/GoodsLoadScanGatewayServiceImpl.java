package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
                if(req.getCurrentOperate().getSiteName() == null) {
                    response.toFail("发货单位名称不能为空");
                    return response;
                }
            }

            if(exceptionScanService.checkException(req.getTaskId())) {
                response.toFail("本次装车存在不齐运单，请点击不齐异常处理操作");
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
        if (req == null || req.getTaskId() == null) {
            log.warn("扫描批次号--任务ID为空");
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

        // 如果没勾选【包裹号转板号】
        if (req.getTransfer() == null || req.getTransfer() != 1) {
            log.info("根据任务ID和包裹号开始检验,常规包裹号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
            // 校验包裹号
            return loadScanService.checkPackageCode(req, response);
        }
        log.info("根据任务ID和包裹号开始检验,包裹号转板号：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());

        // 如果勾选【包裹号转板号】
        // 校验板号
        return loadScanService.checkBoardCode(req, response);
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

        if (log.isDebugEnabled()) {
            log.debug("开始调用暂存接口--判断任务是否已经结束：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);
        }
        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }
        if (log.isDebugEnabled()) {
            log.debug("开始调用暂存接口--任务合法：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);
        }
        // 如果没有勾选【包裹号转板号】
        if (transfer == null || transfer != 1) {
            log.info("开始调用暂存接口--没有勾选【包裹号转板号】：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);
            // 校验是否已验货,并暂存包裹号
            return loadScanService.checkInspectAndSave(req, response, loadCar);
        }

        log.info("开始调用暂存接口--勾选【包裹号转板号】：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);

        // 勾选【包裹号转板号】
        return loadScanService.saveLoadScanByBoardCode(req, response, loadCar);
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


}
