package com.jd.bluedragon.distribution.consumer.cyclebox;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.ministore.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreSortingProcessEvent;
import com.jd.bluedragon.distribution.ministore.enums.ProcessTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jd.bluedragon.distribution.ministore.enums.ProcessTypeEnum.SEND_JIEHUOCANG;
import static com.jd.bluedragon.distribution.ministore.enums.ProcessTypeEnum.SEND_SORT_CENTER;
import static com.jd.bluedragon.distribution.ministore.enums.SiteTypeEnum.JIEHUOCANG;

/**
 * 循环集包袋处理消费
 */
@Service("deliverGoodsNoticeConsumer")
public class DeliverGoodsNoticeConsumer extends MessageBaseConsumer {
    private final Logger log = LoggerFactory.getLogger(DeliverGoodsNoticeConsumer.class);

    @Autowired
    @Qualifier("cycleMaterialSendMQ")
    private DefaultJMQProducer cycleMaterialSendMQ;

    @Autowired
    CycleBoxService cycleBoxService;

    @Autowired
    private SortingService sortingService;
    @Autowired
    private MiniStoreService miniStoreService;
    @Autowired
    @Qualifier("miniStoreSortProcessProducer")
    private DefaultJMQProducer miniStoreSortProcessProducer;
    @Autowired
    BaseMajorManager baseMajorManager;

    @Autowired
    private BoxService boxService;

    @Override
    public void consume(Message message) {
        log.info("DeliverGoodsNoticeConsumer start...");
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[DeliverGoodsNoticeConsumer消费]MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BoxMaterialRelationMQ context = JsonHelper.fromJsonUseGson(message.getText(), BoxMaterialRelationMQ.class);
        deliverGoodsNoticeConsumerMessage(context);
        miniStoreDeliverGoodsConsumerMessage(context);
    }

    void deliverGoodsNoticeConsumerMessage(BoxMaterialRelationMQ context){
        if (StringUtils.isBlank(context.getBoxCode()) || !BusinessUtil.isBoxcode(context.getBoxCode()) || StringUtils.isBlank(context.getSiteCode())){
            return;
        }
        try {
            this.sendBoxMaterial(context);
            this.sendBoxNestMaterial(context, 1);
        }catch (Exception e) {
            log.error("[DeliverGoodsNoticeConsumer]消费异常，MQ message body:{}" , JsonHelper.toJson(context), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + JsonHelper.toJson(context), e);
        }
    }

    void sendBoxNestMaterial(BoxMaterialRelationMQ context, Integer level) {
        if(level >= Constants.BOX_NESTED_MAX_DEPTH){
            return;
        }
        try {
            Box boxNestParam = new Box();
            boxNestParam.setCode(context.getBoxCode());
            final List<Box> boxNestList = boxService.listAllDescendantsByParentBox(boxNestParam);
            if (CollectionUtils.isEmpty(boxNestList)) {
                return;
            }
            for (Box box : boxNestList) {
                String materialCode = cycleBoxService.getBoxMaterialRelation(box.getCode());
                if (StringUtils.isBlank(materialCode)){
                    continue;
                }
                final BoxMaterialRelationMQ boxMaterialRelationMQ = new BoxMaterialRelationMQ();
                BeanCopyUtil.copy(context, boxMaterialRelationMQ);
                boxMaterialRelationMQ.setBoxCode(box.getCode());
                boxMaterialRelationMQ.setMaterialCode(materialCode);
                this.sendBoxMaterial(boxMaterialRelationMQ);

                // 如果有嵌套子级
                if (CollectionUtils.isNotEmpty(box.getChildren())) {
                    sendBoxNestMaterial(boxMaterialRelationMQ, level++);
                }
            }
        } catch (Exception e) {
            log.error("[DeliverGoodsNoticeConsumer] sendBoxNestMaterial 消费异常，MQ message body:{}" , JsonHelper.toJson(context), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + JsonHelper.toJson(context), e);
        }
    }

    void sendBoxMaterial(BoxMaterialRelationMQ context) {
        try {
            String materialCode = cycleBoxService.getBoxMaterialRelation(context.getBoxCode());
            if (StringUtils.isBlank(materialCode)) {
                return;
            }
            context.setMaterialCode(materialCode);

            List<String> waybillCodeList = new ArrayList<>();
            List<String> packageCodeList = new ArrayList<>();

            Sorting sorting = new Sorting();
            sorting.setBoxCode(context.getBoxCode());
            sorting.setCreateSiteCode(Integer.parseInt(context.getSiteCode()));
            List<Sorting> list = sortingService.findByBoxCode(sorting);
            if (CollectionUtils.isEmpty(list)) {
                final Box boxExist = boxService.findBoxByCode(context.getBoxCode());
                if (boxExist != null) {
                    sorting.setCreateSiteCode(boxExist.getCreateSiteCode());
                    list = sortingService.findByBoxCode(sorting);
                }
            }

            if (CollectionUtils.isEmpty(list)) {
                log.warn("[DeliverGoodsNoticeConsumer]消费异常,箱中无任何单据：{}", JsonHelper.toJson(context));
                return;
            }
            Set<String> waybillCodeSet = new HashSet<>();
            for (Sorting sort : list) {
                waybillCodeSet.add(sort.getWaybillCode());
                packageCodeList.add(sort.getPackageCode());
            }
            waybillCodeList = new ArrayList<>(waybillCodeSet);
            context.setWaybillCode(waybillCodeList);
            context.setPackageCode(packageCodeList);
            context.setOperatorTime(new Date());

            cycleMaterialSendMQ.send(context.getMaterialCode(), JsonHelper.toJson(context));
        } catch (Exception e) {
            log.error("[DeliverGoodsNoticeConsumer] sendBoxMaterial 消费异常，MQ message body:{}" , JsonHelper.toJson(context), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + JsonHelper.toJson(context), e);
        }
    }

    void miniStoreDeliverGoodsConsumerMessage(BoxMaterialRelationMQ context){
        if (StringUtils.isBlank(context.getBoxCode()) || !BusinessUtil.isBoxcode(context.getBoxCode()) || StringUtils.isBlank(context.getSiteCode())){
            log.info("移动微仓发货推送节点消息异常：箱号为空！");
            return;
        }
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setBoxCode(context.getBoxCode());
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (miniStoreBindRelation!=null){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(context.getSiteCode()));
            ProcessTypeEnum processType = JIEHUOCANG.getType().equals(baseStaffSiteOrgDto.getSubType())?SEND_JIEHUOCANG:SEND_SORT_CENTER;
            log.info("MiniStoreSyncProcessDataTask start，current processType is {} ",processType.getMsg());
            if (SEND_JIEHUOCANG==processType
                    && MiniStoreProcessStatusEnum.SEAL_BOX.getCode().equals(String.valueOf(miniStoreBindRelation.getState()))){
                log.info("接货仓发货同步节点数据...");
                MiniStoreBindRelation m =new MiniStoreBindRelation();
                m.setId(miniStoreBindRelation.getId());
                m.setUpdateUser(context.getOperatorName());
                m.setUpdateUserCode(Long.valueOf(context.getOperatorCode()));
                m.setState(Byte.valueOf(MiniStoreProcessStatusEnum.DELIVER_GOODS.getCode()));
                m.setUpdateTime(new Date());
                miniStoreService.updateById(m);
            }
            if (SEND_SORT_CENTER==processType
                    && MiniStoreProcessStatusEnum.CHECK_GOODS.getCode().equals(String.valueOf(miniStoreBindRelation.getState()))){
                log.info("分拣中心发货同步节点数据...");
                MiniStoreBindRelation m =new MiniStoreBindRelation();
                m.setId(miniStoreBindRelation.getId());
                m.setUpdateUser(context.getOperatorName());
                m.setUpdateUserCode(Long.valueOf(context.getOperatorCode()));
                m.setState(Byte.valueOf(MiniStoreProcessStatusEnum.SORTCENTER_DELIVER_GOODS.getCode()));
                m.setUpdateTime(new Date());
                miniStoreService.updateById(m);
            }
            MiniStoreSortingProcessEvent event =new MiniStoreSortingProcessEvent();
            event.setStoreCode(miniStoreBindRelation.getStoreCode());
            event.setProcessType(processType.getType());
            event.setSiteName(baseStaffSiteOrgDto.getSiteName());
            Date time =new Date();
            event.setOperateTime(TimeUtils.date2string(time,TimeUtils.yyyy_MM_dd_HH_mm_ss));
            event.setOperateUser(context.getOperatorName());
            event.setCreateTime(TimeUtils.date2string(time,TimeUtils.yyyy_MM_dd_HH_mm_ss));
            log.info("MiniStoreSyncProcessDataTask send mq消息体："+JsonHelper.toJson(event));
            miniStoreSortProcessProducer.sendOnFailPersistent(context.getBoxCode(), JsonHelper.toJson(event));
        }

    }

}
