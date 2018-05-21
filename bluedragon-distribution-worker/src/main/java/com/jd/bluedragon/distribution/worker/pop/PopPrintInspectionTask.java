package com.jd.bluedragon.distribution.worker.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年05月21日 21时:05分
 */
public class PopPrintInspectionTask extends DBSingleScheduler {
    private final Log logger = LogFactory.getLog(this.getClass());


    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private TaskPopRecieveCountService taskPopRecieveCountService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        String body = task.getBody().substring(1, task.getBody().length() - 1);
        PopPrint popPrint = JsonHelper.fromJson(body, PopPrint.class);
        if (!BusinessHelper.isWaybillCode(popPrint.getWaybillCode())) {
            logger.info("平台订单已打印未收货处理 --> 打印单号【" + popPrint.getPopPrintId()
                    + "】，运单号【" + popPrint.getWaybillCode()
                    + "】， 操作人SiteCode【" + popPrint.getCreateSiteCode()
                    + "】，为非平台订单");
            return true;
        }

        if (Waybill.isPopWaybillType(popPrint.getWaybillType())
                && !Constants.POP_QUEUE_EXPRESS.equals(popPrint
                .getPopReceiveType())) {
            try {
                this.taskPopRecieveCountService
                        .insert(popPrintToInspection(popPrint));
                this.logger.info("平台订单已打印未收货处理 --> 分拣中心-运单【"
                        + popPrint.getCreateSiteCode() + "-"
                        + popPrint.getWaybillCode() + "】收货补全回传POP成功");
            } catch (Exception e) {
                this.logger
                        .error("平台订单已打印未收货处理 --> 分拣中心-运单【"
                                + popPrint.getCreateSiteCode() + "-"
                                + popPrint.getWaybillCode()
                                + "】 收货补全回传POP，补全异常", e);
            }
        }
        try {
            this.inspectionService
                    .addInspectionPop(popPrintToInspection(popPrint));
            this.logger.info("平台订单已打印未收货处理 --> 分拣中心-运单【"
                    + popPrint.getCreateSiteCode() + "-"
                    + popPrint.getWaybillCode() + "】 收货信息不存在，补全成功");
        } catch (Exception e) {
            this.logger.error("平台订单已打印未收货处理 --> 分拣中心-运单【"
                    + popPrint.getCreateSiteCode() + "-"
                    + popPrint.getWaybillCode() + "】 收货信息不存在，补全异常", e);
        }
        return true;
    }

    public Inspection popPrintToInspection(PopPrint popPrint) {
        try {
            Inspection inspection = new Inspection();
            inspection.setWaybillCode(popPrint.getWaybillCode());
            if (Constants.POP_QUEUE_SITE.equals(popPrint.getPopReceiveType())) {
                inspection.setInspectionType(Constants.BUSSINESS_TYPE_SITE);
            } else {
                inspection.setInspectionType(Constants.BUSSINESS_TYPE_POP);
            }
            inspection.setCreateUserCode(popPrint.getCreateUserCode());
            inspection.setCreateUser(popPrint.getCreateUser());
            inspection
                    .setCreateTime((popPrint.getPrintPackTime() == null) ? popPrint
                            .getPrintInvoiceTime()
                            : popPrint.getPrintPackTime());
            inspection.setCreateSiteCode(popPrint.getCreateSiteCode());

            inspection.setUpdateTime(inspection.getCreateTime());
            inspection.setUpdateUser(inspection.getCreateUser());
            inspection.setUpdateUserCode(inspection.getCreateUserCode());

            inspection.setPopSupId(popPrint.getPopSupId());
            inspection.setPopSupName(popPrint.getPopSupName());
            inspection.setQuantity(popPrint.getQuantity());
            inspection.setCrossCode(popPrint.getCrossCode());
            inspection.setWaybillType(popPrint.getWaybillType());
            inspection.setPopReceiveType(popPrint.getPopReceiveType());
            inspection.setPopFlag(1);
            inspection.setThirdWaybillCode(popPrint.getThirdWaybillCode());
            inspection.setQueueNo(popPrint.getQueueNo());

            inspection
                    .setPackageBarcode((popPrint.getPackageBarcode() == null) ? popPrint
                            .getWaybillCode()
                            : popPrint.getPackageBarcode());
            inspection.setBoxCode(popPrint.getBoxCode());
            inspection.setDriverCode(popPrint.getDriverCode());
            inspection.setDriverName(popPrint.getDriverName());
            inspection.setBusiId(popPrint.getBusiId());
            inspection.setBusiName(popPrint.getBusiName());
            inspection.setOperateTime(popPrint.getPrintPackTime());
            if (null == inspection.getOperateTime()) {
                inspection.setOperateTime(popPrint.getCreateTime());
            }
            if (Waybill.isPopWaybillType(inspection.getWaybillType())) {
                inspection.setBusiId(popPrint.getPopSupId());
                inspection.setBusiName(popPrint.getPopSupName());
            }

            return inspection;
        } catch (Exception e) {
            logger.error("平台订单已打印未收货处理 --> 转换打印信息异常：", e);
            return null;
        }
    }

}
