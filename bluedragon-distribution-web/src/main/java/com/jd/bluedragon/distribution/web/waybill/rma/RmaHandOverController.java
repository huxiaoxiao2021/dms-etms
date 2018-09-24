package com.jd.bluedragon.distribution.web.waybill.rma;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.RmaHandoverQueryRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.rma.request.RmaHandoverQueryParam;
import com.jd.bluedragon.distribution.api.response.RmaHandoverResponse;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.rma.PrintStatusEnum;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.print.PrintHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
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
                return response;
            }

            // 设置分页对象
            if (page == null) {
                page = new Pager<List<AreaDest>>(Pager.DEFAULT_PAGE_NO);
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
                        response.setData(rmaHandOverWaybillService.getReceiverAddressByWaybillCode(waybillCode, dto.getSiteCode()));
                        response.setCode(RmaHandoverResponse.CODE_NORMAL);
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
            String billsNos = request.getParameter("billsNos");
            String[] arrbillsNos = billsNos.split(",");
            List<Long> idLs=new ArrayList<Long>();
            for(String id:arrbillsNos){
                Long idL=new Long(id);
                idLs.add(idL);
            }
            List<RmaHandoverPrint> rmaHandoverPrintList=rmaHandOverWaybillService.getPrintInfo(idLs);
            PrintHelper.getPrintWaybillRma(rmaHandoverPrintList,response.getOutputStream());
            //todo 更新是否已打印状态，根据id
        } catch (Exception e) {
            logger.error("根据查询条件获取RMA交接清单打印信息异常", e);
            rmaResponse.setCode(RmaHandoverResponse.CODE_EXCEPTION);
            rmaResponse.setMessage("查询地址失败，单号无效");
        }
        return rmaResponse;
    }

    /**
     * 打印
     * @return
     * @param ids
     */
    @RequestMapping("/printWaybillRmaPage")
    @ResponseBody
    public RmaHandoverResponse<String> printWaybillRmaPage(@RequestBody String ids) {
        RmaHandoverResponse<String> rmaHandoverResponse = new RmaHandoverResponse<String>();
        rmaHandoverResponse.setCode(CommonDto.CODE_FAIL);
        try {
            String[] arrbillsNos = ids.split(",");
            List<Long> idLs=new ArrayList<Long>();
            for(String id:arrbillsNos){
                Long idL=new Long(id);
                idLs.add(idL);
            }
            List<RmaHandoverPrint> rmaHandoverPrints=rmaHandOverWaybillService.getPrintInfo(idLs);
            if(rmaHandoverPrints!=null&&rmaHandoverPrints.size()>0){
                HashMap<String,List<Long>> hashMap=new HashMap<String,List<Long>>();
                Integer pages=0;
                for(RmaHandoverPrint rmaHandoverPrint:rmaHandoverPrints){
                    pages++;
                    // TODO: 2018/9/24 此处增加id放入map中
                    hashMap.put(pages.toString(),rmaHandoverPrint.getIds());
                }
                if(hashMap.size()>0){
                    rmaHandoverResponse.setCode(CommonDto.CODE_NORMAL);
                    rmaHandoverResponse.setData(JsonHelper.toJson(hashMap));
                }else{
                    //未作处理
                }
            }
        } catch (Exception e) {
            logger.error("根据查询条件获取RMA交接清单打印信息异常", e);
            rmaHandoverResponse.setCode(RmaHandoverResponse.CODE_EXCEPTION);
            rmaHandoverResponse.setMessage("查询地址失败，单号无效");
        }
        return rmaHandoverResponse;
    }
}
