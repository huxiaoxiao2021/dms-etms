package com.jd.bluedragon.distribution.schedule;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context-test.xml" })
public class DmsScheduleInfoServiceTest{
    @Autowired
    private DmsScheduleInfoService dmsScheduleInfoService;
    @Test
    public void testPrintEdnPickingList(){
        LoginUser user=new LoginUser();
        user.setUserErp("cl");
        dmsScheduleInfoService.printEdnPickingList("ZD20200507",user);
    }
}
