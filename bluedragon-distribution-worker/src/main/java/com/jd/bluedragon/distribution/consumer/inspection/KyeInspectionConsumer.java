package com.jd.bluedragon.distribution.consumer.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.domain.KyeInspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION;

/**
 * 跨越自动化验货
 * 如果是箱号 则收箱
 */
@Service("kyeInspectionConsumer")
public class KyeInspectionConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(InspectionPackageConsumer.class);
    @Autowired
    InspectionService inspectionService;

    @Autowired
    SendDetailService sendDetailService;
    @Override
    public void consume(Message message) throws Exception {
        try {
            final KyeInspection kyeInspection = JsonHelper.fromJsonMs(message.getText(), KyeInspection.class);
            final String umpKey = "DmsWorker.jmq.consumer.KyeInspectionConsumer.consume";

            UmpMonitorHelper.doWithUmpMonitor(umpKey, Constants.UMP_APP_NAME_DMSWORKER, new UmpMonitorHandler() {
                @Override
                public void process() {

                    inspectionIfNotSend(kyeInspection);

                }
            });
        }catch (Exception e){

        }

    }

    private void inspectionIfNotSend(KyeInspection kyeInspection){
        String barcode = kyeInspection.getBarcode();
        if(!WaybillUtil.isPackageCode(barcode) && !BusinessUtil.isBoxcode(barcode)){
            logger.warn("跨越验货条码非包裹号和箱号忽略:{}", barcode);
            return;
        }
        //已发货 说明本场地已操作，忽略
        if(hasSend(barcode, kyeInspection.getCreateSiteCode())){
            logger.warn("跨越验货场地已发货忽略验货:{}场地id:{}", barcode, kyeInspection.getCreateSiteCode());
            return;
        }
        InspectionVO vo = new InspectionVO();
        List<String> barcodes = new ArrayList<>();
        barcodes.add(barcode);
        vo.setBarCodes(barcodes);
        vo.setOperateTime(DateHelper.formatDateTime(kyeInspection.getCreateTime()));
        vo.setSiteCode(kyeInspection.getCreateSiteCode());
        vo.setSiteName(kyeInspection.getCreateSiteName());
        vo.setUserCode(Integer.parseInt(kyeInspection.getOperatorId()));
        vo.setUserName(kyeInspection.getOperatorName());
        inspectionService.inspection(vo, AUTOMATIC_SORTING_MACHINE_INSPECTION);
    }

    private boolean hasSend(String barcode, Integer createSiteCode){
        SendDetail param = new SendDetail();

        if(WaybillUtil.isPackageCode(barcode)){
            param.setPackageBarcode(barcode);
        }else{
            param.setBoxCode(barcode);
        }
        param.setCreateSiteCode(createSiteCode);
        //未取消
        param.setIsCancel(0);
        //已发货
        param.setStatus(1);
        SendDetail sendDetail = sendDetailService.queryOneSendDatailBySendM(param);
        return sendDetail != null;
    }
}
