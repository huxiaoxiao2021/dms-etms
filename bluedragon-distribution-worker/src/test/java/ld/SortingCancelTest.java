package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StorageSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
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

    @Test
    public void putawayWaybillTraceTest() {
        PutawayDTO putawayDTO = new PutawayDTO();
        putawayDTO.setStorageSource(StorageSourceEnum.QPC_STORAGE.getCode());
        putawayDTO.setCreateSiteCode(910);
        putawayDTO.setOperateTime(new Date().getTime());
        putawayDTO.setBarCode("JDVA00119247227-3-5-");
        putawayDTO.setOperatorErp("bjxings");
        boolean isPutAway = true;
        Task tTask = new Task();
        tTask.setKeyword1(putawayDTO.getBarCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_STORAGE_KYZC));
        tTask.setCreateSiteCode(putawayDTO.getCreateSiteCode());
        tTask.setCreateTime(new Date(putawayDTO.getOperateTime()));
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setWaybillCode(WaybillUtil.getWaybillCode(putawayDTO.getBarCode()));
        status.setPackageCode(putawayDTO.getBarCode());
        status.setOperateTime(new Date(putawayDTO.getOperateTime()));
        status.setOperator(putawayDTO.getOperatorErp());
        boolean qpcWaybill = null != putawayDTO.getStorageSource() && StorageSourceEnum.QPC_STORAGE.getCode().equals(putawayDTO.getStorageSource());
        if(isPutAway){
            status.setRemark("分拣中心上架");
            status.setOperateType(WaybillStatus.WAYBILL_STATUS_PUTAWAY_STORAGE_KYZC);
            if (qpcWaybill) {
                status.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
            }
        }else {
            status.setRemark("分拣中心下架");
            status.setOperateType(WaybillStatus.WAYBILL_STATUS_DOWNAWAY_STORAGE_KYZC);

            // 企配仓暂存下架
            if (qpcWaybill) {
                status.setOperateType(WaybillStatus.WAYBILL_INTERNAL_TRACK_OFF_SHELF);
            }
        }
        // 企配仓订单上架发全程跟踪，不更新运单状态
        if (isPutAway && qpcWaybill) {
            tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY));
            status.setRemark("分拣中心上架");
            status.setOperateType(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY);
        }
        status.setCreateSiteCode(putawayDTO.getCreateSiteCode());
        tTask.setBody(JsonHelper.toJson(status));

        waybillService.doWaybillTraceTask(tTask);
    }

}
