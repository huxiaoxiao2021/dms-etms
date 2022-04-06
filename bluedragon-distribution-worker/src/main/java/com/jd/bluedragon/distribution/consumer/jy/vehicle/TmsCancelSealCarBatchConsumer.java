package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/4
 * @Description:
 * 取消封车批次信息
 * {"sealCarCode":"SC22032919264919","batchCode":"R1508741223106682880","waybillCode":null,"operateUserCode":"xulifan6","operateTime":1648551719281}
 */
@Service("tmsCancelSealCarBatchConsumer")
public class TmsCancelSealCarBatchConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsCancelSealCarBatchConsumer.class);

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsCancelSealCarBatchConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsCancelSealCarBatchConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsCancelSealCarBatchConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsCancelSealCarBatchMQBody mqBody = JsonHelper.fromJson(message.getText(), TmsCancelSealCarBatchMQBody.class);
        if(mqBody == null){
            logger.error("TmsCancelSealCarBatchConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isEmpty(mqBody.getBatchCode())){
            logger.error("TmsCancelSealCarBatchConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }

        //取消批次需要更新批次信息表 如果全部批次都被取消则取消 解封车任务

    }

    /**
     * 消息实体
     */
    private class TmsCancelSealCarBatchMQBody implements Serializable {

        static final long serialVersionUID = 1L;
        /**
         * 封车编码
         */
        private String sealCarCode;
        /**
         * 批次号
         */
        private String batchCode;
        /**
         * 操作人ERP
         */
        private String operateUserCode;
        /**
         * 操作人名字
         */
        private String operateUserName;
        /**
         * 操作时间
         */
        private String operateTime;

        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public String getBatchCode() {
            return batchCode;
        }

        public void setBatchCode(String batchCode) {
            this.batchCode = batchCode;
        }

        public String getOperateUserCode() {
            return operateUserCode;
        }

        public void setOperateUserCode(String operateUserCode) {
            this.operateUserCode = operateUserCode;
        }

        public String getOperateUserName() {
            return operateUserName;
        }

        public void setOperateUserName(String operateUserName) {
            this.operateUserName = operateUserName;
        }

        public String getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(String operateTime) {
            this.operateTime = operateTime;
        }
    }
}
