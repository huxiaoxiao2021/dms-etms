package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.spare.dao.SpareSortingRecordDao;
import com.jd.bluedragon.distribution.spare.domain.SpareSortingRecord;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class SpareSendTaskTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReverseSpareService reverseSpareService;

    @Autowired
    private SpareSortingRecordDao spareSortingRecordDao;

    @Autowired
    private ReverseSendService reverseSendService;

    @Test
    public void test(){

        Task tTask = new Task();

        String sendCode = "910-25016-20190418141944010";
        tTask.setBoxCode(sendCode);
        tTask.setBody(sendCode);
        tTask.setCreateSiteCode(910);
        tTask.setKeyword2("20");
        tTask.setReceiveSiteCode(25016);
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

}
