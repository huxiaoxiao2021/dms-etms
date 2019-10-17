package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jcloud.jss.util.ValidateValue;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: WeightAndVolumeCheckController
 * @Description: 重量体积抽验统计--Controller实现
 * @author: hujiping
 * @date: 2019/4/22 20:58
 */
@Controller
@RequestMapping("weightAndVolumeCheck")
public class WeightAndVolumeCheckController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(WeightAndVolumeCheckController.class);

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;


    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = new Integer(-1);
        Integer orgId = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);
        return "/weightAndVolumeCheck/weightAndVolumeCheck";
    }

    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<WeightVolumeCollectDto> listData(@RequestBody WeightAndVolumeCheckCondition condition){

        PagerResult<WeightVolumeCollectDto> result = weightAndVolumeCheckService.queryByCondition(condition);
        return result;
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(WeightAndVolumeCheckCondition condition, Model model) {

        this.logger.info("导出重量体积抽验统计表");
        List<List<Object>> resultList;
        try{
            model.addAttribute("filename", "重量体积抽验统计表.xls");
            model.addAttribute("sheetname", "重量体积抽验统计结果");
            resultList = weightAndVolumeCheckService.getExportData(condition);
        }catch (Exception e){
            this.logger.error("导出重量体积抽验统计表失败:" + e.getMessage(), e);
            List<Object> list = new ArrayList<>();
            list.add("导出重量体积抽验统计表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new DefaultExcelView(), model.asMap());
    }

    /**
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillCode")String waybillCode,
                           @QueryParam("packageCode")String packageCode,
                           @QueryParam("reviewDate")String reviewDate,Model model) {
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("packageCode",packageCode);
        model.addAttribute("reviewDate",reviewDate);

        return "weightAndVolumeCheck/excessPictureUpload";
    }

    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult uploadExcessPicture(@RequestParam("image") MultipartFile image, HttpServletRequest request) {

        InvokeResult result = new InvokeResult();

        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String importErpCode = erpUser.getUserCode();
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
        String packageCode = request.getParameter("packageCode");
        String reviewDate = request.getParameter("reviewDate");
        try {
            String operateTimeForm = DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
            imageName = packageCode + "_" + siteCode + "_" + operateTimeForm + "." + suffixName;
            //上传到jss
            weightAndVolumeCheckService.uploadExcessPicture(imageName,imageSize,image.getInputStream());
        }catch (Exception e){
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.parameterError(formatMsg);
            logger.error(formatMsg,e);
            return result;
        }
        if(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
            //上传成功后给判责系统发消息并更新es数据
            weightAndVolumeCheckService.sendMqAndUpdate(packageCode,siteCode,uploadTime,reviewDate);
        }

        return result;

    }

    /**
     * 查看超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/searchExcessPicture", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<String> searchExcessPicture(@QueryParam("packageCode")String packageCode,
                                            @QueryParam("siteCode")Integer siteCode) {

        return weightAndVolumeCheckService.searchExcessPicture(packageCode,siteCode);
    }

}
