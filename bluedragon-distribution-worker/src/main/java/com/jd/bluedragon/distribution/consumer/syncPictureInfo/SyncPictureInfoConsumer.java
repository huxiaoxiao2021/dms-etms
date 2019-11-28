package com.jd.bluedragon.distribution.consumer.syncPictureInfo;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: syncPictureInfoConsumer
 * @Description: 同步dmsAuto图片信息
 * @author: hujiping
 * @date: 2019/5/10 18:04
 */
@Service("syncPictureInfoConsumer")
public class SyncPictureInfoConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(SyncPictureInfoConsumer.class);

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    @Qualifier("dmsWeightVolumeAbnormal")
    private DefaultJMQProducer dmsWeightVolumeAbnormal;


    @Override
    public void consume(Message message) throws Exception {

        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("auto下发消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        SyncPictureInfoConsumer.PictureInfoMq pictureInfoMq = JsonHelper.fromJsonUseGson(message.getText(), SyncPictureInfoConsumer.PictureInfoMq.class);

        if(pictureInfoMq == null) {
            log.warn("auto下发消息体转换失败，内容为【{}】", message.getText());
            return;
        }

        //1.运单号/包裹号校验
        String packageCode = pictureInfoMq.getWaybillOrPackCode();
        if(!WaybillUtil.isWaybillCode(packageCode) && !WaybillUtil.isPackageCode(packageCode)){
            log.warn("运单号/包裹号{}不符合规则!",packageCode);
            return;
        }
        Integer siteCode = pictureInfoMq.getSiteCode();

        try{
            //2.判断es是否存在此数据（包裹号、站点）
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(1);
            condition.setIsHasPicture(0);
            condition.setPackageCode(packageCode);
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);

            //3.存在则更新不存在则返回
            if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().size() == 0){
                return;
            }
            WeightVolumeCollectDto weightVolumeCollectDto = baseEntity.getData().get(0);
            //4.获取图片链接
            String pictureAddress = weightVolumeCollectDto.getPictureAddress();
            Date reviewDate = weightVolumeCollectDto.getReviewDate();
            InvokeResult<String> result = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);
            if(result != null && !StringHelper.isEmpty(result.getData())){
                pictureAddress = result.getData();
            }

            if(!StringHelper.isEmpty(pictureAddress)){
                //4.存在则更新es数据
                WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
                dto.setPackageCode(packageCode);
                dto.setReviewSiteCode(siteCode);
                dto.setIsHasPicture(1);
                dto.setPictureAddress(pictureAddress);
                reportExternalService.updateForWeightVolume(dto);

                AbnormalPictureMq abnormalPictureMq = new AbnormalPictureMq();
                abnormalPictureMq.setAbnormalId(packageCode+"_"+reviewDate.getTime());
                abnormalPictureMq.setWaybillCode(packageCode);
                abnormalPictureMq.setUploadTime(pictureInfoMq.getUpLoadTime());
                abnormalPictureMq.setExcessPictureAddress(pictureAddress);
                this.log.info("发送MQ[{}],业务ID[{}]",dmsWeightVolumeAbnormal.getTopic(),abnormalPictureMq.getWaybillCode());
                dmsWeightVolumeAbnormal.send(abnormalPictureMq.getAbnormalId(),JsonHelper.toJson(abnormalPictureMq));
            }

        }catch (Exception e){
            log.error("服务异常! {}|{}",packageCode,siteCode,e);
        }

    }


    //自动化同步图片信息实体
    public class PictureInfoMq{

        /**
         * 扫描条码
         * */
        private String waybillOrPackCode;

        /**
         * 站点id
         * */
        private Integer siteCode;

        /**
         * 图片名
         * */
        private String pictureName;

        /**
         * 机器编码
         * */
        private String machineCode;

        /**
         * 上传时间
         * */
        private Long upLoadTime;

        public String getWaybillOrPackCode() {
            return waybillOrPackCode;
        }

        public void setWaybillOrPackCode(String waybillOrPackCode) {
            this.waybillOrPackCode = waybillOrPackCode;
        }

        public Integer getSiteCode() {
            return siteCode;
        }

        public void setSiteCode(Integer siteCode) {
            this.siteCode = siteCode;
        }

        public String getPictureName() {
            return pictureName;
        }

        public void setPictureName(String pictureName) {
            this.pictureName = pictureName;
        }

        public String getMachineCode() {
            return machineCode;
        }

        public void setMachineCode(String machineCode) {
            this.machineCode = machineCode;
        }

        public Long getUpLoadTime() {
            return upLoadTime;
        }

        public void setUpLoadTime(Long upLoadTime) {
            this.upLoadTime = upLoadTime;
        }
    }


}
