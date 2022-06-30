package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyh
 * @className DealPopPrintHandler
 * @description
 * @date 2021/12/2 17:47
 **/
@Service("dealPopPrintHandler")
public class DealPopPrintHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(DealPopPrintHandler.class);

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private ReprintRecordService reprintRecordService;

    private static final List<Integer> EXCLUDE_INSPECTION_OPERATE_TYPE = new ArrayList<>();

    private static final List<Integer> FIRST_PRINT_OPERATE_TYPE = new ArrayList<>();

    static {
        // 平台打印和驻场打印单独插入1180任务，发了验货全程跟踪，此处先去掉
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.FIELD_PRINT.getType());

        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SMS_REPRINT.getType());
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SITE_3PL_PACKAGE_AGAIN_REPRINT.getType());
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SMS_PDA_REPRINT.getType());
        EXCLUDE_INSPECTION_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SITE_HSD_PACKAGE_PRINT.getType());

        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());
        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());
        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.FIELD_PRINT.getType());
    }

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        InterceptResult<Boolean> result = context.getResult();
        if (CollectionUtils.isEmpty(context.getToDealPackageCodes())) {
            return result;
        }

        try {
            List<String> nonFirstPrintCodes = new ArrayList<>();
            for (String packageCode : context.getToDealPackageCodes()) {

                PopPrint popPrint = requestToPopPrint(context.getRequest(), packageCode);

                // 首次打印的包裹，打印记录保存到popPrint
                if (judgePackageFirstPrint(popPrint, context)) {

                    popPrintService.add(popPrint);

                    // 验货任务
                    pushInspectionTrack(context, popPrint);

                    context.addToFirstPrintList(packageCode);
                }
                else {
                    nonFirstPrintCodes.add(packageCode);
                }
            }

            // 非首次打印的包裹，写补打记录
            if (CollectionUtils.isNotEmpty(nonFirstPrintCodes)) {
                context.setReprintPackages(nonFirstPrintCodes);

                saveReprintRecord(context.getRequest());
            }
        }
        catch (Exception e) {
            logger.error("DealPopPrintHandler error. request:{}", JsonHelper.toJson(context.getRequest()), e);
            result.toError();
        }

        return result;
    }

    /**
     * 推送验货任务
     * @param context
     * @param popPrint
     */
    private void pushInspectionTrack(WaybillPrintCompleteContext context, PopPrint popPrint) {
        if (satisfyPushInspectionTrack(context, popPrint)) {
            // 推补验货任务
            popPrintService.pushInspection(popPrint);
        }
    }

    /**
     * 推送验货全程跟踪的条件
     * @param context
     * @param popPrint
     * @return
     */
    private boolean satisfyPushInspectionTrack(WaybillPrintCompleteContext context, PopPrint popPrint) {
        return !EXCLUDE_INSPECTION_OPERATE_TYPE.contains(context.getOperateType())
                && PopPrintRequest.PRINT_PACK_TYPE.equals(popPrint.getOperateType());
    }

    /**
     * 判断包裹是否是首次打印
     * @param popPrint
     * @param context
     * @return
     */
    private boolean judgePackageFirstPrint(PopPrint popPrint, WaybillPrintCompleteContext context) {

        // 去掉打印客户端固定写首打的逻辑
//        if (FIRST_PRINT_OPERATE_TYPE.contains(context.getOperateType())) {
//            return true;
//        }

        // 设置是否首次打印标识，根据入参的值判定
        PrintCompleteRequest request = context.getRequest();
        if (null != request.getFirstTimePrint()) {
            return request.getFirstTimePrint() == 1;
        }
        else {
            return popPrintService.judgePackageFirstPrint(popPrint.getPackageBarcode());
        }
    }

    private void saveReprintRecord(PrintCompleteRequest request) {
        ReprintRecord rePrintRecord = new ReprintRecord();
        rePrintRecord.setBarCode(request.getPackageBarcode());
        rePrintRecord.setSiteCode(request.getOperateSiteCode());
        rePrintRecord.setSiteName(request.getOperateSiteName());
        rePrintRecord.setOperatorCode(request.getOperatorCode());
        rePrintRecord.setOperatorName(request.getOperatorName());
        reprintRecordService.insertRePrintRecord(rePrintRecord);
    }

    private PopPrint requestToPopPrint(PrintCompleteRequest request, String packageCode) {
        PopPrint popPrint = new PopPrint();
        popPrint.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        if (StringUtils.isBlank(packageCode)) {
            popPrint.setPackageBarcode(request.getWaybillCode());
        } else {
            popPrint.setPackageBarcode(packageCode);
        }
        popPrint.setCreateSiteCode(request.getOperateSiteCode());
        popPrint.setCreateSiteName(request.getOperateSiteName());
        popPrint.setCreateUserCode(request.getOperatorCode());
        popPrint.setCreateUser(request.getOperatorName());
        popPrint.setPopSupId(request.getPopSupId());
        popPrint.setPopSupName(request.getPopSupName());
        popPrint.setQuantity(request.getQuantity());
        popPrint.setWaybillType(request.getWaybillType());
        popPrint.setCrossCode(request.getCrossCode());
        popPrint.setPopReceiveType(request.getPopReceiveType());
        popPrint.setThirdWaybillCode(request.getThirdWaybillCode());
        popPrint.setQueueNo(request.getQueueNo());
        popPrint.setOperateType(request.getOperateType());
        popPrint.setBoxCode(request.getBoxCode());
        popPrint.setDriverCode(request.getDriverCode());
        popPrint.setDriverName(request.getDriverName());

        popPrint.setBusiId(request.getBusiId());
        popPrint.setBusiName(request.getBusiName());
        popPrint.setInterfaceType(request.getInterfaceType());

        popPrint.setCategoryName(request.getCategoryName());

        if (PopPrintRequest.PRINT_PACK_TYPE.equals(request.getOperateType())
                || PopPrintRequest.NOT_PRINT_PACK_TYPE.equals(request.getOperateType())) {
            popPrint.setPrintPackCode(request.getOperatorCode());
            popPrint.setPrintPackTime(DateHelper.getSeverTime(request.getOperateTime()));
            popPrint.setPrintPackUser(request.getOperatorName());
        }
        else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
            logger.info("打印发票. {}", JsonHelper.toJson(request));
            popPrint.setPrintInvoiceCode(request.getOperatorCode());
            popPrint.setPrintInvoiceTime(DateHelper.getSeverTime(request.getOperateTime()));
            popPrint.setPrintInvoiceUser(request.getOperatorName());
        }
        else {
            throw new RuntimeException("保存POP打印信息 --> 传入操作类型有误：" + request.getOperateType() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
        }

        boolean isPrintPack = PopPrintRequest.PRINT_PACK_TYPE.equals(request.getOperateType());
        if (isPrintPack) {
            popPrint.setPrintCount(1);
        }

        popPrint.setSortingFirstPrint(request.getSortingFirstPrint());

        return popPrint;
    }
}
