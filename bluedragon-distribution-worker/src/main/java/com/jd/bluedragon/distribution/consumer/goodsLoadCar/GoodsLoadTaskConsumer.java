package com.jd.bluedragon.distribution.consumer.goodsLoadCar;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadDto;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanException;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("goodsLoadTaskConsume")
public class GoodsLoadTaskConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(GoodsLoadTaskConsumer.class);

    @Autowired
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Autowired
    @Qualifier(value = "goodsLoadPackageProducer")
    private DefaultJMQProducer goodsLoadPackageProducer;

    @Override
    public void consume(Message message) throws Exception {
        log.info("20201029--装车发货消费任务MQ--begin--，消息体【{}】", JsonHelper.toJson(message));
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.log.error("PackingConsumableConsumer consume -->消息为空");
            return;
        }

        GoodsLoadingReq context = JsonHelper.fromJson(message.getText(), GoodsLoadingReq.class);

        if(context == null) {
            log.warn("auto下发消息体转换失败，内容为【{}】", message.getText());
            return;
        }

        findTaskList(context);
    }

    private void findTaskList(GoodsLoadingReq context){
        Long taskId = context.getTaskId();

        GoodsLoadDto param = new GoodsLoadDto();
        param.setReceiveSiteCode(context.getReceiveSiteCode());
        param.setCurrentOperate(context.getCurrentOperate());
        param.setSendCode(context.getSendCode());
        param.setUser(context.getUser());

        int start = 0;
        int end = GoodsLoadScanConstants.PAGE_SIZE;
        int cycleCount = 0;
        boolean flag = true;
        //分批循环查询任务下包裹信息，发送MQ进行发货
        while(flag) {
            //控制循环次数
            if(cycleCount < GoodsLoadScanConstants.GOODS_LOAD_CYCLE_COUNT) {//控制循环次数，避免while死循环
                cycleCount = cycleCount + 1;
            }else {
                flag = false;
                break;
            }

            List<GoodsLoadScanRecord> list = findGoodsLoadRecordPage(taskId, start, end);
            if(list == null || list.size() <= 0) {
                flag = false;
            }
            if(list.size() < GoodsLoadScanConstants.PAGE_SIZE) {
                flag = false;
            }

            List<Message> msgList = new ArrayList<>();
            for(GoodsLoadScanRecord glc : list) {
                String key = glc.getTaskId() + glc.getPackageCode();
                param.setPackageCode(glc.getPackageCode());

                Message message = new Message();
                message.setTopic(goodsLoadPackageProducer.getTopic());
                message.setText(JsonHelper.toJson(param));
                message.setBusinessId(key);

                msgList.add(message);

            }
            //发MQ处理包裹下发
            try{
//                    goodsLoadPackageProducer.sendOnFailPersistent(key , JsonHelper.toJson(param));
                log.info("20201029--装车发货批量生产包裹MQ--start--");
                goodsLoadPackageProducer.batchSendOnFailPersistent(msgList);
                log.info("20201029--装车发货批量生产包裹MQ--end--");

            }catch (Exception e ) {
                log.error("包裹发货操作失败，包裹信息【{}】，错误【{}】", param, e);
                throw  new GoodsLoadScanException("包裹【" + param.getPackageCode() + "】装车发货完成失败");
            }

            start = start + GoodsLoadScanConstants.PAGE_SIZE;
        }
    }

    public List<GoodsLoadScanRecord> findGoodsLoadRecordPage(Long taskId, int start, int end) {
        return goodsLoadScanRecordDao.findGoodsLoadRecordPage(taskId, start, end);
    }

}
