package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exception.service.SendCodeExceptionHandlerService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ExportExcelDownFee;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.SendCodeSummaryResponse;
import com.jd.uim.annotation.Authorization;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/19
 */
@Controller
@RequestMapping("/exception")
public class SendCodeExceptionHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendCodeExceptionHandlerController.class);

    private static final String EXCEL_TITLE = "上游批次未操作异常单明细";

    private static final String[] ROW_NAMES = new String[]{"包裹号","运单号","箱号","批次号"};

    @Autowired
    private SendCodeExceptionHandlerService sendCodeExceptionHandlerService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 打开批次异常单处理主页
     * @return
     */
    @Authorization
    @RequestMapping("/sendCodeHandler/index")
    public String sendCodeHandler() {
        return "exception/sendCodeExceptionHandlerIndex";
    }

    /**
     * 打开批次异常单处理明细页
     * @return
     */
    @Authorization
    @RequestMapping("/sendCodeHandler/detailPager")
    public String sendCodeHandlerDetailPager(Model model) {
        model.addAttribute("waybillAddress", PropertiesHelper.newInstance().getValue("WAYBILL_ADDRESS"));
        return "exception/sendCodeExceptionDetail";
    }

    /**
     * 通过单号（包裹号、运单号、批次号）获取上游发给自己的批次号
     * <exception>
     *     1.单号不属于包裹号、运单号、批次号
     *     2.批次不是上游发给自己的批次
     * </exception>
     * <attention>
     *     批次号分为分拣的批次和来自站点的批次两种
     * </attention>
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/querySendCodeByBarCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<String>> querySendCodeByBarCode(@RequestBody SendCodeExceptionRequest request) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        result.success();

        ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
        if (user == null) {
            result.error("用户未登录");
            return result;
        }

        BaseStaffSiteOrgDto staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
        if (null == staffSiteOrgDto) {
            result.error(MessageFormat.format("用户【{}】未维护基础资料信息",user.getUserCode()));
            return result;
        }

        result.setData(sendCodeExceptionHandlerService
                .querySendCodesByBarCode(staffSiteOrgDto.getSiteCode(),request.getBarCode()));
        return result;
    }


    /**
     * 根据批次号查询批次的汇总信息
     * @param request
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/summaryPackageNumBySendCodes", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<SendCodeSummaryResponse> summaryPackageNumBySendCodes(@RequestBody SendCodeExceptionRequest request) {
        InvokeResult<SendCodeSummaryResponse> result = new InvokeResult<>();
        result.success();

        ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
        if (user == null) {
            result.error("用户未登录");
            return result;
        }

        BaseStaffSiteOrgDto staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
        if (null == staffSiteOrgDto) {
            result.error(MessageFormat.format("用户【{}】未维护基础资料信息",user.getUserCode()));
            return result;
        }
        request.setSiteCode(staffSiteOrgDto.getSiteCode());

        result.setData(sendCodeExceptionHandlerService.summaryPackageBySendCodes(request).getData());
        return result;
    }

    /**
     * 根据类型和批次号查询明细
     * @param request
     * @return
     */
    @Authorization
    @RequestMapping(value = "/sendCodeHandler/querySendCodeDetails", method = RequestMethod.POST)
    @ResponseBody
    public PagerResult<GoodsPrintDto> querySendCodeDetails(@RequestBody SendCodeExceptionRequest request) {
        PagerResult<GoodsPrintDto> result = new PagerResult<>();
        LOGGER.debug(JsonHelper.toJson(request));
        System.out.println(JsonHelper.toJson(request));
        ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
        if (user == null) {
            return result;
        }

        BaseStaffSiteOrgDto staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
        if (null == staffSiteOrgDto) {
            LOGGER.warn("用户【{}】未维护基础资料信息",user.getUserCode());
            return result;
        }
        request.setSiteCode(staffSiteOrgDto.getSiteCode());

        BaseEntity<Pager<GoodsPrintDto>> pagerBaseEntity = sendCodeExceptionHandlerService
                .querySendCodeDetailByCondition(request);
        if (pagerBaseEntity != null) {
            result.setRows(pagerBaseEntity.getData().getData());
            result.setTotal(pagerBaseEntity.getData().getTotal().intValue());
        }
        return result;
    }

    /**
     * 导出
     * @return
     */
    @Authorization()
    @RequestMapping(value = "/exportSendCodeDetail")
    public void exportSendCodeDetail(SendCodeExceptionRequest request, HttpServletResponse response){
        LOGGER.info("SendCodeExceptionHandlerController.toExport-->导出异常批次的明细数据：{}",JsonHelper.toJson(request));

        if (null == request || request.getSendCodes() == null || request.getType() == 0) {
            LOGGER.warn("导出异常批次明细数据时提交的参数不正确：{}", JsonHelper.toJson(request));
            return;
        }

        ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
        if (user == null) {
            return;
        }

        BaseStaffSiteOrgDto staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
        if (null == staffSiteOrgDto) {
            LOGGER.warn("用户【{}】未维护基础资料信息",user.getUserCode());
            return;
        }
        request.setSiteCode(staffSiteOrgDto.getSiteCode());

        try{
            List<Object[]> rowData = sendCodeExceptionHandlerService.exportSendCodeDetail(request);

            OutputStream out = response.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook();

            String fileName = EXCEL_TITLE + DateHelper.formatDate(new Date()) + ".xls";
            String headStr = "attachment; filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + "\"";
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);

            ExportExcelDownFee exportUtil = new ExportExcelDownFee(EXCEL_TITLE, ROW_NAMES, rowData);
            exportUtil.export(workbook, out);
            workbook.write(out);
            out.close();
        }catch (Exception e){
            LOGGER.error("导出异常批次明细数据失败!{}",JsonHelper.toJson(request),e);
        }
    }
}
