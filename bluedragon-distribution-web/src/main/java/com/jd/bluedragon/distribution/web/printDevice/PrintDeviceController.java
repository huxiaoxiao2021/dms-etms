package com.jd.bluedragon.distribution.web.printDevice;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.distribution.alpha.PrintDeviceIdListRequest;
import com.jd.bluedragon.distribution.alpha.PrintDeviceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
@Controller
@RequestMapping("/printDevice")
public class PrintDeviceController {

    private static final Logger log = LoggerFactory.getLogger(PrintDeviceController.class);

    @Autowired
    PrintDeviceService printDeviceService;

    @Autowired
    VersionInfoInUccService versionInfoInUccService;

    /**
     * 获取ISV所有基础信息
     * @param model
     * @return
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String printDeviceBaseInfo(Model model){
        List<PrintDevice> printDeviceList = printDeviceService.allPrintDeviceInfo();
        model.addAttribute("printDeviceList",printDeviceList);
        return "printDevice/printDeviceIndex";
    }

    /**
     * 根据信息查询ISV信息，对于结果不进行分页
     * @return
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/query",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<PrintDevice>> printDeviceQuery(@RequestBody PrintDeviceRequest request){
        InvokeResult<List<PrintDevice>> result = new InvokeResult<List<PrintDevice>>();
        try{
            List<PrintDevice> list = printDeviceService.searchPrintDevice(request.getVersionId(),request.getPrintDeviceId());
            result.setCode(200);
            result.setMessage("查询ISV信息成功");
            result.setData(list);
        }catch(Exception e){
            log.error("查询信息失败：",e);
            result.setCode(20000);
            result.setMessage("服务异常，请稍后再试");
        }
        return result;
    }

    /**
     * 跳转增加页面
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/toAddPager",method = RequestMethod.GET)
    public String printDeviceAddPager(Model model){
        List<Version> versionList = versionInfoInUccService.versionList();
        List<String> versionIdList = new ArrayList<String>();
        if (versionList!=null){
            for ( Version version:versionList) {
                versionIdList.add(version.getVersionId());
            }
        }
        model.addAttribute("versionIdList",versionIdList);
        return "printDevice/printDeviceAdd";
    }

    /**
     * 增加ISV信息
     * @return
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult printDeviceAdd(@RequestBody PrintDevice printDevice){
        InvokeResult result = new InvokeResult();
        try{
            Integer i = printDeviceService.addPrintDevice(printDevice);
            if (i.equals(1)){
                result.setCode(200);
                result.setMessage("ISV版本新增成功");
            }else{
                result.setCode(20000);
                result.setMessage("该ISV版本信息已经存在，无法增加");
            }
        }catch(Exception e){
            result.setCode(10000);
            result.setMessage("ISV版本新增失败");
            log.error("增加信息失败：",e);
        }
        return result;
    }

    /**
     * 跳转修改ISV信息页面
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/toModifyPager",method = RequestMethod.GET)
    public ModelAndView toModifyPager(PrintDeviceRequest request){
        List<PrintDevice> list = new ArrayList<PrintDevice>();
        ModelAndView mav = new ModelAndView("printDevice/printDeviceModify");
        try{
            list = printDeviceService.searchPrintDevice(request.getVersionId(),request.getPrintDeviceId());
        } catch (Exception e){
            log.error("查询ISV的版本信息失败");
            return mav;
        }
        List<Version> versionList = new ArrayList<Version>();
        versionList = versionInfoInUccService.versionList();//获取下拉框的下拉列表
        PrintDevice printDevice = list.get(0);
        HashMap printDeviceParamsMap = new HashMap();
        printDeviceParamsMap.put("printDeviceId",printDevice.getPrintDeviceId());
        printDeviceParamsMap.put("versionId",printDevice.getVersionId());
        printDeviceParamsMap.put("des",printDevice.getDes());
        printDeviceParamsMap.put("state",printDevice.isState());
        printDeviceParamsMap.put("createTime",printDevice.getCreateTime());

        mav.addObject("printDeviceParamsMap",printDeviceParamsMap);
        mav.addObject("versionList",versionList);

        return mav;
    }

    /**
     * 执行修改操作
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/modify",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult doModify(@RequestBody PrintDevice printDevice){
        InvokeResult result = new InvokeResult();
        try{
            printDeviceService.modifyPrintDevice(printDevice);
            result.setCode(200);
            result.setMessage("修改成功");
        }catch(Exception e ){
            result.setCode(10000);
            result.setMessage("修改失败");
            log.error("执行修改操作异常：",e);
        }
        return result;
    }

    /**
     * 删除ISV的相关信息
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult doDelete(@RequestBody PrintDeviceIdListRequest<List<String>> request){

        InvokeResult result = new InvokeResult();
        try{
            if(request.getList().contains("00000")){
                result.setCode(20000);
                result.setMessage("不可删除ID为00000的ISV设备信息");
                return result;
            }
            printDeviceService.deletePrintDeviceById(request.getList());
            result.setCode(200);
            result.setMessage("删除成功");
        }catch (Exception e){
            log.error("执行删除操作异常：",e);
            result.setCode(20000);
            result.setMessage("执行删除异常");
        }
        return result;
    }

    /**
     * ISV状态修改
     * @param request printDeviceId state（原来的）
     * @return
     */
    @Authorization(Constants.DMS_WEB_ISV_CONTROL_R)
    @RequestMapping(value = "/stateChange",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult versionStateChange(@RequestBody PrintDevice request){
        InvokeResult result = new InvokeResult();
        request.setState(request.isState()?false:true);//改变状态
        try{
            Integer i = printDeviceService.changePrintDeviceState(request.getPrintDeviceId(),request.isState());
            if(i==1){
                result.setCode(200);
                result.setMessage("修改状态成功");
            }else{
                result.setCode(20000);
                result.setMessage("修改状态失败");
            }

        }catch(Exception e){
            result.setCode(10000);
            result.setMessage("状态修改失败");
            log.error("删除失败",e);
        }
        return result;
    }

}
