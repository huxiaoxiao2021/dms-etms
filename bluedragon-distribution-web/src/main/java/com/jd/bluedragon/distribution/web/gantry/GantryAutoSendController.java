package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.BatchSendPrintImageRequest;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.response.BatchSendPrintImageResponse;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendPrint;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.gantry.domain.GantryBatchSendResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/12/7.
 */
@Controller
@RequestMapping("/gantryAutoSend")
public class GantryAutoSendController {

    private static final Log logger = LogFactory.getLog(GantryAutoSendController.class);

    private final static String HTTP = "http://";

    private final static String prefixKey = "HeadquartersIp";

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

    @RequestMapping(value = "/index" ,method = RequestMethod.GET)
    public String index(Model model){
        this.logger.debug("龙门架自动发货 --> index");
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if(erpUser != null){
            String userCode = "";
            String userName = "";
            Integer siteCode = 0;
            String siteName = "";
            userCode = erpUser.getUserCode() == null ? "none":erpUser.getUserCode();
            userName = erpUser.getUserName() == null ? "none":erpUser.getUserName();
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
            if(bssod.getSiteType() == 64){/** 站点类型为64的时候为分拣中心 **/
                siteCode = bssod.getSiteCode();
                siteName = bssod.getSiteName();
            }
            model.addAttribute("siteCode",String.valueOf(siteCode));
            model.addAttribute("siteName",siteName);
            model.addAttribute("userCode", userCode);
            model.addAttribute("userName", userName);
        }
        return "gantry/gantryAutoSendIndex";
    }

    @RequestMapping(value = "/updateOrInsertGantryDeviceStatus", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<GantryDeviceConfig> UpsertGantryDeviceBusinessOrStatus(GantryDeviceConfigRequest request){
        InvokeResult<GantryDeviceConfig> result = new InvokeResult<GantryDeviceConfig>();
        result.setCode(500);
        result.setMessage("参数异常");
        result.setData(null);
        /** 获取操作人的信息 **/
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "";//erp账号
        String userName = "";//姓名
        Integer userId = null;//员工ID
        if(erpUser != null){
            userCode = erpUser.getUserCode() == null ? null:erpUser.getUserCode();
            userName = erpUser.getUserName() == null ? null:erpUser.getUserName();
            userId = erpUser.getUserId() == null ? null:erpUser.getUserId();
        }
        logger.debug(userName + "试图修改或插入龙门架的状态 --> UpsertGantryDeviceBusinessOrStatus ");
        if(null == request && request.getMachineId() == null){
            logger.error("没有需要修改的龙门架设备信息");
            return null;
        }
        GantryDeviceConfig  gantryDeviceConfig = null;
        gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
        if(request.getLockStatus() == 0){/** 解锁龙门架操作 **/
            /** 第一步：找到gantry_device_config最新的一条龙门架记录 **/
            logger.info("用户：" + userCode + "正在尝试解锁龙门架，ID为" + request.getMachineId());
            try{
                if(gantryDeviceConfig.getLockUserErp().equals(userCode)){
                    //只更新该龙门架的锁定状态为0解锁
                    gantryDeviceConfig.setLockStatus(request.getLockStatus());
                    int i = gantryDeviceConfigService.updateLockStatus(gantryDeviceConfig);
                    if( i > -1){
                        result.setCode(200);
                        result.setMessage("释放龙门架状态成功");
                        result.setData(gantryDeviceConfig);
                    }else{
                        result.setCode(500);
                        result.setMessage("处理龙门架参数状态错误，更新失败");
                        result.setData(gantryDeviceConfig);
                    }
                }else{
                    logger.info("此用户无法解锁由别人锁定的龙门架设备；解锁人"+ userName +"锁定人"+gantryDeviceConfig.getLockUserName());
                    result.setCode(1000);
                    result.setMessage("解锁失败，请联系锁定人" + gantryDeviceConfig.getLockUserErp() + "解锁" );
                    result.setData(gantryDeviceConfig);
                }
            }catch (Exception e){
                logger.error("服务器处理异常：",e);
            }
        }else if(request.getLockStatus() == 1){/** 锁定龙门架操作 **/
            logger.info("用户：" + userCode + "正在锁定龙门架，龙门架ID为："
                    + request.getMachineId() + "锁定龙门架的业务类型为：" + request.getBusinessType() + request.getOperateTypeRemark());
            /** 转换类型 修改最近的一条龙门设备的信息：操作人，更新人，锁定人，业务类型，锁定状态，startTime为now，endTime置为空 新插入 **/
            if(request.getBusinessType() == 4 || request.getBusinessType() == 3 || request.getBusinessType() == 7){
                //龙门架操作类型错误
                result.setCode(400);
                result.setMessage("龙门架功能配置错误，发货验货不可同时使用、量方不可单独使用");
                result.setData(null);
                return result;
            }
            int count = 0;
            try{
                BaseStaffSiteOrgDto site=siteService.getSite(request.getCreateSiteCode());
                if(null!=site) {
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
            }catch(Exception e){
                logger.error("锁定龙门架操作失败..",e);
            }
            if (count >= 1) {
                logger.error("用户正在尝试的启用龙门架操作状态成功，龙门架ID：" + request.getMachineId() + " 操作人：" + userName);
                result.setCode(200);
                result.setMessage("用户锁定龙门架操作成功");
                result.setData(gantryDeviceConfig);
            }else{
                logger.error("用户正在尝试的启用龙门架操作状态异常失败，龙门架ID：" + request.getMachineId() + " 操作人：" + userName);
                result.setCode(400);
                result.setMessage("用户锁定龙门架失败");
                result.setData(null);
            }
        }else{
            logger.error("用户正在尝试的启用、释放龙门架操作状态异常，已经终止..");
        }
        return result;
    }

    @RequestMapping(value = "/pageList",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<ScannerFrameBatchSend>>> currentSplitPageList(GantryDeviceConfigRequest request , Pager<GantryDeviceConfigRequest> pager){
        InvokeResult<Pager<List<ScannerFrameBatchSend>>> result = new InvokeResult<Pager<List<ScannerFrameBatchSend>>>();
        result.setCode(500);
        result.setMessage("服务调用异常");
        result.setData(null);
        this.logger.debug("龙门架自动发货获取数据 --> getCurrentSplitPageList");
        if(request.getMachineId() == null){
            return result;
        }
        ScannerFrameBatchSendSearchArgument sfbssa = new ScannerFrameBatchSendSearchArgument();
        Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
        if(pager.getPageNo() != null){
            argumentPager.setPageNo(pager.getPageNo());
            argumentPager.init();
        }
        sfbssa.setMachineId(request.getMachineId());
//        sfbssa.setStartTime(request.getStartTime());
//        sfbssa.setEndTime(request.getEndTime());
        argumentPager.setData(sfbssa);
        try{
            Pager<List<ScannerFrameBatchSend>> pagerResult = scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager);
            result.setCode(200);
            result.setMessage("服务器调用处理成功");
            result.setData(pagerResult);
        }catch(Exception e){
            logger.error("处理请求数据失败！",e);
        }
        return result;
    }

    @RequestMapping(value = "/summaryBySendCode", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<GantryBatchSendResult> summaryBySendCode(String sendCode){
        InvokeResult<GantryBatchSendResult> result = new InvokeResult<GantryBatchSendResult>();
        result.setCode(500);
        result.setMessage("服务器处理异常");
        if(sendCode != null){
            List<SendDetail> sendDetailList = gantryDeviceService.queryWaybillsBySendCode(sendCode);
            GantryBatchSendResult sendBoxSum = new GantryBatchSendResult();
            Integer packageSum = 0;//批次总包裹数量
            Double volumeSum = 0.00;//取分拣体积
            if(sendDetailList != null && sendDetailList.size() > 0){
                for (SendDetail sendD : sendDetailList){
                    try{
                        WaybillPackageDTO waybillPackageDTO = waybillService.getWaybillPackage(sendD.getPackageBarcode());
                        volumeSum += waybillPackageDTO.getVolume() == 0? waybillPackageDTO.getOriginalVolume():waybillPackageDTO.getVolume();
                    }catch(Exception e){
                        logger.error("获取批次的总数量和总体积失败：批次号为"+sendCode,e);
                    }
                }
                packageSum = sendDetailList.size();//获取包裹的数量
            }
            sendBoxSum.setSendCode(sendCode);
            sendBoxSum.setPackageSum(packageSum);
            sendBoxSum.setVolumeSum(volumeSum);
            result.setCode(200);
            result.setData(sendBoxSum);
            result.setMessage("获取批次号的总数量和总体积成功");
        }else{
            logger.error("获取参数批次的总体积和总数量失败：批次号为空");
            result.setCode(400);
            result.setMessage("参数错误");
            result.setData(null);
        }
        return result;
    }

    @RequestMapping(value = "/generateSendCode" , method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> generateSendCode( @RequestBody ScannerFrameBatchSend[] lists){
        this.logger.debug("龙门架自动换批次 --> changeSendCode");
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        result.setCode(400);
        result.setMessage("服务器处理异常，换批次失败！");
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        Integer userCode = 0;//用户编号
        String userName = "none";//用户姓名
        if(erpUser != null){
            userCode = erpUser.getUserId() == null ? 0:erpUser.getUserId();
            userName = erpUser.getUserName() == null ? "none":erpUser.getUserName();
        }
        try {
            for(ScannerFrameBatchSend item:lists){
                // // FIXME: 2016/12/21  是否可以不读库，读库为了保险
                ScannerFrameBatchSend scannerFrameBatchSend = scannerFrameBatchSendService.selectCurrentBatchSend(item.getMachineId(),item.getReceiveSiteCode(),item.getCreateTime());
                scannerFrameBatchSend.setPrintTimes((byte)0);
                scannerFrameBatchSend.setLastPrintTime(null);
                scannerFrameBatchSend.setCreateUserCode(userCode);
                scannerFrameBatchSend.setCreateUserName(userName);
                scannerFrameBatchSend.setUpdateUserCode(Long.valueOf(userCode));
                scannerFrameBatchSend.setUpdateUserName(userName);
                scannerFrameBatchSend.setCreateTime(new Date());
                scannerFrameBatchSend.setUpdateTime(new Date());
                scannerFrameBatchSend.setYn((byte)1);
                scannerFrameBatchSend.setSendCode(SerialRuleUtil.generateSendCode(scannerFrameBatchSend.getCreateSiteCode(),scannerFrameBatchSend.getReceiveSiteCode(),scannerFrameBatchSend.getCreateTime()));
                boolean bool = scannerFrameBatchSendService.generateSend(scannerFrameBatchSend);
                if(!bool){
                    result.setCode(500);
                    result.setMessage("部分批次转换失败，失败原始批次为：" + item.getSendCode());
                    return result;
                }else{
                    result.setCode(200);
                    result.setMessage("换批次成功");
                }
            }
        }catch(Exception e){
            logger.error("生产新的批次号失败",e);
        }
        return result;
    }

    @RequestMapping(value = "/queryExceptionNum", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> queryExceptionNum(GantryDeviceConfigRequest request){
        this.logger.debug("获取龙门架异常信息 --> queryExceptionNum");
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        result.setCode(400);
        result.setMessage("服务处理异常");
        result.setData(0);
        if(null == request){
            logger.error("龙门架参数异常，获取异常数据失败");
        }
        try{
            if(null != request.getMachineId()){
                Integer count = gantryExceptionService.getGantryExceptionCount((long)request.getMachineId(),request.getStartTime(),request.getEndTime());
                result.setCode(200);
                result.setMessage("龙门架异常数据获取成功");
                result.setData(count);
            }else{
                logger.error("龙门架ID参数错误");
            }
        }catch(NullPointerException e){
            logger.error("获取龙门架自动发货异常数据失败，龙门架ID为：" + request.getMachineId());
        }

        return result;
    }

    /**
     * 批次号打印
     */
    @RequestMapping(value = "/sendCodePrint" ,method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<BatchSendPrintImageResponse>> printSendCode(@RequestBody ScannerFrameBatchSendPrint[] requests){
        this.logger.info("龙门架打印数据开始-->需要打印的龙门架ID为" + requests[0].getMachineId());
        InvokeResult<List<BatchSendPrintImageResponse>> result = new InvokeResult<List<BatchSendPrintImageResponse>>();
        result.setCode(400);
        result.setMessage("服务调用成功，数据为空");

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "";//用户编号
        Integer userId = 0;
        String userName = "none";//用户姓名
        if(erpUser != null){
            userCode = erpUser.getUserCode() == null ? "":erpUser.getUserCode();
            userId = erpUser.getUserId() == null ? 0:erpUser.getUserId();
            userName = erpUser.getUserName() == null ? "none":erpUser.getUserName();
        }

        Integer machineId = requests[0].getMachineId();
        if(machineId == null || machineId == 0){
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
        try{
            Pager<List<ScannerFrameBatchSend>> pagerResult = scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager);//查询该龙门架的所有批次信息
            List<ScannerFrameBatchSend> dataRequest = pagerResult.getData();//取所有批次信息
            if(requests.length > 0){
                logger.info("本次提交的打印事件不是默认全选事件，需要对选中事件进行打印，选中的条数为：" + requests.length);
                /** ==============通过request判断是否有选中打印事件=============== **/
                for(ScannerFrameBatchSend data : dataRequest){
                    Integer itemReceiveSiteCode = (int)data.getReceiveSiteCode();
                    boolean bool = false;
                    for(ScannerFrameBatchSendPrint itemRequest : requests){
                        if (itemReceiveSiteCode == itemRequest.getReceiveSiteCode()){
                            bool = true;
                        }
                    }
                    if(!bool){
                        dataRequest.remove(data);/** 不是请求的打印数据，则剔除 **/
                    }
                }
                /** ==============判断结束，过滤出将要打印的List===================**/
            }

            List<BatchSendPrintImageResponse> results = new ArrayList<BatchSendPrintImageResponse>();
            String url =HTTP + PropertiesHelper.newInstance().getValue(prefixKey) + "/batchSendPrint/print";
            for(ScannerFrameBatchSend item : dataRequest){
                if(item.getReceiveSiteCode() == 0){
                    //没有目的站点，自动退出循环
                    continue;
                }
                /** ===============1.执行换批次动作================== **/
                ScannerFrameBatchSend itemtoEndSend = item;
                logger.info("打印并完结批次-->执行换批次操作：" + item.toString());
                itemtoEndSend.setPrintTimes((byte)0);
                itemtoEndSend.setLastPrintTime(null);
                itemtoEndSend.setCreateUserCode(userId);
                itemtoEndSend.setCreateUserName(userName);
                itemtoEndSend.setUpdateUserCode(Long.valueOf(userCode));
                itemtoEndSend.setUpdateUserName(userName);
                itemtoEndSend.setCreateTime(new Date());
                itemtoEndSend.setUpdateTime(new Date());
                itemtoEndSend.setYn((byte)1);
                itemtoEndSend.setSendCode(SerialRuleUtil.generateSendCode(itemtoEndSend.getCreateSiteCode(),itemtoEndSend.getReceiveSiteCode(),itemtoEndSend.getCreateTime()));
                boolean bool = scannerFrameBatchSendService.generateSend(itemtoEndSend);
                if(!bool){
                    logger.error("换批次动作失败：打印跳过该批次：" + item.toString());
                    continue;
                }
                /** ==================换批次动作执行完毕================ **/
                /** 2. ==================获取打印图片================= **/
                BatchSendPrintImageRequest itemRequest = new BatchSendPrintImageRequest();
                itemRequest.setSendCode(item.getSendCode());
                itemRequest.setCreateSiteCode((int)item.getCreateSiteCode());
                itemRequest.setCreateSiteName(item.getCreateSiteName());
                itemRequest.setReceiveSiteCode((int)item.getReceiveSiteCode());
                itemRequest.setReceiveSiteName(item.getReceiveSiteName());
                Integer packageSum = 0;
                /** 获取包裹的数据量 **/
                List<SendDetail> sendDetailList = gantryDeviceService.queryWaybillsBySendCode(item.getSendCode());
                if(sendDetailList != null && sendDetailList.size() > 0){
                    packageSum = sendDetailList.size();//获取包裹的数量
                }
                itemRequest.setPackageNum(packageSum);

                BatchSendPrintImageResponse itemResponse = RestHelper.jsonPostForEntity(url,itemRequest,new TypeReference<BatchSendPrintImageResponse>(){});
                results.add(itemResponse);
                /** ===================获取打印图片获取base64图片码结束================= **/
                /** =======================3.更新scanner_frame_batch_send表打印时间，打印次数开始================== **/
                scannerFrameBatchSendService.submitPrint(item.getId(),userId,userName);
                result.setCode(200);
                result.setMessage("服务调用成功");
                result.setData(results);
            }
        }catch(Exception e) {
            logger.error("获取数据异常");
            result.setCode(500);
            result.setMessage("服务调用异常");
        }
        return result;
    }





    /**
     * domain 类型转换
     * @param request
     * @return
     */
    private ScannerFrameBatchSend toScannerFrameBatchSend (GantryDeviceConfigRequest request){
        ScannerFrameBatchSend result = new ScannerFrameBatchSend();
        if(request != null){
            result.setMachineId(request.getMachineId());
            result.setCreateSiteCode(request.getCreateSiteCode());
            result.setCreateSiteName(request.getCreateSiteName());
        }
        return result;
    }

    /** 请求对象转换成domain对象，并赋予操作人 **/
    private GantryDeviceConfig toGantryDeviceConfig(GantryDeviceConfigRequest request,String userCode,String userName,Integer userId) {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
//        gantryDeviceConfig.setId(Long.parseLong(request.getId().toString()));
        gantryDeviceConfig.setMachineId(request.getMachineId());
        gantryDeviceConfig.setBusinessType(request.getBusinessType());
        gantryDeviceConfig.setCreateSiteCode(request.getCreateSiteCode());
        gantryDeviceConfig.setCreateSiteName(request.getCreateSiteName());
        gantryDeviceConfig.setGantrySerialNumber(request.getGantrySerialNumber());
        gantryDeviceConfig.setLockStatus(request.getLockStatus());
        gantryDeviceConfig.setLockUserErp(userCode);
        gantryDeviceConfig.setLockUserName(userName);
        gantryDeviceConfig.setBusinessTypeRemark(request.getOperateTypeRemark());
        gantryDeviceConfig.setOperateUserErp(userCode);
        gantryDeviceConfig.setOperateUserId(userId);
        gantryDeviceConfig.setOperateUserName(userName);
        gantryDeviceConfig.setSendCode("");//无发货批次
        gantryDeviceConfig.setStartTime(new Date());
        //        gantryDeviceConfig.setEndTime(request.getEndTime()); 不需要endtime
        gantryDeviceConfig.setUpdateUserErp(userCode);
        gantryDeviceConfig.setUpdateUserName(userName);
        return gantryDeviceConfig;
    }

}
