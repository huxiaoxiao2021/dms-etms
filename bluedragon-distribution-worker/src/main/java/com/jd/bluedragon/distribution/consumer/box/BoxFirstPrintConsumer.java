package com.jd.bluedragon.distribution.consumer.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageFlowService;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageService;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;


/**
 * @author liwenji
 * @description 包裹首次打印消息：生成集包任务
 * @date 2023-10-13 10:48
 */
@Service("boxFirstPrintConsumer")
@Slf4j
public class BoxFirstPrintConsumer extends MessageBaseConsumer {

    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;

    @Autowired
    private JimDbLock jimDbLock;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private BaseMajorManager baseMajorManager;


    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    private JyBizTaskCollectPackageFlowService jyBizTaskCollectPackageFlowService;
    
    @Override
    public void consume(Message message) throws Exception {

        if (ObjectHelper.isEmpty(message) || StringUtils.isEmpty(message.getText())) {
            log.warn("BoxFirstPrintConsumer 消息为空！");
            return;
        }

        Box box = JsonHelper.fromJson(message.getText(), Box.class);
        if (box == null) {
            log.info("首次打印箱号json转换失败：{}", JsonHelper.toJson(message));
            return;
        }

        String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, box.getCode());
        if (!jimDbLock.lock(boxLockKey, box.getCode(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        // 查询当前箱号是否存在任务
        JyBizTaskCollectPackageEntity oldTask = jyBizTaskCollectPackageService.findByBoxCode(box.getCode());
        if (oldTask != null && !dmsConfigManager.getPropertyConfig().getCollectPackageTaskRefreshSwitch()) {
            log.info("已存在该箱号的集包任务：{}", box.getCode());
            return;
        }

        try {
            JyBizTaskCollectPackageEntity newTask = convertToTask(box, oldTask);
            // 创建任务并保存任务流向信息
            jyBizTaskCollectPackageService.createTaskAndFlowInfo(newTask, oldTask);
        } catch (JyBizException e) {
            // 自定义异常不重试
            log.error("首次打印箱号生成箱任务失败：{}", JsonHelper.toJson(message), e);
        } catch (Exception e) {
            log.error("保存集包任务信息异常：{}", JsonHelper.toJson(message), e);
        } finally {
            jimDbLock.releaseLock(boxLockKey, box.getCode());
        }
    }

    private JyBizTaskCollectPackageEntity convertToTask(Box box, JyBizTaskCollectPackageEntity oldBox) {
        JyBizTaskCollectPackageEntity entity = new JyBizTaskCollectPackageEntity();
        entity.setBoxCode(box.getCode());
        entity.setEndSiteId(box.getReceiveSiteCode().longValue());
        entity.setEndSiteName(box.getReceiveSiteName());
        entity.setStartSiteId(box.getCreateSiteCode().longValue());
        entity.setStartSiteName(box.getCreateSiteName());
        entity.setTransportType(box.getTransportType());
        entity.setBoxType(box.getType());
        entity.setMixBoxType(box.getMixBoxType());
        entity.setTaskStatus(JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode());
        entity.setYn(Boolean.TRUE);
        entity.setCreateTime(new Date());
        if (oldBox != null) {
            entity.setBizId(oldBox.getBizId());
        }else {
            entity.setBizId(genTaskBizId());
        }
        if (box.getUpdateUserCode() != null) {
            BaseStaffSiteOrgDto updateUser = baseMajorManager.getBaseStaffInAllRoleByStaffNo(box.getUpdateUserCode());
            if (updateUser != null) {
                entity.setUpdateUserErp(updateUser.getErp());
                entity.setUpdateUserName(box.getUpdateUser());
                entity.setUpdateTime(new Date());
            }
        }
        if (box.getCreateUserCode() != null) {
            BaseStaffSiteOrgDto createUser = baseMajorManager.getBaseStaffInAllRoleByStaffNo(box.getCreateUserCode());
            if (createUser != null) {
                entity.setCreateUserErp(createUser.getErp());
                entity.setCreateUserName(box.getCreateUser());
            }
        }

        return entity;
    }


    private String genTaskBizId() {
        String ownerKey = String.format(JyBizTaskCollectPackageEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }
}
