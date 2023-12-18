package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.bridge.AviationPickingGoodTaskInit;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service
public class TmsAviationPickingGoodConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(TmsAviationPickingGoodConsumer.class);


    @Autowired
    private AviationPickingGoodTaskInit aviationPickingGoodTask;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsAviationPickingGoodConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("TmsAviationPickingGoodConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("TmsAviationPickingGoodConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsAviationPickingGoodMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsAviationPickingGoodMqBody.class);
        if(Objects.isNull(mqBody)){
            log.error("TmsAviationPickingGoodConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            log.error("无效航空提货计划丢弃：{}", JsonHelper.toJson(mqBody));
            return;
        }
        if(log.isInfoEnabled()){
            log.info("航空提货计划生成航空发货任务开始，内容{}",message.getText());
        }
        mqBody.setBusinessId(message.getBusinessId());

        try{
            PickingGoodTaskInitDto initDto = new PickingGoodTaskInitDto();
            //todo zcf
            aviationPickingGoodTask.initTaskTemplate(initDto);
        }catch (Exception ex) {
            log.error("航空提货计划生成提货任务消费异常,businessId={},mqBody={}", message.getBusinessId(), message.getText());
            throw new JyBizException(String.format("航空提货计划生成提货任务消费异常,businessId：%s", message.getBusinessId()));
        }

    }




    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsAviationPickingGoodMqBody mqBody) {

        return true;
    }



    public class TmsAviationPickingGoodMqBody implements Serializable {
        private static final long serialVersionUID = -8024388953489536391L;

        private String businessId;


        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }
    }




}
