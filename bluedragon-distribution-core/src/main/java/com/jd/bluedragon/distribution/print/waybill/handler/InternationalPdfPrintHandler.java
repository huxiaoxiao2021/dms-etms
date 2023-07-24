package com.jd.bluedragon.distribution.print.waybill.handler;

import com.google.common.collect.Lists;
import com.jd.bluedragon.CloudPrintConstants;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillExtVO;
import com.jd.bluedragon.core.base.InternationalCloudPrintManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jdl.print.dto.render.OutputConfigDTO;
import com.jdl.print.dto.render.RenderResultDTO;
import com.jdl.print.dto.render.ReprintDataDTO;
import com.jdl.print.dto.render.ReprintQueryRenderDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 国际化PDF打印处理链
 *
 * @author hujiping
 * @date 2023/7/18 9:12 PM
 */
@Slf4j
@Service
public class InternationalPdfPrintHandler implements InterceptHandler<WaybillPrintContext,String> {

    @Autowired
    private InternationalCloudPrintManager internationalCloudPrintManager;
    
    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        WaybillPrintRequest request = context.getRequest();
        // 目前只处理包裹补打、换单打印
        if(!Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType()) 
                && !Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType())){
            return interceptResult;
        }

        // 目前只处理港澳类运单
        if(!checkIsGA(context)){
            log.warn("单号:{}为非港澳订单", request.getPackageBarCode());
            return interceptResult;
        }
        
        // 判断是否需要打印
        if(checkIsNeedPrintAndFillPack(context)){
            interceptResult.toFail("包裹已打印!");
            return interceptResult;
        }
        
        // 调用云打印获取pdf链接
        String pdfUrl = generateCloudPrintPdfUrl(request);
        if(StringUtils.isEmpty(pdfUrl)){
            interceptResult.toFail("单号:" + request.getPackageBarCode() + "调用云打印失败，请联系分拣小秘!");
            return interceptResult;
        }
        context.getBasePrintWaybill().setLabelFileDownloadUrl(pdfUrl);
        
        // 推送咚咚-pdf链接
//        noticeToDD(context);
        
        interceptResult.toBreak(WaybillPrintMessages.CODE_INTERNATIONAL_SUC, "云打印成功");
        return interceptResult;
    }

    private boolean checkIsNeedPrintAndFillPack(WaybillPrintContext context) {
        WaybillPrintRequest request = context.getRequest();
        if(Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType())){
            // 换单打印需判断是否已操作过打印
            Optional<String> optional = context.getResponse().getPackList()
                    .stream().filter(item -> !item.getIsPrintPack())
                    .map(PrintPackage::getPackageCode)
                    .findFirst();
            // 当前需打印包裹号
            String packageCode = optional.orElse(null);
            // fill request packageCode
            request.setPackageBarCode(packageCode);
            return StringUtils.isEmpty(packageCode);
        }
        return false;
    }
    
    private boolean checkIsGA(WaybillPrintContext context) {
        WaybillExtVO waybillExtVO = context.getWaybill().getWaybillExtVO();
        String startFlowDirection = waybillExtVO == null ? null : waybillExtVO.getStartFlowDirection();
        String endFlowDirection = waybillExtVO == null ? null : waybillExtVO.getEndFlowDirection();
        if(Objects.equals(context.getRequest().getOperateType(), WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType())){
            // 换单打印需判断老单是否为港澳单
            if(context.getOldBigWaybillDto() != null && context.getOldBigWaybillDto().getWaybill() != null){
                WaybillExt oldWaybillExt = context.getOldBigWaybillDto().getWaybill().getWaybillExt();
                startFlowDirection = oldWaybillExt == null ? null : oldWaybillExt.getStartFlowDirection();
                endFlowDirection = oldWaybillExt == null ? null : oldWaybillExt.getEndFlowDirection();
            }else {
                log.warn("根据新单号:{}未获取老单运单信息", context.getRequest().getPackageBarCode());
            }
        }
        return BusinessUtil.isGAWaybill(startFlowDirection, endFlowDirection);
    }

    private String generateCloudPrintPdfUrl(WaybillPrintRequest request) {

        ReprintQueryRenderDTO renderQuery = new ReprintQueryRenderDTO();
        renderQuery.setRequestId(request.getBarCode().concat(Constants.SEPARATOR_HYPHEN).concat(String.valueOf(System.currentTimeMillis())));
        renderQuery.setOperator(request.getUserERP());
        renderQuery.setOperatorSiteId(request.getSiteCode());
        // 打印方式
        renderQuery.setOperateType(Objects.equals(request.getOperateType(), WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType())
                ? CloudPrintConstants.PRINT_TYPE_REPRINT : CloudPrintConstants.PRINT_TYPE_EXCHANGE);
        // 模版尺寸
        renderQuery.setTemplateSize(Objects.equals(request.getPaperSizeCode(), DmsPaperSize.PAPER_SIZE_CODE_1005)
                ? CloudPrintConstants.TEMPLATE_SIZE_1005 : CloudPrintConstants.TEMPLATE_SIZE_1010);
        // 需打印数据
        ReprintDataDTO reprintDataDTO = new ReprintDataDTO();
        // 打印单号
        String printBarCode = StringUtils.isEmpty(request.getPackageBarCode()) ? request.getBarCode() : request.getPackageBarCode();
        reprintDataDTO.setBillCodeType(WaybillUtil.isWaybillCode(printBarCode) 
                ? CloudPrintConstants.BILL_CODE_TYPE_WAYBILL : CloudPrintConstants.BILL_CODE_TYPE_PACK);
        reprintDataDTO.setBillCodeValue(printBarCode);
        reprintDataDTO.setOrderNumber(printBarCode);
        renderQuery.setPrintData(Lists.newArrayList(reprintDataDTO));
        // 输出方式
        OutputConfigDTO outputConfigDTO = new OutputConfigDTO();
        outputConfigDTO.setOutputType(CloudPrintConstants.OUTPUT_TYPE_FILE);
        outputConfigDTO.setFileFormat(CloudPrintConstants.FILE_FORMAT_PDF);
        outputConfigDTO.setDataFormat(CloudPrintConstants.DATA_FORMAT_URL);
        renderQuery.setOutputConfig(Lists.newArrayList(outputConfigDTO));
        // 打印结果
        List<RenderResultDTO> renderResultDTOS = internationalCloudPrintManager.internationalCloudPrint(renderQuery);
        if(CollectionUtils.isNotEmpty(renderResultDTOS)){
            return renderResultDTOS.get(0).getUrl();
        }
        return null;
    }

    private void noticeToDD(WaybillPrintContext context) {
        String title = "港澳打印提示";
        String content = String.format("港澳单号:%s的打印链接已生成，请在网页端输入链接打印!", StringUtils.isEmpty(context.getRequest().getPackageBarCode()) 
                ? context.getRequest().getBarCode() : context.getRequest().getPackageBarCode());
        NoticeUtils.noticeToTimeline(title, content, context.getBasePrintWaybill().getLabelFileDownloadUrl(), context.getRequest().getUserERP());
    }
}
