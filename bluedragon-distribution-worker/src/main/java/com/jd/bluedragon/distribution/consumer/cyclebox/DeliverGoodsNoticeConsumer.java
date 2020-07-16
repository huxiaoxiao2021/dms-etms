package com.jd.bluedragon.distribution.consumer.cyclebox;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.quality.service.BasicMajorWSAdapter;
import org.apache.commons.lang.StringUtils;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public void consume(Message message) {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[DeliverGoodsNoticeConsumer消费]MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BoxMaterialRelationMQ context = JsonHelper.fromJsonUseGson(message.getText(), BoxMaterialRelationMQ.class);
        if (StringUtils.isBlank(context.getBoxCode()) || !BusinessUtil.isBoxcode(context.getBoxCode()) || StringUtils.isBlank(context.getSiteCode())){
            return;
        }
        try {
            String materialCode=cycleBoxService.getBoxMaterialRelation(context.getBoxCode());
            if (StringUtils.isBlank(materialCode)){
                return;
            }
            context.setMaterialCode(materialCode);

            List<String>waybillCodeList = new ArrayList<>();
            List<String>packageCodeList = new ArrayList<>();

            Sorting sorting = new Sorting();
            sorting.setBoxCode(context.getBoxCode());
            sorting.setCreateSiteCode(Integer.parseInt(context.getSiteCode()));
            List<Sorting> list = sortingService.findByBoxCode(sorting);
            if(list!=null && !list.isEmpty()){
                Set<String> waybillCodeSet = new HashSet<>();
                for(Sorting sort :list){
                    waybillCodeSet.add(sort.getWaybillCode());
                    packageCodeList.add(sort.getPackageCode());
                }
                waybillCodeList=new ArrayList<>(waybillCodeSet);
            }else{
                log.warn("[DeliverGoodsNoticeConsumer]消费异常,箱中无任何单据：{}" , message.getText());
                return;
            }
            context.setWaybillCode(waybillCodeList);
            context.setPackageCode(packageCodeList);
            context.setOperatorTime(new Date());

            cycleMaterialSendMQ.send(context.getBoxCode(),JsonHelper.toJson(context));
        }catch (Exception e) {
            log.error("[DeliverGoodsNoticeConsumer]消费异常，MQ message body:{}" , message.getText(), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + message.getText(), e);
        }
    }
}
