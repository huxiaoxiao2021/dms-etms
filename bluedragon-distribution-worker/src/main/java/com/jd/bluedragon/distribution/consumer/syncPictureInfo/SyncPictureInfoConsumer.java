package com.jd.bluedragon.distribution.consumer.syncPictureInfo;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private WeightAndVolumeCheckService weightAndVolumeCheckService;


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
        if(siteCode == null){
            log.warn("站点【{}】不能为空!",siteCode);
            return;
        }

        // 给FXM发消息并更新es数据
        weightAndVolumeCheckService.sendMqAndUpdate(packageCode,siteCode);
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
