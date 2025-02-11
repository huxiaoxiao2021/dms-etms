package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateAppealPermissionsDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordAppealDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dto.evaluate.EvaluateTargetResultDto;
import com.jd.bluedragon.distribution.jy.enums.EvaluateAppealResultStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.EvaluateAppealStatusEnum;
import com.jd.bluedragon.distribution.jy.evaluate.*;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author pengchong28
 * @description 装车评价申诉服务实现
 * @date 2024/3/1
 */
@Service
@Slf4j
public class JyEvaluateAppealServiceImpl implements JyEvaluateAppealService {

    @Autowired
    private JyEvaluateRecordAppealDao jyEvaluateRecordAppealDao;

    @Autowired
    private JyEvaluateAppealPermissionsDao jyEvaluateAppealPermissionsDao;

    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    @Qualifier("evaluateTargetInitProducer")
    private DefaultJMQProducer evaluateTargetInitProducer;

    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    @Qualifier("evaluateTargetResultProducer")
    private DefaultJMQProducer evaluateTargetResultProducer;

    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;

    /**
     * 装车评价初始化消息业务key
     */
    private static final String EVALUATE_INIT_BUSINESS_KEY = "INIT";

    /**
     * 装车评价申诉锁前缀
     */
    public static final String EVALUATE_APPEAL_LOCK_ = "EVALUATE_APPEAL_LOCK_";
    private final static int LOCK_TIME = 60;
    /**
     * 装车评价结果消息业务key
     */
    private static final String EVALUATE_RESULT_BUSINESS_KEY = "RESULT";
    /**
     * 提交装车评价-锁前缀
     */
    private static final String SUBMIT_APPEAL_LOCK = "SUBMIT_APPEAL_LOCK";
    /**
     * 审核装车评价-锁前缀
     */
    private static final String CHECK_APPEAL_LOCK = "CHECK_APPEAL_LOCK";

    /**
     * 根据条件获取评价记录申诉列表
     *
     * @param conditions 条件列表
     * @return 响应对象，包含评价记录申诉列表
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.getListByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealDto>> getListByCondition(
        List<String> conditions) {
        Response<List<JyEvaluateRecordAppealDto>> response = new Response<>();
        response.toSucceed();
        if (CollectionUtils.isEmpty(conditions)) {
            response.toError("装车评价申诉查询条件不能为空！");
            return response;
        }
        ArrayList<JyEvaluateRecordAppealDto> resultList = new ArrayList<>();
        List<JyEvaluateRecordAppealEntity> list = null;
        try {
            list = jyEvaluateRecordAppealDao.queryListByCondition(conditions);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.getListByCondition 根据条件查询数据异常,入参:{}",
                JsonHelper.toJson(conditions), e);
            response.toError("根据条件查询装车评价申诉列表失败！");
            return response;
        }
        if (CollectionUtils.isNotEmpty(list)){
            for (JyEvaluateRecordAppealEntity entity : list) {
                JyEvaluateRecordAppealDto dto = new JyEvaluateRecordAppealDto();
                BeanCopyUtil.copy(entity, dto);
                resultList.add(dto);
            }
        }
        response.setData(resultList);
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.submitAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> submitAppeal(JyEvaluateRecordAppealAddDto addDto) {
        if (log.isInfoEnabled()){
            log.info("JyEvaluateAppealServiceImpl.submitAppeal 入参：{}", JsonHelper.toJson(addDto));
        }
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if (Objects.isNull(addDto) || CollectionUtils.isEmpty(addDto.getJyEvaluateRecordAppealDtoList())) {
            response.toError("待添加的数据为空！");
            response.setData(Boolean.FALSE);
            return response;
        }
        try {
            // 获取锁
            if (!lock(SUBMIT_APPEAL_LOCK + addDto.getTargetBizId())) {
                response.toError("多人同时申诉该评价，请稍后重试！");
                response.setData(Boolean.FALSE);
                return response;
            }
            // 判断场地是否有申诉权限
            List<JyEvaluateRecordAppealDto> dtoList = addDto.getJyEvaluateRecordAppealDtoList();
            Response<Boolean> res = checkAppeal(dtoList, response);
            if (res != null) {
                return res;
            }

            // 保存申诉数据
            jyEvaluateRecordAppealDao.batchInsert(dtoList);
            // 保存申诉上传的图片
            List<JyAttachmentDetailEntity> annexList = buildJyAttachmentDetailEntityList(dtoList);
            jyAttachmentDetailService.batchInsert(annexList);
            // 发送mq消息，同步评价状态（未申诉->已申诉）
            sendMqUpdateStatus(dtoList);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.batchAddJyEvaluateRecordAppeal 批量插入装车评价申诉数据异常", e);
            response.toError("批量插入装车评价申诉数据异常！");
            response.setData(Boolean.FALSE);
            return response;
        }finally {
            unLock(SUBMIT_APPEAL_LOCK + addDto.getTargetBizId());
        }
        response.setData(Boolean.TRUE);
        return response;
    }

    /**
     * 发送MQ更新状态
     * @param dtoList 评价记录申诉DTO列表
     * @throws
     */
    private void sendMqUpdateStatus(List<JyEvaluateRecordAppealDto> dtoList) {
        // 发送报表消息
        JyEvaluateRecordAppealDto jyEvaluateRecordAppealDto = dtoList.get(0);
        String businessId = jyEvaluateRecordAppealDto.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_RESULT_BUSINESS_KEY
            + Constants.UNDER_LINE + System.currentTimeMillis();
        EvaluateTargetResultDto targetResultDto = new EvaluateTargetResultDto();
        targetResultDto.setTargetBizId(jyEvaluateRecordAppealDto.getTargetBizId());
        targetResultDto.setSourceBizId(jyEvaluateRecordAppealDto.getSourceBizId());
        targetResultDto.setAppealStatus(Constants.CONSTANT_NUMBER_TWO);
        targetResultDto.setFirstEvaluate(Boolean.FALSE);
        evaluateTargetResultProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetResultDto));
    }

    /**
     * 判断场地是否有申诉权限
     *
     * @param entityList 实体列表
     * @param response   响应对象
     * @return 返回布尔值的响应
     */
    private Response<Boolean> checkAppeal(List<JyEvaluateRecordAppealDto> entityList, Response<Boolean> response) {
        // 获取场地是否有申诉权限
        JyEvaluateAppealPermissionsEntity permissions =
            jyEvaluateAppealPermissionsDao.queryByCondition(entityList.get(0).getSiteCode());
        // 判断场地申诉权限是否关闭
        if (Objects.nonNull(permissions) && Objects.equals(permissions.getAppeal(),
            Constants.EVALUATE_APPEAL_PERMISSIONS_0)) {
            // 场地申诉已关闭，比较权限关闭时间和当前时间是否超过7天，超过可进行申诉
            Integer closeDay = dmsConfigManager.getPropertyConfig().getEvaluateAppealCloseDay();
            if (!DateHelper.isDateMoreThanDaysAgo(permissions.getAppealClosureDate(), closeDay)) {
                response.toError("当前场地申诉权限已被暂时关闭7天！");
                response.setData(Boolean.FALSE);
                return response;
            } else {
                // 超过7天，开启场地申诉权限
                JyEvaluateAppealPermissionsEntity entity =
                    buildJyEvaluateAppealPermissionsEntity(entityList.get(0), permissions);
                jyEvaluateAppealPermissionsDao.updateAppealStatusById(entity);
            }
        }

        // 场地评价和申诉权限记录为空，初始化记录，默认评价和申诉开启
        if (Objects.isNull(permissions)) {
            JyEvaluateAppealPermissionsEntity entity = buildEntity(entityList.get(0));
            jyEvaluateAppealPermissionsDao.insert(entity);
        }
        return null;
    }

    private JyEvaluateAppealPermissionsEntity buildEntity(JyEvaluateRecordAppealDto dto) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setSiteCode(new Long(dto.getSiteCode()));
        entity.setAppeal(Constants.EVALUATE_APPEAL_PERMISSIONS_1);
        entity.setEvaluate(Constants.EVALUATE_APPEAL_PERMISSIONS_1);
        entity.setCreateUserErp(dto.getCreateUserErp());
        entity.setCreateUserName(dto.getCreateUserName());
        entity.setCreateTime(new Date());
        return entity;
    }

    /**
     * 构建JyEvaluateAppealPermissionsEntity对象
     *
     * @param dto         评价记录申诉数据传输对象
     * @param permissions 评价申诉权限实体
     * @return 构建后的评价申诉权限实体对象
     */
    private JyEvaluateAppealPermissionsEntity buildJyEvaluateAppealPermissionsEntity(JyEvaluateRecordAppealDto dto,
        JyEvaluateAppealPermissionsEntity permissions) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setId(permissions.getId());
        entity.setAppeal(Constants.EVALUATE_APPEAL_PERMISSIONS_1);
        entity.setUpdateUserErp(dto.getUpdateUserErp());
        entity.setUpdateUserName(dto.getUpdateUserName());
        entity.setUpdateTime(new Date());
        return entity;
    }

    private boolean lock(String targetBizId) {
        String lockKey = EVALUATE_APPEAL_LOCK_ + targetBizId;
        log.info("装车评价申诉开始获取锁lockKey={}", lockKey);
        try {
            if (!jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS)) {
                Thread.sleep(100);
                return jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("装车评价申诉lock异常:sourceBizId={},e=", targetBizId, e);
            jimdbCacheService.del(lockKey);
        }
        return true;
    }

    private void unLock(String targetBizId) {
        try {
            String lockKey = EVALUATE_APPEAL_LOCK_ + targetBizId;
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            log.error("装车评价申诉unLock异常:sourceBizId={},e=", targetBizId, e);
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.getDetailByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealDto>> getDetailByCondition(JyEvaluateRecordAppealDto condition) {
        if (log.isInfoEnabled()){
            log.info("JyEvaluateAppealServiceImpl.getDetailByCondition 入参：{}", JsonHelper.toJson(condition));
        }
        Response<List<JyEvaluateRecordAppealDto>> response = new Response<>();
        response.toSucceed();
        List<JyEvaluateRecordAppealEntity> list = null;
        List<JyAttachmentDetailEntity> entities = null;
        try {
            // 查询申诉详情
            list = jyEvaluateRecordAppealDao.queryDetailByCondition(condition);
            // 查询不满意项的图片
            JyAttachmentDetailQuery query = buildJyAttachmentDetailQuery(condition);
            entities = jyAttachmentDetailService.queryDataListByCondition(query);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.getDetailByCondition 根据条件查询数据异常,入参:{}", condition, e);
            response.toError("根据条件查询装车评价申诉详情失败");
            return response;
        }
        ArrayList<JyEvaluateRecordAppealDto> dtos = getJyEvaluateRecordAppealDtos(list, entities);
        response.setData(dtos);
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.checkAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> checkAppeal(JyEvaluateRecordAppealRes res) {
        if (log.isInfoEnabled()){
            log.info("JyEvaluateAppealServiceImpl.checkAppeal 入参：{}", JsonHelper.toJson(res));
        }
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if (res == null || res.getAppealList().isEmpty()) {
            response.toError("待审核的数据为空！");
            response.setData(Boolean.FALSE);
            return response;
        }
        try {
            // 获取锁
            if (!lock(CHECK_APPEAL_LOCK + res.getTargetBizId())) {
                response.toError("多人同时审核该评价的申诉，请稍后重试");
                response.setData(Boolean.FALSE);
                return response;
            }
            List<Long> idList = getIdList(res);
            Map<Long, Integer> idMap = getIdMap(res);

            List<JyEvaluateRecordAppealEntity> dbList = jyEvaluateRecordAppealDao.queryByIdList(idList);
            if (dbList.isEmpty()) {
                response.toError("待审核的数据为空！");
                response.setData(Boolean.FALSE);
                return response;
            }

            // 按审核结果，分批量进行更新
            JyEvaluateRecordAppealUpdateDto updateDto =
                createUpdateDto(EvaluateAppealStatusEnum.TIMED_OUT_AND_NOT_REVIEWED,
                    EvaluateAppealResultStatusEnum.NO_HANDLE, res);
            JyEvaluateRecordAppealUpdateDto updatePassDto =
                createUpdateDto(EvaluateAppealStatusEnum.REVIEWED, EvaluateAppealResultStatusEnum.PASS, res);
            JyEvaluateRecordAppealUpdateDto updateRejectDto =
                createUpdateDto(EvaluateAppealStatusEnum.REVIEWED, EvaluateAppealResultStatusEnum.REJECT, res);

            // 按审核结果，构建需要更新的数据新集合
            getUpdatedDataList(dbList, updateDto, idMap, updatePassDto, updateRejectDto);

            // 进行数据库数据更新，装车评价记录申诉表
            dbUpdate(updateDto, updatePassDto, updateRejectDto);
            // 统计审核次数和审核驳回次数，更新场地权限
            updateSiteEvaluateAndAppeal(res);
            // 审核通过的数据，更新至装车评价记录表
            updateEvaluateRecord(updatePassDto);
            // 审核通过的数据，同步到es
            esDataUpdate(res, updatePassDto);
        } catch (Exception e) {
            response.toError("装车评价申诉审核异常！");
            response.setData(Boolean.FALSE);
            log.error("JyEvaluateAppealServiceImpl.checkAppeal 装车评价申诉审核异常,入参:{}", JsonHelper.toJson(res),
                e);
            return response;
        } finally {
            unLock(CHECK_APPEAL_LOCK + res.getTargetBizId());
        }
        response.setData(Boolean.TRUE);
        return response;
    }

    /**
     * 更新评价记录
     *
     * @param updatePassDto 更新评价记录的数据传输对象
     */
    private void updateEvaluateRecord(JyEvaluateRecordAppealUpdateDto updatePassDto) {
        if (CollectionUtils.isNotEmpty(updatePassDto.getDimensionCodeList())) {
            List<JyEvaluateRecordEntity> recordEntities = jyEvaluateCommonService.findByCondition(updatePassDto);
            List<Long> ids = recordEntities.stream().map(JyEvaluateRecordEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                jyEvaluateCommonService.batchUpdate(ids);
            }
        }
    }

    /**
     * 更新站点评价和申诉权限
     *
     * @param res 评估记录申诉响应
     */
    private void updateSiteEvaluateAndAppeal(JyEvaluateRecordAppealRes res) {
        // 统计场地审核通过次数>3,下游乱评价，关闭下游未来7天的评价权限
        JyEvaluateRecordAppealDto dto = buildJyEvaluateRecordAppealDto(res.getSourceSiteCode(), EvaluateAppealResultStatusEnum.PASS.getCode());
        Integer checkAppealCount = jyEvaluateRecordAppealDao.getAppealCount(dto);
        if (Objects.nonNull(checkAppealCount) && checkAppealCount >= Constants.APPEAL_COUNT_NUM) {
            JyEvaluateAppealPermissionsEntity permissions =
                jyEvaluateAppealPermissionsDao.queryByCondition(res.getSourceSiteCode().intValue());

            // 场地评价和申诉权限记录为空，初始化记录，关闭场地评价权限，申诉权限开启
            if (Objects.isNull(permissions)) {
                JyEvaluateAppealPermissionsEntity entity =
                    buildPermissionsEntity(res, Constants.EVALUATE_APPEAL_PERMISSIONS_0, new Date(),
                        Constants.EVALUATE_APPEAL_PERMISSIONS_1, null, res.getSourceSiteCode());
                jyEvaluateAppealPermissionsDao.insert(entity);
            } else {
                // 关闭场地评价权限，且场地评价权限已开启
                if (Objects.equals(permissions.getEvaluate(),Constants.EVALUATE_APPEAL_PERMISSIONS_1)){
                    JyEvaluateAppealPermissionsEntity entity = buildUpdatePermissionsEvaluateEntity(permissions, res);
                    jyEvaluateAppealPermissionsDao.updateEvaluateStatusById(entity);
                }

            }
        }
        // 统计场地审核中驳回次数>3,申诉人乱申诉，关闭上游游未来7天的申诉权限
        JyEvaluateRecordAppealDto appealDto =
            getJyEvaluateRecordAppealDto(res.getTargetSiteCode(), EvaluateAppealResultStatusEnum.REJECT.getCode());
        Integer appealRejectCount = jyEvaluateRecordAppealDao.getAppealCount(appealDto);
        if (Objects.nonNull(appealRejectCount) && appealRejectCount >= Constants.APPEAL_COUNT_NUM) {
            JyEvaluateAppealPermissionsEntity permissions =
                jyEvaluateAppealPermissionsDao.queryByCondition(res.getTargetSiteCode().intValue());
            // 场地评价和申诉权限记录为空，初始化记录，关闭场地申诉权限，评价权限开启
            if (Objects.isNull(permissions)) {
                JyEvaluateAppealPermissionsEntity entity =
                    buildPermissionsEntity(res, Constants.EVALUATE_APPEAL_PERMISSIONS_1, null,
                        Constants.EVALUATE_APPEAL_PERMISSIONS_0, new Date(), res.getTargetSiteCode());
                jyEvaluateAppealPermissionsDao.insert(entity);
            } else {
                // 关闭场地申诉权限,且场地申诉权限已开启
                if (Objects.equals(permissions.getAppeal(),Constants.EVALUATE_APPEAL_PERMISSIONS_1)) {
                    JyEvaluateAppealPermissionsEntity entity = buildUpdatePermissionsEntity(permissions, res);
                    jyEvaluateAppealPermissionsDao.updateAppealStatusById(entity);
                }
            }
        }
    }

    /**
     * 获取评价记录申诉数据传输对象
     * @param targetSiteCode 场地编码
     * @param appealResult 申诉结果
     * @return 评价记录申诉数据传输对象
     */
    private static JyEvaluateRecordAppealDto getJyEvaluateRecordAppealDto(Long targetSiteCode, Integer appealResult) {
        JyEvaluateRecordAppealDto dto = new JyEvaluateRecordAppealDto();
        dto.setTargetSiteCode(targetSiteCode);
        dto.setAppealResult(appealResult);
        Date date = DateHelper.addDate(new Date(), -7);
        dto.setUpdateTime(date);
        return dto;
    }

    /**
     * 构建教育评价记录申诉数据传输对象
     * @param sourceSiteCode 源站点代码
     * @param appealResult 申诉结果
     * @return dto 构建的教育评价记录申诉数据传输对象
     */
    private static JyEvaluateRecordAppealDto buildJyEvaluateRecordAppealDto(Long sourceSiteCode, Integer appealResult) {
        JyEvaluateRecordAppealDto dto = new JyEvaluateRecordAppealDto();
        dto.setSourceSiteCode(sourceSiteCode);
        dto.setAppealResult(appealResult);
        Date date = DateHelper.addDate(new Date(), -7);
        dto.setUpdateTime(date);
        return dto;
    }

    public JyEvaluateAppealPermissionsEntity buildUpdatePermissionsEntity(JyEvaluateAppealPermissionsEntity permissions,
        JyEvaluateRecordAppealRes res) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setId(permissions.getId());
        entity.setAppeal(Constants.EVALUATE_APPEAL_PERMISSIONS_0);
        entity.setAppealClosureDate(new Date());
        entity.setUpdateUserErp(res.getUpdateUserErp());
        entity.setUpdateUserName(res.getUpdateUserName());
        entity.setUpdateTime(new Date());
        return entity;
    }

    public JyEvaluateAppealPermissionsEntity buildUpdatePermissionsEvaluateEntity(
        JyEvaluateAppealPermissionsEntity permissions, JyEvaluateRecordAppealRes res) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setId(permissions.getId());
        entity.setEvaluate(Constants.EVALUATE_APPEAL_PERMISSIONS_0);
        entity.setEvaluateClosureDate(new Date());
        entity.setUpdateUserErp(res.getUpdateUserErp());
        entity.setUpdateUserName(res.getUpdateUserName());
        entity.setUpdateTime(new Date());
        return entity;
    }

    private JyEvaluateAppealPermissionsEntity buildPermissionsEntity(JyEvaluateRecordAppealRes res, Integer evaluate,
        Date evaluateDate, Integer appeal, Date appealDate, Long siteCode) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setSiteCode(siteCode);
        entity.setAppeal(appeal);
        entity.setEvaluateClosureDate(appealDate);
        entity.setEvaluate(evaluate);
        entity.setEvaluateClosureDate(evaluateDate);
        entity.setCreateUserErp(res.getUpdateUserErp());
        entity.setCreateUserName(res.getUpdateUserName());
        entity.setCreateTime(new Date());
        return entity;
    }

    /**
     * 更新ES数据,通过mq
     *
     * @param res 评价记录申诉响应对象
     */
    private void esDataUpdate(JyEvaluateRecordAppealRes res, JyEvaluateRecordAppealUpdateDto updatePassDto) {
        if (CollectionUtils.isEmpty(updatePassDto.getDimensionCodeList())) {
            return;
        }
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(res.getSourceBizId());
        EvaluateTargetResultDto targetResultDto = new EvaluateTargetResultDto();
        targetResultDto.setTargetBizId(res.getTargetBizId());
        targetResultDto.setSourceBizId(res.getSourceBizId());
        targetResultDto.setFirstEvaluate(Boolean.FALSE);
        // 设置申诉通过的code集合
        List<String> codeList =
            updatePassDto.getDimensionCodeList().stream().map(String::valueOf).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordList)){
            targetResultDto.setStatus(Constants.EVALUATE_STATUS_SATISFIED);
            targetResultDto.setDimensionCode("");
        }else {
            // 评价维度编码集合
            List<String> dimensionCodeList = new ArrayList<>();
            for (JyEvaluateRecordEntity evaluateRecord : recordList) {
                // 评价维度编码
                String dimensionCode = String.valueOf(evaluateRecord.getDimensionCode());
                // 评价状态
                Integer status = evaluateRecord.getEvaluateStatus();
                // 只有不满意的记录才需要统计
                if (Constants.EVALUATE_STATUS_DISSATISFIED.equals(status) && !codeList.contains(dimensionCode)) {
                    if (!dimensionCodeList.contains(dimensionCode)) {
                        dimensionCodeList.add(dimensionCode);
                    }
                }
            }
            targetResultDto.setDimensionCode(String.join(Constants.SEPARATOR_COMMA, dimensionCodeList));
            if (CollectionUtils.isEmpty(dimensionCodeList)){
                targetResultDto.setStatus(Constants.EVALUATE_STATUS_SATISFIED);
                targetResultDto.setDimensionCode("");
            }
        }

        String businessId =
            res.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_RESULT_BUSINESS_KEY + Constants.UNDER_LINE
                + System.currentTimeMillis();
        log.info("JyEvaluateAppealServiceImpl.esDataUpdate 发送mq消息体：={}", JsonHelper.toJson(targetResultDto));
        evaluateTargetResultProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetResultDto));
    }

    /**
     * 更新数据库中的评估记录申诉信息
     *
     * @param updateDto       要更新的申诉信息DTO
     * @param updatePassDto   要更新的通过申诉信息DTO
     * @param updateRejectDto 要更新的拒绝申诉信息DTO
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealServiceImpl.dbUpdate", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    private void dbUpdate(JyEvaluateRecordAppealUpdateDto updateDto, JyEvaluateRecordAppealUpdateDto updatePassDto,
        JyEvaluateRecordAppealUpdateDto updateRejectDto) {
        if (CollectionUtils.isNotEmpty(updateDto.getIdList())) {
            jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updateDto);
        }
        if (CollectionUtils.isNotEmpty(updatePassDto.getIdList())) {
            if (log.isInfoEnabled()){
                log.info("JyEvaluateAppealServiceImpl.dbUpdate 入参：updatePassDto：{}", JsonHelper.toJson(updatePassDto));
            }
            jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updatePassDto);
        }
        if (CollectionUtils.isNotEmpty(updateRejectDto.getIdList())) {
            if (log.isInfoEnabled()){
                log.info("JyEvaluateAppealServiceImpl.dbUpdate 入参：updateRejectDto：{}", JsonHelper.toJson(updateRejectDto));
            }
            jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updateRejectDto);
        }
    }

    /**
     * 获取更新后的数据列表
     *
     * @param dbList          数据库数据列表
     * @param updateDto       更新DTO
     * @param idMap           ID映射表
     * @param updatePassDto   通过更新DTO
     * @param updateRejectDto 拒绝更新DTO
     */
    private void getUpdatedDataList(List<JyEvaluateRecordAppealEntity> dbList,
        JyEvaluateRecordAppealUpdateDto updateDto, Map<Long, Integer> idMap,
        JyEvaluateRecordAppealUpdateDto updatePassDto, JyEvaluateRecordAppealUpdateDto updateRejectDto) {
        // 获取配置的审核超时小时数
        Integer checkOvertimeHour = dmsConfigManager.getPropertyConfig().getCheckOvertimeHour();
        for (JyEvaluateRecordAppealEntity entity : dbList) {
            double hours = DateHelper.betweenHours(entity.getCreateTime(), new Date());
            // 当前时间超过配置的审核超时小时数，不能进行审核，申诉数据更新为 超时未审核
            if (hours > checkOvertimeHour) {
                updateDto.getIdList().add(entity.getId());
                updateDto.getDimensionCodeList().add(entity.getDimensionCode());
            } else {
                Integer opinion = idMap.get(entity.getId());
                if (opinion != null) {
                    if (Objects.equals(opinion, Constants.CONSTANT_NUMBER_ONE)) {
                        updatePassDto.getIdList().add(entity.getId());
                        updatePassDto.getDimensionCodeList().add(entity.getDimensionCode());
                        updatePassDto.setSourceBizId(entity.getSourceBizId());
                        updatePassDto.setTargetBizId(entity.getTargetBizId());
                    } else if (Objects.equals(opinion, Constants.CONSTANT_NUMBER_TWO)) {
                        updateRejectDto.getIdList().add(entity.getId());
                        updateRejectDto.getDimensionCodeList().add(entity.getDimensionCode());
                    }
                }
            }
        }
        if (log.isInfoEnabled()){
            log.info("JyEvaluateAppealServiceImpl.getUpdatedDataList 参数组装结果：idMap:{},"
                + "updateDto:{}, updatePassDto:{}, updateRejectDto:{}", JsonHelper.toJson(idMap),
                JsonHelper.toJson(updateDto), JsonHelper.toJson(updatePassDto), JsonHelper.toJson(updateRejectDto));
        }
    }

    private static Map<Long, Integer> getIdMap(JyEvaluateRecordAppealRes res) {
        Map<Long, Integer> idMap = res.getAppealList().stream().collect(
            Collectors.toMap(map -> Long.valueOf(map.get(Constants.APPEAL_MAP_KEY_ID)),
                map -> map.get(Constants.APPEAL_MAP_KEY_OPINION)));
        return idMap;
    }

    private static List<Long> getIdList(JyEvaluateRecordAppealRes res) {
        List<Long> idList = res.getAppealList().stream().map(map -> Long.valueOf(map.get(Constants.APPEAL_MAP_KEY_ID)))
            .collect(Collectors.toList());
        return idList;
    }

    /**
     * 创建更新数据传输对象
     *
     * @param status 评价申诉状态枚举
     * @param result 评价申诉结果状态枚举
     * @param res    评价申诉响应数据对象
     * @return updateDto 更新数据传输对象
     */
    private JyEvaluateRecordAppealUpdateDto createUpdateDto(EvaluateAppealStatusEnum status,
        EvaluateAppealResultStatusEnum result, JyEvaluateRecordAppealRes res) {
        JyEvaluateRecordAppealUpdateDto updateDto = new JyEvaluateRecordAppealUpdateDto();
        updateDto.setAppealStatus(status.getCode());
        updateDto.setAppealResult(result.getCode());
        updateDto.setUpdateUserErp(res.getUpdateUserErp());
        updateDto.setUpdateUserName(res.getUpdateUserName());
        updateDto.setIdList(new ArrayList<>());
        updateDto.setDimensionCodeList(new ArrayList<>());
        return updateDto;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealServiceImpl.getAppealRejectCount", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Integer> getAppealRejectCount(Long loadSiteCode) {
        Response<Integer> response = new Response<>();
        response.toSucceed();
        if (Objects.isNull(loadSiteCode)) {
            response.toError("查询的场地数据为空！");
            return response;
        }

        Integer integer = null;
        try {
            JyEvaluateRecordAppealDto appealDto =
                getJyEvaluateRecordAppealDto(loadSiteCode, EvaluateAppealResultStatusEnum.REJECT.getCode());
            integer = jyEvaluateRecordAppealDao.getAppealCount(appealDto);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.getAppealRejectCount 根据场地编码查询驳回申诉条数异常,入参:{}",
                loadSiteCode, e);
            response.toError("根据场地编码查询驳回申诉条数异常！");
            return response;
        }
        response.setData(integer);
        return response;
    }

    @Override
    public Response<Boolean> updatePermissionsById(JyEvaluateAppealPermissionsEntity entity) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        try {
            jyEvaluateAppealPermissionsDao.updateById(entity);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.updatePermissionsById 根据id更新场地权限失败,入参:{}",
                JsonHelper.toJson(entity), e);
            response.toError("根据id更新场地权限失败！");
            return response;
        }
        return response;
    }

    /**
     * 获取评价记录申诉DTO列表
     *
     * @param list     评价记录申诉实体列表
     * @param entities 附件详情实体列表
     * @return jyEvaluateRecordAppealDtos 评价记录申诉DTO列表
     * @throws NullPointerException 如果list或entities为空，则返回null
     */
    private ArrayList<JyEvaluateRecordAppealDto> getJyEvaluateRecordAppealDtos(List<JyEvaluateRecordAppealEntity> list,
        List<JyAttachmentDetailEntity> entities) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        // 组装不满意项和图片
        ArrayList<JyEvaluateRecordAppealDto> jyEvaluateRecordAppealDtos = new ArrayList<>();
        for (JyEvaluateRecordAppealEntity entity : list) {
            JyEvaluateRecordAppealDto dto = new JyEvaluateRecordAppealDto();
            BeanCopyUtil.copy(entity, dto);
            ArrayList<String> imgUrlList = new ArrayList<>();
            for (JyAttachmentDetailEntity detailEntity : entities) {
                if (Objects.equals(detailEntity.getBizId(), entity.getTargetBizId()) && Objects.equals(
                    detailEntity.getBizSubType(), entity.getDimensionCode().toString())) {
                    imgUrlList.add(detailEntity.getAttachmentUrl());
                }
            }
            dto.setImgUrlList(imgUrlList);
            jyEvaluateRecordAppealDtos.add(dto);
        }
        return jyEvaluateRecordAppealDtos;
    }

    private JyAttachmentDetailQuery buildJyAttachmentDetailQuery(JyEvaluateRecordAppealDto condition) {
        JyAttachmentDetailQuery jyAttachmentDetailQuery = new JyAttachmentDetailQuery();
        jyAttachmentDetailQuery.setBizId(condition.getTargetBizId());
        jyAttachmentDetailQuery.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
        jyAttachmentDetailQuery.setSiteCode(condition.getSiteCode());
        jyAttachmentDetailQuery.setBizType(JyAttachmentBizTypeEnum.EVALUATE_RECORD_APPEAL.getCode());
        jyAttachmentDetailQuery.setPageSize(50);
        return jyAttachmentDetailQuery;
    }

    /**
     * 构建JyAttachmentDetailEntity列表
     *
     * @param entityList JyEvaluateRecordAppealRequest实体列表
     * @return jyAttachmentDetailEntities JyAttachmentDetailEntity实体列表
     */
    private List<JyAttachmentDetailEntity> buildJyAttachmentDetailEntityList(
        List<JyEvaluateRecordAppealDto> entityList) {
        ArrayList<JyAttachmentDetailEntity> jyAttachmentDetailEntities = new ArrayList<>();
        for (JyEvaluateRecordAppealDto request : entityList) {
            if (CollectionUtils.isNotEmpty(request.getImgUrlList())){
                for (String url : request.getImgUrlList()) {
                    JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                    annexEntity.setBizId(request.getTargetBizId());
                    annexEntity.setSiteCode(request.getSiteCode());
                    annexEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                    annexEntity.setCreateUserErp(request.getCreateUserErp());
                    annexEntity.setUpdateUserErp(request.getUpdateUserErp());
                    annexEntity.setAttachmentUrl(url);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 1);
                    annexEntity.setCreateTime(calendar.getTime());
                    annexEntity.setUpdateTime(new Date());
                    annexEntity.setBizType(JyAttachmentBizTypeEnum.EVALUATE_RECORD_APPEAL.getCode());
                    annexEntity.setBizSubType(request.getDimensionCode().toString());

                    jyAttachmentDetailEntities.add(annexEntity);
                }
            }
        }
        return jyAttachmentDetailEntities;
    }
}
