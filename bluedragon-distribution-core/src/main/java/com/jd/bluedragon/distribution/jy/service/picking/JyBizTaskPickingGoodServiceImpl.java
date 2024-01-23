package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.AirRailTaskCountDto;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodSubsidiaryDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.*;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 空铁提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
@Service
public class JyBizTaskPickingGoodServiceImpl implements JyBizTaskPickingGoodService{

    private static final Logger log = LoggerFactory.getLogger(JyBizTaskPickingGoodServiceImpl.class);
    private static final String MANUAL_CREATE_GROUP_CODE = "XUNI_ZJ";
    private static final String MANUAL_CREATE_GROUP_NAME = "自建任务分组";

    @Autowired
    private JyBizTaskPickingGoodDao jyBizTaskPickingGoodDao;
    @Autowired
    private JyBizTaskPickingGoodSubsidiaryDao jyBizTaskPickingGoodSubsidiaryDao;
    @Autowired
    private JyBizTaskPickingGoodTransactionManager taskPickingGoodTransactionManager;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    @Override
    public JyBizTaskPickingGoodEntity findByBizIdWithYn(String bizId, Boolean ignoreYn) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(bizId);
        if(!Boolean.TRUE.equals(ignoreYn)) {
            entity.setYn(Constants.YN_YES);
        }
        return jyBizTaskPickingGoodDao.findByBizIdWithYn(entity);
    }

    @Override
    public String genPickingGoodTaskBizId(Boolean isNoTaskFlag) {
        String bizIdPre = Boolean.TRUE.equals(isNoTaskFlag) ? JyBizTaskPickingGoodEntity.BIZ_PREFIX_NOTASK : JyBizTaskPickingGoodEntity.BIZ_PREFIX;
        String ownerKey = String.format(bizIdPre, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    @Override
    public int updateTaskByBizIdWithCondition(JyBizTaskPickingGoodEntityCondition entity) {
        return jyBizTaskPickingGoodDao.updateTaskByBizIdWithCondition(entity);
    }

    @Override
    public JyBizTaskPickingGoodEntity generateManualCreateTask(CurrentOperate site, User user, Integer taskType) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(this.genPickingGoodTaskBizId(true));
        entity.setEndNodeCode(MANUAL_CREATE_GROUP_CODE);
        entity.setEndNodeName(MANUAL_CREATE_GROUP_NAME);
//        entity.setStartSiteId((long)site.getSiteCode());
        entity.setNextSiteId((long)site.getSiteCode());
        entity.setServiceNumber(entity.getBizId());
        entity.setStatus(PickingGoodStatusEnum.TO_PICKING.getCode());
        entity.setTaskType(taskType);
        entity.setManualCreatedFlag(Constants.NUMBER_ONE);
        entity.setCreateUserErp(user.getUserErp());
        entity.setCreateUserName(user.getUserName());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        List<JyBizTaskPickingGoodEntity> list = Arrays.asList(entity);
        taskPickingGoodTransactionManager.batchInsertPickingGoodTask(list, null);
        logInfo("生成自建任务【{}】,entity={}", entity.getBizId(), JsonHelper.toJson(entity));
        return entity;
    }

    @Override
    public boolean finishPickingTaskByBizId(String bizId, Integer completeNode, User operator) {
        Date now = new Date();
        JyBizTaskPickingGoodEntityCondition condition = new JyBizTaskPickingGoodEntityCondition();
        condition.setBizId(bizId);
        condition.setCompleteNode(completeNode);
        condition.setStatus(PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
        condition.setUpdateTime(now);
        condition.setUpdateUserErp(operator.getUserErp());
        condition.setUpdateUserName(operator.getUserName());
        condition.setPickingCompleteTime(now);
        jyBizTaskPickingGoodDao.updateTaskByBizIdWithCondition(condition);
        return true;
    }

    @Override
    public JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId, Integer taskType) {
        return jyBizTaskPickingGoodDao.findLatestEffectiveManualCreateTask(siteId, taskType);
    }

    @Override
    public JyBizTaskPickingGoodEntity findLatestTaskByBusinessNumber(String businessNumber, Integer taskType) {
        return jyBizTaskPickingGoodDao.findLatestTaskByBusinessNumber(businessNumber, taskType);
    }

    @Override
    public List<JyBizTaskPickingGoodEntity> findAllTaskByBusinessNumber(String businessNumber, Integer taskType) {
        return jyBizTaskPickingGoodDao.findAllTaskByBusinessNumber(businessNumber, taskType);
    }

    @Override
    public int deleteTaskByBusinessNumber(String businessNumber, Integer taskType) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBusinessNumber(businessNumber);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(Constants.SYS_NAME);
        entity.setUpdateUserName(Constants.SYS_NAME);
        entity.setTaskType(taskType);
        return jyBizTaskPickingGoodDao.deleteByBusinessNumber(entity);
    }

    @Override
    public int deleteTaskSubsidiaryByBusinessNumber(String businessNumber, Integer taskType) {
        JyBizTaskPickingGoodSubsidiaryEntity entity = new JyBizTaskPickingGoodSubsidiaryEntity();
        entity.setBusinessNumber(businessNumber);
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(Constants.SYS_NAME);
        entity.setUpdateUserName(Constants.SYS_NAME);
        entity.setTaskType(taskType);
        return jyBizTaskPickingGoodSubsidiaryDao.deleteByBusinessNumber(entity);
    }

    @Override
    public List<JyBizTaskPickingGoodEntity> listTaskGroupByPickingNodeCode(JyPickingTaskGroupQueryDto queryDto) {
        return jyBizTaskPickingGoodDao.listTaskGroupByPickingNodeCode(queryDto);
    }

    @Override
    public List<JyBizTaskPickingGoodEntity> listTaskByPickingNodeCode(JyPickingTaskBatchQueryDto queryDto) {
        return jyBizTaskPickingGoodDao.listTaskByPickingNodeCode(queryDto);
    }

    @Override
    public List<AirRailTaskCountDto> countAllStatusByPickingSiteId(AirRailTaskCountQueryDto countQueryDto) {
        List<AirRailTaskCountDto> countList = new ArrayList<>();
        for (PickingGoodStatusEnum statusEnum : PickingGoodStatusEnum.values()) {
            countQueryDto.setStatus(statusEnum.getCode());
            AirRailTaskCountDto dto = jyBizTaskPickingGoodDao.countStatusByCondition(countQueryDto);

            dto.setStatus(statusEnum.getCode());
            dto.setStatusName(statusEnum.getName());
            countList.add(dto);
        }
        return countList;
    }

    @Override
    public int batchFinishPickingTaskByBizId(JyPickingTaskBatchUpdateDto updateDto) {
        return jyBizTaskPickingGoodDao.batchFinishPickingTaskByBizId(updateDto);
    }

    @Override
    public List<String> pageRecentCreatedManualBizId(JyBizTaskPickingGoodQueryDto queryDto) {
        return jyBizTaskPickingGoodDao.pageRecentCreatedManualBizId(queryDto);
    }

    @Override
    public void batchInsertTask(List<JyBizTaskPickingGoodEntity> taskEntityList) {
        jyBizTaskPickingGoodDao.batchInsert(taskEntityList);
    }

    @Override
    public void batchInsertTaskSubsidiary(List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList) {
        jyBizTaskPickingGoodSubsidiaryDao.batchInsert(subsidiaryEntityList);
    }

    @Override
    public List<JyBizTaskPickingGoodEntity> listTaskByPickingSiteId(JyPickingTaskBatchQueryDto queryDto) {
        return jyBizTaskPickingGoodDao.listTaskByPickingSiteId(queryDto);
    }

    @Override
    public List<String> findManualCreateTaskBizIds(List<String> bizIdList) {
        return jyBizTaskPickingGoodDao.findManualCreateTaskBizIds(bizIdList);
    }

    @Override
    public List<String> listBizIdByLastSiteId(JyBizTaskPickingGoodSubsidiaryEntity entity) {
        return jyBizTaskPickingGoodSubsidiaryDao.listBizIdByLastSiteId(entity);
    }

    @Override
    public List<JyBizTaskPickingGoodSubsidiaryEntity> listBatchInfoByBizId(List<String> bizIdList) {
        if (CollectionUtils.isEmpty(bizIdList)) {
            return new ArrayList<>();
        }
        return jyBizTaskPickingGoodSubsidiaryDao.listBatchInfoByBizId(bizIdList);
    }

    @Override
    public List<String> listAllBizByPickingSiteId(JyPickingTaskBatchQueryDto queryDto) {
        return jyBizTaskPickingGoodDao.listAllBizByPickingSiteId(queryDto);
    }
}
