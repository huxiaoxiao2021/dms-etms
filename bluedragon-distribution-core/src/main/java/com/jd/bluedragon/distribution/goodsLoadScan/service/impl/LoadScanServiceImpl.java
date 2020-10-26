package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.google.common.base.Stopwatch;
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
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanDao;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
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
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("loadScanService")
public class LoadScanServiceImpl implements LoadScanService {

    private final static Logger log = LoggerFactory.getLogger(LoadScanServiceImpl.class);

    private final static int LOCK_TIME = 60;

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

    @Autowired
    private UnloadCarService unloadCarService;

    @Resource
    private LoadScanCacheService loadScanCacheService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    public static final String LOADS_CAN_LOCK_BEGIN = "LOADS_CAN_LOCK_";


    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        JdCResponse response = new JdCResponse();
        //todo 取出包裹数据

        List<GoodsLoadScanRecord> list = new ArrayList<>();
        list = goodsLoadScanRecordDao.selectRecordByTaskId(req.getTaskId());

        for(GoodsLoadScanRecord glc : list) {
            loadDeliver(req, glc);
        }
        log.info("发货完成【{}】", JsonHelper.toJson(req));

        LoadCar loadCar = new LoadCar();
        loadCar.setId(req.getTaskId());
        loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END);
        boolean flagRes = loadCarDao.updateLoadCarById(loadCar);
        if (!flagRes) {
//            throw new GoodsLoadScanException("任务【" + req.getTaskId() + "】发货完成，任务状态修改失败");
            log.info("发货完成后修改任务状态失败，发货信息【{}】",JsonHelper.toJson(loadCar));
            response.toFail("发货状态修改失败");
            return response;
        }
        response.toSucceed("发货成功");
        return response;
    }

    //调用已有发货接口
    private void loadDeliver(GoodsLoadingReq req, GoodsLoadScanRecord glc) {
        SendBizSourceEnum bizSource = SendBizSourceEnum.ANDROID_PDA_LOAD_SEND;
        SendM domain = new SendM();
        domain.setReceiveSiteCode(req.getReceiveSiteCode());
        domain.setCreateSiteCode(req.getCurrentOperate().getSiteCode());
        domain.setSendCode(req.getSendCode());
        domain.setBoxCode(glc.getPackageCode());//包裹号
        domain.setCreateUser(req.getUser().getUserName());
        domain.setCreateUserCode(req.getUser().getUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(GoodsLoadScanConstants.YN_Y);
        domain.setCreateTime(new Date());
        domain.setOperateTime(new Date());

        log.info("装车完成发货--begin--参数【{}】", JsonHelper.toJson(domain));
        try {
            deliveryService.packageSend(bizSource, domain);
        }catch (GoodsLoadScanException e) {
            log.error("装车发货完成失败----error" + e);
            throw  new GoodsLoadScanException("装车发货完成失败");
        }
        log.info("装车完成发货--end--参数【{}】", JsonHelper.toJson(domain));

    }

    @Override
    public LoadCar findTaskStatus(Long taskId) {
        return loadCarDao.findLoadCarByTaskId(taskId);
    }

    @Override
    public boolean updateGoodsLoadScanAmount(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        boolean res = true;
//todo  加常量定义锁
        String lockKey = goodsLoadScan.getTaskId().toString();
        try {
            if (!loadScanCacheService.lock(lockKey, 1)) {
                Thread.sleep(100);
                boolean cacheResult = jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, 2, TimeUnit.SECONDS);
                if (!cacheResult) {
                    log.info("装车发货扫描计算已安装、未安装数据操作lock失败，任务号【{}】", lockKey);
                    return false;
                }
            }

            //取消扫描
            if (goodsLoadScanRecord.getScanAction() == GoodsLoadScanConstants.GOODS_SCAN_REMOVE) {
                res = this.scanRemove(goodsLoadScan, goodsLoadScanRecord, currentSiteCode);

            } else if (goodsLoadScanRecord.getScanAction() == GoodsLoadScanConstants.GOODS_SCAN_LOAD) { //装车扫描
                res = this.scanLoad(goodsLoadScan, goodsLoadScanRecord, currentSiteCode);

            } else {
                throw new GoodsLoadScanException("包裹扫描状态错误，状态值应该为1(发货扫描)或0(取消扫描)");
            }

            return res;
        } catch (Exception e) {
            log.error("出错了！"+e.getMessage(),e);
            throw new GoodsLoadScanException("装车发货扫描计算已安装、未安装数据写库操作lock异常, 运单信息【" + JsonHelper.toJson(goodsLoadScan) + "】");
        } finally {
            loadScanCacheService.unLock(lockKey);
        }
    }

    /**
     *
     * @param goodsLoadScan  运单维度信息
     * @param goodsLoadScanRecord  包裹维度信息
     * @param currentSiteCode   当前网点code
     * @return
     */
    private boolean scanRemove(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        //包裹信息
        String waybillCode = goodsLoadScan.getWayBillCode();
        Long taskId = goodsLoadScan.getTaskId();

        GoodsLoadScanRecord packageRecord  = loadScanCacheService.getWaybillLoadScanRecord(taskId, waybillCode, goodsLoadScanRecord.getPackageCode());
        if (packageRecord == null) {
            GoodsLoadScanRecord packageRecordQueryParam = new GoodsLoadScanRecord();
            packageRecordQueryParam.setPackageCode(goodsLoadScanRecord.getPackageCode());
            packageRecordQueryParam.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
            packageRecordQueryParam.setTaskId(goodsLoadScanRecord.getTaskId());
            packageRecordQueryParam.setWayBillCode(goodsLoadScanRecord.getWayBillCode());
            packageRecord = goodsLoadScanRecordDao.selectListByCondition(packageRecordQueryParam).get(0);
        }
        goodsLoadScanRecord.setId(packageRecord.getId());

        //包裹对应运单信息
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);
        //ES拉取最新缓存数
        List<LoadScanDto> loadScanDtoList= getLoadScanListByWaybillCode(scanDtoList, currentSiteCode);
        if(CollectionUtils.isEmpty(loadScanDtoList)){
            log.error("取消发货拉取ES库存失败， 入参【运单信息{} | 当前网点{} 】",scanDtoList , currentSiteCode);
            throw new GoodsLoadScanException("取消发货拉取ES库存为空");
        }

        LoadScanDto scanDto = loadScanDtoList.get(0);
        Integer goodsAmount = scanDto.getGoodsAmount();//ES中拉的最新库存
        GoodsLoadScan glcTemp = goodsLoadScanDao.findWaybillInfoByTaskIdAndWaybillCode(taskId, waybillCode);
//                loadScanCacheService.getWaybillLoadScan(taskId, waybillCode);
        //缓存失效查库
//        if (glcTemp == null) {
//            glcTemp = goodsLoadScanDao.findWaybillInfoByTaskIdAndWaybillCode(taskId, waybillCode);//这里上游查询确认库中不为空才会走到这里
//        }
        Integer loadAmount = glcTemp.getLoadAmount();//缓存中取出已装货数量

        goodsLoadScan.setId(glcTemp.getId());
        goodsLoadScan.setLoadAmount(loadAmount - 1);
        goodsLoadScan.setUnloadAmount(goodsAmount - goodsLoadScan.getLoadAmount());

        if (goodsLoadScan.getLoadAmount() == 0) {//  当前已装为1时，取消发货后已装为0，不属于不齐异常，变更状态
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
        } else if (goodsLoadScan.getUnloadAmount() > 0) {//已装等于库存时，说明已经扫描完成，绿色状态，取消一个改为红色状态
            Integer status = packageRecord.getFlowDisaccord();
            if(status == null) {
                log.error("取消包裹扫描时，查询该包裹【{}】流向状态flow_disaccord为null" , packageRecord);
                throw  new GoodsLoadScanException("取消包裹扫描时，查询该包裹流向状态为null");
            }
            if(status == GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y) {
                goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }else {
                goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);
            }
        }

        //执行逻辑删除
        goodRemoveOper(goodsLoadScanRecord, goodsLoadScan);

        // loadScanCacheService.setWaybillLoadScan(taskId, waybillCode, goodsLoadScan);
        loadScanCacheService.delWaybillLoadScanRecord(taskId, waybillCode, goodsLoadScanRecord.getPackageCode());

        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value = "main_undiv")
    public boolean goodRemoveOper(GoodsLoadScanRecord goodsLoadScanRecord, GoodsLoadScan goodsLoadScan) {

        if(log.isInfoEnabled()) {
            log.info("取消发货扫描包裹表修改--begin--，参数【{}】", JsonHelper.toJson(goodsLoadScanRecord));
        }
        int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
        if (num <= 0) {
            log.error("取消发货扫描包裹表修改失败，参数【{}】", goodsLoadScanRecord);
            throw new GoodsLoadScanException("取消发货扫描包裹表修改失败，参数【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
        } else if(log.isInfoEnabled()){
            log.info("取消发货扫描包裹表修改--begin--，参数【{}】", JsonHelper.toJson(goodsLoadScanRecord));
        }

        if(log.isInfoEnabled()) {
            log.info("取消发货扫描运单表修改 --begin--，参数【{}】", JsonHelper.toJson(goodsLoadScan));
        }
        boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
        if (!scNum) {
            log.error("取消发货扫描运单表修改失败,运单信息【{}】" , goodsLoadScan);
            throw new GoodsLoadScanException("取消发货扫描运单表修改失败,运单信息【" + JsonHelper.toJson(goodsLoadScan) + "】");
        }

        if(log.isInfoEnabled()) {
            log.info("取消发货扫描运单表修改 --success--，参数【{}】", JsonHelper.toJson(goodsLoadScan));
        }
        return num > 0 && scNum;

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
        List<LoadScanDto> list=getLoadScanListByWaybillCode(scanDtoList, createSiteId);
        if(CollectionUtils.isEmpty(list)){
            throw new GoodsLoadScanException("取消发货拉取ES库存为空");
        }
        LoadScanDto scanDto = list.get(0);

        Integer goodsAmount = scanDto.getGoodsAmount();//ES中拉的最新库存

//        GoodsLoadScan glcTemp = this.queryWaybillCache(taskId, waybillCode);
        GoodsLoadScan glcTemp = loadScanCacheService.getWaybillLoadScan(taskId, waybillCode);
        boolean exist = true;
        //缓存失效查库
        if (glcTemp == null) {
            glcTemp = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
            if (glcTemp == null) {
                exist = false;

                //写库+ 写缓存
                goodsLoadScan.setLoadAmount(1);//首次添加已装1
                Integer unLoadAmount = goodsAmount - 1;
                goodsLoadScan.setUnloadAmount(unLoadAmount);

                if (unLoadAmount == 0) {//未装为0 说明该包裹库存只有一个包裹，已经装完 运单颜色状态为绿色已装扫描完成
                    goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
                } else {
                    goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED);//开始装车后为红色不齐状态
                }
//写库
                log.info("装车扫描修改包裹记录表--begin--，入参【{}】", JsonHelper.toJson(goodsLoadScanRecord));
                int num = goodsLoadScanRecordDao.insert(goodsLoadScanRecord);
                if (num <= 0) {
                    throw new GoodsLoadScanException("装车扫描修改包裹记录表失败入参【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                }
                log.info("装车扫描修改包裹记录表--success--，入参【{}】", JsonHelper.toJson(goodsLoadScanRecord));

                log.info("装车扫描运单表 --begin--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
                boolean scNum = goodsLoadScanDao.insert(goodsLoadScan);
                if (!scNum) {
                    throw new GoodsLoadScanException("装车扫描运单表修改失败入参【" + JsonHelper.toJson(goodsLoadScan) + "】");
                }
                log.info("装车扫描运单表 --success--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
//写缓存
                jimdbCacheService.setEx(waybillCacheKey, goodsLoadScan, 1, TimeUnit.DAYS);
                jimdbCacheService.setEx(packageCacheKey, goodsLoadScanRecord, 1, TimeUnit.DAYS);

            }
        }
        //缓存中查到  或者 库中查到两个动作 （1）更改数据库 （2）缓存没有失效更改缓存，缓存失效重新插入缓存--这里直接删除缓存重新添加
        if (exist) {

            Integer loadAmount = glcTemp.getLoadAmount() + 1;//已装数量
            Integer unLoadAmount = goodsAmount - loadAmount;//未装数量
            if (unLoadAmount == 0) {//装完时 运单颜色状态为绿色已装扫描完成
                goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
            }
            goodsLoadScan.setLoadAmount(loadAmount);
            goodsLoadScan.setUnloadAmount(unLoadAmount);

//写库
            log.info("装车扫描运单表 --begin--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
            boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
            if (!scNum) {
                throw new GoodsLoadScanException("装车扫描运单表修改失败入参【" + JsonHelper.toJson(goodsLoadScan) + "】");
            }
            log.info("装车扫描运单表 --success--，入参【{}】", JsonHelper.toJson(goodsLoadScan));


            log.info("装车扫描修改包裹记录表--begin--，入参【{}】" + JsonHelper.toJson(goodsLoadScanRecord));
            int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
            if (num <= 0) {
                throw new GoodsLoadScanException("装车扫描修改包裹记录表失败入参【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
            }
            log.info("装车扫描修改包裹记录表--success--，入参【{}】" + JsonHelper.toJson(goodsLoadScanRecord));

            //缓存:先删除在写入
            jimdbCacheService.del(waybillCacheKey);
            jimdbCacheService.del(packageCacheKey);

            jimdbCacheService.setEx(waybillCacheKey, goodsLoadScan, 1, TimeUnit.DAYS);
            jimdbCacheService.setEx(packageCacheKey, goodsLoadScanRecord, 1, TimeUnit.DAYS);

        }
        return true;
    }


    @Override
    public JdCResponse<LoadScanDetailDto> goodsLoadingScan(GoodsLoadingScanningReq req) {
        JdCResponse<LoadScanDetailDto> response = new JdCResponse<>();
        Long taskId = req.getTaskId();
        // 根据任务号查找当前任务所在网点和下一网点
        LoadCar loadCar = loadCarDao.findLoadCarByTaskId(taskId);
        if (loadCar == null) {
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        log.info("开始查找暂存表--判断任务是否已经结束：taskId={}", taskId);

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={}", taskId);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }
        if (loadCar.getCreateSiteCode() == null || loadCar.getEndSiteCode() == null) {
            log.error("该装车任务网点ID为空taskId={}", taskId);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("装车任务网点ID为空");
            return response;
        }
        Integer createSiteId = loadCar.getCreateSiteCode().intValue();
        Integer nextSiteId = loadCar.getEndSiteCode().intValue();
        // 根据任务号查找装车扫描明细暂存表
        List<GoodsLoadScan> tempList = goodsLoadScanDao.findLoadScanByTaskId(taskId);
        //todo 删除无用info
        log.info("根据任务ID查找暂存表，taskId={}", req.getTaskId());
        List<LoadScanDto> reportList;
        List<GoodsDetailDto> goodsDetailDtoList = new ArrayList<>();
        // 暂存表运单号，运单号对应的暂存记录
        Map<String, GoodsLoadScan> map = new HashMap<>(16);

        // 如果暂存表不为空，则去分拣报表拉取最新的库存数据
        if (!tempList.isEmpty()) {
            // 记录属于多扫状态的运单
            Map<String, LoadScanDto> flowDisAccordMap = new HashMap<>(16);

            log.info("根据任务ID查找暂存表不为空，taskId={},size={}", req.getTaskId(), tempList.size());

            List<String> waybillCodeList = getWaybillCodes(tempList, map, flowDisAccordMap);
            reportList = getLoadScanByWaybillCodes(waybillCodeList, createSiteId, nextSiteId, null);

            List<LoadScanDto> flowDisAccordList = new ArrayList<>(flowDisAccordMap.values());

            log.info("根据暂存表记录反查分拣报表正常返回，taskId={},size={}", req.getTaskId(), reportList.size());

            // 该任务下多扫记录存在，因为多扫的运单流向不一致,需要单独查
            if (!flowDisAccordList.isEmpty()) {
                log.info("根据任务ID查找暂存表有多扫记录,开始从分拣报表查询多扫记录，taskId={},size={}", req.getTaskId(), flowDisAccordList.size());

                List<LoadScanDto> externalList = getLoadScanListByWaybillCode(flowDisAccordList, createSiteId);
                if (externalList == null || externalList.isEmpty()) {
                    log.info("根据暂存表该任务下的多扫记录反查分拣报表返回为空，taskId={}", req.getTaskId());
                    response.setCode(JdCResponse.CODE_FAIL);
                    response.setMessage("根据暂存表该任务下的多扫记录反查分拣报表返回为空");
                    return response;
                }
                log.info("根据任务ID查找暂存表有多扫记录,从分拣报表查询多扫记录正常返回，taskId={},size={}", req.getTaskId(), flowDisAccordList.size());
                reportList.addAll(externalList);
            }

            log.info("根据暂存表记录反查分拣报表结束，开始转换数据。taskId={}", req.getTaskId());
            goodsDetailDtoList = transformData(reportList, map, flowDisAccordMap);

            log.info("根据任务ID查找装车扫描记录结束,开始排序! taskId={},size={}", req.getTaskId(), goodsDetailDtoList.size());
            // 按照颜色排序
            Collections.sort(goodsDetailDtoList, new Comparator<GoodsDetailDto>() {
                @Override
                public int compare(GoodsDetailDto o1, GoodsDetailDto o2) {
                    // status：0无特殊颜色,1绿色,2橙色,3黄色,4红色
                    return o2.getStatus().compareTo(o1.getStatus());
                }
            });

        }
        LoadScanDetailDto scanDetailDto = new LoadScanDetailDto();
        scanDetailDto.setBatchCode(loadCar.getBatchCode());
        scanDetailDto.setGoodsDetailDtoList(goodsDetailDtoList);

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setData(scanDetailDto);

        return response;
    }

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    @Override
    public JdCResponse<Void> saveLoadScanByBoardCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();
        // 多扫标识
        Integer flowDisAccord = req.getFlowDisaccord();
        User user = req.getUser();

        log.info("板号暂存接口--根据包裹号找板号开始：taskId={},packageCode={},transfer={},flowDisAccord={}", taskId, packageCode, transfer, flowDisAccord);

        // 根据包裹号查板号
        Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
        if (board == null) {
            log.error("根据包裹号没有找到对应的板号！taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号没有找到对应的板号");
            return response;
        }
        log.info("板号暂存接口--根据包裹号找板号结束：taskId={},packageCode={},transfer={},flowDisAccord={}", taskId, packageCode, transfer, flowDisAccord);

        String boardCode = board.getCode();
        Response<List<String>> result = groupBoardManager.getBoxesByBoardCode(boardCode);
        if (result == null || result.getCode() != ResponseEnum.SUCCESS.getIndex()
                || CollectionUtils.isEmpty(result.getData())) {
            log.error("根据板号没有找到对应的包裹列表！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号没有找到对应的包裹列表");
            return response;
        }
        log.info("板号暂存接口--根据板号找板上的所有包裹结束：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                packageCode, transfer, flowDisAccord, boardCode);

        List<LoadScanDto> loadScanDtoList = new ArrayList<>();

        // 板子上可以装车的有效包裹
        List<GoodsLoadScanRecord> insertRecords = new ArrayList<>();
        List<GoodsLoadScanRecord> updateRecords = new ArrayList<>();

        try {
            // 获取锁
            if (!lock(taskId, null, boardCode)) {
                log.info("板号暂存接口--获取锁失败：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                        packageCode, transfer, flowDisAccord, boardCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("多人同时操作该包裹所在的板，请稍后重试！");
                return response;
            }

            // 校验该任务下运单数量是否已超过上限
            Integer waybillCount = goodsLoadScanDao.findWaybillCountByTaskId(taskId);
            if (waybillCount != null && waybillCount >= uccPropertyConfiguration.getLoadScanTaskWaybillSize()) {
                log.warn("该任务下运单数量已达上限！taskId={},packageCode={}", taskId, packageCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该任务下运单数量已达上限！");
                return response;
            }

            // 根据板号查询之前的包裹扫描记录
            Map<String, GoodsLoadScanRecord> packageMap = goodsLoadScanRecordDao.findRecordsByBoardCode(taskId, boardCode);


            // 运单，包裹数
            Map<String, Integer> map = new HashMap<>(16);

            /**Stopwatch耗时分析**/
            StopWatch watch=new StopWatch();
            watch.start();
            // 循环处理板上的每一个包裹
            for (String packCode : result.getData()) {
                if (!WaybillUtil.isPackageCode(packCode)) {
                    continue;
                }
                String waybillCode = WaybillUtil.getWaybillCode(packCode);

                // 该板号上的包裹都不属于重复扫
                if (packageMap != null && !packageMap.isEmpty()) {
                    GoodsLoadScanRecord record = packageMap.get(packCode);
                    // 重复扫的包裹忽略
                    if (record != null && GoodsLoadScanConstants.GOODS_SCAN_LOAD.equals(record.getScanAction())) {
                        continue;
                    }
                    // 扫描过但被取消的包裹可以再装
                    if (record != null && GoodsLoadScanConstants.GOODS_SCAN_REMOVE.equals(record.getScanAction())) {
                        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
                        record.setUpdateUserName(user.getUserName());
                        record.setUpdateUserCode(user.getUserCode());
                        record.setUpdateTime(new Date());
                        updateRecords.add(record);
                    }
                    // 没扫描过的包裹正常装
                    if (record == null) {
                        GoodsLoadScanRecord goodsLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packCode,
                                boardCode, transfer, flowDisAccord, user, loadCar);
                        insertRecords.add(goodsLoadScanRecord);
                    }
                } else {
                    // 没扫描过的包裹正常装
                    GoodsLoadScanRecord goodsLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packCode,
                            boardCode, transfer, flowDisAccord, user, loadCar);
                    insertRecords.add(goodsLoadScanRecord);
                }

                // 板上的包裹列表不需要再校验是否已验货，直接装车
                LoadScanDto loadScanDto = new LoadScanDto();
                loadScanDto.setWayBillCode(waybillCode);
                loadScanDtoList.add(loadScanDto);

                // 当前板子上同一个运单上的包裹数
                Integer packageNum = map.get(waybillCode);
                if (packageNum == null) {
                    log.info("当前板子上该运单号包裹数不存在，boardCode={},waybillCode={},packageNum={}", boardCode, waybillCode, packageCode);
                    map.put(waybillCode, 1);
                } else {
                    log.info("当前板子上该运单号包裹数存在，boardCode={},waybillCode={},packageNum={}", boardCode, waybillCode, packageCode);
                    packageNum = packageNum + 1;
                    map.put(waybillCode, packageNum);
                }
            }
            watch.split();
            log.info("循环处理板上的每一个包裹耗时={}",watch.getSplitTime());

            log.info("当前板号上各个包裹所在运单的对应数量map={}", map.toString());

            log.info("板号暂存接口--板上包裹数={},有效包裹数={},boardCode={},taskId={}", result.getData().size(),
                    updateRecords.size() + insertRecords.size(), boardCode, taskId);

            log.info("板号暂存接口--开始检验板号是否重复扫，boardCode={},taskId={}", boardCode, taskId);

            // 如果可以装车的包裹为空，则属于重复扫
            if (insertRecords.isEmpty() && updateRecords.isEmpty()) {
                // 重复扫直接跳过
                log.error("该板号属于重复扫！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该板号属于重复扫！");
                return response;
            }

            log.info("板号暂存接口--板号不属于重复扫：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                    packageCode, transfer, flowDisAccord, boardCode);
            log.info("板号暂存接口--根据板号上的运单号去分拣报表反查开始：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                    packageCode, transfer, flowDisAccord, boardCode);
            // 根据运单号列表去分拣报表查找已验未发对应的库存数
            watch.reset();
            watch.start();
            List<LoadScanDto> scanDtoList = new ArrayList<>();
            try {
                scanDtoList = getLoadScanListByWaybillCode(loadScanDtoList, loadCar.getCreateSiteCode().intValue());
            } catch (Exception e) {
                log.error("根据运单去ES获取数据异常error=", e);
            }
            watch.split();
            log.info("根据运单号获取ES数据耗时={}", watch.getSplitTime());
            watch.stop();

            if (scanDtoList == null || scanDtoList.isEmpty()) {
                log.error("根据板号上的包裹号去反查分拣报表返回为空！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据板号上的包裹号查询库存失败");
                return response;
            }
            log.info("板号暂存接口--根据板号上的运单号去分拣报表反查结束：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                    packageCode, transfer, flowDisAccord, boardCode);

            // 更新之前取消的为已装
            if (!updateRecords.isEmpty()) {
                for (GoodsLoadScanRecord record : updateRecords) {
                    goodsLoadScanRecordDao.updateGoodsScanRecordById(record);
                }
            }
            if (!insertRecords.isEmpty()) {
                // 批量保存板上的包裹记录
                goodsLoadScanRecordDao.batchInsert(insertRecords);
            }

            // 扫描第一个包裹时，修改任务状态为已开始
            if (packageMap == null || packageMap.isEmpty()) {
                updateTaskStatus(loadCar, user);
                log.info("板号暂存接口--更新任务状态结束：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                        packageCode, transfer, flowDisAccord, boardCode);
            }

            for (LoadScanDto scanDto : scanDtoList) {
                // 根据任务ID和运单号查询暂存表
                GoodsLoadScan loadScan = new GoodsLoadScan();
                loadScan.setTaskId(taskId);
                loadScan.setWayBillCode(scanDto.getWayBillCode());
                // 取出板子上该运单下的要装车包裹数量
                Integer packageNum = map.get(scanDto.getWayBillCode());
                log.info("板号暂存接口--反查记录1，boardCode={},taskId={},packageNum={},waybillCode={}", boardCode, taskId, packageNum, scanDto.getWayBillCode());

                // 计算已装、未装
                loadScan.setLoadAmount(packageNum);
                int unloadNum = scanDto.getGoodsAmount() - loadScan.getLoadAmount();
                loadScan.setUnloadAmount(unloadNum);
                log.info("板号暂存接口--反查记录2，boardCode={},taskId={},packageNum={},waybillCode={}", boardCode, taskId, packageNum, scanDto.getWayBillCode());

                // 设置运单颜色状态
                Integer status = getWaybillStatus(scanDto.getGoodsAmount(), loadScan.getLoadAmount(),
                        loadScan.getUnloadAmount(), loadScan.getForceAmount());

                log.info("板号暂存接口--反查记录3，boardCode={},taskId={},packageNum={},waybillCode={},flowDisAccord={}",
                        boardCode, taskId, packageNum, scanDto.getWayBillCode(), flowDisAccord);
                loadScan.setStatus(status);

                // 如果是多扫
                if (flowDisAccord != null && flowDisAccord == 1) {
                    loadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
                }

                // 如果已存在就更新，不存在就插入
                saveOrUpdate(loadScan, scanDto, user, flowDisAccord);
                log.info("板号暂存接口--反查记录8，boardCode={},taskId={},packageNum={},waybillCode={}", boardCode, taskId, packageNum, scanDto.getWayBillCode());

            }
        } finally {
            // 释放锁
            unLock(taskId, null, boardCode);
            log.info("板号暂存接口--锁释放：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                    packageCode, transfer, flowDisAccord, boardCode);
        }

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
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

        log.info("常规包裹号后续校验开始：taskId={},packageCode={},flowDisAccord={}", taskId, packageCode, flowDisAccord);

        // 根据包裹号查找运单
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Integer createSiteId = loadCar.getCreateSiteCode().intValue();

        // 查看包裹是否已验货,查数据库,若库无异常,可不加异常处理机制.
        Inspection inspection = new Inspection();
        inspection.setCreateSiteCode(createSiteId);
        inspection.setPackageBarcode(packageCode);
        inspection.setWaybillCode(waybillCode);
        boolean isInspected = inspectionService.haveInspectionByPackageCode(inspection);
        //至此执行了120ms
        log.info("常规包裹号后续校验--是否验货校验完成：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord+"返回结果:"+isInspected);

        // 未操作验货
        // 此类包裹，页面弹出提示：“此包裹未操作验货，无法扫描，请先操作验货”
        if (!isInspected) {
            log.warn("此包裹未操作验货，无法扫描，请先操作验货taskId={},packageCode={},waybillCode={}",
                    taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("此包裹未操作验货，无法扫描，请先操作验货");
            return response;
        }
        log.info("常规包裹号后续校验--B网快运发货校验开始：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord);

        // 校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        InvokeResult<String> invokeResult = unloadCarService.interceptValidateUnloadCar(packageCode);
        if (invokeResult != null) {
            if (InvokeResult.RESULT_INTERCEPT_CODE.equals(invokeResult.getCode())) {
                log.warn("{},taskId={},packageCode={},waybillCode={}", invokeResult.getMessage(),
                        taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage(invokeResult.getMessage());
                return response;
            }
        }
        log.info("常规包裹号后续校验--B网快运发货校验完成：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord);

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);

        log.info("常规包裹号后续校验--根据运单查询库存：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord);
        Integer goodsAmount = 0;
        try{
            List<LoadScanDto> loadScanDto = getLoadScanListByWaybillCode(scanDtoList, createSiteId);
            if (loadScanDto.isEmpty()) {
                log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}",taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据包裹号查询运单库存失败");
                return response;
            }
            log.info("常规包裹号后续校验--去分拣报表查询库存成功：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord);
            LoadScanDto scanDto = loadScanDto.get(0);
            // 校验通过，暂存
            if(scanDto != null){
                goodsAmount = scanDto.getGoodsAmount();
            }
            log.info("常规包裹号后续校验--开始暂存：taskId={}", loadCar.getId());
        }catch(Exception e) {
            log.error("包裹装车扫描出现异常，异常信息：" + e.getMessage(), e);
        }
        return saveLoadScanByPackCode(taskId, waybillCode, packageCode, goodsAmount, transfer, flowDisAccord, user, loadCar);
    }

    /**
     * 根据包裹号获取运单
     *
     * @param packageCode 包裹号
     * @return 运单
     */
    private Waybill getWaybillByPackageCode(String packageCode) {
        // 根据规则把包裹号转成运单号
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        // 根据运单号查运单详情
        log.info("根据运单号查询运单数据，查询条件:包裹号="+packageCode+";运单号="+waybillCode);
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null) {
//            log.info("根据运单号查询运单数据，查询条件:包裹号="+packageCode+";运单号="+waybillCode+"未查询到包裹数据");
            log.info("根据包裹号查询运单信息接口返回空packageCode={},waybillCode={}", packageCode, waybillCode);
            return null;
        }
        return waybill;
    }

    /**
     * 校验包裹号
     */
    @Override
    public JdVerifyResponse<Void> checkPackageCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarByTaskId(taskId);
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
        log.info("任务合法，常规包裹号开始检验：taskId={},packageCode={}", taskId, packageCode);

        String waybillCode = WaybillUtil.getWaybillCode(packageCode);

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        loadScan.setPackageCode(packageCode);
        loadScan.setCreateSiteId(loadCar.getCreateSiteCode().intValue());

        LoadScanDto loadScanDto = getLoadScanByWaybillAndPackageCode(loadScan);
        if (loadScanDto == null) {
            log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号查询运单信息返回空");
            return response;
        }
        // 发货校验
        // 1.校验包裹下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        if (loadScanDto.getNextSiteId() == null || loadCar.getEndSiteCode().intValue() != loadScanDto.getNextSiteId()) {
            log.warn("包裹下一动态路由节点与批次号下一场站不一致taskId={},packageCode={},waybillCode={},packageNextSite={},taskEndSite={}", taskId, packageCode, waybillCode, loadScanDto.getNextSiteId(), loadCar.getEndSiteCode());
            response.setCode(JdCResponse.CODE_CONFIRM);
            JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
            msgBox.setMsg("错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！");
            msgBox.setType(MsgBoxTypeEnum.CONFIRM);
            response.addBox(msgBox);
            return response;
        }

        response.setCode(JdVerifyResponse.CODE_SUCCESS);
        response.setMessage(JdVerifyResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 校验板号
     */
    @Override
    public JdVerifyResponse<Void> checkBoardCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {

        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();

        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarByTaskId(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        log.info("开始校验板号--判断任务是否已经结束：taskId={},packageCode={}", taskId, packageCode);

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }

        log.info("任务合法,包裹号转板号开始校验：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
        // 根据包裹号查板号
        Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
        log.info("根据包裹号查询板号结束：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
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
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 校验批次号并绑定任务
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    @Override
    public JdCResponse<Void> checkBatchCode(GoodsLoadingScanningReq req, JdCResponse<Void> response) {

        Long taskId = req.getTaskId();
        String batchCode = req.getBatchCode();
        User user = req.getUser();
        log.info("开始校验批次号！，taskId={},batchCode={}", taskId, batchCode);
        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarByTaskId(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }

        log.info("开始校验批次号--判断任务是否已经结束：taskId={},batchCode={}", taskId, batchCode);

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }

        log.info("开始校验批次号！任务合法！，taskId={},batchCode={}", taskId, batchCode);

        // 如果批次号已绑定，直接返回
        if (StringUtils.isNotBlank(loadCar.getBatchCode())) {
            log.warn("该任务已经绑定过批次号且不允许修改，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该任务已经绑定过批次号且不允许修改");
            return response;
        }

        try {
            log.info("开始根据批次号查询网点信息！，taskId={},batchCode={}", taskId, batchCode);
            // 根据批次号查询下一网点信息
            CreateAndReceiveSiteInfo siteInfo = siteService.getCreateAndReceiveSiteBySendCode(batchCode);
            if (siteInfo == null) {
                log.warn("根据批次号没有找到对应的网点信息，taskId={},batchCode={}", taskId, batchCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据批次号没有找到对应的网点信息");
                return response;
            }

            Integer siteCode = siteInfo.getReceiveSiteCode();
            // 系统校验批次号的目的地与输入的目的场地是否一致，如果不一致则系统提示：“批次号目的地与目的场地不一致，请检查后重新扫描”
            if (siteCode == null || !siteInfo.getReceiveSiteCode().equals(loadCar.getEndSiteCode().intValue())) {
                log.warn("批次号目的地与目的场地不一致，请检查后重新扫描，taskId={},batchCode={}", taskId, batchCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("批次号目的地与目的场地不一致，请检查后重新扫描");
                return response;
            }

            log.info("开始校验批次号是否已封车！，taskId={},batchCode={}", taskId, batchCode);
            // 校验批次是否已封车
            ServiceMessage<Boolean> result = departureService.checkSendStatusFromVOS(batchCode);
            if (!ServiceResultEnum.SUCCESS.equals(result.getResult())) {
                log.warn("该批次号已经封车，不可绑定任务！，taskId={},batchCode={}", taskId, batchCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该批次号已经封车，不可绑定任务！");
                return response;
            }
        } catch (Exception e) {
            log.error("校验批次号发生异常，taskId={},batchCode={},error=", taskId, batchCode, e);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("校验批次号发生异常！");
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
     *
     * @param taskId        任务ID
     * @param waybillCode   运单号
     * @param packageCode   包裹号
     * @param goodsAmount   库存数
     * @param flowDisAccord 多扫标识
     * @param transfer      包裹号转板号标识
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    public JdCResponse<Void> saveLoadScanByPackCode(Long taskId, String waybillCode, String packageCode,
                                                     Integer goodsAmount, Integer transfer, Integer flowDisAccord,
                                                     User user, LoadCar loadCar) {
        JdCResponse<Void> response = new JdCResponse<>();
        String boardCode = null;
        log.info("常规包裹号后续校验--开始暂存前校验--是否属于重复扫：taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
        try {
            // 判断是否有所属板号
            Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
            if (board != null) {
                log.info("该常规包裹号有对应的板号！taskId={},packageCode={},waybillCode={},boardCode={}",
                        taskId, packageCode, waybillCode, board.getCode());
                // 如果有板号，可能是扫描装车时忘了勾选转板号，记录下来以便转板号时好判断
                boardCode = board.getCode();
            }
            // 获取锁
            if (!lock(taskId, waybillCode, boardCode)) {
                log.warn("多人同时操作该包裹所在的运单，请稍后重试！");
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("多人同时操作该包裹所在的运单，请稍后重试！");
                return response;
            }

            // 校验该任务下运单数量是否已超过上限
            Integer waybillCount = goodsLoadScanDao.findWaybillCountByTaskId(taskId);
            if (waybillCount != null && waybillCount >= uccPropertyConfiguration.getLoadScanTaskWaybillSize()) {
                log.warn("该任务下运单数量已达上限！taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该任务下运单数量已达上限！");
                return response;
            }

            // 校验重复扫
            GoodsLoadScanRecord record = new GoodsLoadScanRecord();
            record.setTaskId(taskId);
            record.setWayBillCode(waybillCode);
            record.setPackageCode(packageCode);
            record.setYn(Constants.YN_YES);
            GoodsLoadScanRecord loadScanRecord = goodsLoadScanRecordDao.findRecordByWaybillCodeAndPackCode(record);
            // 如果是重复扫，返回错误
            if (loadScanRecord != null && GoodsLoadScanConstants.GOODS_SCAN_LOAD.equals(loadScanRecord.getScanAction())) {
                log.warn("该包裹号已扫描装车，请勿重复扫描！taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该包裹号已扫描装车，请勿重复扫描！");
                return response;
            }

            log.info("常规包裹号后续校验--包裹不属于重复扫：taskId={},packageCode={},waybillCode={},flowDisAccord={}",
                    taskId, packageCode, waybillCode, flowDisAccord);

            // 不属于重复扫,但被取消扫描过
            if (loadScanRecord != null) {
                loadScanRecord.setUpdateTime(new Date());
                loadScanRecord.setUpdateUserCode(user.getUserCode());
                loadScanRecord.setUpdateUserName(user.getUserName());
                loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
                goodsLoadScanRecordDao.updateGoodsScanRecordById(loadScanRecord);
            } else {
                // 如果不是重复扫，包裹扫描记录表新增一条记录
                GoodsLoadScanRecord newLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packageCode,
                        boardCode, transfer, flowDisAccord, user, loadCar);

                goodsLoadScanRecordDao.insert(newLoadScanRecord);
            }

            log.info("常规包裹号后续校验--判断是否是第一个包裹开始：taskId={}", loadCar.getId());
            // 扫描第一个包裹时，修改任务状态为已开始
            updateTaskStatus(loadCar, user);

            // 运单暂存表新增或修改
            GoodsLoadScan newLoadScan = createGoodsLoadScan(taskId, waybillCode, packageCode,
                    goodsAmount, flowDisAccord, user);
            GoodsLoadScan oldLoadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
            if (oldLoadScan == null) {
                goodsLoadScanDao.insert(newLoadScan);
            } else {
                computeAndUpdateLoadScan(oldLoadScan, goodsAmount, flowDisAccord);
                goodsLoadScanDao.updateByPrimaryKey(oldLoadScan);
            }

        } finally {
            // 释放锁
            unLock(taskId, waybillCode, boardCode);
        }

        log.info("常规包裹号后续校验--暂存结束：taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 计算已装、未装并更新装车扫描运单明细
     *
     * @param loadScan    装车扫描运单明细记录
     * @param goodsAmount 库存
     */
    private void computeAndUpdateLoadScan(GoodsLoadScan loadScan, Integer goodsAmount, Integer flowDisAccord) {
        // 计算已装、未装
        // 已装 + 1
        loadScan.setLoadAmount(loadScan.getLoadAmount() + 1);
        // 未装 ：库存 - 已装
        loadScan.setUnloadAmount(goodsAmount - loadScan.getLoadAmount());
        // 计算单子状态
        Integer status = getWaybillStatus(goodsAmount, loadScan.getLoadAmount(),
                loadScan.getUnloadAmount(), loadScan.getForceAmount());
        log.info("包裹暂存--status={},flowDisAccord={}", status, flowDisAccord);
        // 如果原来是黄颜色
        if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(loadScan.getStatus())) {
            log.info("包裹暂存--之前status={}", loadScan.getStatus());
            // 无论有没有装齐仍然显示黄颜色
            log.info("包裹暂存-之前-没装齐status={}", loadScan.getStatus());
            status = GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW;

        }
        // 如果是多扫
        if (GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y.equals(flowDisAccord)) {
            log.info("包裹暂存--多扫status={}", loadScan.getStatus());
            // 仍然显示黄颜色
            log.info("包裹暂存--多扫status={}", loadScan.getStatus());
            status = GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW;
        }
        log.info("包裹暂存--最后status={}", loadScan.getStatus());
        loadScan.setStatus(status);
    }


    /**
     * 根据包裹号和运单号获取装车扫描运单明细
     *
     * @param scanDtoList   查询条件列表
     * @param currentSiteId 当前网点id
     * @return 运单
     */
    public List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId) {
        log.warn("根据运单去ES获取数据,查询条件:currentSiteId="+currentSiteId+"开始");
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity = loadScanPackageDetailService
                .findLoadScanList(scanDtoList, currentSiteId);
        if (baseEntity == null) {
            log.warn("根据运单号和包裹号条件列表去分拣报表查询运单明细接口返回空currentSiteId={}", currentSiteId);
            return null;
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode())|| baseEntity.getData() == null) {
            log.error("根据运单号和包裹号条件列表去分拣报表查询运单明细接口失败currentSiteId={}", currentSiteId);
            return null;
        }
        log.warn("根据运单去ES获取数据,查询条件:currentSiteId="+currentSiteId+"结束，返回包裹暂存接口逻辑继续.");
        return baseEntity.getData();
    }

    /**
     * 根据运单号获取装车扫描列表
     *
     * @param waybillCodes  查询条件列表
     * @param currentSiteId 当前网点id
     * @return 运单
     */
    public List<LoadScanDto> getLoadScanByWaybillCodes(List<String> waybillCodes, Integer currentSiteId,
                                                       Integer nextSiteId, Integer rows) {
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService
                    .findLoadScanPackageDetail(waybillCodes, currentSiteId, nextSiteId, rows);
        } catch (Exception e) {
            log.error("根据暂存表记录去分拣报表查询运单明细接口发生异常currentSiteId={},currentSiteId={}",
                    currentSiteId, nextSiteId);
            return new ArrayList<>();
        }

        if (baseEntity == null) {
            log.warn("根据暂存表记录去分拣报表查询运单明细接口返回空currentSiteId={},nextSiteId={}", currentSiteId, nextSiteId);
            return new ArrayList<>();
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
            log.error("根据暂存表记录去分拣报表查询运单明细接口失败currentSiteId={},currentSiteId={},code={}",
                    currentSiteId, nextSiteId, baseEntity.getCode());
            return new ArrayList<>();
        }
        return baseEntity.getData();
    }

    /**
     * 根据运单号获取装车扫描
     *
     * @param loadScanDto 查询条件列表
     * @return 运单
     */
    public LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto) {
        com.jd.ql.dms.report.domain.BaseEntity<LoadScanDto> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService.findLoadScan(loadScanDto);
        } catch (Exception e) {
            log.error("根据包裹号和运单号去分拣报表查询包裹流向发生异常packageCode={},waybillCode={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode());
            return null;
        }

        if (baseEntity == null) {
            log.warn("根据运单号和包裹号去分拣报表查询流向返回空packageCode={},waybillCode={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode());
            return null;
        }
        if (baseEntity.getCode() != Constants.SUCCESS_CODE || baseEntity.getData() == null) {
            log.error("根据运单号和包裹号去分拣报表查询流向接口失败packageCode={},waybillCode={},code={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode(), baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }


    private List<GoodsDetailDto> transformData(List<LoadScanDto> list, Map<String, GoodsLoadScan> map,
                                               Map<String, LoadScanDto> flowDisAccordMap) {
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        for (LoadScanDto detailDto : list) {
            GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
            goodsDetailDto.setWayBillCode(detailDto.getWayBillCode());
            goodsDetailDto.setPackageAmount(detailDto.getPackageAmount());
            goodsDetailDto.setGoodsAmount(detailDto.getGoodsAmount());
            // 从map中获取运单号对应的暂存记录
            GoodsLoadScan loadScan = map.get(detailDto.getWayBillCode());
            goodsDetailDto.setLoadAmount(loadScan.getLoadAmount());
            int unloadNum = detailDto.getGoodsAmount() - loadScan.getLoadAmount();
            goodsDetailDto.setUnloadAmount(unloadNum);
            // 重新计算颜色
            Integer status = getWaybillStatus(detailDto.getGoodsAmount(), loadScan.getLoadAmount(),
                    goodsDetailDto.getUnloadAmount(), loadScan.getForceAmount());
            goodsDetailDto.setStatus(status);
            // 多扫仍然显示黄色
            if (flowDisAccordMap.get(detailDto.getWayBillCode()) != null) {
                goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
                // 如果包裹已装等于库存显示绿色
                if (detailDto.getGoodsAmount().equals(loadScan.getLoadAmount())) {
                    goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
                }
                // 多扫如果强制下发显示橙色
                if (loadScan.getForceAmount() > 0) {
                    goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
                }
            }
            goodsDetails.add(goodsDetailDto);
        }
        return goodsDetails;
    }

    private GoodsLoadScanRecord createGoodsLoadScanRecord(Long taskId, String waybillCode, String packageCode,
                                                          String boardCode, Integer transfer, Integer flowDisAccord,
                                                          User user, LoadCar loadCar) {
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
        // 强发标识
        loadScanRecord.setForceStatus(0);

        loadScanRecord.setCreateUserCode(user.getUserCode());
        loadScanRecord.setCreateUserName(user.getUserName());
        loadScanRecord.setUpdateUserCode(user.getUserCode());
        loadScanRecord.setUpdateUserName(user.getUserName());

        loadScanRecord.setCreateTime(new Date());
        loadScanRecord.setUpdateTime(new Date());
        loadScanRecord.setYn(Constants.YN_YES);


        //2020 10-23为了装车名词报表增加始发和目的场地id以及各自名称
        loadScanRecord.setCreateSiteCode(loadCar.getCreateSiteCode());
        loadScanRecord.setCreateSiteName(loadCar.getCreateSiteName());
        loadScanRecord.setEndSiteCode(loadCar.getEndSiteCode());
        loadScanRecord.setEndSiteName(loadCar.getEndSiteName());


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
        // 如果已装等于库存
        if (goodsLoadScan.getLoadAmount().equals(goodsAmount)) {
            goodsLoadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
        }
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


    private List<String> getWaybillCodes(List<GoodsLoadScan> scans, Map<String, GoodsLoadScan> map,
                                         Map<String, LoadScanDto> flowDisAccordMap) {
        List<String> list = new ArrayList<>();
        LoadScanDto scanDto;
        for (GoodsLoadScan scan : scans) {
            // 如果之前已装大于0，后来都被取消了，那就不显示
            if (scan.getLoadAmount() <= 0) {
                continue;
            }
            list.add(scan.getWayBillCode());
            // 筛选出属于多扫的
            if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(scan.getStatus())) {
                scanDto = new LoadScanDto();
                scanDto.setWayBillCode(scan.getWayBillCode());
                // 记录暂存表中的多扫记录，以便再给端上返回时仍显示黄色
                flowDisAccordMap.put(scan.getWayBillCode(), scanDto);
            }
            // 记录原来的运单对应的暂存记录
            map.put(scan.getWayBillCode(), scan);
        }
        return list;
    }

    /**
     * 修改任务状态
     *
     * @param loadCar 任务
     */
    private void updateTaskStatus(LoadCar loadCar, User user) {
        // 扫描第一个包裹时，将任务状态改为已开始
        List<String> waybillCodeList = goodsLoadScanDao.findWaybillCodesByTaskId(loadCar.getId());
        if (waybillCodeList.isEmpty()) {
            log.info("常规包裹号后续校验--是第一个扫描包裹，开始修改任务状态：taskId={}", loadCar.getId());
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN);
            loadCar.setOperateUserErp(user.getUserErp());
            loadCar.setOperateUserName(user.getUserName());
            loadCar.setUpdateTime(new Date());
            loadCarDao.updateLoadCarById(loadCar);
        }
    }

    /**
     * 获取运单状态
     *
     * @param goodsAmount  库存
     * @param loadAmount   已装
     * @param unloadAmount 未装
     * @param forceAmount  强发
     * @return 状态
     */
    private Integer getWaybillStatus(Integer goodsAmount, Integer loadAmount, Integer unloadAmount,
                                     Integer forceAmount) {
        if (forceAmount == null) {
            forceAmount = 0;
        }

        // 已装等于库存，未装=0 -- 已扫描完 -- 绿色
        if (goodsAmount.equals(loadAmount)) {
            return GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN;
        }

        // 已装等于0且总包裹=库存 -- 货到齐没开始扫 或 扫完取消 -- 无特殊颜色
        // 已装等于0且总包裹≠库存 -- 货没到齐没开始扫 或 扫完取消 -- 无特殊颜色
        if (loadAmount == 0) {
            return GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK;
        }

        // 已装和未装都大于0  -- 没扫齐 -- 红色
        if (loadAmount > 0 && unloadAmount > 0) {
            // 已装和未装都大于0，操作强发 -- 没扫齐强发 -- 橙色
            if (forceAmount > 0) {
                return GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE;
            }
            return GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED;
        }

        return GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK;
    }

    /**
     * 根据包裹号查板号信息
     *
     * @param siteCode    当前站点信息
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

    private boolean lock(Long taskId, String waybillCode, String boardCode) {
        String lockKey = LOADS_CAN_LOCK_BEGIN + taskId + "_" + getLockFlag(waybillCode, boardCode);
        try {
            if (!jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS)) {
                Thread.sleep(100);
                return jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("装车扫描unLock异常:taskId={},waybillCode={},boardCode={},e=", taskId, waybillCode, boardCode, e);
            jimdbCacheService.del(lockKey);
        }
        return true;
    }

    private String getLockFlag(String waybillCode, String boardCode) {
        if (StringUtils.isNotBlank(boardCode)) {
            return boardCode;
        } else {
            return waybillCode;
        }
    }

    private void unLock(Long taskId, String waybillCode, String boardCode) {
        try {
            String lockKey = LOADS_CAN_LOCK_BEGIN + taskId + "_" + getLockFlag(waybillCode, boardCode);
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            log.error("装车扫描unLock异常:taskId={},waybillCode={},boardCode={},e=", taskId, waybillCode, boardCode, e);
        }
    }

    public boolean saveOrUpdate(GoodsLoadScan e, LoadScanDto scanDto, User user, Integer flowDisAccord) {
        log.info("板号暂存接口--反查记录4");

        GoodsLoadScan oldData = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(e.getTaskId(), e.getWayBillCode());
        log.info("板号暂存接口--反查记录5");

        e.setUpdateTime(new Date());
        e.setUpdateUserCode(user.getUserCode());
        e.setUpdateUserName(user.getUserName());
        if (oldData != null) {
            log.info("板号暂存接口--反查记录6");
            e.setLoadAmount(oldData.getLoadAmount() + e.getLoadAmount());
            e.setUnloadAmount(scanDto.getGoodsAmount() - e.getLoadAmount());
            Integer status = getWaybillStatus(scanDto.getGoodsAmount(), e.getLoadAmount(), e.getUnloadAmount(),
                    oldData.getForceAmount());
            e.setStatus(status);
            // 如果原来是黄颜色
            if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(oldData.getStatus())) {
                // 无论有没有没装齐仍然显示黄颜色
                e.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }
            //
            // 如果是多扫
            if (flowDisAccord != null && flowDisAccord == 1) {
                e.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }
            e.setId(oldData.getId());
            return goodsLoadScanDao.updateByPrimaryKey(e);
        } else {
            log.info("板号暂存接口--反查记录7");

            e.setCreateTime(new Date());
            e.setCreateUserCode(user.getUserCode());
            e.setCreateUserName(user.getUserName());
            return goodsLoadScanDao.insert(e);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteLoadScanByTaskId(Long taskId) {
        goodsLoadScanDao.deleteLoadScanByTaskId(taskId);
        goodsLoadScanRecordDao.deleteLoadScanRecordByTaskId(taskId);
    }

}
