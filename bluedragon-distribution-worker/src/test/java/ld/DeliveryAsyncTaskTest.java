package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferService;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wyh
 * @className DeliveryAsyncTaskTest
 * @description
 * @date 2021/8/11 14:28
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DeliveryAsyncTaskTest {

    @Autowired
    private AsynBufferService asynBufferService;

    @Autowired
    private IDeliveryOperationService deliveryOperationService;

    @Test
    public void deliverySendProcessTest() {

    }

    @Test
    public void asyncHandleDeliveryTest() {
        List<SendM> sendMList = new ArrayList<>();
        SendM sendM = new SendM();
        sendM.setBoxCode("JD0003359334696-1-1-");
        sendM.setCreateSiteCode(910);
        sendM.setReceiveSiteCode(39);
        sendM.setCreateUserCode(111111);
        sendM.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        sendM.setCreateUser("bjxings");
        sendM.setSendCode("910-39-20210811163738813");
        sendM.setCreateTime(new Date());
        sendM.setOperateTime(new Date());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode("");
        sendM.setTransporttype(1);

        sendMList.add(sendM);

        deliveryOperationService.asyncHandleDelivery(sendMList, SendBizSourceEnum.OLD_PACKAGE_SEND);
    }
}
