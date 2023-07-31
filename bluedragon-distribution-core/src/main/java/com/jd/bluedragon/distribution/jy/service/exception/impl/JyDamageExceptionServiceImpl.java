package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionProcessStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionPackageType;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDamageDao;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description: 异常-破损 service 实现
 */
@Service
public class JyDamageExceptionServiceImpl extends JyExceptionStrategy implements JyDamageExceptionService {
    private final Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);

    @Resource
    private JyExceptionDamageDao jyExceptionDamageDao;

    @Resource
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;

    @Resource
    private BaseMajorManager baseMajorManager;

    @Resource
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Resource
    private JyExceptionService jyExceptionService;

    @Qualifier("redisClientCache")
    private Cluster redisClient;

    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.DAMAGE.getCode();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req) {

        JdCResponse<Boolean> response = new JdCResponse<>();
        String waybillCode = req.getBarCode();
        //todo  添加校验港澳单

        return response;
    }

    @Override
    public void dealExpDamageInfoByAbnormalReportOutCall(QcReportOutCallJmqDto qcReportJmqDto) {

        try {
            if (StringUtils.isBlank(qcReportJmqDto.getAbnormalDocumentNum())) {
                logger.error("abnormalDocumentNum 为空！");
                return;
            }

        } catch (Exception e) {
            logger.error("根据质控异常提报mq处理破损数据异常!");

        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getToProcessDamageCount", mState = {JProEnum.TP})
    public JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(Integer siteCode) {
        JyDamageExceptionToProcessCountDto toProcessCount = new JyDamageExceptionToProcessCountDto();
        // 获取待处理破损异常新增数量
        String bizIdAddSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + siteCode);
        if (StringUtils.isEmpty(bizIdAddSetStr)) {
            toProcessCount.setToProcessAddCount(0);
        }
        Set<String> oldBizIdAddSet = Arrays.stream(bizIdAddSetStr.split(",")).collect(Collectors.toSet());
        toProcessCount.setToProcessAddCount(oldBizIdAddSet.size());
        // 获取待处理破损异常数量
        String oldBizIdSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + siteCode);
        if (StringUtils.isEmpty(oldBizIdSetStr)) {
            toProcessCount.setToProcessCount(0);
            // 新增转未处理
            redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + siteCode, String.join(",", oldBizIdAddSet));
            return JdCResponse.ok(toProcessCount);
        }
        Set<String> oldBizIdSet = Arrays.stream(oldBizIdSetStr.split(",")).collect(Collectors.toSet());
        toProcessCount.setToProcessCount(oldBizIdSet.size());
        // 新增转未处理
        oldBizIdSet.addAll(oldBizIdAddSet);
        redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + siteCode, String.join(",", oldBizIdSet));
        return JdCResponse.ok(toProcessCount);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.readToProcessDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> readToProcessDamage(Integer siteCode) {
        redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + siteCode);
        redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + siteCode);
        return JdCResponse.ok();
    }

    /**
     * 写入待处理破损异常
     */
    private void writeToProcessDamage(Integer siteCode, String bizId) {
        String bizIdSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + siteCode);
        if (StringUtils.isEmpty(bizIdSetStr)) {
            Set<String> bizIdSet = new HashSet<>();
            bizIdSet.add(bizId);
            redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + siteCode, String.join(",", bizIdSet));
            return;
        }
        Set<String> oldBizIdSet = Arrays.stream(bizIdSetStr.split(",")).collect(Collectors.toSet());
        oldBizIdSet.add(bizId);
        redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + siteCode, String.join(",", oldBizIdSet));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.processTaskOfDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        JyExceptionDamageDto entity = new JyExceptionDamageDto();
        try {
            JyExceptionDamageEntity oldEntity = jyExceptionDamageDao.selectOneByBizId(entity.getBizId());
            if (oldEntity != null) {
                entity.setId(oldEntity.getId());
            }
            // 提交破损信息
            if (oldEntity == null || JyExceptionPackageType.FeedBackTypeEnum.DEFAULT.getCode().equals(oldEntity.getFeedBackType())) {
                this.saveDamage(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPAIR_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                // 1.修复下传
                this.finishFlow(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //2.直接下传
                this.finishFlow(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //3.更换包装下传
                this.replacePackagingHandover(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.DESTROY.getCode().equals(oldEntity.getFeedBackType())) {
                //4.报废
                this.finishFlow(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REVERSE_RETURN.getCode().equals(oldEntity.getFeedBackType())) {
                //5.逆向退回
                this.finishFlow(req, entity);
            } else {
                return JdCResponse.fail("客服反馈类型匹配失败" + req.getBizId());
            }
        } catch (RuntimeException e) {
            return JdCResponse.fail(e.getMessage());
        }
        return JdCResponse.ok();
    }

    private void finishFlow(ExpDamageDetailReq req, JyExceptionDamageDto entity) {
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        this.saveOrUpdate(entity);
        this.updateTask(entity);
    }

    private void updateTask(JyExceptionDamageDto entity) {
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(entity.getBizId());
        update.setStatus(JyExpStatusEnum.COMPLETE.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
        update.setUpdateUserErp(entity.getUpdateErp());
        update.setUpdateUserName(entity.getStaffName());
        update.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(update);
        jyExceptionService.recordLog(JyBizTaskExceptionCycleTypeEnum.CLOSE, update);
    }

    private void replacePackagingHandover(ExpDamageDetailReq req, JyExceptionDamageDto entity) {
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        entity.setVolumeRepairAfter(req.getVolumeRepairAfter());
        entity.setWeightRepairAfter(req.getWeightRepairAfter());
        this.saveImages(req, entity);
        this.saveOrUpdate(entity);
        this.updateTask(entity);
    }

    /**
     * 提交破损信息
     *
     * @param req
     * @return
     */
    private void saveDamage(ExpDamageDetailReq req, JyExceptionDamageDto entity) {
        this.dealTaskInfo(req, entity);
        if (!JyExceptionPackageType.SaveTypeEnum.DRAFT.getCode().equals(req.getSaveType())
                && !JyExceptionPackageType.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(req.getSaveType())) {
            throw new RuntimeException("保存类型错误!" + req.getBizId());
        }
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        this.copyRequestToEntity(req, entity);
        this.saveImages(req, entity);
        this.saveOrUpdate(entity);
    }

    private void saveOrUpdate(JyExceptionDamageEntity entity) {
        if (entity.getId() == null) {
            jyExceptionDamageDao.insertSelective(entity);
        } else {
            jyExceptionDamageDao.updateByBizId(entity);
        }
    }

    private void saveImages(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        if (!CollectionUtils.isEmpty(req.getActualImageUrlList())) {
            this.saveImages(req.getActualImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode(), entity);
        } else if (!CollectionUtils.isEmpty(req.getDealImageUrlList())) {
            this.saveImages(req.getActualImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode(), entity);
        }
    }

    /**
     * 保存图片
     *
     * @param imageUrlList
     * @param bitType
     * @param entity
     */
    private void saveImages(List<String> imageUrlList, String bitType, JyExceptionDamageEntity entity) {
        if (!CollectionUtils.isEmpty(imageUrlList)) {
            JyAttachmentDetailQuery query = new JyAttachmentDetailQuery();
            query.setBizId(entity.getBizId());
            query.setSiteCode(entity.getSiteCode());
            query.setBizType(bitType);
            // 删除老数据
            List<JyAttachmentDetailEntity> oldImageUrlList = jyAttachmentDetailService.queryDataListByCondition(query);
            if (!CollectionUtils.isEmpty(oldImageUrlList)) {
                oldImageUrlList.forEach(i -> jyAttachmentDetailService.delete(i));
            }
            List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
            for (String proveUrl : imageUrlList) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                annexEntity.setBizId(entity.getBizId());
                annexEntity.setSiteCode(entity.getSiteCode());
                annexEntity.setBizType(bitType);
                annexEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                annexEntity.setCreateUserErp(entity.getUpdateErp());
                annexEntity.setUpdateUserErp(entity.getUpdateErp());
                annexEntity.setAttachmentUrl(proveUrl);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, 1);
                annexEntity.setCreateTime(calendar.getTime());
                annexEntity.setUpdateTime(new Date());
                annexList.add(annexEntity);
            }
            jyAttachmentDetailService.batchInsert(annexList);
        }
    }


    /**
     * 校验或设置Erp相关信息
     *
     * @param req
     * @param entity
     * @return
     */
    private void validateOrSetErpInfo(ExpDamageDetailReq req, JyExceptionDamageDto entity) {
        // 获取erp相关信息
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            throw new RuntimeException("登录人ERP有误!" + req.getUserErp());
        }
        this.copyErpToEntity(baseStaffByErp, entity);
    }

    /**
     * 校验或设置Task相关信息
     *
     * @param req
     * @param entity
     * @return
     */
    private JyBizTaskExceptionEntity dealTaskInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        // 获取task相关信息
        JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (bizEntity == null) {
            throw new RuntimeException("无相关任务!bizId=" + req.getBizId());
        }
        this.copyTaskToEntity(bizEntity, entity);
        return bizEntity;
    }

    private void copyErpToEntity(BaseStaffSiteOrgDto baseStaffByErp, JyExceptionDamageDto entity) {
        if (entity.getId() == null) {
            entity.setSiteCode(baseStaffByErp.getSiteCode());
            entity.setSiteName(baseStaffByErp.getSiteName());
            entity.setCreateErp(baseStaffByErp.getErp());
            entity.setCreateTime(new Date());
        }
        entity.setStaffName(baseStaffByErp.getStaffName());
        entity.setUpdateErp(baseStaffByErp.getErp());
        entity.setUpdateTime(new Date());
    }

    private void copyTaskToEntity(JyBizTaskExceptionEntity task, JyExceptionDamageEntity entity) {
        entity.setBizId(task.getBizId());
        if (entity.getId() == null) {
            entity.setPackageCode(task.getBarCode());
        }
    }

    private void copyRequestToEntity(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        entity.setSaveType(req.getSaveType());
        entity.setVolumeRepairBefore(req.getVolumeRepairBefore());
        entity.setVolumeRepairAfter(req.getVolumeRepairAfter());
        entity.setWeightRepairBefore(req.getWeightRepairBefore());
        entity.setWeightRepairBefore(req.getWeightRepairAfter());
        entity.setDamageType(req.getDamageType());
        entity.setRepairType(req.getRepairType());
    }
}
