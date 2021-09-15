package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jss.util.ValidateValue;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.Enum.SpotCheckRecordTypeEnum;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * @ClassName: WeightAndVolumeCheckController
 * @Description: 重量体积抽验统计--Controller实现
 * @author: hujiping
 * @date: 2019/4/22 20:58
 */
@Controller
@RequestMapping("weightAndVolumeCheck")
public class WeightAndVolumeCheckController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(WeightAndVolumeCheckController.class);

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
        condition.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
        condition.setQueryForWeb(Constants.YN_YES);

        PagerResult<WeightVolumeCollectDto> result = weightAndVolumeCheckService.queryByCondition(condition);
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping("/packageDetailListData")
    @ResponseBody
    public PagerResult<WeightVolumeCollectDto> packageDetailListData(@RequestBody WeightAndVolumeCheckCondition condition){
        condition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
        PagerResult<WeightVolumeCollectDto> result = weightAndVolumeCheckService.queryByCondition(condition);
        return result;
    }

    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.weightAndVolumeCheck.controller.WeightAndVolumeCheckController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void toExport(WeightAndVolumeCheckCondition condition, HttpServletResponse response) {
        BufferedWriter bfw = null;
        try{
             exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getCode());
            if(StringUtils.isNotBlank(condition.getBusiName())){
                condition.setBusiName(URLDecoder.decode(condition.getBusiName(), "UTF-8"));
            }
            String fileName = "重量体积抽检统计表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            condition.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
            condition.setQueryForWeb(Constants.YN_YES);
            weightAndVolumeCheckService.export(condition,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getCode());
        }catch (Exception e){
            log.error("exportData error", e);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                log.error("export-error", e);
            }
        }
    }

    /**
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillCode")String waybillCode,
                           @QueryParam("packageCode")String packageCode,
                           @QueryParam("reviewDate")String reviewDate,
                           @QueryParam("reviewSiteCode")String reviewSiteCode,Model model) {
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("packageCode",packageCode);
        model.addAttribute("reviewDate",reviewDate);
        model.addAttribute("reviewSiteCode",reviewSiteCode);

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
            String reviewSiteCode = request.getParameter("reviewSiteCode");
            BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseStaffByErpNoCache(importErpCode);
            if(baseDto != null){
                siteCode = baseDto.getSiteCode();
            }
            /*if(reviewSiteCode != null && !Objects.equals(siteCode + "", reviewSiteCode)){
                result.parameterError("当前用户所属场地与该抽检记录操作场地不一致，不能上传图片");
                return result;
            }*/
        }catch (Exception e){
            log.error("通过登陆人erp获取所属分拣中心异常：{}",importErpCode,e);
        }

        long imageSize = image.getSize();
        String imageName = image.getOriginalFilename();
        String[] strArray = imageName.split("\\.");
        String suffixName = strArray[strArray.length - 1];
        String[] defualtSuffixName = new String[] {"jpg","jpeg","gif","png","bmp"};
        try{
            if(!Arrays.asList(defualtSuffixName).contains(suffixName)){
                result.parameterError("文件格式不正确!"+suffixName);
                log.warn("文件格式不正确:{}", suffixName);
                return result;
            }
            if(imageSize > SINGLE_IMAGE_SIZE_LIMIT){
                result.parameterError(MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, SINGLE_IMAGE_SIZE_LIMIT));
                log.warn("单个图片超出限制大小");
                return result;
            }
            //校验文件名称中的特殊字符
            ValidateValue.validateObjectKey(imageName);
        }catch (Exception e){
            String formatMsg = MessageFormat.format("文件名只能是由字母、数字、中划线(-)及点号(.)组成，该文件名称校验失败{0}",imageName );
            result.parameterError(formatMsg);
            log.warn(formatMsg,e);
            return result;
        }
        String packageCode = request.getParameter("packageCode");
        try {
            String operateTimeForm = DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
            imageName = packageCode + "_" + siteCode + "_" + operateTimeForm + "." + suffixName;
            //上传到jss
            weightAndVolumeCheckService.uploadExcessPicture(imageName,imageSize,image.getInputStream());
        }catch (Exception e){
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.parameterError(formatMsg);
            log.error(formatMsg,e);
            return result;
        }
        if(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
            // 上传成功后，更新图片，发送MQ消息，进行下一步操作
            weightAndVolumeCheckService.updateImgAndSendHandleMq(packageCode, siteCode, null);
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

    /**
     * 跳转到B网超标图片页面
     * @param waybillCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/toSearchPicture4MultiplePackage")
    public String toSearchPicture4MultiplePackage(@QueryParam("waybillCode")String waybillCode,
                                           @QueryParam("siteCode")Integer siteCode,
                                           @QueryParam("pageNo") Integer pageNo,
                                           @QueryParam("pageSize") Integer pageSize, Model model){
        model.addAttribute("siteCode",siteCode);
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("pageNo",pageNo);
        model.addAttribute("pageSize",pageSize);
        return "/weightAndVolumeCheck/multiplePackageExcessPicture";
    }

    /**
     * 查看一单多件超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/searchPicture4MultiplePackage", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<WeightVolumePictureDto>> searchPicture4MultiplePackage(@RequestBody WeightAndVolumeCheckCondition condition) {
        Pager<WeightVolumeQueryCondition> weightVolumeQueryConditionPager = new Pager<>();
        WeightVolumeQueryCondition weightVolumeQueryCondition = new WeightVolumeQueryCondition();
        weightVolumeQueryCondition.setWaybillCode(condition.getWaybillCode());
        weightVolumeQueryCondition.setReviewSiteCode(condition.getCreateSiteCode().intValue());
        weightVolumeQueryCondition.setPackageCode(condition.getWaybillOrPackCode());
        weightVolumeQueryConditionPager.setSearchVo(weightVolumeQueryCondition);
        weightVolumeQueryConditionPager.setPageNo(condition.getOffset()/condition.getLimit() + 1);
        weightVolumeQueryConditionPager.setPageSize(condition.getLimit());
        return weightAndVolumeCheckService.searchPicture4MultiplePackage(weightVolumeQueryConditionPager);
    }

    /**
     * 跳转到B网超标图片页面
     * @param waybillCode
     * @param isWaybillSpotCheck
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/toSearchB2bExcessPicture")
    public String toSearchB2bExcessPicture(@QueryParam("waybillCode")String waybillCode,
                                           @QueryParam("siteCode")Integer siteCode,
                                           @QueryParam("isWaybillSpotCheck")Integer isWaybillSpotCheck,
                                           @QueryParam("fromSource")String fromSource,Model model){
        model.addAttribute("siteCode",siteCode);
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("isWaybillSpotCheck",isWaybillSpotCheck);
        model.addAttribute("fromSource",fromSource);
        return "/weightAndVolumeCheck/b2bExcessPicture";
    }

    /**
     * 显示B网超标图片链接
     * @param waybillCode
     * @param siteCode
     * @param isWaybillSpotCheck
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/searchB2bExcessPicture", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<String> searchB2bExcessPicture(@QueryParam("waybillCode")String waybillCode,
                                                       @QueryParam("siteCode")Integer siteCode,
                                                       @QueryParam("isWaybillSpotCheck")Integer isWaybillSpotCheck,
                                                       @RequestParam("fromSource")String fromSource){
        return weightAndVolumeCheckService.searchPicture(waybillCode,siteCode,isWaybillSpotCheck,fromSource);
    }
}
