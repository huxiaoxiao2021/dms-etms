package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unseal.IJyUnSealVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnSealDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/4
 * @Description:
 * 消费 tms_seal_car_status
 * sealCarCode 维度
 * status  10 封车，20解封车，30出围栏，40进围栏
 * {"sealCarCode":"SC22033019316055","status":20,"operateUserCode":"kongtuantuan","operateUserName":"孔团团","operateTime":"2022-03-30 15:37:56","sealCarType":30,"batchCodes":null,"transBookCode":"TB22033029658674","volume":null,"weight":null,"transWay":null,"vehicleNumber":"京AAG6153","operateSiteId":271733,"operateSiteCode":"010Y316","operateSiteName":"北京槐柏树营业部","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null,"source":1,"sealCarInArea":null}
{"sealCarCode":"SC22040419643118","status":10,"operateUserCode":"chenyifei6","operateUserName":"陈毅飞","operateTime":"2022-04-04 19:10:54","sealCarType":30,"batchCodes":["R202204041106221342","R1510931730171867136","R202204041145461342"],"transBookCode":"TB22040430009175","volume":null,"weight":null,"transWay":2,"vehicleNumber":"京AAJ7385","operateSiteId":1342,"operateSiteCode":"010Y059","operateSiteName":"北京上庄营业部","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null,"source":2,"sealCarInArea":null}
 */
@Service("tmsSealCarStatusConsumer")
public class TmsSealCarStatusConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsSealCarStatusConsumer.class);

    /**
     * 封车状态
     */
    private static final Integer TMS_STATUS_SEAL = 10;
    /**
     * 解封车状态
     */
    private static final Integer TMS_STATUS_UN_SEAL = 20;
    /**
     * 进围栏状态
     */
    private static final Integer TMS_STATUS_IN_RAIL = 40;

    @Autowired
    private IJyUnSealVehicleService jyUnSealVehicleService;

    @Autowired
    private IJyUnloadVehicleService jyUnloadVehicleService;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private VosManager vosManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    @Qualifier(value = "jyCollectDataInitSplitProducer")
    private DefaultJMQProducer jyCollectDataInitSplitProducer;

    @Autowired
    @Qualifier(value = "sealSyncOpenCloseSendTaskProducer")
    private DefaultJMQProducer sealSyncOpenCloseSendTaskProducer;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsSealCarStatusConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("tmsSealCarStatusConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("tmsSealCarStatusConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsSealCarStatusMQBody mqBody = JsonHelper.fromJson(message.getText(),TmsSealCarStatusMQBody.class);
        if(mqBody == null){
            logger.error("tmsSealCarStatusConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("消费处理tms_seal_car_status开始，内容{}",message.getText());
        }
        if(!deal(mqBody)){
            //处理失败 重试
            logger.error("消费处理tms_seal_car_status失败，内容{}",message.getText());
            throw new JyBizException("消费处理tms_seal_car_status失败");
        }else{
            if(logger.isInfoEnabled()) {
                logger.info("消费处理tms_seal_car_status成功，内容{}", message.getText());
            }
        }

    }

    /**
     * 处理逻辑
     * @param tmsSealCarStatus
     * @return
     */
    private boolean deal(TmsSealCarStatusMQBody tmsSealCarStatus){
        //只处理 到拣运中心的数据, 封车,解封车进围栏 状态数据
        List<Integer> needDealStatus = Arrays.asList(TMS_STATUS_SEAL,TMS_STATUS_UN_SEAL,TMS_STATUS_IN_RAIL);
        Integer status = tmsSealCarStatus.getStatus();
        if(!needDealStatus.contains(status)){
            return true;
        }
        SealCarDto sealCarInfoBySealCarCodeOfTms = findSealCarInfoBySealCarCodeOfTms(tmsSealCarStatus.getSealCarCode());
        Integer startSiteId = sealCarInfoBySealCarCodeOfTms.getStartSiteId();
        Integer endSiteId = sealCarInfoBySealCarCodeOfTms.getEndSiteId();
        if(sealCarInfoBySealCarCodeOfTms == null){
            logger.error("从运输未获取到封车信息为空,{}",JsonHelper.toJson(tmsSealCarStatus));
            return false;
        }
        if(startSiteId == null || endSiteId == null){
            logger.error("从运输未获取到封车关键信息为空,{}",JsonHelper.toJson(tmsSealCarStatus));
            return true;
        }
        // 完结板操作
        closeBoard(sealCarInfoBySealCarCodeOfTms);

        //检查目的地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(endSiteId);
        if(siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())){
            //丢弃数据
            logger.info("不需要关心的数据丢弃,目的站点:{},目的站点类型:{}，消息:{}",endSiteId,siteInfo==null?null:siteInfo.getSiteType(),
                    JsonHelper.toJson(tmsSealCarStatus));
            return true;
        }
        if(TMS_STATUS_SEAL.equals(tmsSealCarStatus.getStatus())){
            //上游封车后创建任务 状态为在途
            if(logger.isInfoEnabled()) {
                logger.info("消费处理tms_seal_car_status 执行封车状态逻辑，内容{}", JsonHelper.toJson(tmsSealCarStatus));
            }
            //发送集齐消息初始化MQ  todo  只做转运场地初始化
            if (BusinessUtil.isTransferSite(siteInfo.getSubType())) {
                sendInitCollectMq(tmsSealCarStatus);
            }
            //
            this.sendSealSyncJySendTaskStatusMq(tmsSealCarStatus);
            return jyUnSealVehicleService.createUnSealTask(convert4Seal(tmsSealCarStatus,sealCarInfoBySealCarCodeOfTms));
        }if(TMS_STATUS_UN_SEAL.equals(tmsSealCarStatus.getStatus())){
            //下游操作解封车后  关闭待解任务 同时创建 待卸任务
            if(logger.isInfoEnabled()) {
                logger.info("消费处理tms_seal_car_status 执行解封车状态逻辑，内容{}", JsonHelper.toJson(tmsSealCarStatus));
            }

            return jyUnloadVehicleService.createUnloadTask(convert4Load(tmsSealCarStatus,sealCarInfoBySealCarCodeOfTms)) != null;
        }else if(TMS_STATUS_IN_RAIL.equals(tmsSealCarStatus.getStatus())){
            //触碰围栏后更新任务状态
            Long id = jyBizTaskUnloadVehicleService.findIdByBizId(tmsSealCarStatus.getSealCarCode());
            if(id != null && id > 0){
                //存在更新状态
                if(logger.isInfoEnabled()) {
                    logger.info("消费处理tms_seal_car_status 执行进围栏状态 存在 逻辑，内容{}", JsonHelper.toJson(tmsSealCarStatus));
                }
                //初始化实际到达时间
                jyBizTaskUnloadVehicleService.initActualArriveTime(tmsSealCarStatus.getSealCarCode(),DateHelper.parseAllFormatDateTime(tmsSealCarStatus.getOperateTime()));

                return jyBizTaskUnloadVehicleService.changeStatus(convert2Entity4InRail(tmsSealCarStatus));
            }else {
                //不存在继续创建任务 状态为待解
                if(logger.isInfoEnabled()) {
                    logger.info("消费处理tms_seal_car_status 执行进围栏状态 不存在 逻辑，内容{}", JsonHelper.toJson(tmsSealCarStatus));
                }
                boolean flag = jyUnSealVehicleService.createUnSealTask(convert4InRail(tmsSealCarStatus,sealCarInfoBySealCarCodeOfTms));

                //初始化实际到达时间
                jyBizTaskUnloadVehicleService.initActualArriveTime(tmsSealCarStatus.getSealCarCode(),DateHelper.parseAllFormatDateTime(tmsSealCarStatus.getOperateTime()));

                return flag;
            }

        }
        return false;
    }

    private void closeBoard(SealCarDto sealCarInfoBySealCarCodeOfTms) {
        if (!CollectionUtils.isEmpty(sealCarInfoBySealCarCodeOfTms.getBatchCodes())) {
            try {
                JyBizTaskComboardEntity query = new JyBizTaskComboardEntity();
                query.setSendCodeList(sealCarInfoBySealCarCodeOfTms.getBatchCodes());
                List<JyBizTaskComboardEntity> boardEntityList = jyBizTaskComboardService.listBoardTaskBySendCode(query);
                if (CollectionUtils.isEmpty(boardEntityList)) {
                    return;
                }
                List<String> boardList = new ArrayList<>();
                List<String> sealSendCodeList = new ArrayList<>();
                for (JyBizTaskComboardEntity entity : boardEntityList) {
                    if (!entity.getBoardStatus().equals(ComboardStatusEnum.SEALED.getCode())) {
                        sealSendCodeList.add(entity.getSendCode());
                    }
                    boardList.add(entity.getBoardCode());
                }

                if (!CollectionUtils.isEmpty(sealSendCodeList)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("开始更新板状态：{}",JsonHelper.toJson(sealSendCodeList));
                    }
                    if (!jyBizTaskComboardService.updateBoardStatusBySendCodeList(sealSendCodeList, sealCarInfoBySealCarCodeOfTms.getSealUserCode(),
                            sealCarInfoBySealCarCodeOfTms.getSealUserName(), ComboardStatusEnum.SEALED)) {
                        logger.info("批量更新板状态失败：{}",JsonHelper.toJson(sealSendCodeList));
                    }
                }


                if (!groupBoardManager.batchCloseBoard(boardList)) {
                    logger.error("批量完结板失败：{}",JsonHelper.toJson(boardList));
                }
            }catch (Exception e) {
                logger.error("完结板操作失败，批次信息为：{}",JsonHelper.toJson(sealCarInfoBySealCarCodeOfTms.getBatchCodes()),e);
            }
        }
    }
    //封车信息同步新版app发货任务状态
    private void sendSealSyncJySendTaskStatusMq(TmsSealCarStatusMQBody mqBody) {
        for(String batchCode : mqBody.getBatchCodes()) {
            SealSyncOpenCloseSendTaskDto msg = new SealSyncOpenCloseSendTaskDto();
            msg.setStatus(SealSyncOpenCloseSendTaskDto.STATUS_SEAL);
            msg.setOperateUserCode(mqBody.getOperateUserCode());
            msg.setSealCarCode(mqBody.getSealCarCode());
            Date operateTime = DateHelper.parseAllFormatDateTime(mqBody.getOperateTime());
            msg.setOperateTime(operateTime.getTime());
            msg.setOperateUserName(mqBody.getOperateUserName());
            msg.setBatchCodes(mqBody.getBatchCodes());
            msg.setSingleBatchCode(batchCode);
            msg.setSysTime(System.currentTimeMillis());

            try{
                sealSyncOpenCloseSendTaskProducer.sendOnFailPersistent(mqBody.getSealCarCode(),JsonHelper.toJson(msg));
            }catch (Exception e) {
                logger.error("发送封车同步关闭发货任务mq异常[errMsg={}]，mqBody={}", e.getMessage(), JsonHelper.toJson(msg), e);
            }finally {
                if(logger.isInfoEnabled()) {
                    logger.info("封车同步关闭新版发货任务状态，msg={}", JsonHelper.toJson(msg));
                }
            }
        }
    }

    private void sendInitCollectMq(TmsSealCarStatusMQBody tmsSealCarStatus) {
        InitCollectDto initCollectDto = new InitCollectDto();
        initCollectDto.setBizId(tmsSealCarStatus.getSealCarCode());
        initCollectDto.setOperateTime(System.currentTimeMillis());
        initCollectDto.setOperateNode(CollectInitNodeEnum.SEAL_INIT.getCode());
        String msgText = JsonHelper.toJson(initCollectDto);
        if(logger.isInfoEnabled()) {
            logger.info("TmsSealCarStatusConsumer.sendInitCollectMq-封车节点触发集齐初始化，businessId={}， msg={}", tmsSealCarStatus.getSealCarCode(), msgText);
        }
        jyCollectDataInitSplitProducer.sendOnFailPersistent(tmsSealCarStatus.getSealCarCode(),msgText);


    }

    /**
     * 类型转换 封车状态
     * @param mqBody
     * @return
     */
    private JyBizTaskUnSealDto convert4Seal(TmsSealCarStatusMQBody mqBody,SealCarDto sealCarDto){
        JyBizTaskUnSealDto dto = new JyBizTaskUnSealDto();
        dto.setBizId(mqBody.getSealCarCode());
        dto.setSealCarCode(mqBody.getSealCarCode());
        dto.setOperateUserErp(mqBody.getOperateUserCode());
        dto.setOperateTime(DateHelper.parseAllFormatDateTime(mqBody.getOperateTime()));
        dto.setOperateUserName(mqBody.getOperateUserName());
        dto.setVehicleStatus(JyBizTaskUnloadStatusEnum.ON_WAY.getCode());

        dto.setVehicleNumber(sealCarDto.getVehicleNumber());
        dto.setTransWorkItemCode(sealCarDto.getTransWorkItemCode());
        dto.setStartSiteId(Long.valueOf(sealCarDto.getStartSiteId()));
        dto.setStartSiteName(sealCarDto.getStartSiteName());
        dto.setEndSiteId(Long.valueOf(sealCarDto.getEndSiteId()));
        dto.setEndSiteName(sealCarDto.getEndSiteName());

        return dto;
    }

    /**
     * 类型转换 待解状态
     * @param mqBody
     * @return
     */
    private JyBizTaskUnloadDto convert4Load(TmsSealCarStatusMQBody mqBody, SealCarDto sealCarDto){
        JyBizTaskUnloadDto dto = new JyBizTaskUnloadDto();
        dto.setBizId(mqBody.getSealCarCode());
        dto.setSealCarCode(mqBody.getSealCarCode());
        dto.setOperateUserErp(mqBody.getOperateUserCode());
        dto.setOperateTime(DateHelper.parseAllFormatDateTime(mqBody.getOperateTime()));
        dto.setOperateUserName(mqBody.getOperateUserName());
        dto.setVehicleNumber(sealCarDto.getVehicleNumber());
        return dto;
    }

    /**
     * 类型转换 进围栏状态
     * @param mqBody
     * @return
     */
    private JyBizTaskUnSealDto convert4InRail(TmsSealCarStatusMQBody mqBody,SealCarDto sealCarDto){
        JyBizTaskUnSealDto dto = new JyBizTaskUnSealDto();
        dto.setBizId(mqBody.getSealCarCode());
        dto.setSealCarCode(mqBody.getSealCarCode());
        dto.setOperateUserErp(mqBody.getOperateUserCode());
        dto.setOperateTime(DateHelper.parseAllFormatDateTime(mqBody.getOperateTime()));
        dto.setOperateUserName(mqBody.getOperateUserName());
        dto.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode());

        dto.setVehicleNumber(sealCarDto.getVehicleNumber());
        dto.setTransWorkItemCode(sealCarDto.getTransWorkItemCode());
        dto.setStartSiteId(Long.valueOf(sealCarDto.getStartSiteId()));
        dto.setStartSiteName(sealCarDto.getStartSiteName());
        dto.setEndSiteId(Long.valueOf(sealCarDto.getEndSiteId()));
        dto.setEndSiteName(sealCarDto.getEndSiteName());
        return dto;
    }

    /**
     * 类型转换 进围栏状态
     * @param mqBody
     * @return
     */
    private JyBizTaskUnloadVehicleEntity convert2Entity4InRail(TmsSealCarStatusMQBody mqBody){
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setBizId(mqBody.getSealCarCode());
        entity.setSealCarCode(mqBody.getSealCarCode());
        entity.setUpdateUserErp(mqBody.getOperateUserCode());
        entity.setUpdateTime(DateHelper.parseAllFormatDateTime(mqBody.getOperateTime()));
        entity.setUpdateUserName(mqBody.getOperateUserName());
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

    /**
     * 消息实体
     */
    private class TmsSealCarStatusMQBody implements Serializable {

        static final long serialVersionUID = 1L;
        /**
         * 封车编码
         */
        private String sealCarCode;
        /**
         * 10 封车，20解封车，30进围栏，40出围栏
         */
        private Integer status;
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
        /**
         * 批次 JSON格式
         */
        private List<String> batchCodes;
        /**
         * 运输封车批次集合拆分，下游按单批次处理
         * batchCodes 中的某一个
         */
        private String singleBatchCode;

        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
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

        public List<String> getBatchCodes() {
            return batchCodes;
        }

        public void setBatchCodes(List<String> batchCodes) {
            this.batchCodes = batchCodes;
        }

        public String getSingleBatchCode() {
            return singleBatchCode;
        }

        public void setSingleBatchCode(String singleBatchCode) {
            this.singleBatchCode = singleBatchCode;
        }
    }
}
