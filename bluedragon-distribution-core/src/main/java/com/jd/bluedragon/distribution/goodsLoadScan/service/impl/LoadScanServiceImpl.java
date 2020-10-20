package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsDetailDto;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.lang.StringUtils;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.LoadScanPackageDetailService;
import com.jd.ql.dms.report.domain.LoadScanDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("LoadScanService")
public class LoadScanServiceImpl implements LoadScanService {
    private final static Logger log = LoggerFactory.getLogger(LoadScanServiceImpl.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private LoadCarDao loadCarDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Resource
    private LoadScanPackageDetailService loadScanPackageDetailService;

    @Autowired
    private GoodsLoadScanDao goodsLoadScanDao;

    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private BoardCombinationService boardCombinationService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;


    @Autowired
    private DepartureService departureService;

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        JdCResponse response = new JdCResponse();

        SendBizSourceEnum bizSource = SendBizSourceEnum.ANDROID_PDA_LOAD_SEND;
        SendM domain = new SendM();
        domain.setReceiveSiteCode(req.getReceiveSiteCode());
        domain.setCreateSiteCode(req.getCurrentOperate().getSiteCode());
        domain.setSendCode(req.getSendCode());
        domain.setCreateUser(req.getUser().getUserName());
        domain.setCreateUserCode(req.getUser().getUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(GoodsLoadScanConstants.YN_Y);
        domain.setCreateTime(new Date());
        domain.setOperateTime(new Date());

        log.info("LoadScanServiceImpl#goodsLoadingDeliver--begin 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");
        deliveryService.packageSend(bizSource, domain);
        log.info("LoadScanServiceImpl#goodsLoadingDeliver--end 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");

        LoadCar loadCar = new LoadCar();
        loadCar.setId(req.getTaskId());
        loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END);
        boolean flagRes = loadCarDao.updateLoadCarById(loadCar);
        if(flagRes == false) {
            log.info("LoadScanServiceImpl#goodsLoadingDeliver--发货完成后修改任务状态失败，发货信息【" + JsonHelper.toJson(domain) + "】");
            response.toSucceed("发货状态修改失败");
            return response;
        }
        log.info("LoadScanServiceImpl#goodsLoadingDeliver--发货成功，发货信息【" + JsonHelper.toJson(domain) + "】");
        response.toSucceed("发货成功");
        return response;
    }

    @Override
    public Integer findTaskStatus(Long taskId) {

        LoadCar lc = loadCarDao.findLoadCarById(taskId);
        log.info("LoadScanServiceImpl#findTaskStatus--根据任务号【" + JsonHelper.toJson(taskId) + "】查询任务信息,出参【" + JsonHelper.toJson(lc) +"】");

        if(lc != null && lc.getStatus() != null) {
            return lc.getStatus();
        }
        return null;
    }

    @Override
    public GoodsLoadScan queryByWaybillCodeAndTaskId(Long taskId, String waybillCode) {
        String key = taskId + "_" + waybillCode;
        return jimdbCacheService.get(key, GoodsLoadScan.class);
    }

    @Override
    public boolean updateGoodsLoadScanAmount(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        String lockKey = goodsLoadScan.getTaskId().toString();
        try{
            if(!jimdbCacheService.setNx(lockKey, "1",2, TimeUnit.SECONDS)){
                Thread.sleep(100);
                boolean res = jimdbCacheService.setNx(lockKey,StringUtils.EMPTY,2, TimeUnit.SECONDS);
                if(res != true) {
                    log.info("装车发货扫描计算已安装、未安装数据写库操作lock失败，任务号【" + lockKey + "】");
                    return false;
                }
            }

            boolean res = true;
            //取消扫描
            if(goodsLoadScanRecord.getScanAction() == GoodsLoadScanConstants.GOODS_SCAN_REMOVE) {
                res = this.scanRemove(goodsLoadScan, goodsLoadScanRecord, currentSiteCode) ;
                if(!res) {
                    log.info("updateGoodsLoadScanAmount--取消扫描发货失败，包裹信息【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                    return false;
                }
            }else if(goodsLoadScanRecord.getScanAction() == GoodsLoadScanConstants.GOODS_SCAN_LOAD) { //装车扫描
                res = this.scanLoad(goodsLoadScan, goodsLoadScanRecord, currentSiteCode) ;
                log.info("updateGoodsLoadScanAmount--发货扫描失败，包裹信息【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                return false;
            }else {
                log.info("updateGoodsLoadScanAmount--包裹扫描状态错误，请输入1(发货扫描)或0(取消扫描)，包裹信息【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                return false;
            }

            if(!res) {
                log.info("updateGoodsLoadScanAmount--包裹扫描失败，包裹信息，包裹信息【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                return false;
            }

            return res;
        }catch (Exception e){
            log.error("装车发货扫描计算已安装、未安装数据写库操作lock异常",e);
            return false;
        }finally {
            jimdbCacheService.del(lockKey);
        }
    }

    private boolean scanRemove(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        String waybillCode = goodsLoadScan.getWayBillCode();
        Long taskId = goodsLoadScan.getTaskId();


        // 根据运单号和包裹号查询已验未发的唯一一条记录
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);
        Integer createSiteId = currentSiteCode;
        LoadScanDto scanDto = getLoadScanListByWaybillCode(scanDtoList, createSiteId).get(0);
        Integer goodsAmount = scanDto.getGoodsAmount();//ES中拉的最新库存

        GoodsLoadScan glcTemp = this.queryByWaybillCodeAndTaskId(taskId, waybillCode);
        //缓存失效查库
        if(glcTemp == null) {
            glcTemp = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);//这里上游查询确认库中不为空才会走到这里
        }
        Integer loadAmount = glcTemp.getLoadAmount();//缓存中取出已装货数量


        goodsLoadScan.setLoadAmount(loadAmount - 1);
        goodsLoadScan.setUnloadAmount(goodsAmount - loadAmount);
        if(loadAmount == 0) {//  当前已装为0时，取消发货后已装为0，不属于不齐异常，变更状态
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
        }else if(loadAmount == goodsAmount) {//已装等于库存时，说明已经扫描完成，绿色状态，取消一个改为红色状态
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);
        }
//删除
        log.info("ExceptionScanServiceImpl#scanRemove 取消发货扫描运单表修改 --begin--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");
        boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
        if(!scNum) {
            log.info("ExceptionScanServiceImpl#scanRemove 装车扫描运单表 --error--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");
            return false;
        }
        log.info("ExceptionScanServiceImpl#scanRemove 装车扫描运单表 --end--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");

        log.info("ExceptionScanServiceImpl#scanRemove 取消发货扫描包裹表修改--begin--，参数【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
        int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
        if(num <= 0) {
            log.info("ExceptionScanServiceImpl#scanRemove 取消发货扫描包裹表修改--error--，参数【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
            return false;
        }
        log.info("ExceptionScanServiceImpl#scanRemove 取消发货扫描包裹表修改--end--，参数【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
//清缓存
        String waybillCacheKey = taskId + "_" + waybillCode;
        if(!jimdbCacheService.del(waybillCacheKey)) {
            log.info("ExceptionScanServiceImpl#scanRemove 取消发货取消运单缓存失败--error--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");
            return false;
        }

        String packageCacheKey = taskId + "_" + waybillCode + "_" + goodsLoadScanRecord.getPackageCode();
        if(!jimdbCacheService.del(packageCacheKey)) {
            log.info("ExceptionScanServiceImpl#scanRemove 取消发货取消包裹缓存失败--error--，参数【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
            return false;
        }

        return true;
    }

    private boolean scanLoad(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        Long taskId = goodsLoadScan.getTaskId();
        String waybillCode = goodsLoadScan.getWayBillCode();

        String packageCacheKey = taskId + "_" + waybillCode + "_" + goodsLoadScanRecord.getPackageCode();
        String waybillCacheKey = taskId + "_" + waybillCode;

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);
        Integer createSiteId = currentSiteCode;
        //查ES拿最新库存
        LoadScanDto scanDto = getLoadScanListByWaybillCode(scanDtoList, createSiteId).get(0);

        Integer goodsAmount = scanDto.getGoodsAmount();//ES中拉的最新库存

        GoodsLoadScan glcTemp = this.queryByWaybillCodeAndTaskId(taskId, waybillCode);
        boolean exist = false;
        //缓存失效查库
        if(glcTemp == null) {
            glcTemp = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
            if(glcTemp == null) {
                //写库+ 写缓存
                goodsLoadScan.setLoadAmount(1);//首次添加已装1
                goodsLoadScan.setUnloadAmount(goodsAmount - 1);
                goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);//开始装车后为红色不齐状态
//写库
                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --begin--，入参【"+ JsonHelper.toJson(goodsLoadScan) + "】");
                boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
                if(!scNum) {
                    log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --error--，入参【"+ JsonHelper.toJson(goodsLoadScan) + "】");
                    return false;
                }
                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --end--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");

                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--begin--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
                int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
                if(num <= 0) {
                    log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--error--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
                    return false;
                }
                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--end--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
//写缓存
                jimdbCacheService.setEx(waybillCacheKey, goodsLoadScan, 1, TimeUnit.DAYS);
                jimdbCacheService.setEx(packageCacheKey, goodsLoadScanRecord, 1, TimeUnit.DAYS);

            }else {
                exist = true;
            }
        }

        //缓存中查到  或者 库中查到两个动作 （1）更改数据库 （2）缓存没有失效更改缓存，缓存失效重新插入缓存--这里直接删除缓存重新添加
        if(exist) {

            Integer loadAmount = glcTemp.getLoadAmount() + 1;//已装数量
            Integer unLoadAmount = goodsAmount-loadAmount;//未装数量
            if(unLoadAmount == 0){//装完时 运单颜色状态为绿色已装扫描完成
                goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
            }
            goodsLoadScan.setLoadAmount(loadAmount);
            goodsLoadScan.setUnloadAmount(unLoadAmount);

//写库
            log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --begin--，入参【"+ JsonHelper.toJson(goodsLoadScan) + "】");
            boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
            if(!scNum) {
                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --error--，入参【"+ JsonHelper.toJson(goodsLoadScan) + "】");
                return false;
            }
            log.info("ExceptionScanServiceImpl#scanLoad 装车扫描运单表 --end--，参数【"+ JsonHelper.toJson(goodsLoadScan) + "】");


            log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--begin--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
            int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
            if(num <= 0) {
                log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--error--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
                return false;
            }
            log.info("ExceptionScanServiceImpl#scanLoad 装车扫描修改包裹记录表--end--，入参【"+ JsonHelper.toJson(goodsLoadScanRecord) + "】");
//缓存:先删除在写入
            jimdbCacheService.del(waybillCacheKey);
            jimdbCacheService.del(packageCacheKey);

            jimdbCacheService.setEx(waybillCacheKey, goodsLoadScan, 1, TimeUnit.DAYS);
            jimdbCacheService.setEx(packageCacheKey, goodsLoadScanRecord, 1, TimeUnit.DAYS);

        }
        return true;
    }

    @Override
    public JdCResponse<Map<String, Object>> goodsLoadingScan(GoodsLoadingScanningReq req) {
        JdCResponse<Map<String, Object>> response = new JdCResponse<>();
        Long taskId = req.getTaskId();
        // 根据任务号查找当前任务所在网点和下一网点
        LoadCar loadCar = loadCarDao.findLoadCarById(taskId);
        if (loadCar == null) {
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        Integer createSiteId = loadCar.getCreateSiteCode().intValue();
        Integer nextSiteId = loadCar.getEndSiteCode().intValue();
        // 根据任务号查找装车扫描明细暂存表
        List<GoodsLoadScan> tempList = goodsLoadScanDao.findLoadScanByTaskId(taskId);

        List<LoadScanDto> reportList;

        // 如果暂存表不为空，则去分拣报表拉取最新的库存数据
        if (!tempList.isEmpty()) {
            reportList = getLoadScanByWaybillCodes(getWaybillCodes(tempList), createSiteId, nextSiteId, null);
        } else {
            // 如果暂存表为空，则去分拣报表拉取100条数据
            reportList = getLoadScanByWaybillCodes(null, createSiteId, nextSiteId, 100);
        }

        List<GoodsDetailDto> goodsDetailDtoList;

        // 分拣报表查询返回为空
        if (reportList.isEmpty()) {
            log.error("根据任务ID所在的当前网点和下一网点去分拣报表没有找到相应的运单记录或发生错误，taskId={},currentSiteCode={},endSiteCode={}",
                    taskId, loadCar.getCreateSiteCode(), loadCar.getEndSiteCode());
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务ID没有找到相应的运单记录");
            return response;
        }
        // 如果当前任务装车明细还没初始化，直接返回
        goodsDetailDtoList = transformData(reportList);
        // 按照颜色排序
        Collections.sort(goodsDetailDtoList, new Comparator<GoodsDetailDto>() {
            @Override
            public int compare(GoodsDetailDto o1, GoodsDetailDto o2) {
                // status：0无特殊颜色,1绿色,2橙色,3黄色,4红色
                return o2.getStatus().compareTo(o1.getStatus());
            }
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("batchCode", loadCar.getBatchCode());
        resultMap.put("loadScan", loadCar.getBatchCode());

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setData(resultMap);

        return response;
    }

    @Override
    public JdCResponse<Void> saveLoadScanByBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();
        // 多扫标识
        Integer flowDisAccord = req.getFlowDisaccord();
        User user = req.getUser();

        // 根据包裹号查板号
        Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
        if (board == null) {
            log.error("根据包裹号没有找到对应的板号！taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号没有找到对应的板号");
            return response;
        }
        String boardCode = board.getCode();
        Response<List<String>> result = groupBoardManager.getBoxesByBoardCode(boardCode);
        if (result == null || result.getCode() != ResponseEnum.SUCCESS.getIndex()
                || CollectionUtils.isEmpty(result.getData())) {
            log.error("根据板号没有找到对应的包裹列表！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号没有找到对应的包裹列表");
            return response;
        }

        // 扫描第一个包裹时，修改任务状态为已开始
        updateTaskStatus(loadCar, user);

        // 根据板号判断是否是重复扫
        GoodsLoadScanRecord loadScanRecord = goodsLoadScanRecordDao
                .findLoadScanRecordByTaskIdAndBoardCode(taskId, boardCode);
        if (loadScanRecord != null) {
            // 重复扫直接跳过
            log.error("该板号属于重复扫！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该板号属于重复扫！");
            return response;
        }

        // 循环处理板上的每一个包裹
        for (String packCode : result.getData()) {
            if (!WaybillUtil.isPackageCode(packCode)) {
                continue;
            }
            String waybillCode = WaybillUtil.getWaybillCode(packCode);

            // 板上的包裹列表不需要再校验是否已验货，直接装车
            // 根据包裹号和运单号去分拣报表查找已验未发对应的记录
            // todo 批量   处理完再批量查
            LoadScanDto loadScanDto = new LoadScanDto();
                   // getLoadScanByWaybillCodeAndPackageCode(waybillCode, packCode);
            if (loadScanDto != null) {
                // 保存扫描记录
                GoodsLoadScanRecord goodsLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packCode,
                        boardCode, transfer, flowDisAccord, user);
                goodsLoadScanRecordDao.insert(goodsLoadScanRecord);
                // 根据任务ID和运单号查询暂存表
                // todo 循环包裹，在锁里 根据缓存 进行计算
                GoodsLoadScan loadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
                if (loadScan != null) {
                    // 修改已存在的记录
                    loadScan.setLoadAmount(loadScan.getLoadAmount() + 1);
                    loadScan.setUpdateTime(new Date());

                    loadScan.setUpdateUserCode(user.getUserCode());
                    loadScan.setUpdateUserName(user.getUserName());

                    // 设置运单颜色状态
                    setWaybillStatus(loadScan);
                    goodsLoadScanDao.updateByPrimaryKey(loadScan);
                }
            }
        }
        return response;
    }

    @Override
    public JdCResponse<Void> checkInspectAndSave(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();
        // 是否多扫标识
        Integer flowDisAccord = req.getFlowDisaccord();
        User user = req.getUser();

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
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        Integer createSiteId = loadCar.getCreateSiteCode().intValue();
        List<LoadScanDto> loadScanDto = getLoadScanListByWaybillCode(scanDtoList, createSiteId);
        if (loadScanDto.isEmpty()) {
            log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        LoadScanDto scanDto = loadScanDto.get(0);
        // todo inspectionCountByWaybill 这两个接口区别
        // 查看包裹是否已验货
        Inspection inspection = new Inspection();
        inspection.setCreateSiteCode(createSiteId);
        inspection.setPackageBarcode(packageCode);
        boolean isInspected = inspectionService.haveInspection(inspection);

        // 未操作验货
        // 此类包裹，页面弹出提示：“此包裹未操作验货，无法扫描，请先操作验货”
        if (!isInspected) {
            log.warn("[多扫逻辑校验]|此包裹未操作验货，无法扫描，请先操作验货taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("此包裹未操作验货，无法扫描，请先操作验货");
            return response;
        }

        // 校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        // todo 复用长宇的逻辑，周一提供


        // 校验通过，暂存
        Integer goodsAmount = scanDto.getGoodsAmount();

        // 扫描第一个包裹时，修改任务状态为已开始
        updateTaskStatus(loadCar, user);

        return saveLoadScanByPackCode(taskId, waybillCode, packageCode, goodsAmount, transfer, flowDisAccord, user);
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
     *校验包裹号
     */
    @Override
    public JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {

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
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        loadScan.setPackageCode(packageCode);
        loadScan.setCreateSiteId(loadCar.getCreateSiteCode().intValue());
        LoadScanDto loadScanDto = getLoadScanByWaybillAndPackageCode(loadScan);
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
            response.setCode(JdCResponse.CODE_CONFIRM);
            JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
            msgBox.setMsg("错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！");
            msgBox.setType(MsgBoxTypeEnum.CONFIRM);
            response.addBox(msgBox);
            return response;
        }

        return response;
    }

    /**
     *校验板号
     */
    @Override
    public JdVerifyResponse<Void> checkBoardCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {

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
        // 根据包裹号查板号
        Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
        if (board == null) {
            log.error("根据包裹号没有找到对应的板号！taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号没有找到对应的板号");
            return response;
        }

        String boardCode = board.getCode();
        log.info("获取板信息成功，taskId={},packageCode={},boardCode:{}", taskId, packageCode, boardCode);

        // 校验板号流向与批次号流向是否一致，如不一致进行错发弹框提醒（“错发！请核实！板号与批次目的地不一致，请确认是否继续发货！”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的板号
        if (loadCar.getEndSiteCode().intValue() != board.getDestinationId()) {
            log.warn("错发！请核实！板号与批次目的地不一致，请确认是否继续发货！taskId={},packageCode={},boardCode={},taskSite={}, boardSite={}",
                    taskId, packageCode, boardCode, loadCar.getEndSiteCode(), board.getDestinationId());
            response.setCode(JdCResponse.CODE_CONFIRM);
            JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
            msgBox.setMsg("错发！请核实！板号与批次目的地不一致，请确认是否继续发货！");
            msgBox.setType(MsgBoxTypeEnum.CONFIRM);
            response.addBox(msgBox);
            return response;
        }

        response.setCode(JdCResponse.CODE_SUCCESS);
        return response;
    }

    /**
     * 校验批次号并绑定任务
     */
    @Override
    public JdVerifyResponse<Void> checkBatchCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {

        Long taskId = req.getTaskId();
        String batchCode = req.getBatchCode();
        User user = req.getUser();

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

        // 校验批次是否已封车
        ServiceMessage<Boolean> result = departureService.checkSendStatusFromVOS(batchCode);
        if (!ServiceResultEnum.SUCCESS.equals(result.getResult())) {
            log.warn("该批次号已经封车，不可绑定任务！，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该批次号已经封车，不可绑定任务！");
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

        loadCar.setOperateUserErp(user.getUserErp());
        loadCar.setOperateUserName(user.getUserName());

        loadCar.setUpdateTime(new Date());
        loadCarDao.updateLoadCarById(loadCar);
        response.setCode(JdCResponse.CODE_SUCCESS);

        return response;
    }

    /**
     * 将包裹进行暂存
     * @param taskId 任务ID
     * @param waybillCode 运单号
     * @param packageCode 包裹号
     * @param goodsAmount 库存数
     * @param flowDisAccord 多扫标识
     * @param transfer 包裹号转板号标识
     */
    private JdCResponse<Void> saveLoadScanByPackCode(Long taskId, String waybillCode, String packageCode,
                                                     Integer goodsAmount, Integer transfer, Integer flowDisAccord, User user) {

        JdCResponse<Void> response = new JdCResponse<>();

        // 校验重复扫
        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setTaskId(taskId);
        record.setWayBillCode(waybillCode);
        record.setPackageCode(packageCode);
        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        record.setYn(Constants.YN_YES);
        GoodsLoadScanRecord loadScanRecord = goodsLoadScanRecordDao.findLoadScanRecordByTaskIdAndWaybillCodeAndPackCode(record);
        // 如果是重复扫，返回错误
        if (loadScanRecord != null) {
            log.warn("该包裹号已扫描装车，请勿重复扫描！taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_SUCCESS);
            response.setMessage("该包裹号已扫描装车，请勿重复扫描！");
            return response;
        }

        // 如果不是重复扫，包裹扫描记录表新增一条记录
        GoodsLoadScanRecord newLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packageCode,
                null, transfer, flowDisAccord, user);
        goodsLoadScanRecordDao.insert(newLoadScanRecord);

        // 查看暂存表是否初始化过此运单
        GoodsLoadScan goodsLoadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
        if (goodsLoadScan == null) {
            // 运单暂存表新增
            GoodsLoadScan newLoadScan = createGoodsLoadScan(taskId, waybillCode, packageCode,
                    goodsAmount, flowDisAccord, user);
            goodsLoadScanDao.insert(newLoadScan);
            response.setCode(JdCResponse.CODE_SUCCESS);
            return response;
        }

        // 如果已初始化，直接修改
        computeAndUpdateLoadScan(goodsLoadScan, goodsAmount);

        response.setCode(JdCResponse.CODE_SUCCESS);
        return response;
    }

    /**
     * 计算已装、未装并更新装车扫描运单明细
     * @param loadScan  装车扫描运单明细记录
     * @param goodsAmount 库存
     */
    private void computeAndUpdateLoadScan(GoodsLoadScan loadScan, Integer goodsAmount) {
        // 计算已装、未装
        // 已装 + 1
        loadScan.setLoadAmount(loadScan.getLoadAmount() + 1);
        // 未装 ：库存 - 已装
        loadScan.setUnloadAmount(goodsAmount -loadScan.getLoadAmount());
        // 计算单子状态
        setWaybillStatus(loadScan);
        goodsLoadScanDao.updateByPrimaryKey(loadScan);
        // todo redis锁   任务号+运单号   重试一次  失败大日志
    }


    /**
     * 根据包裹号和运单号获取装车扫描运单明细
     * @param scanDtoList  查询条件列表
     * @param currentSiteId 当前网点id
     * @return 运单
     */
    public List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId) {
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity = loadScanPackageDetailService
                .findLoadScanList(scanDtoList, currentSiteId);
        if (baseEntity == null) {
            //log.warn("根据运单号和包裹号去分拣报表查询运单明细接口返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            return null;
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
            //log.error("根据运单号和包裹号去分拣报表查询运单明细接口失败packageCode={},waybillCode={},code={}",
                   // packageCode, waybillCode, baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    /**
     * 根据运单号获取装车扫描列表
     * @param waybillCodes  查询条件列表
     * @param currentSiteId 当前网点id
     * @return 运单
     */
    public List<LoadScanDto> getLoadScanByWaybillCodes(List<String> waybillCodes, Integer currentSiteId,
                                                        Integer nextSiteId, Integer rows) {
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity = loadScanPackageDetailService
                .findLoadScanPackageDetail(waybillCodes, currentSiteId, nextSiteId, rows);
        if (baseEntity == null) {
            // log.warn("根据运单号和包裹号去分拣报表查询运单明细接口返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            return null;
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
//            log.error("根据运单号和包裹号去分拣报表查询运单明细接口失败packageCode={},waybillCode={},code={}",
//                    packageCode, waybillCode, baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    /**
     * 根据运单号获取装车扫描列表
     * @param loadScanDto  查询条件列表
     * @return 运单
     */
    public LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto) {
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<LoadScanDto> baseEntity = loadScanPackageDetailService
                .findLoadScan(loadScanDto);
        if (baseEntity == null) {
            // log.warn("根据运单号和包裹号去分拣报表查询运单明细接口返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            return null;
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
//            log.error("根据运单号和包裹号去分拣报表查询运单明细接口失败packageCode={},waybillCode={},code={}",
//                    packageCode, waybillCode, baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    private  List<GoodsDetailDto> transformData(List<LoadScanDto> list) {
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        for (LoadScanDto detailDto : list) {
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

    private GoodsLoadScanRecord createGoodsLoadScanRecord(Long taskId, String waybillCode, String packageCode,
                                                          String boardCode, Integer transfer, Integer flowDisAccord,
                                                          User user) {
        GoodsLoadScanRecord loadScanRecord = new GoodsLoadScanRecord();
        loadScanRecord.setTaskId(taskId);
        loadScanRecord.setWayBillCode(waybillCode);
        loadScanRecord.setPackageCode(packageCode);
        loadScanRecord.setBoardCode(boardCode);
        // 装车动作
        loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        // 包裹号转板号标识
        loadScanRecord.setTransfer(transfer == null ? 0 : transfer);
        // 多扫标识
        loadScanRecord.setFlowDisaccord(flowDisAccord == null ? 0 : flowDisAccord);

        loadScanRecord.setCreateUserCode(user.getUserCode());
        loadScanRecord.setCreateUserName(user.getUserName());
        loadScanRecord.setUpdateUserCode(user.getUserCode());
        loadScanRecord.setUpdateUserName(user.getUserName());

        loadScanRecord.setCreateTime(new Date());
        loadScanRecord.setUpdateTime(new Date());
        loadScanRecord.setYn(Constants.YN_YES);
        return loadScanRecord;
    }

    private GoodsLoadScan createGoodsLoadScan(Long taskId, String waybillCode, String packageCode,
                                              Integer goodsAmount, Integer flowDisAccord, User user) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        goodsLoadScan.setLoadAmount(1);
        // 未装：库存包裹数 – 装车已扫包裹数
        goodsLoadScan.setUnloadAmount(goodsAmount - goodsLoadScan.getLoadAmount());
        goodsLoadScan.setForceAmount(0);
        // 运单状态颜色: 已装和未装都大于0，默认红色
        goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);
        //  如果是多扫包裹，底色标位黄色
        if (GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y.equals(flowDisAccord)) {
            log.info("【运单号不在任务列表内的，且此运单本场地已操作验货】|此类包裹为多扫包裹，正常记录的统计表中，"
                    + "底色标位黄色taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
        }

        goodsLoadScan.setCreateUserCode(user.getUserCode());
        goodsLoadScan.setCreateUserName(user.getUserName());
        goodsLoadScan.setUpdateUserCode(user.getUserCode());
        goodsLoadScan.setUpdateUserName(user.getUserName());

        goodsLoadScan.setCreateTime(new Date());
        goodsLoadScan.setUpdateTime(new Date());
        goodsLoadScan.setYn(Constants.YN_YES);
        return goodsLoadScan;
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

    private List<String> getWaybillCodes(List<GoodsLoadScan> scans) {
        List<String> list = new ArrayList<>();
        for (GoodsLoadScan scan : scans) {
            list.add(scan.getWayBillCode());
        }
        return list;
    }

    /**
     * 修改任务状态
     * @param loadCar 任务
     */
    private void updateTaskStatus(LoadCar loadCar, User user) {
        // 扫描第一个包裹时，将任务状态改为已开始
        List<String> waybillCodeList = goodsLoadScanDao.findWaybillCodesByTaskId(loadCar.getId());
        if (waybillCodeList.isEmpty()) {
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN);
            loadCar.setOperateUserErp(user.getUserErp());
            loadCar.setOperateUserName(user.getUserName());
            loadCar.setUpdateTime(new Date());
            loadCarDao.updateLoadCarById(loadCar);
        }
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

    /**
     * 根据包裹号查板号信息
     * @param siteCode 当前站点信息
     * @param packageCode 包裹号
     * @return 板信息
     */
    private Board getBoardCodeByPackageCode(Integer siteCode, String packageCode) {

        Response<Board> tcResponse = boardCombinationService.getBoardByBoxCode(siteCode, packageCode);

        if (tcResponse == null) {
            log.error("根据包裹号查询板号返回结果为空！packageCode={},siteCode={}", packageCode, siteCode);
            return null;
        }
        if (JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())) {
            //查询成功
            return tcResponse.getData();
        }
        log.error("根据包裹号查询板号发生错误！packageCode={},siteCode={},error={}", packageCode, siteCode, tcResponse.getMesseage());
        return null;
    }
}
