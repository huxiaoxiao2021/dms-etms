package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumer.syncPictureInfo.SyncPictureInfoConsumer;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 消费抽检回传消息（fxm）
 *
 * @author: hujiping
 * @date: 2019/10/10 16:54
 */
@Service("spotCheckConsumer")
public class SpotCheckConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(SpotCheckConsumer.class);

    @Autowired
    private TaskService taskService;

    @Override
    public void consume(Message message) throws Exception {

        //主动认责的将此运单号对应的总重量、总体积 写入运单系统
        //1160的称重任务
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.error(MessageFormat.format("抽检回传消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        SyncPictureInfoConsumer.PictureInfoMq pictureInfoMq = JsonHelper.fromJsonUseGson(message.getText(), SyncPictureInfoConsumer.PictureInfoMq.class);

        //主动认责
        Boolean sign = Boolean.FALSE;
        if(sign){
//            OpeEntity opeEntity = new OpeEntity();
//            opeEntity.setOpeType(1);//分拣中心称重、长宽高
//            opeEntity.setWaybillCode();
//            opeEntity.setOpeDetails(new ArrayList<OpeObject>());
//
//            OpeObject obj = new OpeObject();
//            obj.setOpeSiteId();
//            obj.setOpeSiteName();
//            obj.setpWidth();
//            obj.setpLength();
//            obj.setpHigh();
//            obj.setPackageCode();
//            obj.setOpeUserId();
//            obj.setOpeUserName();
//            obj.setOpeTime();
//            obj.setpWeight();
//            opeEntity.getOpeDetails().add(obj);
//            String body = "[" + JsonHelper.toJson(opeEntity) + "]";
//            Task task = new Task();
//            task.setBody(body);
//            task.setType(Task.TASK_TYPE_WEIGHT);
//            task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
//            task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
//            task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
//            task.setKeyword2("上传长宽高、重量");
//            task.setBody(body);
//            task.setSequenceName(Task.getSequenceName(task.getTableName()));
//            task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
//            task.setOwnSign(BusinessHelper.getOwnSign());
//            taskService.add(task);
        }
    }


}
