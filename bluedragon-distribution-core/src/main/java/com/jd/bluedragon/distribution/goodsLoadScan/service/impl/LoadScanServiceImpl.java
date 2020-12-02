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
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadScanDetailDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
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
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("loadScanService")
public class LoadScanServiceImpl implements LoadScanService {

    private final static Logger log = LoggerFactory.getLogger(LoadScanServiceImpl.class);

    private final static int LOCK_TIME = 60;


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
    private DepartureService departureService;

    @Autowired
    private UnloadCarService unloadCarService;

    @Resource
    private LoadScanCacheService loadScanCacheService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    public static final String LOADS_CAN_LOCK_BEGIN = "LOADS_CAN_LOCK_";


    @Autowired
    @Qualifier(value = "goodsLoadTaskProducer")
    private DefaultJMQProducer goodsLoadTaskProducer;

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        JdCResponse response = new JdCResponse();
        //超出最大任务限制包裹数后禁止发货
        int packageCount = goodsLoadScanRecordDao.getPackageCountByTaskId(req.getTaskId());

        if(packageCount >= uccPropertyConfiguration.getLoadScanTaskPackageMaxSize()) {
            if(log.isDebugEnabled()) {
                log.debug("任务【{}】关联包裹数超出最大包裹量（{}）限制", req.getTaskId(), uccPropertyConfiguration.getLoadScanTaskPackageMaxSize());
            }
            response.toFail("该任务装车包裹数超出最大包裹数量限制【" + uccPropertyConfiguration.getLoadScanTaskPackageMaxSize() + "】，无法进行发货");
            return response;
        }

        //发货MQ 任务ID   返回 boolean
        LoadCar loadCar = new LoadCar();
        try{
            //修改任务状态为：已完成状态
            loadCar.setId(req.getTaskId());
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END);
            boolean flagRes = loadCarDao.updateLoadCarById(loadCar);
            if (!flagRes) {
                if(log.isWarnEnabled()) {
                    log.warn("发货完成后修改任务状态失败，发货信息【{}】",JsonHelper.toJson(loadCar));
                }
                response.toFail("发货状态修改失败");
                return response;
            }

            //发送任务MQ
            try{
                log.info("20201029--装车发货生产任务MQ--start--，消息体【{}】", JsonHelper.toJson(req));
                goodsLoadTaskProducer.sendOnFailPersistent(req.getTaskId().toString(), JsonHelper.toJson(req));
                log.info("20201029--装车发货生产任务MQ--end--，消息体【{}】", JsonHelper.toJson(req));
            } catch (Exception e) {
                log.error("装车发货发送任务MQ报错，发送参数【{}】", req);
                System.out.println("----");
            }
        }catch(GoodsLoadScanException e) {
            log.error("装车发货任务发送MQ错误【{}】, 错误信息【{}】", req, e);
        }

//        loadScanCacheService.setTaskLoadScan(loadCar);
        //发货完成设置最终状态时间15天
        String endTaskStatus = GoodsLoadScanConstants.CACHE_KEY_TASK + loadCar.getId().toString();
        try {
            jimdbCacheService.setEx(endTaskStatus, loadCar, 15, TimeUnit.DAYS);
        }catch (GoodsLoadScanException e){
            log.error("装货完成缓存插入失败， K-[{}],V-[{}]", endTaskStatus, loadCar);
        }
        response.toSucceed("发货成功");
        return response;

    }

    public List<GoodsLoadScanRecord> findGoodsLoadRecordPage(Long taskId, int start, int end) {
        return goodsLoadScanRecordDao.findGoodsLoadRecordPage(taskId, start, end);

    }


    @Override
    public LoadCar findTaskStatus(Long taskId) {
        LoadCar lc = loadScanCacheService.getTaskLoadScan(taskId);
        if(lc == null) {
            lc = loadCarDao.findLoadCarByTaskId(taskId);

            if(lc != null) {
                loadScanCacheService.setTaskLoadScan(lc);
            }
        }

        return lc;
    }

    @Override
    public boolean updateGoodsLoadScanAmount(GoodsLoadScan goodsLoadScan, GoodsLoadScanRecord goodsLoadScanRecord, Integer currentSiteCode) {

        boolean res = true;
        String lockKey = goodsLoadScan.getTaskId().toString();
        try {
            if (!loadScanCacheService.lock(lockKey, 1)) {
                Thread.sleep(100);
                boolean cacheResult = jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, 2, TimeUnit.SECONDS);
                if (!cacheResult) {
                    if(log.isDebugEnabled()) {
                        log.debug("装车发货扫描计算已安装、未安装数据操作lock失败，任务号【{}】", lockKey);
                    }
                    return false;
                }
            }

            //取消扫描
            if (GoodsLoadScanConstants.GOODS_SCAN_REMOVE.equals(goodsLoadScanRecord.getScanAction())) {
                res = this.scanRemove(goodsLoadScan, goodsLoadScanRecord, currentSiteCode);

            } else if (GoodsLoadScanConstants.GOODS_SCAN_LOAD.equals(goodsLoadScanRecord.getScanAction())) { //装车扫描
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
            List<GoodsLoadScanRecord> list = goodsLoadScanRecordDao.selectListByCondition(packageRecordQueryParam);
            if(list == null || list.size() <=0) {
                if(log.isDebugEnabled()) {
                    log.debug("查询包裹为null, 参数【{}】", packageRecordQueryParam);
                }

                return false;
            }
            packageRecord = list.get(0);
        }

        goodsLoadScanRecord.setId(packageRecord.getId());//这里不需要MPE判断， 只有不为空一定有id

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
            if(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y.equals(status)) {
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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value = "main_loadunload")
    public boolean goodRemoveOper(GoodsLoadScanRecord goodsLoadScanRecord, GoodsLoadScan goodsLoadScan) {

        if(log.isDebugEnabled()) {
            log.debug("取消发货扫描包裹表修改--begin--，参数【{}】", JsonHelper.toJson(goodsLoadScanRecord));
        }
        int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
        if (num <= 0) {
            log.error("取消发货扫描包裹表修改失败，参数【{}】", goodsLoadScanRecord);
            throw new GoodsLoadScanException("取消发货扫描包裹表修改失败，参数【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
        } else if(log.isDebugEnabled()){
            log.debug("取消发货扫描包裹表修改--begin--，参数【{}】", JsonHelper.toJson(goodsLoadScanRecord));
        }

        if(log.isDebugEnabled()) {
            log.debug("取消发货扫描运单表修改 --begin--，参数【{}】", JsonHelper.toJson(goodsLoadScan));
        }
        boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
        if (!scNum) {
            log.error("取消发货扫描运单表修改失败,运单信息【{}】" , goodsLoadScan);
            throw new GoodsLoadScanException("取消发货扫描运单表修改失败,运单信息【" + JsonHelper.toJson(goodsLoadScan) + "】");
        }

        if(log.isDebugEnabled()) {
            log.debug("取消发货扫描运单表修改 --success--，参数【{}】", JsonHelper.toJson(goodsLoadScan));
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
                if(log.isDebugEnabled()) {
                    log.debug("装车扫描修改包裹记录表--begin--，入参【{}】", JsonHelper.toJson(goodsLoadScanRecord));
                }
                int num = goodsLoadScanRecordDao.insert(goodsLoadScanRecord);
                if (num <= 0) {
                    throw new GoodsLoadScanException("装车扫描修改包裹记录表失败入参【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
                }
                if(log.isDebugEnabled()) {
                    log.debug("装车扫描修改包裹记录表--success--，入参【{}】", JsonHelper.toJson(goodsLoadScanRecord));
                    log.debug("装车扫描运单表 --begin--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
                }

                boolean scNum = goodsLoadScanDao.insert(goodsLoadScan);
                if (!scNum) {
                    throw new GoodsLoadScanException("装车扫描运单表修改失败入参【" + JsonHelper.toJson(goodsLoadScan) + "】");
                }
                if(log.isDebugEnabled()) {
                    log.debug("装车扫描运单表 --success--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
                }
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
            if(log.isDebugEnabled()) {
                log.debug("装车扫描运单表 --begin--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
            }
            boolean scNum = goodsLoadScanDao.updateByPrimaryKey(goodsLoadScan);
            if (!scNum) {
                throw new GoodsLoadScanException("装车扫描运单表修改失败入参【" + JsonHelper.toJson(goodsLoadScan) + "】");
            }
            if(log.isDebugEnabled()) {
                log.debug("装车扫描运单表 --success--，入参【{}】", JsonHelper.toJson(goodsLoadScan));
                log.debug("装车扫描修改包裹记录表--begin--，入参【{}】" + JsonHelper.toJson(goodsLoadScanRecord));
            }

            int num = goodsLoadScanRecordDao.updateGoodsScanRecordById(goodsLoadScanRecord);
            if (num <= 0) {
                throw new GoodsLoadScanException("装车扫描修改包裹记录表失败入参【" + JsonHelper.toJson(goodsLoadScanRecord) + "】");
            }
            if(log.isDebugEnabled()) {
                log.debug("装车扫描修改包裹记录表--success--，入参【{}】" + JsonHelper.toJson(goodsLoadScanRecord));
            }

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
            response.setMessage("该装车任务不存在");
            return response;
        }
        if (log.isDebugEnabled()) {
            log.debug("开始查找暂存表--判断任务是否已经结束：taskId={}", taskId);
        }
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
            response.setMessage("该装车任务网点编码为空");
            return response;
        }
        Integer createSiteId = loadCar.getCreateSiteCode().intValue();
        // 根据任务号查找装车扫描明细暂存表
        List<GoodsLoadScan> tempList = goodsLoadScanDao.findLoadScanByTaskId(taskId);

        if (log.isDebugEnabled()) {
            log.debug("根据任务ID查找暂存表，taskId={}", req.getTaskId());
        }
        List<LoadScanDto> reportList;
        List<GoodsDetailDto> goodsDetailDtoList = new ArrayList<>();
        // 暂存表运单号，运单号对应的暂存记录
        Map<String, GoodsLoadScan> map = new HashMap<>(16);

        // 组装返回对象
        LoadScanDetailDto scanDetailDto = new LoadScanDetailDto();
        scanDetailDto.setBatchCode(loadCar.getBatchCode());

        if (CollectionUtils.isEmpty(tempList)) {
            scanDetailDto.setGoodsDetailDtoList(goodsDetailDtoList);
            scanDetailDto.setTotalWeight(0d);
            scanDetailDto.setTotalVolume(0d);
            scanDetailDto.setTotalPackageNum(0);
            response.setCode(JdCResponse.CODE_SUCCESS);
            response.setData(scanDetailDto);
            return response;
        }

        // 如果暂存表不为空，则去分拣报表拉取最新的库存数据
        // 记录属于多扫状态的运单
        Map<String, LoadScanDto> flowDisAccordMap = new HashMap<>(16);

        if (log.isDebugEnabled()) {
            log.debug("根据任务ID查找暂存表不为空，taskId={},size={}", req.getTaskId(), tempList.size());
        }

        List<LoadScanDto> waybillCodeList = getWaybillCodes(tempList, map, flowDisAccordMap);
        reportList = getLoadScanListByWaybillCode(waybillCodeList, createSiteId);

        log.info("根据暂存表记录反查分拣报表正常返回，taskId={},size={}", req.getTaskId(), reportList.size());
        // 转换数据
        if (!CollectionUtils.isEmpty(reportList)) {
            log.info("根据暂存表记录反查分拣报表结束，开始转换数据。taskId={}", req.getTaskId());
            goodsDetailDtoList = transformData(reportList, map, flowDisAccordMap, scanDetailDto);
        }

        // 按照颜色排序
        Collections.sort(goodsDetailDtoList, new Comparator<GoodsDetailDto>() {
            @Override
            public int compare(GoodsDetailDto o1, GoodsDetailDto o2) {
                // status：0无特殊颜色,1绿色,2橙色,3黄色,4红色
                return o2.getStatus().compareTo(o1.getStatus());
            }
        });

        scanDetailDto.setGoodsDetailDtoList(goodsDetailDtoList);
        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setData(scanDetailDto);

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
        // 查询板号上包裹的数量
        Response<List<String>> result = groupBoardManager.getBoxesByBoardCode(boardCode);
        if (result == null || result.getCode() != ResponseEnum.SUCCESS.getIndex()
                || CollectionUtils.isEmpty(result.getData())) {
            log.error("根据板号没有找到对应的包裹列表！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号没有找到对应的包裹列表");
            return response;
        }

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

            // 板子上可以装车的有效包裹
            List<GoodsLoadScanRecord> insertRecords = new ArrayList<>();
            List<GoodsLoadScanRecord> updateRecords = new ArrayList<>();
            // 校验板号是否属于重复扫
            handlePackagesOfBulk(result.getData(), loadCar, boardCode, req, insertRecords, updateRecords);
            // 如果可以装车的包裹为空，则属于重复扫
            if (insertRecords.isEmpty() && updateRecords.isEmpty()) {
                log.error("该板号属于重复扫！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该板号属于重复扫！");
                return response;
            }

            // 运单，包裹数
            Map<String, Integer> map = new HashMap<>(16);
            // 板上的运单记录
            Map<String, LoadScanDto> waybillMap = new HashMap<>(16);

            // 根据板上的包裹列表计算合并每个运单上的包裹数并根据运单去重
            computeLoadAndFilterWaybill(insertRecords, waybillMap, map);
            computeLoadAndFilterWaybill(updateRecords, waybillMap, map);

            List<LoadScanDto> loadScanDtoList = new ArrayList<>(waybillMap.values());
            if (log.isDebugEnabled()) {
                log.debug("板号暂存接口--根据板号上的运单号去分拣报表反查开始：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                        packageCode, transfer, flowDisAccord, boardCode);
            }
            // 根据运单号列表去分拣报表查找已验未发对应的库存数
            List<LoadScanDto> scanDtoList = new ArrayList<>();
            try {
                scanDtoList = getLoadScanListByWaybillCode(loadScanDtoList, loadCar.getCreateSiteCode().intValue());
            } catch (Exception e) {
                log.error("根据运单去ES获取数据异常error=", e);
            }

            if (scanDtoList == null || scanDtoList.isEmpty()) {
                log.error("根据板号上的包裹号去反查分拣报表返回为空！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("包裹未验货或已发货，请核实包裹状态");
                return response;
            }
            if (log.isDebugEnabled()) {
                log.debug("板号暂存接口--根据板号上的运单号去分拣报表反查结束：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                        packageCode, transfer, flowDisAccord, boardCode);
            }
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
            updateTaskStatus(loadCar, user);
            // 循环处理板上每一个运单
            for (LoadScanDto scanDto : scanDtoList) {
                // 根据任务ID和运单号查询暂存表
                GoodsLoadScan loadScan = new GoodsLoadScan();
                loadScan.setTaskId(taskId);
                loadScan.setWayBillCode(scanDto.getWayBillCode());
                // 取出板子上该运单下的要装车包裹数量
                Integer packageNum = map.get(scanDto.getWayBillCode());
                // 计算已装、未装
                loadScan.setLoadAmount(packageNum);
                int unloadNum = scanDto.getGoodsAmount() - loadScan.getLoadAmount();
                loadScan.setUnloadAmount(unloadNum);
                // 设置运单颜色状态
                Integer status = getWaybillStatus(scanDto.getGoodsAmount(), loadScan.getLoadAmount(),
                        loadScan.getUnloadAmount(), loadScan.getForceAmount());
                loadScan.setStatus(status);

                // 如果是多扫
                if (flowDisAccord != null && flowDisAccord == 1) {
                    loadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
                }
                loadScan.setForceAmount(0);

                // 如果已存在就更新，不存在就插入
                saveOrUpdate(loadScan, scanDto.getGoodsAmount(), user, flowDisAccord);

                if (log.isDebugEnabled()) {
                    log.debug("板号暂存接口--板号暂存结束：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                            packageCode, transfer, flowDisAccord, boardCode);
                }
            }
        } catch (Exception e) {
            log.error("按板号暂存逻辑发生错误e=", e);
        } finally {
            // 释放锁
            unLock(taskId, null, boardCode);
            if (log.isDebugEnabled()) {
                log.debug("板号暂存接口--锁释放：taskId={},packageCode={},transfer={},flowDisAccord={},boardCode={}", taskId,
                        packageCode, transfer, flowDisAccord, boardCode);
            }
        }

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    @Override
    public JdCResponse<Void> saveLoadScanByWaybillCode(GoodsLoadingScanningReq req, JdCResponse<Void> response, LoadCar loadCar) {
        Long taskId = req.getTaskId();
        String packageCode = req.getPackageCode();
        // 包裹号转板号标识
        Integer transfer = req.getTransfer();
        // 多扫标识
        Integer flowDisAccord = req.getFlowDisaccord();
        User user = req.getUser();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);

        // 校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        if (checkInterceptionValidate(response, taskId, packageCode)) {
            return response;
        }

        try {
            // 获取锁
            if (!lock(taskId, waybillCode, null)) {
                log.info("运单暂存接口--获取锁失败：taskId={},packageCode={},transfer={},flowDisAccord={}", taskId,
                        packageCode, transfer, flowDisAccord);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("多人同时操作该包裹所在的运单，请稍后重试！");
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

            // 根据运单号去ES查询库存包裹号 todo
            List<String> packageList = new ArrayList<>();

            // 运单上可以装车的有效包裹
            List<GoodsLoadScanRecord> insertRecords = new ArrayList<>();
            List<GoodsLoadScanRecord> updateRecords = new ArrayList<>();
            // 校验运单号是否属于重复扫
            handlePackagesOfBulk(packageList, loadCar, null, req, insertRecords, updateRecords);
            // 如果可以装车的包裹为空，则属于重复扫
            if (insertRecords.isEmpty() && updateRecords.isEmpty()) {
                log.error("该运单号属于重复扫！taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该运单号属于重复扫！");
                return response;
            }

            // 更新之前取消的为已装
            if (!updateRecords.isEmpty()) {
                for (GoodsLoadScanRecord record : updateRecords) {
                    goodsLoadScanRecordDao.updateGoodsScanRecordById(record);
                }
            }
            if (!insertRecords.isEmpty()) {
                // 批量暂存运单上的包裹
                goodsLoadScanRecordDao.batchInsert(insertRecords);
            }

            // 扫描第一个包裹时，修改任务状态为已开始
            updateTaskStatus(loadCar, user);

            GoodsLoadScan loadScan = new GoodsLoadScan();
            loadScan.setTaskId(taskId);
            loadScan.setWayBillCode(waybillCode);
            // 计算已装、未装
            loadScan.setLoadAmount(packageList.size());
            // 按运单暂存，库存有多少就装多少，未装永远等于0
            loadScan.setUnloadAmount(0);
            // 按运单暂存，永远是装齐状态，运单颜色为绿色
            loadScan.setStatus(1);

            // 如果是多扫
            if (flowDisAccord != null && flowDisAccord == 1) {
                loadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }
            loadScan.setForceAmount(0);

            // 如果已存在就更新，不存在就插入
            saveOrUpdate(loadScan, packageList.size(), user, flowDisAccord);
        } catch (Exception e) {
            log.error("按运单号暂存逻辑发生错误e=", e);
        } finally {
            // 释放锁
            unLock(taskId, waybillCode, null);
            if (log.isDebugEnabled()) {
                log.debug("运单号暂存接口--锁释放：taskId={},packageCode={},transfer={},flowDisAccord={}", taskId,
                        packageCode, transfer, flowDisAccord);
            }
        }

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
     * @param response 响应
     * @param taskId 任务ID
     * @param packageCode 包裹号
     */
    private boolean checkInterceptionValidate(JdCResponse<Void> response, Long taskId, String packageCode) {
        InvokeResult<String> invokeResult = unloadCarService.interceptValidateUnloadCar(packageCode);
        if (invokeResult != null) {
            if (InvokeResult.RESULT_INTERCEPT_CODE.equals(invokeResult.getCode())) {
                log.warn("【B网快运发货】规则校验失败：{},taskId={},packageCode={}", invokeResult.getMessage(), taskId, packageCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage(invokeResult.getMessage());
                return true;
            }
        }
        return false;
    }

    /**
     * 根据板上的包裹列表计算合并每个运单上的包裹数并根据运单去重
     * @param records 板上有效的包裹列表
     * @param waybillMap 运单集合，key为运单号，value为查询库存参数对象
     * @param map 运单集合，key为运单号，value为板上这个运单所对应的包裹数
     */
    private void computeLoadAndFilterWaybill(List<GoodsLoadScanRecord> records, Map<String, LoadScanDto> waybillMap,
                                             Map<String, Integer> map) {
        if (!CollectionUtils.isEmpty(records)) {
            for (GoodsLoadScanRecord record : records) {
                LoadScanDto scanDto = waybillMap.get(record.getWayBillCode());
                if (scanDto == null) {
                    // 板上的包裹列表不需要再校验是否已验货，直接装车
                    LoadScanDto loadScanDto = new LoadScanDto();
                    loadScanDto.setWayBillCode(record.getWayBillCode());
                    waybillMap.put(record.getWayBillCode(), loadScanDto);
                }
                // 当前板子上同一个运单上的包裹数
                Integer packageNum = map.get(record.getWayBillCode());
                if (packageNum == null) {
                    map.put(record.getWayBillCode(), 1);
                } else {
                    packageNum = packageNum + 1;
                    map.put(record.getWayBillCode(), packageNum);
                }
            }
        }
    }

    /**
     * 对包裹号集合进行处理，梳理出新增的和要修改的，从而判断是否重复扫
     * @param packages 包裹号集合
     * @param loadCar 装车任务对象
     * @param boardCode 板号
     * @param req 查询参数对象
     * @param insertRecords 新增的包裹集合
     * @param updateRecords 修改的包裹集合
     */
    private void handlePackagesOfBulk(List<String> packages, LoadCar loadCar, String boardCode, GoodsLoadingScanningReq req,
                                       List<GoodsLoadScanRecord> insertRecords, List<GoodsLoadScanRecord> updateRecords) {
        User user = req.getUser();
        Long taskId = loadCar.getId();
        Integer transfer = req.getTransfer();
        Integer flowDisAccord = req.getFlowDisaccord();

        // 根据即将要装的包裹号列表来查询当前中心是否扫描过。
        Map<String, GoodsLoadScanRecord> packageMap = goodsLoadScanRecordDao.findRecordsByBoardCode(loadCar.getCreateSiteCode(), packages);

        // 如果这些包裹有之前扫过的，需要过滤
        if (packageMap != null && !packageMap.isEmpty()) {
            // 循环处理每一个包裹
            for (String packCode : packages) {
                if (!WaybillUtil.isPackageCode(packCode)) {
                    continue;
                }
                String waybillCode = WaybillUtil.getWaybillCode(packCode);
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
                    record.setBoardCode(boardCode);
                    record.setTaskId(loadCar.getId());
                    record.setCreateSiteCode(loadCar.getCreateSiteCode());
                    record.setCreateSiteName(loadCar.getCreateSiteName());
                    record.setEndSiteCode(loadCar.getEndSiteCode());
                    record.setEndSiteName(loadCar.getEndSiteName());
                    record.setLicenseNumber(loadCar.getLicenseNumber());
                    updateRecords.add(record);
                }
                // 没扫描过的包裹正常装
                if (record == null) {
                    GoodsLoadScanRecord goodsLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packCode,
                            boardCode, transfer, flowDisAccord, user, loadCar);
                    insertRecords.add(goodsLoadScanRecord);
                }
            }
        } else {
            // 这些包裹都是新增的
            for (String packCode : packages) {
                if (!WaybillUtil.isPackageCode(packCode)) {
                    continue;
                }
                String waybillCode = WaybillUtil.getWaybillCode(packCode);
                GoodsLoadScanRecord goodsLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packCode,
                        boardCode, transfer, flowDisAccord, user, loadCar);
                insertRecords.add(goodsLoadScanRecord);
            }
        }
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

        if (log.isDebugEnabled()) {
            log.debug("常规包裹号后续校验开始：taskId={},packageCode={},flowDisAccord={}", taskId, packageCode, flowDisAccord);
        }
        // 根据包裹号查找运单
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Integer createSiteId = loadCar.getCreateSiteCode().intValue();

        // 校验拦截、包装服务、无重量等发货校验，发货校验规则同【B网快运发货】功能
        if (checkInterceptionValidate(response, taskId, packageCode)) {
            return response;
        }

        // 根据运单号和包裹号查询已验未发的唯一一条记录
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);

        Integer goodsAmount = 0;
        try{
            List<LoadScanDto> loadScanDto = getLoadScanListByWaybillCode(scanDtoList, createSiteId);
            if (loadScanDto == null || loadScanDto.isEmpty()) {
                log.error("根据包裹号和运单号从分拣报表查询运单信息返回空taskId={},packageCode={},waybillCode={}",taskId, packageCode, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据包裹号查询运单库存失败");
                return response;
            }
            if (log.isDebugEnabled()) {
                log.debug("常规包裹号后续校验--去分拣报表查询库存成功：taskId={},packageCode={},waybillCode={},flowDisAccord={}", taskId, packageCode, waybillCode, flowDisAccord);
            }
            LoadScanDto scanDto = loadScanDto.get(0);
            // 校验通过，暂存
            if(scanDto != null){
                goodsAmount = scanDto.getGoodsAmount();
            }
            if (log.isDebugEnabled()) {
                log.debug("常规包裹号后续校验--开始暂存：taskId={}", loadCar.getId());
            }
        }catch(Exception e) {
            log.error("包裹装车扫描出现异常，异常信息：" + e.getMessage(), e);
        }
        return saveLoadScanByPackCode(taskId, waybillCode, packageCode, goodsAmount, transfer, flowDisAccord, user, loadCar);
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

        if (log.isDebugEnabled()) {
            log.debug("任务合法，常规包裹号开始检验：taskId={},packageCode={}", taskId, packageCode);
        }
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
            response.setMessage("包裹未验货或已发货，请核实包裹状态");
            return response;
        }
        Integer nextDmsSiteId = loadScanDto.getNextSiteId();
        // 如果ES中的路由还没计算出来，再实时调用一次
        if (nextDmsSiteId == null) {
            log.info("分拣报表中的路由还没计算出来，开始实时调用路由接口taskId={},packageCode={}", taskId, packageCode);
            nextDmsSiteId = waybillService.getRouterFromMasterDb(waybillCode, loadCar.getCreateSiteCode().intValue());
            log.info("实时调用路由接口结束taskId={},packageCode={},nextDmsSiteId={}", taskId, packageCode, nextDmsSiteId);
        }
        // 发货校验
        // 1.校验包裹下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此包裹流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        if (nextDmsSiteId == null || loadCar.getEndSiteCode().intValue() != nextDmsSiteId) {
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

        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }
        if (log.isDebugEnabled()) {
            log.debug("任务合法,包裹号转板号开始校验：taskId={},packageCode={}", req.getTaskId(), req.getPackageCode());
        }
        // 根据包裹号查板号
        Board board = getBoardCodeByPackageCode(loadCar.getCreateSiteCode().intValue(), packageCode);
        if (board == null) {
            log.error("根据包裹号没有找到对应的板号！taskId={},packageCode={}", taskId, packageCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据包裹号没有找到对应的板号");
            return response;
        }
        log.info("根据包裹号查板号结果：board={}", JsonHelper.toJson(board));

        String boardCode = board.getCode();
        if (log.isDebugEnabled()) {
            log.debug("获取板信息成功，taskId={},packageCode={},boardCode:{}", taskId, packageCode, boardCode);
        }

        // 查询板号上包裹的数量
        Response<List<String>> result = groupBoardManager.getBoxesByBoardCode(boardCode);
        if (result == null || result.getCode() != ResponseEnum.SUCCESS.getIndex()
                || CollectionUtils.isEmpty(result.getData())) {
            log.error("根据板号没有找到对应的包裹列表！taskId={},packageCode={},boardCode={}", taskId, packageCode, boardCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据板号没有找到对应的包裹列表");
            return response;
        }

        //校验板上包裹最大数量
        if (result.getData().size() > uccPropertyConfiguration.getLoadScanTaskPackageSize()) {
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("此板号下包裹超" + uccPropertyConfiguration.getLoadScanTaskPackageSize() + "件，请切换至快运发货功能进行发货操作");
            return response;
        }


        JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
        msgBox.setType(MsgBoxTypeEnum.CONFIRM);
        msgBox.setData(boardCode);
        response.setCode(JdCResponse.CODE_CONFIRM);
        String msg;

        // 校验板号流向与批次号流向是否一致，如不一致进行错发弹框提醒（“错发！请核实！板号与批次目的地不一致，请确认是否继续发货！”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的板号
        if (loadCar.getEndSiteCode().intValue() != board.getDestinationId()) {
            log.warn("错发！请核实！板号与批次目的地不一致，请确认是否继续发货！taskId={},packageCode={},boardCode={},taskSite={}, boardSite={}",
                    taskId, packageCode, boardCode, loadCar.getEndSiteCode(), board.getDestinationId());
            msg = "该包裹所在板号共有" + result.getData().size() + "个包裹！\n" + "错发！请核实！板号与批次目的地不一致，请确认是否继续发货！";
            msgBox.setMsg(msg);
            response.addBox(msgBox);
            return response;
        }

        // 板流向一致，则提示板上包裹数量
        msg = "该包裹所在板号共有" + result.getData().size() + "个包裹，请确认实物数量正确后，再点击【确认】，转为板号装车。";
        msgBox.setMsg(msg);
        response.addBox(msgBox);
        return response;
    }

    @Override
    public JdVerifyResponse<Void> checkWaybillCode(GoodsLoadingScanningReq req, JdVerifyResponse<Void> response) {
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

        String waybillCode = WaybillUtil.getWaybillCode(packageCode);

        // 查询运单下的所有包裹
        List<String> packageCodes = waybillPackageManager.getWaybillPackageCodes(waybillCode);
        if (CollectionUtils.isEmpty(packageCodes)) {
            log.error("根据运单号查询包裹号列表返回空：taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据运单号查询包裹号列表返回空！");
            return response;
        }

        // 根据运单号查询库存是否有货
        List<LoadScanDto> scanDtoList = new ArrayList<>();
        LoadScanDto loadScan = new LoadScanDto();
        loadScan.setWayBillCode(waybillCode);
        scanDtoList.add(loadScan);
        List<LoadScanDto> loadScanDtoList = getLoadScanListByWaybillCode(scanDtoList, loadCar.getCreateSiteCode().intValue());
        if (CollectionUtils.isEmpty(loadScanDtoList)) {
            log.error("运单号从分拣报表查询运单库存信息返回空taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该运单所有包裹未验货或已发货，请核实运单状态");
            return response;
        }

        JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
        msgBox.setType(MsgBoxTypeEnum.CONFIRM);
        response.setCode(JdCResponse.CODE_CONFIRM);
        String msg;

        // 实时调用路由接口
        Integer nextDmsSiteId = waybillService.getRouterFromMasterDb(waybillCode, loadCar.getCreateSiteCode().intValue());
        log.info("实时调用路由接口结束taskId={},packageCode={},nextDmsSiteId={}", taskId, packageCode, nextDmsSiteId);

        // 校验运单下一动态路由节点与批次号下一场站是否一致，如不一致进行错发弹框提醒（“错发！请核实！此运单流向与发货流向不一致，请确认是否继续发货！  是  否  ”，特殊提示音），点击“确定”后完成发货，点击取消清空当前操作的包裹号；
        if (nextDmsSiteId == null || loadCar.getEndSiteCode().intValue() != nextDmsSiteId) {
            log.warn("运单下一动态路由节点与批次号下一场站不一致taskId={},packageCode={},waybillCode={},waybillNextSite={},taskEndSite={}", taskId, packageCode, waybillCode, nextDmsSiteId, loadCar.getEndSiteCode());
            msg = "大宗按单操作！此单共计" + packageCodes.size() + "件，请确认包裹集齐！\n" + "错发！请核实！运单号与批次目的地不一致，请确认是否继续发货！";
            msgBox.setMsg(msg);
            response.addBox(msgBox);
            return response;
        }

        // 运单流向一致，则提示运单上包裹数量
        msg = "大宗按单操作！此单共计" + packageCodes.size() + "件，请确认包裹集齐！\n";
        msgBox.setMsg(msg);
        response.addBox(msgBox);
        return response;
    }

    /**
     * 校验批次号并绑定任务
     */
    @Transactional(value = "main_loadunload", propagation = Propagation.REQUIRED)
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
        if (log.isDebugEnabled()) {
            log.debug("开始校验批次号--判断任务是否已经结束：taskId={},batchCode={}", taskId, batchCode);
        }
        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }

        // 如果批次号已绑定，直接返回
        if (StringUtils.isNotBlank(loadCar.getBatchCode())) {
            log.warn("该任务已经绑定过批次号且不允许修改，taskId={},batchCode={}", taskId, batchCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该任务已经绑定过批次号且不允许修改");
            return response;
        }

        try {
            // 校验批次号是否已被使用
            List<Long> taskIds = loadCarDao.findTaskByBatchCode(batchCode);
            if (CollectionUtils.isNotEmpty(taskIds)) {
                log.warn("该批次号已被其他任务绑定，请更换！taskId={},batchCode={}", taskId, batchCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("该批次号已被其他任务绑定，请更换！");
                return response;
            }
            // 根据批次号查询始发与目的网点信息
            CreateAndReceiveSiteInfo siteInfo = siteService.getCreateAndReceiveSiteBySendCode(batchCode);
            if (siteInfo == null) {
                log.warn("根据批次号没有找到对应的网点信息，taskId={},batchCode={}", taskId, batchCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("根据批次号没有找到对应的网点信息");
                return response;
            }

            Integer createSiteCode = siteInfo.getCreateSiteCode();
            Integer receiveSiteCode = siteInfo.getReceiveSiteCode();
            // 系统校验批次号的始发地与输入的始发场地是否一致，如果不一致则系统提示：“当前批次始发地与操作场地不一致，请核实批次号是否正确”
            if (createSiteCode == null || !createSiteCode.equals(loadCar.getCreateSiteCode().intValue())) {
                log.warn("当前批次始发地与操作场地不一致，请核实批次号是否正确，taskId={},batchCode={},batchSite={},taskSite={}",
                        taskId, batchCode, createSiteCode, loadCar.getCreateSiteCode().intValue());
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("当前批次始发地与操作场地不一致，请核实批次号是否正确");
                return response;
            }

            // 系统校验批次号的目的地与输入的目的场地是否一致，如果不一致则系统提示：“批次号目的地与目的场地不一致，请检查后重新扫描”
            if (receiveSiteCode == null || !receiveSiteCode.equals(loadCar.getEndSiteCode().intValue())) {
                log.warn("批次号目的地与目的场地不一致，请检查后重新扫描，taskId={},batchCode={},batchSite={},taskSite={}",
                        taskId, batchCode, receiveSiteCode, loadCar.getEndSiteCode().intValue());
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("批次号目的地与目的场地不一致，请检查后重新扫描");
                return response;
            }

            if (log.isDebugEnabled()) {
                log.debug("开始校验批次号是否已封车！，taskId={},batchCode={}", taskId, batchCode);
            }
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
    @Transactional(value = "main_loadunload", propagation = Propagation.REQUIRED)
    public JdCResponse<Void> saveLoadScanByPackCode(Long taskId, String waybillCode, String packageCode,
                                                     Integer goodsAmount, Integer transfer, Integer flowDisAccord,
                                                     User user, LoadCar loadCar) {
        JdCResponse<Void> response = new JdCResponse<>();
        if (log.isDebugEnabled()) {
            log.debug("常规包裹号后续校验--开始暂存前校验--是否属于重复扫：taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
        }
        try {
            // 获取锁
            if (!lock(taskId, waybillCode, null)) {
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
            GoodsLoadScanRecord loadScanRecord = goodsLoadScanRecordDao.findRecordByWaybillCodeAndPackCode(waybillCode, packageCode, loadCar.getCreateSiteCode());
            // 如果是重复扫，返回错误
            if (loadScanRecord != null && GoodsLoadScanConstants.GOODS_SCAN_LOAD.equals(loadScanRecord.getScanAction())) {
                response.setCode(JdCResponse.CODE_FAIL);
                if (!loadCar.getLicenseNumber().equals(loadScanRecord.getLicenseNumber())) {
                    log.warn("该包裹所属运单已装入{}车内，不能改装！taskId={},packageCode={},waybillCode={}",
                            loadScanRecord.getLicenseNumber(), taskId, packageCode, waybillCode);
                    response.setMessage("该包裹所属运单已装入" + loadScanRecord.getLicenseNumber() + "车内，不能改装！");
                    return response;
                }
                log.warn("该包裹号已扫描装车，请勿重复扫描！taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
                response.setMessage("该包裹号已扫描装车，请勿重复扫描！");
                return response;
            }
            if (log.isDebugEnabled()) {
                log.debug("常规包裹号后续校验--包裹不属于重复扫：taskId={},packageCode={},waybillCode={},flowDisAccord={}",
                        taskId, packageCode, waybillCode, flowDisAccord);
            }
            // 不属于重复扫,但被取消扫描过
            if (loadScanRecord != null) {
                loadScanRecord.setUpdateTime(new Date());
                loadScanRecord.setUpdateUserCode(user.getUserCode());
                loadScanRecord.setUpdateUserName(user.getUserName());
                loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
                loadScanRecord.setTaskId(loadCar.getId());
                loadScanRecord.setCreateSiteCode(loadCar.getCreateSiteCode());
                loadScanRecord.setCreateSiteName(loadCar.getCreateSiteName());
                loadScanRecord.setEndSiteCode(loadCar.getEndSiteCode());
                loadScanRecord.setEndSiteName(loadCar.getEndSiteName());
                loadScanRecord.setLicenseNumber(loadCar.getLicenseNumber());
                goodsLoadScanRecordDao.updateGoodsScanRecordById(loadScanRecord);
            } else {
                // 如果不是重复扫，包裹扫描记录表新增一条记录
                GoodsLoadScanRecord newLoadScanRecord = createGoodsLoadScanRecord(taskId, waybillCode, packageCode,
                        null, transfer, flowDisAccord, user, loadCar);

                goodsLoadScanRecordDao.insert(newLoadScanRecord);
            }

            if (log.isDebugEnabled()) {
                log.debug("常规包裹号后续校验--判断是否是第一个包裹开始：taskId={}", loadCar.getId());
            }
            // 扫描第一个包裹时，修改任务状态为已开始
            updateTaskStatus(loadCar, user);

            // 运单暂存表新增或修改
            GoodsLoadScan newLoadScan = createGoodsLoadScan(taskId, waybillCode, packageCode,
                    goodsAmount, flowDisAccord, user);
            GoodsLoadScan oldLoadScan = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(taskId, waybillCode);
            if (oldLoadScan == null) {
                // 设置重量和体积
                setWeightAndVolume(newLoadScan);
                goodsLoadScanDao.insert(newLoadScan);
            } else {
                computeAndUpdateLoadScan(oldLoadScan, goodsAmount, flowDisAccord);
                goodsLoadScanDao.updateByPrimaryKey(oldLoadScan);
            }

        } finally {
            // 释放锁
            unLock(taskId, waybillCode, null);
        }

        log.info("常规包裹号后续校验--暂存结束：taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);

        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 设置运单重量和体积
     * @param newLoadScan 运单暂存对象
     */
    private void setWeightAndVolume(GoodsLoadScan newLoadScan) {
        // 查询运单详情
        Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(newLoadScan.getWayBillCode());
        if (waybill != null) {
            Double weight = waybill.getAgainWeight();
            String volume = waybill.getSpareColumn2();
            log.info("设置运单重量和体积:taskId={},waybillCode={},复重={},复量方={}|原重={},原量方={}", newLoadScan.getTaskId(),
                    newLoadScan.getWayBillCode(), weight, volume, waybill.getGoodWeight(), waybill.getGoodVolume());
            // 复重：againWeight 无值则取重量：goodWeight
            newLoadScan.setWeight(weight == null ? waybill.getGoodWeight() : weight);
            // 复量方：spareColumn2 无值则取体积：goodVolume
            newLoadScan.setVolume(StringUtils.isBlank(volume) ? waybill.getGoodVolume() : Double.parseDouble(volume));
        } else {
            log.error("设置运单重量和体积--查询运单接口返回空:taskId={},waybillCode={}", newLoadScan.getTaskId(),
                    newLoadScan.getWayBillCode());
        }
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
        // 库存等于已装，强发清零
        if (loadScan.getLoadAmount().equals(goodsAmount)) {
            loadScan.setForceAmount(0);
        }
        if (log.isDebugEnabled()) {
            log.debug("包裹暂存--status={},flowDisAccord={}", status, flowDisAccord);
        }
        // 如果原来是黄颜色
        if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(loadScan.getStatus())) {
            // 无论有没有装齐仍然显示黄颜色
            status = GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW;
        }
        // 如果是多扫
        if (GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y.equals(flowDisAccord)) {
            // 仍然显示黄颜色
            status = GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW;
        }
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
        if (log.isDebugEnabled()) {
            log.debug("根据运单去ES获取数据,查询条件:currentSiteId={}开始", currentSiteId);
        }
        // 根据包裹号查找运单号
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity = loadScanPackageDetailService
                .findLoadScanList(scanDtoList, currentSiteId);
        if (baseEntity == null) {
            log.warn("根据运单号和包裹号条件列表去分拣报表查询运单明细接口返回空currentSiteId={}", currentSiteId);
            return new ArrayList<>();
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode())|| baseEntity.getData() == null) {
            log.error("根据运单号和包裹号条件列表去分拣报表查询运单明细接口失败currentSiteId={}", currentSiteId);
            return new ArrayList<>();
        }
        if (log.isDebugEnabled()) {
            log.debug("根据运单去ES获取数据,查询条件:currentSiteId={}结束，返回包裹暂存接口逻辑继续.", currentSiteId);
        }
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
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
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
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            log.error("根据运单号和包裹号去分拣报表查询流向接口失败packageCode={},waybillCode={},code={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode(), baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    /**
     * 根据运单号、当前网点、已装包裹号查询未装包裹号列表
     *
     * @param waybillCode 运单号
     * @param createSiteId 当前网点ID
     * @param packageCodes 已装包裹号列表
     * @return 未装包裹号列表
     */
    public List<String> getUnloadPackageCodesByWaybillCode(String waybillCode, Integer createSiteId,
                                                           List<String> packageCodes) {
        com.jd.ql.dms.report.domain.BaseEntity<List<String>> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
        } catch (Exception e) {
            log.error("根据已装包裹号列表和运单号去分拣报表查询未装包裹号列表发生异常waybillCode={},createSiteId={},e=",
                    waybillCode, createSiteId, e);
            return new ArrayList<>();
        }

        if (baseEntity == null) {
            log.warn("根据已装包裹号列表和运单号去分拣报表查询未装包裹号列表发生异常waybillCode={},createSiteId={}",
                    waybillCode, createSiteId);
            return new ArrayList<>();
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            log.error("根据已装包裹号列表和运单号去分拣报表查询未装包裹号列表失败waybillCode={},createSiteId={},code={}",
                    waybillCode, createSiteId, baseEntity.getCode());
            return new ArrayList<>();
        }
        return baseEntity.getData();
    }


    private List<GoodsDetailDto> transformData(List<LoadScanDto> list, Map<String, GoodsLoadScan> map,
                                               Map<String, LoadScanDto> flowDisAccordMap, LoadScanDetailDto scanDetailDto) {
        List<GoodsDetailDto> goodsDetails = new ArrayList<>();
        BigDecimal totalWeight = new BigDecimal("0");
        BigDecimal totalVolume = new BigDecimal("0");
        BigDecimal weight;
        BigDecimal volume;
        int totalPackageNum = 0;

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

            // 累计运单总重量
            if (loadScan.getWeight() != null) {
                weight = new BigDecimal(Double.toString(loadScan.getWeight()));
                totalWeight = totalWeight.add(weight);
            }
            // 累计运单总体积
            if (loadScan.getVolume() != null) {
                volume = new BigDecimal(Double.toString(loadScan.getVolume()));
                totalVolume = totalVolume.add(volume);
            }
            // 累计所有已操作运单已装包裹数
            totalPackageNum = totalPackageNum + loadScan.getLoadAmount();

            // 重新计算颜色
            Integer status = getWaybillStatus(detailDto.getGoodsAmount(), loadScan.getLoadAmount(),
                    goodsDetailDto.getUnloadAmount(), loadScan.getForceAmount());
            goodsDetailDto.setStatus(status);
            // 多扫仍然显示黄色
            if (flowDisAccordMap.get(detailDto.getWayBillCode()) != null) {
                goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
                // 如果包裹已装等于库存显示绿色
                if (loadScan.getLoadAmount().equals(detailDto.getGoodsAmount())) {
                    goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN);
                }
                // 多扫如果强制下发显示橙色
                if (loadScan.getForceAmount() != null && loadScan.getForceAmount() > 0) {
                    goodsDetailDto.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE);
                }
            }
            goodsDetails.add(goodsDetailDto);
        }
        scanDetailDto.setTotalWeight(totalWeight.doubleValue());
        scanDetailDto.setTotalVolume(totalVolume.doubleValue());
        scanDetailDto.setTotalPackageNum(totalPackageNum);
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
        loadScanRecord.setForceStatus(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_N);

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
        loadScanRecord.setLicenseNumber(loadCar.getLicenseNumber());

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
            if (log.isDebugEnabled()) {
                log.debug("【运单号不在任务列表内的，且此运单本场地已操作验货】|此类包裹为多扫包裹，正常记录的统计表中，"
                        + "底色标位黄色taskId={},packageCode={},waybillCode={}", taskId, packageCode, waybillCode);
            }
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


    private List<LoadScanDto> getWaybillCodes(List<GoodsLoadScan> scans, Map<String, GoodsLoadScan> map,
                                         Map<String, LoadScanDto> flowDisAccordMap) {
        List<LoadScanDto> list = new ArrayList<>();
        LoadScanDto scanDto;
        for (GoodsLoadScan scan : scans) {
            // 如果之前已装大于0，后来都被取消了，那就不显示
            if (scan.getLoadAmount() <= 0) {
                continue;
            }
            scanDto = new LoadScanDto();
            scanDto.setWayBillCode(scan.getWayBillCode());
            list.add(scanDto);
            // 筛选出属于多扫的
            if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(scan.getStatus())) {
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
     * @param loadCar 任务
     * @param user 操作人
     */
    public void updateTaskStatus(LoadCar loadCar, User user) {
        // 扫描第一个包裹时，将任务状态改为已开始
        LoadCar car = loadCarDao.findLoadCarByTaskId(loadCar.getId());
        if (car != null && GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BLANK.equals(car.getStatus())) {
            loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_BEGIN);
            loadCar.setOperateUserErp(user.getUserErp());
            loadCar.setOperateUserName(user.getUserName());
            loadCar.setUpdateTime(new Date());
            loadCarDao.updateLoadCarById(loadCar);
            loadScanCacheService.setTaskLoadScan(loadCar);
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
    public Board getBoardCodeByPackageCode(Integer siteCode, String packageCode) {

        Response<Board> tcResponse = boardCombinationService.getBoardByBoxCode(siteCode, packageCode);

        if (tcResponse == null) {
            log.error("根据包裹号查询板号返回结果为空！packageCode={},siteCode={}", packageCode, siteCode);
            return null;
        }
        if (JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())) {
            //查询成功
            return tcResponse.getData();
        }
        if (log.isDebugEnabled()) {
            log.debug("根据包裹号没有查询到板号！packageCode={},siteCode={},error={}", packageCode, siteCode, tcResponse.getMesseage());
        }
        return null;
    }

    private boolean lock(Long taskId, String waybillCode, String boardCode) {
        String lockKey = LOADS_CAN_LOCK_BEGIN + taskId + "_" + getLockFlag(waybillCode, boardCode);
        log.info("开始获取锁lockKey={}", lockKey);
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

    public boolean saveOrUpdate(GoodsLoadScan e, Integer goodsAmount, User user, Integer flowDisAccord) {

        GoodsLoadScan oldData = goodsLoadScanDao.findLoadScanByTaskIdAndWaybillCode(e.getTaskId(), e.getWayBillCode());
        e.setUpdateTime(new Date());
        e.setUpdateUserCode(user.getUserCode());
        e.setUpdateUserName(user.getUserName());
        if (oldData != null) {
            e.setLoadAmount(oldData.getLoadAmount() + e.getLoadAmount());
            e.setUnloadAmount(goodsAmount - e.getLoadAmount());
            Integer status = getWaybillStatus(goodsAmount, e.getLoadAmount(), e.getUnloadAmount(),
                    oldData.getForceAmount());
            e.setStatus(status);
            e.setForceAmount(oldData.getForceAmount());
            // 如果原来是黄颜色
            if (GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW.equals(oldData.getStatus())) {
                // 无论有没有没装齐仍然显示黄颜色
                e.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }
            // 如果是多扫
            if (flowDisAccord != null && flowDisAccord == 1) {
                e.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW);
            }
            // 库存等于已装，强发清零
            if (e.getLoadAmount().equals(goodsAmount)) {
                e.setForceAmount(0);
            }
            e.setId(oldData.getId());
            return goodsLoadScanDao.updateByPrimaryKey(e);
        } else {
            // 设置重量和体积
            setWeightAndVolume(e);
            e.setCreateTime(new Date());
            e.setCreateUserCode(user.getUserCode());
            e.setCreateUserName(user.getUserName());
            return goodsLoadScanDao.insert(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, value = "main_loadunload")
    public void deleteLoadScanByTaskId(Long taskId) {
        if (CollectionUtils.isNotEmpty(goodsLoadScanDao.loadScanRecordIsExist(taskId))) {
            goodsLoadScanDao.deleteLoadScanByTaskId(taskId);
        }
        if(CollectionUtils.isNotEmpty(goodsLoadScanRecordDao.loadScanRecordIsExist(taskId))){
            goodsLoadScanRecordDao.deleteLoadScanRecordByTaskId(taskId);
        }
    }

    @Override
    public JdCResponse<List<String>> findUnloadPackages(GoodsLoadingScanningReq req, JdCResponse<List<String>> response) {
        Long taskId = req.getTaskId();
        String waybillCode = req.getWayBillCode();
        log.info("开始查询未装包裹明细列表！taskId={},waybillCode={}", taskId, waybillCode);
        // 根据任务号查询装车任务记录
        LoadCar loadCar = loadCarDao.findLoadCarByTaskId(taskId);
        if (loadCar == null) {
            log.error("根据任务号找不到对应的装车任务，taskId={},waybillCode={}", taskId, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("根据任务号找不到对应的装车任务");
            return response;
        }
        // 任务是否已经结束
        if (GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END.equals(loadCar.getStatus())) {
            log.error("该装车任务已经结束，taskId={},waybillCode={}", taskId, waybillCode);
            response.setCode(JdCResponse.CODE_FAIL);
            response.setMessage("该装车任务已经结束");
            return response;
        }
        List<String> unloadPackages;
        try {
            // 获取锁
            if (!lock(taskId, waybillCode, null)) {
                log.info("未装包裹明细--获取锁失败：taskId={},waybillCode={}", taskId, waybillCode);
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage("多人同时查看该运单下未装包裹明细，请稍后重试！");
                return response;
            }
            // 从包裹记录表查询该运单下所有已装车的包裹
            List<String> packageCodes = goodsLoadScanRecordDao.selectPackageCodesByWaybillCode(taskId, waybillCode);
            // 从ES中根据已装车包裹筛选出未装包裹列表
            unloadPackages = getUnloadPackageCodesByWaybillCode(waybillCode,
                    loadCar.getCreateSiteCode().intValue(), packageCodes);
        } finally {
            // 释放锁
            unLock(taskId, waybillCode, null);
        }
        response.setCode(JdCResponse.CODE_SUCCESS);
        response.setData(unloadPackages);
        return response;
    }

}
