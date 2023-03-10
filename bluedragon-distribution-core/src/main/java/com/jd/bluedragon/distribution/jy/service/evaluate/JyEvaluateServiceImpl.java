package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateDimensionDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateTargetInfoDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
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
     * 装车评价锁前缀
     */
    public static final String SEND_EVALUATE_LOCK_PREFIX = "SEND_EVALUATE_LOCK_";

    private final static int LOCK_TIME = 60;


    @Autowired
    private JyEvaluateDimensionDao jyEvaluateDimensionDao;
    @Autowired
    private JyEvaluateTargetInfoDao jyEvaluateTargetInfoDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;


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
            dimensionOption.setCode(dimension.getCode());
            dimensionOption.setName(dimension.getName());
            dimensionOption.setStatus(dimension.getStatus());
            dimensionOption.setHasTextBox(dimension.getHasTextBox());
            options.add(dimensionOption);
        }
        return options;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean checkIsEvaluate(EvaluateTargetReq request) {
        JyEvaluateRecordEntity evaluateRecord = jyEvaluateRecordDao.findRecordBySourceBizId(request.getSourceBizId());
        if (evaluateRecord == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<EvaluateDimensionDto> findTargetEvaluateInfo(EvaluateTargetReq request) {
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(request.getSourceBizId());
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
            // 校验操作合法性
            if (EVALUATE_STATUS_SATISFIED.equals(request.getStatus())) {
                // 查询一条评价记录
                List<Integer> statusList = jyEvaluateRecordDao.findStatusListBySourceBizId(request.getSourceBizId());
                if (CollectionUtils.isNotEmpty(statusList)) {
                    if (statusList.contains(EVALUATE_STATUS_SATISFIED)) {
                        throw new JyBizException("该任务已经评价过满意，无需再次评价满意！");
                    } else {
                        throw new JyBizException("该任务已经评价不满意，不可修改为满意！");
                    }
                }
            }
            String targetBizId = getTargetBizId(request);
            // 评价明细列表
            List<JyEvaluateRecordEntity> recordList;
            if (EVALUATE_STATUS_SATISFIED.equals(request.getStatus())) {
                // 构造满意的记录
                recordList = createSatisfiedEvaluateRecord(request, targetBizId);
            } else {
                // 构造不满意的记录
                recordList = createEvaluateRecordList(request, targetBizId);
            }
            // 保存评价明细(前面都是对象组装，事务都加在这一层)
            jyEvaluateCommonService.saveEvaluateInfo(recordList);
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
            String targetBizId = getTargetBizId(request);
            // 评价明细列表
            List<JyEvaluateRecordEntity> recordList = createEvaluateRecordList(request, targetBizId);
            // 保存评价明细(前面都是对象组装，事务都加在这一层)
            jyEvaluateCommonService.saveEvaluateInfo(recordList);
        } finally {
            unLock(request.getSourceBizId());
        }
    }



    private String getTargetBizId(EvaluateTargetReq request) {
        // 查询一条评价记录
        JyEvaluateRecordEntity evaluateRecord = jyEvaluateRecordDao.findRecordBySourceBizId(request.getSourceBizId());
        if (evaluateRecord == null) {
            // 根据运输封车编码查询对应的发货任务
            JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyEvaluateCommonService.findSendTaskByBizId(request.getSourceBizId());
            return sendVehicleDetail.getBizId();
        } else {
            return evaluateRecord.getTargetBizId();
        }
    }


    private List<JyEvaluateRecordEntity> createEvaluateRecordList(EvaluateTargetReq request, String targetBizId) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        for (EvaluateDimensionReq dimensionReq : request.getDimensionList()) {
            JyEvaluateRecordEntity record = createEvaluateRecord(request, targetBizId);
            record.setDimensionCode(dimensionReq.getDimensionCode());
            if (CollectionUtils.isNotEmpty(dimensionReq.getImgUrlList())) {
                record.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(dimensionReq.getImgUrlList()));
            }
            record.setRemark(dimensionReq.getRemark());
            recordList.add(record);
        }
        return recordList;
    }

    private List<JyEvaluateRecordEntity> createSatisfiedEvaluateRecord(EvaluateTargetReq request, String targetBizId) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        JyEvaluateRecordEntity record = createEvaluateRecord(request, targetBizId);
        recordList.add(record);
        return recordList;
    }

    private JyEvaluateRecordEntity createEvaluateRecord(EvaluateTargetReq request, String targetBizId) {
        JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
        record.setEvaluateType(EVALUATE_TYPE_LOAD);
        record.setTargetBizId(targetBizId);
        record.setSourceBizId(request.getSourceBizId());
        record.setStatus(request.getStatus());
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
            dimensionEnumMap.put(entity.getCode(), entity);
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
            evaluateDimensionDto.setDimensionName(dimensionEnum.getName());
            // 设置维度图片列表
            if (StringUtils.isNotBlank(imgUrl)) {
                evaluateDimensionDto.setImgUrlList(new ArrayList<>(Arrays.asList(imgUrl.split(Constants.SEPARATOR_COMMA))));
            }
            // 设置维度是否重点关注
            evaluateDimensionDto.setStatus(dimensionEnum.getStatus());
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
                    evaluateDimensionDto.setRemark(evaluateDimensionDto.getRemark() + "\n" + remark);
                }
            }
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<JyEvaluateTargetInfoEntity>> queryPageList(JyEvaluateTargetInfoQuery query) {
        Result<List<JyEvaluateTargetInfoEntity>> result = Result.success();
        this.checkAndFillQuery(query);
        result.setData(jyEvaluateTargetInfoDao.queryPageList(query));
        return result;
    }

    private void checkAndFillQuery(JyEvaluateTargetInfoQuery query) {
        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(10);
        }
        if (query.getPageNumber() == null || query.getPageNumber() < 1) {
            query.setPageNumber(1);
        }
        query.setLimit(query.getPageSize());
        int offset = (query.getPageNumber() - 1) * query.getPageSize();
        query.setOffset(offset);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(JyEvaluateTargetInfoQuery query) {
        Result<Long> result = Result.success();
        this.checkAndFillQuery(query);
        result.setData(jyEvaluateTargetInfoDao.queryCount(query));
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryInfoByTargetBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<JyEvaluateTargetInfoEntity> queryInfoByTargetBizId(String businessId) {
        Result<JyEvaluateTargetInfoEntity> result = Result.success();
        result.setData(jyEvaluateTargetInfoDao.queryInfoByTargetBizId(businessId));
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryRecordByTargetBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<JyEvaluateRecordEntity>> queryRecordByTargetBizId(String businessId) {
        Result<List<JyEvaluateRecordEntity>> result = Result.success();
        result.setData(jyEvaluateRecordDao.queryRecordByTargetBizId(businessId));
        return result;
    }
}
