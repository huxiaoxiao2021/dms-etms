package com.jd.bluedragon.distribution.weight.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
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
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Resource(name = "goddessService")
    private GoddessService goddessService;

    private static final Type WAYBILL_WEIGHT=new TypeToken<List<OpeEntity>>(){}.getType();

    public boolean doWeightTrack(Task task) {
        String body = null;
        Goddess goddess=builderGoddess(task.getBody());
        try {
             body = task.getBody();
            if (!StringUtils.isNotBlank(body)) {
                logger.error("向运单回传包裹称重信息失败，称重信息为空");
                return false;
            }
            String newBody = dealMinusVolume(body);
            if(StringUtils.isNotEmpty(newBody)){
                body = newBody;
            }

            Map<String, Object> map = waybillPackageApiJsf.uploadOpe(body.substring(1, body.length() - 1));
            goddess.setHead(MessageFormat.format("提交称重至运单:结果{0}", JsonHelper.toJson(map)));
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
            goddess.setHead(MessageFormat.format("提交称重至运单:异常为{0}",e.getMessage()));
        }finally {
            goddessService.save(goddess);
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
                    opeSend.setWidth(ope.getpWidth());
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
     * 处理长宽高重量为空或小于0 置为0
     * @param body 数据内容
     * @return
     */
    private String dealMinusVolume(String body){
        try{
            JavaType javaType = JsonHelper.getCollectionType(ArrayList.class, OpeEntity.class);
            List<OpeEntity> opeEntities = JsonHelper.getMapper().readValue(body, javaType);
            OpeEntity opeEntity = opeEntities.get(0);

            List<OpeObject> opeDetails = opeEntity.getOpeDetails();
            if (opeDetails != null && opeDetails.size() > 0) {
                boolean update = false;
                for (OpeObject ope : opeDetails) {
                    if(ope.getpWeight() == null || ope.getpWeight() < 0){
                        ope.setpWeight(0f);
                        update = true;
                    }
                    if(ope.getpLength() == null || ope.getpLength() < 0){
                        ope.setpLength(0f);
                        update = true;
                    }
                    if(ope.getpWidth() == null || ope.getpWidth() < 0){
                        ope.setpWidth(0f);
                        update = true;
                    }
                    if(ope.getpHigh() == null || ope.getpHigh() < 0){
                        ope.setpHigh(0f);
                        update = true;
                    }
                }
                //如果有修改，才重新生成json格式数据
                if(update){
                    return   JsonHelper.toJson(opeEntities);
                }

            }
        }catch (Exception e){
            this.logger.error("处理长宽高重量为空或小于0置为0时异常", e);
        }
        return null;
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

    private Goddess builderGoddess(String body){
        Goddess domain = new Goddess();
        try {
            List<OpeEntity> waybillWeights = JsonHelper.fromJsonUseGson(body, WAYBILL_WEIGHT);
            if (null != waybillWeights) {
                for (OpeEntity entity : waybillWeights) {
                    domain.setKey(entity.getWaybillCode());
                    domain.setBody(body);
                }
            }
        }catch (Throwable throwable){
            logger.error("称重JSON解析失败",throwable);
        }
        return domain;
    }
}
