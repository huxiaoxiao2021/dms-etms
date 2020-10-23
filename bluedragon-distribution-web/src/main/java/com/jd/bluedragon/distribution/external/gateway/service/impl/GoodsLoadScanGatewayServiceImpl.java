package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.annotation.Resource;
import java.util.*;

public class GoodsLoadScanGatewayServiceImpl implements GoodsLoadScanGatewayService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ExceptionScanService exceptionScanService;

    @Resource
    private LoadScanService loadScanService;

    @Autowired
    private LoadService loadService;



    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsRemoveScanning",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsRemoveScanning(GoodsExceptionScanningReq req) {
        /*
            1: 先根据包裹号，去暂存记录表里查询该包裹是否存在  不存在未多扫   查询结果中含该包裹运单号
            2： 存在该包裹，去修改下该包裹扫描取消的动作
            2：在通过该包裹运单号，去暂存表中修改该包裹对应运单
         */
        JdCResponse response = new JdCResponse<Boolean>();

        if(req.getTaskId() == null){
            response.toFail("任务号不能为空");
            return response;
        }


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
//        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货查询任务号- 参数【" + JsonHelper.toJson(req) + "】");
        ExceptionScanDto exceptionScanDto = exceptionScanService.findExceptionGoodsScan(record);//入参 包裹号  包裹状态=1 yn

        if(exceptionScanDto == null) {
            response.toFail("此包裹号未操作装车，无法取消");
            return response;
        }else if(exceptionScanDto.getForceStatus() == GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_Y){
            response.toFail("此包裹已被操作强发，无法取消");
            return response;
        }


        exceptionScanDto.setOperator(req.getUser().getUserName());
        exceptionScanDto.setOperatorCode(req.getUser().getUserCode());
        exceptionScanDto.setCurrentSiteCode(req.getCurrentOperate().getSiteCode());
        exceptionScanDto.setCurrentSiteName(req.getCurrentOperate().getSiteName());
//        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货更改不齐异常数据，参数【" + JsonHelper.toJson(exceptionScanDto) + "】");
        boolean removeRes =  exceptionScanService.removeGoodsScan(exceptionScanDto);

        if(!removeRes) {
            response.toError("取消包裹扫描失败");
        }
        response.toSucceed("取消包裹扫描成功");
        return response;

    }


    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsCompulsoryDeliver",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {

        JdCResponse response = new JdCResponse<Boolean>();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
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
        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.findExceptionGoodsLoading",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req) {
        JdCResponse<List<GoodsExceptionScanningDto>> response = new JdCResponse<>();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
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
        if(list == null || list.size() <= 0) {
            response.toError("不齐异常数据查找失败");
            return response;
        }
        response.toSucceed("不齐异常数据查找成功");
        response.setData(list);
        return response;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsLoadingDeliver",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> goodsLoadingDeliver(GoodsLoadingReq req) {

        JdCResponse response = new JdCResponse();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
            return response;
        }

        if(jimdbCacheService.get(req.getTaskId().toString()) != null) {
            response.toFail("任务发货重复提交");
            return response;
        }else {
            //防止PDA-1用户在发货页面停留过久，期间PDA-2用户操作了发货，此时发货状态已经改变为已完成，PDA不能再进行发货动作
            Integer taskStatus = loadScanService.findTaskStatus(req.getTaskId());
            if(taskStatus == null) {
                response.toFail("该任务存在异常,无法发货");
                return response;
            }else if(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(taskStatus)) {
                response.toFail("该任务已经完成发货，请勿重复发货");
                return response;
            } else if(!GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN.equals(taskStatus)){
                response.toFail("未开始任务无法进行发货发货");
                return response;
            }
        }

        jimdbCacheService.setEx(req.getTaskId().toString(), 1, 60);
//        jimdbCacheService.setEx(req.getTaskId().toString(), 1, 1000);

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

        return loadScanService.goodsLoadingDeliver(req);
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.goodsLoadingScan",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<LoadScanDetailDto> goodsLoadingScan(GoodsLoadingScanningReq req) {
        
        log.info("根据任务ID查找装车扫描列表，taskId={}", req.getTaskId());

        return loadScanService.goodsLoadingScan(req);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsLoadScanGatewayServiceImpl.checkBatchCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req) {

        log.info("根据任务ID和批次号开始检验：taskId={},batchCode={}", req.getTaskId(), req.getBatchCode());

        JdCResponse<Void> response = new JdCResponse<>();

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

        log.info("根据任务ID和包裹号开始检验：taskId={},packageCode={},transfer={}", req.getTaskId(), req.getPackageCode(), req.getTransfer());

        JdVerifyResponse<Void> response = new JdVerifyResponse<>();

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

        log.info("开始调用暂存接口--判断任务是否已经结束：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }
        log.info("开始调用暂存接口--任务合法：taskId={},packageCode={},transfer={}", taskId, packageCode, transfer);

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











}
