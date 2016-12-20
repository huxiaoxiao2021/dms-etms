package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/12/15.
 */
@Controller
@RequestMapping("/GantryBatchSendReplenishPrint")
public class GantryBatchSendReplenishPrintController {
    private static final Log logger = LogFactory.getLog(GantryBatchSendReplenishPrintController.class);

    @Autowired
    ScannerFrameBatchSendService scannerFrameBatchSendService;

    @RequestMapping(value = "/index")
    public String index(Model model,GantryDeviceConfigRequest request){
        if (request != null){
            model.addAttribute("machineId",request.getMachineId());
            model.addAttribute("createSiteCode",request.getCreateSiteCode());
            try{
                model.addAttribute("createSiteName", URLDecoder.decode(request.getCreateSiteName(),"UTF-8"));
            }catch (UnsupportedEncodingException e){
                logger.info("补打界面跳转参数解码异常",e);
                model.addAttribute("createSiteName", "未知分拣中心");
            }
            model.addAttribute("startTime",request.getStartTime());
            model.addAttribute("endTime",request.getEndTime());
        }
        return "/gantry/GantryBatchSendReplenishPrint";
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public InvokeResult<List<?>> query(GantryDeviceConfigRequest request){
        logger.debug("获取补打印数据 --> ");
        InvokeResult<List<?>> result = new InvokeResult<List<?>>();
        result.setCode(400);
        result.setMessage("服务器处理信息异常，查询补打印数据失败!!");
        result.setData(null);
        if (request != null){
//            scannerFrameBatchSendService.getCurrentSplitPageList()
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
