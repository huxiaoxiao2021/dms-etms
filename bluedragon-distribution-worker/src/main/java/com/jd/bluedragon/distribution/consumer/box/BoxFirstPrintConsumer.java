package com.jd.bluedragon.distribution.consumer.box;

import com.jd.bluedragon.common.dto.collectpackage.enums.CollectionPackageTaskStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageFlowService;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author liwenji
 * @description 包裹首次对打印消息
 * @date 2023-10-13 10:48
 */
@Service("boxFirstPrintConsumer")
@Slf4j
public class BoxFirstPrintConsumer extends MessageBaseConsumer {
    
    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;
    
    @Autowired
    private BoxLimitConfigManager boxLimitConfigManager;
    
    @Autowired
    private JyBizTaskCollectPackageFlowService jyBizTaskCollectPackageFlowService;
    
    @Override
    public void consume(Message message) throws Exception {

        if (ObjectHelper.isEmpty(message) || StringUtils.isEmpty(message.getText())){
            log.warn("BoxFirstPrintConsumer 消息为空！");
            return;
        }
        try{
            Box box = JsonHelper.fromJson(message.getText(), Box.class);
            if (box == null) {
                log.info("首次打印箱号json转换失败：{}", JsonHelper.toJson(message));
            }
            // 保存任务和流向信息
            saveTaskAndFlowInfo(box);
        }catch (JyBizException e) {
            // 自定义异常不重试
            log.error("首次打印箱号生成箱任务失败：{}",JsonHelper.toJson(message),e);
        }catch (Exception e) {
            log.error("保存集包任务信息异常：{}", JsonHelper.toJson(message), e);
        }
    }

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveTaskAndFlowInfo(Box box) {
        // 查询当前箱号是否存在任务
        JyBizTaskCollectPackageEntity oldBox = jyBizTaskCollectPackageService.findByBoxCode(box.getCode());
        boolean insert = oldBox == null;
        
        // 保存箱号任务
        JyBizTaskCollectPackageEntity task = convertToTask(box);

        log.info("新增或更新集包任务信息：{}", JsonHelper.toJson(task));
        if (insert) {
            task.setBizId(genTaskBizId());
            jyBizTaskCollectPackageService.save(task);
        }else {
            task.setBizId(oldBox.getBizId());
            task.setId(oldBox.getId());
            jyBizTaskCollectPackageService.updateById(task);
        }

        // 如果支持混装，保存当前流向集合
        if (MixBoxTypeEnum.MIX_ENABLE.getCode().equals(task.getMixBoxType()) && insert) {
            jyBizTaskCollectPackageFlowService.batchInsert(getMixBoxFlowList(task));
        }
    }

    /**
     * 查询混装的流向集合（查询混装的集包的流向集合）
     *
     * @return
     */
    private List<JyBizTaskCollectPackageFlowEntity> getMixBoxFlowList(JyBizTaskCollectPackageEntity task) {
        CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(task);
        List<CollectBoxFlowDirectionConf> collectBoxFlowDirectionConfList = boxLimitConfigManager.listCollectBoxFlowDirection(con);
        if (CollectionUtils.isEmpty(collectBoxFlowDirectionConfList)) {
            log.info("包裹号: {}未查询到对应目的地的可混装的流向集合！ request：{}",task.getBoxCode(),JsonHelper.toJson(con));
            throw new JyBizException("未查询到对应目的地的可混装的流向集合!");
        }
        return collectBoxFlowDirectionConfList.stream().map(item -> {
            JyBizTaskCollectPackageFlowEntity entity = new JyBizTaskCollectPackageFlowEntity();
            entity.setBoxCode(task.getBoxCode());
            entity.setCreateTime(task.getCreateTime());
            entity.setCreateUserErp(task.getCreateUserErp());
            entity.setStartSiteId(task.getStartSiteId());
            entity.setStartSiteName(task.getStartSiteName());
            entity.setCreateUserName(task.getCreateUserName());
            entity.setCollectPackageBizId(task.getBizId());
            entity.setEndSiteId(item.getEndSiteId().longValue());
            entity.setEndSiteName(item.getEndSiteName());
            entity.setUpdateTime(task.getUpdateTime());
            entity.setUpdateUserErp(task.getUpdateUserErp());
            entity.setUpdateUserName(task.getUpdateUserName());
            entity.setYn(Boolean.TRUE);
            return entity;
        }).collect(Collectors.toList());
    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(JyBizTaskCollectPackageEntity task) {
        CollectBoxFlowDirectionConf conf =new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(task.getStartSiteId().intValue());
        conf.setBoxReceiveId(task.getEndSiteId().intValue());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }
    
    private JyBizTaskCollectPackageEntity convertToTask(Box box) {
        JyBizTaskCollectPackageEntity entity = new JyBizTaskCollectPackageEntity();
        entity.setBoxCode(box.getCode());
        entity.setEndSiteId(box.getReceiveSiteCode().longValue());
        entity.setEndSiteName(box.getReceiveSiteName());
        entity.setStartSiteId(box.getCreateSiteCode().longValue());
        entity.setStartSiteName(box.getCreateSiteName());
        entity.setTransportType(box.getTransportType());
        entity.setBoxType(box.getType());
        entity.setMixBoxType(box.getMixBoxType());
        entity.setTaskStatus(CollectionPackageTaskStatusEnum.TO_COLLECTION.getCode());
        entity.setYn(box.getYn() == 1);
        if (box.getUpdateUserCode() != null) {
            BaseStaffSiteOrgDto updateUser = baseMajorManager.getBaseStaffInAllRoleByStaffNo(box.getUpdateUserCode());
            if (updateUser != null) {
                entity.setUpdateUserErp(updateUser.getErp());
                entity.setUpdateUserName(box.getUpdateUser());
                entity.setUpdateTime(box.getUpdateTime());
            }
        }
        if (box.getCreateUserCode() != null) {
            BaseStaffSiteOrgDto createUser = baseMajorManager.getBaseStaffInAllRoleByStaffNo(box.getCreateUserCode());
            if (createUser != null) {
                entity.setCreateUserErp(createUser.getErp());
                entity.setCreateUserName(box.getCreateUser());
                entity.setCreateTime(box.getCreateTime());
            }
        }

        return entity;
    }

    private String genTaskBizId() {
        String ownerKey = String.format(JyBizTaskCollectPackageEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }
}
