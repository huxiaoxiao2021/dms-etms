package com.jd.bluedragon.distribution.andon;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.sdk.modules.andon.enums.AndonEventSourceEnum;
import com.jd.bluedragon.distribution.sdk.modules.andon.model.AndonCombinedCmd;
import com.jd.bluedragon.distribution.sdk.modules.andon.model.AndonEvent;
import com.jd.bluedragon.distribution.sdk.modules.andon.utils.AndonCmdNoGenerator;
import com.jd.bluedragon.distribution.sdk.modules.andon.utils.AndonEventIdGenerator;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dto.violentSorting.ViolentSortingDto;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.jd.bluedragon.distribution.sdk.modules.andon.enums.AndonEventSourceEnum.VIOLENT_SORTING;

@Service("andonEventService")
public class AndonEventServiceImpl implements AndonEventService {
    private static final Logger logger = LoggerFactory.getLogger(AndonEventServiceImpl.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    @Qualifier("andonEventProducer")
    private DefaultJMQProducer andonEventProducer;
    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;


    @Override
    @JProfiler(jKey = "DMSWEB.AndonEventService.lightOn", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public void lightOn(AndonEventSourceEnum source, String sourceId, Integer siteCode, String gridCode, String andonMachineCode, Date timestamp, Object detail) {
        switch (source) {
            // 暴力分拣
            case VIOLENT_SORTING:
                ViolentSortingDto d = (ViolentSortingDto) detail;
                // 发送亮灯和蜂鸣器响
                emitViolentSortingLightOn2DeviceMQ(d, andonMachineCode);
                break;
            default:
                logger.warn("安灯事件 未知来源 忽略");
                break;
        }
    }


    // 亮灯
    private void emitViolentSortingLightOn2DeviceMQ(ViolentSortingDto violentSorting, String andonMachineCode) {
//        String eventId = AndonEventIdGenerator.generate(violentSorting.getSiteCode(), violentSorting.getGridCode(), new Date(), andonMachineCode, VIOLENT_SORTING, String.valueOf(violentSorting.getId()));
        long andon = sequenceGenAdaptor.newId("ANDON");
        String eventId = new Long(andon).toString();
        logger.info("暴力分拣事件亮灯，eventId:[{}]", eventId);
        AndonEvent event = new AndonEvent();
        event.setEventId(eventId);
        event.setMachineCode(andonMachineCode);
        event.setAreaHubCode(violentSorting.getAreaHubCode());
        event.setAreaHubName(violentSorting.getAreaHubName());
        event.setProvinceAgencyCode(violentSorting.getProvinceAgencyCode());
        event.setProvinceAgencyName(violentSorting.getProvinceAgencyName());
        event.setSiteCode(violentSorting.getSiteCode());
        event.setSiteName(violentSorting.getSiteName());
        event.setGridCode(violentSorting.getGridCode());
        event.setGridBusinessKey(violentSorting.getGridBusinessKey());
        // 灯亮，蜂鸣器响
        String cmdString = AndonCombinedCmd.RED_LIGHT_ON_AND_BUZZER_ON.toCmdString(8);
        event.setCmd(cmdString);
        // 指令id
        String cmdNo = AndonCmdNoGenerator.CCmdNo();
        event.setCmdNo(cmdNo);
        event.setEventSource(VIOLENT_SORTING.getCode());
        event.setEventSourceId(String.valueOf(violentSorting.getId()));
        event.setDetails((JSONObject) JSONObject.toJSON(violentSorting));
        event.setAndonGridOwnerErp(violentSorting.getOwnerUserErp());

        // 防止key超长
        if (eventId.length() > 50) {
            eventId = eventId.substring(0, 50);
        }
        try {
            andonEventProducer.send(eventId, JSONObject.toJSONString(event));
        } catch (JMQException e) {
            logger.warn("暴力分拣安灯亮灯mq发送失败： " + JSONObject.toJSONString(event));
            throw new RuntimeException(e);
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AndonEventService.lightOff", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public void lightOff(AndonEventSourceEnum source, String sourceId, Integer siteCode, String gridCode, String andonMachineCode, Date timestamp, Object detail) {

    }
}
