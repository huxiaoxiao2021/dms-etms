package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.LoadScanPackageDetailService;
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

        if(req.getWaybillCode() == null || req.getWaybillCode().size() == 0 ){
            response.toFail("运单号不能为空");
            return response;
        }

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


        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JdCResponse<List<GoodsDetailDto>> goodsLoadingScan(GoodsLoadingScanningReq req) {
        JdCResponse<List<GoodsDetailDto>> response = new JdCResponse<>();
        response.setCode(JdCResponse.CODE_SUCCESS);
        Long taskId = req.getTaskId();
        // 根据任务号查找装车扫描表
        List<GoodsLoadScan> scanList = goodsLoadScanDao.findLoadScanByTaskId(taskId);
        // 如果已初始化，直接返回
        if (!scanList.isEmpty()) {
            List<GoodsDetailDto> details = transformDtoToEntity(scanList);
            response.setData(details);
            return response;
        }
        // 如果如果此任务尚未初始化
        // 根据任务号查找当前任务所在网点和下一网点
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        // 从es查
        com.jd.ql.dms.report.domain.BaseEntity<List<com.jd.ql.dms.report.domain.GoodsDetailDto>> detailList = loadScanPackageDetailService
                .findLoadScanPackageDetail(loadCar.getCurrentSiteCode().intValue(), loadCar.getEndSiteCode().intValue());
        if (detailList.getCode() == Constants.SUCCESS_CODE) {
            List<com.jd.ql.dms.report.domain.GoodsDetailDto> list = detailList.getData();
            if (!list.isEmpty()) {
                // 组装数据
                Map<String, Object> map = transformEntityToDto(list, taskId);
                // 保存到装车扫描表
                goodsLoadScanDao.batchInsert((List<GoodsLoadScan>) map.get("insert"));
                // 返回列表给端上
                response.setData((List<GoodsDetailDto>) map.get("return"));
                return response;
            }
        }

        // 没有数据的情况
        response.setMessage("根据任务ID没有找到对应的数据");
        response.setData(null);
        return response;
    }

    @Override
    public JdCResponse<Void> checkByBatchCodeOrBoardCodeOrPackageCode(GoodsLoadingScanningReq req) {
        JdCResponse<Void> response = new JdCResponse<>();

        // 如果批次号不为空
        if (StringUtils.isNotBlank(req.getBatchCode())) {
            return checkBatchCode(req, response);
        }

        // 如果是包裹号
        // 如果没勾选【包裹号转板号】
        if (req.getTransfer() == null || req.getTransfer() != 1) {
            return checkPackageCode(req, response);
        }

        // 如果勾选【包裹号转板号】
        // 校验板号
        return checkBoardCode(req, response);
    }

    /**
     *校验板号
     */
    private JdCResponse<Void> checkBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        // 当扫描板号/包裹号转化为板号进行发货时，校验板号流向与批次号流向是否一致，如不一致进行错发弹框提醒（“错发！请核实！板号与批次目的地不一致，请确认是否继续发货！”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的板号
        // todo

        return response;
    }

    /**
     *校验包裹号
     */
    private JdCResponse<Void> checkPackageCode(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        Integer transfer = req.getTransfer();
        // 根据包裹号查找运单号
        Waybill waybill = getWaybillByPackageCode(packageCode);
        if (waybill == null) {
            log.warn("根据包裹号查询运单信息返回空packageCode[{}]", packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        String waybillCode = waybill.getWaybillCode();
        // todo 库存还是总包裹？
        Integer goodsAmount = waybill.getGoodNumber();

        // todo inspectionCountByWaybill 这两个接口区别
        // 查看包裹是否已验货
        Inspection inspection = new Inspection();
        inspection.setPackageBarcode(packageCode);
        boolean isInspected = inspectionService.haveInspection(inspection);

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);

        // 多扫逻辑校验
        GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);

        // 1.运单号在任务列表内的，且此运单本场地未操作验货
        // 此类包裹，页面弹出提示：“此包裹未操作验货，无法扫描，请先操作验货”
        if (loadScan != null && !isInspected) {
            log.warn("[多扫逻辑校验]|此包裹未操作验货，无法扫描，请先操作验货packageCode[{}]", packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("此包裹未操作验货，无法扫描，请先操作验货");
            return response;
        }
        if (loadScan == null) {
            // 2.运单号不在任务列表内的，且此运单本场地未操作验货
            // 此类包裹，页面弹出提示：“此包裹未操作验货，无法扫描，请先操作验货”
            if (!isInspected) {
                log.warn("【多扫逻辑校验】-【运单号不在任务列表内，且此运单本场地未操作验货】|此包裹未操作验货，无法扫描，请先操作验货packageCode[{}]", packageCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("此包裹未操作验货，无法扫描，请先操作验货");
            } else {
                //  3.运单号不在任务列表内的，且此运单本场地已操作验货
                //  此类包裹未多扫包裹，正常记录的统计表中，底色标位黄色
                log.info("【多扫逻辑校验】-【运单号不在任务列表内的，且此运单本场地已操作验货】|此类包裹未多扫包裹，正常记录的统计表中，底色标位黄色packageCode[{}]", req.getPackageCode());
                saveExternalWaybill(taskId, waybillCode, packageCode, goodsAmount, transfer);
                // 扫描第一个包裹时，将任务状态改为已开始
                loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN);
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                if (erpUser != null) {
                    loadCar.setOperateUserErp(erpUser.getUserCode());
                    loadCar.setOperateUserName(erpUser.getUserName());
                }
                loadCar.setOperateTime(new Date());
                loadCarDao.updateLoadCarById(loadCar);
                response.setCode(JdCResponse.CODE_SUCCESS);
            }
            return response;
        }

        // 发货校验
        // 1.校验包裹下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        // todo 如果先扫包裹号，这时还没有批次号

        // 2.当扫描包裹号发货时，校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能

        // 全部校验通过后，计算已装、未装并更新
        computeAndUpdateLoadScan(loadScan, packageCode);
        return response;
    }

    /**
     * 校验批次号并绑定任务
     */
    private JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        Long taskId = req.getTaskId();
        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        // 如果是批次号 todo 批次号是否可以多次更改覆盖
        String batchCode = req.getBatchCode();
        CreateAndReceiveSiteInfo siteInfo = siteService.getCreateAndReceiveSiteBySendCode(batchCode);

        if (siteInfo != null) {
            // 系统校验批次号的目的地与输入的目的场地是否一致，如果不一致则系统提示：“批次号目的地与目的场地不一致，请检查后重新扫描”
            if (!siteInfo.getReceiveSiteCode().equals(loadCar.getEndSiteCode().intValue())) {
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
        response.setCode(JdCResponse.CODE_FAIL);
        response.setMessage("根据批次号找不到对应的目的地信息");
        return response;
    }

    /**
     * 计算已装、未装并更新装车扫描运单明细
     * @param loadScan  装车扫描运单明细记录
     * @param packageCode 包裹号
     */
    private void computeAndUpdateLoadScan(GoodsLoadScan loadScan, String packageCode) {
        // 计算已装、未装
        String packageCodes = loadScan.getPackageCodes();
        // 如果要装车的包裹号在库存中
        if (StringUtils.isNotBlank(packageCodes) && packageCodes.contains(packageCode)) {
            // 已装 + 1
            loadScan.setLoadAmount(loadScan.getLoadAmount() + 1);
            // 计算单子状态
            setWaybillStatus(loadScan);
            goodsLoadScanDao.updateByPrimaryKey(loadScan);
        }
    }

    /**
     * 保存不属于本任务的运单
     * @param taskId 任务ID
     * @param waybillCode 运单号
     * @param packageCode 包裹号
     * @param goodsAmount 库存
     * @param transfer 包裹号转板号标识
     */
    private void saveExternalWaybill(Long taskId, String waybillCode, String packageCode,
                                     Integer goodsAmount, Integer transfer) {
        GoodsLoadScan goodsLoadScan = createGoodsLoadScan(taskId, waybillCode, packageCode, goodsAmount);
        goodsLoadScanDao.insert(goodsLoadScan);
        // 装车记录表新增一条记录 todo
        GoodsLoadScanRecord loadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packageCode,
                goodsAmount, transfer);
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
        if(baseEntity == null){
            log.warn("根据包裹号查询运单信息接口返回空packageCode[{}]", packageCode);
            return null;
        }
        if(baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null){
            log.error("查询运单信息接口失败packageCode[{}]code[{}]", packageCode, baseEntity.getResultCode());
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
        // 已装等于库存，未装=0 -- 已扫描完 -- 绿色 todo 待确认
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
        // 根据包裹号查找运单号
        Waybill waybill = getWaybillByPackageCode(req.getPackageCode());
        if (waybill == null) {
            log.warn("根据包裹号查询运单信息返回空packageCode[{}]", req.getPackageCode());
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        String waybillCode = waybill.getWaybillCode();
        // todo 是库存还是总包裹
        Integer goodsAmount = waybill.getGoodNumber();

        // 根据任务号和运单号查找包裹是否属于当前批次
        GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);

        // 如果不属于当前批次，为当前任务添加一条运单
        if (loadScan == null) {
            saveExternalWaybill(taskId, waybillCode, packageCode, goodsAmount, req.getTransfer());
        } else {
            computeAndUpdateLoadScan(loadScan, packageCode);
        }
        return response;
    }
    private GoodsLoadScan createGoodsLoadScan(Long taskId, String waybillCode, String packageCode, Integer goodsAmount) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        goodsLoadScan.setPackageCodes(packageCode);
        // todo 是库存还是总包裹
        goodsLoadScan.setPackageAmount(goodsAmount);
        goodsLoadScan.setGoodsAmount(1);
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
        }
        goodsLoadScan.setCreateTime(new Date());
        goodsLoadScan.setYn(Constants.YN_YES);
        return goodsLoadScan;
    }

    private GoodsLoadScanRecord createGoodsLoadScanRecord(Long taskId, String waybillCode, String packageCode,
                                                          Integer goodsAmount, Integer transfer) {
        GoodsLoadScanRecord loadScanRecord = new GoodsLoadScanRecord();
        loadScanRecord.setTaskId(taskId);
        loadScanRecord.setWayBillCode(waybillCode);
        loadScanRecord.setPackageCode(packageCode);
        // todo 是库存还是总包裹
        loadScanRecord.setPackageAmount(1);
        // 装车动作
        loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        loadScanRecord.setTransfer(transfer);
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser != null) {
            loadScanRecord.setCreateUserCode(erpUser.getUserCode());
            loadScanRecord.setCreateUserName(erpUser.getUserName());
        }
        loadScanRecord.setCreateTime(new Date());
        loadScanRecord.setYn(Constants.YN_YES);
        return loadScanRecord;
    }

    private Map<String, Object> transformEntityToDto(List<com.jd.ql.dms.report.domain.GoodsDetailDto> list, Long taskId) {
        Map<String, Object> map = new HashMap<>(16);
        List<GoodsLoadScan> scanList = new ArrayList<>();
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        Date date = new Date();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        for (com.jd.ql.dms.report.domain.GoodsDetailDto detailDto : list) {
            // 组装入库的对象
            GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
            goodsLoadScan.setTaskId(taskId);
            goodsLoadScan.setWayBillCode(detailDto.getWayBillCode());
            goodsLoadScan.setPackageCodes(detailDto.getPackageCodes());
            goodsLoadScan.setPackageAmount(detailDto.getPackageAmount());
            goodsLoadScan.setGoodsAmount(detailDto.getGoodsAmount());
            goodsLoadScan.setLoadAmount(0);
            goodsLoadScan.setUnloadAmount(0);
            goodsLoadScan.setForceAmount(0);
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            goodsLoadScan.setYn(Constants.YN_YES);
            goodsLoadScan.setCreateTime(date);
            if (erpUser != null) {
                goodsLoadScan.setCreateUserCode(erpUser.getUserCode());
                goodsLoadScan.setCreateUserName(erpUser.getUserName());
            }
            scanList.add(goodsLoadScan);
            // 组装给端上返回的数据
            GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
            goodsDetailDto.setWayBillCode(detailDto.getWayBillCode());
            goodsDetailDto.setPackageAmount(detailDto.getPackageAmount());
            goodsDetailDto.setGoodsAmount(detailDto.getGoodsAmount());
            goodsDetailDto.setLoadAmount(0);
            goodsDetailDto.setUnloadAmount(0);
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            goodsDetails.add(goodsDetailDto);
        }
        map.put("insert", scanList);
        map.put("return", goodsDetails);
        return map;
    }

    private List<GoodsDetailDto> transformDtoToEntity(List<GoodsLoadScan> loadScans) {
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        for (GoodsLoadScan loadScan : loadScans) {
            GoodsDetailDto goodsDetail = new GoodsDetailDto();
            goodsDetail.setWayBillCode(loadScan.getWayBillCode());
            goodsDetail.setPackageAmount(loadScan.getPackageAmount());
            goodsDetail.setGoodsAmount(loadScan.getGoodsAmount());
            goodsDetail.setLoadAmount(loadScan.getLoadAmount());
            goodsDetail.setUnloadAmount(loadScan.getUnloadAmount());
            goodsDetail.setStatus(loadScan.getStatus());
            goodsDetails.add(goodsDetail);
        }
        return goodsDetails;
    }
}
