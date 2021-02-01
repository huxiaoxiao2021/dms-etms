package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.schedule.service.impl.DmsScheduleInfoServiceImpl;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jp.print.templet.center.sdk.common.SdkCommonResult;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchPdfDto;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchRequest;
import com.jd.jp.print.templet.center.sdk.service.KaGenerateEdnDeliveryReceiptPdfService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("dmsScheduleInfoServiceManager")
public class DmsScheduleInfoServiceManagerImpl implements DmsScheduleInfoServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(DmsScheduleInfoServiceManagerImpl.class);

    @Resource
    private KaGenerateEdnDeliveryReceiptPdfService kaGenerateEdnDeliveryReceiptPdfService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "com.jd.bluedragon.core.base.DmsScheduleInfoServiceManagerImpl.generatePdfUrlByBatchList",mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public JdResponse<EdnDeliveryReceiptBatchPdfDto> generatePdfUrlByBatchList(EdnDeliveryReceiptBatchRequest param) {
        logger.warn("com.jd.bluedragon.core.base.DmsScheduleInfoServiceManagerImpl--》generatePdfUrlByBatchList  start ,param=[{}]", JsonHelper.toJson(param));
        JdResponse<EdnDeliveryReceiptBatchPdfDto> response = new JdResponse<>();
        try {
            SdkCommonResult<EdnDeliveryReceiptBatchPdfDto> result = kaGenerateEdnDeliveryReceiptPdfService.generatePdfUrlByBatchList(param);
            if (!result.isSuccess()){
                logger.warn("com.jd.bluedragon.core.base.DmsScheduleInfoServiceManagerImpl--》generatePdfUrlByBatchList ,param=[{}],msg=[{}]", JsonHelper.toJson(param),result.getMessage());
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage(result.getMessage());
                return response;
            }
            EdnDeliveryReceiptBatchPdfDto pdfDto = result.getDefaultModel();
            if(pdfDto==null|| StringUtils.isBlank(pdfDto.getPdfUrl())){
                logger.warn("com.jd.bluedragon.core.base.DmsScheduleInfoServiceManagerImpl--》generatePdfUrlByBatchList  pdfDto为null或 pdfUrl为空 ,param=[{}],pdfDto=[{}]", JsonHelper.toJson(param),JsonHelper.toJson(pdfDto));
                response.setCode(JdResponse.CODE_FAIL);
                response.setMessage("PDF url未生成");
                return response;
            }
            response.setData(result.getDefaultModel());
            return response;
        } catch (Exception e) {
            logger.error("com.jd.bluedragon.core.base.DmsScheduleInfoServiceManagerImpl--》generatePdfUrlByBatchList 远程调用失败 ,param=[{}],error=[{}]",JsonHelper.toJson(param),e.getMessage());
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage("错误信息:"+e.getMessage());
            return  response;
        }
    }
}
