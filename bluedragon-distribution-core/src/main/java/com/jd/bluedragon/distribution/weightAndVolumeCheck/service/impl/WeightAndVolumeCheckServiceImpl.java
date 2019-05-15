package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jcloud.jss.JingdongStorageService;
import com.jcloud.jss.client.Request;
import com.jcloud.jss.domain.ObjectListing;
import com.jcloud.jss.domain.ObjectSummary;
import com.jcloud.jss.http.JssInputStreamEntity;
import com.jcloud.jss.service.BucketService;
import com.jcloud.jss.service.ObjectService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BusinessFinanceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SystemEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

    private final Logger logger = Logger.getLogger(this.getClass());

    /** 对象存储 **/
    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";

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
            logger.info(MessageFormat.format("上传文件成功imageName:{0},imageSize:{1}", imageName, imageSize));
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException ioe){
                logger.error("关闭输入流再异常：",ioe);
            }
        }
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
            //获取最近的对应的图片并返回
            String excessPictureUrl = getRecentUrl(urls,siteCode);
            if("".equals(excessPictureUrl)){
                result.parameterError("图片未上传!"+prefixName);
                return result;
            }
            result.setData(excessPictureUrl);
        }catch (Exception e){
            logger.error(prefixName+"|"+siteCode + "获取图片链接失败!" + e);
            result.parameterError("查看图片失败!"+prefixName);
        }
        return result;
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
            logger.error("获取图片路径异常!");
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
     * @param executeTime
     */
    public void sendMqAndUpdate(String packageCode, Integer siteCode, Long uploadTime,String reviewDate,Integer executeTime){
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
                logger.warn(result.getMessage() + abnormalPictureMq.getWaybillCode()+"|"+siteCode);
                return;
            }
            //更新es数据
            WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
            dto.setPackageCode(packageCode);
            dto.setReviewSiteCode(siteCode);
            dto.setPictureAddress(pictureAddress);
            dto.setExecuteTimes((executeTime==null?0:executeTime)+1);
            dto.setIsHasPicture(1);
            reportExternalService.updateForWeightVolume(dto);
            if(!StringHelper.isEmpty(pictureAddress)){
                abnormalPictureMq.setExcessPictureAddress(pictureAddress);
                this.logger.info("发送MQ[" + dmsWeightVolumeAbnormal.getTopic() + "],业务ID[" + abnormalPictureMq.getWaybillCode() + "],消息主题: " + JsonHelper.toJson(abnormalPictureMq));
                dmsWeightVolumeAbnormal.send(abnormalPictureMq.getAbnormalId(), JsonHelper.toJson(abnormalPictureMq));
            }

        }catch (Exception e){
            logger.error("异常消息发送失败!"+abnormalPictureMq.getWaybillCode() + "失败原因:"+ e.getMessage());
        }
    }

    /**
     * 执行task任务
     * @param task
     * @return
     */
    @Override
    public boolean excuteWeightVolumeExcessTask(Task task) {

        boolean isSuccess = true;
        try {
            logger.info("超标定时任务开始执行.....");
            WeightVolumeCollectDto dto = JsonHelper.fromJson(task.getBody(), WeightVolumeCollectDto.class);

            sendMqAndUpdate(dto.getPackageCode(),dto.getReviewSiteCode(),new Date().getTime(),Long.toString(dto.getReviewDate().getTime()),dto.getExecuteTimes());
        }catch (Exception e){
            isSuccess =false;
            logger.error("处理重量体积超标任务异常!");
        }

        return isSuccess;
    }

    /**
     * 获取任务
     * @param type
     * @param ownSign
     * @return
     */
    @Override
    public List<Task> findLimitedTasks(Integer type, String ownSign,int fetchNum) {
        List<WeightVolumeCollectDto> totalList = new ArrayList<>();

        Pager<WeightVolumeQueryCondition> pager = new Pager<>();
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setIsExcess(1);
        condition.setIsHasPicture(0);
        condition.setExecuteTimes(times);
        pager.setSearchVo(condition);
        pager.setPageNo(1);
        pager.setPageSize(fetchNum);
        BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity = reportExternalService.getPagerByConditionForWeightVolume(pager);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getData() != null){
            List<WeightVolumeCollectDto> list = baseEntity.getData().getData();
            totalList.addAll(list);
        }


        List<Task> tasks = convert(totalList);
        return tasks;

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

            if(billingWeight == 0){
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setData(false);
                result.setMessage("计费重量为0或空，无法进行校验");
                weightVolumeCollectDto.setIsExcess(1);
            }else{
                double diffOfWeight = Math.abs(keeTwoDecimals(reviewWeightStr - billingWeight));
                if((reviewWeightStr <= 5 && diffOfWeight> 0.3) || (reviewWeightStr > 5 && reviewWeightStr <= 20 && diffOfWeight> 0.5)
                        || (reviewWeightStr > 20 && reviewWeightStr <= 50 && diffOfWeight> 1)
                        || (reviewWeightStr > 50 && diffOfWeight > reviewWeightStr * 0.02)){
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setData(false);
                    result.setMessage("此次操作重量为"+reviewWeightStr+"kg,计费重量为"+billingWeight+"kg，"
                            +"经校验误差值"+diffOfWeight+"kg已超出规定"+ (reviewWeightStr <=5 ? "0.3":reviewWeightStr<=20 ? "0.5":reviewWeightStr<=50 ? "1" : reviewWeightStr * 0.02)+"kg！");
                    weightVolumeCollectDto.setIsExcess(1);
                }
            }
            weightVolumeCollectDto.setWeightDiff(new DecimalFormat("#0.00").format(Math.abs(reviewWeightStr - billingWeight)));
            StringBuilder diffStandardOfWeight = new StringBuilder("");
            if(reviewVolume <= 5){
                diffStandardOfWeight.append("重量:0.3");
            }else if(reviewVolume > 5 && reviewVolume <= 20){
                diffStandardOfWeight.append("重量:0.5");
            }else if(reviewVolume > 20 && reviewVolume <= 50){
                diffStandardOfWeight.append("重量:1");
            }else if(reviewVolume > 50){
                diffStandardOfWeight.append("重量:2%");
            }
            if(billingVolume == 0){
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setData(false);
                result.setMessage("计费体积为0或空，无法进行校验");
                weightVolumeCollectDto.setIsExcess(1);
            }else{
                double diff = Math.abs(keeTwoDecimals(reviewVolume - billingVolume));
                double diffOfVolume = diff==0.00 ? 0.01 : diff;
                if((reviewVolume/8000 <= 5 && diffOfVolume/8000> 0.3)
                        || (reviewVolume/8000 > 5 && reviewVolume/8000 <= 20  && diffOfVolume/8000 > 0.5)
                        || (reviewVolume/8000 > 20 && reviewVolume/8000 <= 50  && diffOfVolume/8000 > 1)
                        || (reviewVolume/8000 > 50 && diffOfVolume/8000 > reviewVolume*0.02/8000)){
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setData(false);
                    String message = "此次操作体积重量（体积除以8000）为"+String.format("%.6f", reviewVolume/8000)+"kg,计费体积重量（体积除以8000）为"+String.format("%.6f", billingVolume/8000)+"kg，"

                            +"经校验误差值"+diffOfVolume/8000+"kg已超出规定"+ (reviewVolume/8000 <=5 ? "0.3":reviewVolume/8000<=20 ? "0.5":reviewVolume/8000<=50 ? "1" : reviewVolume/8000 * 0.02)+"kg！";
                    if(!StringUtils.isBlank(result.getMessage())){
                        message = result.getMessage()+"\r\n"+message;
                    }
                    result.setMessage(message);
                    weightVolumeCollectDto.setIsExcess(1);
                }
            }
            if(reviewVolume/8000 <= 5){
                diffStandardOfWeight.append("体积重量:0.3");
            }else if(reviewVolume/8000 > 5 && reviewVolume/8000 <= 20){
                diffStandardOfWeight.append("体积重量:0.5");
            }else if(reviewVolume/8000 > 20 && reviewVolume/8000 <= 50){
                diffStandardOfWeight.append("体积重量:1");
            }else if(reviewVolume/8000 > 50){
                diffStandardOfWeight.append("体积重量:2%");
            }
            weightVolumeCollectDto.setDiffStandard(diffStandardOfWeight.toString());
            weightVolumeCollectDto.setVolumeWeightDiff(new DecimalFormat("#0.00").format(Math.abs(reviewVolume/8000 - billingVolume/8000)));
            //将重量体积实体存入es中
            reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
        }catch (Exception e){
            logger.error("包裹称重提示警告信息异常"+JsonHelper.toJson(packWeightVO),e);
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
     * 组装称重计费实体
     * @param packWeightVO
     * @param weightVolumeCollectDto
     */
    private void assemble(PackWeightVO packWeightVO, WeightVolumeCollectDto weightVolumeCollectDto) {
        weightVolumeCollectDto.setReviewDate(new Date());
        weightVolumeCollectDto.setReviewLWH(packWeightVO.getLength()+"*"+packWeightVO.getWidth()+"*"+packWeightVO.getHigh());
        weightVolumeCollectDto.setReviewWeight(packWeightVO.getWeight());
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()),
                true, false, false, false);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            weightVolumeCollectDto.setBusiName(baseEntity.getData().getWaybill().getBusiName());
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

            this.logger.info("发送MQ[" + dmsWeightVolumeExcess.getTopic() + "],业务ID[" + abnormalResultMq.getBillCode() + "],消息主题: " + JsonHelper.toJson(abnormalResultMq));
            dmsWeightVolumeExcess.send(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
        }catch (Exception e){
            this.logger.error("发送超标异常mq给fxm失败" + weightVolumeCollectDto.getPackageCode() + "失败原因：" + e);
        }
    }

    private List<Task> convert(List<WeightVolumeCollectDto> totalList) {
        List<Task> list = new ArrayList<>();
        for(WeightVolumeCollectDto dto : totalList){
            String json = JsonHelper.toJson(dto);
            Task task = new Task();
            task.setKeyword1(dto.getWaybillCode()+"_"+dto.getReviewDate().getTime());
            task.setBody(json);
            list.add(task);
        }
        return list;
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
            if(baseEntity != null && baseEntity.getCode() == BaseEntity.CODE_SUCCESS){

                result.setTotal(baseEntity.getData().getTotal().intValue());
                result.setRows(baseEntity.getData().getData());
            }
        }catch (Exception e){
            logger.error("服务异常,根据查询条件查询es失败!"+JsonHelper.toJson(condition));
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
        heads.add("商家名称");
        heads.add("信任商家");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("机构类型");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积cm³");
        heads.add("计费操作区域");
        heads.add("计费操作机构");
        heads.add("计费操作人ERP");
        heads.add("计费重量kg");
        heads.add("计费体积cm³");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("误差标准值");
        heads.add("是否超标");
        heads.add("有无图片");
        heads.add("图片链接");
        resList.add(heads);
        WeightVolumeQueryCondition transform = transform(condition);
        BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(transform);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().size() > 0){
            List<WeightVolumeCollectDto> list = baseEntity.getData();
            //表格信息
            for(WeightVolumeCollectDto weightVolumeCollectDto : list){
                List<Object> body = Lists.newArrayList();
                body.add(weightVolumeCollectDto.getReviewDate() == null ? null : DateHelper.formatDate(weightVolumeCollectDto.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(weightVolumeCollectDto.getWaybillCode());
                body.add(weightVolumeCollectDto.getPackageCode());
                body.add(weightVolumeCollectDto.getBusiName());
                body.add(weightVolumeCollectDto.getIsTrustBusi()==null?"":weightVolumeCollectDto.getIsTrustBusi()==1?"是":"否");
                body.add(weightVolumeCollectDto.getReviewOrgName());
                body.add(weightVolumeCollectDto.getReviewSiteName());
                body.add((weightVolumeCollectDto.getReviewSubType()==null || weightVolumeCollectDto.getReviewSubType()==-1)?"":weightVolumeCollectDto.getReviewSubType()==1?"分拣中心":"转运中心");
                body.add(weightVolumeCollectDto.getReviewErp());
                body.add(weightVolumeCollectDto.getReviewWeight());
                body.add(weightVolumeCollectDto.getReviewLWH());
                body.add(weightVolumeCollectDto.getReviewVolume());
                body.add(weightVolumeCollectDto.getBillingOrgName());
                body.add(weightVolumeCollectDto.getBillingDeptName());
                body.add(weightVolumeCollectDto.getBillingErp());
                body.add(weightVolumeCollectDto.getBillingWeight());
                body.add(weightVolumeCollectDto.getBillingVolume());
                body.add(weightVolumeCollectDto.getWeightDiff());
                body.add(weightVolumeCollectDto.getVolumeWeightDiff());
                body.add(weightVolumeCollectDto.getDiffStandard());
                body.add(weightVolumeCollectDto.getIsExcess()==null?"":weightVolumeCollectDto.getIsExcess()==1?"超标":"未超标");
                body.add(weightVolumeCollectDto.getIsHasPicture()==null?"":weightVolumeCollectDto.getIsHasPicture()==1?"有":"无");
                body.add(StringHelper.isEmpty(weightVolumeCollectDto.getPictureAddress())?"":weightVolumeCollectDto.getPictureAddress());
                resList.add(body);
            }
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
        //获得带有预签名的下载地址timeout == 10000
        URI uri = dmsWebJingdongStorageService.bucket(bucket).object(keyName).generatePresignedUrl(10000);
        return uri;
    }


}
