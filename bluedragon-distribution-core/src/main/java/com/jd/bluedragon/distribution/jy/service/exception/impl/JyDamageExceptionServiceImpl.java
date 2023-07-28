package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionProcessStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionPackageType;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDamageDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.processTaskOfDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        JyExceptionDamageEntity entity = new JyExceptionDamageEntity();
        try {
            JyExceptionDamageEntity oldEntity = jyExceptionDamageDao.selectOneByBizId(entity.getBizId());
            // 提交破损信息
            if (oldEntity == null || JyExceptionPackageType.FeedBackTypeEnum.DEFAULT.getCode().equals(oldEntity.getFeedBackType())) {
                this.saveDamage(req, entity);
                // 1.修复下传
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPAIR_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //2.直接下传
            } else if (JyExceptionPackageType.FeedBackTypeEnum.HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //3.更换包装下传
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //4.报废
            } else if (JyExceptionPackageType.FeedBackTypeEnum.DESTROY.getCode().equals(oldEntity.getFeedBackType())) {
                //5.逆向退回
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REVERSE_RETURN.getCode().equals(oldEntity.getFeedBackType())) {

            } else {
                return JdCResponse.fail("客服反馈类型匹配失败" + req.getBizId());
            }


        } catch (RuntimeException e) {
            return JdCResponse.fail(e.getMessage());
        }

        return JdCResponse.ok();
    }

    /**
     * 提交破损信息
     *
     * @param req
     * @return
     */
    private void saveDamage(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
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
    private void validateOrSetErpInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
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

//    private JdCResponse<Boolean> validateBaseInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
//
//        if (!Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), bizEntity.getStatus())) {
//            return JdCResponse.fail("当前任务已被处理,请勿重复操作!bizId=" + req.getBizId());
//        }
//    }

    private JdCResponse<Boolean> savaOrUpdateDamage(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {

        //修改状态为 status处理中-processingStatus匹配中
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(req.getBizId());
        update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode());
        update.setUpdateUserErp(req.getUserErp());
        update.setUpdateUserName(entity.getUpdateErp());
        update.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(update);
//        recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT,update);
        return JdCResponse.ok();
    }

    private void copyErpToEntity(BaseStaffSiteOrgDto baseStaffByErp, JyExceptionDamageEntity entity) {
        entity.setSiteCode(baseStaffByErp.getSiteCode());
        entity.setSiteName(baseStaffByErp.getSiteName());
//        entity.setCreateErp(baseStaffByErp.getErp());
//        entity.setCreateTime(new Date());
        entity.setUpdateErp(baseStaffByErp.getErp());
        entity.setUpdateTime(new Date());
    }

    private void copyTaskToEntity(JyBizTaskExceptionEntity task, JyExceptionDamageEntity entity) {
        entity.setBizId(task.getBizId());
        entity.setPackageCode(task.getBarCode());
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
