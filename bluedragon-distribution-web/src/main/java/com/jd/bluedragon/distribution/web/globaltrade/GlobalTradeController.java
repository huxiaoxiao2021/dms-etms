package com.jd.bluedragon.distribution.web.globaltrade;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.request.LoadBillRequest;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.GlobalTradeException;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.jsf.gd.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/globalTrade")
public class GlobalTradeController {

    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String CARCODE_REG = "[A-Za-z0-9\u4e00-\u9fa5]+";

    private static final String ZHUOZHI_PRELOAD_URL = "http://121.33.205.117:18080/customs/rest/custjson/postwmspredata.do";

    @Autowired
    private LoadBillService loadBillService;
    @Autowired
    private LoadBillDao loadBillDao;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "globaltrade/global-trade-index";
    }

    @RequestMapping(value = "/preload", method = RequestMethod.POST)
    @ResponseBody
    public LoadBillReportResponse prepareLoadBill(HttpServletRequest request) {
        String carCode = request.getParameter("carCode");
        String loadBillIdStr = request.getParameter("loadBillId");
        if (StringHelper.isEmpty(carCode) || !Pattern.matches(CARCODE_REG, carCode)) {
            return new LoadBillReportResponse(1000, "车次号不符合要求");
        }
        if (StringHelper.isEmpty(loadBillIdStr)) {
            return new LoadBillReportResponse(2000, "订单号不能为空");
        }

        Integer effectSize = null;

        try {
            List<Long> loadBillId = new ArrayList<Long>();
            for (String id : loadBillIdStr.split(",")) {
                loadBillId.add(Long.valueOf(id));
            }
//            taskService.addBtch(toGlobalTradeTask(loadBIlls,carCode));
            effectSize = loadBillService.preLoadBill(loadBillId, carCode);
        } catch (Exception ex) {
            logger.error("预装载操作失败，原因", ex);
            if (ex instanceof NumberFormatException) {
                return new LoadBillReportResponse(3000, "预装载操作失败,数据不合法");
            } else if (ex instanceof GlobalTradeException) {
                return new LoadBillReportResponse(4000, ex.getMessage());
            } else {
                return new LoadBillReportResponse(5000, "预装载操作失败,操作异常");
            }
        }
        return new LoadBillReportResponse(200, "预装载成功[" + effectSize + "]条订单");
    }

    private List<Task> toGlobalTradeTask(List<LoadBill> loadBills, String trunkNo) {
        List<Task> tasks = new ArrayList<Task>();
        for (LoadBill bill : loadBills) {
            bill.setTruckNo(trunkNo);
            bill.setPackageAmount(1);
            bill.setWaybillCode(null);
            bill.setPackageUserCode(null);
            bill.setWeight(null);
            Task task = new Task();
            task.setBody(JsonHelper.toJson(bill));
            task.setFingerprint(Md5Helper.encode(bill.getLoadId()));
            task.setKeyword1(bill.getWaybillCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setType(Task.TASK_TYPE_GLOBAL_TRADE);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            tasks.add(task);
        }
        return tasks;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUser erpUser = ErpUserClient.getCurrUser();
            model.addAttribute("erpUser", erpUser);
        } catch (Exception e) {
            logger.error("index error!", e);
        }
        return "globaltrade/global-trade-index";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ResponseBody
    public LoadBillReportResponse cancelLoadBillStatus(LoadBillRequest request) {
        LoadBillReportResponse response = new LoadBillReportResponse(1, JdResponse.MESSAGE_OK);
        try {
            if (request == null || StringUtils.isBlank(request.getIds())) {
                return new LoadBillReportResponse(2, "参数错误");
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            String[] orderIdArray = request.getIds().split(",");
            if (orderIdArray.length == 0) {
                return new LoadBillReportResponse(2, "orderId数量为0");
            }
            List<LoadBill> loadBillList = new ArrayList<LoadBill>();
            for (String orderid : orderIdArray) {
                LoadBill loadBill = loadBillService.findLoadbillByID(Long.parseLong(orderid));
                loadBill.setPackageUser(erpUser.getUserName());
                loadBill.setPackageUserCode(Integer.parseInt(erpUser.getUserCode()));
                loadBill.setApprovalTime(new Date());
                loadBill.setApprovalCode(LoadBill.BEGINNING);
            }
            loadBillService.cancelPreloaded(loadBillList);
        } catch (Exception e) {
            response = new LoadBillReportResponse(2, "操作异常");
            logger.error("GlobalTradeController 发生异常,异常信息 : ", e);
        }
        return response;
    }

    @RequestMapping(value = "/loadBill/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<Pager<List<LoadBill>>> doQueryLoadBill(LoadBillRequest
                                                                    request, Pager<List<LoadBill>> pager) {
        CommonDto<Pager<List<LoadBill>>> cdto = new CommonDto<Pager<List<LoadBill>>>();
        try {
            logger.info("GlobalTradeController doQueryLoadBill begin...");
            if (null == request) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            Map<String, Object> params = this.getParamsFromRequest(request);
            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<LoadBill>>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<List<LoadBill>>(pager.getPageNo(), pager.getPageSize());
            }
            params.putAll(ObjectMapHelper.makeObject2Map(pager));

            List<LoadBill> loadBillList = loadBillService.findPageLoadBill(params);
            Integer totalSize = loadBillService.findCountLoadBill(params);
            pager.setTotalSize(totalSize);
            pager.setData(loadBillList);
            logger.info("查询符合条件的规则数量：" + totalSize);

            cdto.setData(pager);
            cdto.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("doQueryLoadBill-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    private Map<String, Object> getParamsFromRequest(LoadBillRequest request) throws ParseException {
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(request.getSendCode())) {
            params.put("sendCodeList", StringHelper.parseList(request.getSendCode(), ","));
        }
        if (request.getDmsCode() != null && request.getDmsCode() > 0) {
            params.put("dmsCode", request.getDmsCode());
        }
        if (request.getApprovalCode() != null && request.getApprovalCode() > 0) {
            params.put("approvalCode", request.getApprovalCode());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(request.getSendTimeFrom())) {
            params.put("sendTimeFrom", sdf.parse(request.getSendTimeFrom()));
        }
        if (StringUtils.isNotBlank(request.getSendTimeTo())) {
            params.put("sendTimeTo", sdf.parse(request.getSendTimeTo()));
        }
        return params;
    }

    @RequestMapping(value = "/loadBill/initial", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<String> initialLoadBill(LoadBillRequest request) {
        CommonDto<String> cdto = new CommonDto<String>();
        try {
            logger.info("GlobalTradeController initialLoadBill begin with sendCode is " + request.getSendCode());
            if (null == request || StringUtils.isBlank(request.getSendCode())) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            int initialNum = -1;
            ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                initialNum = loadBillService.initialLoadBill(request.getSendCode(), erpUser.getUserId(), erpUser.getUserName());
            } else {
                initialNum = loadBillService.initialLoadBill(request.getSendCode(), null, null);
            }
            if (initialNum == 0) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("批次号 " + request.getSendCode() + " 的数据为空！");
                return cdto;
            }
            cdto.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("initialLoadBill-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    @RequestMapping(value = "/loadBill/status", method = RequestMethod.POST)
    @ResponseBody
    public LoadBillReportResponse updateLoadBillStatus(LoadBillReportRequest request) {
        LoadBillReportResponse response = new LoadBillReportResponse(1, JdResponse.MESSAGE_OK);
        try {
            if (request == null || StringUtils.isBlank(request.getReportId())
                    || StringUtils.isBlank(request.getLoadId())
                    || StringUtils.isBlank(request.getOrderId())
                    || StringUtils.isBlank(request.getLoadId())
                    || StringUtils.isBlank(request.getWarehouseId())
                    || request.getStatus() == null
                    || request.getStatus() < 1) {
                return new LoadBillReportResponse(2, "参数错误");
            }
            loadBillService.updateLoadBillStatusByReport(resolveLoadBillReport(request));
        } catch (Exception e) {
            response = new LoadBillReportResponse(2, "操作异常");
            logger.error("GlobalTradeController 发生异常,异常信息 : ", e);
        }
        return response;
    }

    private LoadBillReport resolveLoadBillReport(LoadBillReportRequest request) {
        LoadBillReport report = new LoadBillReport();
        report.setReportId(request.getReportId());
        report.setLoadId(request.getLoadId());
        report.setProcessTime(request.getProcessTime());
        report.setStatus(request.getStatus());
        report.setWarehouseId(request.getWarehouseId());
        report.setNotes(request.getNotes());
        report.setOrderId(request.getOrderId());
        return report;
    }

}
