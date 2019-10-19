package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.error(MessageFormat.format("抽检回传消息体非JSON格式，内容为【{0}】", message.getText()));
                return;
            }

            SpotCheckConsumer.AbnormalMqOfB2bSpotCheck pictureInfoMq = JsonHelper.fromJsonUseGson(message.getText(), SpotCheckConsumer.AbnormalMqOfB2bSpotCheck.class);

            //主动认责
            Integer blameType = pictureInfoMq.getBlameType();
            Integer businessType = pictureInfoMq.getBusinessType();
            if(businessType == 2 && blameType == 1){
                //B网抽检且主动认责
                OpeEntity opeEntity = new OpeEntity();
                opeEntity.setOpeType(1);//分拣中心称重、长宽高
                opeEntity.setWaybillCode(pictureInfoMq.getBillCode());
                opeEntity.setOpeDetails(new ArrayList<OpeObject>());

                OpeObject obj = new OpeObject();
                obj.setOpeSiteId(pictureInfoMq.getReviewSecondLevelId());
                obj.setOpeSiteName(pictureInfoMq.getSecondLevelName());
                obj.setpWidth(pictureInfoMq.getReviewWidth()==null?null:pictureInfoMq.getReviewWidth().floatValue());
                obj.setpLength(pictureInfoMq.getReviewLength()==null?null:pictureInfoMq.getReviewLength().floatValue());
                obj.setpHigh(pictureInfoMq.getReviewHeight()==null?null:pictureInfoMq.getReviewHeight().floatValue());
                obj.setpWeight(pictureInfoMq.getReviewWeight()==null?null:pictureInfoMq.getReviewWeight().floatValue());
                obj.setPackageCode(pictureInfoMq.getBillCode());
//            obj.setOpeUserId();
                obj.setOpeUserName(pictureInfoMq.getReviewErp());
                obj.setOpeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pictureInfoMq.getReviewDate()));
                opeEntity.getOpeDetails().add(obj);
                String body = "[" + JsonHelper.toJson(opeEntity) + "]";
                Task task = new Task();
                task.setBody(body);
                task.setType(Task.TASK_TYPE_WEIGHT);
                task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
                task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
                task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
                task.setKeyword2("上传长宽高、重量");
                task.setBody(body);
                task.setSequenceName(Task.getSequenceName(task.getTableName()));
                task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
                task.setOwnSign(BusinessHelper.getOwnSign());
                taskService.add(task);
            }
        }catch (Exception e){
            logger.error("转换异常：" + message.getText(),e);
        }
    }

    public class AbnormalMqOfB2bSpotCheck implements Serializable {
        private static final long serialVersionUID = 1L;
        private String from;
        private String to;
        private Integer source;
        private String id;
        private Integer errorType;
        private String billCode;
        private	Integer	 businessObjectId;
        private	String	 businessObject;
        private Integer dutyType;
        private String firstLevelId;
        private String firstLevelName;
        private String secondLevelId;
        private String secondLevelName;
        private String threeLevelId;
        private String threeLevelName;
        private String dutyErp;
        private BigDecimal weight;
        private BigDecimal volume;
        private Double reviewWeight;
        private Double reviewVolume;
        private Integer deliverCityId;
        private String deliverCityName;
        private Integer provinceId;
        private Integer cityId;
        private Integer countyId;
        private String provinceName;
        private String cityName;
        private String countyName;
        private Date reviewDate;
        private Integer reviewFirstLevelId;
        private String reviewFirstLevelName;
        private Integer reviewSecondLevelId;
        private String reviewSecondLevelName;
        private Integer reviewThreeLevelId;
        private String reviewThreeLevelName;
        private Integer reviewMechanismType;
        private String reviewErp;
        private String diffStandard;
        private Double weightDiff;
        private Double volumeDiff;
        private Integer isExcess;
        private String pictureAddress;
        private Integer isAccusation;
        private Integer isNeedBlame;
        private String operateUser;
        private Date operateTime;
        private Integer judgeDutyResult;
        private Integer blameDutyType;
        private Integer blameFirstLevelId;
        private String blameFirstLevelName;
        private Integer blameSecondLevelId;
        private String blameSecondLevelName;
        private Integer blameThreeLevelId;
        private String blameThreeLevelName;
        private Double blameWidth;
        private Double blameLength;
        private Double blameHeight;
        private Double blameVolume;
        private Double blameWeight;
        private Integer blameType;
        private Integer businessType;

        //新增 TODO
        private Double reviewLength;
        private Double reviewWidth;
        private Double reviewHeight;

        public Double getReviewLength() {
            return reviewLength;
        }

        public void setReviewLength(Double reviewLength) {
            this.reviewLength = reviewLength;
        }

        public Double getReviewWidth() {
            return reviewWidth;
        }

        public void setReviewWidth(Double reviewWidth) {
            this.reviewWidth = reviewWidth;
        }

        public Double getReviewHeight() {
            return reviewHeight;
        }

        public void setReviewHeight(Double reviewHeight) {
            this.reviewHeight = reviewHeight;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Integer getSource() {
            return source;
        }

        public void setSource(Integer source) {
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getErrorType() {
            return errorType;
        }

        public void setErrorType(Integer errorType) {
            this.errorType = errorType;
        }

        public String getBillCode() {
            return billCode;
        }

        public void setBillCode(String billCode) {
            this.billCode = billCode;
        }

        public Integer getBusinessObjectId() {
            return businessObjectId;
        }

        public void setBusinessObjectId(Integer businessObjectId) {
            this.businessObjectId = businessObjectId;
        }

        public String getBusinessObject() {
            return businessObject;
        }

        public void setBusinessObject(String businessObject) {
            this.businessObject = businessObject;
        }

        public Integer getDutyType() {
            return dutyType;
        }

        public void setDutyType(Integer dutyType) {
            this.dutyType = dutyType;
        }

        public String getFirstLevelId() {
            return firstLevelId;
        }

        public void setFirstLevelId(String firstLevelId) {
            this.firstLevelId = firstLevelId;
        }

        public String getFirstLevelName() {
            return firstLevelName;
        }

        public void setFirstLevelName(String firstLevelName) {
            this.firstLevelName = firstLevelName;
        }

        public String getSecondLevelId() {
            return secondLevelId;
        }

        public void setSecondLevelId(String secondLevelId) {
            this.secondLevelId = secondLevelId;
        }

        public String getSecondLevelName() {
            return secondLevelName;
        }

        public void setSecondLevelName(String secondLevelName) {
            this.secondLevelName = secondLevelName;
        }

        public String getThreeLevelId() {
            return threeLevelId;
        }

        public void setThreeLevelId(String threeLevelId) {
            this.threeLevelId = threeLevelId;
        }

        public String getThreeLevelName() {
            return threeLevelName;
        }

        public void setThreeLevelName(String threeLevelName) {
            this.threeLevelName = threeLevelName;
        }

        public String getDutyErp() {
            return dutyErp;
        }

        public void setDutyErp(String dutyErp) {
            this.dutyErp = dutyErp;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getVolume() {
            return volume;
        }

        public void setVolume(BigDecimal volume) {
            this.volume = volume;
        }

        public Double getReviewWeight() {
            return reviewWeight;
        }

        public void setReviewWeight(Double reviewWeight) {
            this.reviewWeight = reviewWeight;
        }

        public Double getReviewVolume() {
            return reviewVolume;
        }

        public void setReviewVolume(Double reviewVolume) {
            this.reviewVolume = reviewVolume;
        }

        public Integer getDeliverCityId() {
            return deliverCityId;
        }

        public void setDeliverCityId(Integer deliverCityId) {
            this.deliverCityId = deliverCityId;
        }

        public String getDeliverCityName() {
            return deliverCityName;
        }

        public void setDeliverCityName(String deliverCityName) {
            this.deliverCityName = deliverCityName;
        }

        public Integer getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(Integer provinceId) {
            this.provinceId = provinceId;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public Integer getCountyId() {
            return countyId;
        }

        public void setCountyId(Integer countyId) {
            this.countyId = countyId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCountyName() {
            return countyName;
        }

        public void setCountyName(String countyName) {
            this.countyName = countyName;
        }

        public Date getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(Date reviewDate) {
            this.reviewDate = reviewDate;
        }

        public Integer getReviewFirstLevelId() {
            return reviewFirstLevelId;
        }

        public void setReviewFirstLevelId(Integer reviewFirstLevelId) {
            this.reviewFirstLevelId = reviewFirstLevelId;
        }

        public String getReviewFirstLevelName() {
            return reviewFirstLevelName;
        }

        public void setReviewFirstLevelName(String reviewFirstLevelName) {
            this.reviewFirstLevelName = reviewFirstLevelName;
        }

        public Integer getReviewSecondLevelId() {
            return reviewSecondLevelId;
        }

        public void setReviewSecondLevelId(Integer reviewSecondLevelId) {
            this.reviewSecondLevelId = reviewSecondLevelId;
        }

        public String getReviewSecondLevelName() {
            return reviewSecondLevelName;
        }

        public void setReviewSecondLevelName(String reviewSecondLevelName) {
            this.reviewSecondLevelName = reviewSecondLevelName;
        }

        public Integer getReviewThreeLevelId() {
            return reviewThreeLevelId;
        }

        public void setReviewThreeLevelId(Integer reviewThreeLevelId) {
            this.reviewThreeLevelId = reviewThreeLevelId;
        }

        public String getReviewThreeLevelName() {
            return reviewThreeLevelName;
        }

        public void setReviewThreeLevelName(String reviewThreeLevelName) {
            this.reviewThreeLevelName = reviewThreeLevelName;
        }

        public Integer getReviewMechanismType() {
            return reviewMechanismType;
        }

        public void setReviewMechanismType(Integer reviewMechanismType) {
            this.reviewMechanismType = reviewMechanismType;
        }

        public String getReviewErp() {
            return reviewErp;
        }

        public void setReviewErp(String reviewErp) {
            this.reviewErp = reviewErp;
        }

        public String getDiffStandard() {
            return diffStandard;
        }

        public void setDiffStandard(String diffStandard) {
            this.diffStandard = diffStandard;
        }

        public Double getWeightDiff() {
            return weightDiff;
        }

        public void setWeightDiff(Double weightDiff) {
            this.weightDiff = weightDiff;
        }

        public Double getVolumeDiff() {
            return volumeDiff;
        }

        public void setVolumeDiff(Double volumeDiff) {
            this.volumeDiff = volumeDiff;
        }

        public Integer getIsExcess() {
            return isExcess;
        }

        public void setIsExcess(Integer isExcess) {
            this.isExcess = isExcess;
        }

        public String getPictureAddress() {
            return pictureAddress;
        }

        public void setPictureAddress(String pictureAddress) {
            this.pictureAddress = pictureAddress;
        }

        public Integer getIsAccusation() {
            return isAccusation;
        }

        public void setIsAccusation(Integer isAccusation) {
            this.isAccusation = isAccusation;
        }

        public Integer getIsNeedBlame() {
            return isNeedBlame;
        }

        public void setIsNeedBlame(Integer isNeedBlame) {
            this.isNeedBlame = isNeedBlame;
        }

        public String getOperateUser() {
            return operateUser;
        }

        public void setOperateUser(String operateUser) {
            this.operateUser = operateUser;
        }

        public Date getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(Date operateTime) {
            this.operateTime = operateTime;
        }

        public Integer getJudgeDutyResult() {
            return judgeDutyResult;
        }

        public void setJudgeDutyResult(Integer judgeDutyResult) {
            this.judgeDutyResult = judgeDutyResult;
        }

        public Integer getBlameDutyType() {
            return blameDutyType;
        }

        public void setBlameDutyType(Integer blameDutyType) {
            this.blameDutyType = blameDutyType;
        }

        public Integer getBlameFirstLevelId() {
            return blameFirstLevelId;
        }

        public void setBlameFirstLevelId(Integer blameFirstLevelId) {
            this.blameFirstLevelId = blameFirstLevelId;
        }

        public String getBlameFirstLevelName() {
            return blameFirstLevelName;
        }

        public void setBlameFirstLevelName(String blameFirstLevelName) {
            this.blameFirstLevelName = blameFirstLevelName;
        }

        public Integer getBlameSecondLevelId() {
            return blameSecondLevelId;
        }

        public void setBlameSecondLevelId(Integer blameSecondLevelId) {
            this.blameSecondLevelId = blameSecondLevelId;
        }

        public String getBlameSecondLevelName() {
            return blameSecondLevelName;
        }

        public void setBlameSecondLevelName(String blameSecondLevelName) {
            this.blameSecondLevelName = blameSecondLevelName;
        }

        public Integer getBlameThreeLevelId() {
            return blameThreeLevelId;
        }

        public void setBlameThreeLevelId(Integer blameThreeLevelId) {
            this.blameThreeLevelId = blameThreeLevelId;
        }

        public String getBlameThreeLevelName() {
            return blameThreeLevelName;
        }

        public void setBlameThreeLevelName(String blameThreeLevelName) {
            this.blameThreeLevelName = blameThreeLevelName;
        }

        public Double getBlameWidth() {
            return blameWidth;
        }

        public void setBlameWidth(Double blameWidth) {
            this.blameWidth = blameWidth;
        }

        public Double getBlameLength() {
            return blameLength;
        }

        public void setBlameLength(Double blameLength) {
            this.blameLength = blameLength;
        }

        public Double getBlameHeight() {
            return blameHeight;
        }

        public void setBlameHeight(Double blameHeight) {
            this.blameHeight = blameHeight;
        }

        public Double getBlameVolume() {
            return blameVolume;
        }

        public void setBlameVolume(Double blameVolume) {
            this.blameVolume = blameVolume;
        }

        public Double getBlameWeight() {
            return blameWeight;
        }

        public void setBlameWeight(Double blameWeight) {
            this.blameWeight = blameWeight;
        }

        public Integer getBlameType() {
            return blameType;
        }

        public void setBlameType(Integer blameType) {
            this.blameType = blameType;
        }

        public Integer getBusinessType() {
            return businessType;
        }

        public void setBusinessType(Integer businessType) {
            this.businessType = businessType;
        }
    }


}
