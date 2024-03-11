package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateAppealPermissionsDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordAppealDao;
import com.jd.bluedragon.distribution.jy.dto.evaluate.EvaluateTargetInitDto;
import com.jd.bluedragon.distribution.jy.enums.EvaluateAppealResultStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.EvaluateAppealStatusEnum;
import com.jd.bluedragon.distribution.jy.evaluate.*;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    /**
     * 装车评价初始化消息业务key
     */
    private static final String EVALUATE_INIT_BUSINESS_KEY = "INIT";

    /**
     * 根据条件获取评价记录申诉列表
     *
     * @param conditions 条件列表
     * @return 响应对象，包含评价记录申诉列表
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.getListByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealEntity>> getListByCondition(
        List<JyEvaluateRecordAppealEntity> conditions) {
        Response<List<JyEvaluateRecordAppealEntity>> response = new Response<>();
        response.toSucceed();
        List<JyEvaluateRecordAppealEntity> list = null;
        try {
            list = jyEvaluateRecordAppealDao.queryListByCondition(conditions);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.getListByCondition 根据条件查询数据异常,入参:{}", JsonHelper.toJson(conditions), e);
            response.toError("根据条件查询装车评价申诉列表失败！");
        }
        response.setData(list);
        return response;
    }

    @Override
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.submitAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> submitAppeal(List<JyEvaluateRecordAppealDto> entityList) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if (CollectionUtils.isEmpty(entityList)) {
            response.toError("待添加的数据为空！");
            response.setData(Boolean.FALSE);
            return response;
        }
        // 判断场地是否有申诉权限
        Response<Boolean> res = checkAppeal(entityList, response);
        if (res != null) {
            return res;
        }
        try {
            // 保存申诉数据
            jyEvaluateRecordAppealDao.batchInsert(entityList);
            // 保存申诉上传的图片
            List<JyAttachmentDetailEntity> annexList = buildJyAttachmentDetailEntityList(entityList);
            jyAttachmentDetailService.batchInsert(annexList);
            response.setData(Boolean.TRUE);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.batchAddJyEvaluateRecordAppeal 批量插入装车评价申诉数据异常", e);
            response.toError("批量插入装车评价申诉数据异常！");
            response.setData(Boolean.FALSE);
        }
        response.setData(Boolean.TRUE);
        return response;
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
            if (DateHelper.isDateMoreThanDaysAgo(permissions.getAppealClosureDate(), closeDay)) {
                response.toError("当前场地申诉已经被关闭！");
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
        if (Objects.nonNull(permissions)) {
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

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.getDetailByCondition", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    public Response<List<JyEvaluateRecordAppealDto>> getDetailByCondition(JyEvaluateRecordAppealDto condition) {
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
        }
        ArrayList<JyEvaluateRecordAppealDto> jyEvaluateRecordAppealDtos = getJyEvaluateRecordAppealDtos(list, entities);
        response.setData(jyEvaluateRecordAppealDtos);
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealService.checkAppeal", mState = {
        JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response<Boolean> checkAppeal(JyEvaluateRecordAppealRes res) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();

        if (res == null || res.getAppealList().isEmpty()) {
            response.toError("待审核的数据为空！");
            response.setData(Boolean.FALSE);
            return response;
        }

        List<Long> idList = getIdList(res);

        Map<Long, Integer> idMap = getIdMap(res);

        List<JyEvaluateRecordAppealEntity> dbList = jyEvaluateRecordAppealDao.queryByIdList(idList);
        if (dbList.isEmpty()) {
            response.toError();
            response.setMessage("待审核的数据为空！");
            response.setData(Boolean.FALSE);
            return response;
        }

        // 按审核结果，分批量进行更新
        JyEvaluateRecordAppealUpdateDto updateDto = createUpdateDto(EvaluateAppealStatusEnum.TIMED_OUT_AND_NOT_REVIEWED,
            EvaluateAppealResultStatusEnum.NO_HANDLE, res);
        JyEvaluateRecordAppealUpdateDto updatePassDto = createUpdateDto(EvaluateAppealStatusEnum.REVIEWED,
            EvaluateAppealResultStatusEnum.PASS, res);
        JyEvaluateRecordAppealUpdateDto updateRejectDto = createUpdateDto(EvaluateAppealStatusEnum.REVIEWED,
            EvaluateAppealResultStatusEnum.REJECT, res);

        // 按审核结果，构建需要更新的数据新集合
        getUpdatedDataList(dbList, updateDto, idMap, updatePassDto, updateRejectDto);
        try {
            // 进行数据库数据更新
            dbUpdate(updateDto, updatePassDto, updateRejectDto);
            // 统计审核次数和审核驳回次数，更新场地权限
            updateSiteEvaluateAndAppeal(res);
            // 审核通过的数据，同步到es
            esDataUpdate(res, updatePassDto);
        } catch (Exception e) {
            response.toError("装车评价申诉审核异常！");
            response.setData(Boolean.FALSE);
            log.error("JyEvaluateAppealServiceImpl.checkAppeal 装车评价申诉审核异常,入参:{}", JsonHelper.toJson(res), e);
        }
        response.setData(Boolean.TRUE);
        return response;
    }

    /**
     * 更新站点评价和申诉权限
     * @param res 评估记录申诉响应
     */
    private void updateSiteEvaluateAndAppeal(JyEvaluateRecordAppealRes res) {
        // 统计场地审核次数>3,下游乱评价，关闭下游未来7天的评价权限
        Integer checkAppealCount = jyEvaluateRecordAppealDao.getCheckAppealCount(res.getTargetSiteCode());
        if (Objects.nonNull(checkAppealCount) && checkAppealCount > Constants.APPEAL_COUNT_NUM){
            JyEvaluateAppealPermissionsEntity permissions =
                jyEvaluateAppealPermissionsDao.queryByCondition(res.getSourceSiteCode().intValue());

            // 场地评价和申诉权限记录为空，初始化记录，默认评价和申诉开启
            if (Objects.nonNull(permissions)) {
                JyEvaluateAppealPermissionsEntity entity = buildPermissionsEntity(res);
                jyEvaluateAppealPermissionsDao.insert(entity);
            }else {
                // 关闭场地评价权限
                JyEvaluateAppealPermissionsEntity entity =
                    buildUpdatePermissionsEvaluateEntity(permissions, res);
                jyEvaluateAppealPermissionsDao.updateEvaluateStatusById(entity);
            }
        }
        // 统计场地审核中驳回次数>3,申诉人乱申诉，关闭上游游未来7天的申诉权限
        Integer appealRejectCount = jyEvaluateRecordAppealDao.getAppealRejectCount(res.getTargetSiteCode());
        if (Objects.nonNull(appealRejectCount) && appealRejectCount > Constants.APPEAL_COUNT_NUM){
            JyEvaluateAppealPermissionsEntity permissions =
                jyEvaluateAppealPermissionsDao.queryByCondition(res.getTargetSiteCode().intValue());
            // 场地评价和申诉权限记录为空，初始化记录，默认评价和申诉开启
            if (Objects.nonNull(permissions)) {
                JyEvaluateAppealPermissionsEntity entity = buildPermissionsEntity(res);
                jyEvaluateAppealPermissionsDao.insert(entity);
            }else {
                // 关闭场地申诉权限
                JyEvaluateAppealPermissionsEntity entity =
                    buildUpdatePermissionsEntity(permissions, res);
                jyEvaluateAppealPermissionsDao.updateAppealStatusById(entity);
            }
        }

    }

    public JyEvaluateAppealPermissionsEntity buildUpdatePermissionsEntity(JyEvaluateAppealPermissionsEntity permissions, JyEvaluateRecordAppealRes res) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setId(permissions.getId());
        entity.setAppeal(Constants.EVALUATE_APPEAL_PERMISSIONS_0);
        entity.setAppealClosureDate(new Date());
        entity.setUpdateUserErp(res.getUpdateUserErp());
        entity.setUpdateUserName(res.getUpdateUserName());
        entity.setUpdateTime(new Date());
        return entity;
    }

    public JyEvaluateAppealPermissionsEntity buildUpdatePermissionsEvaluateEntity(JyEvaluateAppealPermissionsEntity permissions, JyEvaluateRecordAppealRes res) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setId(permissions.getId());
        entity.setEvaluate(Constants.EVALUATE_APPEAL_PERMISSIONS_0);
        entity.setEvaluateClosureDate(new Date());
        entity.setUpdateUserErp(res.getUpdateUserErp());
        entity.setUpdateUserName(res.getUpdateUserName());
        entity.setUpdateTime(new Date());
        return entity;
    }

    private JyEvaluateAppealPermissionsEntity buildPermissionsEntity(JyEvaluateRecordAppealRes res) {
        JyEvaluateAppealPermissionsEntity entity = new JyEvaluateAppealPermissionsEntity();
        entity.setSiteCode(res.getSourceSiteCode());
        entity.setAppeal(Constants.EVALUATE_APPEAL_PERMISSIONS_1);
        entity.setEvaluate(Constants.EVALUATE_APPEAL_PERMISSIONS_1);
        entity.setCreateUserErp(res.getUpdateUserErp());
        entity.setCreateUserName(res.getUpdateUserName());
        entity.setCreateTime(new Date());
        return entity;
    }

    /**
     * 更新ES数据
     * @param res 评价记录申诉响应对象
     */
    private void esDataUpdate(JyEvaluateRecordAppealRes res, JyEvaluateRecordAppealUpdateDto updatePassDto) {
        // 过滤出全部的历史不满意项中，没有进行申或者申诉驳回的数据
        ArrayList<AppealDimensionReq> reqArrayList = new ArrayList<>();
        for (AppealDimensionReq req : res.getDimensionList()) {
            if (!updatePassDto.getDimensionCodeList().contains(req.getDimensionCode())){
                reqArrayList.add(req);
            }
        }
        // 如果dimensionList为空，说明全部满意报表加工MQ实体
        EvaluateTargetInitDto targetInitDto = new EvaluateTargetInitDto();
        // 操作时间
        targetInitDto.setOperateTime(System.currentTimeMillis());
        // 操作人erp
        targetInitDto.setOperateUserErp(res.getUpdateUserErp());
        // 操作人姓名
        targetInitDto.setOperateUserName(res.getUpdateUserName());
        // 评价来源bizId
        targetInitDto.setSourceBizId(res.getSourceBizId());
        // 本次新增评价明细
        List<EvaluateDimensionReq> evaluateDimensionReqs = convertEvaluateDimensionList(reqArrayList);
        targetInitDto.setDimensionList(evaluateDimensionReqs);
        String businessId = res.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_INIT_BUSINESS_KEY
            + Constants.UNDER_LINE + targetInitDto.getOperateTime();
        evaluateTargetInitProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetInitDto));
    }

    /**
     * 将申诉维度列表转换为评价维度列表
     * @param appealDimensionReqList 申诉维度列表
     * @return evaluateDimensionReqs 评价维度列表
     * @throws NullPointerException 如果appealDimensionReqList为空时
     */
    private  List<EvaluateDimensionReq> convertEvaluateDimensionList(List<AppealDimensionReq> appealDimensionReqList){
        ArrayList<EvaluateDimensionReq> evaluateDimensionReqs = new ArrayList<>();
        for (AppealDimensionReq req : appealDimensionReqList) {
            EvaluateDimensionReq evaluateDimensionReq = new EvaluateDimensionReq();
            evaluateDimensionReq.setDimensionCode(req.getDimensionCode());
            evaluateDimensionReq.setImgUrlList(req.getImgUrlList());
            evaluateDimensionReq.setRemark(req.getReasons());
            evaluateDimensionReqs.add(evaluateDimensionReq);
        }

        return evaluateDimensionReqs;
    }


    /**
     * 更新数据库中的评估记录申诉信息
     * @param updateDto 要更新的申诉信息DTO
     * @param updatePassDto 要更新的通过申诉信息DTO
     * @param updateRejectDto 要更新的拒绝申诉信息DTO
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateAppealServiceImpl.dbUpdate", mState = {JProEnum.TP, JProEnum.FunctionError})
    private void dbUpdate(JyEvaluateRecordAppealUpdateDto updateDto, JyEvaluateRecordAppealUpdateDto updatePassDto,
        JyEvaluateRecordAppealUpdateDto updateRejectDto) {
        jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updateDto);
        jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updatePassDto);
        jyEvaluateRecordAppealDao.batchUpdateStatusByIds(updateRejectDto);
    }

    /**
     * 获取更新后的数据列表
     * @param dbList 数据库数据列表
     * @param updateDto 更新DTO
     * @param idMap ID映射表
     * @param updatePassDto 通过更新DTO
     * @param updateRejectDto 拒绝更新DTO
     */
    private void getUpdatedDataList(List<JyEvaluateRecordAppealEntity> dbList, JyEvaluateRecordAppealUpdateDto updateDto,
        Map<Long, Integer> idMap, JyEvaluateRecordAppealUpdateDto updatePassDto,
        JyEvaluateRecordAppealUpdateDto updateRejectDto) {
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
                    if (opinion.equals(Constants.CONSTANT_NUMBER_ONE)) {
                        updatePassDto.getIdList().add(entity.getId());
                        updatePassDto.getDimensionCodeList().add(entity.getDimensionCode());
                    } else if (opinion.equals(Constants.CONSTANT_NUMBER_TWO)) {
                        updateRejectDto.getIdList().add(entity.getId());
                        updateRejectDto.getDimensionCodeList().add(entity.getDimensionCode());
                    }
                }
            }
        }
    }

    private static Map<Long, Integer> getIdMap(JyEvaluateRecordAppealRes res) {
        Map<Long, Integer> idMap = res.getAppealList().stream()
            .collect(Collectors.toMap(map -> Long.valueOf(map.get(Constants.APPEAL_MAP_KEY_ID)),
                map -> map.get(Constants.APPEAL_MAP_KEY_OPINION)));
        return idMap;
    }

    private static List<Long> getIdList(JyEvaluateRecordAppealRes res) {
        List<Long> idList = res.getAppealList().stream()
            .map(map -> Long.valueOf(map.get(Constants.APPEAL_MAP_KEY_ID)))
            .collect(Collectors.toList());
        return idList;
    }

    /**
     * 创建更新数据传输对象
     * @param status 评价申诉状态枚举
     * @param result 评价申诉结果状态枚举
     * @param res 评价申诉响应数据对象
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
    public Response<Integer> getAppealRejectCount(Long loadSiteCode) {
        Response<Integer> response = new Response<>();
        response.toSucceed();
        if (Objects.isNull(loadSiteCode)){
            response.toError("待审核的数据为空！");
            return response;
        }

        Integer integer = null;
        try {
            integer = jyEvaluateRecordAppealDao.getAppealRejectCount(loadSiteCode);
        } catch (Exception e) {
            log.error("JyEvaluateAppealServiceImpl.getAppealRejectCount 根据场地编码查询驳回申诉条数异常,入参:{}", loadSiteCode, e);
            response.toError("根据场地编码查询驳回申诉条数异常！");
        }
        response.setData(integer);
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
    private static ArrayList<JyEvaluateRecordAppealDto> getJyEvaluateRecordAppealDtos(
        List<JyEvaluateRecordAppealEntity> list, List<JyAttachmentDetailEntity> entities) {
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(entities)) {
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
        return jyAttachmentDetailEntities;
    }
}
