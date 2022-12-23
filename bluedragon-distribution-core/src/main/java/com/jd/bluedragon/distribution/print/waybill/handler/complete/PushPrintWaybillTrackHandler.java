package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author wyh
 * @className PushPrintWaybillTrackHandler
 * @description
 * @date 2021/12/4 19:34
 **/
@Service("pushPrintWaybillTrackHandler")
public class PushPrintWaybillTrackHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PushPrintWaybillTrackHandler.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private WaybillService waybillService;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        if (CollectionUtils.isNotEmpty(context.getFirstPrintPackages())) {
            for (String packageCode : context.getFirstPrintPackages()) {

                // 发送首次打印的全程跟踪
                pushWaybillPrintTrack(context.getRequest(), packageCode, WaybillStatus.WAYBILL_TRACK_PACKAGE_PRINT);
            }
        }

        if (CollectionUtils.isNotEmpty(context.getReprintPackages())) {
            if (logger.isInfoEnabled()) {
                logger.info("非首次打印的包裹数据. code:{}", context.getReprintPackages());
            }
            for (String packageCode : context.getReprintPackages()) {

                // 发送补打类型的全程跟踪
                pushWaybillPrintTrack(context.getRequest(), packageCode, WaybillStatus.WAYBILL_TRACK_MSGTYPE_PACK_REPRINT);
            }
        }

        return context.getResult();
    }

    /**
     * 发送打印全程跟踪
     * @param printData
     * @param packageCode
     * @param waybillOperateType
     */
    private void pushWaybillPrintTrack(PrintCompleteRequest printData, String packageCode, Integer waybillOperateType){
        WaybillStatus waybillStatus = new WaybillStatus();

        waybillStatus.setCreateSiteCode(printData.getOperateSiteCode());
        waybillStatus.setCreateSiteName(printData.getOperateSiteName());
        waybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        waybillStatus.setPackageCode(packageCode);
        if (StringUtils.isNotBlank(printData.getOperateTime())) {
            waybillStatus.setOperateTime(DateHelper.parseDate(printData.getOperateTime(), Constants.DATE_TIME_FORMAT));
        }
        else {
            waybillStatus.setOperateTime(new Date());
        }
        waybillStatus.setOperatorId(printData.getOperatorCode());
        waybillStatus.setOperator(printData.getOperatorName());

        waybillStatus.setOperateType(waybillOperateType);
        // 快运改址拦截打印处理
        kyAddressModifyReprintDeal(printData, waybillStatus);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());

        taskService.add(task);
    }

    private void kyAddressModifyReprintDeal(PrintCompleteRequest printData, WaybillStatus waybillStatus) {
        if(BusinessUtil.isKyAddressModifyWaybill(printData.getWaybillSign())){
            BlockResponse blockResponse;
            if(WaybillUtil.isPackageCode(printData.getPackageBarcode())){
                blockResponse = waybillService.checkPackageBlock(printData.getPackageBarcode(),
                        CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT);
            }else {
                blockResponse = waybillService.checkWaybillBlock(WaybillUtil.getWaybillCode(printData.getWaybillCode()),
                        CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT);
            }
            if(Objects.equals(blockResponse.getCode(), BlockResponse.BLOCK)){
                // 快运改址打印：reprintType = 1
                Map<String, Object> extendParamMap = Maps.newHashMap();
                extendParamMap.put("reprintType", 1);
                waybillStatus.setExtendParamMap(extendParamMap);
            }
        }
    }
}
