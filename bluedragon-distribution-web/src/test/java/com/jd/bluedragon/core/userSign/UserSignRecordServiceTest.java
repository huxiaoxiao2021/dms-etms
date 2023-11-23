package com.jd.bluedragon.core.userSign;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
@Slf4j
public class UserSignRecordServiceTest {
    @Autowired
    private UserSignRecordService userSignRecordService;

    @Test
    public void signAutoWithGroup() {
        UserSignRequest request = new UserSignRequest();
        request.setOperateUserCode("wuyoude");
        request.setOperateUserName("吴有德");
        request.setPositionCode("GW00017001");
        request.setScanUserCode("444172219760404661x");
        request.setUserName("张三");
        JdCResponse response = userSignRecordService.signAutoWithGroup(request);
        log.info("result {}", JsonHelper.toJson(response));
    }
}
