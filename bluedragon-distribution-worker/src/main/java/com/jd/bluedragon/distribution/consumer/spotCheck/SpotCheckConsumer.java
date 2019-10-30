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
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.error(MessageFormat.format("抽检回传消息体非JSON格式，内容为【{0}】", message.getText()));
                return;
            }

            SpotCheckConsumer.AbnormalMqOfB2bSpotCheck pictureInfoMq = JsonHelper.fromJsonUseGson(message.getText(), SpotCheckConsumer.AbnormalMqOfB2bSpotCheck.class);

            //来源分拣、主动认责、B2b抽检
            Integer source = pictureInfoMq.getSource();
            Integer blameType = pictureInfoMq.getBlameType();
            Integer businessType = pictureInfoMq.getBusinessType();
            if(source!=null&&source==2
                    &&businessType!=null&&businessType==2
                    &&blameType!=null&&blameType==1){
                //B网抽检且主动认责
                OpeEntity opeEntity = new OpeEntity();
                opeEntity.setOpeType(1);//分拣中心称重、长宽高
                opeEntity.setWaybillCode(pictureInfoMq.getBillCode());
                opeEntity.setOpeDetails(new ArrayList<OpeObject>());

                OpeObject obj = new OpeObject();
                obj.setOpeSiteId(pictureInfoMq.getReviewSecondLevelId());
                obj.setOpeSiteName(pictureInfoMq.getReviewSecondLevelName());
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
        private String billCode;
        private Integer dutyType;
        private String dutyErp;
        private BigDecimal weight;
        private BigDecimal volume;
        private Double reviewWeight;
        private Double reviewVolume;
        private Long reviewDate;
        private Integer reviewFirstLevelId;
        private String reviewFirstLevelName;
        private Integer reviewSecondLevelId;
        private String reviewSecondLevelName;
        private String reviewErp;
        private Integer isExcess;
        private Integer blameType;
        private Integer businessType;

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

        public String getBillCode() {
            return billCode;
        }

        public void setBillCode(String billCode) {
            this.billCode = billCode;
        }

        public Integer getDutyType() {
            return dutyType;
        }

        public void setDutyType(Integer dutyType) {
            this.dutyType = dutyType;
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

        public Long getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(Long reviewDate) {
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

        public String getReviewErp() {
            return reviewErp;
        }

        public void setReviewErp(String reviewErp) {
            this.reviewErp = reviewErp;
        }

        public Integer getIsExcess() {
            return isExcess;
        }

        public void setIsExcess(Integer isExcess) {
            this.isExcess = isExcess;
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
