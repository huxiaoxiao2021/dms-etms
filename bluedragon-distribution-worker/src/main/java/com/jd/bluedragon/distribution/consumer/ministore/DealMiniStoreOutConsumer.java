package com.jd.bluedragon.distribution.consumer.ministore;

import com.jd.bluedragon.common.dto.ministore.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumer.spotCheck.SpotCheckNotifyConsumer;
import com.jd.bluedragon.distribution.material.dto.BoxInOutMessage;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckNotifyMQ;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("dealMiniStoreOutConsumer")
public class DealMiniStoreOutConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DealMiniStoreOutConsumer.class);

    @Autowired
    MiniStoreService miniStoreService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.error("保温箱逆向出库非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BoxInOutMessage boxInOutMessage = JsonHelper.fromJson(message.getText(), BoxInOutMessage.class);
        if (boxInOutMessage==null){
            logger.error("保温箱逆向出库消息体为空！");
            return;
        }
        String storeCode =boxInOutMessage.getBoxNo();
        logger.info("保温箱逆向出库，箱号码 {}",storeCode);
        if (BusinessUtil.isStoreCode(storeCode)){
            DeviceDto deviceDto =new DeviceDto();
            deviceDto.setStoreCode(storeCode);
            MiniStoreBindRelation miniStoreBindRelation =miniStoreService.selectBindRelation(deviceDto);
            if (miniStoreBindRelation==null){
                logger.error("未查询到匹配的微仓绑定记录");
                return;
            }
            MiniStoreBindRelation m =new MiniStoreBindRelation();
            m.setId(miniStoreBindRelation.getId());
            m.setState(Byte.valueOf(MiniStoreProcessStatusEnum.BACK_TO_STORE.getCode()));
            m.setOccupiedFlag(false);
            m.setUpdateTime(new Date());
            miniStoreService.updateById(m);
            logger.info("释放微仓设备成功！");
        }
    }
}
