package com.jd.bluedragon.distribution.web.sortMachine;

import IceInternal.Ex;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.request.SendExceptionRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendPrintImageResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendPrint;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.gantry.domain.GantryBatchSendResult;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineBatchSendResult;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupConfig;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineGroupRequest;
import com.jd.bluedragon.distribution.sendGroup.domain.SortMachineSendGroup;
import com.jd.bluedragon.distribution.sendGroup.service.SortMachineSendGroupService;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.meta.TypeQualifier;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
@Controller
@RequestMapping("/sortMachineAutoSend")
public class SortMachineAutoSendController {
    private static final Log logger = LogFactory.getLog(SortMachineAutoSendController.class);
    private final static String prefixKey = "localdmsIp$";
    private final static String HTTP = "http://";
    private final static String PREFIX_VER_URL = "DMSVER_ADDRESS";

    private final static int SENDCODE_PRINT_TYPE = 1;//批次打印

    private final static int SUMMARY_PRINT_TYPE = 2;//汇总单打印

    private final static int MAX_PAGE_SIZE = 1000;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Resource
    private SortSchemeService sortSchemeService;
    @Resource
    private SortMachineSendGroupService sortMachineSendGroupService;
    @Resource
    private ScannerFrameBatchSendService scannerFrameBatchSendService;
    @Autowired
    GantryExceptionService gantryExceptionService;

    @Autowired
    GantryDeviceService gantryDeviceService;

    @Autowired
    WaybillService waybillService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        this.logger.debug("分拣机自动发货 --> index");
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                Integer siteCode = 0;
                String siteName = "";
                String userCode = erpUser.getUserCode() == null ? "none" : erpUser.getUserCode();
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if (bssod != null && bssod.getSiteType() == 64) {
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
                model.addAttribute("createSiteCode", String.valueOf(siteCode));
                model.addAttribute("createSiteName", siteName);
            }
        } catch (Exception e) {
            logger.info("没有维护分拣中心，初始化加载失败");
        }
        return "sortMachine/sortMachineAutoSendIndex";
    }

    /**
     * 根据用户erp加载该用户所在分拣中心的分拣机
     * @return
     */
    @RequestMapping(value = "/findSortMachineByErp", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<String>> findSortMachineByErp(){
        InvokeResult<List<String>> response = new InvokeResult<List<String>>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        //获取分拣站点失败
        if(bssod == null || bssod.getSiteCode() == null){
            response.parameterError("根据erp活动分拣中心失败！");
            return response;
        }
        //站点类型不是分拣中心
        if(bssod.getSiteType() != 64){
            response.parameterError("该站点不是分拣中心！");
            return response;
        }
        //获取分拣中心本地服务url
        String url = PropertiesHelper.newInstance().getValue(prefixKey + bssod.getSiteCode());
        if (StringUtils.isBlank(url)) {
            response.parameterError("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
            return response;
        }
        //构建查询分拣机的model
        SortSchemeRequest sortSchemeRequest = new SortSchemeRequest();
        sortSchemeRequest.setSiteNo(String.valueOf(bssod.getSiteCode()));
        sortSchemeRequest.setPageNo(1);
        sortSchemeRequest.setPageSize(MAX_PAGE_SIZE);
        SortSchemeResponse<Pager<List<SortScheme>>> remoteResponse = sortSchemeService.
                pageQuerySortScheme(sortSchemeRequest, HTTP + url + "/autosorting/sortScheme/list");
        if(remoteResponse == null
                || remoteResponse.getData() == null
                || remoteResponse.getData().getData() == null
                || remoteResponse.getData().getData().isEmpty()){
            response.parameterError("获取分拣机失败,或当前站点无分拣机！");
            return response;
        }
        List<SortScheme> sortSchemes = remoteResponse.getData().getData();
        List<String> machineCodes = new ArrayList<String>();
        Map<String, String> machineCodeMap = new HashMap<String, String>();
        for(SortScheme sortScheme : sortSchemes){
            //去除重复的
            if(machineCodeMap.get(sortScheme.getMachineCode()) == null){
                machineCodes.add(sortScheme.getMachineCode());
                machineCodeMap.put(sortScheme.getMachineCode(), sortScheme.getMachineCode());
            }
        }
        response.setData(machineCodes);

        return response;
    }

    /**
     * 根据分拣机编号查询发货组
     * @param machineCode
     * @return
     */
    @RequestMapping(value = "/findSendGroupByMachineCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineSendGroup>> findSendGroupByMachineCode(String machineCode){
        InvokeResult<List<SortMachineSendGroup>> response = new InvokeResult<List<SortMachineSendGroup>>();
        try{
            List<SortMachineSendGroup> sortMachineSendGroups = sortMachineSendGroupService.
                    findSendGroupByMachineCode(machineCode);
            response.setData(sortMachineSendGroups);
        }catch (Exception e){
            e.printStackTrace();
            response.error(null);
        }
        return response;

    }

    /**
     * 根据发货组Id查询该组关联的滑道
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/findSendGroupConfigByGroupId", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineGroupConfig>> findSendGroupConfigByGroupId(Integer groupId){
        InvokeResult<List<SortMachineGroupConfig>> response = new InvokeResult<List<SortMachineGroupConfig>>();
        try{
            List<SortMachineGroupConfig> sendGroupConfigs = sortMachineSendGroupService.
                    findSendGroupConfigByGroupId(groupId);
            response.setData(sendGroupConfigs);
        }catch (Exception e){
            e.printStackTrace();
            response.error(null);
        }
        return response;
    }

    /**
     * 根据分拣机号查询滑槽信息
     * @param machineCode
     * @return
     */
    @RequestMapping(value = "/queryChuteBySortMachineCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<SortMachineBatchSendResult>> queryChuteBySortMachineCode(String machineCode){
        InvokeResult<List<SortMachineBatchSendResult>> response = new InvokeResult<List<SortMachineBatchSendResult>>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        //获取分拣站点失败
        if(bssod == null || bssod.getSiteCode() == null){
            response.parameterError("根据erp活动分拣中心失败！");
            return response;
        }
        //获取分拣中心本地服务url
        String url = PropertiesHelper.newInstance().getValue(prefixKey + bssod.getSiteCode());
        if (StringUtils.isBlank(url)) {
            response.parameterError("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
            return response;
        }
        SortSchemeDetailRequest request = new SortSchemeDetailRequest();
        request.setMachineCode(machineCode);
        request.setSortSchemeYn(1);
        request.setPageNo(1);
        request.setPageSize(MAX_PAGE_SIZE);
        SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> remoteResponse = null;
        remoteResponse = sortSchemeService.pageQuerySortSchemeDetail(request, HTTP + url + "/services/sortSchemeDetail/list");
        if(remoteResponse == null ||
                remoteResponse.getData() == null ||
                remoteResponse.getData().getData() == null ||
                remoteResponse.getData().getData().isEmpty()
                ){
            response.parameterError("根据分拣机编码获取分拣计划详情失败！");
            return response;
        }
        response.setData(getSortMachineBatchSendResult(remoteResponse.getData().getData(),
                machineCode,
                bssod.getSiteCode()));
        return response;
    }

    /**
     *
     * @param sortSchemeDetails
     * @param machineCode
     * @return
     */
    private List<SortMachineBatchSendResult> getSortMachineBatchSendResult(
            List<SortSchemeDetail> sortSchemeDetails ,
            String machineCode,
            Integer siteCode){
        if(sortSchemeDetails == null || sortSchemeDetails.isEmpty()){
            return null;
        }
        //查询批次信息
        ScannerFrameBatchSendSearchArgument scannerArgument = new ScannerFrameBatchSendSearchArgument();
        scannerArgument.setMachineId(machineCode);
        scannerArgument.setEndTime(new Date());
        scannerArgument.setStartTime(DateHelper.add(scannerArgument.getEndTime(), Calendar.HOUR,-24));
        List<ScannerFrameBatchSend> scannerFrameBatchSends = scannerFrameBatchSendService.queryByMachineIdAndTime(scannerArgument);
        if(scannerFrameBatchSends == null){
            logger.error("查询发货批次信息失败machineCode=[" + machineCode + "][EndTime=[" + scannerArgument.getEndTime()
            + "][StartTime=[" + scannerArgument.getStartTime() + "]");
            return null;
        }
        Map<String, ScannerFrameBatchSend> stringScannerFrameBatchSendMap = convert2Map(scannerFrameBatchSends);
        List<SortMachineBatchSendResult> results = new ArrayList<SortMachineBatchSendResult>(sortSchemeDetails.size());
        for(SortSchemeDetail sortSchemeDetail : sortSchemeDetails){
            SortMachineBatchSendResult sortMachineBatchSendResult = new SortMachineBatchSendResult();
            sortMachineBatchSendResult.setSortSchemeDetail(sortSchemeDetail);
            ScannerFrameBatchSend batchSend = stringScannerFrameBatchSendMap.get(siteCode + "-" + sortSchemeDetail.getSendSiteCode());
            String sendCode = batchSend == null ? "" : batchSend.getSendCode();
            Date createTime = batchSend == null ? null : batchSend.getCreateTime();
            sortMachineBatchSendResult.setSendCode(sendCode);
            sortMachineBatchSendResult.setSendCodeCreateTime(createTime);
            results.add(sortMachineBatchSendResult);
        }
        return results;
    }

    /**
     * key 为createSiteCode-receiveSiteCode 方便根据 createSiteCode receiveSiteCode 查询ScannerFrameBatchSend
     * @param scannerFrameBatchSends
     * @return
     */
    private Map<String, ScannerFrameBatchSend> convert2Map(List<ScannerFrameBatchSend> scannerFrameBatchSends){
        if(scannerFrameBatchSends == null || scannerFrameBatchSends.isEmpty()){
            return Collections.EMPTY_MAP;
        }
        Map<String, ScannerFrameBatchSend> resultMap = new HashMap<String, ScannerFrameBatchSend>(scannerFrameBatchSends.size());
        for(ScannerFrameBatchSend scannerFrameBatchSend : scannerFrameBatchSends){
            resultMap.put(scannerFrameBatchSend.getCreateSiteCode() + "-" + scannerFrameBatchSend.getReceiveSiteCode(),
                    scannerFrameBatchSend);
        }
        return resultMap;
    }

    /**
     * 添加发货组
     * @param request
     * @return
     */
    @RequestMapping(value = "/addSendGroup", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult addSendGroup(@RequestBody SortMachineGroupRequest request){
        InvokeResult respone = new InvokeResult();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        try{
            InvokeResult addResult = sortMachineSendGroupService.addSendGroup(request.getMachineCode(),
                    request.getGroupName(), request.getChuteCodes(), erpUser.getStaffNo(), erpUser.getUserName());
            if(addResult.getCode() != 200){
                return addResult;
            }
        }catch (Exception e){
            e.printStackTrace();
            respone.customMessage(500, "添加发货组时系统异常！");
        }

        return respone;
    }

    /**
     * 更新发货组
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateSendGroup", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult updateSendGroup(@RequestBody SortMachineGroupRequest request){
        InvokeResult respone = new InvokeResult();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        try{
             sortMachineSendGroupService.updateSendGroup(request.getGroupId(),
                     request.getMachineCode(),request.getChuteCodes(),
                     erpUser.getStaffNo(), erpUser.getUserName());
        }catch (Exception e){
            e.printStackTrace();
            respone.customMessage(500, "修改发货组时系统异常！");
        }

        return respone;
    }

    /**
     * 更新发货组
     * @param groupId 发货组ID
     * @return
     */
    @RequestMapping(value = "/deleteSendGroup", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult deleteSendGroup(Long groupId){
        InvokeResult respone = new InvokeResult();
        try{
            sortMachineSendGroupService.deleteSendGroup(groupId);
        }catch (Exception e){
            e.printStackTrace();
            respone.customMessage(500, "删除发货组时系统异常！");
        }

        return respone;
    }

    /**
     * 批次完结and打印
     * @param requests
     * @return
     */
    @RequestMapping(value = "/sendEndAndPrint", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<List<BatchSendPrintImageResponse>> sendEndAndPrint(
            @RequestBody ScannerFrameBatchSendPrint[] requests) {
        com.jd.bluedragon.distribution.base.domain.InvokeResult<List<BatchSendPrintImageResponse>> result =
                new com.jd.bluedragon.distribution.base.domain.InvokeResult<List<BatchSendPrintImageResponse>>();
        logger.info("已打印并完结批次动作开始-->打印的分拣机ID为：" + requests[0].getMachineId());
        result.setCode(400);
        result.setMessage("服务调用成功，数据为空");
        result.setData(null);

        String machineId = requests[0].getMachineId();
        Integer printType = requests[0].getPrintType();//打印方式逻辑与：1 批次号打印 2 汇总单 3 两者
        if (StringUtils.isBlank(machineId) || printType == null) {
            result.setCode(200);
            result.setMessage("分拣机参数错误");
            return result;
        }

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "none";//用户erp
        Integer userId = 0;
        String userName = "none";//用户姓名
        if (erpUser != null) {
            try {
                userCode = erpUser.getUserCode() == null ? "none" : erpUser.getUserCode();
                userId = erpUser.getStaffNo() == null ? 0 : erpUser.getStaffNo();
                userName = erpUser.getUserName() == null ? "none" : erpUser.getUserName();
            } catch (Exception e) {
                logger.info("没有在基础资料中维护此erp信息");
                result.setCode(500);
                result.setMessage("没有在基础资料中维护您的登录信息");
                result.setData(null);
                return result;
            }
        }

        ScannerFrameBatchSendSearchArgument sfbssa = new ScannerFrameBatchSendSearchArgument();
        sfbssa.setMachineId(String.valueOf(machineId));//查询参数只有分拣机ID
        Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
        argumentPager.setStartIndex(0);
        argumentPager.setPageSize(Integer.MAX_VALUE);
        argumentPager.setData(sfbssa);
        try {
            Pager<List<ScannerFrameBatchSend>> pagerResult = scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager);//查询该分拣机的所有批次信息
            List<ScannerFrameBatchSend> dataRequestOld = pagerResult.getData();//取所有批次信息
            List<ScannerFrameBatchSend> dataRequest = new ArrayList<ScannerFrameBatchSend>();//取所有批次信息
            if (requests.length > 1) {
                logger.info("本次提交的打印事件不是默认全选事件，需要对选中事件进行打印，选中的条数为：" + (requests.length - 1));
                /** ==============通过request判断是否有选中打印事件=============== **/
                for (ScannerFrameBatchSend data : dataRequestOld) {
                    long itemReceiveSiteCode = data.getReceiveSiteCode();
                    boolean bool = false;
                    for (ScannerFrameBatchSendPrint itemRequest : requests) {
                        if (itemRequest.getReceiveSiteCode() != null
                                && itemReceiveSiteCode == itemRequest.getReceiveSiteCode().longValue()) {
                            bool = true;
                        }
                    }
                    if (bool) {
                        dataRequest.add(data);/** 不是请求的打印数据，则剔除 **/
                    }
                }
                /** ==============判断结束，过滤出将要打印的List===================**/
            } else {
                dataRequest = dataRequestOld;
            }
            logger.info("需要执行该打印并完结批次的条数为：" + dataRequest.size());
            List<BatchSendPrintImageResponse> results = new ArrayList<BatchSendPrintImageResponse>();
            String urlBatchPrint = PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + "/batchSendPrint/sortMachineBatchPrint";
            String urlSummaryPrint = PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + "/batchSendPrint/sortMachineSummaryPrint";
            for (ScannerFrameBatchSend item : dataRequest) {
                if (item.getReceiveSiteCode() == 0) {
                    //没有目的站点，自动退出循环
                    logger.error("检测出该条数据没有目的站点：本条数据丢弃，本次循环退出。");
                    continue;
                }
                /** ===============1.执行换批次动作================== **/
                ScannerFrameBatchSend itemtoEndSend = new ScannerFrameBatchSend();
                logger.info("打印并完结批次-->执行换批次操作：" + item.toString());
                itemtoEndSend.setMachineId(item.getMachineId());
                itemtoEndSend.setCreateSiteCode(item.getCreateSiteCode());
                itemtoEndSend.setCreateSiteName(item.getCreateSiteName());
                itemtoEndSend.setReceiveSiteCode(item.getReceiveSiteCode());
                itemtoEndSend.setReceiveSiteName(item.getReceiveSiteName());
                itemtoEndSend.setPrintTimes((byte) 0);
                itemtoEndSend.setLastPrintTime(null);
                itemtoEndSend.setCreateUserCode((long) userId);
                itemtoEndSend.setCreateUserName(userName);
                itemtoEndSend.setUpdateUserCode((long) userId);
                itemtoEndSend.setUpdateUserName(userName);
                itemtoEndSend.setCreateTime(new Date());
                itemtoEndSend.setUpdateTime(new Date());
                itemtoEndSend.setYn((byte) 1);
                itemtoEndSend.setSendCode(SerialRuleUtil.generateSendCode(itemtoEndSend.getCreateSiteCode(), itemtoEndSend.getReceiveSiteCode(), itemtoEndSend.getCreateTime()));
                boolean bool = scannerFrameBatchSendService.generateSend(itemtoEndSend);
                if (!bool) {
                    logger.error("换批次动作失败：打印跳过该批次：" + item.toString());
                    continue;
                }
                result.setCode(300);
                result.setMessage("服务调用成功,换批次成功，打印失败");
                result.setData(results);
                logger.info("换批次动作实心成功，执行打印获取base64。");
                /** ==================换批次动作执行完毕================ **/
                if ((printType & 1) == 1) {//批次打印逻辑
                    logger.info("分拣机自动发货页面--批次打印开始");
                    BatchSendPrintImageResponse itemSendCodeResponse = scannerFrameBatchSendService.batchPrint(urlBatchPrint, item, userId, userName);
                    itemSendCodeResponse.setPrintType(SENDCODE_PRINT_TYPE);//批次打印单
                    results.add(itemSendCodeResponse);
                }
                if ((printType & 2) == 2) {//汇总打印逻辑
                    logger.info("分拣机自动发货页面-打印汇总单开始");
                    BatchSendPrintImageResponse itemSummaryResponse = scannerFrameBatchSendService.summaryPrint(urlSummaryPrint, item, userId, userName);
                    itemSummaryResponse.setPrintType(SUMMARY_PRINT_TYPE);//汇总打印单
                    results.add(itemSummaryResponse);
                }
                logger.info("本次打印结束，获取的图片个数为：" + results.size());
            }
            result.setCode(200);
            result.setMessage("服务调用成功");
            result.setData(results);
        } catch (Exception e) {
            logger.error("获取数据异常");
            result.setCode(500);
            result.setMessage("服务调用异常");
        }
        return result;
    }

    @RequestMapping(value = "/queryExceptionNum", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer> queryExceptionNum(@RequestBody SendExceptionRequest request) {
        this.logger.debug("获取分拣机发货异常信息 --> queryExceptionNum");
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer> result =
                new com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer>();
        try {
            Integer count = gantryExceptionService.getGantryExceptionCount(request.getMachineId(),
                    request.getStartTime(), request.getEndTime());
            result.setData(count);
        } catch (NullPointerException e) {
            logger.error("获取分拣机自动发货异常数据失败，分拣机ID为：" + request.getMachineId());
        }
        return result;
    }

    @RequestMapping(value = "/generateSendCode", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer> generateSendCode(@RequestBody ScannerFrameBatchSend[] lists) {
        this.logger.debug("分拣机自动换批次 --> changeSendCode");
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer> result = new com.jd.bluedragon.distribution.base.domain.InvokeResult<Integer>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        Integer userCode = 0;//用户编号
        String userName = "none";//用户姓名
        if (erpUser != null) {
            userCode = erpUser.getStaffNo() == null ? 0 : erpUser.getStaffNo();
            userName = erpUser.getUserName() == null ? "none" : erpUser.getUserName();
        }
        try {
            for (ScannerFrameBatchSend item : lists) {
                ScannerFrameBatchSend scannerFrameBatchSend = scannerFrameBatchSendService.selectCurrentBatchSend(item.getMachineId(), item.getReceiveSiteCode(), item.getCreateTime());
                scannerFrameBatchSend.setPrintTimes((byte) 0);
                scannerFrameBatchSend.setLastPrintTime(null);
                scannerFrameBatchSend.setCreateUserCode(userCode);
                scannerFrameBatchSend.setCreateUserName(userName);
                scannerFrameBatchSend.setUpdateUserCode(Long.valueOf(userCode));
                scannerFrameBatchSend.setUpdateUserName(userName);
                scannerFrameBatchSend.setCreateTime(new Date());
                scannerFrameBatchSend.setUpdateTime(new Date());
                scannerFrameBatchSend.setYn((byte) 1);
                scannerFrameBatchSend.setSendCode(SerialRuleUtil.generateSendCode(scannerFrameBatchSend.getCreateSiteCode(), scannerFrameBatchSend.getReceiveSiteCode(), scannerFrameBatchSend.getCreateTime()));
                boolean bool = scannerFrameBatchSendService.generateSend(scannerFrameBatchSend);
                if (!bool) {
                    result.setCode(500);
                    result.setMessage("部分批次转换失败，失败原始批次为：" + item.getSendCode());
                    return result;
                } else {
                    result.setCode(200);
                    result.setMessage("换批次成功");
                }
            }
        } catch (Exception e) {
            logger.error("生产新的批次号失败", e);
        }
        return result;
    }


    @RequestMapping(value = "/replenishPrintIndex", method = RequestMethod.GET)
    public String index(Model model, String machineId,
                        Integer createSiteCode,
                        String createSiteName,
                        String startTime,
                        String endTime) {
        if (machineId != null && createSiteCode != null) {
            model.addAttribute("machineId", machineId);
            model.addAttribute("createSiteCode", createSiteCode);
            try {
                model.addAttribute("createSiteName", URLDecoder.decode(createSiteName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.info("补打界面跳转参数解码异常", e);
                model.addAttribute("createSiteName", "未知分拣中心");
            }
            Date nowTime = new Date();
            endTime = StringUtils.isBlank(endTime) ? DateUtil.format(nowTime, DateUtil.FORMAT_DATE_TIME) : endTime;
            if(StringUtils.isBlank(startTime)){
                Date startDateTime = DateHelper.add(nowTime, Calendar.HOUR,-24);
                startTime = DateUtil.format(startDateTime, DateUtil.FORMAT_DATE_TIME);
            }
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
        }
        return "/sortMachine/replenishPrint";
    }

    @RequestMapping(value = "/summaryBySendCode", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<GantryBatchSendResult> summaryBySendCode(String sendCode) {
        com.jd.bluedragon.distribution.base.domain.InvokeResult<GantryBatchSendResult> result = new com.jd.bluedragon.distribution.base.domain.InvokeResult<GantryBatchSendResult>();
        result.setCode(500);
        result.setMessage("服务器处理异常");
        if (sendCode != null) {
            List<SendDetail> sendDetailList = gantryDeviceService.queryBoxCodeBySendCode(sendCode);
            GantryBatchSendResult sendBoxSum = new GantryBatchSendResult();
            Integer packageSum = 0;//批次总包裹数量
            Double volumeSum = 0.00;//取分拣体积
            if (sendDetailList != null && sendDetailList.size() > 0) {
                HashSet<String> sendDByBoxCode = new HashSet<String>();
                for (SendDetail sendD : sendDetailList) {
                    //根据sendD的boxCode去重
                    try {
                        if (sendDByBoxCode.contains(sendD.getBoxCode())) {
                            continue;
                        }
                        sendDByBoxCode.add(sendD.getBoxCode());
                        WaybillPackageDTO waybillPackageDTO = waybillService.getWaybillPackage(sendD.getBoxCode());
                        if (waybillPackageDTO == null) {
                            continue;
                        }
                        volumeSum += waybillPackageDTO.getVolume() == 0 ? waybillPackageDTO.getOriginalVolume() : waybillPackageDTO.getVolume();
                    } catch (Exception e) {
                        logger.error("获取批次的总数量和总体积失败：批次号为" + sendCode, e);
                    }
                }
                packageSum = sendDetailList.size();//获取包裹的数量
            }
            BigDecimal bg = new BigDecimal(volumeSum).setScale(2, RoundingMode.UP);//四舍五入;保留两位有效数字
            sendBoxSum.setSendCode(sendCode);
            sendBoxSum.setPackageSum(packageSum);
            sendBoxSum.setVolumeSum(bg.doubleValue());
            result.setCode(200);
            result.setData(sendBoxSum);
            result.setMessage("获取批次号的总数量和总体积成功");
        } else {
            logger.error("获取参数批次的总体积和总数量失败：批次号为空");
            result.setCode(400);
            result.setMessage("参数错误");
            result.setData(null);
        }
        return result;
    }

}
