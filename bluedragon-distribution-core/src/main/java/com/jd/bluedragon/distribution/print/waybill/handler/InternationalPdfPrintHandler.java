package com.jd.bluedragon.distribution.print.waybill.handler;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.InternationalPrintManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.domain.international.InternationalPrintReq;
import com.jd.bluedragon.distribution.print.domain.international.OutputConfigDTO;
import com.jd.bluedragon.utils.NoticeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 国际化PDF打印处理链
 *
 * @author hujiping
 * @date 2023/7/18 9:12 PM
 */
@Service
public class InternationalPdfPrintHandler implements InterceptHandler<WaybillPrintContext,String> {

    @Autowired
    private InternationalPrintManager internationalPrintManager;
    
    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        interceptResult.toBreak(WaybillPrintMessages.CODE_INTERNATIONAL_SUC, "云打印成功");
        // 目前只处理港澳类运单
        if(1!=1){
            return interceptResult;
        }
        WaybillPrintRequest request = context.getRequest();
        // 目前只处理包裹补打、换单打印
        if(!Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType()) 
                && !Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType())){
            return interceptResult;
        }
        InternationalPrintReq cloudPrintReq = new InternationalPrintReq();
        cloudPrintReq.setRequestId(request.getBarCode().concat(Constants.SEPARATOR_VERTICAL_LINE).concat(String.valueOf(System.currentTimeMillis())));
        cloudPrintReq.setOperator(request.getUserERP());
        cloudPrintReq.setOperatorSiteId(String.valueOf(request.getSiteCode()));
        cloudPrintReq.setOperateType(Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType()) 
                ? 2 : 1);
        cloudPrintReq.setTemplateSize(Objects.equals(request.getPaperSizeCode(), DmsPaperSize.PAPER_SIZE_CODE_1005) 
                ? "100x50" : "100x100");
        cloudPrintReq.setOutputConfig(Lists.newArrayList(new OutputConfigDTO().outputType(1).fileFormat(1).dataFormat(1)));
        // 获取pdf地址
        String pdfUrl = internationalPrintManager.generatePdfUrl(cloudPrintReq);
        if(StringUtils.isEmpty(pdfUrl)){
            interceptResult.toFail("单号:" + request.getPackageBarCode() + "调用云打印失败，请联系分拣小秘!");
        }
        context.getBasePrintWaybill().setLabelFileDownloadUrl(pdfUrl);
        // 推送咚咚-pdf链接
        noticeToDD(context);
        return interceptResult;
    }

    private void noticeToDD(WaybillPrintContext context) {
        String title = "港澳打印提示";
        String content = String.format("港澳单号:%s的打印链接已生成，请在网页端输入链接打印!", StringUtils.isEmpty(context.getRequest().getPackageBarCode()) 
                ? context.getRequest().getBarCode() : context.getRequest().getPackageBarCode());
        NoticeUtils.noticeToTimeline(title, content, context.getBasePrintWaybill().getLabelFileDownloadUrl(), context.getRequest().getUserERP());
    }
}
