package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.alibaba.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jss.JingdongStorageService;
import com.jd.jss.client.Request;
import com.jd.jss.domain.ObjectListing;
import com.jd.jss.domain.ObjectSummary;
import com.jd.jss.http.JssInputStreamEntity;
import com.jd.jss.service.BucketService;
import com.jd.jss.service.ObjectService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.*;
import com.jd.ql.dms.report.domain.Enum.*;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: WeightAndVolumeCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:48
 */
@Service("weightAndVolumeCheckService")
public class WeightAndVolumeCheckServiceImpl implements WeightAndVolumeCheckService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * C网抽检是否下发MQ缓存前缀
     * */
    private static final String B2C_SPOT_CHECK_ISSUE = "B2C_SPOT_CHECK_ISSUE_";

    @Value("${spotCheck.fourSumLWH:70}")
    public String fourSumLWH;

    @Value("${spotCheck.multiplePackageVolumeStandard:12700}")
    public String multiplePackageVolumeStandard;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * C网超标下发MQ天数
     * */
    @Value("${b2c.spotCheck.interval.days:3}")
    private int defaultIntervalDays;

    /** 对象存储 **/
    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";
    /** 预签名过期时间 */
    private static final Integer SIGNATURE_TIMEOUT = 2592000;

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

    /**
     * 包裹称重抽检记录有效时间（单位为天）
     */
    private Integer packageWeightCheckRecordExpireTime = 7;

    /**
     * 包裹抽检数据运单保存的包裹数据集合
     */
    private Integer waybillPackageWeightCheckRecordExpireTime = 30;
    /**
     * 抽检数据下发fxm缓存过期时间
     */
    private Integer cacheFxmSendWaybillExpireTime = 15;

    @Autowired
    @Qualifier("dmsWeightVolumeExcess")
    private DefaultJMQProducer dmsWeightVolumeExcess;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    @Qualifier("weightAndVolumeCheckHandleProducer")
    private DefaultJMQProducer weightAndVolumeCheckHandleProducer;

    @Autowired
    @Qualifier("weightAndVolumeCheckAHandler")
    private WeightAndVolumeCheckStandardHandler weightAndVolumeCheckAHandler;

    @Autowired
    @Qualifier("weightAndVolumeCheckBHandler")
    private WeightAndVolumeCheckStandardHandler weightAndVolumeCheckBHandler;

    @Autowired
    private WeightAndVolumeCPureTypeCheckAForDwsHandler weightAndVolumeCPureTypeCheckAForDwsHandler;

    @Autowired
    private WeightAndVolumeCPureTypeCheckBForDwsHandler weightAndVolumeCPureTypeCheckBForDwsHandler;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private LogEngine logEngine;

    /**
     * 不允许第二个分拣中心称重的返回码
     */
    private final int NOT_ALLOW_SECOND_SITE_CHECK_CODE = 30001;
    /**
     * 抽检发现超标返回码
     */
    private final int CHECK_OVER_STANDARD_CODE = 30002;
    /**
     * 无计费数据返回码
     */
    private final int NO_CHARGE_SYSTEM_DATA_CODE = 30003;

    /**
     * standard  超标校验对象为空 返回码
     */
    private final  int STANDARD_ERROR_CODE = 30004;


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
    public InvokeResult<String> searchPicture(String waybillCode,Integer siteCode,Integer isWaybillSpotCheck,String fromSource){
        InvokeResult<String> result = new InvokeResult<>();
        Map<String,List<String>> map = new LinkedHashMap<>();
        if(isWaybillSpotCheck!=null && isWaybillSpotCheck==1){
            if(SpotCheckSourceEnum.SPOT_CHECK_ANDROID.name().equals(fromSource)){
                map.put(waybillCode,spotCheckPdaPictures(waybillCode,siteCode));
                result.setData(JsonHelper.toJson(map));
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                return result;
            }
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
     * @param barCode 单号
     * @param siteCode 站点id
     * @return
     */
    @Override
    public InvokeResult<String> searchExcessPicture(String barCode,Integer siteCode) {

        InvokeResult<String> result = new InvokeResult<String>();
        try{
            String prefixName = barCode + Constants.UNDERLINE_FILL +siteCode + Constants.UNDERLINE_FILL;
            //获取最近的对应的图片并返回
            String excessPictureUrl = searchPictureUrlRecent(prefixName);
            if(StringUtils.isEmpty(excessPictureUrl)){
                result.parameterError("图片未上传!"+prefixName);
                return result;
            }
            result.setData(excessPictureUrl);
        }catch (Exception e){
            log.error("根据单号{}站点{}获取图片链接失败!", barCode, siteCode, e);
            result.parameterError("查看图片失败!"+ barCode);
        }
        return result;
    }

    /**
     * 查看超标图片（一单多件）
     * @return 图片列表
     */
    @Override
    public InvokeResult<Pager<WeightVolumePictureDto>> searchPicture4MultiplePackage(Pager<WeightVolumeQueryCondition> weightVolumeQueryConditionPager){
        InvokeResult<Pager<WeightVolumePictureDto>> result = new InvokeResult<>();
        final WeightVolumeQueryCondition waybillSpotCheckCondition = weightVolumeQueryConditionPager.getSearchVo();
        Pager<WeightVolumePictureDto> dataPager = new Pager<>();
        List<WeightVolumePictureDto> dataList = new ArrayList<>();
        try {
            // 先查询运单所有包裹抽检记录
            if(waybillSpotCheckCondition.getReviewSiteCode() == null){
                result.parameterError("参数错误，reviewSiteCode不能为空");
                return result;
            }
            if(waybillSpotCheckCondition.getWaybillCode() == null){
                result.parameterError("参数错误，waybillCode不能为空");
                return result;
            }
            int siteCode = waybillSpotCheckCondition.getReviewSiteCode();
            waybillSpotCheckCondition.setIsHasPicture(Constants.YN_YES);
            waybillSpotCheckCondition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());

            final BaseEntity<Pager<String>> packageCodesResult = reportExternalService.getSpotCheckPackageCodesByCondition(weightVolumeQueryConditionPager);
            final Pager<String> packageCodePager = packageCodesResult.getData();
            dataPager.setTotal(packageCodePager.getTotal());
            final List<String> packageCodeList = packageCodePager.getData();
            for (String packageCode : packageCodeList) {
                String prefixName = packageCode + Constants.UNDERLINE_FILL + siteCode + Constants.UNDERLINE_FILL;
                //获取最近的对应的图片并返回
                String excessPictureUrl = searchPictureUrlRecent(prefixName, 1);
                if(StringUtils.isNotEmpty(excessPictureUrl)){
                    WeightVolumePictureDto weightVolumePictureDto = new WeightVolumePictureDto();
                    weightVolumePictureDto.setWaybillCode(waybillSpotCheckCondition.getWaybillCode());
                    weightVolumePictureDto.setPackageCode(packageCode);
                    weightVolumePictureDto.setUrl(excessPictureUrl);
                    dataList.add(weightVolumePictureDto);
                }
            }
        }catch (Exception e){
            log.error("searchPicture4MultiplePackage 获取图片链接失败! {}", JsonHelper.toJson(weightVolumeQueryConditionPager));
            result.parameterError(String.format("查看单号%s站点%s的图片失败!", waybillSpotCheckCondition.getWaybillCode(), waybillSpotCheckCondition.getReviewSiteCode()));
        }
        dataPager.setData(dataList);
        result.setData(dataPager);

        return result;
    }

    @Override
    public InvokeResult<List<String>> searchExcessPictureOfB2b(String barCode, Integer siteCode) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        try {
            List<String> excessPictureUrls = new ArrayList<>(5);
            for (SpotCheckPictureDimensionEnum value : SpotCheckPictureDimensionEnum.values()) {
                String prefixName = Constants.SPOT_CHECK_B + Constants.UNDER_LINE + barCode + Constants.UNDER_LINE + siteCode
                        + Constants.UNDER_LINE + value.getCode() + Constants.UNDER_LINE;
                if(StringUtils.isEmpty(searchPictureUrlRecent(prefixName))){
                    // 兼容之前的逻辑（之前上传的图片名称：JDV000690914941_910_1_20210508142931）
                    prefixName = barCode + Constants.UNDER_LINE + siteCode
                            + Constants.UNDER_LINE + value.getCode() + Constants.UNDER_LINE;
                }
                String pictureUrlRecent = searchPictureUrlRecent(prefixName);
                if(!StringUtils.isEmpty(pictureUrlRecent)){
                    excessPictureUrls.add(pictureUrlRecent);
                }
            }
            if(excessPictureUrls.isEmpty()){
                result.parameterError(String.format("单号%s站点%s的图片未上传!", barCode ,siteCode));
                return result;
            }
            result.setData(excessPictureUrls);
        }catch (Exception e){
            log.error("{}|{}获取图片链接失败!", barCode, siteCode, e);
            result.parameterError(String.format("查看单号%s站点%s的图片失败!", barCode ,siteCode));
        }

        return result;
    }

    @Override
    public String searchPictureUrlRecent(String prefixName, Integer maxKeys) {
        if(maxKeys == null || maxKeys <= 0){
            maxKeys = 1000;
        }
        ObjectListing objectListing = listObject(prefixName, null, maxKeys);
        if(objectListing != null && CollectionUtils.isNotEmpty(objectListing.getObjectSummaries())){
            List<String> pictureUrlList = new ArrayList<>(3);
            for(ObjectSummary objectSummary : objectListing.getObjectSummaries()){
                URI uri = getURI(objectSummary.getKey());
                if(uri != null){
                    String uriString = uri.toString();
                    //将内部访问域名替换成外部访问域名
                    uriString = uriString.replaceAll(STORAGE_DOMAIN_LOCAL,STORAGE_DOMAIN_COM);
                    uri = URI.create(uriString);
                    pictureUrlList.add(uri.toString());
                }
            }
            return findRecentUrl(prefixName, pictureUrlList);
        }
        return Constants.EMPTY_FILL;
    }

    @Override
    public String searchPictureUrlRecent(String prefixName) {
        return this.searchPictureUrlRecent(prefixName, null);
    }

    /**
     * 获取最近上传的图片链接
     * @param prefixName
     * @param pictureUrlList
     * @return
     */
    private String findRecentUrl(String prefixName, List<String> pictureUrlList) {
        if(CollectionUtils.isEmpty(pictureUrlList)){
            return Constants.EMPTY_FILL;
        }
        // key：时间，value：url
        Map<String,String> map = new HashMap<>(3);
        for (String pictureUrl : pictureUrlList) {
            map.put(getOperateTimeByUrl(prefixName, pictureUrl), pictureUrl);
        }
        if(map.size() == Constants.NUMBER_ZERO){
            return Constants.EMPTY_FILL;
        }
        List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        String recentPictureTime = list.get(list.size() - Constants.CONSTANT_NUMBER_ONE);
        return map.get(recentPictureTime);
    }

    /**
     * 获取图片链接中的时间
     * @param prefixName
     * @param pictureUrl
     * @return
     */
    private String getOperateTimeByUrl(String prefixName, String pictureUrl) {
        String[] splits = pictureUrl.split("/");
        String pictureName = splits[splits.length - Constants.CONSTANT_NUMBER_ONE];
        String[] pictureNames = pictureName.split("\\.");
        String pictureNamePrefix = pictureNames[Constants.NUMBER_ZERO];
        return pictureNamePrefix.replace(prefixName, Constants.EMPTY_FILL);
    }

    /**
     * 发消息并更新es
     * @param packageCode
     * @param siteCode
     * @deprecated
     */
    @Override
    public void sendMqAndUpdate(String packageCode, Integer siteCode){

        //获取图片链接
        InvokeResult<String> result = searchExcessPicture(packageCode,siteCode);
        if(result == null || result.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                || StringUtils.isEmpty(result.getData())){
            log.warn("运单【{}】站点【{}】的超标图片为空",packageCode,siteCode);
            return;
        }
        String pictureAddress = result.getData();

        WeightVolumeCollectDto weightVolumeCollectDto;
        try {
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(IsExcessEnum.EXCESS_ENUM_YES.getCode());
            condition.setIsHasPicture(0);
            condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
            if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())
                    || baseEntity.getData().get(0) == null){
                log.warn("通过运单【{}】站点【{}】查询超标数据为空",packageCode,siteCode);
                return;
            }
            weightVolumeCollectDto = baseEntity.getData().get(0);
        }catch (Exception e){
            log.warn("通过运单【{}】站点【{}】查询超标数据异常",packageCode,siteCode,e);
            return;
        }

        //更新es数据设置图片连接
        try {
            weightVolumeCollectDto.setPictureAddress(pictureAddress);
            weightVolumeCollectDto.setIsHasPicture(1);
            reportExternalService.updateForWeightVolume(weightVolumeCollectDto);
        }catch (Exception e){
            log.warn("通过运单【{}】站点【{}】更新超标数据异常",packageCode,siteCode,e);
        }

        // 同一包裹只下发一次超标数据（一单一件）
        String key = B2C_SPOT_CHECK_ISSUE.concat(WaybillUtil.getWaybillCode(packageCode));
        try {
            String redisValue = jimdbCacheService.get(key);
            if(redisValue != null && Boolean.valueOf(redisValue) == true){
                return;
            }
        }catch (Exception e){
            log.error("获取C网抽检下发MQ缓存【{}】异常",key);
        }

        // 发对外消息
        Date reviewDate = weightVolumeCollectDto.getReviewDate();
        Date uploadTime = new Date();
        if(checkIsOverTime(reviewDate,uploadTime)){
            log.warn("运单【{}】上传图片时间【{}】已超过站点【{}】的抽检时间【{}】【{}】天",packageCode,
                    DateHelper.formatDateTime(uploadTime),siteCode,DateHelper.formatDateTime(reviewDate),defaultIntervalDays);
            return;
        }
        AbnormalResultMq abnormalResultMq = convertToAbnormalResultMq(weightVolumeCollectDto);
        if(abnormalResultMq == null){
            return;
        }
        log.info("发送MQ【{}】,业务ID【{}】 ",dmsWeightVolumeExcess.getTopic(),abnormalResultMq.getAbnormalId());
        dmsWeightVolumeExcess.sendOnFailPersistent(abnormalResultMq.getAbnormalId(), JsonHelper.toJson(abnormalResultMq));

        // 设置下发缓存
        try {
            jimdbCacheService.setEx(key,String.valueOf(true),15, TimeUnit.DAYS);
        }catch (Exception e){
            log.error("设置C网抽检下发MQ缓存【{}】异常",key);
        }

    }

    /**
     * 发消息并更新es
     * @param packageCode
     * @param siteCode
     */
    @Override
    public void updateImgAndSendHandleMq(String packageCode, Integer siteCode, String pictureUrl){

        if(StringUtils.isEmpty(pictureUrl)){
            //获取图片链接
            InvokeResult<String> result = searchExcessPicture(packageCode,siteCode);
            if(result == null || result.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                    || StringUtils.isEmpty(result.getData())){
                log.warn("运单【{}】站点【{}】的超标图片为空",packageCode,siteCode);
                return;
            }
            pictureUrl = result.getData();
        }
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        final boolean isMultiplePackage = this.getIsMultiplePackage(waybill, packageCode);

        if(!isMultiplePackage && !checkPackExcessRedisIsExist(packageCode, siteCode)){
            log.warn("根据包裹{}站点{}查询抽检数据不存在或抽检不超标!", packageCode, siteCode);
            return;
        }
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        weightVolumeCollectDto.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        weightVolumeCollectDto.setPackageCode(packageCode);
        weightVolumeCollectDto.setReviewSiteCode(siteCode);
        weightVolumeCollectDto.setPictureAddress(pictureUrl);
        weightVolumeCollectDto.setIsHasPicture(1);
        reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);

        // 上传成功后，发送MQ消息，进行下一步操作
        WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = new WeightAndVolumeCheckHandleMessage();
        weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.UPLOAD_IMG);
        if(WaybillUtil.isPackageCode(packageCode)){
            weightAndVolumeCheckHandleMessage.setPackageCode(packageCode);
            weightAndVolumeCheckHandleMessage.setWaybillCode(WaybillUtil.getWaybillCodeByPackCode(packageCode));
        }
        if(WaybillUtil.isWaybillCode(packageCode)){
            weightAndVolumeCheckHandleMessage.setWaybillCode(packageCode);
        }
        weightAndVolumeCheckHandleMessage.setSiteCode(siteCode);
        try {
            weightAndVolumeCheckHandleProducer.send(packageCode, JSON.toJSONString(weightAndVolumeCheckHandleMessage));
        } catch (JMQException e) {
            log.warn("updateImgAndSendHandleMq weightAndVolumeCheckHandleProducer send exception {}", e.getMessage(), e);
        }

    }

    /**
     * 包裹是否超标
     * @param packageCode
     * @param siteCode
     * @return
     */
    private boolean checkPackExcessRedisIsExist(String packageCode, Integer siteCode) {
        try{
            String key = String.format(CacheKeyConstants.CACHE_KEY_SPOT_IS_EXCESS_FLAG, packageCode, siteCode);
            String isExcess = jimdbCacheService.get(key);
            if(StringUtils.isEmpty(isExcess)){
                WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
                condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
                condition.setReviewSiteCode(siteCode);
                BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
                if(baseEntity != null && CollectionUtils.isNotEmpty(baseEntity.getData())){
                    Integer excessFlag = baseEntity.getData().get(0).getIsExcess();
                    isExcess = excessFlag == null ? null : String.valueOf(excessFlag);
                }
            }
            if(StringUtils.isEmpty(isExcess)){
                log.warn("根据包裹{}站点{}获取包裹未查询到超标标识!", packageCode, siteCode);
            }
            if(Objects.equals(isExcess, String.valueOf(IsExcessEnum.EXCESS_ENUM_YES.getCode()))){
                return true;
            }
        }catch (Exception e){
            log.error("根据包裹{}站点{}获取包裹是否超标缓存异常!", packageCode, siteCode);
        }
        return false;
    }

    /**
     * 判断是否超过默认天数
     * @param reviewDate
     * @param uploadTime
     * @return
     */
    private boolean checkIsOverTime(Date reviewDate, Date uploadTime) {
        return uploadTime.getTime() - reviewDate.getTime() > defaultIntervalDays*24*60*60*1000;
    }

    /**
     * 构建超标消息实体
     * @param weightVolumeCollectDto
     * @return
     */
    private AbnormalResultMq convertToAbnormalResultMq(WeightVolumeCollectDto weightVolumeCollectDto) {
        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setSource(SystemEnum.DMS.getCode());
        abnormalResultMq.setBusinessType(BusinessHelper.translateSpotCheckTypeToBusinessType(weightVolumeCollectDto.getSpotCheckType()));
        try {
            String[] split = weightVolumeCollectDto.getReviewLWH().split("\\*");
            abnormalResultMq.setReviewLength(Double.valueOf(split[0]));
            abnormalResultMq.setReviewWidth(Double.valueOf(split[1]));
            abnormalResultMq.setReviewHeight(Double.valueOf(split[2]));
        }catch (Exception e){
            log.error("运单【{}】站点【{}】的长宽高【{}】异常",weightVolumeCollectDto.getPackageCode(),
                    weightVolumeCollectDto.getReviewSiteCode(),weightVolumeCollectDto.getReviewLWH());
        }

        // 核对区域、核对操作站点、核对erp三者缺一则不下发
        if(uccPropertyConfiguration.getSpotCheckIssueControl()
                && (weightVolumeCollectDto.getBillingOrgCode() == null || StringUtils.isEmpty(weightVolumeCollectDto.getBillingCompany())
                || StringUtils.isEmpty(weightVolumeCollectDto.getBillingErp()))){
            return null;
        }
        abnormalResultMq.setFirstLevelId(String.valueOf(weightVolumeCollectDto.getBillingOrgCode()));
        abnormalResultMq.setFirstLevelName(weightVolumeCollectDto.getBillingOrgName());
        abnormalResultMq.setSecondLevelId(weightVolumeCollectDto.getBillingDeptCodeStr());
        abnormalResultMq.setSecondLevelName(weightVolumeCollectDto.getBillingDeptName());
        abnormalResultMq.setThreeLevelId(weightVolumeCollectDto.getBillingThreeLevelId());
        abnormalResultMq.setThreeLevelName(weightVolumeCollectDto.getBillingThreeLevelName());
        abnormalResultMq.setWeight(weightVolumeCollectDto.getBillingWeight() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(weightVolumeCollectDto.getBillingWeight()));
        abnormalResultMq.setVolume(weightVolumeCollectDto.getBillingVolume() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(weightVolumeCollectDto.getBillingVolume()));
        abnormalResultMq.setDutyType(weightVolumeCollectDto.getDutyType());
        abnormalResultMq.setDutyErp(weightVolumeCollectDto.getBillingErp());
        abnormalResultMq.setReviewDutyType(weightVolumeCollectDto.getDutyType());
        abnormalResultMq.setBusinessObjectId(weightVolumeCollectDto.getBusiCode());
        abnormalResultMq.setBusinessObject(weightVolumeCollectDto.getBusiName());
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
        abnormalResultMq.setWeightDiff(StringUtils.isEmpty(weightVolumeCollectDto.getWeightDiff())
                ? null : Double.parseDouble(weightVolumeCollectDto.getWeightDiff()));
        abnormalResultMq.setVolumeDiff(StringUtils.isEmpty(weightVolumeCollectDto.getVolumeWeightDiff())
                ? null : Double.parseDouble(weightVolumeCollectDto.getVolumeWeightDiff()));
        abnormalResultMq.setIsExcess(weightVolumeCollectDto.getIsExcess());
        abnormalResultMq.setPictureAddress(weightVolumeCollectDto.getPictureAddress());
        //默认值:认责不判责
        abnormalResultMq.setIsAccusation(0);
        abnormalResultMq.setIsNeedBlame(1);
        abnormalResultMq.setOperateTime(weightVolumeCollectDto.getReviewDate());
        // 如果是一单多件
        if (Objects.equals(IsMultiPackEnum.MULTIPLE.getCode(), weightVolumeCollectDto.getMultiplePackage())) {
            abnormalResultMq.setBillCode(weightVolumeCollectDto.getWaybillCode());
            abnormalResultMq.setAbnormalId(weightVolumeCollectDto.getWaybillCode());
            abnormalResultMq.setId(weightVolumeCollectDto.getWaybillCode() + "_" + weightVolumeCollectDto.getReviewDate().getTime());
            abnormalResultMq.setPictureAddress(null);
        }
        return abnormalResultMq;
    }

    /**
     * 校验是否超标
     * @param packWeightVO
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkIsExcess(PackWeightVO packWeightVO) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
        if(!checkIsCanSpotCheck(packWeightVO, waybill, result)){
            return result;
        }
        WeightVolumeCollectDto weightVolumeCollectDto = assemble(packWeightVO, waybill, SpotCheckSourceEnum.SPOT_CHECK_DWS, result);
        result.setData(Objects.equals(weightVolumeCollectDto.getIsExcess(),IsExcessEnum.EXCESS_ENUM_YES.getCode()));
        return result;
    }

    /**
     * 校验是否可以抽检
     * @param packWeightVO
     * @param waybill
     * @param result
     * @return
     */
    private boolean checkIsCanSpotCheck(PackWeightVO packWeightVO, Waybill waybill, InvokeResult<Boolean> result) {
        // 参数校验
        if(!paramCheck(packWeightVO, result)){
            return false;
        }
        // 运单属性校验
        if(!waybillCheck(packWeightVO, waybill, result)){
            return false;
        }
        // 校验是否能操作抽检
        InvokeResult<Boolean> canDealSportCheckResult = canDealSportCheck(packWeightVO);
        if(!canDealSportCheckResult.getData()){
            result.customMessage(canDealSportCheckResult.getCode(), canDealSportCheckResult.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 抽检数据处理
     * @param packWeightVO
     * @param spotCheckSourceEnum
     * @return
     */
    @Override
    public InvokeResult<Boolean> dealSportCheck(PackWeightVO packWeightVO, SpotCheckSourceEnum spotCheckSourceEnum) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        CallerInfo info = Profiler.registerInfo("dms.WeightAndVolumeCheckServiceImpl.dealSportCheck", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try{
            // 校验是否能操作抽检
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
            if(!checkIsCanSpotCheck(packWeightVO, waybill, result)){
                return result;
            }
            // 组装抽检数据
            WeightVolumeCollectDto weightVolumeCollectDto = assemble(packWeightVO, waybill, spotCheckSourceEnum, result);
            // 如果是一单多件抽检，则更新运单纬度的数据
            this.handleMultiplePackage(packWeightVO, weightVolumeCollectDto, waybill, spotCheckSourceEnum);
            // 抽检数据落es
            reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
            // 抽检全程跟踪
            sendWaybillTrace(weightVolumeCollectDto);
            // 缓存抽检记录
            cachePackageOrWaybillCheckRecord(packWeightVO.getCodeStr());
            // 特殊场景处理
            specialSceneDeal(weightVolumeCollectDto);
            // 记录抽检操作日志
            recordSpotCheckLog(weightVolumeCollectDto, spotCheckSourceEnum);

            result.setData(Objects.equals(weightVolumeCollectDto.getIsExcess(), IsExcessEnum.EXCESS_ENUM_YES.getCode()));
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("单号{}的抽检数据处理异常!", packWeightVO.getCodeStr(), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 设置包裹是否超标缓存
     * @param weightVolumeCollectDto
     */
    private void cachePackIsExcessRecord(WeightVolumeCollectDto weightVolumeCollectDto) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_SPOT_IS_EXCESS_FLAG, weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
            jimdbCacheService.setEx(key, weightVolumeCollectDto.getIsExcess(), 30, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("设置包裹{}站点{}是否超标缓存异常!", weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
        }
    }

    private SpotCheckPackageExistResult getSpotPackageTotal(WeightVolumeCollectDto weightVolumeCollectDto, int waybillPackTotalNum){
        // 1. 获取已抽检包裹数
        // 1.1 是否第一个判断，则写入空的运单数据
        // 1.2 是否集齐判断，集齐进行超标判断，更新运单数据
        // 写入包裹纬度抽检数据缓存
        SpotCheckPackageExistResult spotCheckPackageExistResult = new SpotCheckPackageExistResult(false, 0);

        WeightVolumeQueryCondition packageWeightVolumeQuery = new WeightVolumeQueryCondition();
        packageWeightVolumeQuery.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());
        packageWeightVolumeQuery.setWaybillCode(weightVolumeCollectDto.getWaybillCode());
        packageWeightVolumeQuery.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
        packageWeightVolumeQuery.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
        // packageWeightVolumeQuery.setNotThesePackageCode(new ArrayList<>(Arrays.asList(weightVolumeCollectDto.getPackageCode())));

        BaseEntity<Long> packageWeightVolumeTotalResult = reportExternalService.countByParam(packageWeightVolumeQuery);
        log.info("packageWeightVolumeTotalResult {}", packageWeightVolumeTotalResult);
        if(packageWeightVolumeTotalResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.error("getSpotPackageTotal error {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(packageWeightVolumeQuery), packageWeightVolumeTotalResult.getMessage());
            return spotCheckPackageExistResult;
        }

        long packageWeightVolumeExistTotal = packageWeightVolumeTotalResult.getData();
        // 如果es中的就已能查到所有，直接返回
        if(packageWeightVolumeExistTotal == waybillPackTotalNum){
            spotCheckPackageExistResult.setPackageSpotCheckTotal(packageWeightVolumeExistTotal);
            spotCheckPackageExistResult.setWaybillSpotCheckExist(true);
            return spotCheckPackageExistResult;
        }

        packageWeightVolumeQuery.setRecordType(null);
        Pager<WeightVolumeQueryCondition> weightVolumeQueryConditionPager = new Pager<>();
        weightVolumeQueryConditionPager.setPageSize(waybillPackTotalNum);
        weightVolumeQueryConditionPager.setPageNo(1);
        weightVolumeQueryConditionPager.setSearchVo(packageWeightVolumeQuery);
        BaseEntity<Pager<String>> packageCodesResult = reportExternalService.getSpotCheckPackageCodesByCondition(weightVolumeQueryConditionPager);
        // log.info("getSpotPackageTotal packageCodesResult {}", packageCodesResult);
        if(packageCodesResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.error("getSpotPackageTotal error {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(packageWeightVolumeQuery), packageCodesResult.getMessage());
            return spotCheckPackageExistResult;
        }
        final List<String> packageCodeExistEsList = packageCodesResult.getData().getData();

        Set<String> packageCodeSet = new HashSet<>(packageCodeExistEsList);
        if(packageCodeSet.contains(weightVolumeCollectDto.getWaybillCode())){
            spotCheckPackageExistResult.setWaybillSpotCheckExist(true);
            packageCodeSet.remove(weightVolumeCollectDto.getWaybillCode());
        }

        // 先存一遍缓存
        String waybillKey = CacheKeyConstants.CACHE_KEY_WAYBILL_PACKAGE_CHECK_LIST.concat(weightVolumeCollectDto.getWaybillCode());
        try {
            final String existPackageListStr = jimdbCacheService.get(waybillKey);
            if(com.jd.common.util.StringUtils.isNotEmpty(existPackageListStr)){
                spotCheckPackageExistResult.setWaybillSpotCheckExist(true);
                Set<String> packageCodeExistCacheSet = new HashSet<>();
                List<String> packageCodeList = JsonHelper.fromJson(existPackageListStr, new ArrayList<String>().getClass());
                if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(packageCodeList)){
                    packageCodeList.add(weightVolumeCollectDto.getPackageCode());
                    packageCodeExistCacheSet.addAll(packageCodeList);
                } else {
                    packageCodeExistCacheSet.add(weightVolumeCollectDto.getPackageCode());
                }
                packageCodeSet.addAll(packageCodeExistCacheSet);
            } else {
                packageCodeSet.add(weightVolumeCollectDto.getPackageCode());
            }
            jimdbCacheService.setEx(waybillKey, JSON.toJSONString(packageCodeSet), this.waybillPackageWeightCheckRecordExpireTime, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("存储运单的包裹抽检数据【{}】异常", waybillKey);
        }

        spotCheckPackageExistResult.setPackageSpotCheckTotal(packageCodeSet.size());

        return spotCheckPackageExistResult;
    }

    /**
     * 一单多件抽检，更新运单纬度数据，汇集运单纬度数据
     */
    private WeightVolumeCollectDto handleMultiplePackage(PackWeightVO packWeightVO, WeightVolumeCollectDto weightVolumeCollectDto, Waybill waybill, SpotCheckSourceEnum spotCheckSourceEnum){
        if(!this.getIsMultiplePackage(waybill, packWeightVO.getCodeStr())){
            return null;
        }
        int waybillPackTotalNum = this.getPackageNumberTotal(waybill, packWeightVO.getCodeStr());
        WeightVolumeCollectDto waybillWeightVolumeCollectDto = new WeightVolumeCollectDto();
        BeanUtils.copyProperties(weightVolumeCollectDto, waybillWeightVolumeCollectDto);
        waybillWeightVolumeCollectDto.setReviewWeight((double) 0);
        waybillWeightVolumeCollectDto.setReviewVolume((double) 0);
        waybillWeightVolumeCollectDto.setReviewVolumeWeight((double) 0);
        waybillWeightVolumeCollectDto.setMoreBigWeight((double) 0);
        waybillWeightVolumeCollectDto.setReviewLWH("0*0*0");
        waybillWeightVolumeCollectDto.setPackageCode(waybillWeightVolumeCollectDto.getWaybillCode());
        waybillWeightVolumeCollectDto.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
        // waybillWeightVolumeCollectDto.setIsHasPicture(null);

        // 1. 获取已抽检包裹数
        SpotCheckPackageExistResult spotCheckPackageExistResult = this.getSpotPackageTotal(weightVolumeCollectDto, waybillPackTotalNum);
        long packageWeightVolumeExistTotal = spotCheckPackageExistResult.getPackageSpotCheckTotal();

        // 1. 如果没有，则说明本次操作是第1个包裹，则写一个初始化的运单维度数据，否则不处理
        if(!spotCheckPackageExistResult.getWaybillSpotCheckExist()){
            reportExternalService.insertOrUpdateForWeightVolume(waybillWeightVolumeCollectDto);
            log.info("insertOrUpdateForWeightVolume waybillWeightVolumeCollectDto {}", JsonHelper.toJson(waybillWeightVolumeCollectDto));
            return waybillWeightVolumeCollectDto;
        }
        // 2. 查看是否集齐，未集齐则不更新数据，集齐则处理整单超标数据，更新复核数据，超标数据
        if(packageWeightVolumeExistTotal == waybillPackTotalNum){
            // 汇总已有包裹总重量、体积 + 本次抽检的体积重量数据
            // 取得计费数据
            waybillWeightVolumeCollectDto.setWaybillCode(weightVolumeCollectDto.getWaybillCode());
            boolean contrastDataFromFinance = this.assembleContrastDataFromFinance(waybillWeightVolumeCollectDto);
            // 从计费取到重量，则进行超标判断
            WeightVolumeCollectDto waybillWeightVolumeCollectDtoUpdate = new WeightVolumeCollectDto();
            if(contrastDataFromFinance){
                WeightVolumeQueryCondition weightVolumeQueryCondition = new WeightVolumeQueryCondition();
                weightVolumeQueryCondition.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());
                weightVolumeQueryCondition.setWaybillCode(weightVolumeCollectDto.getWaybillCode());
                weightVolumeQueryCondition.setNotThesePackageCode(new ArrayList<>(Arrays.asList(weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getWaybillCode())));
                BaseEntity<WeightVolumeCollectDto> sumCollectResultByReviewSiteAndWaybillCodeResult = reportExternalService.getSumCollectResultByReviewSiteAndWaybillCode(weightVolumeQueryCondition);
                if(sumCollectResultByReviewSiteAndWaybillCodeResult.getCode() != BaseEntity.CODE_SUCCESS){
                    log.error("handleMultiplePackage error {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(weightVolumeQueryCondition), sumCollectResultByReviewSiteAndWaybillCodeResult.getMessage());
                    return null;
                }
                final WeightVolumeCollectDto sumCollect = sumCollectResultByReviewSiteAndWaybillCodeResult.getData();
                // 加上本次称重数据
                PackWeightVO waybillWeightVO = new PackWeightVO();
                BeanUtils.copyProperties(packWeightVO, waybillWeightVO);
                waybillWeightVO.setCodeStr(weightVolumeCollectDto.getWaybillCode());
                waybillWeightVO.setLength((double) 0);
                waybillWeightVO.setWidth((double) 0);
                waybillWeightVO.setHigh((double) 0);
                waybillWeightVO.setVolume(sumCollect.getReviewVolume() + weightVolumeCollectDto.getReviewVolume());
                waybillWeightVO.setWeight(sumCollect.getReviewWeight() + weightVolumeCollectDto.getReviewWeight());

                waybillWeightVolumeCollectDto.setReviewWeight(sumCollect.getReviewWeight() + weightVolumeCollectDto.getReviewWeight());
                waybillWeightVolumeCollectDto.setReviewVolume(sumCollect.getReviewVolume() + weightVolumeCollectDto.getReviewVolume());

                waybillWeightVolumeCollectDtoUpdate = this.assemble4MultiplePackage(waybillWeightVO, waybill, spotCheckSourceEnum, new InvokeResult<Boolean>(), waybillWeightVolumeCollectDto);
                waybillWeightVolumeCollectDtoUpdate.setReviewErp(null); // 不更新抽检人erp，仍取第一次抽检人的erp
                waybillWeightVolumeCollectDtoUpdate.setFullCollect(Constants.YN_YES);
                // 更新运单纬度数据
                reportExternalService.insertOrUpdateForWeightVolume(waybillWeightVolumeCollectDtoUpdate);
            } else {
                // 更新集齐数据
                waybillWeightVolumeCollectDtoUpdate.setPackageCode(weightVolumeCollectDto.getWaybillCode());
                waybillWeightVolumeCollectDtoUpdate.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());
                waybillWeightVolumeCollectDtoUpdate.setFullCollect(Constants.YN_YES);
                // 更新运单纬度数据
                reportExternalService.insertOrUpdateForWeightVolume(waybillWeightVolumeCollectDtoUpdate);
            }
        }
        return waybillWeightVolumeCollectDto;
    }

    /**
     * 特殊场景处理
     *  1、图片早于超标数据的场景
     * @param weightVolumeCollectDto
     */
    private void specialSceneDeal(WeightVolumeCollectDto weightVolumeCollectDto) {
        // 超标且有图片则下发超标mq（场景：上传图片时间早于超标数据落库时间）
        if(Objects.equals(weightVolumeCollectDto.getIsExcess(), IsExcessEnum.EXCESS_ENUM_YES.getCode())
                && StringUtils.isNotEmpty(weightVolumeCollectDto.getPictureAddress())){
            WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = new WeightAndVolumeCheckHandleMessage();
            weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.IMG_BEFORE_DATA);
            weightAndVolumeCheckHandleMessage.setWaybillCode(weightVolumeCollectDto.getWaybillCode());
            weightAndVolumeCheckHandleMessage.setPackageCode(weightVolumeCollectDto.getPackageCode());
            weightAndVolumeCheckHandleMessage.setSiteCode(weightVolumeCollectDto.getReviewSiteCode());
            try {
                weightAndVolumeCheckHandleProducer.send(weightVolumeCollectDto.getPackageCode(), JSON.toJSONString(weightAndVolumeCheckHandleMessage));
            } catch (Exception e) {
                log.warn("imgUnloadTime before spotCheckDataTime send exception", e);
            }
        }
    }

    /**
     * 运单属性校验
     * @param packWeightVO
     * @param waybill
     * @param result
     * @return
     */
    private boolean waybillCheck(PackWeightVO packWeightVO, Waybill waybill, InvokeResult<Boolean> result) {
        if(waybill == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"运单不存在!!");
            return false;
        }
        Integer packNum;
        if(WaybillUtil.isPackageCode(packWeightVO.getCodeStr())){
            packNum = WaybillUtil.getPackNumByPackCode(packWeightVO.getCodeStr());
        }else {
            packNum = waybill.getGoodNumber();
        }
        if(packNum == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "未能获取到运单包裹数，请稍后再试!");
            return false;
        }
        if(packNum > Constants.CONSTANT_NUMBER_ONE){
            if(BusinessUtil.isPurematch(waybill.getWaybillSign())){
                if(!WaybillUtil.isPackageCode(packWeightVO.getCodeStr())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"C网纯配运单重量体积抽查只支持扫包裹，请勿扫描其他类型条码");
                    return false;
                }
                // 增加ucc开关
                if(uccPropertyConfiguration.getMultiplePackageSpotCheckSwitchOn() && !uccPropertyConfiguration.matchMultiplePackageSpotCheckSite(packWeightVO.getOperatorSiteCode())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"当前场地未开通一单多件抽检功能");
                    return false;
                }
                return true;
            }
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"非C网纯配运单重量体积抽查只支持一单一件!");
            return false;
        }

        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            // 非纯配外单不计入抽检 & 前台不提示
            result.success();
            return false;
        }
        return true;
    }

    private int getPackageNumberTotal(Waybill waybill, String packageCode){
        int packNum;
        if(WaybillUtil.isPackageCode(packageCode)){
            packNum = WaybillUtil.getPackNumByPackCode(packageCode);
        }else {
            packNum = waybill.getGoodNumber();
        }
        return packNum;
    }

    /**
     * 根据运单判断是否为一单多件
     * @param waybill 运单
     * @return 判定结果
     */
    private boolean getIsMultiplePackage(Waybill waybill, String packageCode) {
        int packNum = this.getPackageNumberTotal(waybill, packageCode);
        return packNum > Constants.CONSTANT_NUMBER_ONE;
    }

    /**
     * 1、针对C抽B的：是否超标不填
     * 2、生鲜：分拣较大值 < 核对较大值,则不超标
     * 3、分拣较大值 <= 1.5 && 核对较大值 <= 1.5 则不超标
     * @param weightVolumeCollectDto
     * @param waybill
     */
    private void specialTreatment(WeightVolumeCollectDto weightVolumeCollectDto, Waybill waybill) {
        if(weightVolumeCollectDto.getSpotCheckType().equals(SpotCheckTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO_KNOW.getCode());
            return;
        }
        // 分拣复核较大值
        double reviewMore = weightVolumeCollectDto.getMoreBigWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getMoreBigWeight();
        // 核对较大值
        double checkMore = weightVolumeCollectDto.getContrastLarge() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getContrastLarge();
        if(BusinessUtil.isFresh(waybill.getWaybillSign()) && reviewMore < checkMore){
            weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO.getCode());
            weightVolumeCollectDto.setExcessReason(Constants.EMPTY_FILL);
            return;
        }
        // 分拣复核较大值 <= 1.5 && 核对较大值 <= 1.5
        double spotCheckNoExcessLimit = uccPropertyConfiguration.getSpotCheckNoExcessLimit();
        if(reviewMore <= spotCheckNoExcessLimit && checkMore <= spotCheckNoExcessLimit){
            weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO.getCode());
            weightVolumeCollectDto.setExcessReason(Constants.EMPTY_FILL);
        }
    }

    private void sendWaybillTrace(WeightVolumeCollectDto dto) {
        Task tTask = new Task();
        tTask.setKeyword1(dto.getWaybillCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK));
        tTask.setCreateSiteCode(dto.getReviewSiteCode());
        tTask.setCreateTime(dto.getReviewDate());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK);
        status.setWaybillCode(dto.getWaybillCode());
        status.setPackageCode(dto.getPackageCode());
        status.setOperateTime(dto.getReviewDate());
        status.setOperator(dto.getReviewErp());
        status.setRemark("重量体积抽检：重量"+dto.getReviewWeight()+"公斤，体积"+dto.getReviewVolume()+"立方厘米");
        status.setCreateSiteCode(dto.getReviewSiteCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

    /**
     * 将运单的所有包裹缓存抽检记录标志
     * @param waybillCodeOrPackageCode 运单号或包裹号
     */
    private void cachePackageOrWaybillCheckRecord(String waybillCodeOrPackageCode){
        try {
            String key = CacheKeyConstants.CACHE_KEY_PACKAGE_OR_WAYBILL_CHECK_FLAG.concat(waybillCodeOrPackageCode);
            jimdbCacheService.setEx(key, Constants.YN_YES.toString(), this.packageWeightCheckRecordExpireTime, TimeUnit.DAYS);
        }catch (Exception e){
            log.error("缓存包裹抽检记录异常 waybillCodeOrPackageCode {}, message {}", waybillCodeOrPackageCode, e.getMessage() ,e);
        }
    }

    /**
     * 参数校验
     *  校验通过 true
     * @param packWeightVO
     * @param result
     * @return
     */
    private boolean paramCheck(PackWeightVO packWeightVO, InvokeResult<Boolean> result) {
        if(!WaybillUtil.isWaybillCode(packWeightVO.getCodeStr())
                && !WaybillUtil.isPackageCode(packWeightVO.getCodeStr())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"运单号/包裹号不符合规则!");
            return false;
        }
        return true;
    }

    /**
     * 校验是否能进行称重抽检
     * @param packWeightVO 请求参数
     * @return InvokeResult
     * @author fanggang7
     * @time 2020-08-24 19:44:29 周一
     */
    private InvokeResult<Boolean> canDealSportCheck(PackWeightVO packWeightVO){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        // 校验发货状态
        String waybillCode = WaybillUtil.getWaybillCode(packWeightVO.getCodeStr());
        String packageCode = null;
        if(WaybillUtil.isPackageCode(packWeightVO.getCodeStr())){
            packageCode = packWeightVO.getCodeStr();
        }
        // 没有抽检过，通过发货明细校验是否已发货
        List<SendDetail> sendDetailRecords = sendDetailService.findByWaybillCodeOrPackageCode(packWeightVO.getOperatorSiteCode(), waybillCode, packageCode);
        if(CollectionUtils.isNotEmpty(sendDetailRecords)){
            result.setData(false);
            result.customMessage(this.NOT_ALLOW_SECOND_SITE_CHECK_CODE, "抽检失败，此单已发货，不可再抽检，请在发货操作前进行抽检");
            return result;
        }

        // 查看是否为第一个抽检的分拣中心，如果不是，则提示不允许抽检，提示"此单已操作过抽检，请勿重复操作"
        InvokeResult<Boolean> firstSiteCheckResult = this.isFirstSiteCheck(packWeightVO);
        if(!firstSiteCheckResult.getData()){
            result.setData(false);
            result.customMessage(firstSiteCheckResult.getCode(), firstSiteCheckResult.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 校验是否为第一个操作称重抽检的分拣中心
     * @param packWeightVO 请求参数
     * @return InvokeResult
     * @author fanggang7
     * @time 2020-08-24 19:44:29 周一
     */
    private InvokeResult<Boolean> isFirstSiteCheck(PackWeightVO packWeightVO){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
        // B网抽检是按运单号存入packageCode字段，如果以后不是一单一件，则需要更改此处
        InvokeResult<WeightVolumeCollectDto> weightVolumeCollectDtoInvokeResult = this.queryLatestCheckRecord(condition);
        if(weightVolumeCollectDtoInvokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            result.setData(false);
            result.customMessage(this.NOT_ALLOW_SECOND_SITE_CHECK_CODE, "抽检失败，校验是否为第一个操作抽检的分拣中心失败");
            return result;
        }
        WeightVolumeCollectDto weightVolumeCollectDtoExist = weightVolumeCollectDtoInvokeResult.getData();
        final List<Integer> excessResultList = new ArrayList<>(Arrays.asList(IsExcessEnum.EXCESS_ENUM_NO.getCode(), IsExcessEnum.EXCESS_ENUM_YES.getCode()));
        if(weightVolumeCollectDtoExist != null && weightVolumeCollectDtoExist.getIsExcess() != null && excessResultList.contains(weightVolumeCollectDtoExist.getIsExcess())){
            // 校验是否为第一个抽检的单位
            if (!Objects.equals(weightVolumeCollectDtoExist.getReviewSiteCode(), packWeightVO.getOperatorSiteCode())) {
                result.setData(false);
                result.customMessage(this.NOT_ALLOW_SECOND_SITE_CHECK_CODE, "抽检失败，此单已操作过抽检，请勿重复操作");
                return result;
            }
            // 有抽检记录，校验抽检记录上的发货状态
            if (Objects.equals(weightVolumeCollectDtoExist.getReviewSiteCode(), packWeightVO.getOperatorSiteCode())) {
                boolean waybillSendStatusFlag = this.getWaybillSendStatus(weightVolumeCollectDtoExist);
                if(waybillSendStatusFlag){
                    result.setData(false);
                    result.customMessage(this.NOT_ALLOW_SECOND_SITE_CHECK_CODE, "抽检失败，此单已发货，不可再抽检，请在发货操作前进行抽检");
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 比较方法- 判断是走A标准还是走B标准
     * 根据泡重比类型判断是否超标
     *
     * C网抽检对比逻辑变更
     * 校验标准A
     *      1. 分拣【较大值】小于等于1.5公斤，不论误差多少均判断为正常
     *      2. 分拣【较大值】1.5公斤至20公斤（含）， 误差标准正负0.5 kg（含）
     *      3. 分拣【较大值】20公斤至50公斤（含）， 误差标准正负1 kg（含）
     *      4. 分拣【较大值】50公斤以上，误差标准为重量的正负2%（含）
     *
     * 校验标准B
     *      1.  70cm=<三边之和<100cm，误差标准正负0.8 kg（含）
     *      2. 100cm=<三边之和<120cm，误差标准正负1 kg（含）
     *      3. 120cm=<三边之和<200cm，误差标准正负1.5 kg（含）
     *      4. 三边之和>200cm，误差标准正负2kg（含）
     *
     *7.2.2 判断逻辑
     *  分拣重量 与 体积重量 取最大值
     *       1.当分拣重量为较大值时，使用【较大值】与计费系统【唯一值】比较，执行校验标准A
     *       2.当分拣体积重量为较大值，且三边之和小于70 cm 时，使用【较大值】与计费系统【唯一值】比较，执行校验标准A
     *       3.当分拣体积重量为较大值，且三边之和大于70 cm 时，使用【较大值】与计费系统【唯一值】比较，执行校验标准B
     * @param weightVolumeCollectDto
     * @return
     */
    private StandardDto checkStandard(WeightVolumeCollectDto weightVolumeCollectDto){
        Double reviewWeight = weightVolumeCollectDto.getReviewWeight();
        Double reviewVolumeWeight = weightVolumeCollectDto.getReviewVolumeWeight();
        // 复核较大值
        Double moreBigValue = weightVolumeCollectDto.getMoreBigWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getMoreBigWeight();
        // 核对较大值
        Double contrastLarge = weightVolumeCollectDto.getContrastLarge() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getContrastLarge();

        // 三边之和
        BigDecimal sumLWH = BigDecimal.ZERO;
        if (weightVolumeCollectDto.getReviewLWH() != null) {
            for (String v : weightVolumeCollectDto.getReviewLWH().split("\\*")) {
                sumLWH = sumLWH.add(new BigDecimal(v));
            }
        }

        WeightAndVolumeCheckStandardHandler weightAndVolumeCheckStandardHandler = null;
        if(this.isMultiplePackage(weightVolumeCollectDto)){
            weightAndVolumeCheckStandardHandler = this.getCheckStandardForMultiplePackageHandler(weightVolumeCollectDto);
        } else {
            weightAndVolumeCheckStandardHandler = this.getCheckStandardHandler(reviewWeight,reviewVolumeWeight,sumLWH);
        }

        CheckExcessParam checkExcessParam = new CheckExcessParam();
        checkExcessParam.setSumLWH(sumLWH);
        checkExcessParam.setDifferenceValue(weightVolumeCollectDto.getLargeDiff());
        checkExcessParam.setMoreBigValue(moreBigValue);
        checkExcessParam.setCheckMoreBigValue(contrastLarge);
        checkExcessParam.setReviewWeight(reviewWeight);
        checkExcessParam.setReviewVolume(weightVolumeCollectDto.getReviewVolume());

        StandardDto standardDto = weightAndVolumeCheckStandardHandler.checkExcess(checkExcessParam);

        if(standardDto!=null && standardDto.getExcessFlag()){
            String baseMessage = "此次操作的重量:"+weightVolumeCollectDto.getReviewWeight()+"kg,体积重量:"+reviewVolumeWeight+"kg,核对较大值:"+contrastLarge+"kg，经校验误差值"+weightVolumeCollectDto.getLargeDiff()+"kg已超出规定";
            StringBuilder warnMessage = new StringBuilder().append(baseMessage).append("误差标准:"+standardDto.getHitMessage()).append("kg");
            standardDto.setWarnMessage(warnMessage.toString());
        }
        return standardDto;
    }

    private boolean isMultiplePackage(WeightVolumeCollectDto weightVolumeCollectDto) {
        return Objects.equals(IsMultiPackEnum.MULTIPLE.getCode(), weightVolumeCollectDto.getMultiplePackage());
    }

    /**
     * 判断是否走A标准还是B标准的 实现类
     * @param sumLWH
     * @return
     */
    private WeightAndVolumeCheckStandardHandler getCheckStandardHandler(Double reviewWeight, Double reviewVolumeWeight, BigDecimal sumLWH) {
        if(reviewWeight > reviewVolumeWeight){
           return this.weightAndVolumeCheckAHandler;
        }else {
            //体积重量为较大值---判断三边之和与70cm
            if(sumLWH.compareTo(new BigDecimal(fourSumLWH))<0){
                return this.weightAndVolumeCheckAHandler;
            }else {
               return this.weightAndVolumeCheckBHandler;
            }
        }
    }

    /**
     * 判断得到一单多件处理标准
     * @return
     */
    private WeightAndVolumeCheckStandardHandler getCheckStandardForMultiplePackageHandler(WeightVolumeCollectDto weightVolumeCollectDto) {
        // 整单的分拣较大值与整单的计费结算重量计费较大值进行对比
        /**
         * ①	当分拣重量为较大值时，使用【较大值】与计费系统【唯一值】计费较大值比较，执行校验标准A
         * ②	当分拣体积重量为较大值，且体积小于12700立方厘米时，使用【较大值】与计费系统【唯一值】计费较大值比较，执行校验标准A
         * ③	当分拣体积重量为较大值，且体积大于12700立方厘米 时，使用【较大值】与计费系统【唯一值】计费较大值比较，执行校验标准B
         * ④	分拣【较大值】与计费系统【唯一值】计费较大值，均小于等于1.5公斤，不论误差多少均判断为正常
         */
        Double reviewWeight = weightVolumeCollectDto.getReviewWeight();
        Double reviewVolume = weightVolumeCollectDto.getReviewVolume();
        Double reviewVolumeWeight = weightVolumeCollectDto.getReviewVolumeWeight();
        if(reviewWeight > reviewVolumeWeight){
            return this.weightAndVolumeCPureTypeCheckAForDwsHandler;
        } else {
            if(reviewVolume < Double.parseDouble(multiplePackageVolumeStandard)){
                return this.weightAndVolumeCPureTypeCheckAForDwsHandler;
            }else {
                return this.weightAndVolumeCPureTypeCheckBForDwsHandler;
            }
        }
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
     * 组装抽检数据
     *  1、分拣复核数据
     *  2、核对数据：计费|运单称重流水（计费未获取到数据的兜底方案）
     *  3、超标逻辑
     * @param packWeightVO
     */
    private WeightVolumeCollectDto assemble(PackWeightVO packWeightVO, Waybill waybill, SpotCheckSourceEnum spotCheckSourceEnum, InvokeResult<Boolean> result) {
        // 抽检复核数据
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        assembleReviewData(packWeightVO, weightVolumeCollectDto, waybill, spotCheckSourceEnum.name());
        // 抽检核对数据
        assembleContrastData(weightVolumeCollectDto);
        // 超标处理
        excessDeal(weightVolumeCollectDto, result);
        // 特殊处理
        specialTreatment(weightVolumeCollectDto, waybill);
        // 图片处理
        pictureUrlDeal(weightVolumeCollectDto);

        return weightVolumeCollectDto;
    }

    /**
     * 组装抽检数据
     *  1、分拣复核数据
     *  2、核对数据：计费|运单称重流水（计费未获取到数据的兜底方案）
     *  3、超标逻辑
     * @param packWeightVO
     */
    private WeightVolumeCollectDto assemble4MultiplePackage(PackWeightVO packWeightVO, Waybill waybill, SpotCheckSourceEnum spotCheckSourceEnum, InvokeResult<Boolean> result, WeightVolumeCollectDto waybillWeightVolumeCollectDto) {
        final Double reviewVolumeSum = waybillWeightVolumeCollectDto.getReviewVolume();
        // 抽检复核数据
        assembleReviewData(packWeightVO, waybillWeightVolumeCollectDto, waybill, spotCheckSourceEnum.name());
        // 一单多件的体积特殊处理
        Double reviewVolumeWeight = getVolumeAndWeight(reviewVolumeSum /waybillWeightVolumeCollectDto.getVolumeRate());
        waybillWeightVolumeCollectDto.setReviewVolumeWeight(reviewVolumeWeight);
        waybillWeightVolumeCollectDto.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
        waybillWeightVolumeCollectDto.setReviewVolume(reviewVolumeSum);
        // 复核较大值
        Double moreBigValue = waybillWeightVolumeCollectDto.getReviewWeight() >= reviewVolumeWeight ? waybillWeightVolumeCollectDto.getReviewWeight() : reviewVolumeWeight;
        waybillWeightVolumeCollectDto.setMoreBigWeight(moreBigValue);
        // 较大值差异
        double contrastLarge = waybillWeightVolumeCollectDto.getContrastLarge() == null ? Constants.DOUBLE_ZERO : waybillWeightVolumeCollectDto.getContrastLarge();
        double moreBigWeight = waybillWeightVolumeCollectDto.getMoreBigWeight() == null ? Constants.DOUBLE_ZERO : waybillWeightVolumeCollectDto.getMoreBigWeight();
        waybillWeightVolumeCollectDto.setLargeDiff(keeTwoDecimals(Math.abs(contrastLarge - moreBigWeight)));
        // 抽检核对数据
        // assembleContrastData(waybillWeightVolumeCollectDto);
        // 超标处理
        excessDeal(waybillWeightVolumeCollectDto, result);
        // 特殊处理
        specialTreatment(waybillWeightVolumeCollectDto, waybill);

        return waybillWeightVolumeCollectDto;
    }

    private void pictureUrlDeal(WeightVolumeCollectDto weightVolumeCollectDto) {
        String pictureUrl = Constants.EMPTY_FILL;
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_SPOT_CHECK_PICTURE_URL_UPLOAD_FLAG,
                    weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
            pictureUrl = jimdbCacheService.get(key);
            if(StringUtils.isEmpty(pictureUrl)){
                InvokeResult<String> result = searchExcessPicture(weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
                if(result != null && Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    pictureUrl = result.getData();
                }
            }
        }catch (Exception e){
            log.error("根据包裹{}站点{}查询包裹图片异常!", weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
        }
        if(StringUtils.isNotEmpty(pictureUrl)){
            weightVolumeCollectDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            weightVolumeCollectDto.setPictureAddress(pictureUrl);
        }else {
            log.warn("根据包裹{}站点{}未查询到包裹图片!", weightVolumeCollectDto.getPackageCode(), weightVolumeCollectDto.getReviewSiteCode());
        }
    }

    /**
     * 超标逻辑处理
     * @param weightVolumeCollectDto
     */
    private void excessDeal(WeightVolumeCollectDto weightVolumeCollectDto, InvokeResult<Boolean> result) {
        //校验是否超标
        // 如果是一单多件的包裹纬度数据，则不判断超标；如果是自动化来源的一单多件抽检，则必定超标
        if(Objects.equals(IsMultiPackEnum.MULTIPLE.getCode(), weightVolumeCollectDto.getMultiplePackage())
                && Objects.equals(SpotCheckRecordTypeEnum.PACKAGE.getCode(), weightVolumeCollectDto.getRecordType())){
            weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO_KNOW.getCode());
            weightVolumeCollectDto.setDiffStandard(Constants.EMPTY_FILL);
        } else {
            StandardDto standardDto = checkStandard(weightVolumeCollectDto);
            // 超标标识判断  1:是超标  0:未超标
            if(standardDto == null){
                // 未知
                result.customMessage(STANDARD_ERROR_CODE, "无法获取是否超标standardDto对象");
                weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO_KNOW.getCode());
                weightVolumeCollectDto.setDiffStandard(Constants.EMPTY_FILL);
            }else if(standardDto.getExcessFlag()){
                //超标--- C抽B  临时不提示
                if(Objects.equals(weightVolumeCollectDto.getSpotCheckType(),SpotCheckTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
                    result.customMessage(CHECK_OVER_STANDARD_CODE, standardDto.getWarnMessage());
                }
                weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_YES.getCode());
                weightVolumeCollectDto.setDiffStandard(standardDto.getHitMessage());
            }else{
                //未超标
                weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO.getCode());
                weightVolumeCollectDto.setDiffStandard(Constants.EMPTY_FILL);
            }
            weightVolumeCollectDto.setExcessReason(standardDto == null ? Constants.EMPTY_FILL : standardDto.getExcessReason());
        }
    }

    /**
     * 组装抽检核对数据
     * @param weightVolumeCollectDto
     */
    private void assembleContrastData(WeightVolumeCollectDto weightVolumeCollectDto) {
        try{
            // 如果是一单多件包裹纬度数据就不处理拉取计费数据
            if(this.isMultiplePackage(weightVolumeCollectDto)
                    && Objects.equals(SpotCheckRecordTypeEnum.PACKAGE.getCode(), weightVolumeCollectDto.getRecordType())){
                return ;
            }
            this.assembleContrastDataFromFinance(weightVolumeCollectDto);
        }catch (Exception e){
            log.error("通过运单号{}获取计费信息异常!", weightVolumeCollectDto.getWaybillCode());
        }

        // 一单多件抽检目前不查找运单流水
        if(!this.isMultiplePackage(weightVolumeCollectDto)){
            // 计费重量为0则取运单流水
            dealWaybillFlow(weightVolumeCollectDto);
        }
        // 较大值差异
        double contrastLarge = weightVolumeCollectDto.getContrastLarge() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getContrastLarge();
        double moreBigWeight = weightVolumeCollectDto.getMoreBigWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getMoreBigWeight();
        weightVolumeCollectDto.setLargeDiff(keeTwoDecimals(Math.abs(contrastLarge - moreBigWeight)));
    }

    /**
     * 从计费组装抽检核对数据
     * @param weightVolumeCollectDto 抽检数据
     * @author fanggang7
     * @time 2021-07-29 17:35:51 周四
     */
    private boolean assembleContrastDataFromFinance(WeightVolumeCollectDto weightVolumeCollectDto) {
        boolean hasBusinessFinanceFlag = false;
        try {
            // 调用计费接口获取重量体积信息
            ResponseDTO<BizDutyDTO> responseDto = businessFinanceManager.queryDutyInfo(weightVolumeCollectDto.getWaybillCode());
            if (responseDto != null && Objects.equals(responseDto.getStatusCode(), Constants.NUMBER_ZERO) && responseDto.getData() != null) {
                BizDutyDTO bizDutyDTO = responseDto.getData();
                // 核对来源：计费
                weightVolumeCollectDto.setContrastSourceFrom(ContrastSourceFromEnum.SOURCE_FROM_BILLING.getCode());
                weightVolumeCollectDto.setDutyType(bizDutyDTO.getDutyType());
                weightVolumeCollectDto.setBillingErp(bizDutyDTO.getDutyErp());
                weightVolumeCollectDto.setBillingOrgCode(bizDutyDTO.getFirstLevelId() == null ? null : Integer.parseInt(bizDutyDTO.getFirstLevelId()));
                weightVolumeCollectDto.setBillingOrgName(bizDutyDTO.getFirstLevelName());
                weightVolumeCollectDto.setBillingDeptCodeStr(bizDutyDTO.getSecondLevelId());
                weightVolumeCollectDto.setBillingDeptName(bizDutyDTO.getSecondLevelName());
                weightVolumeCollectDto.setBillingThreeLevelId(bizDutyDTO.getThreeLevelId());
                weightVolumeCollectDto.setBillingThreeLevelName(bizDutyDTO.getThreeLevelName());
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(bizDutyDTO.getDutyErp());
                weightVolumeCollectDto.setBillingCompanyCode(dto == null ? null : dto.getSiteCode());
                weightVolumeCollectDto.setBillingCompany(dto == null ? null : dto.getSiteName());
                double billingWeight = bizDutyDTO.getWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getWeight().doubleValue();
                weightVolumeCollectDto.setBillingWeight(billingWeight);
                double billingVolume = bizDutyDTO.getVolume() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getVolume().doubleValue();
                weightVolumeCollectDto.setBillingVolume(billingVolume);
                // 计费结算重量
                weightVolumeCollectDto.setBillingCalcWeight(bizDutyDTO.getCalcWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getCalcWeight().doubleValue());
                double billingVolumeWeight = getVolumeAndWeight(billingVolume / weightVolumeCollectDto.getVolumeRate());
                // 核对体积重量
                weightVolumeCollectDto.setBillingVolumeWeight(billingVolumeWeight);
                // 核对较大值
                Double contrastLarge = Math.max(billingWeight, billingVolumeWeight);
                weightVolumeCollectDto.setContrastLarge(contrastLarge);
                weightVolumeCollectDto.setLargeDiff(Math.abs(keeTwoDecimals(weightVolumeCollectDto.getContrastLarge() - weightVolumeCollectDto.getMoreBigWeight())));
                hasBusinessFinanceFlag = true;
            }
        } catch (Exception e) {
            log.error("通过运单号{}获取计费信息异常!", weightVolumeCollectDto.getWaybillCode(), e);
        }
        return hasBusinessFinanceFlag;
    }

    /**
     * 计费重量为0或null则获取运单称重流水
     * @param weightVolumeCollectDto
     */
    private void dealWaybillFlow(WeightVolumeCollectDto weightVolumeCollectDto) {
        if(weightVolumeCollectDto.getBillingWeight() != null && !Objects.equals(weightVolumeCollectDto.getBillingWeight(), Constants.DOUBLE_ZERO)){
            return;
        }
        // 核对来源：运单
        weightVolumeCollectDto.setContrastSourceFrom(ContrastSourceFromEnum.SOURCE_FROM_WAYBILL.getCode());
        // 获取运单流水最早的记录
        PackFlowDetail packFlowDetail = getFirstOperateWeight(weightVolumeCollectDto.getWaybillCode());
        if(packFlowDetail == null){
            log.warn("根据单号{}未获取到称重流水!", weightVolumeCollectDto.getWaybillCode());
            return;
        }
        // 责任人
        BaseStaffSiteOrgDto dutyBaseStaffSiteOrgDto = getDutyBaseStaffSiteOrgDto(packFlowDetail);
        if(dutyBaseStaffSiteOrgDto == null || StringUtils.isEmpty(dutyBaseStaffSiteOrgDto.getAccountNumber())){
            log.warn("根据单号{}获取运单称重流水最早操作人为空!", weightVolumeCollectDto.getWaybillCode());
            return;
        }

        weightVolumeCollectDto.setBillingErp(dutyBaseStaffSiteOrgDto.getAccountNumber());
        // 处理责任类型
        dealDutyType(dutyBaseStaffSiteOrgDto, weightVolumeCollectDto);

        double billingWeight = packFlowDetail.getpWeight() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWeight();
        weightVolumeCollectDto.setBillingWeight(billingWeight);
        double billingVolume;
        if(packFlowDetail.getpLength() == null || packFlowDetail.getpWidth() == null
                || packFlowDetail.getpHigh() == null){
            billingVolume = Constants.DOUBLE_ZERO;
        }else {
            billingVolume = packFlowDetail.getpLength() * packFlowDetail.getpWidth() * packFlowDetail.getpHigh();
        }
        weightVolumeCollectDto.setBillingVolume(billingVolume);
        int volumeRate = weightVolumeCollectDto.getVolumeRate();
        double billingVolumeWeight = billingVolume/volumeRate;
        // 核对体积重量
        weightVolumeCollectDto.setBillingVolumeWeight(getVolumeAndWeight(billingVolumeWeight));
        Double contrastLarge = Math.max(billingWeight, billingVolumeWeight);
        // 核对较大值：核对重量和核对体积重量的较大值
        weightVolumeCollectDto.setContrastLarge(contrastLarge);

    }

    /**
     * 获取责任人
     * @param packFlowDetail
     * @return
     */
    private BaseStaffSiteOrgDto getDutyBaseStaffSiteOrgDto(PackFlowDetail packFlowDetail) {
        String billingErp = StringUtils.isEmpty(packFlowDetail.getMeasureUserErp()) ? packFlowDetail.getWeighUserErp() : packFlowDetail.getMeasureUserErp();
        if(StringUtils.isEmpty(billingErp)){
            String billingUserId = StringUtils.isEmpty(packFlowDetail.getMeasureUserId()) ? packFlowDetail.getWeighUserId() : packFlowDetail.getMeasureUserId();
            if (StringUtils.isEmpty(billingUserId) || !NumberHelper.isNumber(billingUserId)){
                log.warn("包裹号{}的首称重操作人{}非法!", packFlowDetail.getPackageCode(), billingUserId);
                return null;
            }
            return baseMajorManager.getBaseStaffByStaffIdNoCache(Integer.valueOf(billingUserId));
        }else {
            return baseMajorManager.getBaseStaffByErpNoCache(billingErp);
        }
    }

    /**
     * 责任类型处理，责任类型不同1、2、3级不同
     *  分拣：1级表示区域，2级表示分拣，3级无
     *  站点：1级表示区域，2级表示片区，3级表示站点
     *  仓：1级表示区域，2级表示配送中心，3级表示仓对应的站点
     * @param dto
     * @return
     */
    private void dealDutyType(BaseStaffSiteOrgDto dto, WeightVolumeCollectDto weightVolumeCollectDto) {
        // 一级
        weightVolumeCollectDto.setBillingOrgCode(dto.getOrgId());
        weightVolumeCollectDto.setBillingOrgName(dto.getOrgName());
        // 实操站点
        weightVolumeCollectDto.setBillingCompanyCode(dto.getSiteCode());
        weightVolumeCollectDto.setBillingCompany(dto.getSiteName());
        if(BusinessUtil.isDistrubutionCenter(dto.getSiteType())){
            // 二级
            weightVolumeCollectDto.setBillingDeptCode(dto.getSiteCode());
            weightVolumeCollectDto.setBillingDeptName(dto.getSiteName());
            weightVolumeCollectDto.setDutyType(DutyTypeEnum.DMS.getCode());
        }else  if(BusinessUtil.isSite(dto.getSiteType())){
            // 站点类型二级为片区
            weightVolumeCollectDto.setBillingDeptCode(Integer.valueOf(String.valueOf(dto.getAreaId())));
            weightVolumeCollectDto.setBillingDeptCodeStr(dto.getAreaCode());
            weightVolumeCollectDto.setBillingDeptName(dto.getAreaName());
            // 站点类型三级为站点
            weightVolumeCollectDto.setBillingThreeLevelId(String.valueOf(dto.getSiteCode()));
            weightVolumeCollectDto.setBillingThreeLevelName(dto.getSiteName());
            weightVolumeCollectDto.setDutyType(DutyTypeEnum.SITE.getCode());
        }else if(BusinessUtil.isWmsSite(dto.getSiteType())){
            // C网抽检暂无仓数据，则不记录二级，只三级
            weightVolumeCollectDto.setBillingThreeLevelId(String.valueOf(dto.getSiteCode()));
            weightVolumeCollectDto.setBillingThreeLevelName(dto.getSiteName());
            weightVolumeCollectDto.setDutyType(DutyTypeEnum.WMS.getCode());
        }else {
            weightVolumeCollectDto.setDutyType(DutyTypeEnum.OTHER.getCode());
        }
    }

    /**
     * 获取称重流水中的第一个操作人的记录
     * @param waybillCode
     * @return
     */
    private PackFlowDetail getFirstOperateWeight(String waybillCode) {

        Page<PackFlowDetail> page = new Page<>();
        page.setPageSize(1000);
        Page<PackFlowDetail> result = waybillPackageManager.getOpeDetailByCode(waybillCode, page);
        if(result == null || CollectionUtils.isEmpty(result.getResult())){
            log.warn("根据单号{}获取称重流水为空!", waybillCode);
            return null;
        }
        List<PackFlowDetail> packageOpeList = result.getResult();
        // 按称重时间升序
        List<PackFlowDetail> weightList = new ArrayList<>(packageOpeList);
        Collections.sort(weightList, new Comparator<PackFlowDetail>() {
            @Override
            public int compare(PackFlowDetail v1, PackFlowDetail v2) {
                if (v1.getWeighTime() == null && v2.getWeighTime() == null) {
                    return 1;
                }
                if (v1.getWeighTime() == null && v2.getWeighTime() != null) {
                    return 1;
                }
                if (v1.getWeighTime() != null && v2.getWeighTime() == null) {
                    return -1;
                }
                return v1.getWeighTime().compareTo(v2.getWeighTime());
            }
        });
        // 按体积测量时间升序
        List<PackFlowDetail> measureList = new ArrayList<>(packageOpeList);
        Collections.sort(measureList, new Comparator<PackFlowDetail>() {
            @Override
            public int compare(PackFlowDetail v1, PackFlowDetail v2) {
                if (v1.getMeasureTime() == null && v2.getMeasureTime() == null) {
                    return 1;
                }
                if (v1.getMeasureTime() == null && v2.getMeasureTime() != null) {
                    return 1;
                }
                if (v1.getMeasureTime() != null && v2.getMeasureTime() == null) {
                    return -1;
                }
                return v1.getMeasureTime().compareTo(v2.getMeasureTime());
            }
        });
        Date weightTime = weightList.get(0).getWeighTime();
        Date measureTime = measureList.get(0).getWeighTime();

        // 获取最早操作的记录
        if(measureTime == null){
            return weightList.get(0);
        }
        if(weightTime == null){
            return measureList.get(0);
        }else {
            return weightTime.before(measureTime) ? weightList.get(0) : measureList.get(0);
        }

    }

    /**
     * 组装抽检复核数据
     * @param packWeightVO
     * @param waybill
     * @param sourceFrom
     * @return
     */
    private void assembleReviewData(PackWeightVO packWeightVO, WeightVolumeCollectDto weightVolumeCollectDto, Waybill waybill, String sourceFrom) {
        weightVolumeCollectDto.setFromSource(sourceFrom);
        weightVolumeCollectDto.setWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
        weightVolumeCollectDto.setPackageCode(packWeightVO.getCodeStr());
        weightVolumeCollectDto.setReviewDate(new Date());

        Double reviewLengthStr = keeTwoDecimals(packWeightVO.getLength());
        Double reviewWidthStr = keeTwoDecimals(packWeightVO.getWidth());
        Double reviewHighStr = keeTwoDecimals(packWeightVO.getHigh());
        Double reviewWeight = keeTwoDecimals(packWeightVO.getWeight());
        Double reviewVolume = keeTwoDecimals(reviewLengthStr * reviewWidthStr * reviewHighStr);
        weightVolumeCollectDto.setReviewLWH(reviewLengthStr + Constants.SEPARATOR_ASTERISK + reviewWidthStr
                + Constants.SEPARATOR_ASTERISK + reviewHighStr);
        weightVolumeCollectDto.setReviewWeight(reviewWeight);
        weightVolumeCollectDto.setReviewVolume(reviewVolume);
        weightVolumeCollectDto.setBusiCode(waybill.getBusiId());
        weightVolumeCollectDto.setBusiName(waybill.getBusiName());
        weightVolumeCollectDto.setMerchantCode(waybill.getBusiOrderCode());

        String waybillSign = waybill.getWaybillSign();
        // 抽检类型
        weightVolumeCollectDto.setSpotCheckType(BusinessHelper.getSpotCheckTypeBorC(waybillSign));
        // 计泡比系数
        int volumeRate = BusinessUtil.isExpress(waybillSign) ? Constants.EXPRESS_VOLUME_RATE : Constants.DEFAULT_VOLUME_RATE;
        weightVolumeCollectDto.setVolumeRate(volumeRate);
        // 复核体积重量
        Double reviewVolumeWeight =  getVolumeAndWeight(reviewVolume/volumeRate);
        weightVolumeCollectDto.setReviewVolumeWeight(reviewVolumeWeight);
        // 复核较大值
        Double moreBigValue = reviewWeight >= reviewVolumeWeight ? reviewWeight : reviewVolumeWeight;
        weightVolumeCollectDto.setMoreBigWeight(moreBigValue);

        // 信任商家
        if(BusinessUtil.isTrustBusi(waybillSign)){
            weightVolumeCollectDto.setIsTrustBusi(Constants.CONSTANT_NUMBER_ONE);
        }else if(BusinessUtil.isSignInChars(waybillSign,56,'0','2','3')) {
            weightVolumeCollectDto.setIsTrustBusi(Constants.NUMBER_ZERO);
        }else {
            weightVolumeCollectDto.setIsTrustBusi(-1);
        }

        // 转运|分拣
        BaseStaffSiteOrgDto baseOrgDto = baseMajorManager.getBaseSiteBySiteId(packWeightVO.getOperatorSiteCode());
        if(baseOrgDto != null && Objects.equals(baseOrgDto.getSiteType(), Constants.DMS_SITE_TYPE)){
            if(Objects.equals(BusinessUtil.isSortOrTransport(baseOrgDto.getSubType()), Constants.CONSTANT_NUMBER_ONE)){
                weightVolumeCollectDto.setReviewSubType(Constants.CONSTANT_NUMBER_ONE);
            }else if(Objects.equals(BusinessUtil.isSortOrTransport(baseOrgDto.getSubType()), Constants.NUMBER_ZERO)){
                weightVolumeCollectDto.setReviewSubType(Constants.NUMBER_ZERO);
            }else{
                weightVolumeCollectDto.setReviewSubType(-1);
            }
        }

        weightVolumeCollectDto.setReviewOrgCode(packWeightVO.getOrganizationCode());
        weightVolumeCollectDto.setReviewOrgName(packWeightVO.getOrganizationName());
        weightVolumeCollectDto.setReviewSiteCode(packWeightVO.getOperatorSiteCode());
        weightVolumeCollectDto.setReviewSiteName(packWeightVO.getOperatorSiteName());
        weightVolumeCollectDto.setReviewErp(packWeightVO.getErpCode());

        // 产品类型
        setProductType(weightVolumeCollectDto, waybill);

        weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO.getCode());
        weightVolumeCollectDto.setIsHasPicture(Constants.NUMBER_ZERO);
        weightVolumeCollectDto.setPictureAddress(Constants.EMPTY_FILL);

        // 一单多件
        final boolean isMultiplePackage = this.getIsMultiplePackage(waybill, packWeightVO.getCodeStr());
        if(isMultiplePackage){
            weightVolumeCollectDto.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
            weightVolumeCollectDto.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
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

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("reviewDate","复核日期");
        headerMap.put("waybillCode","运单号");
        headerMap.put("packageCode","扫描条码");
        headerMap.put("spotCheckType","业务类型");
        headerMap.put("productTypeName","产品标识");
        headerMap.put("merchantCode","配送商家编号");
        headerMap.put("busiName","商家名称");
        headerMap.put("isTrustBusi","信任商家");
        headerMap.put("reviewOrgName","复核区域");
        headerMap.put("reviewSiteName","复核分拣");
        headerMap.put("reviewSubType","机构类型");
        headerMap.put("reviewErp","复核人erp");
        headerMap.put("reviewWeight","复核重量");
        headerMap.put("reviewLWH","复核长宽高");
        headerMap.put("reviewVolumeWeight","复核体积重量");
        headerMap.put("moreBigWeight","复核较大值");
        headerMap.put("volumeRate","计泡系数");
        headerMap.put("billingOrgName","核对操作区域");
        headerMap.put("billingDeptName","核对操作片区");
        headerMap.put("BillingCompany","核对操作单位");
        headerMap.put("billingErp","核对操作人ERP");
        headerMap.put("billingCalcWeight","计费结算重量");
        headerMap.put("billingWeight","核对重量");
        headerMap.put("billingVolume","核对体积");
        headerMap.put("billingVolumeWeight","核对体积重量");
        headerMap.put("contrastLarge","核对较大值");
        headerMap.put("contrastSourceFrom","核对来源");
        headerMap.put("largeDiff","较大值差异");
        headerMap.put("diffStandard","误差标准值");
        headerMap.put("isExcess","是否超标");
        headerMap.put("fromSource","数据来源");
        headerMap.put("isHasPicture","有无图片");
        headerMap.put("pictureAddress","图片链接");
        return headerMap;
    }

    /**
     * 获取来源
     * @param fromSource
     * @return
     */
    private String getFromSource(String fromSource) {
        if(StringUtils.isEmpty(fromSource)){
            return null;
        }
        // 为了兼容之前定义的枚举。。。。。
        if(FromSourceEnum.DMS_CLIENT_PLATE_PRINT.name().equals(fromSource)
                || SpotCheckSourceEnum.SPOT_CHECK_CLIENT_PLATE.name().equals(fromSource)){
            return "平台打印抽检";
        }
        if(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name().equals(fromSource)
                || SpotCheckSourceEnum.SPOT_CHECK_DWS.name().equals(fromSource)){
            return "DWS抽检";
        }
        return SpotCheckSourceEnum.SPOT_CHECK_DMS_WEB.name().equals(fromSource) ? "B网网页抽检" :
                SpotCheckSourceEnum.SPOT_CHECK_ANDROID.name().equals(fromSource) ? "B网安卓抽检" : "其它";
    }

    /**
     * 分页获取导出数据
     *
     * @param condition
     */
    @Override
    public void export(WeightAndVolumeCheckCondition condition, BufferedWriter innerBfw) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, innerBfw, headerMap.values().size());
            // 分页查询记录
            WeightVolumeQueryCondition weightVolumeQueryCondition = transform(condition);

            // 设置总导出数据
            Integer uccSpotCheckMaxSize = exportConcurrencyLimitService.uccSpotCheckMaxSize();

            int queryTotal = 0;
            int index = 1;
            while (index++ <= 1000) {
                BaseEntity<WeightVolumeCollectScrollResult> baseEntity = reportExternalService.queryWeightVolumeByScroll(weightVolumeQueryCondition);
                if(baseEntity == null || !baseEntity.isSuccess()
                        || baseEntity.getData() == null || CollectionUtils.isEmpty(baseEntity.getData().getList())){
                    log.warn("scroll查询抽检明细数据为空!");
                    break;
                }
                // 设置scrollId
                weightVolumeQueryCondition.setScrollId(baseEntity.getData().getScrollId());
                // 输出至excel
                CsvExporterUtils.writeCsvByPage(innerBfw, headerMap, trans2ExportDto(baseEntity.getData().getList()));
                // 限制导出数量
                queryTotal += baseEntity.getData().getList().size();
                if(queryTotal > uccSpotCheckMaxSize){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getName(), end-start,queryTotal);
        }catch (Exception e){
            log.error("分页获取导出数据失败",e);
        }
    }

    /**
     * es对象转换为导出对象
     * @param weightVolumeCollectDtoList
     * @return
     */
    private List<ExportWeightVolumeCollectDto> trans2ExportDto(List<WeightVolumeCollectDto> weightVolumeCollectDtoList) {
        List<ExportWeightVolumeCollectDto> list = new ArrayList<ExportWeightVolumeCollectDto>();
        for (WeightVolumeCollectDto dto : weightVolumeCollectDtoList) {
            ExportWeightVolumeCollectDto exportWeightVolumeCollectDto = new ExportWeightVolumeCollectDto();
            exportWeightVolumeCollectDto.setReviewDate(DateHelper.formatDate(dto.getReviewDate(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
            exportWeightVolumeCollectDto.setWaybillCode(dto.getWaybillCode());
            exportWeightVolumeCollectDto.setPackageCode(dto.getPackageCode());
            exportWeightVolumeCollectDto.setSpotCheckType(Objects.equals(dto.getSpotCheckType(),Constants.CONSTANT_NUMBER_ONE) ? "B网":"C网");
            exportWeightVolumeCollectDto.setProductTypeName(dto.getProductTypeName());
            exportWeightVolumeCollectDto.setMerchantCode(dto.getMerchantCode());
            exportWeightVolumeCollectDto.setBusiName(dto.getBusiName());
            exportWeightVolumeCollectDto.setIsTrustBusi(Objects.equals(dto.getIsTrustBusi(),Constants.CONSTANT_NUMBER_ONE) ? "是" : "否");
            exportWeightVolumeCollectDto.setReviewOrgName(dto.getReviewOrgName());
            exportWeightVolumeCollectDto.setReviewSiteName(dto.getReviewSiteName());
            exportWeightVolumeCollectDto.setReviewSubType(Objects.equals(dto.getReviewSubType(),Constants.CONSTANT_NUMBER_ONE) ? "分拣中心":"转运中心");
            exportWeightVolumeCollectDto.setReviewErp(dto.getReviewErp());
            exportWeightVolumeCollectDto.setReviewWeight(dto.getReviewWeight() == null ? null : String.valueOf(dto.getReviewWeight()));
            exportWeightVolumeCollectDto.setReviewLWH(dto.getReviewLWH());
            exportWeightVolumeCollectDto.setReviewVolumeWeight(dto.getReviewVolumeWeight() == null ? null : String.valueOf(dto.getReviewVolumeWeight()));
            exportWeightVolumeCollectDto.setMoreBigWeight(dto.getMoreBigWeight() == null ? null : String.valueOf(dto.getMoreBigWeight()));
            exportWeightVolumeCollectDto.setVolumeRate(dto.getVolumeRate() == null ? null : String.valueOf(dto.getVolumeRate()));
            exportWeightVolumeCollectDto.setBillingOrgName(dto.getBillingOrgName());
            exportWeightVolumeCollectDto.setBillingDeptName(dto.getBillingDeptName());
            exportWeightVolumeCollectDto.setBillingCompany(dto.getBillingCompany());
            exportWeightVolumeCollectDto.setBillingErp(dto.getBillingErp());
            exportWeightVolumeCollectDto.setBillingCalcWeight(dto.getBillingCalcWeight() == null ? null : String.valueOf(dto.getBillingCalcWeight()));
            exportWeightVolumeCollectDto.setBillingWeight(dto.getBillingWeight() == null ? null : String.valueOf(dto.getBillingWeight()));
            exportWeightVolumeCollectDto.setBillingVolume(dto.getBillingVolume() == null ? null : String.valueOf(dto.getBillingVolume()));
            exportWeightVolumeCollectDto.setBillingVolumeWeight(dto.getBillingVolumeWeight() == null ? null : String.valueOf(dto.getBillingVolumeWeight()));
            exportWeightVolumeCollectDto.setContrastLarge(dto.getContrastLarge() == null ? null : String.valueOf(dto.getContrastLarge()));
            exportWeightVolumeCollectDto.setContrastSourceFrom(Objects.equals(dto.getContrastSourceFrom(),Constants.CONSTANT_NUMBER_ONE) ? "运单" : "计费");
            exportWeightVolumeCollectDto.setLargeDiff(dto.getLargeDiff() == null ? null : String.valueOf(dto.getLargeDiff()));
            exportWeightVolumeCollectDto.setDiffStandard(dto.getDiffStandard());
            exportWeightVolumeCollectDto.setIsExcess(Objects.equals(dto.getIsExcess(),Constants.CONSTANT_NUMBER_ONE) ? "超标" : "未超标");
            exportWeightVolumeCollectDto.setFromSource(getFromSource(dto.getFromSource()));
            exportWeightVolumeCollectDto.setIsHasPicture(Objects.equals(dto.getIsHasPicture(),Constants.CONSTANT_NUMBER_ONE) ? "有" : "无");
            exportWeightVolumeCollectDto.setPictureAddress(dto.getPictureAddress());
            list.add(exportWeightVolumeCollectDto);
        }
        return list;
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
        newCondition.setRecordType(condition.getRecordType());
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
    public void setProductType(WeightVolumeCollectDto weightVolumeCollectDto, Waybill waybill) {
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(Constants.PRODUCT_PARENT_ID);
        HashMap<String, DmsBaseDict> map = new HashMap<String, DmsBaseDict>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getTypeName(), list.get(i));
        }
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

    /**
     * 查询最新一条抽检记录数据，如果不存在则返回null
     * @param query 查询条件
     * @return 抽检记录
     * @author fanggang7
     * @time 2020-08-24 17:12:55 周一
     */
    @Override
    public InvokeResult<WeightVolumeCollectDto> queryLatestCheckRecord(WeightVolumeQueryCondition query) {
        log.info("queryLatestCheckRecord param: {}", JSON.toJSONString(query));
        InvokeResult<WeightVolumeCollectDto> result = new InvokeResult<>();
        try {
            Pager<WeightVolumeQueryCondition> pager = new Pager<>();
            pager.setSearchVo(query);
            pager.setPageNo(1);
            pager.setPageSize(50);
            BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity = reportExternalService.getPagerByConditionForWeightVolume(pager);
            if (baseEntity.getCode() == BaseEntity.CODE_SUCCESS) {
                Pager<WeightVolumeCollectDto> pageData = baseEntity.getData();
                if (CollectionUtils.isNotEmpty(pageData.getData())) {
                    WeightVolumeCollectDto weightVolumeCollectDto = pageData.getData().get(0);
                    result.setData(weightVolumeCollectDto);
                    if(weightVolumeCollectDto.getIsHasPicture() == 1){
                    }
                }
            } else {
                log.warn("queryLatestCheckRecord getPagerByConditionForWeightVolume warn {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(query), baseEntity.getMessage());
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setData(null);
            }
        } catch (Exception e) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            log.error("服务异常,根据查询条件查询es失败:{}", JsonHelper.toJson(query), e);
        }
        return result;
    }

    /**
     * 查看抽检记录是否上传图片
     * @param packageCode 运单或包裹号
     * @param siteCode 站点
     * @return 上传图片的记录结果，可能为空
     * @author fanggang7
     * @time 2020-08-26 15:17:53 周三
     */
    private WeightVolumeCollectDto getUploadImgRecord(String packageCode, Integer siteCode){
        //获取图片链接
        InvokeResult<String> result = searchExcessPicture(packageCode,siteCode);
        if(result == null || result.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                || StringUtils.isEmpty(result.getData())){
            log.warn("WeightAndVolumeCheckServiceImpl.getUploadImgRecord 运单【{}】站点【{}】的超标图片为空",packageCode,siteCode);
            return null;
        }

        WeightVolumeCollectDto weightVolumeCollectDto;
        try {
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(IsExcessEnum.EXCESS_ENUM_YES.getCode());
            condition.setIsHasPicture(Constants.YN_YES);
            condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
            if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())
                    || baseEntity.getData().get(0) == null){
                log.warn("通过运单【{}】站点【{}】查询超标数据为空",packageCode,siteCode);
                return null;
            }
            weightVolumeCollectDto = baseEntity.getData().get(0);
        }catch (Exception e){
            log.warn("通过运单【{}】站点【{}】查询超标数据异常",packageCode,siteCode,e);
            return null;
        }
        return weightVolumeCollectDto;
    }

    /**
     * 更新抽检记录的图片
     * @param packageCode 运单或包裹号
     * @param siteCode 站点
     * @return 更新后的抽检记录，可能为空
     * @author fanggang7
     * @time 2020-08-26 15:17:53 周三
     */
    private WeightVolumeCollectDto updateCheckRecordImage(String packageCode, Integer siteCode){
        //获取图片链接
        InvokeResult<String> result = searchExcessPicture(packageCode,siteCode);
        if(result == null || result.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                || StringUtils.isEmpty(result.getData())){
            log.warn("运单【{}】站点【{}】的超标图片为空",packageCode,siteCode);
            return null;
        }
        String pictureAddress = result.getData();

        WeightVolumeCollectDto weightVolumeCollectDto;
        try {
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(IsExcessEnum.EXCESS_ENUM_YES.getCode());
            condition.setIsHasPicture(0);
            condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
            if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())
                    || baseEntity.getData().get(0) == null){
                log.warn("通过运单【{}】站点【{}】查询超标数据为空",packageCode,siteCode);
                return null;
            }
            weightVolumeCollectDto = baseEntity.getData().get(0);
        }catch (Exception e){
            log.warn("通过运单【{}】站点【{}】查询超标数据异常",packageCode,siteCode,e);
            return null;
        }

        //更新es数据设置图片连接
        try {
            weightVolumeCollectDto.setPictureAddress(pictureAddress);
            weightVolumeCollectDto.setIsHasPicture(1);
            reportExternalService.updateForWeightVolume(weightVolumeCollectDto);
            return weightVolumeCollectDto;
        }catch (Exception e){
            log.warn("通过运单【{}】站点【{}】更新超标数据异常",packageCode,siteCode,e);
            return null;
        }
    }

    /**
     * 处理消费称重抽检处理消息
     *
     * @param weightAndVolumeCheckHandleMessage 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-08-25 10:08:37 周二
     */
    @Override
    public InvokeResult<Boolean> handleAfterUploadImgMessageOrAfterSend(final WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage) {
        log.info("handleAfterUploadImgMessage param: {}", JSON.toJSONString(weightAndVolumeCheckHandleMessage));
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);

        // 上传图片环节 || 图片早于抽检数据环节
        if(weightAndVolumeCheckHandleMessage.getOpNode() == WeightAndVolumeCheckHandleMessage.UPLOAD_IMG
                || weightAndVolumeCheckHandleMessage.getOpNode() == WeightAndVolumeCheckHandleMessage.IMG_BEFORE_DATA){
            // 1 一单一件处理
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
            final boolean isMultiplePackage = this.getIsMultiplePackage(waybill, weightAndVolumeCheckHandleMessage.getPackageCode());
            if(!isMultiplePackage){
                // 1.1 检查运单状态，如果运单状态为未发货，不下发
                WeightVolumeCollectDto weightVolumeCollectDto = this.getUploadImgRecord(weightAndVolumeCheckHandleMessage.getWaybillCode(), weightAndVolumeCheckHandleMessage.getSiteCode());
                // 1.2 一单一件运单状态为已发货，则下发到FXM
                if(weightVolumeCollectDto != null){
                    boolean packageSendStatus = this.getWaybillSendStatus(weightVolumeCollectDto);
                    if(packageSendStatus){
                        this.sendMqToFxm(weightVolumeCollectDto);
                    }
                    return result;
                }
            } else {
                // 2 一单多件处理
                // 更新运单纬度是否有图片字段数据
                WeightVolumeCollectDto updateWeightVolumeCollectDto = new WeightVolumeCollectDto();
                updateWeightVolumeCollectDto.setPackageCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
                updateWeightVolumeCollectDto.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
                updateWeightVolumeCollectDto.setIsHasPicture(Constants.YN_YES);
                BaseEntity<String> updateResult = reportExternalService.insertOrUpdateForWeightVolume(updateWeightVolumeCollectDto);
                if(!updateResult.isSuccess()){
                    log.error("handleAfterUploadImgMessageOrAfterSend updateForWeightVolume error {}", JsonHelper.toJson(updateResult));
                }
                // 再处理下发
                this.sendMqToFxmForMultiplePackage(weightAndVolumeCheckHandleMessage, waybill);
            }

        }

        // 发货完成环节
        if(weightAndVolumeCheckHandleMessage.getOpNode() == WeightAndVolumeCheckHandleMessage.SEND){
            return this.handleBySendOpForImgMessage(weightAndVolumeCheckHandleMessage);
        }

        return result;
    }

    /**
     * 处理发货环节生成的抽检下发消息
     * @param weightAndVolumeCheckHandleMessage 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-08-26 15:18:49 周三
     */
    private InvokeResult<Boolean> handleBySendOpForImgMessage(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
        int packNum = this.getPackageNumberTotal(waybill, weightAndVolumeCheckHandleMessage.getPackageCode());
        final boolean isMultiplePackage = packNum > Constants.CONSTANT_NUMBER_ONE;
        // 如果是运单，则按运单号更新所有包裹发货状态
        WeightVolumeQueryCondition query = new WeightVolumeQueryCondition();
        query.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
        String waybillCode = weightAndVolumeCheckHandleMessage.getWaybillCode();
        if (WaybillUtil.isWaybillCode(weightAndVolumeCheckHandleMessage.getPackageCode())) {
            query.setWaybillCode(waybillCode);
            if(isMultiplePackage){
                // 只更新运单发货状态
                query.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
                query.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
            }
        } else {
            query.setPackageCode(weightAndVolumeCheckHandleMessage.getPackageCode());
        }
        Pager<WeightVolumeQueryCondition> pager = new Pager<>();
        pager.setSearchVo(query);
        pager.setPageNo(1);
        pager.setPageSize(packNum);
        pager.setSearchVo(query);
        BaseEntity<Pager<WeightVolumeCollectDto>> weightVolumeExistResult = reportExternalService.getPagerByConditionForWeightVolume(pager);
        if(weightVolumeExistResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.warn("updatePackageSendStatus getPagerByConditionForWeightVolume warn {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(query), weightVolumeExistResult.getMessage());
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setData(false);
            return result;
        }
        Pager<WeightVolumeCollectDto> pageData = weightVolumeExistResult.getData();
        // 无抽检记录，直接返回成功
        if (CollectionUtils.isEmpty(pageData.getData())) {
            return result;
        }
        // 已有的抽检记录，只更新抽检记录中的发货状态
        List<WeightVolumeCollectDto> existList = pageData.getData();
        for (WeightVolumeCollectDto weightVolumeCollectDto : existList) {
            WeightVolumeCollectDto updateWeightVolumeCollectDto = new WeightVolumeCollectDto();
            updateWeightVolumeCollectDto.setPackageCode(weightVolumeCollectDto.getPackageCode());
            updateWeightVolumeCollectDto.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());
            updateWeightVolumeCollectDto.setWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
            // 更新发货状态
            BaseEntity<Boolean> updateResult = reportExternalService.updateForWeightVolume(updateWeightVolumeCollectDto);
            if(!updateResult.isSuccess()){
                log.error("handleBySendOpForImgMessage updateForWeightVolume error {}", JsonHelper.toJson(updateResult));
            }
        }
        if(!isMultiplePackage){
            // 一单一件处理
            List<WeightVolumeCollectDto> existCurrentSiteHasPictureList = this.getHasPictureCheckRecordList(existList);
            // 无上传图片的抽检记录，此时还不能下发给下游，直接返回
            if(CollectionUtils.isEmpty(existCurrentSiteHasPictureList)){
                return result;
            }
            // 如果有图片，则下发fxm
            this.sendMqToFxm(existCurrentSiteHasPictureList.get(0));
        } else {
            // 如果满足条件，一单多件则按运单纬度处理下发fxm
            this.sendMqToFxmForMultiplePackage(weightAndVolumeCheckHandleMessage, waybill);
        }
        return result;
    }

    private void sendMqToFxmForMultiplePackage(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage, Waybill waybill){
        // 如果已经存在下发fxm的mq缓存，短时时间内不用再次下发
        String cacheFxmSendWaybillKey = String.format(CacheKeyConstants.CACHE_KEY_FXM_SEND_WAYBILL, weightAndVolumeCheckHandleMessage.getWaybillCode());
        final boolean cacheFxmSendWaybillExist = jimdbCacheService.exists(cacheFxmSendWaybillKey);
        if(cacheFxmSendWaybillExist){
            log.info("sendMqToFxmForMultiplePackage cacheFxmSendWaybillExist will not send {}", weightAndVolumeCheckHandleMessage.getWaybillCode());
            return;
        }
        // 一单多件则按运单纬度处理下发
        final boolean canSendMqToFxmForMultiplePackage = this.checkCanSendMqToFxmForMultiplePackage(weightAndVolumeCheckHandleMessage, waybill);
        if(canSendMqToFxmForMultiplePackage){
            // 查询运单纬度数据，进行下发
            final WeightVolumeCollectDto waybillWeightVolumeCollectDto = this.getWaybillWeightVolumeCollectDto4MultiplePackage(weightAndVolumeCheckHandleMessage);
            if(waybillWeightVolumeCollectDto != null){
                this.sendMqToFxm(waybillWeightVolumeCollectDto);
                // 存储运单纬度的fxm下发缓存
                jimdbCacheService.setEx(cacheFxmSendWaybillKey, "1", this.cacheFxmSendWaybillExpireTime, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * 判断是否能发送给fxm
     * 发货+照片+超标+关键信息齐全+包裹集齐
     * @param weightAndVolumeCheckHandleMessage
     * @return
     */
    private boolean checkCanSendMqToFxmForMultiplePackage(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage, Waybill waybill) {
        // 如果运单整体已发货，就不用再关系包裹发货状态
        boolean waybillSendStatus = this.getWaybillSendStatusCache(weightAndVolumeCheckHandleMessage.getWaybillCode());
        final int waybillPackTotalNum = this.getPackageNumberTotal(waybill, weightAndVolumeCheckHandleMessage.getPackageCode());
        // 查找运单纬度的发货状态
        WeightVolumeQueryCondition waybillVolumeQueryCondition = new WeightVolumeQueryCondition();
        waybillVolumeQueryCondition.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
        waybillVolumeQueryCondition.setWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
        waybillVolumeQueryCondition.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
        waybillVolumeQueryCondition.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
        waybillVolumeQueryCondition.setWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
        // 找es已满足条件的总数
        BaseEntity<Long> totalResult = reportExternalService.countByParam(waybillVolumeQueryCondition);
        if(totalResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.error("checkCanSendMqToFxmForMultiplePackage warn waybillVolumeQueryCondition {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(waybillVolumeQueryCondition), totalResult.getMessage());
            return false;
        }
        long total = totalResult.getData();
        if(total == 1){
            waybillSendStatus = true;
        }
        // 如果是C网纯配运单，且是DWS上传的，判断 发货 + 照片 + 集齐
        WeightVolumeQueryCondition packageVolumeQueryCondition = new WeightVolumeQueryCondition();
        packageVolumeQueryCondition.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
        packageVolumeQueryCondition.setWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
        packageVolumeQueryCondition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
        packageVolumeQueryCondition.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
        packageVolumeQueryCondition.setIsHasPicture(Constants.YN_YES);
        packageVolumeQueryCondition.setNotThesePackageCode(new ArrayList<>(Arrays.asList(weightAndVolumeCheckHandleMessage.getPackageCode())));
        // 如果缓存未记录已发货，则需要查es中的发货状态
        if(!waybillSendStatus){
            packageVolumeQueryCondition.setWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
        }
        // 找es已满足条件的总数
        totalResult = reportExternalService.countByParam(packageVolumeQueryCondition);
        if(totalResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.error("checkCanSendMqToFxmForMultiplePackage warn {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(packageVolumeQueryCondition), totalResult.getMessage());
            return false;
        }
        // 找es不满足发货、有图片的数据的包裹列表，根据此包裹列表，查找缓存中是否有记录，如果发货、有图片二者缓存都有，则满足条件
        total = totalResult.getData();
        if(total >= waybillPackTotalNum){
            return true;
        }
        // 运单整体未发货
        List<String> notSendSpotCheckPackageCodeList = new ArrayList<>();
        if(!waybillSendStatus){
            // 查找未发货数据
            WeightVolumeQueryCondition notSendSpotCheckCondition = new WeightVolumeQueryCondition();
            BeanUtils.copyProperties(packageVolumeQueryCondition, notSendSpotCheckCondition);
            notSendSpotCheckCondition.setWaybillStatus(null);
            notSendSpotCheckCondition.setNotSendWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
            notSendSpotCheckCondition.setIsHasPicture(null);
            notSendSpotCheckCondition.setNotThesePackageCode(new ArrayList<>(Arrays.asList(weightAndVolumeCheckHandleMessage.getPackageCode())));
            Pager<WeightVolumeQueryCondition> notSendSpotCheckConditionPager = new Pager<>();
            notSendSpotCheckConditionPager.setPageSize(waybillPackTotalNum);
            notSendSpotCheckConditionPager.setPageNo(1);
            notSendSpotCheckConditionPager.setSearchVo(notSendSpotCheckCondition);
            final BaseEntity<Pager<String>> notSendSpotCheckResult = reportExternalService.getSpotCheckPackageCodesByCondition(notSendSpotCheckConditionPager);
            // 遍历包裹数据，查找缓存，命中缓存都是已发货
            notSendSpotCheckPackageCodeList = notSendSpotCheckResult.getData().getData();
        }
        // 查找无图片数据
        WeightVolumeQueryCondition noPicSpotCheckCondition = new WeightVolumeQueryCondition();
        BeanUtils.copyProperties(packageVolumeQueryCondition, noPicSpotCheckCondition);
        noPicSpotCheckCondition.setIsHasPicture(Constants.YN_NO);
        noPicSpotCheckCondition.setWaybillStatus(null);
        noPicSpotCheckCondition.setNotThesePackageCode(new ArrayList<>(Arrays.asList(weightAndVolumeCheckHandleMessage.getPackageCode())));
        Pager<WeightVolumeQueryCondition> noPicSpotCheckConditionPager = new Pager<>();
        noPicSpotCheckConditionPager.setPageSize(waybillPackTotalNum);
        noPicSpotCheckConditionPager.setPageNo(1);
        noPicSpotCheckConditionPager.setSearchVo(noPicSpotCheckCondition);
        final BaseEntity<Pager<String>> noPicSpotCheckResult = reportExternalService.getSpotCheckPackageCodesByCondition(noPicSpotCheckConditionPager);
        final List<String> noPicSpotCheckPackageCodeList = noPicSpotCheckResult.getData().getData();

        // 查找交集
        final HashSet<String> notSendAndNoPicSpotCheckPackageCodeSet = new HashSet<>(notSendSpotCheckPackageCodeList);
        notSendAndNoPicSpotCheckPackageCodeSet.retainAll(new HashSet<>(noPicSpotCheckPackageCodeList));
        // 遍历未发货+无图片交集，判断是否满足既有发货缓存又有图片缓存
        for (String packageCode : notSendAndNoPicSpotCheckPackageCodeSet) {
            String pictureUrl = this.getPackagePicUrlCache(packageCode, weightAndVolumeCheckHandleMessage.getSiteCode());
            final boolean packageSendStatus = waybillSendStatus || this.getWaybillSendStatusCache(packageCode);
            // 有图片
            if(StringUtils.isNotEmpty(pictureUrl) && packageSendStatus){
                total++;
            }
        }

        if(!waybillSendStatus){
            // 遍历仅未发货包裹数据，判断是否有发货缓存
            List<String> justNotSendSpotCheckPackageCodeList = new ArrayList<>(notSendSpotCheckPackageCodeList);
            justNotSendSpotCheckPackageCodeList.removeAll(notSendAndNoPicSpotCheckPackageCodeSet);
            for (String packageCode : justNotSendSpotCheckPackageCodeList) {
                if(this.getWaybillSendStatusCache(packageCode)){
                    total++;
                }
            }
        }
        // 遍历仅无图片包裹数据，判断是否有图片缓存
        List<String> justNoPicSpotCheckPackageCodeList = new ArrayList<>(noPicSpotCheckPackageCodeList);
        justNoPicSpotCheckPackageCodeList.removeAll(notSendAndNoPicSpotCheckPackageCodeSet);
        for (String packageCode : justNoPicSpotCheckPackageCodeList) {
            String pictureUrl = this.getPackagePicUrlCache(packageCode, weightAndVolumeCheckHandleMessage.getSiteCode());
            if(StringUtils.isNotEmpty(pictureUrl)){
                total++;
            }
        }
        if(total + 1 >= waybillPackTotalNum){
            return true;
        } else {
            log.info("checkCanSendMqToFxmForMultiplePackage 未集齐，不发送");
        }

        return false;
    }

    private WeightVolumeCollectDto getWaybillWeightVolumeCollectDto4MultiplePackage(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage){
        // 查询运单纬度数据，进行下发
        WeightVolumeQueryCondition query = new WeightVolumeQueryCondition();
        query.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
        query.setWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
        query.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode());
        query.setMultiplePackage(IsMultiPackEnum.MULTIPLE.getCode());
        final BaseEntity<List<WeightVolumeCollectDto>> waybillCollectResult = reportExternalService.getByParamForWeightVolume(query);
        if(waybillCollectResult == null || CollectionUtils.isEmpty(waybillCollectResult.getData()) || waybillCollectResult.getData().get(0) == null){
            log.warn("通过运单【{}】站点【{}】查询超标数据为空",weightAndVolumeCheckHandleMessage.getWaybillCode(), weightAndVolumeCheckHandleMessage.getSiteCode());
            return null;
        }
        return waybillCollectResult.getData().get(0);
    }

    /**
     * 获取已经上传图片的抽检记录，并排序
     * @param existCurrentSiteList 抽检记录
     * @return 结果
     * @author fanggang7
     * @time 2020-08-26 14:59:39 周三
     */
    private List<WeightVolumeCollectDto> getHasPictureCheckRecordList(List<WeightVolumeCollectDto> existCurrentSiteList){
        List<WeightVolumeCollectDto> existCurrentSiteHasPictureList = new ArrayList<>();
        for (WeightVolumeCollectDto weightVolumeCollectDto : existCurrentSiteList) {
            if(Objects.equals(weightVolumeCollectDto.getIsHasPicture(), Constants.YN_YES) && Objects.equals(weightVolumeCollectDto.getIsExcess(), Constants.YN_YES)){
                existCurrentSiteHasPictureList.add(weightVolumeCollectDto);
            }
        }
        // 按抽检日期排序
        Collections.sort(existCurrentSiteHasPictureList, new Comparator<WeightVolumeCollectDto>(){
            @Override
            public int compare(WeightVolumeCollectDto o1, WeightVolumeCollectDto o2) {
                //忽略掉大小写后,进行字符串的比较
                Date s1 = o1.getReviewDate();
                Date s2 = o2.getReviewDate();
                return s1.compareTo(s2);
            }
        });
        return existCurrentSiteHasPictureList;
    }

    /**
     * 获取包裹图片缓存
     * @return String 图片url，为空表示没有
     * @author fanggang7
     * @time 2021-08-11 00:40:25 周三
     */
    private String getPackagePicUrlCache(String packageCode, Integer siteCode) {
        String key = String.format(CacheKeyConstants.CACHE_KEY_SPOT_CHECK_PICTURE_URL_UPLOAD_FLAG, packageCode, siteCode);
        try {
            String pictureUrl = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(pictureUrl)){
                return pictureUrl;
            }
        }catch (Exception e){
            log.error("获取C网抽检下发MQ缓存【{}】异常",key);
        }
        return null;
    }

    /**
     * 获取包裹发货状态
     * @return boolean 发货-true,未发货-false
     * @author fanggang7
     * @time 2020-08-26 15:08:54 周三
     */
    private boolean getWaybillSendStatusCache(String waybillCode) {
        String key = CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_STATUS.concat(waybillCode);
        try {
            String redisValue = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(redisValue) && Integer.parseInt(redisValue) == Constants.YN_YES){
                return true;
            }
        }catch (Exception e){
            log.error("获取获取包裹发货状态缓存【{}】异常", key);
        }
        return false;
    }

    /**
     * 获取包裹发货状态
     * @param weightVolumeCollectDto 抽检记录
     * @return boolean 发货-true,未发货-false
     * @author fanggang7
     * @time 2020-08-26 15:08:54 周三
     */
    private boolean getWaybillSendStatus(WeightVolumeCollectDto weightVolumeCollectDto){
        final boolean waybillSendStatusCache = getWaybillSendStatusCache(weightVolumeCollectDto.getWaybillCode());
        if(waybillSendStatusCache){
            return true;
        }
        if(Objects.equals(weightVolumeCollectDto.getWaybillStatus(), WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY)){
            return true;
        }
        return false;
    }

    /**
     * 下发给下游
     * @param weightVolumeCollectDto 最新的一条有图片的抽检记录
     * @author fanggang7
     * @time 2020-08-26 15:00:19 周三
     */
    private void sendMqToFxm(WeightVolumeCollectDto weightVolumeCollectDto){
        // C抽B 临时方案,不下发
        if(weightVolumeCollectDto.getSpotCheckType().equals(SpotCheckTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            return;
        }
        // 下发
        AbnormalResultMq abnormalResultMq = convertToAbnormalResultMq(weightVolumeCollectDto);
        if(abnormalResultMq == null){
            log.warn("sendMqToFxm convertToAbnormalResultMq 组装消息报文失败 {}", JsonHelper.toJson(weightVolumeCollectDto));
            return;
        }
        log.info("发送MQ【{}】,业务ID【{}】 ",dmsWeightVolumeExcess.getTopic(),abnormalResultMq.getAbnormalId());
        log.info("sendMqToFxm abnormalResultMq {}", JsonHelper.toJson(abnormalResultMq));
        dmsWeightVolumeExcess.sendOnFailPersistent(abnormalResultMq.getAbnormalId(), JsonHelper.toJson(abnormalResultMq));
    }

    /**
     * 查询PDA上传的图片
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    public List<String> spotCheckPdaPictures(String waybillCode, Integer siteCode) {
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        List<String> list = new ArrayList<>();
        condition.setReviewSiteCode(siteCode);
        condition.setIsExcess(1);
        condition.setWaybillCode(waybillCode);
        BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
        log.info("spotCheckPdaPictures.result={}", JSON.toJSONString(baseEntity));
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())
                || baseEntity.getData().get(0) == null) {
            log.warn("通过运单【{}】站点【{}】查询超标数据为空", waybillCode, siteCode);
            return list;
        }
        return StringUtils.isBlank(baseEntity.getData().get(0).getPictureAddress()) ? list : Arrays.asList(baseEntity.getData().get(0).getPictureAddress().split(";"));
    }


    /**
     * 记录抽检记录
     * @param weightVolumeCollectDto
     */
    @Override
    public void recordSpotCheckLog(WeightVolumeCollectDto weightVolumeCollectDto, SpotCheckSourceEnum spotCheckSourceEnum) {
        BusinessLogConstans.OperateTypeEnum operateTypeEnum;
        if(Objects.equals(spotCheckSourceEnum, SpotCheckSourceEnum.SPOT_CHECK_CLIENT_PLATE)
                || Objects.equals(spotCheckSourceEnum, SpotCheckSourceEnum.SPOT_CHECK_DWS)){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_C;
        }else if(Objects.equals(spotCheckSourceEnum, SpotCheckSourceEnum.SPOT_CHECK_DMS_WEB)){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_B;
        }else {
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_ANDROID;
        }
        try {
            long startTime = System.currentTimeMillis();
            JSONObject request = new JSONObject();
            request.put("operatorCode", weightVolumeCollectDto.getReviewErp());
            request.put("siteCode", weightVolumeCollectDto.getReviewSiteCode());
            request.put("operateTime", weightVolumeCollectDto.getReviewDate().getTime());
            request.put("waybillCode", weightVolumeCollectDto.getWaybillCode());
            request.put("packageCode", weightVolumeCollectDto.getPackageCode());
            long endTime = System.currentTimeMillis();

            BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(operateTypeEnum)
                    .processTime(endTime, startTime)
                    .operateRequest(request)
                    .reMark(weightVolumeCollectDto.getExcessReason())
                    .methodName("WeightAndVolumeCheckServiceImpl")
                    .build();

            logEngine.addLog(logProfiler);
        }catch (Exception e){
            log.error("WeightAndVolumeCheckServiceImpl recordSpotCheckLog异常!", e);
        }
    }
}
