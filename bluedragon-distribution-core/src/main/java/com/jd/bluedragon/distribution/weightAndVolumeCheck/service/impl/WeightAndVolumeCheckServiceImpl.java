package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BusinessFinanceManager;
import com.jd.bluedragon.core.base.QuoteCustomerApiServiceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SystemEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jss.JingdongStorageService;
import com.jd.jss.client.Request;
import com.jd.jss.domain.ObjectListing;
import com.jd.jss.domain.ObjectSummary;
import com.jd.jss.http.JssInputStreamEntity;
import com.jd.jss.service.BucketService;
import com.jd.jss.service.ObjectService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WeightAndVolumeCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:48
 */
@Service("weightAndVolumeCheckService")
public class WeightAndVolumeCheckServiceImpl implements WeightAndVolumeCheckService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /** 对象存储 **/
    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";
    /** 预签名过期时间 */
    private static final Integer SIGNATURE_TIMEOUT = 2592000;

    @Value("${excess.execute.times}")
    private Integer times;

    /** 存储空间文件夹名称 */
    @Value("${jss.bucket.picture}")
    private String bucket;


    @Autowired
    @Qualifier("dmsWebJingdongStorageService")
    private JingdongStorageService dmsWebJingdongStorageService;

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    DmsBaseDictService dmsBaseDictService;

    @Autowired
    private BusinessFinanceManager businessFinanceManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("dmsWeightVolumeAbnormal")
    private DefaultJMQProducer dmsWeightVolumeAbnormal;

    @Autowired
    @Qualifier("dmsWeightVolumeExcess")
    private DefaultJMQProducer dmsWeightVolumeExcess;

    @Autowired
    private QuoteCustomerApiServiceManager quoteCustomerApiServiceManager;


    /**
     * 上传超标图片
     * @param imageName 文件名
     * @param imageSize 流的大小
     * @param inputStream 上传流
     * @return
     */
    @Override
    public void uploadExcessPicture(String imageName, long imageSize, InputStream inputStream) throws Exception {
        try {
            ObjectService objectService = dmsWebJingdongStorageService.bucket(bucket).object(imageName);
            JssInputStreamEntity entity =  new JssInputStreamEntity(inputStream, imageSize);
            Request.Builder builder = (Request.Builder) FieldUtils.readField(objectService, "builder", true);
            builder.entity(entity);
            objectService.put();
            inputStream.close();
            log.info("上传文件成功imageName:{},imageSize:{}", imageName, imageSize);
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException ioe){
                log.error("关闭输入流再异常：",ioe);
            }
        }
    }

    @Override
    public InvokeResult<String> searchPicture(String waybillCode,Integer siteCode,Integer isWaybillSpotCheck){
        InvokeResult<String> result = new InvokeResult<>();
        Map<String,List<String>> map = new LinkedHashMap<>();
        if(isWaybillSpotCheck!=null && isWaybillSpotCheck==1){
            //B网运单维度
            InvokeResult<List<String>> invokeResult = searchExcessPictureOfB2b(waybillCode, siteCode);
            if(invokeResult != null && !CollectionUtils.isEmpty(invokeResult.getData())){
                map.put(waybillCode,invokeResult.getData());
            }
            if(invokeResult != null){
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
            }
            result.setData(JsonHelper.toJson(map));
        }else {
            //B网包裹维度
            com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice
                    = waybillQueryManager.getDataByChoice(waybillCode, false, false, false, true);
            if(dataByChoice!=null && dataByChoice.getData()!=null
                    && !CollectionUtils.isEmpty(dataByChoice.getData().getPackageList())){
                List<DeliveryPackageD> packageList = dataByChoice.getData().getPackageList();
                for(DeliveryPackageD deliveryPackageD : packageList){
                    InvokeResult<List<String>> invokeResult = searchExcessPictureOfB2b(deliveryPackageD.getPackageBarcode(), siteCode);
                    if(invokeResult != null && !CollectionUtils.isEmpty(invokeResult.getData())){
                        map.put(deliveryPackageD.getPackageBarcode(),invokeResult.getData());
                    }
                }
            }
            result.setData(JsonHelper.toJson(map));
        }
        return result;
    }

    /**
     * 查看超标图片
     * @param prefixName 图片名前缀
     * @param siteCode 站点id
     * @return
     */
    @Override
    public InvokeResult<String> searchExcessPicture(String prefixName,Integer siteCode) {

        InvokeResult<String> result = new InvokeResult<String>();
        try{
            List<String> urls = getPictureUrl(prefixName);
            //获取最近的对应的图片并返回
            String excessPictureUrl = getRecentUrl(urls,siteCode);
            if("".equals(excessPictureUrl)){
                result.parameterError("图片未上传!"+prefixName);
                return result;
            }
            result.setData(excessPictureUrl);
        }catch (Exception e){
            log.error("{}|{}获取图片链接失败!",prefixName, siteCode, e);
            result.parameterError("查看图片失败!"+prefixName);
        }
        return result;
    }

    @Override
    public InvokeResult<List<String>> searchExcessPictureOfB2b(String prefixName, Integer siteCode) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        try {
            List<String> urls = getPictureUrl(prefixName);
            List<String> excessPictureUrls = getAllTypePictureUrl(urls,siteCode);
            if(excessPictureUrls.isEmpty()){
                result.parameterError("图片未上传!"+prefixName);
                return result;
            }
            result.setData(excessPictureUrls);
        }catch (Exception e){
            log.error("{}|{}获取图片链接失败!",prefixName, siteCode, e);
            result.parameterError("查看图片失败!"+prefixName);
        }

        return result;
    }

    /**
     * 获取所有单号前缀的图片链接
     * @param prefixName
     * @return
     */
    private List<String> getPictureUrl(String prefixName){
        List<String> urls = new ArrayList<>();
        ObjectListing objectListing = listObject(prefixName, null, 100);
        if(objectListing != null && objectListing.getObjectSummaries() != null &&
                !objectListing.getObjectSummaries().isEmpty()){
            for(ObjectSummary objectSummary : objectListing.getObjectSummaries()){
                URI uri = getURI(objectSummary.getKey());
                if(uri != null){
                    String uriString = uri.toString();
                    //将内部访问域名替换成外部访问域名
                    uriString = uriString.replaceAll(STORAGE_DOMAIN_LOCAL,STORAGE_DOMAIN_COM);
                    uri = URI.create(uriString);
                    if(uri != null){
                        urls.add(uri.toString());
                    }
                }
            }
        }
        return urls;
    }

    /**
     * 获取5种类型图片链接
     * @param urls
     * @param siteCode
     * @return
     */
    private List<String> getAllTypePictureUrl(List<String> urls, Integer siteCode) {
        List<String> list = new ArrayList<>();
        Map<String,Long> map1 = new HashMap<>();
        Map<String,Long> map2 = new HashMap<>();
        Map<String,Long> map3 = new HashMap<>();
        Map<String,Long> map4 = new HashMap<>();
        Map<String,Long> map5 = new HashMap<>();
        for(String url : urls){
            String[] packageCodeAndOperateTimes = getArrayByUrl(url);
            String createSiteCode = packageCodeAndOperateTimes[1];
            //图片类型 1:重量 2:长 3:宽 4:高 5:面单
            Integer type = Integer.valueOf(packageCodeAndOperateTimes[2]);
            String operateTime = packageCodeAndOperateTimes[3];
            if(packageCodeAndOperateTimes.length != 4 || !siteCode.equals(Integer.valueOf(createSiteCode))){
                break;
            }
            if(type == 1){
                map1.put(url,Long.parseLong(operateTime));
            }else if(type == 2){
                map2.put(url,Long.parseLong(operateTime));
            }else if(type == 3){
                map3.put(url,Long.parseLong(operateTime));
            }else if(type == 4){
                map4.put(url,Long.parseLong(operateTime));
            }else if(type == 5){
                map5.put(url,Long.parseLong(operateTime));
            }
        }
        list.add(getRecentUrlOfB2b(map1));
        list.add(getRecentUrlOfB2b(map2));
        list.add(getRecentUrlOfB2b(map3));
        list.add(getRecentUrlOfB2b(map4));
        list.add(getRecentUrlOfB2b(map5));

        return list;
    }

    private String getRecentUrlOfB2b(Map<String,Long> map){
        String recentUrl = "";
        Object[] objects = map.values().toArray();
        Arrays.sort(objects);
        for(String url : map.keySet()){
            if(map.get(url) == objects[objects.length-1]){
                return url;
            }
        }
        return recentUrl;
    }

    private String getRecentUrl(List<String> urls,Integer siteCode){
        String recentUrl = "";
        try{
            if(urls.size() == 0){
                return recentUrl;
            }else if(urls.size() == 1){
                String[] packageCodeAndOperateTimes = getArrayByUrl(urls.get(0));
                if(packageCodeAndOperateTimes.length == 3){
                    return urls.get(0);
                }
                return recentUrl;
            }else{

                Map<String,Long> map = new HashMap<>();
                for(String url : urls){
                    String[] packageCodeAndOperateTimes = getArrayByUrl(url);
                    String operateTime = "";
                    if(packageCodeAndOperateTimes.length == 3){
                        String siteCodeFromOSS = packageCodeAndOperateTimes[1];
                        if(siteCodeFromOSS.equals(siteCode.toString())){
                            operateTime = packageCodeAndOperateTimes[packageCodeAndOperateTimes.length - 1];
                        }
                    }else{
                        break;
                    }
                    if(!"".equals(operateTime)){
                        long l = Long.parseLong(operateTime);
                        map.put(url,l);
                    }
                }
                Object[] objects = map.values().toArray();
                Arrays.sort(objects);
                for(String url : map.keySet()){
                    if(map.get(url) == objects[objects.length-1]){
                        recentUrl = url;
                        break;
                    }
                }
                return recentUrl;
            }
        }catch (Exception e){
            log.error("获取图片路径异常!");
            return recentUrl;
        }

    }

    private String[]  getArrayByUrl(String url) {
        String[] splits = url.split("/");
        String pictureName = splits[splits.length - 1];
        String[] pictureNames = pictureName.split("\\.");
        String pictureNamePrefix = pictureNames[0];
        return pictureNamePrefix.split("_");
    }


    /**
     * 发消息并更新es
     * @param packageCode
     * @param siteCode
     * @param uploadTime
     * @param siteCode
     * @param reviewDate
     */
    public void sendMqAndUpdate(String packageCode, Integer siteCode, Long uploadTime,String reviewDate){
        AbnormalPictureMq abnormalPictureMq = new AbnormalPictureMq();
        try{
            abnormalPictureMq.setAbnormalId(packageCode+"_"+reviewDate);
            abnormalPictureMq.setWaybillCode(packageCode);
            abnormalPictureMq.setUploadTime(uploadTime);
            //查存储空间获取图片链接
            String pictureAddress;
            InvokeResult<String> result = searchExcessPicture(abnormalPictureMq.getWaybillCode(),siteCode);
            if(result != null && result.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
                pictureAddress= result.getData();
            }else{
                log.warn("查看超标图片查询异常:{}|{}，异常信息：{}",abnormalPictureMq.getWaybillCode(),siteCode,JsonHelper.toJson(result));
                return;
            }
            //更新es数据
            WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
            dto.setPackageCode(packageCode);
            dto.setReviewSiteCode(siteCode);
            dto.setPictureAddress(pictureAddress);
            dto.setIsHasPicture(1);
            reportExternalService.updateForWeightVolume(dto);
            if(!StringHelper.isEmpty(pictureAddress)){
                abnormalPictureMq.setExcessPictureAddress(pictureAddress);
                this.log.info("发送MQ[{}],业务ID[{}] ",dmsWeightVolumeAbnormal.getTopic(),abnormalPictureMq.getWaybillCode());
                dmsWeightVolumeAbnormal.send(abnormalPictureMq.getAbnormalId(), JsonHelper.toJson(abnormalPictureMq));
            }

        }catch (Exception e){
            log.error("异常消息发送失败{}",abnormalPictureMq.getWaybillCode(), e);
        }
    }

    /**
     * 称重体积数据处理
     * @param packWeightVO
     * @param result
     */
    @Override
    public InvokeResult<Boolean> insertAndSendMq(PackWeightVO packWeightVO,WeightVolumeCollectDto weightVolumeCollectDto, InvokeResult<Boolean> result) {

        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        assemble(packWeightVO, weightVolumeCollectDto);

        //2.复核与计费比较
        Double reviewLengthStr = keeTwoDecimals(packWeightVO.getLength());
        Double reviewWidthStr = keeTwoDecimals(packWeightVO.getWidth());
        Double reviewHighStr = keeTwoDecimals(packWeightVO.getHigh());
        Double reviewWeightStr = keeTwoDecimals(packWeightVO.getWeight());
        Double reviewVolume = keeTwoDecimals(reviewLengthStr*reviewWidthStr*reviewHighStr);
        weightVolumeCollectDto.setReviewLWH(reviewLengthStr+"*"+reviewWidthStr+"*"+reviewHighStr);
        weightVolumeCollectDto.setReviewWeight(reviewWeightStr);
        weightVolumeCollectDto.setReviewVolume(reviewVolume);

        abnormalResultMq.setSource(SystemEnum.DMS.getCode());
        abnormalResultMq.setBusinessType(1);
        abnormalResultMq.setReviewLength(reviewLengthStr);
        abnormalResultMq.setReviewWidth(reviewWidthStr);
        abnormalResultMq.setReviewHeight(reviewHighStr);

        String waybillCode = WaybillUtil.getWaybillCode(packWeightVO.getCodeStr());
        try{
            //计费信息
            double billingWeight = 0;
            double billingVolume = 0;
            ResponseDTO<BizDutyDTO> responseDto = businessFinanceManager.queryDutyInfo(waybillCode);
            if(responseDto != null && responseDto.getStatusCode() == 0 && responseDto.getData() != null){
                weightVolumeCollectDto.setBillingOrgCode(Integer.parseInt(responseDto.getData().getFirstLevelId()==null?"-1":responseDto.getData().getFirstLevelId()));
                weightVolumeCollectDto.setBillingOrgName(responseDto.getData().getFirstLevelName());
                weightVolumeCollectDto.setBillingDeptCode(Integer.parseInt(responseDto.getData().getSecondLevelId()==null?"-1":responseDto.getData().getSecondLevelId()));
                weightVolumeCollectDto.setBillingDeptName(responseDto.getData().getSecondLevelName());
                weightVolumeCollectDto.setBillingErp(responseDto.getData().getDutyErp());
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(responseDto.getData().getDutyErp());
                weightVolumeCollectDto.setBillingCompany(dto==null?null:dto.getSiteName());

                abnormalResultMq.setFirstLevelId(responseDto.getData().getFirstLevelId());
                abnormalResultMq.setFirstLevelName(responseDto.getData().getFirstLevelName());
                abnormalResultMq.setSecondLevelId(responseDto.getData().getSecondLevelId());
                abnormalResultMq.setSecondLevelName(responseDto.getData().getSecondLevelName());
                abnormalResultMq.setThreeLevelId(responseDto.getData().getThreeLevelId());
                abnormalResultMq.setThreeLevelName(responseDto.getData().getThreeLevelName());
                abnormalResultMq.setWeight(responseDto.getData().getWeight());
                abnormalResultMq.setVolume(responseDto.getData().getVolume());
                abnormalResultMq.setDutyType(responseDto.getData().getDutyType());
                abnormalResultMq.setReviewDutyType(responseDto.getData().getDutyType());
                abnormalResultMq.setDutyErp(responseDto.getData().getDutyErp());
                abnormalResultMq.setBusinessObjectId(responseDto.getData().getBusinessObjectId());
                abnormalResultMq.setBusinessObject(responseDto.getData().getBusinessObject());

                billingWeight = responseDto.getData().getWeight()==null?0:responseDto.getData().getWeight().doubleValue();
                billingVolume = responseDto.getData().getVolume()==null?0:responseDto.getData().getVolume().doubleValue();
                weightVolumeCollectDto.setBillingWeight(billingWeight);
                weightVolumeCollectDto.setBillingVolume(billingVolume);
            }

            //复核重泡比

            //根据青龙商家ID查询商家信息
            //返回值中的volumeFeeType（泡重比类型），当volumeFeeType的值为1.则按体积；当volumeFeeType的值2，则按泡重比；当volumeFeeType的值为空或0，则按重量
            QuoteCustomerDto quoteCustomerDto = quoteCustomerApiServiceManager.queryCustomerById(weightVolumeCollectDto.getBusiCode());
            Integer volumeRate = 8000;
            if(quoteCustomerDto != null){
                //取重泡比，当重泡比为null或0时，volumeRate取默认值8000
                volumeRate = quoteCustomerDto.getVolumeRate();
                if(volumeRate == null || volumeRate == 0){
                    volumeRate = 8000;
                }

                if(quoteCustomerDto.getVolumeFeeType() == Constants.VOLUMEFEETYPE_VOLUME){
                    Double reviewVolumeWeight = reviewVolume/volumeRate;
                    Double diffVolumeWeight = Math.abs(reviewVolumeWeight - billingVolume/volumeRate);
                    if(isExcess(reviewVolumeWeight,diffVolumeWeight)){
                        result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                        result.setData(false);
                        result.setMessage("此次操作的体积重量为"+reviewVolumeWeight+"kg,计费的体积重量为"+billingVolume/volumeRate+"kg，"
                                +"经校验误差值"+diffVolumeWeight+"kg已超出规定"+ (reviewVolumeWeight <=5 ? "0.3":reviewVolumeWeight<=20 ?
                                "0.5":reviewVolumeWeight<=50 ? "1":reviewVolumeWeight * 0.02)+"kg！");
                        weightVolumeCollectDto.setIsExcess(1);
                        weightVolumeCollectDto.setVolumeWeightIsExcess(1);
                    }
                }else if(quoteCustomerDto.getVolumeFeeType() == Constants.VOLUMEFEETYPE_VOLUMERATE){
                    Double reviewVolumeWeight =  keeTwoDecimals(reviewVolume/volumeRate);
                    //maxReviewWeight为抽检重量与抽检体积重量中较大的
                    Double maxReviewWeight = reviewWeightStr > reviewVolumeWeight ? reviewWeightStr : reviewVolumeWeight;

                    Double billVolumeWeight = keeTwoDecimals(billingVolume/volumeRate);
                    // maxBillingWeight为计费重量与计费体积重量中较大的。
                    Double maxBillingWeight = billingWeight > billVolumeWeight ? billingWeight : billVolumeWeight;

                    // diffOfWeight为抽检中的较大质量与计费中的较大质量之间的差异
                    double diffOfWeight = Math.abs(keeTwoDecimals(maxReviewWeight - maxBillingWeight));

                    if(isExcess(maxReviewWeight,diffOfWeight)){
                        result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                        result.setData(false);
                        result.setMessage("此次操作的泡重比为"+reviewVolumeWeight+"kg,计费的泡重比为"+billVolumeWeight+"kg，"
                                +"经校验误差值"+diffOfWeight+"kg已超出规定"+ (reviewVolumeWeight <=5 ? "0.3":reviewVolumeWeight<=20 ?
                                "0.5":reviewVolumeWeight<=50 ? "1":reviewVolumeWeight * 0.02)+"kg！");
                        weightVolumeCollectDto.setIsExcess(1);
                    }

                }else if(quoteCustomerDto.getVolumeFeeType() == null || quoteCustomerDto.getVolumeFeeType() == 0){
                    Double diffWeight = Math.abs(reviewWeightStr - billingWeight);
                    if(isExcess(reviewWeightStr,diffWeight)){
                        result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                        result.setData(false);
                        result.setMessage("此次操作的复核重量为"+reviewWeightStr+"kg,计费的重量为"+billingWeight+"kg，"
                                +"经校验误差值"+diffWeight+"kg已超出规定"+ (reviewWeightStr <=5 ? "0.3":reviewWeightStr<=20 ?
                                "0.5":reviewWeightStr<=50 ? "1":reviewWeightStr * 0.02)+"kg！");
                        weightVolumeCollectDto.setIsExcess(1);
                    }
                }
            }

            //当计费重量或计费体积为0时，体积重量抽检超标
            if(billingWeight == 0 || billingVolume == 0){
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setData(false);
                result.setMessage("计费重量/体积为0或空，无法进行校验");
                weightVolumeCollectDto.setIsExcess(1);
            }


            weightVolumeCollectDto.setWeightDiff(new DecimalFormat("#0.00").format(reviewWeightStr - billingWeight));
            StringBuilder diffStandardOfWeight = new StringBuilder("");
            if(reviewWeightStr <= 5){
                diffStandardOfWeight.append("重量:0.3");
            }else if(reviewWeightStr > 5 && reviewWeightStr <= 20){
                diffStandardOfWeight.append("重量:0.5");
            }else if(reviewWeightStr > 20 && reviewWeightStr <= 50){
                diffStandardOfWeight.append("重量:1");
            }else if(reviewWeightStr > 50){
                diffStandardOfWeight.append("重量:2%");
            }

            weightVolumeCollectDto.setReviewVolumeWeight(getVolumeAndWeight(reviewVolume/volumeRate));
            weightVolumeCollectDto.setBillingVolumeWeight(getVolumeAndWeight(billingVolume/volumeRate));
            if(reviewVolume/volumeRate <= 5){
                diffStandardOfWeight.append("体积重量:0.3");
            }else if(reviewVolume/volumeRate > 5 && reviewVolume/volumeRate <= 20){
                diffStandardOfWeight.append("体积重量:0.5");
            }else if(reviewVolume/volumeRate > 20 && reviewVolume/volumeRate <= 50){
                diffStandardOfWeight.append("体积重量:1");
            }else if(reviewVolume/volumeRate > 50){
                diffStandardOfWeight.append("体积重量:2%");
            }
            weightVolumeCollectDto.setDiffStandard(diffStandardOfWeight.toString());
            weightVolumeCollectDto.setVolumeWeightDiff(new DecimalFormat("#0.00").format(reviewVolume/volumeRate - billingVolume/volumeRate));
            setProductType(weightVolumeCollectDto);
            //将重量体积实体存入es中
            reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
        }catch (Exception e){
            log.error("包裹称重提示警告信息异常:{}", JsonHelper.toJson(packWeightVO),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        //超标给fxm发消息
        if(weightVolumeCollectDto.getIsExcess() == 1){
            sendMqToFXM(weightVolumeCollectDto,abnormalResultMq);
        }
        return result;

    }

    /**
     * 重量超标判断(true:超标；false：未超标)
     */
    private boolean isExcess(double weight,double diffOfWeight){
        //当抽拣重量<=5kg时，重量差异>0.3kg为超标；当抽拣重量在5-20kg之间时，重量差异>0.5kg为超标；
        //当抽拣重量在20-50kg之间时，重量差异>1kg为超标；当抽拣重量>50kg之间时，重量差异>(0.02*抽检重量)为超标；
        if((weight <= 5 && diffOfWeight> 0.3) || (weight > 5 && weight <= 20 && diffOfWeight> 0.5)
                || (weight > 20 && weight <= 50 && diffOfWeight> 1)
                || (weight > 50 && diffOfWeight > weight * 0.02)){
            return true;
        }
        return false;
    }

    /**
     * 保留两位小数
     * @param param
     */
    private Double keeTwoDecimals(Double param) {
        if(param == null){
            return 0.00;
        }
        param = (double)Math.round(param*100)/100;
        return param;
    }

    /**
     * 体积重量取值
     * */
    private Double getVolumeAndWeight(Double param) {
        Double temp = keeTwoDecimals(param);
        if(temp == 0.00){
            return 0.00;
        }
        if(temp > 0.00 && temp < 0.01){
            return 0.01;
        }
        return temp;
    }

    /**
     * 组装称重计费实体
     * @param packWeightVO
     * @param weightVolumeCollectDto
     */
    private void assemble(PackWeightVO packWeightVO, WeightVolumeCollectDto weightVolumeCollectDto) {
        weightVolumeCollectDto.setSpotCheckType(0);//C网
        weightVolumeCollectDto.setReviewDate(new Date());
        weightVolumeCollectDto.setReviewLWH(packWeightVO.getLength()+"*"+packWeightVO.getWidth()+"*"+packWeightVO.getHigh());
        weightVolumeCollectDto.setReviewWeight(packWeightVO.getWeight());
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()),
                true, false, false, false);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            weightVolumeCollectDto.setBusiCode(baseEntity.getData().getWaybill().getBusiId());
            weightVolumeCollectDto.setBusiName(baseEntity.getData().getWaybill().getBusiName());
            weightVolumeCollectDto.setBusiCode(baseEntity.getData().getWaybill().getBusiId());
            if(BusinessUtil.isSignChar(baseEntity.getData().getWaybill().getWaybillSign(),56,'1')){
                //信任商家
                weightVolumeCollectDto.setIsTrustBusi(1);
            }else if(BusinessUtil.isSignInChars(baseEntity.getData().getWaybill().getWaybillSign(),56,'0','2','3')) {
                //普通商家
                weightVolumeCollectDto.setIsTrustBusi(0);
            }else {
                //其他
                weightVolumeCollectDto.setIsTrustBusi(-1);
            }
        }
        BaseStaffSiteOrgDto baseOrgDto = baseMajorManager.getBaseSiteBySiteId(packWeightVO.getOperatorSiteCode());
        if(baseOrgDto != null && baseOrgDto.getSiteType() == 64){
            if(BusinessUtil.isSortOrTransport(baseOrgDto.getSubType()) == 1){
                weightVolumeCollectDto.setReviewSubType(1);
            }else if(BusinessUtil.isSortOrTransport(baseOrgDto.getSubType()) == 0){
                weightVolumeCollectDto.setReviewSubType(0);
            }else{
                weightVolumeCollectDto.setReviewSubType(-1);
            }
        }
        weightVolumeCollectDto.setReviewOrgCode(packWeightVO.getOrganizationCode());
        weightVolumeCollectDto.setReviewOrgName(packWeightVO.getOrganizationName());
        weightVolumeCollectDto.setReviewSiteCode(packWeightVO.getOperatorSiteCode());
        weightVolumeCollectDto.setReviewSiteName(packWeightVO.getOperatorSiteName());
        weightVolumeCollectDto.setReviewErp(packWeightVO.getErpCode());
        weightVolumeCollectDto.setIsExcess(0);
        weightVolumeCollectDto.setVolumeWeightIsExcess(0);
        //设置无图片无图片链接
        weightVolumeCollectDto.setIsHasPicture(0);
        weightVolumeCollectDto.setPictureAddress("");
    }

    /**
     * 给fxm发超标消息
     * @param weightVolumeCollectDto
     * @param abnormalResultMq
     */
    private void sendMqToFXM(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        try{
            abnormalResultMq.setId(weightVolumeCollectDto.getPackageCode() + "_" +weightVolumeCollectDto.getReviewDate().getTime());
            abnormalResultMq.setAbnormalId(weightVolumeCollectDto.getPackageCode() + "_" +weightVolumeCollectDto.getReviewDate().getTime());
            abnormalResultMq.setFrom(SystemEnum.DMS.getCode().toString());
            if(abnormalResultMq.getDutyType() != null){
                if(abnormalResultMq.getDutyType() == 2 || abnormalResultMq.getDutyType() == 3){
                    //分拣、站点发给下游判责
                    abnormalResultMq.setTo(SystemEnum.PANZE.getCode().toString());
                }else if(abnormalResultMq.getDutyType()==1){
                    //仓发给下游质控
                    abnormalResultMq.setTo(SystemEnum.ZHIKONG.getCode().toString());
                }
            }
            abnormalResultMq.setBillCode(weightVolumeCollectDto.getPackageCode());
            abnormalResultMq.setReviewDate(weightVolumeCollectDto.getReviewDate());
            abnormalResultMq.setReviewMechanismType(weightVolumeCollectDto.getReviewSubType());
            abnormalResultMq.setReviewErp(weightVolumeCollectDto.getReviewErp());
            abnormalResultMq.setReviewWeight(weightVolumeCollectDto.getReviewWeight());
            abnormalResultMq.setReviewVolume(weightVolumeCollectDto.getReviewVolume());
            abnormalResultMq.setReviewFirstLevelId(weightVolumeCollectDto.getReviewOrgCode());
            abnormalResultMq.setReviewFirstLevelName(weightVolumeCollectDto.getReviewOrgName());
            abnormalResultMq.setReviewSecondLevelId(weightVolumeCollectDto.getReviewSiteCode());
            abnormalResultMq.setReviewSecondLevelName(weightVolumeCollectDto.getReviewSiteName());

            abnormalResultMq.setDiffStandard(weightVolumeCollectDto.getDiffStandard());
            abnormalResultMq.setWeightDiff(Double.parseDouble(weightVolumeCollectDto.getWeightDiff()));
            abnormalResultMq.setVolumeDiff(Double.parseDouble(weightVolumeCollectDto.getVolumeWeightDiff()));
            abnormalResultMq.setIsExcess(weightVolumeCollectDto.getIsExcess());
            abnormalResultMq.setPictureAddress(weightVolumeCollectDto.getPictureAddress());
            //默认值:认责不判责
            abnormalResultMq.setIsAccusation(0);
            abnormalResultMq.setIsNeedBlame(1);

            abnormalResultMq.setOperateTime(weightVolumeCollectDto.getReviewDate());

            this.log.info("发送MQ[{}],业务ID[{}]",dmsWeightVolumeExcess.getTopic(),abnormalResultMq.getBillCode());
            dmsWeightVolumeExcess.send(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
        }catch (Exception e){
            this.log.error("发送超标异常mq给fxm失败:{}", weightVolumeCollectDto.getPackageCode() , e);
        }
    }

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    @Override
    public PagerResult<WeightVolumeCollectDto> queryByCondition(WeightAndVolumeCheckCondition condition) {

        PagerResult<WeightVolumeCollectDto>  result = new PagerResult<>();

        try{
            Pager<WeightVolumeQueryCondition> pager = new Pager<>();
            WeightVolumeQueryCondition transform = transform(condition);
            pager.setSearchVo(transform);
            pager.setPageNo(condition.getOffset()/condition.getLimit() + 1);
            pager.setPageSize(condition.getLimit());
            BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity = reportExternalService.getPagerByConditionForWeightVolume(pager);
            if(baseEntity.getCode() == BaseEntity.CODE_SUCCESS){
                result.setTotal(baseEntity.getData().getTotal().intValue());
                result.setRows(baseEntity.getData().getData());
            }else{
                log.warn("{}根据查询条件查询es失败,失败原因:{}",JsonHelper.toJson(condition),baseEntity.getMessage());
                result.setTotal(0);
                result.setRows(new ArrayList<WeightVolumeCollectDto>());
            }
        }catch (Exception e){
            log.error("服务异常,根据查询条件查询es失败:{}",JsonHelper.toJson(condition),e);
        }

        return result;
    }

    /**
     * 导出
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("运单号");
        heads.add("扫描条码");
        heads.add("业务类型");
        heads.add("产品标识");
        heads.add("商家ID");
        heads.add("商家名称");
        heads.add("信任商家");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("机构类型");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积重量");
        heads.add("计费操作区域");
        heads.add("计费操作片区");
        heads.add("计费操作单位");
        heads.add("计费操作人ERP");
        heads.add("计费重量kg");
        heads.add("计费体积cm³");
        heads.add("计费体积重量");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("体积重量是否超标");
        heads.add("误差标准值");
        heads.add("是否超标");
        heads.add("有无图片");
        heads.add("图片链接");
        resList.add(heads);
        WeightVolumeQueryCondition transform = transform(condition);
        BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(transform);
        if(baseEntity.getData() != null && baseEntity.getData().size() > 0){
            List<WeightVolumeCollectDto> list = baseEntity.getData();
            //表格信息
            for(WeightVolumeCollectDto weightVolumeCollectDto : list){
                List<Object> body = Lists.newArrayList();
                body.add(weightVolumeCollectDto.getReviewDate() == null ? null : DateHelper.formatDate(weightVolumeCollectDto.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(weightVolumeCollectDto.getWaybillCode());
                body.add(weightVolumeCollectDto.getPackageCode());
                body.add(weightVolumeCollectDto.getSpotCheckType()==null?"C网":(weightVolumeCollectDto.getSpotCheckType()==1?"B网":"C网"));
                body.add(weightVolumeCollectDto.getProductTypeName());
                body.add(weightVolumeCollectDto.getBusiCode());
                body.add(weightVolumeCollectDto.getBusiName());
                body.add(weightVolumeCollectDto.getIsTrustBusi()==null?"":weightVolumeCollectDto.getIsTrustBusi()==1?"是":"否");
                body.add(weightVolumeCollectDto.getReviewOrgName());
                body.add(weightVolumeCollectDto.getReviewSiteName());
                body.add((weightVolumeCollectDto.getReviewSubType()==null || weightVolumeCollectDto.getReviewSubType()==-1)?"":weightVolumeCollectDto.getReviewSubType()==1?"分拣中心":"转运中心");
                body.add(weightVolumeCollectDto.getReviewErp());
                body.add(weightVolumeCollectDto.getReviewWeight());
                body.add(weightVolumeCollectDto.getReviewLWH());
                body.add(weightVolumeCollectDto.getReviewVolumeWeight());
                body.add(weightVolumeCollectDto.getBillingOrgName());
                body.add(weightVolumeCollectDto.getBillingDeptName());
                body.add(weightVolumeCollectDto.getBillingCompany());
                body.add(weightVolumeCollectDto.getBillingErp());
                body.add(weightVolumeCollectDto.getBillingWeight());
                body.add(weightVolumeCollectDto.getBillingVolume());
                body.add(weightVolumeCollectDto.getBillingVolumeWeight());
                body.add(weightVolumeCollectDto.getWeightDiff());
                body.add(weightVolumeCollectDto.getVolumeWeightDiff());
                body.add(weightVolumeCollectDto.getVolumeWeightIsExcess()==null?"":weightVolumeCollectDto.getVolumeWeightIsExcess()==1?"超标":"未超标");
                body.add(weightVolumeCollectDto.getDiffStandard());
                body.add(weightVolumeCollectDto.getIsExcess()==null?"":weightVolumeCollectDto.getIsExcess()==1?"超标":"未超标");
                body.add(weightVolumeCollectDto.getIsHasPicture()==null?"":weightVolumeCollectDto.getIsHasPicture()==1?"有":"无");
                body.add(StringHelper.isEmpty(weightVolumeCollectDto.getPictureAddress())?"":weightVolumeCollectDto.getPictureAddress());
                resList.add(body);
            }
        }else{
            List<Object> list = new ArrayList<>();
            list.add(baseEntity.getMessage());
            resList = new ArrayList<>();
            resList.add(list);
        }
        return  resList;
    }

    /**
     * 查询条件转换
     * */
    private WeightVolumeQueryCondition transform(WeightAndVolumeCheckCondition condition) {
        WeightVolumeQueryCondition newCondition = new WeightVolumeQueryCondition();
        newCondition.setStartTime(DateHelper.parseDateTime(condition.getStartTime()));
        newCondition.setEndTime(DateHelper.parseDateTime(condition.getEndTime()));
        newCondition.setReviewOrgCode(condition.getReviewOrgCode()==null?null:condition.getReviewOrgCode().intValue());
        newCondition.setReviewSiteCode(condition.getCreateSiteCode()==null?null:condition.getCreateSiteCode().intValue());
        newCondition.setIsExcess(condition.getIsExcess());
        newCondition.setWaybillCode(condition.getWaybillCode());
        newCondition.setBarCode(condition.getWaybillOrPackCode());
        newCondition.setBusiName(condition.getBusiName());
        newCondition.setReviewErp(condition.getReviewErp());
        newCondition.setBillingErp(condition.getBillingErp());
        newCondition.setSpotCheckType(condition.getSpotCheckType());
        return newCondition;
    }


    /**
     * 列出对象(最多返回1千 )
     * @param prefix 前缀
     * @param marker 标记 从marker开始获取列表
     * @param maxKeys 返回 Object 信息的数量，最大为 1000
     */
    public ObjectListing listObject(String prefix, String marker, int maxKeys ){
        if(maxKeys <= 0 ){
            maxKeys = 1000;
        }
        BucketService bucketService = dmsWebJingdongStorageService.bucket(bucket);
        if(StringUtils.isNotBlank(prefix)){
            bucketService.prefix(prefix);
        }
        if(StringUtils.isNotBlank(marker)){
            bucketService.marker(marker);
        }
        return  bucketService.maxKeys(maxKeys).listObject();
    }

    /**
     * 获取对应版本的下载地址
     * 抛出异常
     */
    public URI getURI(String keyName){
        //获得带有预签名的下载地址timeout为30天
        URI uri = dmsWebJingdongStorageService.bucket(bucket).object(keyName).generatePresignedUrl(SIGNATURE_TIMEOUT);
        return uri;
    }

    /**
     * 根据运单号查询waybillSign，
     * 当waybillSign的40为0时，根据waybillSign的31位的值填入产品类型
     *当waybillSign的40为1-5时，根据waybillSign的80位的值填入产品类型
     */
    @Override
    public void setProductType(WeightVolumeCollectDto weightVolumeCollectDto) {
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(Constants.PRODUCT_PARENT_ID);
        HashMap<String, DmsBaseDict> map = new HashMap<String, DmsBaseDict>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getTypeName(), list.get(i));
        }
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(weightVolumeCollectDto.getWaybillCode());
        if (waybill != null) {
            String waybillSign = waybill.getWaybillSign();
            DmsBaseDict dmsBaseDict = null;
            //当waybillSign的40位为0时，根据waybillSign的31位值判断产品类型
            if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, '0')) {
                dmsBaseDict = map.get("31" + "-" + waybillSign.charAt(30));
                if (dmsBaseDict != null) {
                    weightVolumeCollectDto.setProductTypeCode(dmsBaseDict.getTypeCode());
                    weightVolumeCollectDto.setProductTypeName(dmsBaseDict.getMemo());
                }
                //当waybillSign的40位为1，2，3，4，5时，根据waybillSign的80位值判断产品类型
            } else if (BusinessUtil.isFastTrans(waybillSign)) {
                dmsBaseDict = map.get("80" + "-" + waybillSign.charAt(79));
                if (dmsBaseDict != null) {
                    weightVolumeCollectDto.setProductTypeCode(dmsBaseDict.getTypeCode());
                    weightVolumeCollectDto.setProductTypeName(dmsBaseDict.getMemo());
                }
            }
        }

    }
}
