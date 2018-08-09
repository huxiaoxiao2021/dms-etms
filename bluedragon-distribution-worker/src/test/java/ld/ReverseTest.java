package ld;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.basic.domain.BaseDataDict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class ReverseTest {

    @Autowired
    private ReverseSendService reverseSendService;

    @Test
    public void ECLPRejectReason(){

        Task tTask = new Task();

        String sendCode = "910-71639-20180718201718305";
        tTask.setBoxCode(sendCode);
        tTask.setBody(sendCode);
        tTask.setCreateSiteCode(910);
        tTask.setKeyword2("20");
        tTask.setReceiveSiteCode(71639);
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        tTask.setOwnSign("DMS");
        tTask.setKeyword1("4");// 1 回传运单状态
        tTask.setFingerprint(sendCode + "_" + tTask.getKeyword1());
        try {
            reverseSendService.findSendwaybillMessage(tTask);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Test
    public void testBase(){
        BaseDataDict refuseReason = baseMajorManager.getValidBaseDataDictListToMap(
                13,2,13).get(13);
        System.out.println(refuseReason.getTypeName());

    }
}
