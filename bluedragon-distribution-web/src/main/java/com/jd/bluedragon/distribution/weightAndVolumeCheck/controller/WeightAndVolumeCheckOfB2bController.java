package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.*;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 10:24
 */
@Controller
@RequestMapping("weightAndVolumeCheckOfB2b")
public class WeightAndVolumeCheckOfB2bController extends DmsBaseController {

    @Autowired
    private WeightAndVolumeCheckOfB2bService weightAndVolumeCheckOfB2bService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = -1;
        LoginUser loginUser = getLoginUser();
        String loginErp = null;
        if(loginUser != null){
            createSiteCode = loginUser.getSiteCode();
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
     * 获取运单信息
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/getWaybillInfo")
    @ResponseBody
    public InvokeResult<Boolean> getWaybillInfo(@QueryParam("waybillCode") String waybillCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(!WaybillUtil.isWaybillCode(waybillCode) && !WaybillUtil.isPackageCode(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "单号不符合规则!");
            return result;
        }
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(waybillCode));
        if(waybill == null || waybill.getGoodNumber() == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "此单无运单数据，请联系'分拣小秘'!");
            return result;
        }
        result.setData(!Objects.equals(waybill.getGoodNumber(), 1));
        return result;
    }

    /**
     * 运单维度校验是否超标
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/checkIsExcessOfWaybill", method = RequestMethod.POST)
    @ResponseBody
    public SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill> checkIsExcessOfWaybill(@RequestBody WeightVolumeCheckConditionB2b condition){
        SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill> spotCheckPagerResult = new SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill>();
        spotCheckPagerResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(StringUtils.isEmpty(condition.getWaybillOrPackageCode())){
            // 初始化数据
            spotCheckPagerResult.setTotal(0);
            spotCheckPagerResult.setRows(new ArrayList<WeightVolumeCheckOfB2bWaybill>());
            return spotCheckPagerResult;
        }
        InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> invokeResult = weightAndVolumeCheckOfB2bService.checkIsExcessOfWaybill(condition);
        if(!invokeResult.codeSuccess() || CollectionUtils.isEmpty(invokeResult.getData())){
            spotCheckPagerResult.setCode(invokeResult.getCode());
            spotCheckPagerResult.setMessage(invokeResult.getMessage());
            return spotCheckPagerResult;
        }
        spotCheckPagerResult.setTotal(invokeResult.getData().size());
        spotCheckPagerResult.setRows(invokeResult.getData());
        return spotCheckPagerResult;
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
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillOrPackageCode")String waybillOrPackageCode,
                           @QueryParam("createSiteCode")Integer createSiteCode,
                           @QueryParam("weight")Double weight,
                           @QueryParam("excessType")Integer excessType,
                           @QueryParam("isMultiPack")Boolean isMultiPack,
                           Model model) {
        model.addAttribute("waybillOrPackageCode",waybillOrPackageCode);
        model.addAttribute("createSiteCode",createSiteCode);
        model.addAttribute("weight",weight);
        model.addAttribute("excessType",excessType);
        model.addAttribute("isMultiPack",isMultiPack);
        return "/weightAndVolumeCheck/excessPictureUploadOfB2b";
    }

    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> uploadExcessPicture(@RequestParam("image") MultipartFile image,
                                                                                       HttpServletRequest request) {
        return weightAndVolumeCheckOfB2bService.uploadExcessPicture(image,request);
    }
}
