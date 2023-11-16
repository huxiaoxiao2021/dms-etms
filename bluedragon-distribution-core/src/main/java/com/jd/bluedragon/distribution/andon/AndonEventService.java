package com.jd.bluedragon.distribution.andon;

import com.jd.bluedragon.distribution.sdk.modules.andon.enums.AndonEventSourceEnum;

import java.util.Date;

public interface AndonEventService {
    void lightOn(AndonEventSourceEnum source, String sourceId, Integer siteCode, String gridCode, String andonMachineCode, Date timestamp, Object detail);

    void lightOff(AndonEventSourceEnum source, String sourceId, Integer siteCode, String gridCode, String andonMachineCode, Date timestamp, Object detail);
}
