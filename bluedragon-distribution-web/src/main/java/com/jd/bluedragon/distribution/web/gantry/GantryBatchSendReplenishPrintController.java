package com.jd.bluedragon.distribution.web.gantry;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String index(Model model,Integer machineId,Integer createSiteCode,String createSiteName,String startTime ,String endTime){
        if (machineId != null && createSiteCode != null){
            model.addAttribute("machineId",machineId);
            model.addAttribute("createSiteCode",createSiteCode);
            try{
                model.addAttribute("createSiteName", URLDecoder.decode(createSiteName,"UTF-8"));
            }catch (UnsupportedEncodingException e){
                logger.info("补打界面跳转参数解码异常",e);
                model.addAttribute("createSiteName", "未知分拣中心");
            }
            model.addAttribute("startTime",startTime);
            model.addAttribute("endTime",endTime);
        }
        return "/gantry/GantryBatchSendReplenishPrint";
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<ScannerFrameBatchSend>>> query(ScannerFrameBatchSendSearchArgument request, Pager<List<ScannerFrameBatchSend>> pager){
        logger.debug("获取补打印数据 --> ");
        InvokeResult<Pager<List<ScannerFrameBatchSend>>> result = new InvokeResult<Pager<List<ScannerFrameBatchSend>>>();
        result.setCode(400);
        result.setMessage("服务器处理信息异常，查询补打印数据失败!!");
        result.setData(null);
        if (request != null){
            Pager<ScannerFrameBatchSendSearchArgument> argumentPager = new Pager<ScannerFrameBatchSendSearchArgument>();
            if(pager.getPageNo() != null){
                argumentPager.setPageNo(pager.getPageNo());
                argumentPager.init();
            }
            request.setHasPrinted(false);//未打印标示
            argumentPager.setData(request);
            try{
                result.setData(scannerFrameBatchSendService.getCurrentSplitPageList(argumentPager));
                result.setCode(200);
                result.setMessage("补打数据获取成功");
            }catch(Exception e){
                result.setCode(200);
                result.setMessage("补打数据获取成功");
                result.setData(null);
                logger.error("补打数据获取失败..",e);
            }
        }
        return result;
    }

    @RequestMapping(value = "/querySubSiteNo",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<ScannerFrameBatchSend>> querySubSiteNo(ScannerFrameBatchSendSearchArgument request){
        InvokeResult<List<ScannerFrameBatchSend>> result = new InvokeResult<List<ScannerFrameBatchSend>>();
        result.setCode(400);
        result.setMessage("服务调用异常");
        result.setData(null);
        if(request == null){
            return null;
        }
        try{
            List<ScannerFrameBatchSend> list = scannerFrameBatchSendService.queryByMachineIdAndTime(request);
            result.setCode(200);
            result.setData(list);
            result.setMessage("获取龙门架的目的站点成功");
        }catch(Exception e){
            result.setMessage("获取龙门架的目的站点失败");
            logger.error("加载龙门架的目的站点失败。。",e);
        }
        return result;
    }

    /**
     * domain 类型转换
     * @param request
     * @return
     */
    private ScannerFrameBatchSendSearchArgument toScannerFrameBatchSend (GantryDeviceConfigRequest request,Integer receiveSiteCode,String receiveSiteName){
        ScannerFrameBatchSendSearchArgument result = new ScannerFrameBatchSendSearchArgument();
        if(request != null){
            result.setMachineId(request.getMachineId());
            result.setStartTime(request.getStartTime());
            result.setEndTime(request.getEndTime());
            result.setReceiveSiteCode(receiveSiteCode);
            result.setHasPrinted(false);
        }
        return result;
    }


}
