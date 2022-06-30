package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
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
 * 司机到达（调度任务）
 * transWorkItemCode 维度
 *
 * {"transWorkCode":"TW22033029178590","transWorkItemCode":"TW22033029178590-001","comeTime":null,"sendTime":null,"arriveTime":"2022-03-30 15:08:20","businessType":14,"transType":11,"transWay":2,"vehicleNumber":"甘AX6A83","operateType":2,"transNeedList":[{"transNeedCode":"TN22033048104099","businessCode":"MJ22011903010512-001202203301101531","businessType":14,"dataSource":39,"businessCodeType":14,"reserveBillCode":null,"requirePickupTime":null,"purchaseBillCode":null}
 */
@Service("tmsTransWorkCarArriveConsumer")
public class TmsTransWorkCarArriveConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsTransWorkCarArriveConsumer.class);

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JdiTransWorkWSManager jdiTransWorkWSManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.tmsTransWorkCarArriveConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsTransWorkCarArriveConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsTransWorkCarArriveConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsTransWorkCarArriveMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsTransWorkCarArriveMqBody.class);
        if(mqBody == null){
            logger.error("TmsTransWorkCarArriveConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isEmpty(mqBody.getTransWorkItemCode()) || StringUtils.isEmpty(mqBody.getArriveTime())){
            logger.error("TmsTransWorkCarArriveConsumer consume -->关键数据为空，内容为【{}】", message.getText());
            return;
        }
        //获取派车明细编码对应的封车任务 并 更新状态为待解
        JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.findByTransWorkItemCode(mqBody.getTransWorkItemCode());
        if(taskUnloadVehicleEntity != null && taskUnloadVehicleEntity.getId() > 0){
            //存在更新状态
            if(logger.isInfoEnabled()) {
                logger.info("消费处理TmsTransWorkCarArriveConsumer 执行到达状态 存在 逻辑，内容{}", JsonHelper.toJson(mqBody));
            }
            if(!jyBizTaskUnloadVehicleService.changeStatus(convert(taskUnloadVehicleEntity,mqBody))){
                //失败重试
                throw new JyBizException("司机到车（调度任务）更新状态失败重试");
            }
        }else {
            //不存在因无封车编码  无法继续创建任务 部分数据需要进入重试队列等待重试
            if(logger.isInfoEnabled()) {
                logger.info("消费处理TmsTransWorkCarArriveConsumer 执行到达状态 不存在 逻辑，内容{}", JsonHelper.toJson(mqBody));
            }
            //获取派车任务对应数据的目的地类型，如果是分拣的则进入重试，反之直接丢弃
            BigQueryOption queryOption = new BigQueryOption();
            queryOption.setQueryTransWorkItemDto(Boolean.TRUE);
            BigTransWorkItemDto bigTransWorkItemDto = jdiTransWorkWSManager.queryTransWorkItemByOptionWithRead(mqBody.getTransWorkItemCode(),queryOption);
            TransWorkItemDto transWorkItemDto = bigTransWorkItemDto == null ? null : bigTransWorkItemDto.getTransWorkItemDto();
            if(transWorkItemDto != null ){
                String endNodeCode = transWorkItemDto.getEndNodeCode();
                if(!StringUtils.isEmpty(endNodeCode)){
                    //检查目的地是否是拣运中心
                    BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteByDmsCode(endNodeCode);
                    if(siteInfo != null && BusinessUtil.isSorting(siteInfo.getSiteType())){
                        throw new JyBizException("司机到车（调度任务）无任务，等待封车任务重试"+mqBody.getTransWorkItemCode());
                    }
                }
            }
            if(logger.isInfoEnabled()) {
                logger.info("TmsTransWorkCarArriveConsumer 不需要关心的数据丢弃,消息:{}", JsonHelper.toJson(mqBody));
            }
            return;

        }
    }

    /**
     * 类型转换
     * @param mqBody
     * @return
     */
    private JyBizTaskUnloadVehicleEntity convert(JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity,TmsTransWorkCarArriveMqBody mqBody){
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setBizId(taskUnloadVehicleEntity.getBizId());
        entity.setSealCarCode(taskUnloadVehicleEntity.getSealCarCode());
        entity.setUpdateTime(DateHelper.parseAllFormatDateTime(mqBody.getArriveTime()));
        entity.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode());
        return entity;
    }

    private class  TmsTransWorkCarArriveMqBody implements Serializable {

        static final long serialVersionUID = 1L;

        /**
         * 派车明细编码
         */
        private String transWorkItemCode;

        /**
         * 到达时间
         */
        private String arriveTime;

        public String getTransWorkItemCode() {
            return transWorkItemCode;
        }

        public void setTransWorkItemCode(String transWorkItemCode) {
            this.transWorkItemCode = transWorkItemCode;
        }

        public String getArriveTime() {
            return arriveTime;
        }

        public void setArriveTime(String arriveTime) {
            this.arriveTime = arriveTime;
        }
    }
}
