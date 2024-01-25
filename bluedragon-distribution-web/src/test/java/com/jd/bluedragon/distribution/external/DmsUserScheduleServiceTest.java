package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.distribution.api.request.sendcode.DmsUserScheduleRequest;
import com.jd.bluedragon.distribution.external.service.DmsUserScheduleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class DmsUserScheduleServiceTest {
    @Autowired
    private DmsUserScheduleService userScheduleService;

    @Test
    public void allowEntryTurnStileTest() {
        DmsUserScheduleRequest request = new DmsUserScheduleRequest();
        request.setUserCode("liuduo8");
        JdResponse<Boolean> response = userScheduleService.allowEntryTurnStile(request);
        log.info("response {}", JsonHelper.toJson(response));
    }

}
