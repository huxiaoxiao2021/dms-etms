package ld;

import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @ClassName AutoSignOutRecordTaskTest
 * @Description
 * @Author wyh
 * @Date 2022/2/25 22:33
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class AutoSignOutRecordTaskTest {

    @Autowired
    private UserSignRecordService userSignRecordService;

    @Test
    public void autoHandleSignInRecordTest() {
        userSignRecordService.autoHandleSignInRecord();
    }
}
