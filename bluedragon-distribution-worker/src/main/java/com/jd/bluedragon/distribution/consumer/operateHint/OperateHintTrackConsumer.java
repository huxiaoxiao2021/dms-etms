package com.jd.bluedragon.distribution.consumer.operateHint;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintTrackService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by xumei3 on 2018/7/26.
 */
@Service("operateHintTrackConsumer")
public class OperateHintTrackConsumer extends MessageBaseConsumer {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DmsOperateHintTrackService dmsOperateHintTrackService;

    @Autowired
    private DmsOperateHintService dmsOperateHintService;

    @Autowired
    SiteService siteService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        this.logger.debug("OperateHintTrackConsumer consume --> 消息Body为【"
                + message.getText() + "】");
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.logger.warn("OperateHintTrackConsumer consume -->消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("OperateHintTrackConsumer consume -->消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        DmsOperateHintTrack track = JsonHelper.fromJson(message.getText(),DmsOperateHintTrack.class);
        if (track == null) {
            this.logger.error("OperateHintTrackConsumer consume -->消息转换对象失败：" + message.getText());
            return;
        }

        //完善追踪信息
        if(track.getHintDmsCode() != null){
            BaseStaffSiteOrgDto site = siteService.getSite(track.getHintDmsCode());
            if(site != null){
                track.setHintDmsName(site.getSiteName());
            }
        }
        //1.写表
        dmsOperateHintTrackService.save(track);

        //2.自动更新状态
        DmsOperateHint operateHint = new DmsOperateHint();
        operateHint.setWaybillCode(track.getWaybillCode());
        operateHint.setIsEnable(1);
        operateHint = dmsOperateHintService.getEnabledOperateHint(operateHint);

        if(operateHint != null ){
            Integer dmsCode = operateHint.getDmsSiteCode();
            if(!dmsCode.equals(track.getHintDmsCode())){
                operateHint.setIsEnable(0);
                dmsOperateHintService.saveOrUpdate(operateHint);
            }
        }
    }
}
