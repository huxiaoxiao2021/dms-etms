package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.KyAddressModifyPrintCancelInterceptMQ;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.handler.AbstractHandler;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 打印取消拦截处理器
 *
 * @author hujiping
 * @date 2022/12/14 3:06 PM
 */
@Service("printCancelInterceptHandler")
public class PrintCancelInterceptHandler extends AbstractHandler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PrintCancelInterceptHandler.class);

    @Autowired
    @Qualifier("printCancelKyAddressModifyInterceptProducer")
    private DefaultJMQProducer printCancelKyAddressModifyInterceptProducer;
    @Autowired
    private WaybillService waybillService;
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        PrintCompleteRequest request = context.getRequest();
        if(this.isChangeAddressLegalWaybill(request.getWaybillCode(), request.getWaybillSign())){
            // 快运改址，发打印消息通知ver，ver消费来解除拦截状态
            KyAddressModifyPrintCancelInterceptMQ kyAddressModifyPrintCancelInterceptMQ = new KyAddressModifyPrintCancelInterceptMQ();
            kyAddressModifyPrintCancelInterceptMQ.setWaybillCode(request.getWaybillCode());
            if(WaybillUtil.isPackageCode(request.getPackageBarcode())){
                kyAddressModifyPrintCancelInterceptMQ.setPackageCode(request.getPackageBarcode());
            }
            Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
            kyAddressModifyPrintCancelInterceptMQ.setOperateTime(operateTime == null ? System.currentTimeMillis() : operateTime.getTime());
            kyAddressModifyPrintCancelInterceptMQ.setPrintType(context.getOperateType());
            kyAddressModifyPrintCancelInterceptMQ.setUserCode(request.getOperatorCode());
            kyAddressModifyPrintCancelInterceptMQ.setUserName(request.getOperatorName());
            kyAddressModifyPrintCancelInterceptMQ.setUserErp(request.getOperatorErp());
            kyAddressModifyPrintCancelInterceptMQ.setSiteCode(request.getOperateSiteCode());
            kyAddressModifyPrintCancelInterceptMQ.setSiteName(request.getOperateSiteName());
            printCancelKyAddressModifyInterceptProducer.sendOnFailPersistent(kyAddressModifyPrintCancelInterceptMQ.getWaybillCode(), JsonHelper.toJson(kyAddressModifyPrintCancelInterceptMQ));
            logger.info("单号:{}操作打印后取消快运改址拦截!", StringUtils.isEmpty(request.getPackageBarcode())
                    ? request.getWaybillCode() : request.getPackageBarcode());
        }
        return context.getResult();
    }

    /**
     * 是否改址合法运单，true:是  false:否
     * @param waybillCode
     * @param waybillSignStr
     * @return
     */
    public boolean isChangeAddressLegalWaybill(String waybillCode, String waybillSignStr) {
        if(StringUtils.isBlank(waybillCode)) {
            return false;
        }
        if (BusinessUtil.isKyAddressModifyWaybill(waybillSignStr)) {
            return true;
        }
        if(BusinessUtil.isMedicineCpModifyWaybill(waybillSignStr)) {
            return true;
        }
        if(this.isKdChangeAddressLegalWaybill(waybillCode, waybillSignStr)) {
            return true;
        }
        if(logger.isInfoEnabled()) {
            logger.info("不符合改址换单拦截逻辑waybillCode[%s],wbs=[%s]", waybillCode, waybillSignStr);
        }
        return false;
    }

    /**
     * 快递改址运单范围（快递+纯配外单+百川）
     * @param waybillCode
     * @param waybillSignStr
     * @return
     */
    public boolean isKdChangeAddressLegalWaybill(String waybillCode, String waybillSignStr) {
        if(StringUtils.isBlank(waybillCode)) {
            return false;
        }
        //快递单
        if(!BusinessUtil.isCInternet(waybillSignStr)){
            return false;
        }
        if(!BusinessUtil.isPurematch(waybillSignStr)) {
            return false;
        }
        com.jd.etms.waybill.domain.Waybill waybill = waybillService.getWaybillByWayCode(waybillCode);
        if(waybill == null){
            logger.warn(String.format("快递改址换单拦截处理-查询运单信息为空waybillCode[%s]", waybillCode));
            return false;
        }
        if(StringUtils.isBlank(waybillService.baiChuanEnableSwitch(waybill))) {
            return false;
        }
        logger.info(String.format("该单【%s】为快递纯配外单百川单的改址运单", waybillCode));
        return true;
    }

}
