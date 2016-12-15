package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "/gantryStatus" , method = RequestMethod.POST)
    public InvokeResult<String> changeGantryStatus(GantryDeviceConfigRequest request) {
        this.logger.debug("龙门架启动/释放 --> changeGantryStatus");
        InvokeResult<String> result = new InvokeResult<String>();





        return result;
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

    @RequestMapping(value = "/getCurrentSplitPageList",method = RequestMethod.POST)
    public InvokeResult<Pager<List<ScannerFrameBatchSend>>> getCurrentSplitPageList(GantryDeviceConfigRequest request ,Pager<GantryDeviceConfig> pager){
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
        result.setCode(200);
        result.setMessage("服务调用成功");
        result.setData(0);
        // FIXME: 2016/12/12 直接调用嘉兴的接口获取异常数据

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
