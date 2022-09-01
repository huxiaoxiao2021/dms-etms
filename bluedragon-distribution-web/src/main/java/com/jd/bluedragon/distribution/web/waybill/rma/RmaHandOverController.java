package com.jd.bluedragon.distribution.web.waybill.rma;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.security.SecurityCheckerExecutor;
import com.jd.bluedragon.core.security.enums.SecurityDataMapFuncEnum;
import com.jd.bluedragon.distribution.api.request.RmaHandoverQueryRequest;
import com.jd.bluedragon.distribution.api.response.RmaHandoverResponse;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ExportExcelDownFee;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.print.PrintHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghao141 on 2018/9/18.
 */
@Controller
@RequestMapping("/waybill/rma")
public class RmaHandOverController {

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private static final Logger log = LoggerFactory.getLogger(RmaHandOverController.class);

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Autowired
    private SecurityCheckerExecutor securityCheckerExecutor;

    /**
     * 跳转到主界面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping("/index")
    public String index() {
        return "waybill/rma/list";
    }

    /**
     * 查询方法
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController.query", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public RmaHandoverResponse<Pager<List<RmaHandoverWaybill>>> query(RmaHandoverQueryRequest rmaHandoverQueryRequest, Pager page) {
        RmaHandoverResponse<Pager<List<RmaHandoverWaybill>>> response = new RmaHandoverResponse<Pager<List<RmaHandoverWaybill>>>();
        response.setCode(RmaHandoverResponse.CODE_NORMAL);
        try {
            if (rmaHandoverQueryRequest == null) {
                response.toWarn("查询条件不能为空");
                return response;
            }

            if (rmaHandoverQueryRequest.getSendDateStart() == null && rmaHandoverQueryRequest.getSendDateEnd() == null) {
                response.toWarn("发货时间条件不能为空");
                return response;
            }

            if (!StringUtils.isEmpty(rmaHandoverQueryRequest.getWaybillCode())) {
                if (!SerialRuleUtil.isWaybillOrPackageNo(rmaHandoverQueryRequest.getWaybillCode())) {
                    response.toWarn("运单号/包裹号输入错误，请核对后重新输入");
                    return response;
                }
            }

            if (StringUtils.isEmpty(rmaHandoverQueryRequest.getReceiverAddress())) {
                response.toWarn("请输入收货地址");
                return response;
            }

            Integer oneQueryLimit = exportConcurrencyLimitService.getOneQuerySizeLimit();
            if(page.getPageSize()==null || page.getPageSize() > oneQueryLimit){
                response.toWarn("单页限制显示条数上限"+ oneQueryLimit);
                return response;
            }

            RmaHandoverQueryParam queryParam = getQueryParam(rmaHandoverQueryRequest);
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                if (dto != null) {
                    queryParam.setCreateSiteCode(dto.getSiteCode());
                } else {
                    response.toFail("根据ERP账号:" + erpUser.getUserCode() + "获取所属站点信息为空，请检查是否配置基础资料信息");
                    return response;
                }
            } else {
                response.toFail("获取当前登录用户信息失败，请重新登录ERP后尝试");
                return response;
            }

            // 设置默认分页大小
            if (page.getPageSize() == null || page.getPageSize() <= 0) {
                page.setPageSize(Pager.DEFAULT_PAGE_SIZE);
            }

            Pager<List<RmaHandoverWaybill>> pager = rmaHandOverWaybillService.getListWithoutDetail(queryParam, page);
            response.setData(pager);
            response.setCode(RmaHandoverResponse.CODE_NORMAL);
        } catch (Exception e) {
            log.error("根据查询条件获取RMA交接清单打印信息异常", e);
            response.toException("根据查询条件获取RMA交接清单打印信息异常：" + e.getMessage());
        }
        return response;
    }

    private RmaHandoverQueryParam getQueryParam(RmaHandoverQueryRequest request) {
        RmaHandoverQueryParam queryParam = new RmaHandoverQueryParam();
        queryParam.setSendDateStart(DateHelper.parseDate(request.getSendDateStart(), DateHelper.DATE_TIME_FORMAT[0]));
        queryParam.setSendDateEnd(DateHelper.parseDate(request.getSendDateEnd(), DateHelper.DATE_TIME_FORMAT[0]));
        if (request.getPrintStatus() != null && PrintStatusEnum.getEnum(request.getPrintStatus()) != null) {
            queryParam.setPrintStatus(request.getPrintStatus());
        }
        queryParam.setReceiverAddress(request.getReceiverAddress());
        // 获取运单号
        queryParam.setWaybillCode(SerialRuleUtil.getWaybillCode(request.getWaybillCode()));
        return queryParam;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping("/showDetailAddress")
    @ResponseBody
    public RmaHandoverResponse<String> showDetailAddress(@RequestBody Integer id) {
        RmaHandoverResponse<String> response = new RmaHandoverResponse<String>();
        response.setCode(RmaHandoverResponse.CODE_NORMAL);
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser == null) {
            response.toFail("获取当前登录用户信息失败，请重新登录ERP后尝试");
            return response;
        }
        if(id == null){
            response.toFail("参数错误!");
            return response;
        }
        List<Long> list = Lists.newArrayList(Long.valueOf(String.valueOf(id)));
        List<RmaHandoverWaybill> queryResult = rmaHandOverWaybillService.getList(list, false);
        RmaHandoverWaybill rmaHandoverWaybill = CollectionUtils.isEmpty(queryResult) ? null : queryResult.get(0);
        if(rmaHandoverWaybill == null){
            response.toFail("根据id未查询到数据!");
            return response;
        }
        // 安全次数限制
        InvokeResult<Boolean> securityCheckResult
                = securityCheckerExecutor.verifyWaybillDetailPermission(SecurityDataMapFuncEnum.WAYBILL_RMA, erpUser.getUserCode(), rmaHandoverWaybill.getWaybillCode());
        if(!securityCheckResult.codeSuccess()){
            response.toFail(securityCheckResult.getMessage());
            return response;
        }
        response.setData(rmaHandoverWaybill.getReceiverAddress());
        return response;
    }

    /**
     * 查询地址方法
     *
     * @param waybillCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping("/receiverAddressQuery")
    @ResponseBody
    public RmaHandoverResponse<String> getReceiverAddressQuery(@RequestBody String waybillCode) {
        RmaHandoverResponse<String> response = new RmaHandoverResponse<String>();
        if (!StringUtils.isEmpty(waybillCode)) {
            if (!SerialRuleUtil.isWaybillOrPackageNo(waybillCode)) {
                response.toWarn("运单号/包裹号输入错误，请核对后重新输入");
                return response;
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                try {
                    // 安全次数限制
                    InvokeResult<Boolean> securityCheckResult
                            = securityCheckerExecutor.verifyWaybillDetailPermission(SecurityDataMapFuncEnum.WAYBILL_RMA, erpUser.getUserCode(), waybillCode);
                    if(!securityCheckResult.codeSuccess()){
                        response.toFail(securityCheckResult.getMessage());
                        return response;
                    }
                    BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                    if (dto != null) {
                        String receiverAddress = rmaHandOverWaybillService.getReceiverAddressByWaybillCode(SerialRuleUtil.getWaybillCode(waybillCode), dto.getSiteCode());
                        if (StringUtils.isEmpty(receiverAddress)) {
                            response.toFail("根据运单号查询收货地址为空");
                        } else {
                            response.setData(rmaHandOverWaybillService.getReceiverAddressByWaybillCode(waybillCode, dto.getSiteCode()));
                            response.setCode(RmaHandoverResponse.CODE_NORMAL);
                        }
                    } else {
                        response.toFail("根据ERP账号:" + erpUser.getUserCode() + "获取所属站点信息为空，请检查是否配置基础资料信息");
                    }
                } catch (Exception e) {
                    log.error("根据运单号获取收货人地址时发生异常，运单号：{}", waybillCode, e);
                    response.toException("根据运单号获取收货人地址时发生异常");
                }
            } else {
                response.toFail("获取当前登录用户信息失败，请重新登录ERP后尝试");
            }
        } else {
            response.setCode(RmaHandoverResponse.CODE_NORMAL);
        }
        return response;
    }

    /**
     * 打印
     *
     * @param idList
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping(value = "/printWaybillRma")
    @ResponseBody
    public RmaHandoverResponse<String> printWaybillRma(String idList, HttpServletResponse response) {
        RmaHandoverResponse<String> rmaResponse = new RmaHandoverResponse<String>();
        rmaResponse.setCode(RmaHandoverResponse.CODE_FAIL);
        try {
            String[] idsArray = idList.split(Constants.SEPARATOR_COMMA);
            List<Long> idLs = new ArrayList<Long>();
            for (String id : idsArray) {
                Long idL = new Long(id);
                idLs.add(idL);
            }
            List<RmaHandoverPrint> rmaHandoverPrintList = rmaHandOverWaybillService.getPrintInfo(idLs);
            PrintHelper.printRmaHandoverPDF(rmaHandoverPrintList, response.getOutputStream());
            Date date = new Date();
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                for (Long id : idLs) {
                    RmaHandoverWaybill rmaHandoverWaybill = new RmaHandoverWaybill();
                    rmaHandoverWaybill.setId(id);
                    rmaHandoverWaybill.setPrintStatus(PrintStatusEnum.HAD_PRINTED.getCode());
                    rmaHandoverWaybill.setPrintTime(date);
                    rmaHandoverWaybill.setPrintUserCode(erpUser.getUserId());
                    rmaHandoverWaybill.setPrintUserName(erpUser.getUserName());
                    rmaHandOverWaybillService.update(rmaHandoverWaybill);
                }
            }
        } catch (Exception e) {
            log.error("[RMA交接清单打印]生成打印页面时发生异常", e);
            rmaResponse.toException("生成打印页面时发生异常，请联系管理员");
        }
        return rmaResponse;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_RMAHANDOVER_R)
    @RequestMapping(value = "/toExport")
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.waybill.rma.RmaHandOverController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void toExport(String idList, HttpServletResponse response) {
        try {
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.RMA_HAND_OVER_REPORT.getCode());
            this.doExport(idList, response);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.RMA_HAND_OVER_REPORT.getCode());
        } catch (Exception e) {
            log.error("toExport:异常", e);
        }
    }

    /**
     * 组装导出数据
     *
     * @param idList
     * @return
     */
    private void doExport(String idList, HttpServletResponse response) throws Exception {
        //"备件条码", "运单号", "出库单号", "商品编号", "商品名称", "异常备注"
        String[] heads = new String[]{"备件条码", "运单号", "出库单号", "商品编号", "商品名称", "异常备注"};

        String[] idsArray = idList.split(Constants.SEPARATOR_COMMA);
        List<Long> idLs = new ArrayList<Long>();

        for (String id : idsArray) {
            Long idL = new Long(id);
            idLs.add(idL);
        }
        Map<String, RmaHandoverPrint> rmaHandoverPrintMap = rmaHandOverWaybillService.getPrintInfoMap(idLs);

        HSSFWorkbook workbook = new HSSFWorkbook();
        OutputStream out = response.getOutputStream();
        //使用流将数据导出
        //防止中文乱码
        String fileName = "RMA交接清单打印-" + DateHelper.formatDate(new Date()) + ".xls";
        String headStr = "attachment; filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + "\"";
        response.setContentType("octets/stream");
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", headStr);

        ExportExcelDownFee ex;
        for (String key : rmaHandoverPrintMap.keySet()) {
            RmaHandoverPrint rmaHandoverPrint = rmaHandoverPrintMap.get(key);
            List<RmaHandoverDetail> data = rmaHandoverPrint.getHandoverDetails();
            ex = new ExportExcelDownFee(key, heads, this.buildSheetData(data));
            ex.export(workbook, out);
        }

        workbook.write(out);
        out.close();
    }

    private List<Object[]> buildSheetData(List<RmaHandoverDetail> data) {
        List<Object[]> sheetData = new ArrayList<Object[]>(data.size());
        if (data != null && data.size() > 0) {
            for (RmaHandoverDetail detail : data) {
                Object[] rowData = new Object[6];
                rowData[0] = detail.getSpareCode();
                rowData[1] = detail.getWaybillCode();
                rowData[2] = detail.getOutboundOrderCode();
                rowData[3] = detail.getSkuCode();
                rowData[4] = detail.getGoodName();
                rowData[5] = detail.getExceptionRemark();
                sheetData.add(rowData);
            }
        }
        return sheetData;
    }

}
