package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsExceptionScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.ExceptionScanDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.goodsLoadScan.service.ExceptionScanService;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GoodsLoadingScanningServiceImpl implements GoodsLoadingScanningService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExceptionScanService exceptionScanService;

    @Autowired
    private GoodsLoadScanDao goodsLoadScanDao;

    @Autowired
    private GoodsScanRecordDao goodsScanRecordDao;

    @Autowired
    private LoadScanPackageDetailService loadScanPackageDetailService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private ErpUserClient erpUserClient;

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
        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货查询任务号【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
        ExceptionScanDto exceptionScanDto = exceptionScanService.findExceptionGoodsScan(record);//入参 包裹号  包裹状态=1 yn

        if(exceptionScanDto == null) {
            response.toFail("此包裹号未操作装车，无法取消");
            return response;
        }

        log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消发货更改不齐异常数据，任务号【"+ exceptionScanDto.getTaskId() +"】-运单号【" + exceptionScanDto.getPackageCode() +"】");
        boolean removeRes =  exceptionScanService.removeGoodsScan(exceptionScanDto);

        if(removeRes == true) {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描成功【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
            response.toSucceed("取消包裹扫描成功");
        } else {
            log.info("GoodsLoadingScanningServiceImpl#goodsRemoveScanning- 取消包裹扫描失败【"+ req.getTaskId() +"】-包裹号【" + req.getPackageCode() +"】是否被扫描装车");
            response.toError("取消包裹扫描失败");
        }
        return response;

    }


    @Override
    public JdCResponse goodsCompulsoryDeliver(GoodsExceptionScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse<List<GoodsExceptionScanningDto>> findExceptionGoodsLoading(GoodsExceptionScanningReq req) {
        return null;
    }

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        return null;
    }

    @Override
    public JdCResponse<List<GoodsDetailDto>> goodsLoadingScan(GoodsLoadingScanningReq req) {
        // 根据任务号查找装车扫描表

        // 如果如果此任务尚未初始化
        // 根据任务号查找当前任务所在网点和下一网点

        // 从es查
        List<GoodsDetailDto> detailList = loadScanPackageDetailService.findLoadScanPackageDetail();
        // 保存到装车扫描表

        // 返回列表给端上
        return null;
    }

    @Override
    public JdCResponse checkByBatchCodeOrBoardCodeOrPackageCode(GoodsLoadingScanningReq req) {
        // 如果是批次号
        // 系统校验批次号的目的地与输入的目的场地是否一致，如果不一致则系统提示：“批次号目的地与目的场地不一致，请检查后重新扫描”

        //批次号校验通过，则和任务绑定

        // 多扫逻辑校验

        // 7.1当扫描包裹号发货时，校验包裹下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        //
        //7.2 当扫描包裹号发货时，校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        //
        //7.3 当扫描包裹号发货时，此包裹号未操作验货，则提示“未验货，请先操作验货”
        //
        //7.4当扫描板号/包裹号转化为板号进行发货时，校验板号流向与批次号流向是否一致，如不一致进行错发弹框提醒（“错发！请核实！板号与批次目的地不一致，请确认是否继续发货！”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的板号

        // 校验通过后，计算已装、未装

        return null;
    }

    @Override
    public JdCResponse saveByPackageCode(GoodsLoadingScanningReq req) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 根据包裹号查找运单号
        BaseEntity<Waybill> baseEntity = waybillQueryManager.getWaybillByPackCode(req.getPackageCode());
        if(baseEntity == null){
            log.warn("查询运单信息接口返回空waybillCode[{}]", baseEntity);
            return null;
        }
        if(baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null){
            log.warn("查询运单信息接口失败waybillCode[{}]code[{}]", baseEntity, baseEntity.getResultCode());
            return null;
        }
        Waybill waybill = baseEntity.getData();
        String waybillCode = waybill.getWaybillCode();
        // todo 是库存还是总包裹
        Integer goodsAmount = waybill.getGoodNumber();

        // 根据任务号和运单号查找包裹是否属于当前批次
        GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);

        // 如果不属于当前批次，为当前任务添加一条运单
        if (loadScan == null) {
            GoodsLoadScan goodsLoadScan = createGoodsLoadScan(taskId, waybillCode, packageCode, goodsAmount);
            goodsLoadScanDao.insert(goodsLoadScan);
            // 装车记录表新增一条记录 todo
            GoodsLoadScanRecord loadScanRecord = new GoodsLoadScanRecord();
            goodsScanRecordDao.insert(loadScanRecord);
        } else {
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

        return null;
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
        // '运单状态颜色：此时 多扫 3黄色
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


}
