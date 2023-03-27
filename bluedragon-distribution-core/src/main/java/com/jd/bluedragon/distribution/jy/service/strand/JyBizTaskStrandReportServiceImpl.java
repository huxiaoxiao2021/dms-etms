package com.jd.bluedragon.distribution.jy.service.strand;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.*;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.attachment.JyAttachmentDetailDao;
import com.jd.bluedragon.distribution.jy.dao.strand.JyBizStrandReportDetailDao;
import com.jd.bluedragon.distribution.jy.dao.strand.JyBizTaskStrandReportDao;
import com.jd.bluedragon.distribution.jy.dto.JyBusinessException;
import com.jd.bluedragon.distribution.jy.dto.strand.JyStrandTaskPageCondition;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 拣运app-滞留上报接口实现
 *
 * @author hujiping
 * @date 2023/3/27 4:04 PM
 */
@Service("jyBizTaskStrandReportService")
public class JyBizTaskStrandReportServiceImpl implements JyBizTaskStrandReportService {

    private final Logger logger = LoggerFactory.getLogger(JyBizTaskStrandReportServiceImpl.class);

    @Autowired
    private IGenerateObjectId genObjectId;
    
    @Autowired
    private JyBizTaskStrandReportDao jyBizTaskStrandReportDao;

    @Autowired
    private JyBizStrandReportDetailDao jyBizStrandReportDetailDao;

    @Autowired
    private JyAttachmentDetailDao jyAttachmentDetailDao;

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    private SortingDao sortingDao;

    @Autowired
    private BoxService boxService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private StrandService strandService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    @Qualifier("jyStrandScanContainerDealProducer")
    private DefaultJMQProducer jyStrandScanContainerDealProducer;

    @Autowired
    private TaskService taskService;

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.artificialCreateStrandReportTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> artificialCreateStrandReportTask(JyStrandReportTaskCreateReq request) {
        request.setTaskType(JyBizStrandTaskTypeEnum.ARTIFICIAL.getCode());
        return createStrandReportTask(request);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.systemCreateStrandReportTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> systemCreateStrandReportTask(JyStrandReportTaskCreateReq request) {
        request.setTaskType(JyBizStrandTaskTypeEnum.TRANS_REJECT_SYSTEM.getCode());
        return createStrandReportTask(request);
    }

    private InvokeResult<Void> createStrandReportTask(JyStrandReportTaskCreateReq request) {
        InvokeResult<Void> result = paramsCheck(request);
        if(!result.codeSuccess()){
            return result;
        }
        // 新增任务表数据
        request.setBizId(generateBiz());
        insertTask(request, JyBizStrandTaskTypeEnum.ARTIFICIAL.getCode());
        // 新增附件数据
        insertAnnex(request);
        // todo 推送延时任务
//        addDelayTask();
        return result;
    }

    private void insertAnnex(JyStrandReportTaskCreateReq request) {
        List<String> proveUrlList = request.getProveUrlList();
        if(CollectionUtils.isNotEmpty(proveUrlList)){
            List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
            for (String proveUrl : proveUrlList) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                annexEntity.setBizId(request.getBizId());
                annexEntity.setSiteCode(request.getSiteCode());
                annexEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                annexEntity.setCreateUserErp(request.getOperateUserErp());
                annexEntity.setUpdateUserErp(request.getOperateUserErp());
                annexEntity.setAttachmentUrl(proveUrl);
                annexList.add(annexEntity);
            }
            jyAttachmentDetailDao.batchInsert(annexList);
        }
    }

    private void insertTask(JyStrandReportTaskCreateReq request, Integer taskType) {
        JyBizTaskStrandReportEntity entity = new JyBizTaskStrandReportEntity();
        entity.setBizId(request.getBizId());
        entity.setTransportRejectBiz(request.getTransportRejectBiz());
        entity.setWaveTime(request.getWaveTime());
        entity.setSiteCode(request.getSiteCode());
        entity.setSiteName(request.getSiteName());
        entity.setStrandCode(request.getStrandCode());
        entity.setStrandReason(request.getStrandReason());
        entity.setNextSiteCode(request.getNextSiteCode());
        entity.setNextSiteName(request.getNextSiteName());
        entity.setTaskType(taskType);
        entity.setTaskStatus(JyBizStrandTaskStatusEnum.TODO.getCode());
        entity.setCreateUserErp(request.getOperateUserErp());
        entity.setCreateUserName(request.getOperateUserName());
        Long sysCloseTime = uccPropertyConfiguration.getJySysStrandTaskCloseTime();
        Long artificialCloseTime = uccPropertyConfiguration.getJyArtificialStrandTaskCloseTime();
        entity.setExpectCloseTime(new Date(System.currentTimeMillis() +
                (Objects.equals(taskType, JyBizStrandTaskTypeEnum.ARTIFICIAL.getCode()) ? artificialCloseTime : sysCloseTime)));
        jyBizTaskStrandReportDao.insert(entity);
    }

    private InvokeResult<Void> paramsCheck(JyStrandReportTaskCreateReq request) {
        InvokeResult<Void> result = new InvokeResult<>();
        if(request == null){
            result.parameterError();
            return result;
        }
        if(request.getSiteCode() == null){
            result.parameterError("参数错误-场地不能为空!");
            return result;
        }
        if(StringUtils.isEmpty(request.getOperateUserErp())){
            result.parameterError("参数错误-操作人ERP不能为空!");
            return result;
        }
        if(request.getStrandCode() == null || StringUtils.isEmpty(request.getStrandReason())){
            result.parameterError("参数错误-滞留原因不能为空!");
            return result;
        }
        if(request.getNextSiteCode() == null || StringUtils.isEmpty(request.getNextSiteName())){
            result.parameterError("参数错误-下级流向不能为空!");
            return result;
        }
        if(Objects.equals(request.getTaskType(), JyBizStrandTaskTypeEnum.ARTIFICIAL.getCode()) 
                && CollectionUtils.isEmpty(request.getProveUrlList())){
            result.parameterError("参数错误-证明照片不能为空!");
            return result;
        }
        return result;
    }

    private String generateBiz() {
        // 生成自定义业务主键
        return Constants.JY_BIZ_TASK_STRAND_PREFIX.concat(StringHelper.padZero(genObjectId.getObjectId(JyBizTaskStrandReportEntity.class.getName()),8));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.cancelStrandReportTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> cancelStrandReportTask(JyStrandReportTaskCreateReq request) {
        InvokeResult<Void> result = new InvokeResult<>();
        if(request == null || request.getBizId() == null){
            result.parameterError("参数错误-请传入合法的bizId!");
            return result;
        }
        // 任务校验
        strandTaskCheck(request.getBizId(), result);
        if(!result.codeSuccess()){
            return result;
        }
        JyBizTaskStrandReportEntity updateEntity = new JyBizTaskStrandReportEntity();
        updateEntity.setBizId(request.getBizId());
        updateEntity.setTaskStatus(JyBizStrandTaskStatusEnum.CANCEL.getCode());
        updateEntity.setUpdateUserErp(request.getOperateUserErp());
        jyBizTaskStrandReportDao.updateStatus(updateEntity);
        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.queryStrandReason",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<ConfigStrandReasonData>> queryStrandReason() {
        return strandService.queryJyStrandReasons();
    }
    
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.queryStrandScanType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<JyBizStrandScanTypeEnum>> queryStrandScanType() {
        InvokeResult<List<JyBizStrandScanTypeEnum>> result = new InvokeResult<>();
        result.setData(Arrays.asList(JyBizStrandScanTypeEnum.values()));
        return result;
    }

    @Override
    public InvokeResult<JyStrandReportScanResp> strandScan(JyStrandReportScanReq scanRequest) {
        CallerInfo info = Profiler.registerInfo("dmsWeb.JyBizTaskStrandReportService.strandScan",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<JyStrandReportScanResp> result = scanParamsCheck(scanRequest);
        if(!result.codeSuccess()){
            return result;
        }
        // 防重key
        String key = String.format(CacheKeyConstants.CACHE_KEY_JY_STRAND_SCAN, 
                scanRequest.getBizId().concat(Constants.SEPARATOR_HYPHEN).concat(scanRequest.getScanBarCode()));
        try {
            // 防重校验
            repeatCheck(key);
            // 存在性校验
            existCheck(scanRequest);
            // 数量限制
            scanNumLimit(scanRequest.getBizId());
            // 获取扫描条码归属容器及容器内数量
            ImmutablePair<String, Integer> containerPair = queryBelongContainer(scanRequest);
            // 新增明细表数据
            insertDetailRecord(scanRequest, containerPair);
            // 获取已扫明细
            result.setData(queryScanDetail(scanRequest));
        }catch (JyBusinessException e){
            result.error(e.getMessage());
            Profiler.functionError(info);
        }catch (Exception e){
            logger.error("任务:{}下条码:{}的滞留扫描操作异常!", scanRequest.getBizId(), scanRequest.getScanBarCode(), e);
            result.error("扫描异常,请联系分拣小秘!");
            Profiler.functionError(info);
        }finally {
            releaseCache(key);
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    private void scanNumLimit(String bizId) {
        // 拣运-滞留扫描数量上线
        Integer jyStrandScanNumLimit = uccPropertyConfiguration.getJyStrandScanNumLimit();
        Integer totalScanNum = jyBizStrandReportDetailDao.queryTotalScanNum(bizId);
        if(totalScanNum >= jyStrandScanNumLimit){
            throw new JyBusinessException("当前任务已扫数量超限,请更换任务扫描!");
        }
    }

    private JyStrandReportScanResp queryScanDetail(JyStrandReportScanReq scanRequest) {
        String bizId = scanRequest.getBizId();
        JyStrandReportScanResp jyStrandReportScanResp = new JyStrandReportScanResp();
        jyStrandReportScanResp.setBizId(bizId);
        Integer total = jyBizStrandReportDetailDao.queryTotalScanNum(bizId);
        if(total > 0){
            List<JyStrandReportScanVO> scanVOList = Lists.newArrayList();
            Map<String, Object> paramsMap = Maps.newHashMap();
            paramsMap.put("bizId", bizId);
            paramsMap.put("offset", 0);
            paramsMap.put("pageSize", 10);
            List<JyBizStrandReportDetailEntity> detailList = jyBizStrandReportDetailDao.queryPageListByCondition(paramsMap);
            for (JyBizStrandReportDetailEntity temp : detailList) {
                JyStrandReportScanVO jyStrandReportScanVO = new JyStrandReportScanVO();
                jyStrandReportScanVO.setScanType(temp.getScanType());
                jyStrandReportScanVO.setScanBarCode(temp.getScanBarCode());
                jyStrandReportScanVO.setContainerCode(temp.getContainerCode());
                jyStrandReportScanVO.setContainerInnerCount(temp.getContainerInnerCount());
                scanVOList.add(jyStrandReportScanVO);
            }
            jyStrandReportScanResp.setScanNum(jyBizStrandReportDetailDao.queryTotalInnerScanNum(bizId));
            jyStrandReportScanResp.setScanVOList(scanVOList);
        }else {
            jyStrandReportScanResp.setScanNum(Constants.NUMBER_ZERO);
            jyStrandReportScanResp.setScanVOList(Lists.newArrayList());
        }
        return jyStrandReportScanResp;
    }

    private void existCheck(JyStrandReportScanReq scanRequest) {
        JyBizStrandReportDetailEntity condition = new JyBizStrandReportDetailEntity();
        condition.setBizId(scanRequest.getBizId());
        condition.setScanBarCode(scanRequest.getScanBarCode());
        JyBizStrandReportDetailEntity detailEntity = jyBizStrandReportDetailDao.queryOneByCondition(condition);
        if(detailEntity != null && Objects.equals(detailEntity.getIsCancel(), Constants.NUMBER_ZERO)){
            throw new JyBusinessException("当前条码:" + scanRequest.getScanBarCode() + "已扫描,请勿重复扫描!");
        }
    }

    private void releaseCache(String key) {
        try {
            jimdbCacheService.del(key);
        }catch (Exception e){
            logger.error("释放锁:{}异常!", key, e);
        }
    }

    private void repeatCheck(String key) {
        boolean isSuc = jimdbCacheService.setNx(key, Constants.NUMBER_ONE, 1, TimeUnit.SECONDS);
        if(!isSuc){
            throw new JyBusinessException("当前扫描条码正在处理,请勿重复操作!");
        }
    }

    private void insertDetailRecord(JyStrandReportScanReq scanRequest, ImmutablePair<String, Integer> containerPair) {
        JyBizStrandReportDetailEntity entity = new JyBizStrandReportDetailEntity();
        entity.setBizId(scanRequest.getBizId());
        entity.setScanBarCode(scanRequest.getScanBarCode());
        entity.setContainerCode(containerPair.left);
        entity.setContainerInnerCount(containerPair.right);
        entity.setScanType(scanRequest.getScanType());
        entity.setIsCancel(Constants.NUMBER_ZERO);
        if(StringUtils.isNotEmpty(scanRequest.getPositionCode())){
            Result<PositionDetailRecord> positionResult = positionQueryJsfManager.queryOneByPositionCode(scanRequest.getPositionCode());
            if (positionResult != null && positionResult.getData() != null) {
                entity.setGridCode(positionResult.getData().getGridCode());
            }
        }
        entity.setSiteCode(scanRequest.getSiteCode());
        entity.setSiteName(scanRequest.getSiteName());
        entity.setCreateUserErp(scanRequest.getOperateUserErp());
        entity.setUpdateUserErp(scanRequest.getOperateUserErp());
        jyBizStrandReportDetailDao.insert(entity);
    }

    /**
     * 根据扫描条码获取归属容器
     *  left：归属容器
     *  right：容器内扫描件数量
     * @param scanRequest
     * @return
     */
    private ImmutablePair<String, Integer> queryBelongContainer(JyStrandReportScanReq scanRequest) {
        JyBizTaskStrandReportEntity taskEntity = jyBizTaskStrandReportDao.queryOneByBiz(scanRequest.getBizId());
        // 下级流向
        Integer nextSiteCode = taskEntity.getNextSiteCode();
        switch (JyBizStrandScanTypeEnum.queryEnumByCode(scanRequest.getScanType())){
            case PACK:
            case WAYBILL:
                return queryPackOrWaybillInnerScanCount(scanRequest, nextSiteCode);
            case BOX:
                return queryBoxInnerScanCount(scanRequest, nextSiteCode);
            case BOARD:
                return queryBoardInnerScanCount(scanRequest, scanRequest.getSiteCode());
            default:
                throw new JyBusinessException("扫货方式不合法!");
        }
    }

    private ImmutablePair<String, Integer> queryPackOrWaybillInnerScanCount(JyStrandReportScanReq scanRequest, Integer nextSiteCode) {
        String waybillCode = WaybillUtil.getWaybillCode(scanRequest.getScanBarCode());
        String containerCode = Objects.equals(scanRequest.getScanType(), JyBizStrandScanTypeEnum.PACK.getCode()) 
                ? scanRequest.getScanBarCode() : waybillCode;
        Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
        if(waybill == null || !Objects.equals(waybill.getOldSiteId(), nextSiteCode)){
            throw new JyBusinessException("扫描条码所属容器的流向和任务流向不一致!");
        }
        return ImmutablePair.of(containerCode, waybill.getGoodNumber() == null ? Constants.NUMBER_ZERO : waybill.getGoodNumber());
    }

    /**
     * 查询板内扫描件数量
     * 
     * @param scanRequest
     * @param nextSiteCode
     * @return
     */
    private ImmutablePair<String, Integer> queryBoardInnerScanCount(JyStrandReportScanReq scanRequest, Integer nextSiteCode) {
        CallerInfo info = Profiler.registerInfo("dmsWeb.JyBizTaskStrandReportService.queryBoardInnerScanCount",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        String packOrBoxCode = scanRequest.getScanBarCode();
        Integer siteCode = scanRequest.getSiteCode();
        try {
            Response<Board> response = groupBoardManager.getBoardByBoxCode(packOrBoxCode, siteCode);
            if(response != null && response.getData() != null && StringUtils.isNotEmpty(response.getData().getCode())){
                String boardCode = response.getData().getCode();
                if(!Objects.equals(response.getData().getDestinationId(), nextSiteCode)){
                    throw new JyBusinessException("扫描单所属板的流向和任务目的地不一致!");
                }
                Integer boardInnerCount = groupBoardManager.getBoardBoxCount(boardCode, siteCode);
                return ImmutablePair.of(boardCode, boardInnerCount == null ? Constants.NUMBER_ZERO : boardInnerCount);
            }
        }catch (JyBusinessException e){
            throw e;   
        }catch (Exception e){
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        throw new JyBusinessException("未查询单号:" + packOrBoxCode + "对应的板号!");
    }

    /**
     * 查询箱内扫描件数量
     * 
     * @param scanRequest
     * @param nextSiteCode
     * @return
     */
    private ImmutablePair<String, Integer> queryBoxInnerScanCount(JyStrandReportScanReq scanRequest, Integer nextSiteCode) {
        String boxCode = scanRequest.getScanBarCode();
        Box box = boxService.findBoxByCode(boxCode);
        if(box == null){
            throw new JyBusinessException("未查询单号:" + boxCode + "对应的箱!");
        }
        if(!Objects.equals(box.getReceiveSiteCode(), nextSiteCode)){
            throw new JyBusinessException("箱号的目的地和任务流向不一致!");
        }
        List<String> keywords = new ArrayList<String>();
        keywords.add(boxCode);
        List<Integer> siteCodes = kvIndexDao.queryByKeywordSet(keywords);
        if(CollectionUtils.isEmpty(siteCodes)){
            throw new JyBusinessException("未查询单号:" + boxCode + "对应的箱!");
        }
        Integer createSiteCode = siteCodes.get(0);
        Sorting sortingCondition = new Sorting();
        sortingCondition.setCreateSiteCode(createSiteCode);
        sortingCondition.setBoxCode(boxCode);
        List<Sorting> sortingList = sortingDao.findByBoxCode(sortingCondition);
        if(CollectionUtils.isEmpty(sortingList)){
            throw new JyBusinessException("箱内没有明细!");
        }
        return ImmutablePair.of(boxCode, sortingList.size());
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.cancelStrandScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<JyStrandReportScanResp> cancelStrandScan(JyStrandReportScanReq request) {
        InvokeResult<JyStrandReportScanResp> result = new InvokeResult<>();
        if(request == null || StringUtils.isEmpty(request.getBizId()) 
                || StringUtils.isEmpty(request.getScanBarCode()) || StringUtils.isEmpty(request.getContainerCode())){
            result.parameterError();
            return result;
        }
        // 任务校验
        strandTaskCheck(request.getBizId(), result);
        if(!result.codeSuccess()){
            return result;
        }
        JyBizStrandReportDetailEntity condition = new JyBizStrandReportDetailEntity();
        condition.setBizId(request.getBizId());
        condition.setScanBarCode(request.getScanBarCode());
        condition.setContainerCode(request.getContainerCode());
        JyBizStrandReportDetailEntity entity = jyBizStrandReportDetailDao.queryOneByCondition(condition);
        if(entity == null){
            logger.warn("未查询bizId:{},barCode:{},containerCode:{}的扫描记录!", request.getBizId(), request.getScanBarCode(), request.getContainerCode());
            result.error("未查询到单号:" + request.getScanBarCode() + "的扫描记录,请联系分拣小秘!");
            return result;
        }
        if(Objects.equals(entity.getIsCancel(), Constants.CONSTANT_NUMBER_ONE)){
            logger.warn("单号:{}的扫描记录已被{}取消!", entity.getScanBarCode(), entity.getUpdateUserErp());
            return result;
        }
        JyBizStrandReportDetailEntity cancelEntity = new JyBizStrandReportDetailEntity();
        cancelEntity.setBizId(request.getBizId());
        cancelEntity.setScanBarCode(request.getScanBarCode());
        cancelEntity.setContainerCode(request.getContainerCode());
        cancelEntity.setUpdateUserErp(request.getOperateUserErp());
        jyBizStrandReportDetailDao.cancel(cancelEntity);
        // 获取已扫明细
        result.setData(queryScanDetail(request));
        return result;
    }

    private InvokeResult<JyStrandReportScanResp> scanParamsCheck(JyStrandReportScanReq request) {
        InvokeResult<JyStrandReportScanResp> result = new InvokeResult<>();
        if(request == null){
            result.parameterError();
            return result;
        }
        if(StringUtils.isEmpty(request.getBizId())){
            result.parameterError("参数错误-业务主键bizId不能为空!");
            return result;
        }
        if(request.getSiteCode() == null){
            result.parameterError("参数错误-场地不能为空!");
            return result;
        }
        if(StringUtils.isEmpty(request.getOperateUserErp())){
            result.parameterError("参数错误-操作人ERP不能为空!");
            return result;
        }
        // 扫描条码校验
        if(!scanBarCodeCheck(request, result)){
            return result;
        }
        // 任务校验
        strandTaskCheck(request.getBizId(), result);
        if(!result.codeSuccess()){
           return result; 
        }
        return result;
    }

    private <T> void strandTaskCheck(String bizId, InvokeResult<T> result) {
        JyBizTaskStrandReportEntity taskEntity = jyBizTaskStrandReportDao.queryOneByBiz(bizId);
        if(taskEntity == null){
            result.error("当前任务不存在,请联系分拣小秘!");
            return;
        }
        if(Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.CANCEL.getCode())){
            result.error("当前任务已被:" + taskEntity.getUpdateUserErp() + "取消!");
            return;
        }
        if(Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.COMPLETE.getCode())
                || Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.OVER_TIME.getCode())){
            result.error("当前任务已完成!");
        }
    }

    private boolean scanBarCodeCheck(JyStrandReportScanReq request, InvokeResult<JyStrandReportScanResp> result) {
        if(Objects.equals(JyBizStrandScanTypeEnum.queryEnumByCode(request.getScanType()), JyBizStrandScanTypeEnum.UNKNOWN)){
            result.parameterError("参数错误-扫货方式不合法!");
            return false;
        }
        if(StringUtils.isEmpty(request.getScanBarCode())){
            result.parameterError("参数错误-扫描条码不能为空!");
            return false;
        }
        if(!WaybillUtil.isPackageCode(request.getScanBarCode()) && !WaybillUtil.isWaybillCode(request.getScanBarCode())
                && !BusinessHelper.isBoxcode(request.getScanBarCode()) && !BusinessUtil.isBoardCode(request.getScanBarCode())){
            result.parameterError("参数错误-扫描条码不合法!");
            return false;
        }
        if(Objects.equals(request.getScanType(), JyBizStrandScanTypeEnum.PACK.getCode())
                && !WaybillUtil.isPackageCode(request.getScanBarCode())){
            result.error("扫货方式【按包裹】只能扫描包裹号!");
            return false;
        }
        if(Objects.equals(request.getScanType(), JyBizStrandScanTypeEnum.WAYBILL.getCode())
                && !WaybillUtil.isPackageCode(request.getScanBarCode()) && !WaybillUtil.isWaybillCode(request.getScanBarCode())){
            result.error("扫货方式【按运单】只能扫描包裹号/运单号!");
            return false;
        }
        if(Objects.equals(request.getScanType(), JyBizStrandScanTypeEnum.BOX.getCode())
                && !BusinessHelper.isBoxcode(request.getScanBarCode())){
            result.error("扫货方式【按箱】只能扫描箱号!");
            return false;
        }
        if(Objects.equals(request.getScanType(), JyBizStrandScanTypeEnum.BOARD.getCode())
                && (!WaybillUtil.isPackageCode(request.getScanBarCode()) && !BusinessHelper.isBoxcode(request.getScanBarCode()))){
            result.error("扫货方式【按板】只能扫描包裹号/箱号!");
            return false;
        }
        return true;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.strandReportSubmit",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> strandReportSubmit(JyStrandReportScanReq scanRequest) {
        InvokeResult<Void> result = new InvokeResult<>();
        if(scanRequest == null || StringUtils.isEmpty(scanRequest.getBizId())){
            result.parameterError();
            return result;
        }
        String bizId = scanRequest.getBizId();
        // 任务校验
        strandTaskCheck(bizId, result);
        if(!result.codeSuccess()){
           return result; 
        }
        // 分批获取明细异步处理
        Integer totalCount = jyBizStrandReportDetailDao.queryTotalScanNum(bizId);
        if(totalCount > 0){
            int pageNum = 0;
            int pageSize = 50;
            int loopCount = 0;
            while (loopCount < 100) {
                Map<String, Object> paramsMap = Maps.newHashMap();
                paramsMap.put("bizId", bizId);
                paramsMap.put("pageSize", pageSize);
                paramsMap.put("offset", pageNum * pageSize);
                List<JyBizStrandReportDetailEntity> pageList = jyBizStrandReportDetailDao.queryPageListByCondition(paramsMap);
                if(CollectionUtils.isEmpty(pageList)){
                    break;
                }
                List<Message> messageList = Lists.newArrayList();
                for (JyBizStrandReportDetailEntity entity : pageList) {
                    Message message = new Message();
                    message.setTopic(jyStrandScanContainerDealProducer.getTopic());
                    message.setText(JsonHelper.toJson(entity));
                    message.setBusinessId(entity.getBizId() + Constants.SEPARATOR_HYPHEN + entity.getContainerCode());
                    messageList.add(message);
                }
                if(logger.isInfoEnabled()){
                    logger.info("拣运-滞留扫描容器处理消息批量发送,content:{}", JsonHelper.toJson(pageList));
                }
                jyStrandScanContainerDealProducer.batchSendOnFailPersistent(messageList);
                if(pageList.size() < pageSize){
                    break;
                }
                pageNum ++;
                loopCount ++;
            }
        }
        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.queryStrandReportTaskPageList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<JyStrandReportTaskPageResp> queryStrandReportTaskPageList(JyStrandReportTaskPageReq pageReq) {
        InvokeResult<JyStrandReportTaskPageResp> result = new InvokeResult<>();
        if(pageReq == null || pageReq.getPageNo() == null 
                || pageReq.getPageSize() == null || pageReq.getSiteCode() == null){
            result.parameterError();
            return result;
        }
        JyStrandReportTaskPageResp jyStrandReportTaskPageResp = new JyStrandReportTaskPageResp();
        List<JyStrandReportTaskVO> pageList = Lists.newLinkedList();
        // 构建分页查询对象
        JyStrandTaskPageCondition pageCondition = convert2PageQueryCondition(pageReq);
        // 查询总数
        Integer total = jyBizTaskStrandReportDao.queryTotalCondition(pageCondition);
        if(NumberHelper.isPositiveNumber(total)){
            pageList.addAll(queryTaskPageList(pageCondition));
        }else {
            total = Constants.NUMBER_ZERO;
        }
        jyStrandReportTaskPageResp.setTotal(total);
        jyStrandReportTaskPageResp.setList(pageList);
        result.setData(jyStrandReportTaskPageResp);
        return result;
    }

    private List<JyStrandReportTaskVO> queryTaskPageList(JyStrandTaskPageCondition pageCondition) {
        List<JyStrandReportTaskVO> pageList = Lists.newLinkedList();
        List<JyBizTaskStrandReportEntity> list = jyBizTaskStrandReportDao.queryPageListByCondition(pageCondition);
        if(CollectionUtils.isNotEmpty(list)){
            long sysTime = System.currentTimeMillis();
            for (JyBizTaskStrandReportEntity temp : list) {
                JyStrandReportTaskVO jyStrandReportTaskVO = new JyStrandReportTaskVO();
                jyStrandReportTaskVO.setBizId(temp.getBizId());
                jyStrandReportTaskVO.setSiteCode(temp.getSiteCode());
                jyStrandReportTaskVO.setWaveTime(temp.getWaveTime());
                jyStrandReportTaskVO.setNextSiteCode(temp.getNextSiteCode());
                jyStrandReportTaskVO.setNextSiteName(temp.getNextSiteName());
                jyStrandReportTaskVO.setStrandCode(temp.getStrandCode());
                jyStrandReportTaskVO.setStrandReason(temp.getStrandReason());
                jyStrandReportTaskVO.setTaskStatus(temp.getTaskStatus());
                jyStrandReportTaskVO.setTaskType(temp.getTaskType());
                jyStrandReportTaskVO.setSystemTime(sysTime);
                jyStrandReportTaskVO.setTaskEndTime(temp.getExpectCloseTime() == null ? null : temp.getExpectCloseTime().getTime());
                jyStrandReportTaskVO.setTaskRemainingTime(jyStrandReportTaskVO.getTaskEndTime() - sysTime);
                jyStrandReportTaskVO.setScanNum(jyBizStrandReportDetailDao.queryTotalInnerScanNum(temp.getBizId()));
                pageList.add(jyStrandReportTaskVO);
            }
        }
        return pageList;
    }

    private JyStrandTaskPageCondition convert2PageQueryCondition(JyStrandReportTaskPageReq pageReq) {
        JyStrandTaskPageCondition pageCondition = new JyStrandTaskPageCondition();
        BeanUtils.copyProperties(pageReq, pageCondition);
        // 任务状态默认查'未完成'和'已完成'
        if(pageReq.getTaskStatus() == null){
            List<Integer> taskStatusList = Lists.newArrayList();
            taskStatusList.addAll(JyBizStrandTaskStatusEnum.unCompleteStatus);
            taskStatusList.addAll(JyBizStrandTaskStatusEnum.completeStatus);
            pageCondition.setTaskStatusList(taskStatusList);
        }
        // 任务类型默认查所有
        if(pageReq.getTaskType() == null){
            pageCondition.setTaskTypeList(JyBizStrandTaskTypeEnum.taskCodeList);
        }
        pageCondition.setOffset((pageCondition.getPageNo() -1) * pageCondition.getPageSize());
        return pageCondition;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.queryStrandReportTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<JyStrandReportTaskDetailVO> queryStrandReportTaskDetail(String bizId) {
        InvokeResult<JyStrandReportTaskDetailVO> result = new InvokeResult<>();
        if(StringUtils.isEmpty(bizId)){
            result.parameterError();
            return result;
        }
        JyBizTaskStrandReportEntity taskEntity = jyBizTaskStrandReportDao.queryOneByBiz(bizId);
        if(taskEntity == null){
            result.error("未查询到:" + bizId + "的任务数据,请联系分拣小秘!");
            return result;
        }
        JyStrandReportTaskDetailVO jyStrandReportTaskDetailVO = new JyStrandReportTaskDetailVO();
        jyStrandReportTaskDetailVO.setBizId(bizId);
        jyStrandReportTaskDetailVO.setSiteCode(taskEntity.getSiteCode());
        jyStrandReportTaskDetailVO.setSiteName(taskEntity.getSiteName());
        jyStrandReportTaskDetailVO.setCreateUserErp(taskEntity.getCreateUserErp());
        jyStrandReportTaskDetailVO.setCreateUserName(taskEntity.getCreateUserName());
        jyStrandReportTaskDetailVO.setStrandCode(taskEntity.getStrandCode());
        jyStrandReportTaskDetailVO.setStrandReason(taskEntity.getStrandReason());
        jyStrandReportTaskDetailVO.setNextSiteCode(taskEntity.getNextSiteCode());
        jyStrandReportTaskDetailVO.setNextSiteName(taskEntity.getNextSiteName());
        jyStrandReportTaskDetailVO.setProveUrlList(queryProveUrl(bizId, taskEntity.getSiteCode()));
        jyStrandReportTaskDetailVO.setSubmitTime(taskEntity.getSubmitTime() == null ? null : taskEntity.getSubmitTime().getTime());
        // 已扫容器内的扫描件数量
        Integer scanContainerInnerNum = jyBizStrandReportDetailDao.queryTotalInnerScanNum(bizId);
        jyStrandReportTaskDetailVO.setScanNum(scanContainerInnerNum == null ? Constants.NUMBER_ZERO : scanContainerInnerNum);
        // 已扫数量
        Integer scanNum = jyBizStrandReportDetailDao.queryTotalScanNum(bizId);
        if(scanNum > 0){
            // 已扫
            Map<String, Object> paramsMap = Maps.newHashMap();
            paramsMap.put("bizId", bizId);
            paramsMap.put("offset", 0);
            paramsMap.put("pageSize", 10);
            List<JyBizStrandReportDetailEntity> detailScanList = jyBizStrandReportDetailDao.queryPageListByCondition(paramsMap);
            if(CollectionUtils.isNotEmpty(detailScanList)){
                List<JyStrandReportScanVO> scanVOList = Lists.newArrayList();
                for (JyBizStrandReportDetailEntity temp : detailScanList) {
                    JyStrandReportScanVO jyStrandReportScanVO = new JyStrandReportScanVO();
                    jyStrandReportScanVO.setBizId(bizId);
                    jyStrandReportScanVO.setScanType(temp.getScanType());
                    jyStrandReportScanVO.setScanBarCode(temp.getScanBarCode());
                    jyStrandReportScanVO.setContainerCode(temp.getContainerCode());
                    jyStrandReportScanVO.setContainerInnerCount(temp.getContainerInnerCount());
                    scanVOList.add(jyStrandReportScanVO);
                }
                jyStrandReportTaskDetailVO.setScanVOList(scanVOList);
            }    
        }
        result.setData(jyStrandReportTaskDetailVO);
        return result;
    }

    private List<String> queryProveUrl(String bizId, Integer siteCode) {
        List<String> list = Lists.newArrayList();
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("bizId", bizId);
        paramsMap.put("siteCode", siteCode);
        paramsMap.put("offset", 0);
        paramsMap.put("pageSize", 100);
        List<JyAttachmentDetailEntity> attachmentList = jyAttachmentDetailDao.queryPageListByCondition(paramsMap);
        if(CollectionUtils.isNotEmpty(attachmentList)){
            for (JyAttachmentDetailEntity temp : attachmentList) {
                list.add(temp.getAttachmentUrl());
            }
        }
        return list;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.queryPageStrandReportTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<List<JyStrandReportScanVO>> queryPageStrandReportTaskDetail(JyStrandReportScanPageReq detailPageReq) {
        InvokeResult<List<JyStrandReportScanVO>> result = new InvokeResult<>();
        if(detailPageReq == null || StringUtils.isEmpty(detailPageReq.getBizId()) 
                || detailPageReq.getPageNum() == null || detailPageReq.getPageSize() == null){
            result.parameterError();
            return result;
        }
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("bizId", detailPageReq.getBizId());
        paramsMap.put("offset", (detailPageReq.getPageNum() - 1) * detailPageReq.getPageSize());
        paramsMap.put("pageSize", detailPageReq.getPageSize());
        List<JyBizStrandReportDetailEntity> pageList = jyBizStrandReportDetailDao.queryPageListByCondition(paramsMap);
        if(CollectionUtils.isEmpty(pageList)){
            result.error("未查询到已扫明细!");
            return result;
        }
        List<JyStrandReportScanVO> list = Lists.newArrayList();
        for (JyBizStrandReportDetailEntity temp : pageList) {
            JyStrandReportScanVO jyStrandReportScanVO = new JyStrandReportScanVO();
            jyStrandReportScanVO.setBizId(temp.getBizId());
            jyStrandReportScanVO.setScanType(temp.getScanType());
            jyStrandReportScanVO.setScanBarCode(temp.getScanBarCode());
            jyStrandReportScanVO.setContainerCode(temp.getContainerCode());
            jyStrandReportScanVO.setContainerInnerCount(temp.getContainerInnerCount());
            list.add(jyStrandReportScanVO);
        }
        result.setData(list);
        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskStrandReportService.regularDealStrandReport",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> regularDealStrandReport() {
        return null;
    }

    @Override
    public void scanContainerDeal(JyBizStrandReportDetailEntity detailEntity) {
        String bizId = detailEntity.getBizId();
        JyBizTaskStrandReportEntity strandReportTask = jyBizTaskStrandReportDao.queryOneByBiz(bizId);
        if(strandReportTask == null || Objects.equals(strandReportTask.getTaskStatus(), JyBizStrandTaskStatusEnum.CANCEL.getCode())){
            logger.warn("拣运滞留任务bizId:{}已取消/已完成!", bizId);
            return;
        }
        StrandReportRequest request = new StrandReportRequest();
        request.setBarcode(detailEntity.getContainerCode());
        request.setReasonCode(strandReportTask.getStrandCode());
        request.setReasonMessage(strandReportTask.getStrandReason());
        request.setSyncFlag(Constants.NUMBER_ONE);
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(detailEntity.getCreateUserErp());
        if(baseStaff == null){
            logger.warn("根据erp:{}未查询到人员信息!", detailEntity.getCreateUserErp());
            return;
        }
        request.setUserCode(baseStaff.getStaffNo());
        request.setUserName(baseStaff.getStaffName());
        request.setSiteCode(strandReportTask.getSiteCode());
        request.setSiteName(strandReportTask.getSiteName());
        request.setOperateTime(DateHelper.formatDateTime(detailEntity.getCreateTime()));
        
        switch (JyBizStrandScanTypeEnum.queryEnumByCode(detailEntity.getScanType())){
            case PACK:
                request.setReportType(ReportTypeEnum.PACKAGE_CODE.getCode());
                break;
            case WAYBILL:
                request.setReportType(ReportTypeEnum.WAYBILL_CODE.getCode());
                break;
            case BOX:
                request.setReportType(ReportTypeEnum.BOX_CODE.getCode());
                break;
            case BOARD:
                request.setReportType(ReportTypeEnum.BOARD_NO.getCode());
                break;
            default:
                logger.warn("条码:{}通过非法扫描方式,不进行处理!", detailEntity.getScanBarCode());
                return;
        }
        strandService.report(request);
    }

    @Override
    public boolean existCheck(String transPlanCode) {
        return jyBizTaskStrandReportDao.queryOneByTransportRejectBiz(transPlanCode) != null;
    }
}
