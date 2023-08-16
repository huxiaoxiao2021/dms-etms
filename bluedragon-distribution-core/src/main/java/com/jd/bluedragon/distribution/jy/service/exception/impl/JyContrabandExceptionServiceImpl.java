package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpContrabandReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionContrabandEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionContrabandDao;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandEntity;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: ext.xuwenrui
 * @Date: 2023/8/15 10:18
 * @Description: 违禁品上报 service 实现
 */
@Service
public class JyContrabandExceptionServiceImpl implements JyContrabandExceptionService {
    private final Logger logger = LoggerFactory.getLogger(JyContrabandExceptionServiceImpl.class);


    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyExceptionContrabandDao jyExceptionContrabandDao;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyContrabandExceptionServiceImpl.processTaskOfContraband", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req) {
        try {
            logger.info("processTaskOfContraband req:{}", JSON.toJSONString(req));
            this.validateReq(req);
            JyExceptionContrabandEntity entity = buildEntity(req);
            this.saveImages(req, entity);
            jyExceptionContrabandDao.insertSelective(entity);
        } catch (Exception e) {
            logger.error("提交违禁品上报报错:", e);
            return JdCResponse.fail(e.getMessage());
        }
        return JdCResponse.ok();
    }

    private JyExceptionContrabandEntity buildEntity(ExpContrabandReq req) {
        JyExceptionContrabandEntity entity = new JyExceptionContrabandEntity();
        entity.setContrabandType(req.getContrabandType());
        String bizId = req.getBarCode() + "_" + entity.getSiteCode();
        entity.setBizId(bizId);
        entity.setBarCode(req.getBarCode());
        entity.setDescription(req.getDescription());

        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaff != null) {
            entity.setSiteCode(baseStaff.getSiteCode());
            entity.setSiteName(baseStaff.getSiteName());
            entity.setCreateErp(baseStaff.getAccountNumber());
            entity.setCreateStaffName(baseStaff.getStaffName());
            entity.setCreateTime(new Date());
            entity.setUpdateErp(baseStaff.getAccountNumber());
            entity.setUpdateStaffName(baseStaff.getAccountNumber());
            entity.setUpdateTime(entity.getCreateTime());
        }
        return entity;
    }

    /**
     * 保存图片
     *
     * @param entity
     */
    private void saveImages(ExpContrabandReq req, JyExceptionContrabandEntity entity) {
        if (!CollectionUtils.isEmpty(req.getImageUrlList())) {
            List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
            for (String proveUrl : req.getImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                annexEntity.setBizId(entity.getBizId());
                annexEntity.setSiteCode(entity.getSiteCode());
                annexEntity.setBizType(JyAttachmentBizTypeEnum.CONTRABAND_UPLOAD_EXCEPTION.getCode());
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
     * 校验参数
     *
     * @param req
     */
    private void validateReq(ExpContrabandReq req) {
        if (req == null) {
            throw new RuntimeException("请求参数不能为空");
        }
        if (req.getContrabandType() == null) {
            throw new RuntimeException("违禁品类型不能为空");
        }
        boolean noneMatchContrabandType = Arrays.stream(JyExceptionContrabandEnum.ContrabandTypeEnum.values())
                .noneMatch(o -> o.getCode().equals(req.getContrabandType()));
        if (noneMatchContrabandType) {
            throw new RuntimeException("违禁品类型错误");
        }
        if (StringUtils.isEmpty(req.getBarCode())) {
            throw new RuntimeException("包裹号不能为空");
        }
        if (StringUtils.isEmpty(req.getUserErp())) {
            throw new RuntimeException("用户ERP不能为空");
        }

        JyExceptionContrabandEntity query = new JyExceptionContrabandEntity();
        query.setSiteCode(req.getSiteId());
        query.setBarCode(req.getBarCode());
        query.setContrabandType(req.getContrabandType());
        List<JyExceptionContrabandEntity> list = jyExceptionContrabandDao.selectByParams(query);
        if (!CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("请勿重复提交");
        }
    }
}
