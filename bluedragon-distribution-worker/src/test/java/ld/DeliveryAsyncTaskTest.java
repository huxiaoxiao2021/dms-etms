package ld;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferService;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintInternalService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

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
    public void deliverySendProcessTest() throws Exception {
        String json = "{\n" +
                "    \"body\": \"{\\n  \\\"sendM\\\" : {\\n    \\\"sendCode\\\" : \\\"833342-1326978-20211011210317515\\\",\\n    \\\"createSiteCode\\\" : 833342,\\n    \\\"receiveSiteCode\\\" : 1326978,\\n    \\\"sendType\\\" : 10,\\n    \\\"createUser\\\" : \\\"梁奕晖\\\",\\n    \\\"createUserCode\\\" : 20839621,\\n    \\\"operateTime\\\" : 1633960576490,\\n    \\\"createTime\\\" : 1633960576490,\\n    \\\"yn\\\" : 1,\\n    \\\"transporttype\\\" : 0,\\n    \\\"bizSource\\\" : 9\\n  },\\n  \\\"keyType\\\" : \\\"BY_WAYBILL\\\",\\n  \\\"batchUniqKey\\\" : \\\"JDV000705321163_833342\\\",\\n  \\\"waybillCode\\\" : \\\"JDV000705321163\\\",\\n  \\\"pageNo\\\" : 1,\\n  \\\"pageSize\\\" : 1000,\\n  \\\"totalPage\\\" : 1\\n}\",\n" +
                "    \"createSiteCode\": 833342,\n" +
                "    \"executeTime\": 1633960576519,\n" +
                "    \"fingerprint\": \"7E25211E9380488CBF90602C535FD04F\",\n" +
                "    \"keyword1\": \"BY_WAYBILL\",\n" +
                "    \"keyword2\": \"1_1_JDV000705321163\",\n" +
                "    \"ownSign\": \"DMS\",\n" +
                "    \"receiveSiteCode\": 1326978,\n" +
                "    \"sequenceName\": \"SEQ_TASK_SORTING\",\n" +
                "    \"tableName\": \"task_send\",\n" +
                "    \"type\": 1350\n" +
                "}";
        final Task task = JsonHelper.fromJson(json, Task.class);
        ExecutorService executorService =  new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (;;) {
            Thread.sleep(10);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        asynBufferService.deliverySendProcess(task);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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

    @Autowired
    private PackagePrintInternalService packagePrintInternalService;

    @Test
    public void printCompleteTest() {
        String json = "{\n" +
                "    \"systemCode\": \"dms\",\n" +
                "    \"secretKey\": \"123456\",\n" +
                "    \"businessType\": \"1003\",\n" +
                "    \"operateType\": \"100302\",\n" +
                "    \"data\": {\n" +
                "        \"waybillCode\": \"JDVF00001319770\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"operatorName\": \"邢松\",\n" +
                "        \"operatorErp\": \"bjxings\",\n" +
                "        \"opeTime\": \"1638879104893\"\n" +
                "    }\n" +
                "}";

        JdCommand<PrintCompleteRequest> request = JsonHelper.fromJsonUseGson(json, new TypeToken<JdCommand<PrintCompleteRequest>>(){}.getType());
        packagePrintInternalService.printComplete(request);

    }
}
