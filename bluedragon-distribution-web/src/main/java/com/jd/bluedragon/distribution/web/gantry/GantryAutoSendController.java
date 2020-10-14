package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.request.SendExceptionRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendPrintImageResponse;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanDetailService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanService;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendPrint;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.gantry.domain.GantryBatchSendResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.UsingState;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2016/12/7.
 */
@Controller
@RequestMapping("/gantryAutoSend")
public class GantryAutoSendController {

    private static final Logger log = LoggerFactory.getLogger(GantryAutoSendController.class);

    private final static String PREFIX_VER_URL = "DMSVER_ADDRESS";

    private final static int MAX_DATA_TO_PRINT = 500;//最大打印的数据条数

    private final static int SENDCODE_PRINT_TYPE = 1;//批次打印

    private final static int SUMMARY_PRINT_TYPE = 2;//汇总单打印

    @Autowired
    BaseMajorManager baseMajorManager;

    @Autowired
    ScannerFrameBatchSendService scannerFrameBatchSendService;

    @Autowired
    GantryDeviceConfigService gantryDeviceConfigService;

    @Autowired
    GantryExceptionService gantryExceptionService;

    @Autowired
    WaybillService waybillService;

    @Autowired
    GantryDeviceService gantryDeviceService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private AreaDestPlanService areaDestPlanService;

    @Autowired
    private AreaDestPlanDetailService areaDestPlanDetailService;

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        this.log.debug("龙门架自动发货 --> index");
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if (erpUser != null) {
                String userCode = "";
                String userName = "";
                Integer siteCode = 0;
                String siteName = "";
                userCode = erpUser.getUserCode() == null ? "none" : erpUser.getUserCode();
                userName = erpUser.getUserName() == null ? "none" : erpUser.getUserName();
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if (bssod != null && BusinessUtil.isDistrubutionCenter(bssod.getSiteType())) {/** 站点类型为64的时候为分拣中心 **/
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
                model.addAttribute("siteCode", String.valueOf(siteCode));
                model.addAttribute("siteName", siteName);
//                model.addAttribute("userCode", userCode);
                model.addAttribute("userNameAndCode", userName + "||" + userCode);
            }
        } catch (Exception e) {
            log.error("没有维护分拣中心，初始化加载失败",e);
        }
        return "gantry/gantryAutoSendIndex";
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/updateOrInsertGantryDeviceStatus", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<GantryDeviceConfig> UpsertGantryDeviceBusinessOrStatus(GantryDeviceConfigRequest request) {
        InvokeResult<GantryDeviceConfig> result = new InvokeResult<GantryDeviceConfig>();
        result.setCode(500);
        result.setMessage("参数异常");
        result.setData(null);
        /** 获取操作人的信息 **/
        String userCode = "";//erp账号
        String userName = "";//姓名
        Integer userId = 0;//员工ID
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                userCode = erpUser.getUserCode() == null ? null : erpUser.getUserCode();
                userName = erpUser.getUserName() == null ? null : erpUser.getUserName();
                userId = erpUser.getStaffNo() == null ? null : erpUser.getStaffNo();
            }
            log.debug(userName + "试图修改或插入龙门架的状态 --> UpsertGantryDeviceBusinessOrStatus ");
        } catch (Exception e) {
            log.error("无法从cookie中获取登录人的信息",e);
        }
        if (null == request || request.getMachineId() == null) {
            log.warn("没有需要修改的龙门架设备信息");
            return null;
        }
        GantryDeviceConfig gantryDeviceConfig = null;
        gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
        if (request.getLockStatus() == 0) {/** 解锁龙门架操作 **/
            log.info("用户：{}正在尝试解锁龙门架，ID为{}",userCode, request.getMachineId());
            try {
                    gantryDeviceConfig.setLockStatus(request.getLockStatus());
                    gantryDeviceConfig.setEndTime(new Date());//解锁动作设置结束时间
                    gantryDeviceConfig.setOperateUserErp(userCode);//设置操作人员与更新人员
                    gantryDeviceConfig.setOperateUserId(userId);
                    gantryDeviceConfig.setOperateUserName(userName);
                    gantryDeviceConfig.setUpdateUserErp(userCode);
                    gantryDeviceConfig.setUpdateUserName(userName);
                    int i = gantryDeviceConfigService.unlockDevice(gantryDeviceConfig);
                    if (i > -1) {
                        if ((request.getBusinessType() & 2) == 2) {
                            Long planId = request.getPlanId();
                            if (planId != null) {
                                // 更新方案使用状态为停用
                                areaDestPlanService.updateUsingState(Integer.valueOf(planId.toString()), UsingState.NOT_USED);
                            }
                        }
                        result.setCode(200);
                        result.setMessage("释放龙门架状态成功");
                        result.setData(gantryDeviceConfig);
                    } else {
                        result.setCode(500);
                        result.setMessage("处理龙门架参数状态错误，更新失败");
                        result.setData(gantryDeviceConfig);
                    }
            } catch (Exception e) {
                log.error("服务器处理异常：", e);
            }
        } else if (request.getLockStatus() == 1) {/** 锁定龙门架操作 **/
            gantryDeviceConfig = new GantryDeviceConfig();
            /**  config表中没有数据说明此龙门架是第一次添加，需要进行初始化所有字段数据数据 **/
            if (log.isInfoEnabled()) {
                log.info("用户{}正在尝试第一次配置该龙门架设备ID：{}",userName, request.getMachineId());
            }
            gantryDeviceConfig.setMachineId(request.getMachineId().toString());
            gantryDeviceConfig.setCreateSiteCode(request.getCreateSiteCode());
            gantryDeviceConfig.setYn(1);
            if (request.getBusinessType() == 4 || request.getBusinessType() == 3 || request.getBusinessType() == 7) {
                //龙门架操作类型错误
                result.setCode(400);
                result.setMessage("龙门架功能配置错误，发货验货不可同时使用、量方不可单独使用");
                result.setData(null);
                return result;
            }
            int count = 0;
            try {
                BaseStaffSiteOrgDto site = siteService.getSite(request.getCreateSiteCode());
                if (null != site) {
                    gantryDeviceConfig.setCreateSiteName(site.getSiteName());//根据createSiteName 读取一次分拣中心的名字(优化代码)
                }
                gantryDeviceConfig.setOperateUserErp(userCode);
                gantryDeviceConfig.setOperateUserId(userId);
                gantryDeviceConfig.setOperateUserName(userName);
                gantryDeviceConfig.setUpdateUserErp(userCode);
                gantryDeviceConfig.setUpdateUserName(userName);
                gantryDeviceConfig.setLockUserErp(userCode);
                gantryDeviceConfig.setLockUserName(userName);
                gantryDeviceConfig.setBusinessType(request.getBusinessType());
                gantryDeviceConfig.setBusinessTypeRemark(request.getOperateTypeRemark());
                gantryDeviceConfig.setLockStatus(request.getLockStatus());
                gantryDeviceConfig.setStartTime(new Date());
                count = gantryDeviceConfigService.add(gantryDeviceConfig);
                if ((request.getBusinessType() & 2) == 2) {
                    Boolean j = areaDestPlanService.modifyGantryPlan(request.getMachineId(), request.getPlanId(), userId, request.getCreateSiteCode());
                    if (j) {
                        Long planId = request.getPlanId();
                        if (planId != null) {
                            // 更新方案使用状态为启用
                            areaDestPlanService.updateUsingState(Integer.valueOf(planId.toString()), UsingState.USING);
                        }
                    } else {
                        this.log.warn("锁定龙门架的方案失败");
                    }
                }


            } catch (Exception e) {
                log.error("锁定龙门架操作失败..", e);
            }
            if (count >= 1) {
                log.warn("用户正在尝试的启用龙门架操作状态成功，龙门架ID：{} 操作人：{}" ,request.getMachineId(), userName);
                result.setCode(200);
                result.setMessage("用户锁定龙门架操作成功");
                result.setData(gantryDeviceConfig);
            } else {
                log.warn("用户正在尝试的启用龙门架操作状态异常失败，龙门架ID：{} 操作人：{}",request.getMachineId(), userName);
                result.setCode(400);
                result.setMessage("用户锁定龙门架失败");
                result.setData(null);
            }
        }

        return result;

    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<ScannerFrameBatchSend>>> currentSplitPageList(GantryDeviceConfigRequest request, Pager<GantryDeviceConfigRequest> pager) {
        InvokeResult<Pager<List<ScannerFrameBatchSend>>> result = new InvokeResult<Pager<List<ScannerFrameBatchSend>>>();
        result.setCode(400);
        result.setMessage("服务调用异常");
        result.setData(null);
        log.debug("龙门架自动发货获取数据 --> getCurrentSplitPageList");
        if (request.getMachineId() == null) {
            return result;
        }

        ScannerFrameBatchSendSearchArgument sfbssa = new ScannerFrameBatchSendSearchArgument();
        Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
        if (pager.getPageNo() != null) {
            argumentPager.setPageNo(pager.getPageNo());
            argumentPager.init();
        }
        sfbssa.setMachineId(String.valueOf(request.getMachineId()));
//        sfbssa.setPlanId(request.getPlanId());
        sfbssa.setStartTime(new Date(new Date().getTime()-3*24*60*60*1000));
        sfbssa.setHasPrinted(false);
        sfbssa.setYn(1);
        argumentPager.setData(sfbssa);
        try {
            Pager<List<ScannerFrameBatchSend>> pagerResult = scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager);
            result.setCode(200);
            result.setMessage("服务器调用处理成功");
            result.setData(pagerResult);
        } catch (Exception e) {
            log.error("处理请求数据失败！", e);
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/summaryBySendCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<GantryBatchSendResult> summaryBySendCode(String sendCode) {
        InvokeResult<GantryBatchSendResult> result = new InvokeResult<GantryBatchSendResult>();
        result.setCode(500);
        result.setMessage("服务器处理异常");
        if (sendCode != null) {
            try {
                result.setData(gantryDeviceService.getSummaryVolumeBySendCode(sendCode));
            } catch (Exception e) {
                log.error("获取批次的总数量和总体积失败：批次号为:{}", sendCode, e);
            }
            result.setCode(200);
            result.setMessage("获取批次号的总数量和总体积成功");
        } else {
            log.error("获取参数批次的总体积和总数量失败：批次号为空");
            result.setCode(400);
            result.setMessage("参数错误");
            result.setData(null);
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/generateSendCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> generateSendCode(@RequestBody ScannerFrameBatchSend[] lists) {
        log.debug("龙门架自动换批次 --> changeSendCode");
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        result.setCode(400);
        result.setMessage("服务器处理异常，换批次失败！");
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        Integer userCode = 0;//用户编号
        String userName = "none";//用户姓名
        if (erpUser != null) {
            userCode = erpUser.getStaffNo() == null ? 0 : erpUser.getStaffNo();
            userName = erpUser.getUserName() == null ? "none" : erpUser.getUserName();
        }
        if (null == lists || lists.length == 0){
            result.setCode(10000);
            result.setMessage("参数为空");
            return result;
        }
        try {
            Map<String ,Object> param = new HashMap<String, Object>();
            param.put("machineId",lists[0].getMachineId());
            param.put("version",1);//版本号1表示的是自动发货的新版本，可以去掉这个限制，加上是防止龙门架注册的时候，两个版本用了同一个machineId
            List<GantryDevice> gantrys = gantryDeviceService.getGantry(param);
            if (null == gantrys || gantrys.size() == 0){
                result.setCode(400);
                result.setMessage("龙门架的配置信息为空");
                return result;
            }
            for (ScannerFrameBatchSend item : lists) {
                ScannerFrameBatchSend scannerFrameBatchSend = new ScannerFrameBatchSend();
                scannerFrameBatchSend.setMachineId(item.getMachineId());
                scannerFrameBatchSend.setCreateSiteCode(gantrys.get(0).getSiteCode());
                scannerFrameBatchSend.setCreateSiteName(gantrys.get(0).getSiteName());
                scannerFrameBatchSend.setReceiveSiteCode(item.getReceiveSiteCode());
                BaseStaffSiteOrgDto site =  siteService.getSite((int) item.getReceiveSiteCode());//理论上会存在溢出的风险，但是实际站点没有那么大的，故可以忽略
                if (null == site) {
                    log.warn("该站点已经失效：{}", item.getReceiveSiteCode());
                    continue;
                }
                scannerFrameBatchSend.setReceiveSiteName(site.getSiteName());//通过基础资料获取站点名称
                scannerFrameBatchSend.setPrintTimes((byte) 0);
                scannerFrameBatchSend.setCreateUserCode(userCode);
                scannerFrameBatchSend.setCreateUserName(userName);
                scannerFrameBatchSend.setUpdateUserCode(Long.valueOf(userCode));
                scannerFrameBatchSend.setUpdateUserName(userName);
                scannerFrameBatchSend.setLastPrintTime(null);
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
            log.error("生产新的批次号失败", e);
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/queryExceptionNum", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> queryExceptionNum(SendExceptionRequest request) {
        log.debug("获取龙门架异常信息 --> queryExceptionNum");
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        result.setData(0);
        if (null == request || StringUtils.isBlank(request.getMachineId())) {
            log.warn("龙门架参数异常，获取异常数据失败");
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            Integer count = gantryExceptionService.getGantryExceptionCount( request.getMachineId(), request.getStartTime(), request.getEndTime());
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setMessage("龙门架异常数据获取成功");
            result.setData(count);
        } catch (Exception e) {
            log.error("获取龙门架自动发货异常数据失败，龙门架ID为：{}", request.getMachineId());
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/sendEndAndPrint", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<BatchSendPrintImageResponse>> sendEndAndPrint(@RequestBody ScannerFrameBatchSendPrint[] requests) {
        InvokeResult<List<BatchSendPrintImageResponse>> result = new InvokeResult<List<BatchSendPrintImageResponse>>();
        log.info("已打印并完结批次动作开始-->打印的龙门架ID为：{}", requests[0].getMachineId());
        result.setCode(400);
        result.setMessage("服务调用成功，数据为空");
        result.setData(null);

        String machineId = requests[0].getMachineId();
        Integer printType = requests[0].getPrintType();//打印方式逻辑与：1 批次号打印 2 汇总单 3 两者
        if (StringUtils.isBlank(machineId) || printType == null) {
            result.setCode(200);
            result.setMessage("龙门架参数错误");
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
                log.info("没有在基础资料中维护此erp信息");
                result.setCode(500);
                result.setMessage("没有在基础资料中维护您的登录信息");
                result.setData(null);
                return result;
            }
        }

        ScannerFrameBatchSendSearchArgument sfbssa = new ScannerFrameBatchSendSearchArgument();
        sfbssa.setMachineId(machineId);//查询参数只有龙门架ID
        Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
        argumentPager.setStartIndex(0);
        argumentPager.setPageSize(MAX_DATA_TO_PRINT);
        argumentPager.setData(sfbssa);
        try {
            Pager<List<ScannerFrameBatchSend>> pagerResult = scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager);//查询该龙门架的所有批次信息
            List<ScannerFrameBatchSend> dataRequestOld = pagerResult.getData();//取所有批次信息
            List<ScannerFrameBatchSend> dataRequest = new ArrayList<ScannerFrameBatchSend>();//取所有批次信息
            if (requests.length > 1) {
                log.info("本次提交的打印事件不是默认全选事件，需要对选中事件进行打印，选中的条数为：{}",  (requests.length - 1));
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
            log.info("需要执行该打印并完结批次的条数为：{}", dataRequest.size());
            List<BatchSendPrintImageResponse> results = new ArrayList<BatchSendPrintImageResponse>();
            String urlBatchPrint = PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + "/batchSendPrint/print";
            String urlSummaryPrint = PropertiesHelper.newInstance().getValue(PREFIX_VER_URL) + "/batchSendPrint/summaryPrint";
            for (ScannerFrameBatchSend item : dataRequest) {
                if (item.getReceiveSiteCode() == 0) {
                    //没有目的站点，自动退出循环
                    log.warn("检测出该条数据没有目的站点：本条数据丢弃，本次循环退出。");
                    continue;
                }
                /** ===============1.执行换批次动作================== **/
                ScannerFrameBatchSend itemtoEndSend = new ScannerFrameBatchSend();
                log.info("打印并完结批次-->执行换批次操作：{}", item.toString());
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
                    log.warn("换批次动作失败：打印跳过该批次：{}", item.toString());
                    continue;
                }
                result.setCode(300);
                result.setMessage("服务调用成功,换批次成功，打印失败");
                result.setData(results);
                log.info("换批次动作实心成功，执行打印获取base64。");
                /** ==================换批次动作执行完毕================ **/
                if ((printType & 1) == 1) {//批次打印逻辑
                    log.info("龙门架自动发货页面--批次打印开始");
                    BatchSendPrintImageResponse itemSendCodeResponse = scannerFrameBatchSendService.batchPrint(urlBatchPrint, item, userId, userName);
                    itemSendCodeResponse.setPrintType(SENDCODE_PRINT_TYPE);//批次打印单
                    results.add(itemSendCodeResponse);
                }
                if ((printType & 2) == 2) {//汇总打印逻辑
                    log.info("龙门架自动发货页面-打印汇总单开始");
                    BatchSendPrintImageResponse itemSummaryResponse = scannerFrameBatchSendService.summaryPrint(urlSummaryPrint, item, userId, userName);
                    itemSummaryResponse.setPrintType(SUMMARY_PRINT_TYPE);//汇总打印单
                    results.add(itemSummaryResponse);
                }
                log.info("本次打印结束，获取的图片个数为：{}", results.size());
            }
            result.setCode(200);
            result.setMessage("服务调用成功");
            result.setData(results);
        } catch (Exception e) {
            log.error("获取数据异常");
            result.setCode(500);
            result.setMessage("服务调用异常");
        }
        return result;
    }

    /**
     * 根据ERP账号更新龙门架操作用户ERP编号为青龙用户编号
     *
     * @param userErp 为all时更新所有
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
    @RequestMapping(value = "/updateUserCodeToStaffNo/{userErp}", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult updateUserCodeToStaffNo(@PathVariable String userErp) {
        InvokeResult invokeResult = new InvokeResult();
        try {
            if (StringUtils.isNotEmpty(userErp)) {
                if ("all".equals(userErp)) {
                    List<String> erpList = gantryDeviceConfigService.getAllOperateUserErp();
                    if (erpList != null && erpList.size() > 0) {
                        for (String erp : erpList) {
                            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erp);
                            if (dto != null) {
                                gantryDeviceConfigService.updateOperateUserIdByErp(erp, dto.getStaffNo());
                            }
                        }
                        invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                        invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                        return invokeResult;
                    }
                    invokeResult.setCode(InvokeResult.RESULT_NULL_CODE);
                    invokeResult.setMessage("无有效的龙门架配置记录");
                    return invokeResult;
                } else {
                    BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(userErp);
                    if (dto != null) {
                        gantryDeviceConfigService.updateOperateUserIdByErp(userErp, dto.getStaffNo());
                        invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                        invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                    } else {
                        invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                        invokeResult.setMessage("无效Erp账号");
                    }
                    return invokeResult;
                }
            }
            invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.PARAM_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.SERVER_ERROR_MESSAGE + e.getMessage());
        }
        return invokeResult;
    }
}
