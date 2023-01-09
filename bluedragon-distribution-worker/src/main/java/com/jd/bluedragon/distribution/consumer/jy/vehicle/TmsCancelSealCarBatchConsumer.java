package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unseal.IJyUnSealVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnSealDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.CommonDto;
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
import java.util.Date;
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

    /**
     * 运输取消封车状态
     */
    private static final Integer TMS_CANCEL_SEAL_CAR = 200;

    @Autowired
    private VosManager vosManager;

    @Autowired
    private IJyUnSealVehicleService jyUnSealVehicleService;
    
    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private JySealVehicleService jySealVehicleService;

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
        //接收取消批次信息 查询运输获取取消状态
        SealCarDto sealCarCodeOfTms = findSealCarInfoBySealCarCodeOfTms(mqBody.getSealCarCode());
        if(TMS_CANCEL_SEAL_CAR.equals(sealCarCodeOfTms.getStatus())){
            //证明整个封车编码被取消了 , 取消对应任务
            try {
				if(!jyUnSealVehicleService.cancelUnSealTask(convert2Dto(mqBody))){
				    //失败重试
				    logger.error("TmsCancelSealCarBatchConsumer consume -->关键数据为空，内容为【{}】", message.getText());
				}
			} catch (Exception e) {
				logger.error("jyUnSealVehicleService.cancelUnSealTask error!内容为【{}】",message.getText(),e);
			}
            try {
				sendVehicleTransactionManager.resetSendStatusToseal(sealCarCodeOfTms,mqBody.getOperateUserCode(),mqBody.getOperateUserName(),mqBody.getOperateTime());
			} catch (Exception e) {
				logger.error("sendVehicleTransactionManager.resetSendStatusToseal error!内容为【{}】",message.getText(),e);
			}
            try {
                logger.info("开始操作取消封车：{} {}",message.getText(),JsonHelper.toJson(sealCarCodeOfTms));
                InvokeResult<Boolean> result = jySealVehicleService.updateBoardStatusAndSealCode(sealCarCodeOfTms, mqBody.getBatchCode(),mqBody.getOperateUserCode(), mqBody.getOperateUserName());
                if (result != null && !result.getData()) {
                    logger.error("jySealVehicleService.cancelSealCar 参数为【{}】 error!内容为【{}】",message.getText(),result.getMessage());
                }
            } catch (Exception e) {
                logger.error("jySealVehicleService.cancelSealCar error!内容为【{}】",message.getText(),e);
            }
        }
    }

    private JyBizTaskUnSealDto convert2Dto(TmsCancelSealCarBatchMQBody mqBody){
        JyBizTaskUnSealDto dto = new JyBizTaskUnSealDto();
        dto.setBizId(mqBody.getSealCarCode());
        dto.setSealCarCode(mqBody.getSealCarCode());
        dto.setOperateUserName(mqBody.getOperateUserName());
        dto.setOperateUserErp(mqBody.getOperateUserCode());
        dto.setOperateTime(new Date(mqBody.getOperateTime()));
        return dto;
    }

    /**
     * 通过封车编码获取封车信息
     * @param sealCarCode
     * @return
     */
    private SealCarDto findSealCarInfoBySealCarCodeOfTms(String sealCarCode){
        if(logger.isInfoEnabled()){
            logger.info("TTmsCancelSealCarBatchConsumer获取封车信息开始 {}",sealCarCode);
        }
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        if(logger.isInfoEnabled()){
            logger.info("TmsCancelSealCarBatchConsumer获取封车信息返回数据 {},{}",sealCarCode,JsonHelper.toJson(sealCarDtoCommonDto));
        }
        if(sealCarDtoCommonDto == null || Constants.RESULT_SUCCESS != sealCarDtoCommonDto.getCode()){
            throw new JyBizException("querySealCarInfoBySealCarCode fail!");
        }
        if(sealCarDtoCommonDto.getData() == null || StringUtils.isEmpty(sealCarDtoCommonDto.getData().getTransWorkItemCode())){
            throw new JyBizException("querySealCarInfoBySealCarCode null!");
        }
        return sealCarDtoCommonDto.getData();
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
        private Long operateTime;

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

        public Long getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(Long operateTime) {
            this.operateTime = operateTime;
        }
    }
}
