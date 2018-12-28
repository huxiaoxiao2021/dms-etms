package com.jd.bluedragon.distribution.web.waybill.rma;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.RmaHandoverQueryRequest;
import com.jd.bluedragon.distribution.api.response.RmaHandoverResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.print.PrintHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static final Logger logger = Logger.getLogger(RmaHandOverController.class);

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
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
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
                //queryParam.setCreateSiteCode(910); //TODO 上线删除 刘铎
                return response;
            }

            // 设置分页对象
            if (page == null) {
                page = new Pager<List<AreaDest>>(Pager.DEFAULT_PAGE_NO);
            }
            // 设置默认分页大小
            if (page.getPageSize() == null || page.getPageSize() <= 0) {
                page.setPageSize(Pager.DEFAULT_PAGE_SIZE);
            }

            Pager<List<RmaHandoverWaybill>> pager = rmaHandOverWaybillService.getListWithoutDetail(queryParam, page);
            response.setData(pager);
            response.setCode(RmaHandoverResponse.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("根据查询条件获取RMA交接清单打印信息异常", e);
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

    /**
     * 查询地址方法
     *
     * @param waybillCode
     * @return
     */
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
                    logger.error("根据运单号获取收货人地址时发生异常，运单号：" + waybillCode, e);
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
     * @param request
     * @return
     */
    @RequestMapping(value = "/printWaybillRma")
    public RmaHandoverResponse<String> printWaybillRma(HttpServletRequest request, HttpServletResponse response) {
        RmaHandoverResponse<String> rmaResponse = new RmaHandoverResponse<String>();
        rmaResponse.setCode(RmaHandoverResponse.CODE_FAIL);
        try {
            String token = request.getParameter("token");
            RmaHandoverPrint rmaHandoverPrint = rmaHandOverWaybillService.getPrintObject(Integer.valueOf(token));
            if (rmaHandoverPrint != null) {
                PrintHelper.printRmaHandoverPDF(rmaHandoverPrint, response.getOutputStream());
                Date date = new Date();
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                if (erpUser != null) {
                    for (Long id : rmaHandoverPrint.getIds()) {
                        RmaHandoverWaybill rmaHandoverWaybill = new RmaHandoverWaybill();
                        rmaHandoverWaybill.setId(id);
                        rmaHandoverWaybill.setPrintStatus(PrintStatusEnum.HAD_PRINTED.getCode());
                        rmaHandoverWaybill.setPrintTime(date);
                        rmaHandoverWaybill.setPrintUserCode(erpUser.getUserId());
                        rmaHandoverWaybill.setPrintUserName(erpUser.getUserName());
                        rmaHandOverWaybillService.update(rmaHandoverWaybill);
                    }
                }
            }
            rmaResponse.toNormal("");
        } catch (Exception e) {
            logger.error("[RMA交接清单打印]生成打印页面时发生异常", e);
            rmaResponse.toException("生成打印页面时发生异常，请联系管理员");
        }
        return rmaResponse;
    }

    /**
     * 打印
     *
     * @param idList
     * @return
     */
    @RequestMapping("/printWaybillRmaPage")
    @ResponseBody
    public RmaHandoverResponse<List<Integer>> printWaybillRmaPage(@RequestBody String idList) {
        RmaHandoverResponse<List<Integer>> response = new RmaHandoverResponse<List<Integer>>();
        response.setCode(CommonDto.CODE_FAIL);
        try {
            String[] idsArray = idList.split(",");
            List<Long> idLs = new ArrayList<Long>();
            for (String id : idsArray) {
                Long idL = new Long(id);
                idLs.add(idL);
            }
            List<Integer> tokenList = rmaHandOverWaybillService.getTokenGroupByKey(idLs);
            if (tokenList.size() > 0) {
                response.setCode(CommonDto.CODE_NORMAL);
                response.setData(tokenList);
            }
        } catch (Exception e) {
            logger.error("[RMA交接清单打印]获取打印页面分组信息时发生异常", e);
            response.toException("获取打印页面分组信息时发生异常，请联系管理员");
        }
        return response;
    }

    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(String token, Model model) {
        try {
            this.getExportData(token, model);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            logger.error("toExport:" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 组装导出数据
     *
     * @param token
     * @return
     */
    private List<List<Object>> getExportData(String token, Model model) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();

        //"备件条码", "运单号", "出库单号", "商品编号", "商品名称", "异常备注"
        heads.add("备件条码");
        heads.add("运单号");
        heads.add("出库单号");
        heads.add("商品编号");
        heads.add("商品名称");
        heads.add("异常备注");
        resList.add(heads);

        RmaHandoverPrint rmaHandoverPrint = rmaHandOverWaybillService.getPrintObject(Integer.valueOf(token));

        //能进来导出 就证明rmaHandoverPrints 集合只有一条数据，，根据他打印功能梳理出来的 导出只导出明细部分
        if (rmaHandoverPrint == null) {
            logger.error("RMA交接清单导出存在异常数据, 根据token获取打印数据为空，token:" + token);
            return resList;
        }
        model.addAttribute("filename", "RMA交接清单-" + rmaHandoverPrint.getReceiver() + ".xls");
        model.addAttribute("sheetname", "RMA交接清单");

        if (!rmaHandoverPrint.getHandoverDetails().isEmpty()) {
            for (RmaHandoverDetail rmaHandoverDetail : rmaHandoverPrint.getHandoverDetails()) {
                List<Object> body = new ArrayList<Object>();
                body.add(rmaHandoverDetail.getSpareCode());
                body.add(rmaHandoverDetail.getWaybillCode());
                body.add(rmaHandoverDetail.getOutboundOrderCode());
                body.add(rmaHandoverDetail.getSkuCode());
                body.add(rmaHandoverDetail.getGoodName());
                body.add(rmaHandoverDetail.getExceptionRemark());
                resList.add(body);
            }
        }
        model.addAttribute("contents", resList);
        return resList;
    }


}
