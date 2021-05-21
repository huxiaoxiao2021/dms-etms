package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

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
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.ExportWeightVolumeCollectDto;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
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
import com.jd.ql.dms.report.domain.Enum.ContrastSourceFromEnum;
import com.jd.ql.dms.report.domain.Enum.IsExcessEnum;
import com.jd.ql.dms.report.domain.Enum.SpotCheckTypeEnum;
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
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillPackageManager waybillPackageManager;
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
    public String searchPictureUrlRecent(String prefixName) {
        ObjectListing objectListing = listObject(prefixName, null, 100);
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
    public void updateImgAndSendHandleMq(String packageCode, Integer siteCode){

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
        abnormalResultMq.setSecondLevelId(String.valueOf(weightVolumeCollectDto.getBillingDeptCode()));
        abnormalResultMq.setSecondLevelName(weightVolumeCollectDto.getBillingDeptName());
        abnormalResultMq.setThreeLevelId(weightVolumeCollectDto.getBillingThreeLevelId());
        abnormalResultMq.setThreeLevelName(weightVolumeCollectDto.getBillingThreeLevelName());
        abnormalResultMq.setWeight(BigDecimal.valueOf(weightVolumeCollectDto.getBillingWeight()));
        abnormalResultMq.setVolume(BigDecimal.valueOf(weightVolumeCollectDto.getBillingVolume()));
        abnormalResultMq.setDutyType(weightVolumeCollectDto.getDutyType());
        abnormalResultMq.setDutyErp(weightVolumeCollectDto.getBillingErp());
        abnormalResultMq.setReviewDutyType(weightVolumeCollectDto.getDutyType());
        abnormalResultMq.setBusinessObjectId(weightVolumeCollectDto.getBusiCode());
        abnormalResultMq.setBusinessObject(weightVolumeCollectDto.getBusiName());

        // 防止上线后无数据，兼容之前逻辑
        compatiblePrevious(abnormalResultMq);

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
        return abnormalResultMq;
    }

    /**
     * 兼容之前老逻辑，待上线后无问题则删除此处代码
     * @param abnormalResultMq
     */
    private void compatiblePrevious(AbnormalResultMq abnormalResultMq) {
        if(abnormalResultMq.getDutyType() == null){
            String waybillCode = WaybillUtil.getWaybillCode(abnormalResultMq.getBillCode());
            BizDutyDTO bizDutyDTO;
            try {
                ResponseDTO<BizDutyDTO> responseDto
                        = businessFinanceManager.queryDutyInfo(waybillCode);
                if(responseDto == null || responseDto.getStatusCode() != 0
                        || responseDto.getData() == null){
                    log.warn("根据运单【{}】查询计费信息为空",waybillCode);
                    return;
                }
                bizDutyDTO = responseDto.getData();
            }catch (Exception e){
                log.error("根据运单【{}】查询计费信息异常",waybillCode);
                return;
            }
            abnormalResultMq.setFirstLevelId(bizDutyDTO.getFirstLevelId());
            abnormalResultMq.setFirstLevelName(bizDutyDTO.getFirstLevelName());
            abnormalResultMq.setSecondLevelId(bizDutyDTO.getSecondLevelId());
            abnormalResultMq.setSecondLevelName(bizDutyDTO.getSecondLevelName());
            abnormalResultMq.setThreeLevelId(bizDutyDTO.getThreeLevelId());
            abnormalResultMq.setThreeLevelName(bizDutyDTO.getThreeLevelName());
            abnormalResultMq.setWeight(bizDutyDTO.getWeight());
            abnormalResultMq.setVolume(bizDutyDTO.getVolume());
            abnormalResultMq.setDutyType(bizDutyDTO.getDutyType());
            abnormalResultMq.setReviewDutyType(bizDutyDTO.getDutyType());
            abnormalResultMq.setDutyErp(bizDutyDTO.getDutyErp());
            abnormalResultMq.setBusinessObjectId(bizDutyDTO.getBusinessObjectId());
            abnormalResultMq.setBusinessObject(bizDutyDTO.getBusinessObject());
        }
    }

    /**
     * 抽检数据处理
     * @param packWeightVO
     * @param spotCheckSourceEnum
     * @param result
     */
    @Override
    public InvokeResult<Boolean> dealSportCheck(PackWeightVO packWeightVO, SpotCheckSourceEnum spotCheckSourceEnum, InvokeResult<Boolean> result) {
        try{
            // 基础参数校验
            if(!paramCheck(packWeightVO, result)){
                return result;
            }
            // 运单属性校验
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
            if(!waybillCheck(waybill, result)){
                return result;
            }
            // 校验是否能操作抽检
            InvokeResult<Boolean> canDealSportCheckResult = canDealSportCheck(packWeightVO);
            if(!canDealSportCheckResult.getData()){
                result.customMessage(canDealSportCheckResult.getCode(), canDealSportCheckResult.getMessage());
                return result;
            }
            // 组装抽检数据
            WeightVolumeCollectDto weightVolumeCollectDto = assemble(packWeightVO, waybill, spotCheckSourceEnum, result);
            // 抽检数据落es
            reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
            // 抽检全程跟踪
            sendWaybillTrace(weightVolumeCollectDto);
            // 缓存抽检记录
            cachePackageOrWaybillCheckRecord(packWeightVO.getCodeStr());
        }catch (Exception e){
            log.error("单号{}的抽检数据处理异常!", packWeightVO.getCodeStr(), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 运单属性校验
     * @param waybill
     * @param result
     * @return
     */
    private boolean waybillCheck(Waybill waybill, InvokeResult<Boolean> result) {
        if(waybill == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"运单不存在!!");
            return false;
        }
        int packNum = waybill.getGoodNumber() == null ? Constants.NUMBER_ZERO : waybill.getGoodNumber();
        if(packNum > Constants.CONSTANT_NUMBER_ONE){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"重量体积抽查只支持一单一件!");
            return false;
        }
        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            // 非纯配外单不计入抽检 & 前台不提示
            result.success();
            return false;
        }
        return true;
    }

    /**
     * 1、针对C抽B的：是否超标不填
     * 2、生鲜：分拣较大值 < 计费结算重量（核对较大值）则不超标
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
        // 计费结算重量或核对较大值
        double checkMore = weightVolumeCollectDto.getBillingCalcWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getBillingCalcWeight();
        if(BusinessUtil.isFresh(waybill.getWaybillSign()) && reviewMore < checkMore){
            weightVolumeCollectDto.setIsExcess(IsExcessEnum.EXCESS_ENUM_NO.getCode());
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
        if(weightVolumeCollectDtoExist != null){
            // 校验是否为第一个抽检的单位
            if (!Objects.equals(weightVolumeCollectDtoExist.getReviewSiteCode(), packWeightVO.getOperatorSiteCode())) {
                result.setData(false);
                result.customMessage(this.NOT_ALLOW_SECOND_SITE_CHECK_CODE, "抽检失败，此单已操作过抽检，请勿重复操作");
                return result;
            }
            // 有抽检记录，校验抽检记录上的发货状态
            if (Objects.equals(weightVolumeCollectDtoExist.getReviewSiteCode(), packWeightVO.getOperatorSiteCode())) {
                boolean waybillSendStatusFlag = this.getWaybillSendStatus(weightVolumeCollectDtoExist.getWaybillCode(), weightVolumeCollectDtoExist);
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
     * 获取包裹发货状态
     * @param waybillCode 运单号
     * @param weightVolumeCollectDto 抽检记录
     * @return boolean 发货-true,未发货-false
     * @author fanggang7
     * @time 2020-08-26 15:08:54 周三
     */
    private boolean getWaybillSendStatus(String waybillCode, WeightVolumeCollectDto weightVolumeCollectDto){
        String key = CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_STATUS.concat(waybillCode);
        try {
            String redisValue = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(redisValue) && Integer.parseInt(redisValue) == Constants.YN_YES){
                return true;
            }
        }catch (Exception e){
            log.error("获取C网抽检下发MQ缓存【{}】异常",key);
        }
        if(Objects.equals(weightVolumeCollectDto.getWaybillStatus(), WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY)){
            return true;
        }
        return false;
    }

    /**
     * 比较方法- 判断是走A标准还是走B标准
     * 根据泡重比类型判断是否超标
     *
     * C网抽检对比逻辑变更
     * 校验标准A
     *      1. 分拣【较大值】小于等于1公斤，不论误差多少均判断为正常
     *      2. 分拣【较大值】1公斤至20公斤（含）， 误差标准正负0.5 kg（含）
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
     * @param volumeRate 泡重比
     * @return
     */
    private StandardDto  checkStandard(WeightVolumeCollectDto weightVolumeCollectDto,Integer volumeRate){
        Double reviewVolume = weightVolumeCollectDto.getReviewVolume();
        Double reviewWeight = weightVolumeCollectDto.getReviewWeight();
        // 核对较大值
        Double billingCalcWeight = weightVolumeCollectDto.getBillingCalcWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getBillingCalcWeight();

        // 三边之和
        BigDecimal sumLWH = BigDecimal.ZERO;
        if (weightVolumeCollectDto.getReviewLWH() != null) {
            for (String v : weightVolumeCollectDto.getReviewLWH().split("\\*")) {
                sumLWH = sumLWH.add(new BigDecimal(v));
            }
        }

        // ----比较分拣抽检实物重量与分拣体积重量比较-----
        Double reviewVolumeWeight =  keeTwoDecimals(reviewVolume/volumeRate);
        //分拣重量与体积重量的较大值
        Double moreBigValue = reviewWeight >= reviewVolumeWeight ? reviewWeight : reviewVolumeWeight;
        Double differenceValue = Math.abs(keeTwoDecimals(moreBigValue - billingCalcWeight));

        weightVolumeCollectDto.setMoreBigWeight(moreBigValue);
        weightVolumeCollectDto.setBillingWeightDifference(differenceValue);

        WeightAndVolumeCheckStandardHandler weightAndVolumeCheckStandardHandler = this.getCheckStandardHandler(reviewWeight,reviewVolumeWeight,sumLWH);
        CheckExcessParam checkExcessParam = new CheckExcessParam();
        checkExcessParam.setSumLWH(sumLWH);
        checkExcessParam.setDifferenceValue(differenceValue);
        checkExcessParam.setMoreBigValue(moreBigValue);
        checkExcessParam.setCheckMoreBigValue(billingCalcWeight);
        checkExcessParam.setReviewWeight(reviewWeight);

        StandardDto  standardDto = weightAndVolumeCheckStandardHandler.checkExcess(checkExcessParam);

        if(standardDto!=null && standardDto.getExcessFlag()){
            String baseMessage = "此次操作的重量:"+weightVolumeCollectDto.getReviewWeight()+"kg,体积重量:"+reviewVolumeWeight+"kg,计费唯一值:"+billingCalcWeight+"kg，经校验误差值"+differenceValue+"kg已超出规定";
            StringBuilder warnMessage = new StringBuilder().append(baseMessage).append("误差标准:"+standardDto.getHitMessage()).append("kg");
            standardDto.setWarnMessage(warnMessage.toString());
        }
        return standardDto;
    }

    /**
     * 判断是否走A标准还是B标准的 实现类
     * @param reviewWeight
     * @param reviewVolumeWeight
     * @param sumLWH
     * @return
     */
    private WeightAndVolumeCheckStandardHandler getCheckStandardHandler(Double reviewWeight,Double reviewVolumeWeight,BigDecimal sumLWH) {
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
     */
    private WeightVolumeCollectDto assemble(PackWeightVO packWeightVO, Waybill waybill, SpotCheckSourceEnum spotCheckSourceEnum, InvokeResult<Boolean> result) {
        // 抽检复核数据
        WeightVolumeCollectDto weightVolumeCollectDto = assembleReviewData(packWeightVO, waybill, spotCheckSourceEnum.name());
        // 抽检核对数据
        assembleContrastData(weightVolumeCollectDto);
        // 超标处理
        excessDeal(weightVolumeCollectDto, result);
        //特殊处理
        specialTreatment(weightVolumeCollectDto, waybill);

        return weightVolumeCollectDto;
    }

    /**
     * 超标逻辑处理
     * @param weightVolumeCollectDto
     */
    private void excessDeal(WeightVolumeCollectDto weightVolumeCollectDto, InvokeResult<Boolean> result) {
        int volumeRate = weightVolumeCollectDto.getVolumeRate();
        //校验是否超标
        StandardDto standardDto = checkStandard(weightVolumeCollectDto,volumeRate);
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
    }

    /**
     * 组装抽检核对数据
     * @param weightVolumeCollectDto
     */
    private void assembleContrastData(WeightVolumeCollectDto weightVolumeCollectDto) {
        try{
            // 调用计费接口获取重量体积信息
            ResponseDTO<BizDutyDTO> responseDto = businessFinanceManager.queryDutyInfo(weightVolumeCollectDto.getWaybillCode());
            if(responseDto != null && Objects.equals(responseDto.getStatusCode(), Constants.NUMBER_ZERO) && responseDto.getData() != null){
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
                weightVolumeCollectDto.setBillingWeight(bizDutyDTO.getWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getWeight().doubleValue());
                weightVolumeCollectDto.setBillingVolume(bizDutyDTO.getVolume() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getVolume().doubleValue());
                // 核对较大值：计费结算重量
                weightVolumeCollectDto.setBillingCalcWeight(bizDutyDTO.getCalcWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getCalcWeight().doubleValue());
            }
        }catch (Exception e){
            log.error("通过运单号{}获取计费信息异常!", weightVolumeCollectDto.getWaybillCode());
        }

        // 计费重量为0则取运单流水
        dealWaybillFlow(weightVolumeCollectDto);

        int volumeRate = weightVolumeCollectDto.getVolumeRate();
        double billingVolume = weightVolumeCollectDto.getBillingVolume() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getBillingVolume();
        weightVolumeCollectDto.setBillingVolumeWeight(getVolumeAndWeight(billingVolume/volumeRate));

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
            log.warn("未获取到{}称重流水!", weightVolumeCollectDto.getWaybillCode());
            return;
        }
        // 责任人ERP
        String billingErp = StringUtils.isEmpty(packFlowDetail.getMeasureUserErp()) ? packFlowDetail.getWeighUserErp() : packFlowDetail.getMeasureUserErp();
        weightVolumeCollectDto.setBillingErp(billingErp);
        weightVolumeCollectDto.setBillingWeight(packFlowDetail.getpWeight());
        double billingVolume;
        if(packFlowDetail.getpLength() == null || packFlowDetail.getpWidth() == null
                || packFlowDetail.getpHigh() == null){
            billingVolume = Constants.DOUBLE_ZERO;
        }else {
            billingVolume = packFlowDetail.getpLength() * packFlowDetail.getpWidth() * packFlowDetail.getpHigh();
        }
        weightVolumeCollectDto.setBillingVolume(billingVolume);
        // 核对较大值：核对重量和核对体积重量的较大值
        int volumeRate = weightVolumeCollectDto.getVolumeRate();
        Double billingCalcWeight = packFlowDetail.getpWeight() == null
                ? billingVolume/volumeRate : packFlowDetail.getpWeight() > billingVolume/volumeRate ? packFlowDetail.getpWeight() : billingVolume/volumeRate;
        weightVolumeCollectDto.setBillingCalcWeight(billingCalcWeight);

        // 处理责任类型
        dealDutyType(billingErp, weightVolumeCollectDto);
    }

    /**
     * 责任类型处理，责任类型不同1、2、3级不同
     *  分拣：1级表示区域，2级表示分拣，3级无
     *  站点：1级表示区域，2级表示片区，3级表示站点
     *  仓：1级表示区域，2级表示配送中心，3级表示仓对应的站点
     * @param dutyErp
     * @return
     */
    private void dealDutyType(String dutyErp, WeightVolumeCollectDto weightVolumeCollectDto) {
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(dutyErp);
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
    private WeightVolumeCollectDto assembleReviewData(PackWeightVO packWeightVO, Waybill waybill, String sourceFrom) {
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
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
        weightVolumeCollectDto.setReviewVolumeWeight(getVolumeAndWeight(reviewVolume/volumeRate));

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

        return weightVolumeCollectDto;
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
        headerMap.put("productTypeCode","产品标识");
        headerMap.put("merchantCode","配送商家编号");
        headerMap.put("busiName","商家名称");
        headerMap.put("isTrustBusi","信任商家");
        headerMap.put("reviewOrgName","复核区域");
        headerMap.put("reviewSiteName","复核分拣");
        headerMap.put("reviewSubType","机构类型");
        headerMap.put("reviewErp","复核人erp");
        headerMap.put("reviewWeight","分拣复重kg");
        headerMap.put("reviewLWH","复核长宽高cm");
        headerMap.put("volumeRate","计泡系数");
        headerMap.put("reviewVolumeWeight","复核体积重量");
        headerMap.put("moreBigWeight","较大重量值");
        headerMap.put("billingOrgName","核对操作区域");
        headerMap.put("billingDeptName","核对操作片区");
        headerMap.put("BillingCompany","核对操作单位");
        headerMap.put("billingErp","核对操作人ERP");
        headerMap.put("billingCalcWeight","计费结算重量kg");
        headerMap.put("billingWeight","核对重量kg");
        headerMap.put("billingVolume","核对体积cm");
        headerMap.put("billingVolumeWeight","核对体积重量");
        headerMap.put("billingWeightDifference","计费结算重量差异");
        headerMap.put("contrastSourceFrom","核对来源");
        headerMap.put("diffStandard","误差标准值");
        headerMap.put("isExcess","是否超标");
        headerMap.put("weightDiff","重量差异");
        headerMap.put("volumeWeightDiff","体积重量差异");
        headerMap.put("volumeWeightIsExcess","体积重量是否超标");
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
            BeanUtils.copyProperties(dto,exportWeightVolumeCollectDto);
            exportWeightVolumeCollectDto.setIsTrustBusi(Objects.equals(dto.getIsTrustBusi(),Constants.CONSTANT_NUMBER_ONE) ? "是" : "否");
            exportWeightVolumeCollectDto.setSpotCheckType(Objects.equals(dto.getSpotCheckType(),Constants.CONSTANT_NUMBER_ONE) ? "B网":"C网");
            exportWeightVolumeCollectDto.setReviewSubType(Objects.equals(dto.getReviewSubType(),Constants.CONSTANT_NUMBER_ONE) ? "分拣中心":"转运中心");
            exportWeightVolumeCollectDto.setIsExcess(Objects.equals(dto.getIsExcess(),Constants.CONSTANT_NUMBER_ONE) ? "超标" : "未超标");
            exportWeightVolumeCollectDto.setVolumeWeightIsExcess(Objects.equals(dto.getVolumeWeightIsExcess(),Constants.CONSTANT_NUMBER_ONE) ? "超标" : "未超标");
            exportWeightVolumeCollectDto.setIsHasPicture(Objects.equals(dto.getIsHasPicture(),Constants.CONSTANT_NUMBER_ONE) ? "有" : "无");
            exportWeightVolumeCollectDto.setIsWaybillSpotCheck(Objects.equals(dto.getIsWaybillSpotCheck(),Constants.CONSTANT_NUMBER_ONE) ? "是" : "否");
            exportWeightVolumeCollectDto.setFromSource(getFromSource(dto.getFromSource()));
            exportWeightVolumeCollectDto.setContrastSourceFrom(Objects.equals(dto.getContrastSourceFrom(),Constants.CONSTANT_NUMBER_ONE) ? "运单" : "计费");
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
     * 更新抽检记录的图片
     * @param packageCode 运单或包裹号
     * @param siteCode 站点
     * @return 更新后的抽检记录，可能为空
     * @author fanggang7
     * @time 2020-08-26 15:17:53 周三
     */
    private WeightVolumeCollectDto getUploadImgRecord(String packageCode, Integer siteCode){
        //获取图片链接
        InvokeResult<String> result = searchExcessPicture(packageCode,siteCode);
        if(result == null || result.getCode() != InvokeResult.RESULT_SUCCESS_CODE
                || StringUtils.isEmpty(result.getData())){
            log.warn("运单【{}】站点【{}】的超标图片为空",packageCode,siteCode);
            return null;
        }

        WeightVolumeCollectDto weightVolumeCollectDto;
        try {
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(IsExcessEnum.EXCESS_ENUM_YES.getCode());
            condition.setIsHasPicture(1);
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

        // 根据节点操作类型，分别做不同处理逻辑
        // 上传图片环节
        if(weightAndVolumeCheckHandleMessage.getOpNode() == WeightAndVolumeCheckHandleMessage.UPLOAD_IMG){
            // 1.1 检查运单状态，如果运单状态为未发货，则仅更新图片URL，不下发
            WeightVolumeCollectDto weightVolumeCollectDto = this.getUploadImgRecord(weightAndVolumeCheckHandleMessage.getWaybillCode(), weightAndVolumeCheckHandleMessage.getSiteCode());
            // 1.2 运单状态为已发货，则下发到FXM
            if(weightVolumeCollectDto != null){
                boolean packageSendStatus = this.getWaybillSendStatus(weightAndVolumeCheckHandleMessage, weightVolumeCollectDto);
                if(packageSendStatus){
                    this.sendMqToFxm(weightVolumeCollectDto);
                }
                return result;
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
        // 1.1 查询最新一条抽检记录是否有图片，有则下发到FXM
        WeightVolumeQueryCondition query = new WeightVolumeQueryCondition();
        query.setReviewSiteCode(weightAndVolumeCheckHandleMessage.getSiteCode());
        String waybillCode = WaybillUtil.getWaybillCode(weightAndVolumeCheckHandleMessage.getWaybillCode());
        query.setWaybillCode(waybillCode);
        Pager<WeightVolumeQueryCondition> pager = new Pager<>();
        pager.setSearchVo(query);
        pager.setPageNo(1);
        pager.setPageSize(1);
        BaseEntity<Pager<WeightVolumeCollectDto>> weightVolumeExistResult = reportExternalService.getPagerByConditionForWeightVolume(pager);
        if(weightVolumeExistResult.getCode() != BaseEntity.CODE_SUCCESS){
            log.warn("queryLatestHasUploadPictureCheckRecord getPagerByConditionForWeightVolume warn {}根据查询条件查询es失败,失败原因:{}", JsonHelper.toJson(query), weightVolumeExistResult.getMessage());
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
        List<WeightVolumeCollectDto> existCurrentSiteList = new ArrayList<>();  // 有抽检记录的数据
        for (WeightVolumeCollectDto weightVolumeCollectDto : existList) {
            if(Objects.equals(weightAndVolumeCheckHandleMessage.getSiteCode(), weightVolumeCollectDto.getReviewSiteCode())){
                existCurrentSiteList.add(weightVolumeCollectDto);
                weightVolumeCollectDto.setWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
                // 更新发货状态
                reportExternalService.updateForWeightVolume(weightVolumeCollectDto);
            }
        }
        List<WeightVolumeCollectDto> existCurrentSiteHasPictureList = this.getHasPictureCheckRecordList(existCurrentSiteList);
        // 无上传图片的抽检记录，此时还不能下发给下游，直接返回
        if(CollectionUtils.isEmpty(existCurrentSiteHasPictureList)){
            return result;
        }
        // 下发
        this.sendMqToFxm(existCurrentSiteHasPictureList.get(0));
        return result;
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
            if(weightVolumeCollectDto.getIsHasPicture() == 1){
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
     * 获取包裹发货状态
     * @param weightAndVolumeCheckHandleMessage 消息
     * @param weightVolumeCollectDto 抽检记录
     * @return boolean 发货-true,未发货-false
     * @author fanggang7
     * @time 2020-08-26 15:08:54 周三
     */
    private boolean getWaybillSendStatus(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage, WeightVolumeCollectDto weightVolumeCollectDto){
        String key = CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_STATUS.concat(weightAndVolumeCheckHandleMessage.getWaybillCode());
        try {
            String redisValue = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(redisValue) && Integer.parseInt(redisValue) == Constants.YN_YES){
                return true;
            }
        }catch (Exception e){
            log.error("获取C网抽检下发MQ缓存【{}】异常",key);
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
            return;
        }
        log.info("发送MQ【{}】,业务ID【{}】 ",dmsWeightVolumeExcess.getTopic(),abnormalResultMq.getAbnormalId());
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
}
