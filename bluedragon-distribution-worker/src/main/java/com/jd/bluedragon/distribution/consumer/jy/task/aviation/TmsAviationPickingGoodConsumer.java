package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskExtendInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.template.AviationPickingGoodTaskInit;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 消息文档： https://cf.jd.com/pages/viewpage.action?pageId=1421890482
 * 对接研发：zhangyaqiang6
 * 对接产品：zhujian6
 * 我方产品：zhangshuo37
 *
 * 消费规则：tplBillCode 做流水号
 * （1） 重复tplBillCode时，历史任务清除，重新生成任务
 * （2） 实际降落时间非空后，在收到同tplBillCode的消息，视为无效数据
 * （3） 业务系统处理，实际降落时间之前只允许展示任务，不允许实操， 实际降落时间到达之后，才开始做实操处理
 *
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
            return;
        }
        if(log.isInfoEnabled()){
            log.info("航空提货计划消费开始，mqBody={}",message.getText());
        }
        try{
            PickingGoodTaskInitDto initDto = this.convertPickingGoodTaskInitDto(mqBody);
            aviationPickingGoodTask.initTaskTemplate(initDto);
        }catch (JyBizException ex) {
            log.error("航空提货计划消费失败,businessId={},errMsg={}, mqBody={}", message.getBusinessId(), ex.getMessage(), message.getText());
            throw new JyBizException(String.format("航空提货计划消费失败,businessId：%s", message.getBusinessId()));
        }catch (Exception ex) {
            log.error("航空提货计划消费异常,businessId={},errMsg={}, mqBody={}", message.getBusinessId(), ex.getMessage(), message.getText(), ex);
            throw new JyBizException(String.format("航空提货计划消费异常,businessId：%s", message.getBusinessId()));
        }

    }

    private PickingGoodTaskInitDto convertPickingGoodTaskInitDto(TmsAviationPickingGoodMqBody mqBody) {
        PickingGoodTaskInitDto initDto = new PickingGoodTaskInitDto();
        initDto.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        initDto.setBusinessNumber(mqBody.getTplBillCode());
        initDto.setServiceNumber(mqBody.getFlightNumber());
        initDto.setBeginNodeCode(mqBody.getBeginNodeCode());
        initDto.setBeginNodeName(mqBody.getBeginNodeName());
        initDto.setEndNodeCode(mqBody.getEndNodeCode());
        initDto.setEndNodeName(mqBody.getEndNodeName());
        initDto.setNodePlanStartTime(mqBody.getPlanTakeOffTime());
        initDto.setNodePlanArriveTime(mqBody.getPlanTouchDownTime());
        initDto.setNodeRealStartTime(mqBody.getRealTakeOffTime());
        initDto.setNodeRealArriveTime(mqBody.getRealTouchDownTime());
        initDto.setOperateNodeType(mqBody.getOperateType());
        initDto.setCargoNumber(mqBody.getDepartCargoAmount());
        initDto.setCargoWeight(mqBody.getDepartCargoRealWeight());
        initDto.setCreateUserErp(Constants.SYS_NAME);
        initDto.setCreateUserName(Constants.SYS_NAME);
        initDto.setOperateTime(mqBody.getOperateTime().getTime());
        List<AirTransportBillDto> airTransportBillDtoList = mqBody.getTransbillList();
        List<PickingGoodTaskExtendInitDto> extendInitDtoList = new ArrayList<>();
        airTransportBillDtoList.forEach(o -> {
            PickingGoodTaskExtendInitDto dto = new PickingGoodTaskExtendInitDto();
            dto.setBatchCode(o.getBatchCode());
            dto.setSealCarCode(o.getSealCarCode());
            dto.setStartSiteCode(o.getBeginNodeCode());
            dto.setStartSiteName(o.getBeginNodeName());
            dto.setEndSiteCode(o.getEndNodeCode());
            dto.setEndSiteName(o.getEndNodeName());
            dto.setTransportCode(o.getTransportCode());
            extendInitDtoList.add(dto);
        });
        return initDto;
    }


    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsAviationPickingGoodMqBody mqBody) {
        if(StringUtils.isBlank(mqBody.getTplBillCode())) {
            log.error("tplBillCode为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getEndNodeCode()) || StringUtils.isBlank(mqBody.getEndNodeName()) ) {
            log.error("提货机场编码或名称为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            log.error("operateTime为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(CollectionUtils.isEmpty(mqBody.getTransbillList())) {
            log.error("批次信息为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
            return false;
        }else {
            //消息完整性保障
            for(AirTransportBillDto dto : mqBody.getTransbillList()) {
                if(StringUtils.isBlank(dto.getBatchCode()) || StringUtils.isBlank(dto.getSealCarCode())) {
                    log.error("批次信息中批次号或者封车编码为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
                    return false;
                }
                if(StringUtils.isBlank(dto.getEndNodeCode())) {
                    log.error("批次信息中批次提货场地编码为空，航空提货计划无效，不做消费{}", JsonHelper.toJson(mqBody));
                    return false;
                }
            }
        }
        return true;
    }



    class TmsAviationPickingGoodMqBody {

        //主运单号
        private String tplBillCode;
        //航班号
        private String flightNumber;
        //始发机场
        private String beginNodeCode;
        //始发机场名称
        private String beginNodeName;
        //目的机场
        private String endNodeCode;
        //目的机场名称
        private String endNodeName;
        //计划起飞日期时间
        private Date planTakeOffTime;
        //计划降落日期时间
        private Date planTouchDownTime;
        //实际起飞日期时间
        private Date realTakeOffTime;
        //实际降落日期时间
        private Date realTouchDownTime;
        //操作类型   10 发货  20 部分改配 30 全部改配 40 起飞 50 降落 60提货
        private Integer operateType;
        //发货登记件数
        private Integer departCargoAmount;
        //发货实际重量
        private Double departCargoRealWeight;
        //批次信息
        private List<AirTransportBillDto> transbillList;
        //
        private Date operateTime;

        public String getTplBillCode() {
            return tplBillCode;
        }

        public void setTplBillCode(String tplBillCode) {
            this.tplBillCode = tplBillCode;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public String getBeginNodeCode() {
            return beginNodeCode;
        }

        public void setBeginNodeCode(String beginNodeCode) {
            this.beginNodeCode = beginNodeCode;
        }

        public String getBeginNodeName() {
            return beginNodeName;
        }

        public void setBeginNodeName(String beginNodeName) {
            this.beginNodeName = beginNodeName;
        }

        public String getEndNodeCode() {
            return endNodeCode;
        }

        public void setEndNodeCode(String endNodeCode) {
            this.endNodeCode = endNodeCode;
        }

        public String getEndNodeName() {
            return endNodeName;
        }

        public void setEndNodeName(String endNodeName) {
            this.endNodeName = endNodeName;
        }

        public Date getPlanTakeOffTime() {
            return planTakeOffTime;
        }

        public void setPlanTakeOffTime(Date planTakeOffTime) {
            this.planTakeOffTime = planTakeOffTime;
        }

        public Date getPlanTouchDownTime() {
            return planTouchDownTime;
        }

        public void setPlanTouchDownTime(Date planTouchDownTime) {
            this.planTouchDownTime = planTouchDownTime;
        }

        public Date getRealTakeOffTime() {
            return realTakeOffTime;
        }

        public void setRealTakeOffTime(Date realTakeOffTime) {
            this.realTakeOffTime = realTakeOffTime;
        }

        public Date getRealTouchDownTime() {
            return realTouchDownTime;
        }

        public void setRealTouchDownTime(Date realTouchDownTime) {
            this.realTouchDownTime = realTouchDownTime;
        }

        public Integer getOperateType() {
            return operateType;
        }

        public void setOperateType(Integer operateType) {
            this.operateType = operateType;
        }

        public Integer getDepartCargoAmount() {
            return departCargoAmount;
        }

        public void setDepartCargoAmount(Integer departCargoAmount) {
            this.departCargoAmount = departCargoAmount;
        }

        public Double getDepartCargoRealWeight() {
            return departCargoRealWeight;
        }

        public void setDepartCargoRealWeight(Double departCargoRealWeight) {
            this.departCargoRealWeight = departCargoRealWeight;
        }

        public List<AirTransportBillDto> getTransbillList() {
            return transbillList;
        }

        public void setTransbillList(List<AirTransportBillDto> transbillList) {
            this.transbillList = transbillList;
        }

        public Date getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(Date operateTime) {
            this.operateTime = operateTime;
        }
    }


    class AirTransportBillDto {
        private String batchCode;
        private String sealCarCode;
        private String beginNodeCode;
        private String beginNodeName;
        private String endNodeCode;
        private String endNodeName;
        private String transportCode;


        public String getBatchCode() {
            return batchCode;
        }

        public void setBatchCode(String batchCode) {
            this.batchCode = batchCode;
        }

        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public String getBeginNodeCode() {
            return beginNodeCode;
        }

        public void setBeginNodeCode(String beginNodeCode) {
            this.beginNodeCode = beginNodeCode;
        }

        public String getBeginNodeName() {
            return beginNodeName;
        }

        public void setBeginNodeName(String beginNodeName) {
            this.beginNodeName = beginNodeName;
        }

        public String getEndNodeCode() {
            return endNodeCode;
        }

        public void setEndNodeCode(String endNodeCode) {
            this.endNodeCode = endNodeCode;
        }

        public String getEndNodeName() {
            return endNodeName;
        }

        public void setEndNodeName(String endNodeName) {
            this.endNodeName = endNodeName;
        }

        public String getTransportCode() {
            return transportCode;
        }

        public void setTransportCode(String transportCode) {
            this.transportCode = transportCode;
        }
    }




}
