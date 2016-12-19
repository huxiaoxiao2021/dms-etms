package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.gantry.domain.GantryBatchSendResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wuzuxiang on 2016/12/7.
 */
@Controller
@RequestMapping("/gantryAutoSend")
public class GantryAutoSendController {

    private static final Log logger = LogFactory.getLog(GantryAutoSendController.class);

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
    DepartureService departureService;

    @RequestMapping(value = "/index" ,method = RequestMethod.GET)
    public String index(Model model){
        this.logger.debug("龙门架自动发货 --> index");
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if(erpUser != null){
            String userCode = "";
            String userName = "";
            userCode = erpUser.getUserCode() == null ? "none":erpUser.getUserCode();
            userName = erpUser.getUserName() == null ? "none":erpUser.getUserName();
            model.addAttribute("userCode", userCode);
            model.addAttribute("userName", userName);
        }
        return "gantry/gantryAutoSendIndex";
    }

    @RequestMapping(value = "/updateOrInsertGantryDeviceStatus", method = RequestMethod.POST)
    public InvokeResult<GantryDeviceConfig> UpsertGantryDeviceBusinessOrStatus(GantryDeviceConfigRequest request){
        InvokeResult<GantryDeviceConfig> result = new InvokeResult<GantryDeviceConfig>();
        result.setCode(500);
        result.setMessage("参数异常");
        result.setData(null);
        logger.debug("修改或插入龙门架的状态 --> UpsertGantryDeviceBusinessOrStatus");
        if(null == request && request.getMachineId() == null){
            logger.error("没有需要修改的龙门架设备信息");
            return null;
        }
        if(request.getLockStatus() == 0){/** 解锁龙门架操作 **/
            /** 第一步：找到gantry_device_config最新的一条龙门架记录 **/
            logger.info("用户：" + request.getLockUserErp() + "正在尝试解锁龙门架，ID为" + request.getMachineId());
            try{
                GantryDeviceConfig  gantryDeviceConfig = null;
                gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
                if(gantryDeviceConfig.getLockUserErp().equals(request.getLockUserErp())){
                    //只更新该龙门架的锁定状态为0解锁
                    gantryDeviceConfig.setLockStatus(request.getLockStatus());
                    int i = gantryDeviceConfigService.updateLockStatus(gantryDeviceConfig);
                    if( i != -1){
                        result.setCode(200);
                        result.setMessage("释放龙门架状态成功");
                        result.setData(gantryDeviceConfig);
                    }else{
                        result.setCode(500);
                        result.setMessage("处理龙门架参数状态错误，更新失败");
                        result.setData(gantryDeviceConfig);
                    }
                }else{
                    logger.info("此用户无法解锁由别人锁定的龙门架设备；解锁人"+request.getLockUserName()+"锁定人"+request.getLockUserName());
                    result.setCode(1000);
                    result.setMessage("解锁失败，请联系锁定人" + gantryDeviceConfig.getLockUserErp() + "解锁" );
                    result.setData(gantryDeviceConfig);
                }
            }catch (Exception e){
                logger.error("服务器处理异常：",e);
            }
        }else if(request.getLockStatus() == 1){/** 锁定龙门架操作 **/
            logger.info("用户：" + request.getLockUserErp() + "正在锁定龙门架，龙门架ID为："
                    + request.getMachineId() + "锁定龙门架的业务类型为：" + request.getBusinessType() + request.getOperateTypeRemark());




        }else{
            logger.error("用户正在尝试的启用、释放龙门架操作状态异常，已经终止..");
        }
        return null;
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
        sfbssa.setStartTime(request.getStartTime());
        sfbssa.setEndTime(request.getEndTime());
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
    public InvokeResult<GantryBatchSendResult> summaryBySendCode(String sendCode){
        InvokeResult<GantryBatchSendResult> result = new InvokeResult<GantryBatchSendResult>();
        result.setCode(500);
        result.setMessage("服务器处理异常");
        if(sendCode != null){
            List<SendDetail> sendDetailList = null;
            GantryBatchSendResult sendBoxSum = new GantryBatchSendResult();
            Integer packageSum = 0;//批次总包裹数量
            Double volumeSum = 0.00;//取分拣体积
            if(sendDetailList != null && sendDetailList.size() > 0){
                for (SendDetail sendD : sendDetailList){
                    try{
                        packageSum += sendD.getPackageNum();
                        WaybillPackageDTO waybillPackageDTO = waybillService.getWaybillPackage(sendD.getPackageBarcode());
                        volumeSum += waybillPackageDTO.getVolume() == 0? waybillPackageDTO.getOriginalVolume():waybillPackageDTO.getVolume();
                    }catch(Exception e){
                        logger.error("获取批次的总数量和总体积失败：批次号为"+sendCode,e);
                    }
                }
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
    public InvokeResult<Integer> generateSendCode(GantryDeviceConfigRequest request){
        this.logger.debug("龙门架自动换批次 --> changeSendCode");
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        result.setCode(400);
        result.setMessage("服务器处理异常，换批次失败！");
        ScannerFrameBatchSend scannerFrameBatchSend = toScannerFrameBatchSend(request);
        try {
            boolean bool = scannerFrameBatchSendService.generateSend(scannerFrameBatchSend);
            if(bool){
                result.setCode(200);
                result.setMessage("换批次成功");
            }
        }catch(Exception e){
            logger.error("生产新的批次号失败",e);
        }
        return result;
    }

    @RequestMapping(value = "/queryExceptionNum", method = RequestMethod.POST)
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


}
