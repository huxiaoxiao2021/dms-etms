package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.popPrint.dto.PushPrintRecordDto;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wyh
 * @className PushPrintRecordHandler
 * @description
 * @date 2021/12/2 19:41
 **/
@Service("pushPrintRecordHandler")
public class PushPrintRecordHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PushPrintRecordHandler.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("packagePrintRecordProducer")
    private DefaultJMQProducer packagePrintRecordProducer;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        pushPrintRecordMq(context);

        return context.getResult();
    }

    /**
     * 发送打印消息
     * @param context
     */
    private void pushPrintRecordMq(WaybillPrintCompleteContext context) {

        PrintCompleteRequest request = context.getRequest();

        PushPrintRecordDto printRecordDto = new PushPrintRecordDto();
        printRecordDto.setBusinessType(context.getBusinessType());
        printRecordDto.setOperateType(context.getOperateType());
        printRecordDto.setPrintType(0); // TODO 设置打印类型
        printRecordDto.setWaybillCode(request.getWaybillCode());
        printRecordDto.setPackageCode(request.getPackageBarcode());
        printRecordDto.setSiteCode(request.getOperateSiteCode());
        printRecordDto.setOperatorName(request.getOperatorName());

        if (StringUtils.isNotBlank(request.getOperateTime())) {
            Date opeTime = DateHelper.parseDate(request.getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2);
            printRecordDto.setOpeTime(opeTime == null ? System.currentTimeMillis() : opeTime.getTime());
        }
        else {
            printRecordDto.setOpeTime(System.currentTimeMillis());
        }

        if (NumberHelper.isPositiveNumber(request.getOperatorCode())) {
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(request.getOperatorCode());
            if (baseStaff != null) {
                printRecordDto.setOperatorErp(baseStaff.getErp());
            }
            else {
                printRecordDto.setOperatorErp(String.valueOf(request.getOperatorCode()));
            }
        }

        packagePrintRecordProducer.sendOnFailPersistent(printRecordDto.getPackageCode(), JsonHelper.toJson(printRecordDto));
    }
}
