package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateDimensionDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dto.evaluate.EvaluateTargetInitDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("jyEvaluateService")
public class JyEvaluateServiceImpl implements JyEvaluateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateServiceImpl.class);

    /**
     * 评价类型：1-装车 2-卸车
     */
    private static final Integer EVALUATE_TYPE_LOAD = 1;

    /**
     * 评价状态：0-不满意 1-满意
     */
    private static final Integer EVALUATE_STATUS_SATISFIED = 1;

    /**
     * 每个评价维度对应的图片上限
     */
    private static final Integer EVALUATE_IMAGE_LIMIT = 4;

    /**
     * 装车评价锁前缀
     */
    public static final String SEND_EVALUATE_LOCK_PREFIX = "SEND_EVALUATE_LOCK_";

    private final static int LOCK_TIME = 60;

    /**
     * 装车评价初始化消息业务key
     */
    private static final String EVALUATE_INIT_BUSINESS_KEY = "INIT";


    @Autowired
    private JyEvaluateDimensionDao jyEvaluateDimensionDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    @Autowired
    @Qualifier("evaluateTargetInitProducer")
    private DefaultJMQProducer evaluateTargetInitProducer;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.dimensionOptions", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<DimensionOption> dimensionOptions() {
        List<JyEvaluateDimensionEntity> list = jyEvaluateDimensionDao.findAllDimension();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<DimensionOption> options = new ArrayList<>();
        for (JyEvaluateDimensionEntity dimension : list) {
            DimensionOption dimensionOption = new DimensionOption();
            dimensionOption.setCode(dimension.getDimensionCode());
            dimensionOption.setName(dimension.getDimensionName());
            dimensionOption.setStatus(dimension.getDimensionStatus());
            dimensionOption.setHasTextBox(dimension.getHasTextBox());
            options.add(dimensionOption);
        }
        return options;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean checkIsEvaluate(EvaluateTargetReq request) {
        // 装车评价开关如果关闭，直接返回已评价
        if (!uccPropertyConfiguration.isLoadCarEvaluateSwitch()) {
            return Boolean.TRUE;
        }
        JyEvaluateRecordEntity evaluateRecord = jyEvaluateRecordDao.findRecordBySourceBizId(request.getSourceBizId());
        if (evaluateRecord == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<EvaluateDimensionDto> findTargetEvaluateInfo(EvaluateTargetReq request) {
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findUnsatisfiedRecordsBySourceBizId(request.getSourceBizId());
        if (CollectionUtils.isEmpty(recordList)) {
            return new ArrayList<>();
        }
        List<JyEvaluateDimensionEntity> list = jyEvaluateDimensionDao.findAllDimension();
        if (CollectionUtils.isEmpty(list)) {
            throw new JyBizException("查询评价维度枚举列表为空");
        }
        // 将评价枚举列表转换为map
        Map<Integer, JyEvaluateDimensionEntity> dimensionEnumMap = transformDataToMap(list);
        // 组装结果
        Map<Integer, EvaluateDimensionDto> map = new HashMap<>();
        for (JyEvaluateRecordEntity record : recordList) {
            transformData(map, dimensionEnumMap, record);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.saveTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void saveTargetEvaluate(EvaluateTargetReq request) {
        try {
            // 获取锁
            if (!lock(request.getSourceBizId())) {
                throw new JyBizException("多人同时评价该任务，请稍后重试！");
            }
            // 报表加工MQ实体
            EvaluateTargetInitDto targetInitDto = new EvaluateTargetInitDto();
            // 校验操作合法性
            checkEvaluateValidity(request, targetInitDto);
            // 构造评价明细列表
            List<JyEvaluateRecordEntity> recordList = createEvaluateRecords(request);
            // 保存评价明细(前面都是对象组装，事务都加在这一层)
            jyEvaluateCommonService.saveEvaluateInfo(recordList);
            // 发送报表加工异步消息
            sendEvaluateMQ(request, targetInitDto);
        } finally {
            unLock(request.getSourceBizId());
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.updateTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void updateTargetEvaluate(EvaluateTargetReq request) {
        try {
            // 获取锁
            if (!lock(request.getSourceBizId())) {
                throw new JyBizException("多人同时评价该任务，请稍后重试！");
            }
            // 报表加工MQ实体
            EvaluateTargetInitDto targetInitDto = new EvaluateTargetInitDto();
            // 校验操作合法性
            checkEvaluateValidity(request, targetInitDto);
            // 评价明细列表
            List<JyEvaluateRecordEntity> recordList = createEvaluateRecords(request);
            // 保存评价明细(前面都是对象组装，事务都加在这一层)
            jyEvaluateCommonService.saveEvaluateInfo(recordList);
            // 发送报表加工异步消息
            sendEvaluateMQ(request, targetInitDto);
        } finally {
            unLock(request.getSourceBizId());
        }
    }


    private void sendEvaluateMQ(EvaluateTargetReq request, EvaluateTargetInitDto targetInitDto) {
        // 操作时间
        targetInitDto.setOperateTime(System.currentTimeMillis());
        // 操作人erp
        targetInitDto.setOperateUserErp(request.getUser().getUserErp());
        // 操作人姓名
        targetInitDto.setOperateUserName(request.getUser().getUserName());
        // 评价来源bizId
        targetInitDto.setSourceBizId(request.getSourceBizId());
        // 本次新增评价明细
        targetInitDto.setDimensionList(request.getDimensionList());
        String businessId = request.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_INIT_BUSINESS_KEY
                + Constants.UNDER_LINE + targetInitDto.getOperateTime();
        evaluateTargetInitProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetInitDto));
    }

    private void checkEvaluateValidity(EvaluateTargetReq request, EvaluateTargetInitDto targetInitDto) {

        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(request.getSourceBizId());
        // 如果记录为空，代表首次评价
        if (CollectionUtils.isEmpty(recordList)) {
            request.setTargetBizId(Constants.EMPTY_FILL);
            targetInitDto.setTargetBizId(Constants.EMPTY_FILL);
            targetInitDto.setFirstEvaluate(Boolean.TRUE);
            JyBizTaskUnloadVehicleEntity unloadVehicle = jyEvaluateCommonService.findUnloadTaskByBizId(request.getSourceBizId());
            // 派车单号
            String transWorkItemCode = Constants.EMPTY_FILL;
            if (unloadVehicle != null) {
                transWorkItemCode = unloadVehicle.getTransWorkItemCode();
                // 根据运输封车编码查询对应的发货任务
                JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyEvaluateCommonService.findSendTaskByTransWorkItemCode(transWorkItemCode);
                if (sendVehicleDetail != null) {
                    request.setTargetBizId(sendVehicleDetail.getSendVehicleBizId());
                    targetInitDto.setTargetBizId(sendVehicleDetail.getSendVehicleBizId());
                    targetInitDto.setTargetSiteCode(sendVehicleDetail.getStartSiteId().intValue());
                    targetInitDto.setTargetSiteName(sendVehicleDetail.getStartSiteName());
                    targetInitDto.setSealTime(sendVehicleDetail.getSealCarTime());
                }
                targetInitDto.setSourceSiteCode(unloadVehicle.getEndSiteId().intValue());
                targetInitDto.setSourceSiteName(unloadVehicle.getEndSiteName());
                targetInitDto.setUnsealTime(unloadVehicle.getDesealCarTime());
                targetInitDto.setVehicleNumber(unloadVehicle.getVehicleNumber());
            }
            targetInitDto.setTransWorkItemCode(transWorkItemCode);
            return;
        }
        // 如果本次评价满意
        if (EVALUATE_STATUS_SATISFIED.equals(request.getStatus())) {
            checkOperateValidity(recordList);
        } else {
            // 校验图片数量是否超过限制
            checkImageCountLimit(request, targetInitDto, recordList);
        }
    }

    private void checkOperateValidity(List<JyEvaluateRecordEntity> recordList) {
        for (JyEvaluateRecordEntity evaluateRecord : recordList) {
            // 如果之前已经评价过满意
            if (EVALUATE_STATUS_SATISFIED.equals(evaluateRecord.getEvaluateStatus())) {
                throw new JyBizException("该任务已经评价过满意，无需再次评价满意！");
                // 如果之前评价不满意，则不能修改为满意
            } else {
                throw new JyBizException("该任务已经评价不满意，不可修改为满意！");
            }
        }
    }

    private void checkImageCountLimit(EvaluateTargetReq request, EvaluateTargetInitDto targetInitDto, List<JyEvaluateRecordEntity> recordList) {
        // <评价维度，图片数量>
        Map<Integer, Integer> dimensionMap = new HashMap<>();
        for (JyEvaluateRecordEntity evaluateRecord : recordList) {
            // 评价维度
            Integer dimensionCode = evaluateRecord.getDimensionCode();
            // 图片集合
            String imgUrls = evaluateRecord.getImgUrl();
            // 评价状态
            Integer status = evaluateRecord.getEvaluateStatus();
            if (!EVALUATE_STATUS_SATISFIED.equals(status)) {
                // 该评价维度累计的图片数量
                Integer imgCount = dimensionMap.get(dimensionCode);
                // 当前记录上的图片数量
                Integer imgSize = imgUrls.split(Constants.SEPARATOR_COMMA).length;
                if (imgCount == null) {
                    dimensionMap.put(dimensionCode, imgSize);
                } else {
                    dimensionMap.put(dimensionCode, imgCount + imgSize);
                }
            }
        }
        request.setTargetBizId(recordList.get(0).getTargetBizId());
        targetInitDto.setTargetBizId(recordList.get(0).getTargetBizId());
        // 如果之前没有评价过不满意，则不需要校验
        if (dimensionMap.isEmpty()) {
            return;
        }
        // 校验图片是否超过上限
        for (EvaluateDimensionReq evaluateDimensionReq : request.getDimensionList()) {
            // 本次提交的评价维度
            Integer dimensionCode = evaluateDimensionReq.getDimensionCode();
            // 本次该评价维度提交的图片数量
            Integer newImgCount = evaluateDimensionReq.getImgUrlList().size();
            // 该评价维度已有的图片数量
            Integer oldImgCount = dimensionMap.get(dimensionCode);
            if (oldImgCount != null) {
                if (oldImgCount >= EVALUATE_IMAGE_LIMIT) {
                    throw new LoadIllegalException("照片数量超过上限，已有人上传照片，请刷新查看！");
                } else if ((oldImgCount + newImgCount) > EVALUATE_IMAGE_LIMIT) {
                    throw new LoadIllegalException("照片数量超过上限，已有人上传照片，请刷新查看！");
                }
            }
        }
    }




    private List<JyEvaluateRecordEntity> createEvaluateRecords(EvaluateTargetReq request) {
        if (EVALUATE_STATUS_SATISFIED.equals(request.getStatus())) {
            // 构造满意的记录
            return createSatisfiedEvaluateRecords(request);
        } else {
            // 构造不满意的记录
            return createUnsatisfiedEvaluateRecords(request);
        }
    }

    private List<JyEvaluateRecordEntity> createUnsatisfiedEvaluateRecords(EvaluateTargetReq request) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        for (EvaluateDimensionReq dimensionReq : request.getDimensionList()) {
            JyEvaluateRecordEntity record = createEvaluateRecord(request);
            record.setDimensionCode(dimensionReq.getDimensionCode());
            if (CollectionUtils.isNotEmpty(dimensionReq.getImgUrlList())) {
                record.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(dimensionReq.getImgUrlList()));
            }
            record.setRemark(dimensionReq.getRemark());
            recordList.add(record);
        }
        return recordList;
    }

    private List<JyEvaluateRecordEntity> createSatisfiedEvaluateRecords(EvaluateTargetReq request) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        JyEvaluateRecordEntity record = createEvaluateRecord(request);
        recordList.add(record);
        return recordList;
    }

    private JyEvaluateRecordEntity createEvaluateRecord(EvaluateTargetReq request) {
        JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
        record.setEvaluateType(EVALUATE_TYPE_LOAD);
        record.setTargetBizId(request.getTargetBizId());
        record.setSourceBizId(request.getSourceBizId());
        record.setEvaluateStatus(request.getStatus());
        record.setCreateUserErp(request.getUser().getUserErp());
        record.setCreateUserName(request.getUser().getUserName());
        record.setUpdateUserErp(request.getUser().getUserErp());
        record.setUpdateUserName(request.getUser().getUserName());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setYn(Constants.YN_YES);
        return record;
    }

    private boolean lock(String sourceBizId) {
        String lockKey = SEND_EVALUATE_LOCK_PREFIX + sourceBizId;
        LOGGER.info("装车评价开始获取锁lockKey={}", lockKey);
        try {
            if (!jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS)) {
                Thread.sleep(100);
                return jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LOGGER.error("装车评价lock异常:sourceBizId={},e=", sourceBizId, e);
            jimdbCacheService.del(lockKey);
        }
        return true;
    }

    private void unLock(String sourceBizId) {
        try {
            String lockKey = SEND_EVALUATE_LOCK_PREFIX + sourceBizId;
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            LOGGER.error("装车评价unLock异常:sourceBizId={},e=", sourceBizId, e);
        }
    }

    private Map<Integer, JyEvaluateDimensionEntity> transformDataToMap(List<JyEvaluateDimensionEntity> list) {
        Map<Integer, JyEvaluateDimensionEntity> dimensionEnumMap = new HashMap<>();
        for (JyEvaluateDimensionEntity entity : list) {
            dimensionEnumMap.put(entity.getDimensionCode(), entity);
        }
        return dimensionEnumMap;
    }

    private void transformData(Map<Integer, EvaluateDimensionDto> map, Map<Integer,
            JyEvaluateDimensionEntity> dimensionEnumMap, JyEvaluateRecordEntity record) {
        // 评价维度编码
        Integer dimensionCode = record.getDimensionCode();
        // 评价维度对应图片url
        String imgUrl = record.getImgUrl();
        // 评价维度文本框内容
        String remark = record.getRemark();

        EvaluateDimensionDto evaluateDimensionDto = map.get(dimensionCode);
        if (evaluateDimensionDto == null) {
            evaluateDimensionDto = new EvaluateDimensionDto();
            evaluateDimensionDto.setDimensionCode(dimensionCode);
            // 评价维度详情
            JyEvaluateDimensionEntity dimensionEnum = dimensionEnumMap.get(dimensionCode);
            // 设置维度名称
            evaluateDimensionDto.setDimensionName(dimensionEnum.getDimensionName());
            // 设置维度图片列表
            if (StringUtils.isNotBlank(imgUrl)) {
                evaluateDimensionDto.setImgUrlList(new ArrayList<>(Arrays.asList(imgUrl.split(Constants.SEPARATOR_COMMA))));
            }
            // 设置维度是否重点关注
            evaluateDimensionDto.setStatus(dimensionEnum.getDimensionStatus());
            // 设置维度是否携带文本框
            evaluateDimensionDto.setHasTextBox(dimensionEnum.getHasTextBox());
            // 设置维度文本框内容
            evaluateDimensionDto.setRemark(remark);
            map.put(dimensionCode, evaluateDimensionDto);
        } else {
            // 同一评价维度图片放在一起
            if (StringUtils.isNotBlank(imgUrl)) {
                List<String> currentImgUrlList = new ArrayList<>(Arrays.asList(imgUrl.split(Constants.SEPARATOR_COMMA)));
                if (CollectionUtils.isEmpty(evaluateDimensionDto.getImgUrlList())) {
                    evaluateDimensionDto.setImgUrlList(currentImgUrlList);
                } else {
                    evaluateDimensionDto.getImgUrlList().addAll(currentImgUrlList);
                }
            }
            // 文本框内容显示在一起
            if (StringUtils.isNotBlank(remark)) {
                if (StringUtils.isBlank(evaluateDimensionDto.getRemark())) {
                    evaluateDimensionDto.setRemark(remark);
                } else {
                    evaluateDimensionDto.setRemark(evaluateDimensionDto.getRemark() + Constants.LINE_NEXT_CHAR + remark);
                }
            }
        }
    }
}
