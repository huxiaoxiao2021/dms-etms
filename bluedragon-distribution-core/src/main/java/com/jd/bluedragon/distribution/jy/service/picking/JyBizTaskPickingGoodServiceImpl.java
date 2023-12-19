package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodExtendDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 空铁提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
@Service
public class JyBizTaskPickingGoodServiceImpl implements JyBizTaskPickingGoodService{

    private static final Logger log = LoggerFactory.getLogger(JyBizTaskPickingGoodServiceImpl.class);

    @Autowired
    private JyBizTaskPickingGoodDao jyBizTaskPickingGoodDao;
    @Autowired
    private JyBizTaskPickingGoodExtendDao jyBizTaskPickingGoodExtendDao;

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
    public JyBizTaskPickingGoodEntity generateManualCreateTask(CurrentOperate site, User user) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(this.genPickingGoodTaskBizId(true));
        entity.setStartSiteId((long)site.getSiteCode());
        entity.setNextSiteId((long)site.getSiteCode());
        entity.setServiceNumber(entity.getBizId());
        entity.setStatus(PickingGoodStatusEnum.TO_PICKING.getCode());
        entity.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        entity.setManualCreatedFlag(Constants.NUMBER_ONE);
        entity.setCreateUserErp(user.getUserErp());
        entity.setCreateUserName(user.getUserName());
        entity.setUpdateUserErp(entity.getCreateUserErp());
        entity.setUpdateUserName(entity.getCreateUserName());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        logInfo("生成自建任务【{}】,entity={}", entity.getBizId(), JsonHelper.toJson(entity));
        jyBizTaskPickingGoodDao.insertSelective(entity);
        return entity;
    }

    @Override
    public boolean updateStatusByBizId(String bizId, Integer status) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(bizId);
        entity.setStatus(status);
        jyBizTaskPickingGoodDao.updateStatusByBizId(entity);
        return true;
    }

    @Override
    public JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId) {
        return jyBizTaskPickingGoodDao.findLatestEffectiveManualCreateTask(siteId);
    }


}
