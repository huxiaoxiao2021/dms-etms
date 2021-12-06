package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @className DealReprintHandler
 * @description
 * @date 2021/12/2 18:08
 **/
@Service("dealReprintHandler")
public class DealReprintHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    @Autowired
    private ReprintRecordService reprintRecordService;

    @Autowired
    private TaskService taskService;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        PrintCompleteRequest request = context.getRequest();

        // 非首次打印的包裹，写补打记录
        this.saveReprintRecord(request, request.getPackageBarcode());

        for (String packageCode : context.getToDealPackageCodes()) {

            // 发送补打类型的全程跟踪
            this.pushReprintWaybillTrack(packageCode, request);
        }

        return context.getResult();
    }

    /**
     * 发送打印全程跟踪
     * @param packageCode
     * @param printData
     */
    private void pushReprintWaybillTrack(String packageCode, PrintCompleteRequest printData){
        WaybillStatus waybillStatus = new WaybillStatus();

        waybillStatus.setCreateSiteCode(printData.getOperateSiteCode());
        waybillStatus.setCreateSiteName(printData.getOperateSiteName());
        waybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        waybillStatus.setPackageCode(packageCode);
        waybillStatus.setOperateTime(DateHelper.parseDate(printData.getOperateTime(), Constants.DATE_TIME_FORMAT));
        waybillStatus.setOperatorId(printData.getOperatorCode());
        waybillStatus.setOperator(printData.getOperatorName());

        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_MSGTYPE_PACK_REPRINT);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());

        taskService.add(task);
    }

    private void saveReprintRecord(PrintCompleteRequest request, String barCode) {
        ReprintRecord rePrintRecord = new ReprintRecord();
        rePrintRecord.setBarCode(barCode);
        rePrintRecord.setSiteCode(request.getOperateSiteCode());
        rePrintRecord.setSiteName(request.getOperateSiteName());
        rePrintRecord.setOperatorCode(request.getOperatorCode());
        rePrintRecord.setOperatorName(request.getOperatorName());
        reprintRecordService.insertRePrintRecord(rePrintRecord);
    }
}
