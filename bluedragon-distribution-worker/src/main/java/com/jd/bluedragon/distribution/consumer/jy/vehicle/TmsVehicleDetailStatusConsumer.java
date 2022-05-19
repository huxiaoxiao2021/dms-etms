package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/4
 * @Description:
 * 司机到达（自建任务/传摆任务）
 * transWorkItemCode 维度   status 20 到车
 *
 * {"status":10,"vehicleDetailCode":"CBD22032933731038","operateTime":1648551913608,"operatorUserId":21926679,"operatorCode":"wangzheng27","operatorName":"王政","operatorPhone":"15361435295","sealCarCode":"SC22032919265363","volume":null,"sendCarInArea":1,"arriveCarInArea":null,"specialTrans":null,"sameDevice":null}
 */
@Service("tmsVehicleDetailStatusConsumer")
public class TmsVehicleDetailStatusConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsVehicleDetailStatusConsumer.class);

    private static final Integer ARRIVE_CAR_STATUS = 20;

    @Autowired
    private VosManager vosManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.tmsVehicleDetailStatusConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("tmsVehicleDetailStatusConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("tmsVehicleDetailStatusConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsVehicleDetailStatusMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsVehicleDetailStatusMqBody.class);
        if(mqBody == null){
            logger.error("tmsVehicleDetailStatusConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isEmpty(mqBody.getSealCarCode()) || StringUtils.isEmpty(mqBody.getOperateTime())
                || mqBody.getStatus() == null){
            logger.error("tmsVehicleDetailStatusConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }
        //获取派车明细编码对应的封车任务 并 更新状态为待解
        if(!ARRIVE_CAR_STATUS.equals(mqBody.getStatus())){
            // 只处理到车数据
            logger.info("tmsVehicleDetailStatusConsumer consume -->丢弃，只处理司机到达，内容为【{}】", message.getText());
            return;
        }
        SealCarDto sealCarInfoBySealCarCodeOfTms = findSealCarInfoBySealCarCodeOfTms(mqBody.getSealCarCode());
        if(sealCarInfoBySealCarCodeOfTms == null){
            logger.error("tmsVehicleDetailStatusConsumer 从运输未获取到封车信息,{}",JsonHelper.toJson(mqBody));
            return;
        }
        Integer endSiteId = sealCarInfoBySealCarCodeOfTms.getEndSiteId();
        //检查目的地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(endSiteId);
        if(siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())){
            //丢弃数据
            logger.info("tmsVehicleDetailStatusConsumer 不需要关心的数据丢弃,目的站点:{},目的站点类型:{}，消息:{}",endSiteId,siteInfo==null?null:siteInfo.getSiteType(),
                    JsonHelper.toJson(mqBody));
            return;
        }

        String bizId = mqBody.getSealCarCode();
        Long id = jyBizTaskUnloadVehicleService.findIdByBizId(bizId);
        if(id != null && id > 0){
            //存在更新状态
            if(logger.isInfoEnabled()) {
                logger.info("消费处理tmsVehicleDetailStatusConsumer 执行到达状态 存在 逻辑，内容{}", JsonHelper.toJson(mqBody));
            }
            if(!jyBizTaskUnloadVehicleService.changeStatus(convert(mqBody))){
                //失败重试
                throw new JyBizException("司机到车（自建任务/传摆任务）更新状态失败重试");
            }
        }else {
            //不存在因无封车编码  无法继续创建任务 需要进入重试队列等待重试
            if(logger.isInfoEnabled()) {
                logger.info("消费处理tmsVehicleDetailStatusConsumer 执行到达状态 不存在 逻辑，内容{}", JsonHelper.toJson(mqBody));
            }
            throw new JyBizException("司机到车（自建任务/传摆任务）无任务，等待封车任务重试");
        }
    }

    /**
     * 类型转换
     * @param mqBody
     * @return
     */
    private JyBizTaskUnloadVehicleEntity convert(TmsVehicleDetailStatusMqBody mqBody){
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setBizId(mqBody.getSealCarCode());
        entity.setSealCarCode(mqBody.getSealCarCode());
        entity.setUpdateTime(DateHelper.toDate(Long.valueOf(mqBody.getOperateTime())));
        entity.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode());
        return entity;
    }

    /**
     * 通过封车编码获取封车信息
     * @param sealCarCode
     * @return
     */
    private SealCarDto findSealCarInfoBySealCarCodeOfTms(String sealCarCode){
        if(logger.isInfoEnabled()){
            logger.info("TmsSealCarStatusConsumer获取封车信息开始 {}",sealCarCode);
        }
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        if(logger.isInfoEnabled()){
            logger.info("TmsSealCarStatusConsumer获取封车信息返回数据 {},{}",sealCarCode,JsonHelper.toJson(sealCarDtoCommonDto));
        }
        if(sealCarDtoCommonDto == null || Constants.RESULT_SUCCESS != sealCarDtoCommonDto.getCode()){
            return null;
        }
        return sealCarDtoCommonDto.getData();
    }

    private class TmsVehicleDetailStatusMqBody implements Serializable {

        static final long serialVersionUID = 1L;

        /**
         * 封车编码
         */
        private String sealCarCode;

        /**
         * 到达操作时间 时间戳
         */
        private String operateTime;

        /**
         * 状态 20-到车
         */
        private Integer status;

        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public String getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(String operateTime) {
            this.operateTime = operateTime;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
