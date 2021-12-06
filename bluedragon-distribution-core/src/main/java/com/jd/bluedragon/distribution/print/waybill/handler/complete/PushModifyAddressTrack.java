package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wyh
 * @className PushModifyAddressTrack
 * @description
 * @date 2021/12/4 15:29
 **/
@Service("pushModifyAddressTrack")
public class PushModifyAddressTrack implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PushModifyAddressTrack.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        // 修改客户地址补打,发送全程跟踪,用于在商城前台显示
        pushModifyAddressTask(context.getRequest());

        return context.getResult();
    }

    private void pushModifyAddressTask(PrintCompleteRequest request) {
        BaseStaffSiteOrgDto bDto = null;
        try{
            Integer siteType = 0;
            if (request.getOperateSiteCode() != null) {
                bDto = baseMajorManager.getBaseSiteBySiteId(request.getOperateSiteCode());
            }
            if (bDto != null) {
                siteType = bDto.getSiteType();
            }
            String waybillSign = request.getWaybillSign();
            if (siteType != 0 && StringHelper.isNotEmpty(waybillSign)) {
                //操作人所在机构是配送站并且waybillSign第八位是1或2或3的触发全程跟踪
                if (siteType == 4 && (BusinessUtil.isSignChar(waybillSign,8,'1' ) // 1 仅修改地址
                        || BusinessUtil.isSignChar(waybillSign,8,'2')             // 2 修改地址和其他
                        || BusinessUtil.isSignChar(waybillSign,8,'3')             // 3 未修改地址仅修改其他
                )) {

                    String barCode = request.getWaybillCode();
                    if (StringHelper.isNotEmpty(request.getPackageBarcode())) {
                        barCode = request.getPackageBarcode();
                    }

                    if (barCode != null && request.getOperatorName() != null && request.getOperatorCode() != null && !Integer.valueOf(-1).equals(request.getOperatorCode())) {
                        taskService.add(this.toAddressModTask(barCode, bDto.getSiteCode(), bDto.getSiteName(), request.getOperatorCode(), request.getOperatorName()));
                    }
                    else {
                        logger.info("修改客户地址包裹补打触发全程跟踪失败。参数信息不完整。包裹号{},操作人ID{},操作人名称{},操作站点ID{},操作站点名称{}",
                                barCode, request.getOperatorCode(), request.getOperatorName(), bDto.getSiteCode(), bDto.getSiteName());
                    }
                }
            }
        }
        catch (Exception e){
            logger.error("修改客户地址包裹补打触发全程跟踪失败", e);
        }
    }

    /**
     *
     * @param barCode
     * @param createSiteCode
     * @param createSiteName
     * @param operatorId
     * @param operateName
     * @return
     */
    private Task toAddressModTask(String barCode, Integer createSiteCode, String createSiteName, Integer operatorId, String operateName){
        WaybillStatus waybillStatus = new WaybillStatus();

        if (WaybillUtil.isPackageCode(barCode)) {
            waybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
            waybillStatus.setPackageCode(barCode);
        }
        else {
            waybillStatus.setWaybillCode(barCode);
        }

        waybillStatus.setOperateTime(new Date());
        waybillStatus.setCreateSiteCode(createSiteCode);
        waybillStatus.setCreateSiteName(createSiteName);
        waybillStatus.setOperatorId(operatorId);
        waybillStatus.setOperator(operateName);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WAYBILL_BD);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }
}
