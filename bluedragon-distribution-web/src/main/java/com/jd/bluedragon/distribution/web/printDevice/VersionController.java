package com.jd.bluedragon.distribution.web.printDevice;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.service.VersionInfoInJssService;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.distribution.alpha.VersionIdListRequest;
import com.jd.bluedragon.distribution.alpha.VersionRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
@Controller
@RequestMapping("/version")
public class VersionController {

    private static final Logger log = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    VersionInfoInUccService versionInfoInUccService;

    @Autowired
    VersionInfoInJssService versionInfoInJssService;

    @Autowired
    PrintDeviceService printDeviceService;

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String versionInfo(Model model){
        List<Version> versionList = versionInfoInUccService.versionList();
        model.addAttribute("versionList",versionList);
        return "printDevice/versionIndex";
    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/query",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<Version>> versionQuery(@RequestBody VersionRequest request){
        InvokeResult<List<Version>> result = new InvokeResult<List<Version>>();
        try{
            List<Version> versionList = versionInfoInUccService.queryList(request.getVersionId(),request.isState());
            result.setCode(200);
            result.setMessage("查询成功");
            result.setData(versionList);
        }catch(Exception e){
            log.error("查询信息失败：",e);
            result.setCode(20000);
            result.setMessage("查询信息失败，服务器异常");
        }
        return result;

    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/toAddPager",method = RequestMethod.GET)
    public String versionAddPager(Model model){

        return "printDevice/versionAdd";
    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult versionAdd(@RequestParam("uploadFile") MultipartFile file,
                                   @RequestParam("versionId") String versionId,
                                   @RequestParam("des") String des,
                                   @RequestParam("state") boolean state,
                                   HttpServletResponse response) {
            response.setContentType("text/json;charset=utf-8");
        InvokeResult result = new InvokeResult();

        try {
            if ( null == versionId || null == des) {
                result.setCode(10000);
                result.setMessage("版本号为空");
            }else if(versionInfoInJssService.allVersionIdInJss().contains(versionId)){
                result.setCode(10000);
                result.setMessage("版本信息已经存在，请取消！");
            } else {
                Version version = new Version();
                version.setVersionId(versionId);
                version.setState(state);
                version.setDes(des);
                version.setCreateTime(DateHelper.formatDateTime(new Date()));
                version.setUpdateTime(DateHelper.formatDateTime(new Date()));
                versionInfoInJssService.uploadNewVersion(versionId,file.getSize(),file.getInputStream());//执行JSS添加
                versionInfoInUccService.uploadVersion(version);//执行UCC备份数据
                result.setCode(200);
                result.setMessage("版本上传成功");
            }
        } catch (Exception e) {
            log.error("版本上传失败", e);
            result.setCode(10000);
            result.setMessage("版本上传失败");
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/toModifyPager",method = RequestMethod.GET)
    public ModelAndView toModifyPager(String versionId){

        Version version;
        HashMap versionParamsMap = new HashMap();

        try{
            version = versionInfoInUccService.queryById(versionId);//获取版本的基本信息

            versionParamsMap.put("versionId",version.getVersionId());
            versionParamsMap.put("des",version.getDes());
            versionParamsMap.put("state",version.isState());
            versionParamsMap.put("createTime",version.getCreateTime());
        }catch (Exception e){
            log.error("跳转错误：",e);
            return new ModelAndView("printDevice/versionModify");
        }

        ModelAndView mav = new ModelAndView("printDevice/versionModify");
        mav.addObject("versionParamsMap",versionParamsMap);

        return mav;
    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult versionModify(@RequestBody Version request) {
        InvokeResult result = new InvokeResult();

        try {
            if (null == request.getVersionId() || null == request.getDes()) {
                throw new IOException("版本信息不完善，请重试!!");
            }
            Version version = new Version();
            version.setVersionId(request.getVersionId());
            version.setDes(request.getDes());
            version.setState(request.isState());
            version.setCreateTime(request.getCreateTime());
            version.setUpdateTime(DateHelper.formatDateTime(new Date()));
            versionInfoInUccService.modifyVersion(version);//只执行UCC更新数据
            result.setCode(200);
            result.setMessage("版本更新成功");
        } catch (Exception e) {
            log.error("版本更新失败", e);
            result.setCode(10000);
            result.setMessage("版本更新失败");
        }
        return result;
    }

    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult versionDelete(@RequestBody VersionIdListRequest<List<String>> versionIdList){
        InvokeResult result = new InvokeResult();
        try{
            for(String id:versionIdList.getList()){
                if(printDeviceService.searchPrintDevice(id,"").size() != 0){
                    throw new IOException("选中的版本号中，包含不能被删除的版本号");
                }
            }
            versionInfoInUccService.deleteVersion(versionIdList.getList());
            versionInfoInJssService.deleteVersionByVersionId(versionIdList.getList());
            result.setCode(200);
            result.setMessage("执行删除成功");
        }catch (Exception e){
            result.setCode(1000);
            result.setMessage("执行删除失败");
            log.error("删除失败：",e);
        }
        return result;
    }

    /**
     * 状态修改
     * @param request versionId state（原来的）
     * @return
     */
    @Authorization(Constants.DMS_WEB_ISV_MANAGE_R)
    @RequestMapping(value = "/stateChange",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult versionStateChange(@RequestBody VersionRequest request){
        InvokeResult result = new InvokeResult();
        request.setState(request.isState()?false:true);//改变状态
        try{
            Integer i = versionInfoInUccService.changeVersionState(request.getVersionId(),request.isState());
            if(1==i){
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
