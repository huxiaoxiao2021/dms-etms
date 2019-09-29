package com.jd.bluedragon.distribution.consumer.cyclebox;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 循环集包袋处理消费
 */
@Service("deliverGoodsNoticeConsumer")
public class DeliverGoodsNoticeConsumer extends MessageBaseConsumer {
    private final Log logger = LogFactory.getLog(DeliverGoodsNoticeConsumer.class);

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
            logger.warn(MessageFormat.format("[DeliverGoodsNoticeConsumer消费]MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        BoxMaterialRelationMQ context = JsonHelper.fromJsonUseGson(message.getText(), BoxMaterialRelationMQ.class);
        if (StringUtils.isNotBlank(context.getBoxCode()) || BusinessUtil.isBoxcode(context.getBoxCode()) || StringUtils.isNotBlank(context.getSiteCode())){
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
            List<Sorting> list = sortingService.findOrderDetail(sorting);
            if(list!=null && !list.isEmpty()){
                Set<String> waybillCodeSet = new HashSet<>();
                for(Sorting sort :list){
                    waybillCodeSet.add(sort.getWaybillCode());
                    packageCodeList.add(sort.getPackageCode());
                }
                waybillCodeList=new ArrayList<>(waybillCodeSet);
            }else{
                logger.error("[DeliverGoodsNoticeConsumer]消费异常,箱中无任何单据" + message.getText());
                return;
            }
            context.setWaybillCode(waybillCodeList);
            context.setPackageCode(packageCodeList);

            cycleMaterialSendMQ.send(context.getBoxCode(),JsonHelper.toJson(context));
        }catch (Exception e) {
            logger.error("[DeliverGoodsNoticeConsumer]消费异常" + "，MQ message body:" + message.getText(), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + message.getText(), e);
        }
    }
}
