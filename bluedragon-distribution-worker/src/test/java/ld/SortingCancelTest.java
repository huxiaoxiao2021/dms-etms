package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BusinessHelper;
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
public class SortingCancelTest {

    @Autowired
    private WaybillService waybillService;


    @Test
    public void test(){

        String body = "{\n" +
                "  \"boxCode\" : \"BC1001190514190000000101\",\n" +
                "  \"waybillCode\" : \"JDVA00003568470\",\n" +
                "  \"packageCode\" : \"JDVA00003568470-2-2-\",\n" +
                "  \"createSiteCode\" : 910,\n" +
                "  \"operatorId\" : 10053,\n" +
                "  \"operator\" : \"邢松\",\n" +
                "  \"operateType\" : 13400,\n" +
                "  \"operateTime\" : 1557825937511,\n" +
                "  \"remark\" : \"箱号：BC1001190514190000000101，取消建箱\"\n" +
                "}";

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(WaybillStatus.WAYBILL_TRACK_SORTING_CANCEL.toString());
        task.setCreateSiteCode(910);
        task.setBody(body);
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());

        waybillService.doWaybillTraceTask(task);
    }

}
