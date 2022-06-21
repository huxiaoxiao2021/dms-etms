package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 15:06
 */
@Service("weightAndVolumeCheckOfB2bService")
public class WeightAndVolumeCheckOfB2bServiceImpl implements WeightAndVolumeCheckOfB2bService {

    private static final Logger log = LoggerFactory.getLogger(WeightAndVolumeCheckOfB2bServiceImpl.class);

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    /**
     * B网抽检缓存前缀
     * */
    private static final String B2B_SPOT_CHECK_REDIS_KEY_PREFIX = "B2B_SPOT_CHECK_KEY";

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Override
    public InvokeResult<String> uploadExcessPicture(MultipartFile image, HttpServletRequest request) {
        InvokeResult<String> result = new InvokeResult<String>();
        String waybillCode = WaybillUtil.getWaybillCode(request.getParameter("waybillOrPackageCode"));
        double weight = Double.parseDouble(request.getParameter("weight"));
        // 是否多包裹
        boolean isMultiPack = Boolean.parseBoolean(request.getParameter("isMultiPack"));
        // 超标类型 1：重量超标 2：体积超标
        Integer excessType = request.getParameter("excessType") == null ? null : Integer.parseInt(request.getParameter("excessType"));
        // 上传的图片类型：@see:SpotCheckPicTypeEnum
        int uploadPicType = Integer.parseInt(request.getParameter("uploadPicType"));
        // 是否强制
        boolean isForce = request.getParameter("isForce") != null && Boolean.parseBoolean(request.getParameter("isForce"));
        long imageSize = image.getSize();
        String imageName = image.getOriginalFilename();
        String[] strArray = imageName.split("\\.");
        String suffixName = strArray[strArray.length - 1];

        String picUrl = null;
        try {
            String[] defaultSuffixName = new String[] {"jpg","jpeg","gif","png","bmp"};
            if(!Arrays.asList(defaultSuffixName).contains(suffixName)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"文件格式不正确!"+suffixName);
                log.warn("参数:{}, 异常信息:{}", suffixName,"文件格式不正确!");
                return result;
            }
            if(imageSize > SINGLE_IMAGE_SIZE_LIMIT){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, SINGLE_IMAGE_SIZE_LIMIT));
                log.warn("参数:{}, 异常信息:{}", waybillCode, "上传的超标图片失败,单个图片超出限制大小");
                return result;
            }
            //上传到jss
            picUrl = spotCheckDealService.uploadExcessPicture(imageName, image.getInputStream());
        }catch (Exception e){
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, formatMsg);
            log.error("参数:{}, 异常信息:{}", imageName , e.getMessage(), e);
        }
        if(StringUtils.isEmpty(picUrl)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "图片上传失败，请重新上传!");
            return result;
        }
        // 强制 | 多包裹  则不AI识别
        if(isForce || isMultiPack){
            result.setData(picUrl);
            return result;
        }
        // AI图片识别
        ImmutablePair<Integer, String> aiResult = spotCheckDealService.singlePicAutoDistinguish(waybillCode, weight, picUrl, uploadPicType, excessType);
        result.customMessage(aiResult.getLeft(), aiResult.getRight());
        result.setData(picUrl);
        return result;
    }

    @Override
    public InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> checkIsExcessOfWaybill(WeightVolumeCheckConditionB2b condition) {
        InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> result = new InvokeResult<>();
        if(condition == null){
            result.parameterError("参数错误!");
            return result;
        }
        String waybillCode = WaybillUtil.getWaybillCode(condition.getWaybillOrPackageCode());
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
            result.parameterError("运单数据为空!");
            return result;
        }
        List<WeightVolumeCheckOfB2bWaybill> list = new ArrayList<>();
        result.setData(list);
        WeightVolumeCheckOfB2bWaybill weightVolumeCheckOfB2bWaybill = new WeightVolumeCheckOfB2bWaybill();
        list.add(weightVolumeCheckOfB2bWaybill);
        weightVolumeCheckOfB2bWaybill.setWaybillCode(waybillCode);
        weightVolumeCheckOfB2bWaybill.setWaybillWeight(condition.getWaybillWeight());
        Double waybillLength = condition.getWaybillLength();
        Double waybillWidth = condition.getWaybillWidth();
        Double waybillHeight = condition.getWaybillHeight();
        Double waybillVolume = condition.getWaybillVolume();
        if(waybillVolume == null && waybillLength != null && waybillWidth != null && waybillHeight != null ){
            waybillVolume = waybillLength * waybillWidth * waybillHeight;
        }
        weightVolumeCheckOfB2bWaybill.setWaybillVolume(waybillVolume);
        weightVolumeCheckOfB2bWaybill.setUpLoadNum(0);//初始上传图片数量
        weightVolumeCheckOfB2bWaybill.setPackNum(baseEntity.getData().getPackageList().size());
        // 页面抽检执行新抽检逻辑
        InvokeResult<SpotCheckResult> spotCheckResult = spotCheckCurrencyService.checkExcess(convertToSpotCheckDto(condition));
        weightVolumeCheckOfB2bWaybill.setIsExcess(spotCheckResult.getData() == null ? null : spotCheckResult.getData().getExcessStatus());
        weightVolumeCheckOfB2bWaybill.setExcessType(spotCheckResult.getData() == null ? null : spotCheckResult.getData().getExcessType());
        weightVolumeCheckOfB2bWaybill.setIsMultiPack(spotCheckResult.getData() == null ? null : spotCheckResult.getData().getIsMultiPack());
        result.customMessage(spotCheckResult.getCode(), spotCheckResult.getMessage());
        return result;
    }

    private SpotCheckDto convertToSpotCheckDto(WeightVolumeCheckConditionB2b condition) {
        String waybillCode = WaybillUtil.getWaybillCode(condition.getWaybillOrPackageCode());
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(waybillCode);
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName());
        spotCheckDto.setWeight(condition.getWaybillWeight());
        Double length = condition.getWaybillLength();
        Double width = condition.getWaybillWidth();
        Double height = condition.getWaybillHeight();
        Double volume = condition.getWaybillVolume();
        spotCheckDto.setLength(length);
        spotCheckDto.setWidth(width);
        spotCheckDto.setHeight(height);
        if(volume == null &&
                (length != null && width != null && height != null)){
            volume = length * width * height;
        }
        spotCheckDto.setVolume(volume);
        String operateErp = condition.getLoginErp();
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(operateErp);
        spotCheckDto.setOrgId(baseStaff.getOrgId());
        spotCheckDto.setOrgName(baseStaff.getOrgName());
        spotCheckDto.setSiteCode(condition.getCreateSiteCode());
        spotCheckDto.setSiteName(baseStaff.getSiteName());
        spotCheckDto.setOperateUserId(baseStaff.getStaffNo());
        spotCheckDto.setOperateUserErp(operateErp);
        spotCheckDto.setOperateUserName(baseStaff.getStaffName());
        spotCheckDto.setDimensionType(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode());
        spotCheckDto.setExcessStatus(condition.getIsExcess());
        spotCheckDto.setExcessType(condition.getExcessType());
        List<String> urls = condition.getUrls();
        if(CollectionUtils.isNotEmpty(urls)){
            Map<String, String> picUtlMap = new HashMap<>();
            picUtlMap.put("total", StringUtils.join(condition.getUrls(), Constants.SEPARATOR_SEMICOLON));
            spotCheckDto.setPictureUrls(picUtlMap);
        }
        return spotCheckDto;
    }

    @Override
    public InvokeResult<String> dealExcessDataOfWaybill(WeightVolumeCheckConditionB2b param) {
        InvokeResult<String> result = new InvokeResult<>();
        try{
            InvokeResult<Boolean> spotCheckDealResult = spotCheckCurrencyService.spotCheckDeal(convertToSpotCheckDto(param));
            result.customMessage(spotCheckDealResult.getCode(), spotCheckDealResult.getMessage());
            return result;
        }catch (Exception e){
            log.error("参数:{}, 异常信息:{}", JsonHelper.toJson(param) , e.getMessage(), e);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"按运单抽检失败!");
        }
        result.setCode(Constants.SUCCESS_CODE);
        return result;
    }

    /**
     * 判断是否操作过抽检
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private boolean isSpotCheck(String waybillCode,Integer siteCode){
        try {
            String key = B2B_SPOT_CHECK_REDIS_KEY_PREFIX + "_" + waybillCode + "_" + siteCode;
            String redisValue = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(redisValue)){
               return Boolean.valueOf(redisValue);
            }
            WeightVolumeQueryCondition weightVolumeQueryCondition = new WeightVolumeQueryCondition();
            weightVolumeQueryCondition.setPackageCode(waybillCode);
            weightVolumeQueryCondition.setReviewSiteCode(siteCode);
            BaseEntity<List<WeightVolumeCollectDto>> entity = reportExternalService.getByParamForWeightVolume(weightVolumeQueryCondition);
            if(entity != null && entity.getCode() == 200
                    && CollectionUtils.isNotEmpty(entity.getData())){
                jimdbCacheService.setEx(key,String.valueOf(true),24, TimeUnit.HOURS);
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            log.error("判断运单【{}】、站点【{}】是否操作过抽检缓存异常",waybillCode,siteCode,e);
        }
        return false;
    }


    @Override
    public InvokeResult<String> checkRecordExist(String waybillCode, Integer siteCode) {
        InvokeResult<String> result = new InvokeResult<>();
        if (isSpotCheck(waybillCode, siteCode)) {
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "运单" + waybillCode + "已经进行过抽检，请勿重复操作!");
            return result;
        }
        result.setCode(Constants.SUCCESS_CODE);
        return result;
    }

}
