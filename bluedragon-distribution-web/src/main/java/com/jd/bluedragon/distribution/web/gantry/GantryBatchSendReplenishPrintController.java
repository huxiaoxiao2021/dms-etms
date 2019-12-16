package com.jd.bluedragon.distribution.web.gantry;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.BatchSendPrintImageRequest;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendPrintImageResponse;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendPrint;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.common.util.StringUtils;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/12/15.
 */
@Controller
@RequestMapping("/GantryBatchSendReplenishPrint")
public class GantryBatchSendReplenishPrintController {
    private static final Logger log = LoggerFactory.getLogger(GantryBatchSendReplenishPrintController.class);

    private final static String HTTP = "http://";

    private final static String prefixKey = "HeadquartersIp";

    @Autowired
    ScannerFrameBatchSendService scannerFrameBatchSendService;

    @Autowired
    GantryDeviceService gantryDeviceService;

    @Autowired
    BaseService baseService;

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/index")
    public String index(Model model, Integer machineId, Integer createSiteCode, String createSiteName, String startTime, String endTime) {
        if (machineId != null && createSiteCode != null) {
            model.addAttribute("machineId", machineId);
            model.addAttribute("createSiteCode", createSiteCode);
            try {
                model.addAttribute("createSiteName", URLDecoder.decode(createSiteName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("补打界面跳转参数解码异常", e);
                model.addAttribute("createSiteName", "未知分拣中心");
            }
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
        }
        return "/gantry/GantryBatchSendReplenishPrint";
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<ScannerFrameBatchSend>>> query(ScannerFrameBatchSendSearchArgument request, Pager<List<ScannerFrameBatchSend>> pager) {
        log.debug("获取补打印数据 --> ");
        InvokeResult<Pager<List<ScannerFrameBatchSend>>> result = new InvokeResult<Pager<List<ScannerFrameBatchSend>>>();
        result.setCode(400);
        result.setMessage("服务器处理信息异常，查询补打印数据失败!!");
        result.setData(null);
        if (request != null) {
            Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
            if (pager.getPageNo() != null) {
                argumentPager.setPageNo(pager.getPageNo());
                argumentPager.init();
            }
            request.setHasPrinted(false);//未打印标示
            argumentPager.setData(request);
            try {
                result.setData(scannerFrameBatchSendService.queryAllHistoryBatchSend(argumentPager));
                result.setCode(200);
                result.setMessage("补打数据获取成功");
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("服务调用异常");
                result.setData(null);
                log.error("补打数据获取失败..", e);
            }
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/querySubSiteNo", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<ScannerFrameBatchSend>> querySubSiteNo(ScannerFrameBatchSendSearchArgument request) {
        InvokeResult<List<ScannerFrameBatchSend>> result = new InvokeResult<List<ScannerFrameBatchSend>>();
        result.setCode(400);
        result.setMessage("服务调用异常");
        result.setData(null);
        if (request == null) {
            return null;
        }
        try {
            List<ScannerFrameBatchSend> list = scannerFrameBatchSendService.queryAllReceiveSites(null,request.getMachineId());
            result.setCode(200);
            result.setData(list);
            result.setMessage("获取龙门架的目的站点成功");
        } catch (Exception e) {
            result.setMessage("获取龙门架的目的站点失败");
            log.error("加载龙门架的目的站点失败。。", e);
        }
        return result;
    }

    /**
     * 批次号打印
     */
    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/sendCodePrint", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<BatchSendPrintImageResponse>> printSendCode(@RequestBody ScannerFrameBatchSendPrint[] requests) {
        this.log.info("龙门架补打印数据开始-->需要打印的龙门架ID为:{}" , requests[0].getMachineId());
        InvokeResult<List<BatchSendPrintImageResponse>> result = new InvokeResult<List<BatchSendPrintImageResponse>>();
        result.setCode(400);
        result.setMessage("服务调用成功，数据为空");

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        Integer userId = 0;
        String userName = "none";//用户姓名
        if (erpUser != null) {
            userId = erpUser.getStaffNo() == null ? 0 : erpUser.getStaffNo();
            userName = erpUser.getUserName() == null ? "none" : erpUser.getUserName();
        }

        String machineId = requests[0].getMachineId();
        if (StringUtils.isBlank(machineId)) {
            result.setCode(200);
            result.setMessage("服务调用成功，龙门架参数错误");
            return result;
        }

        ScannerFrameBatchSendSearchArgument sfbssa = new ScannerFrameBatchSendSearchArgument();
        sfbssa.setMachineId(machineId);//查询参数只有龙门架ID
        Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
        argumentPager.setStartIndex(0);
        argumentPager.setPageSize(500);//最多一次打印500条
        argumentPager.setData(sfbssa);
        try {
            log.info("需要执行该打印并完结批次的条数为：{}", requests.length);
            List<BatchSendPrintImageResponse> results = new ArrayList<BatchSendPrintImageResponse>();
            String url = HTTP + PropertiesHelper.newInstance().getValue(prefixKey) + "/batchSendPrint/print";
            for (ScannerFrameBatchSendPrint item : requests) {
                if (item.getReceiveSiteCode() == 0 || "".equals(item.getSendCode()) || item.getCreateSiteCode() == 0) {
                    //没有目的站点，自动退出循环
                    log.warn("检测出该条数据参数不完全：本条数据丢弃，本次循环退出。");
                    continue;
                }
                /** 2. ==================获取打印图片================= **/
                BatchSendPrintImageRequest itemRequest = new BatchSendPrintImageRequest();
                itemRequest.setSendCode(item.getSendCode());
                itemRequest.setCreateSiteCode(item.getCreateSiteCode());
                String createSiteName = "";
                String receiveSiteName = "";
                createSiteName = baseService.getSiteNameBySiteID(item.getCreateSiteCode());
                receiveSiteName = baseService.getSiteNameBySiteID(item.getReceiveSiteCode());
                itemRequest.setCreateSiteName(createSiteName);
                itemRequest.setReceiveSiteCode(item.getReceiveSiteCode());
                itemRequest.setReceiveSiteName(receiveSiteName);
                Integer packageSum = 0;
                /** 获取包裹的数据量 **/
                List<SendDetail> sendDetailList = gantryDeviceService.queryWaybillsBySendCode(item.getSendCode());
                if (sendDetailList != null && sendDetailList.size() > 0) {
                    packageSum = sendDetailList.size();//获取包裹的数量
                }
                itemRequest.setPackageNum(packageSum);

                BatchSendPrintImageResponse itemResponse = RestHelper.jsonPostForEntity(url, itemRequest, new TypeReference<BatchSendPrintImageResponse>() {
                });
                results.add(itemResponse);
                log.info("获取图片的base64结束。");
                /** ===================获取打印图片获取base64图片码结束================= **/
                /** =======================3.更新scanner_frame_batch_send表打印时间，打印次数开始================== **/
//                scannerFrameBatchSendService.submitPrint(item.getId(),userId,userName);
                // TODO: 2016/12/26 打印次数加一，最后一次打印时间更新
                log.info("更新打印时间次数结束。返回结果");
                result.setCode(200);
                result.setMessage("服务调用成功");
                result.setData(results);
            }
        } catch (Exception e) {
            log.error("获取数据异常");
            result.setCode(500);
            result.setMessage("服务调用异常");
        }
        return result;
    }

    /**
     * domain 类型转换
     *
     * @param request
     * @return
     */
    private ScannerFrameBatchSendSearchArgument toScannerFrameBatchSend(GantryDeviceConfigRequest request, Integer receiveSiteCode, String receiveSiteName) {
        ScannerFrameBatchSendSearchArgument result = new ScannerFrameBatchSendSearchArgument();
        if (request != null) {
            result.setMachineId(String.valueOf(request.getMachineId()));
            result.setStartTime(request.getStartTime());
            result.setEndTime(request.getEndTime());
            result.setReceiveSiteCode(receiveSiteCode);
            result.setHasPrinted(false);
        }
        return result;
    }


}
