package com.jd.bluedragon.distribution.weight.service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.distribution.weight.domain.OpeSendObject;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("weightService")
public class WeightServiceImpl implements WeightService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    @Qualifier("dmsWeightSendMQ")
    private DefaultJMQProducer dmsWeightSendMQ;

    @Autowired
    private WaybillPackageApi waybillPackageApiJsf;

    public boolean doWeightTrack(Task task) {
        this.logger.info("向运单系统回传包裹称重信息: ");
        //WeightResponse response = null;
        String body = null;
        try {
             body = task.getBody();
            if (!StringUtils.isNotBlank(body)) {
                logger.error("向运单回传包裹称重信息失败，称重信息为空");
                return false;
            }
            //response = WeightClient.weightTrack(body.substring(1, body.length() - 1));
            Map<String, Object> map = waybillPackageApiJsf.uploadOpe(body.substring(1, body.length() - 1));
            this.sendMQ(body);

            if (map != null && map.containsKey("code") && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(map.get("code").toString())) {
                this.logger.info("向运单系统回传包裹称重信息：\n" + body);
                this.logger.info("向运单系统回传包裹称重信息成功：" + (null != map ? JsonHelper.toJson(map) : " result null"));
                return true;
            } else {
                this.logger.info("向运单系统回传包裹称重信息：\n" + body);
                this.logger.error("向运单系统回传包裹称重信息失败： " + (null != map ? JsonHelper.toJson(map) : " result null"));
                return false;
            }
        } catch (Exception e) {
            this.logger.info("向运单系统回传包裹称重信息：\n" + body);
            this.logger.error("处理称重回传任务发生异常，异常信息为：", e);
        }
        return false;
    }

    /**
     * 发送包裹称重、体积信息MQ
     *
     * @param body 数据内容
     * @throws JMQException
     */
    private void sendMQ(String body) {
        try {
            List<OpeObject> opeDetails = JsonHelper.jsonToArray(body.substring(1, body.length() - 1), OpeEntity.class).getOpeDetails();
            if (opeDetails != null && opeDetails.size() > 0) {
                for (OpeObject ope : opeDetails) {
                    OpeSendObject opeSend = new OpeSendObject();
                    opeSend.setPackage_code(ope.getPackageCode());
                    opeSend.setDms_site_id(ope.getOpeSiteId());
                    opeSend.setThisUpdateTime(this.getDateLong(ope.getOpeTime()));
                    opeSend.setWeight(ope.getpWeight());
                    opeSend.setLength(ope.getpLength());
                    opeSend.setWeight(ope.getpWidth());
                    opeSend.setHigh(ope.getpHigh());
                    opeSend.setOpeUserId(ope.getOpeUserId());
                    opeSend.setOpeUserName(ope.getOpeUserName());
                    if (ope.getpHigh() != null && ope.getpLength() != null && ope.getpWidth() != null) {
                        //计算体积
                        opeSend.setVolume(ope.getpHigh() * ope.getpLength() * ope.getpWidth());
                    } else {
                        opeSend.setVolume(null);
                    }
                    this.dmsWeightSendMQ.send(ope.getPackageCode(), JsonHelper.toJson(opeSend));
                }
            }
        } catch (Exception e) {
            Profiler.businessAlarm("DmsWorker.send_weight_mq_error",(new Date()).getTime(),"向分拣中心监控报表系统发送称重MQ消息失败，异常信息为：" + e.getMessage());
                this.logger.error("向分拣中心监控报表系统发送称重MQ消息失败，异常信息为：", e);
        }
    }

    /**
     * 根据字符串类型日期转换为时间戳
     * @param dateStr
     * @return
     */
    private Long getDateLong(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(dateStr);
                return date.getTime();
            } catch (ParseException e) {
                this.logger.error("无效的日期格式：" + dateStr, e);
                return null;
            }
        }
        return null;
    }
}
