package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WaybillFlowDetail;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bPackage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 10:24
 */
@Controller
@RequestMapping("weightAndVolumeCheckOfB2b")
public class WeightAndVolumeCheckOfB2bController extends DmsBaseController {

    private static final Logger logger = LoggerFactory.getLogger(WeightAndVolumeCheckOfB2bController.class);

    @Autowired
    private WeightAndVolumeCheckOfB2bService weightAndVolumeCheckOfB2bService;

    @Autowired
    private WaybillPackageManager waybillPackageManager;


    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        String loginErp = null;
        if(loginUser != null){
            if(loginUser.getSiteType() == 64){
                createSiteCode = loginUser.getSiteCode();
            }
            loginErp = loginUser.getUserErp();
        }
        model.addAttribute("createSiteCode",createSiteCode);
        model.addAttribute("loginErp",loginErp);
        return "/weightAndVolumeCheck/weightAndVolumeCheckOfB2b";
    }

    /**
     * 初始化运单校验页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<List<WeightVolumeCheckOfB2bPackage>> init(){
        InvokeResult<List<WeightVolumeCheckOfB2bPackage>> result = new InvokeResult<>();
        List<WeightVolumeCheckOfB2bPackage> list = new ArrayList<>();
        result.setData(list);
        return result;
    }

    /**
     * 获取所有包裹
     * @param
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/getPackage", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<List<WeightVolumeCheckOfB2bPackage>> getPackage(String waybillOrPackageCode){

        return weightAndVolumeCheckOfB2bService.getPackageNum(waybillOrPackageCode);
    }

    /**
     * 运单维度校验是否超标
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/checkIsExcessOfWaybill", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> checkIsExcessOfWaybill(
            @RequestBody WeightVolumeCheckConditionB2b condition){

        return weightAndVolumeCheckOfB2bService.checkIsExcessOfWaybill(condition);
    }

    /**
     * 包裹维度校验是否超标
     * @param params
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/checkIsExcessOfPackage", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> checkIsExcessOfPackage(@RequestBody List<WeightVolumeCheckOfB2bPackage> params){
        return weightAndVolumeCheckOfB2bService.checkIsExcessOfPackage(params);
    }

    /**
     * 运单维度提交
     * @param param
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/waybillSubmitUrl", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> waybillSubmit(@RequestBody WeightVolumeCheckConditionB2b param){
        return weightAndVolumeCheckOfB2bService.dealExcessDataOfWaybill(param);
    }

    /**
     * 包裹维度提交
     * @param params
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/packageSubmitUrl", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> packageSubmit(@RequestBody List<WeightVolumeCheckOfB2bPackage> params){
        return weightAndVolumeCheckOfB2bService.dealExcessDataOfPackage(params);
    }

    /**
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillOrPackageCode")String waybillOrPackageCode,
                           @QueryParam("createSiteCode")Integer createSiteCode,
                           @QueryParam("rowIndex")Integer rowIndex,
                           @QueryParam("isWaybill")Integer isWaybill,
                           Model model) {
        model.addAttribute("waybillOrPackageCode",waybillOrPackageCode);
        model.addAttribute("createSiteCode",createSiteCode);
        model.addAttribute("rowIndex",rowIndex);
        model.addAttribute("isWaybill",isWaybill);
        return "/weightAndVolumeCheck/excessPictureUploadOfB2b";
    }

    /**
     * 查看超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/searchExcessPicture", method = RequestMethod.GET)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> searchExcessPicture(@QueryParam("waybillOrPackageCode")String waybillOrPackageCode,
                                                                                               @QueryParam("siteCode")Integer siteCode) {
        return weightAndVolumeCheckOfB2bService.searchExcessPicture(waybillOrPackageCode,siteCode);
    }


    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult uploadExcessPicture(@RequestParam("image") MultipartFile image,
                                                                                       HttpServletRequest request) {
        return weightAndVolumeCheckOfB2bService.uploadExcessPicture(image,request);
    }

    /**
     * 获取运单称重流水
     *
     * @param waybillCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/getOpeDetailByCode", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<Map<String,Object>> getOpeDetailByCode(@RequestParam("waybillCode") String waybillCode) {
        InvokeResult<Map<String,Object>> result = new InvokeResult<>();
        Map<String,Object> map = new HashMap<>();
        List<PackFlowDetail> list = Collections.emptyList();
        WaybillFlowDetail waybillFlowDetail;
        try {
            Page<PackFlowDetail> page = new Page<>();
            page.setPageSize(1000);
            page.setCurPage(1);
            Page<PackFlowDetail> pageResult = waybillPackageManager.getOpeDetailByCode(waybillCode, page);
            if(pageResult != null){
                list = pageResult.getResult();
            }
            waybillFlowDetail = weightAndVolumeCheckOfB2bService.getFirstWeightAndVolumeDetail(waybillCode);
            map.put("waybillRecord",list);
            map.put("firstRecord",waybillFlowDetail);
            result.setData(map);
        }catch (Exception e){
            logger.error("获取运单称重流水异常,异常信息:【{}】",e.getMessage(),e);
            result.customMessage(600,"获取运单称重流水异常");
        }
        return result;
    }


}
