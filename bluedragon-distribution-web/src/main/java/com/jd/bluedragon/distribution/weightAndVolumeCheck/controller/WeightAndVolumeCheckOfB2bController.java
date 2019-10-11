package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bPackage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 10:24
 */
@Controller
@RequestMapping("weightAndVolumeCheckOfB2b")
public class WeightAndVolumeCheckOfB2bController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(WeightAndVolumeCheckOfB2bController.class);

    @Autowired
    private WeightAndVolumeCheckOfB2bService weightAndVolumeCheckOfB2bService;

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
        }
        model.addAttribute("createSiteCode",createSiteCode);
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
     * @param params
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/waybillSubmitUrl", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> waybillSubmit(@RequestBody List<WeightVolumeCheckConditionB2b> params){
        return weightAndVolumeCheckOfB2bService.dealExcessDataOfWaybill(params);
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
    public String toUpload(@QueryParam("waybillCode")String waybillCode,
                           @QueryParam("createSiteCode")Integer createSiteCode,
                           @QueryParam("rowIndex")Integer rowIndex,
                           @QueryParam("isWaybill")Integer isWaybill,
                           Model model) {
        model.addAttribute("waybillCode",waybillCode);
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
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<String> searchExcessPicture(@QueryParam("packageCode")String packageCode,
                                                                                               @QueryParam("siteCode")Integer siteCode) {
        return weightAndVolumeCheckOfB2bService.searchExcessPicture(packageCode,siteCode);
    }


    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public com.jd.bluedragon.distribution.base.domain.InvokeResult uploadExcessPicture(@RequestParam("image") MultipartFile image, HttpServletRequest request) {

        com.jd.bluedragon.distribution.base.domain.InvokeResult result = new com.jd.bluedragon.distribution.base.domain.InvokeResult();

        /*ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
//        String importErpCode = erpUser.getUserCode();
        String importErpCode = "bjxings";
        Integer siteCode = -1;
        try{
            BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseStaffByErpNoCache(importErpCode);
            if(baseDto != null){
                siteCode = baseDto.getSiteCode();
            }
        }catch (Exception e){
            logger.error("通过登陆人erp获取所属分拣中心异常!"+importErpCode);
        }

        long imageSize = image.getSize();
        String imageName = image.getOriginalFilename();
        String[] strArray = imageName.split("\\.");
        String suffixName = strArray[strArray.length - 1];
        String[] defualtSuffixName = new String[] {"jpg","jpeg","gif","png","bmp"};
        try{
            if(!Arrays.asList(defualtSuffixName).contains(suffixName)){
                result.parameterError("文件格式不正确!"+suffixName);
                logger.error("文件格式不正确!"+suffixName);
                return result;
            }
            if(imageSize > SINGLE_IMAGE_SIZE_LIMIT){
                result.parameterError(MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, SINGLE_IMAGE_SIZE_LIMIT));
                logger.warn("单个图片超出限制大小");
                return result;
            }
            //校验文件名称中的特殊字符
            ValidateValue.validateObjectKey(imageName);
        }catch (Exception e){
            String formatMsg = MessageFormat.format("文件名只能是由字母、数字、中划线(-)及点号(.)组成，该文件名称校验失败{0}",imageName );
            result.parameterError(formatMsg);
            logger.warn(formatMsg,e);
            return result;
        }
        Long uploadTime  = new Date().getTime();
        String packageCode = request.getParameter("waybillCode");
        String reviewDate = new Date().toString();
        try {
            String operateTimeForm = DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
            imageName = packageCode + "_" + siteCode + "_" + operateTimeForm + "." + suffixName;
            //上传到jss
            weightAndVolumeCheckService.uploadExcessPicture(imageName,imageSize,image.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.parameterError(formatMsg);
            logger.error(formatMsg,e);
            return result;
        }
        if(result.getCode() == com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE){
            //上传成功后给判责系统发消息并更新es数据
            weightAndVolumeCheckService.sendMqAndUpdate(packageCode,siteCode,uploadTime,reviewDate);
        }*/

        return result;

    }
}
