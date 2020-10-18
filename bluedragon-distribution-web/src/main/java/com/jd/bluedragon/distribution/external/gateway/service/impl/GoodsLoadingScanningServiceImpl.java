package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.NoRepeatSubmitService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.LoadScanPackageDetailService;
import com.jd.ql.dms.report.domain.LoadScanDetailDto;
import com.jd.ql.dms.report.domain.LoadScanDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExceptionScanService exceptionScanService;

    @Autowired
    private GoodsLoadScanDao goodsLoadScanDao;

    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    private LoadScanPackageDetailService loadScanPackageDetailService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private LoadCarDao loadCarDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private NoRepeatSubmitService noRepeatSubmitService;

    @Autowired
    private LoadScanService loadScanService;

    @Override
    public JdCResponse goodsRemoveScanning(GoodsExceptionScanningReq req) {
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

//        if(req.getWaybillCode() == null || req.getWaybillCode().size() == 0 ){
//            response.toFail("运单号不能为空");
//            return response;
//        }

        if(StringUtils.isBlank(req.getPackageCode())){
            response.toFail("包裹号不能为空");
            return response;
        }

        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setTaskId(req.getTaskId());
        record.setPackageCode(req.getPackageCode());
        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货查询任务号- 参数【" + JsonHelper.toJson(req) + "】");
        ExceptionScanDto exceptionScanDto = exceptionScanService.findExceptionGoodsScan(record);//入参 包裹号  包裹状态=1 yn

        if(exceptionScanDto == null) {
            response.toFail("此包裹号未操作装车，无法取消");
            return response;
        }

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning 取消发货当前操作人【" + JsonHelper.toJson(erpUser) + "】");

        if (erpUser != null) {
            exceptionScanDto.setOperator(erpUser.getUserName());
            exceptionScanDto.setOperatorCode(erpUser.getUserCode());
        }

        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货更改不齐异常数据，参数【" + JsonHelper.toJson(exceptionScanDto) + "】");
        boolean removeRes =  exceptionScanService.removeGoodsScan(exceptionScanDto);

        if(removeRes == true) {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描成功,【" + JsonHelper.toJson(exceptionScanDto) + "】");
            response.toSucceed("取消包裹扫描成功");
        } else {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描失败,【" + JsonHelper.toJson(exceptionScanDto) + "】");
            response.toError("取消包裹扫描失败");
        }
        return response;

    }


    @Override
    public JdCResponse goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {

        JdCResponse response = new JdCResponse<Boolean>();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
            return response;
        }

        if(req.getWaybillCode() == null || req.getWaybillCode().size() <=0) {
            response.toFail("运单号不能为空");
            return response;
        }

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        log.info("GoodsLoadingScanningServiceImpl#goodsCompulsoryDeliver 强发当前操作人【" + JsonHelper.toJson(erpUser) + "】");
        if (erpUser != null) {
            req.setOperator(erpUser.getUserName());
            req.setOperatorCode(erpUser.getUserCode());
        }

        log.info("GoodsLoadingScanningServiceImpl#goodsCompulsoryDeliver-强制下发--begin:入参【" + JsonHelper.toJson(req) + "】");
        boolean res = exceptionScanService.goodsCompulsoryDeliver(req);

        if(res != true) {
            response.toError("强制下发失败");
            return response;
        }

        response.toSucceed("强制下发成功");
        return null;
    }

    @Override
    public JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req) {
        JdCResponse<List<GoodsExceptionScanningDto>> response = new JdCResponse<>();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
            return response;
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
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {

        JdCResponse response = new JdCResponse();

        if(req.getTaskId() == null) {
            response.toFail("任务号不能为空");
            return response;
        }
        //防重复提交，返回true表示可以提交，false表示已经提交过，缓存中未过期，不允许再重复提交
        if(noRepeatSubmitService.checkRepeatSubmit(req.getTaskId()) != 1L) {
            response.toFail("任务发货重复提交");
            return response;
        }

        //防止PDA-1用户在发货页面停留过久，期间PDA-2用户操作了发货，此时发货状态已经改变为已完成，PDA不能再进行发货动作
        Integer taskStatus = loadScanService.findTaskStatus(req.getTaskId());
        if(taskStatus == null) {
            response.toFail("该任务存在异常,无法发货");
            return response;
        }else if(taskStatus == GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END) {
            response.toFail("该任务已经完成发货，请勿重复发货");
            return response;
        }

        if(req.getCreateUser() == null) {
            response.toFail("操作人不能为空");
            return response;
        }

//        if(req.getCreateUserCode() == null) {
//            response.toFail("操作人编码不能为空");
//            return response;
//        }
//
//        if(req.getCreateSiteCode() == null) {
//            response.toFail("发货单位编码不能为空");
//            return response;
//        }

        if(req.getSendCode() == null) {
            response.toFail("发货批次号不能为空");
            return response;
        }

        if(req.getReceiveSiteCode() == null) {
            response.toFail("收货单位编码不能为空");
            return response;
        }

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        log.info("GoodsLoadingScanningServiceImpl#goodsLoadingDeliver 装车发货当前操作人【" + JsonHelper.toJson(erpUser) + "】");
        if (erpUser != null) {
            req.setOperator(erpUser.getUserName());
            req.setOperatorCode(erpUser.getUserCode());
        }

        return loadScanService.goodsLoadingDeliver(req);
    }


    @Override
    public JdCResponse<List<GoodsDetailDto>> goodsLoadingScan(GoodsLoadingScanningReq req) {

        JdCResponse<List<GoodsDetailDto>> response = new JdCResponse<>();

        Long taskId = req.getTaskId();
        // 根据任务号查找当前任务所在网点和下一网点
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        if (loadCar == null) {
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        // 根据任务号查找装车扫描明细暂存表
        List<GoodsLoadScan> tempList = goodsLoadScanDao.findLoadScanByTaskId(taskId);
        // 从分拣报表中根据当前网点和下一网点查询最新的已验未发的运单记录
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDetailDto>> detailList
                = loadScanPackageDetailService.findLoadScanPackageDetail(loadCar.getCurrentSiteCode().intValue(),
                loadCar.getEndSiteCode().intValue());

        List<GoodsDetailDto> goodsDetailDtoList;
        if (detailList.getCode() == Constants.SUCCESS_CODE) {
            List<LoadScanDetailDto> reportList = detailList.getData();
            // 分拣报表查询返回为空
            if (reportList.isEmpty()) {
                log.error("根据任务ID所在的当前网点和下一网点去分拣报表没有找到相应的运单记录，taskId={},currentSiteCode={},endSiteCode={}",
                        taskId, loadCar.getCurrentSiteCode(), loadCar.getEndSiteCode());
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据任务ID没有找到相应的运单记录");
                return response;
            }
            // 如果当前任务装车明细还没初始化，直接返回
            if (tempList.isEmpty()) {
                goodsDetailDtoList = transformData(reportList);
                response.setCode(JdCResponse.CODE_SUCCESS);
                response.setData(goodsDetailDtoList);
                return response;
            }
            // 如果已初始化，组装数据
            goodsDetailDtoList = new ArrayList<>();
            for (LoadScanDetailDto detailDto : reportList) {
                String reportWaybillCode = detailDto.getWayBillCode();
                GoodsDetailDto goodsDetailDto;
                for (GoodsLoadScan loadScan : tempList) {
                    String waybillCode = loadScan.getWayBillCode();
                    if (reportWaybillCode.equals(waybillCode)) {
                        // 库存取报表返回的最新数据
                        goodsDetailDto = createGoodsDetailDto(waybillCode, detailDto.getPackageAmount(),
                                detailDto.getGoodsAmount(), loadScan.getLoadAmount(), loadScan.getUnloadAmount(), loadScan.getStatus());
                        goodsDetailDtoList.add(goodsDetailDto);
                        break;
                    }
                }
                // 报表里最新增加的数据
                goodsDetailDto = createGoodsDetailDto(reportWaybillCode, detailDto.getPackageAmount(),
                        detailDto.getGoodsAmount(), detailDto.getLoadAmount(), detailDto.getUnloadAmount(),
                        GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
                goodsDetailDtoList.add(goodsDetailDto);
            }
            // 按照颜色排序
            Collections.sort(goodsDetailDtoList, new Comparator<GoodsDetailDto>() {
                @Override
                public int compare(GoodsDetailDto o1, GoodsDetailDto o2) {
                    // status：0无特殊颜色,1绿色,2橙色,3黄色,4红色
                    return o2.getStatus().compareTo(o1.getStatus());
                }
            });
            response.setCode(JdCResponse.CODE_SUCCESS);
            response.setData(goodsDetailDtoList);
            return response;
        }

        // 没有数据的情况
        log.error("根据任务ID所在的当前网点和下一网点没有找到相应的运单记录，taskId={},currentSiteCode={},endSiteCode={}",
                taskId, loadCar.getCurrentSiteCode(), loadCar.getEndSiteCode());
        response.setMessage("根据任务ID没有找到对应的数据");
        response.setCode(JdCResponse.CODE_FAIL);
        return response;
    }

    @Override
    public JdCResponse<Map<String, Object>> checkByBatchCodeOrBoardCodeOrPackageCode(GoodsLoadingScanningReq req) {

        JdCResponse<Map<String, Object>> response = new JdCResponse<>();

        // 如果批次号不为空，校验批次号
        if (StringUtils.isNotBlank(req.getBatchCode())) {
            return checkBatchCode(req, response);
        }

        // 如果是包裹号
        if (StringUtils.isNotBlank(req.getPackageCode())) {
            // 如果没勾选【包裹号转板号】
            if (req.getTransfer() == null || req.getTransfer() != 1) {
                // 校验包裹号
                return checkPackageCode(req, response);
            }
            // 如果勾选【包裹号转板号】
            // 校验板号
            return checkBoardCode(req, response);
        }

        // 其他情况就是参数错误
        response.setCode(JdCResponse.CODE_FAIL);
        response.setMessage("参数校验错误，请检查必填参数是否填写");

        return response;
    }

    /**
     *校验板号
     */
    private JdCResponse<Map<String, Object>> checkBoardCode(GoodsLoadingScanningReq req,
                                                            JdCResponse<Map<String, Object>> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        Map<String, Object> resultMap;

        // 根据包裹号查板号 todo 接口尚未确定
        String boardCode = "";
        Response<Board> tcResponse = groupBoardManager.getBoard(boardCode);
        if (tcResponse == null){
            log.error("根据板号获取板信息失败，taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号获取板信息失败");
            return response;
        }
        Board board = tcResponse.getData();
        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }
        if (tcResponse.getCode() == JdCResponse.CODE_SUCCESS && board != null){
            log.info("获取板信息成功，boardCode:{}", boardCode);
            // 校验板号流向与批次号流向是否一致，如不一致进行错发弹框提醒（“错发！请核实！板号与批次目的地不一致，请确认是否继续发货！”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的板号
            if (loadCar.getEndSiteCode().intValue() != board.getDestinationId()) {
                log.warn("错发！请核实！板号与批次目的地不一致，请确认是否继续发货！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
                response.setCode(JdCResponse.CODE_CONFIRM);
                response.setMessage("错发！请核实！板号与批次目的地不一致，请确认是否继续发货！");
                resultMap = new HashMap<>();
                resultMap.put("flowDisaccord", 1);
                response.setData(resultMap);
                return response;
            }
        }
        response.setCode(JdCResponse.CODE_SUCCESS);
        return response;
    }


    /**
     * 板号流向校验一致，保存板号上的包裹
     * @param req 板号入参
     * @param response 返回
     */
    private JdCResponse<Void> saveLoadScanByBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();

        // 根据包裹号查板号 todo 接口尚未确定
        String boardCode = "";
        Response<List<String>> result = groupBoardManager.getBoxesByBoardCode(boardCode);
        if (result == null || result.getCode() != ResponseEnum.SUCCESS.getIndex()
                || CollectionUtils.isEmpty(result.getData())) {
            log.error("根据板号没有找到对应的包裹列表！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号没有找到对应的包裹列表");
            return response;
        }
        for (String packCode : result.getData()) {
            if (!WaybillUtil.isPackageCode(packCode)) {
                continue;
            }
            String waybillCode = WaybillUtil.getWaybillCode(packCode);
            // checkPackageCommon()
        }
        return response;
    }

    /**
     *校验包裹号
     */
    private JdCResponse<Map<String, Object>> checkPackageCode(GoodsLoadingScanningReq req, JdCResponse<Map<String, Object>> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        Map<String, Object> resultMap;

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        // 根据包裹号查找运单
        Waybill waybill = getWaybillByPackageCode(packageCode);
        if (waybill == null) {
            log.error("根据包裹号查询运单信息返回空taskId={},packageCode[{}]", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        String waybillCode = waybill.getWaybillCode();

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        LoadScanDto loadScanDto = getLoadScanByWaybillCodeAndPackageCode(waybillCode, packageCode);
        if (loadScanDto == null) {
            log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }

        // 发货校验
        // 1.校验包裹下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        if (loadCar.getEndSiteCode().intValue() != loadScanDto.getNextSiteId()) {
            log.warn("包裹下一动态路由节点与批次号下一场站不一致taskId={},packageCode={},waybillCode={},packageNextSite={},taskEndSite={}",
                    taskId, packageCode, waybillCode, loadScanDto.getNextSiteId(), loadCar.getEndSiteCode());
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！");
            resultMap = new HashMap<>();
            resultMap.put("flowDisaccord", 1);
            response.setData(resultMap);
            return response;
        }

        return response;
    }

    /**
     * 校验包裹号的通用逻辑
     * @param req 包裹号入参
     * @param response 返回
     */
    private JdCResponse<Void> checkPackageCommon(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();
        // 是否多扫标识
        Integer flowDisAccord = req.getFlowDisaccord();

        // 根据包裹号查找运单
        Waybill waybill = getWaybillByPackageCode(packageCode);
        if (waybill == null) {
            log.error("根据包裹号查询运单信息返回空taskId={},packageCode[{}]", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        String waybillCode = waybill.getWaybillCode();

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        LoadScanDto loadScanDto = getLoadScanByWaybillCodeAndPackageCode(waybillCode, packageCode);
        if (loadScanDto == null) {
            log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }

        // todo inspectionCountByWaybill 这两个接口区别
        // 查看包裹是否已验货
        Inspection inspection = new Inspection();
        inspection.setPackageBarcode(packageCode);
        boolean isInspected = inspectionService.haveInspection(inspection);

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);

        // 多扫逻辑校验
        GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);

        // 未操作验货
        // 此类包裹，页面弹出提示：“此包裹未操作验货，无法扫描，请先操作验货”
        if (!isInspected) {
            log.warn("[多扫逻辑校验]|此包裹未操作验货，无法扫描，请先操作验货taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("此包裹未操作验货，无法扫描，请先操作验货");
            return response;
        }

        //  已操作验货
        //  此类包裹为多扫包裹，正常记录的统计表中，底色标位黄色
        log.info("【运单号不在任务列表内的，且此运单本场地已操作验货】|此类包裹为多扫包裹，正常记录的统计表中，"
                + "底色标位黄色taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
        saveExternalWaybill(taskId, waybillCode, packageCode, loadScanDto.getGoodsAmount(), transfer, flowDisAccord);

        // 扫描第一个包裹时，将任务状态改为已开始
        List<String> waybillCodeList = goodsLoadScanDao.findWaybillCodesByTaskId(taskId);
        if (waybillCodeList.isEmpty()) {
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN);
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                loadCar.setOperateUserErp(erpUser.getUserCode());
                loadCar.setOperateUserName(erpUser.getUserName());
            }
            loadCar.setOperateTime(new Date());
            loadCarDao.updateLoadCarById(loadCar);
        }
        response.setCode(JdCResponse.CODE_SUCCESS);
        return response;


        // 2.当扫描包裹号发货时，校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        // todo 复用长宇的逻辑，周一提供

        // 全部校验通过后，计算已装、未装并更新
        // computeAndUpdateLoadScan(loadScan, packageCode);
        // return response;
    }

    /**
     * 校验批次号并绑定任务
     */
    private JdCResponse<Map<String, Object>> checkBatchCode(GoodsLoadingScanningReq req, JdCResponse<Map<String, Object>> response) {

        Long taskId = req.getTaskId();
        String batchCode = req.getBatchCode();

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }
        // 如果批次号已绑定，直接返回
        if (StringUtils.isNotBlank(loadCar.getBatchCode())) {
            log.warn("该批次号已绑定此任务，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该批次号已绑定此任务");
            return response;
        }

        // 根据批次号查询下一网点信息
        CreateAndReceiveSiteInfo siteInfo = siteService.getCreateAndReceiveSiteBySendCode(batchCode);
        if (siteInfo == null) {
            log.warn("根据批次号没有找到对应的网点信息，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据批次号没有找到对应的网点信息");
            return response;
        }

        // 系统校验批次号的目的地与输入的目的场地是否一致，如果不一致则系统提示：“批次号目的地与目的场地不一致，请检查后重新扫描”
        if (!siteInfo.getReceiveSiteCode().equals(loadCar.getEndSiteCode().intValue())) {
            log.error("批次号目的地与目的场地不一致，请检查后重新扫描，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("批次号目的地与目的场地不一致，请检查后重新扫描");
            return response;
        }
        // 批次号校验通过，则和任务绑定
        loadCar.setBatchCode(batchCode);
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            loadCar.setOperateUserErp(erpUser.getUserCode());
            loadCar.setOperateUserName(erpUser.getUserName());
        }
        loadCar.setOperateTime(new Date());
        loadCarDao.updateLoadCarById(loadCar);
        response.setCode(JdCResponse.CODE_SUCCESS);

        return response;
    }

    /**
     * 计算已装、未装并更新装车扫描运单明细
     * @param loadScan  装车扫描运单明细记录
     * @param packageCode 包裹号
     */
    private void computeAndUpdateLoadScan(GoodsLoadScan loadScan, String packageCode) {
        // 计算已装、未装
        // 如果要装车的包裹号在库存中
        // 已装 + 1
        loadScan.setLoadAmount(loadScan.getLoadAmount() + 1);
        // 计算单子状态
        setWaybillStatus(loadScan);
        goodsLoadScanDao.updateByPrimaryKey(loadScan);
    }

    /**
     * 保存不属于本任务的运单
     * @param taskId 任务ID
     * @param waybillCode 运单号
     * @param packageCode 包裹号
     * @param goodsAmount 库存数
     * @param flowDisAccord 多扫标识
     * @param transfer 包裹号转板号标识
     */
    private void saveExternalWaybill(Long taskId, String waybillCode, String packageCode,
                                     Integer goodsAmount, Integer transfer, Integer flowDisAccord) {
        GoodsLoadScan goodsLoadScan = createGoodsLoadScan(taskId, waybillCode, goodsAmount);
        goodsLoadScanDao.insert(goodsLoadScan);
        // 装车记录表新增一条记录
        GoodsLoadScanRecord loadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packageCode,
                transfer, flowDisAccord);
        goodsLoadScanRecordDao.insert(loadScanRecord);
    }

    /**
     * 根据包裹号获取运单
     * @param packageCode  包裹号
     * @return 运单
     */
    private Waybill getWaybillByPackageCode(String packageCode) {
        // 根据包裹号查找运单号
        BaseEntity<Waybill> baseEntity = waybillQueryManager.getWaybillByPackCode(packageCode);
        if (baseEntity == null) {
            log.warn("根据包裹号查询运单信息接口返回空packageCode[{}]", packageCode);
            return null;
        }
        if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null) {
            log.error("查询运单信息接口失败packageCode[{}]code[{}]", packageCode, baseEntity.getResultCode());
            return null;
        }
        return baseEntity.getData();
    }

    /**
     * 根据包裹号和运单号获取装车扫描运单明细
     * @param packageCode  包裹号
     * @return 运单
     */
    private LoadScanDto getLoadScanByWaybillCodeAndPackageCode(String waybillCode, String packageCode) {
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<LoadScanDto> baseEntity = loadScanPackageDetailService
                .findLoadScan(waybillCode, packageCode);
        if (baseEntity == null) {
            log.warn("根据运单号和包裹号去分拣报表查询运单明细接口返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            return null;
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
            log.error("根据运单号和包裹号去分拣报表查询运单明细接口失败packageCode={},waybillCode={},code={}",
                    packageCode, waybillCode, baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    /**
     * 设置运单状态
     * @param goodsLoadScan 装车扫描运单记录
     */
    private void setWaybillStatus(GoodsLoadScan goodsLoadScan) {
        Integer goodsAmount = goodsLoadScan.getGoodsAmount();
        Integer loadAmount = goodsLoadScan.getLoadAmount();
        Integer unloadAmount = goodsLoadScan.getUnloadAmount();
        Integer forceAmount = goodsLoadScan.getForceAmount();
        // 已装和未装都大于0  -- 没扫齐 -- 红色
        if (loadAmount > 0 && unloadAmount > 0) {
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);
        }
        // 已装和未装都大于0，操作强发 -- 没扫齐强发 -- 橙色
        if (loadAmount > 0 && unloadAmount > 0 && forceAmount > 0) {
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
        }
        // 已装等于库存，未装=0 -- 已扫描完 -- 绿色
        if (goodsAmount.equals(loadAmount) && unloadAmount == 0) {
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
        }
        // 已装等于0且总包裹=库存 -- 货到齐没开始扫 或 扫完取消 -- 无特殊颜色
        // 已装等于0且总包裹≠库存 -- 货没到齐没开始扫 或 扫完取消 -- 无特殊颜色
        if (loadAmount == 0) {
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
        }
    }


    @Override
    public JdCResponse<Void> saveByPackageCode(GoodsLoadingScanningReq req) {

        JdCResponse<Void> response = new JdCResponse<>();
        response.setCode(JdCResponse.CODE_SUCCESS);
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();

        response = checkPackageCommon(req, response);

        // 根据包裹号查找运单号
        Waybill waybill = getWaybillByPackageCode(packageCode);
        if (waybill == null) {
            log.warn("根据包裹号查询运单信息返回空packageCode[{}]", packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        String waybillCode = waybill.getWaybillCode();

        LoadScanDto loadScanDto = getLoadScanByWaybillCodeAndPackageCode(waybillCode, packageCode);
        if (loadScanDto == null) {
            log.error("根据包裹号和运单号从分拣报表查询运单信息返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }

        // 根据任务号和运单号查找包裹是否属于当前批次
        GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);

        // 如果不属于当前批次，为当前任务添加一条运单
        if (loadScan == null) {
            // saveExternalWaybill(taskId, waybillCode, packageCode, loadScanDto.getGoodsAmount(), req.getTransfer());
        } else {
            // 如果属于校验当前批次，直接计算修改
            computeAndUpdateLoadScan(loadScan, packageCode);
        }
        return response;
    }

    private GoodsDetailDto createGoodsDetailDto(String waybillCode, Integer packageAmount, Integer goodsAmount,
                                                Integer loadAmount, Integer unloadAmount, Integer status) {
        GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
        goodsDetailDto.setWayBillCode(waybillCode);
        goodsDetailDto.setPackageAmount(packageAmount);
        goodsDetailDto.setGoodsAmount(goodsAmount);
        goodsDetailDto.setLoadAmount(loadAmount);
        goodsDetailDto.setUnloadAmount(unloadAmount);
        goodsDetailDto.setStatus(status);
        return goodsDetailDto;
    }

    private GoodsLoadScan createGoodsLoadScan(Long taskId, String waybillCode, Integer goodsAmount) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        goodsLoadScan.setLoadAmount(1);
        // 未装：库存包裹数 – 装车已扫包裹数
        goodsLoadScan.setUnloadAmount(goodsAmount - 1);
        goodsLoadScan.setForceAmount(0);
        // 运单状态颜色：此时 多扫 3黄色
        goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            goodsLoadScan.setCreateUserCode(erpUser.getUserCode());
            goodsLoadScan.setCreateUserName(erpUser.getUserName());
            goodsLoadScan.setUpdateUserCode(erpUser.getUserCode());
            goodsLoadScan.setUpdateUserName(erpUser.getUserName());
        }
        goodsLoadScan.setCreateTime(new Date());
        goodsLoadScan.setUpdateTime(new Date());
        goodsLoadScan.setYn(Constants.YN_YES);
        return goodsLoadScan;
    }

    private GoodsLoadScanRecord createGoodsLoadScanRecord(Long taskId, String waybillCode, String packageCode,
                                                          Integer transfer, Integer flowDisAccord) {
        GoodsLoadScanRecord loadScanRecord = new GoodsLoadScanRecord();
        loadScanRecord.setTaskId(taskId);
        loadScanRecord.setWayBillCode(waybillCode);
        loadScanRecord.setPackageCode(packageCode);
        // 装车动作
        loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        // 包裹号转板号标识
        loadScanRecord.setTransfer(transfer);
        // 多扫标识
        loadScanRecord.setFlowDisaccord(flowDisAccord == null ? 0 : flowDisAccord);
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            loadScanRecord.setCreateUserCode(erpUser.getUserCode());
            loadScanRecord.setCreateUserName(erpUser.getUserName());
            loadScanRecord.setUpdateUserCode(erpUser.getUserCode());
            loadScanRecord.setUpdateUserName(erpUser.getUserName());
        }
        loadScanRecord.setCreateTime(new Date());
        loadScanRecord.setUpdateTime(new Date());
        loadScanRecord.setYn(Constants.YN_YES);
        return loadScanRecord;
    }


    private  List<GoodsDetailDto> transformData(List<LoadScanDetailDto> list) {
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        for (LoadScanDetailDto detailDto : list) {
            GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
            goodsDetailDto.setWayBillCode(detailDto.getWayBillCode());
            goodsDetailDto.setPackageAmount(detailDto.getPackageAmount());
            goodsDetailDto.setGoodsAmount(detailDto.getGoodsAmount());
            goodsDetailDto.setLoadAmount(0);
            goodsDetailDto.setUnloadAmount(0);
            goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            goodsDetails.add(goodsDetailDto);
        }
        return goodsDetails;
    }



}
