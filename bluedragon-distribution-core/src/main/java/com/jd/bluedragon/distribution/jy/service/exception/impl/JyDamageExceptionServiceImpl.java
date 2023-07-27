package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionProcessStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionPackageType;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDamageDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description: 异常-破损 service 实现
 */
@Service
public class JyDamageExceptionServiceImpl implements JyDamageExceptionService {
    @Resource
    private JyExceptionDamageDao jyExceptionDamageDao;

    @Resource
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;

    @Resource
    private BaseMajorManager baseMajorManager;
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.processTaskOfDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        if (JyExceptionPackageType.SaveTypeEnum.DRAFT.getCode().equals(req.getSaveType())) {

        }
        return null;
    }
    private JdCResponse<Boolean> saveDraftDamage(ExpDamageDetailReq req){
        return null;
    }
    private JdCResponse<Boolean> saveDamage(ExpDamageDetailReq req) {
        JyExceptionScrappedPO entity = new JyExceptionScrappedPO();
        entity.setBizId(req.getBizId());
        jyExceptionDamageDao.insertSelective(entity);
        return null;
    }
    private JdCResponse<Boolean> savaOrUpdateDamage(ExpDamageDetailReq req, JyExceptionScrappedPO entity){
//        PositionDetailRecord position = getPosition(req.getPositionCode());
//        if (position == null) {
//            return JdCResponse.fail("岗位码有误!");
//        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            return JdCResponse.fail("登录人ERP有误!" + req.getUserErp());
        }


                JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (bizEntity == null) {
            return JdCResponse.fail("无相关任务!bizId=" + req.getBizId());
        }
        if (!Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), bizEntity.getStatus())) {
            return JdCResponse.fail("当前任务已被处理,请勿重复操作!bizId=" + req.getBizId());
        }
        //修改状态为 status处理中-processingStatus匹配中
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(req.getBizId());
        update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode());
        update.setUpdateUserErp(req.getUserErp());
        update.setUpdateUserName(baseStaffByErp.getStaffName());
        update.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(update);
//        recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT,update);
        return JdCResponse.ok();
    }

    private void copyErpToEntity(BaseStaffSiteOrgDto baseStaffByErp,JyExceptionScrappedPO entity){
        entity.setSiteCode(baseStaffByErp.getSiteCode());
        entity.setSiteName(baseStaffByErp.getSiteName());
    }
}
