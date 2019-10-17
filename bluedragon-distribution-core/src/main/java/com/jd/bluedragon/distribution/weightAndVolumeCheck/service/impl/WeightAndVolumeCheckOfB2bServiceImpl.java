package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckOfPackageDetail;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SystemEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bPackage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 15:06
 */
@Service("weightAndVolumeCheckOfB2bService")
public class WeightAndVolumeCheckOfB2bServiceImpl implements WeightAndVolumeCheckOfB2bService {

    private Logger logger = Logger.getLogger(WeightAndVolumeCheckOfB2bServiceImpl.class);

    /**
     * 包裹维度抽检支持的最大包裹数
     * */
    private static final Integer MAX_PACK_NUM = 10;

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("dmsWeightVolumeExcess")
    private DefaultJMQProducer dmsWeightVolumeExcess;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    public InvokeResult<String> dealExcessDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params) {
        InvokeResult<String> result = new InvokeResult<String>();
        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setInputMode(2);
        List<SpotCheckOfPackageDetail> detailList = new ArrayList<>();
        abnormalResultMq.setDetailList(detailList);
        try{
            //查es防止二次提交
            String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(params.get(0).getCreateSiteCode());
            condition.setPackageCode(waybillCode);
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
            if(baseEntity != null && baseEntity.getData() != null
                    && baseEntity.getData().size() != 0){
                result.customMessage(600,"请勿重复提交!");
                return result;
            }
            Double nowWeight = 0.00;
            Double nowVolume = 0.00;
            for(WeightVolumeCheckOfB2bPackage param : params){
                nowWeight = keeTwoDecimals(nowWeight + param.getWeight());
                nowVolume = keeTwoDecimals(nowVolume + param.getLength() * param.getWidth() * param.getHeight());//厘米
                param.setTotalWeight(nowWeight);
                param.setTotalVolume(nowVolume);
                SpotCheckOfPackageDetail spotCheckOfPackageDetail = new SpotCheckOfPackageDetail();
                spotCheckOfPackageDetail.setWaybillCode(waybillCode);
                spotCheckOfPackageDetail.setPackageCode(param.getPackageCode());
                spotCheckOfPackageDetail.setWeight(param.getWeight());
                spotCheckOfPackageDetail.setLength(param.getLength());
                spotCheckOfPackageDetail.setWidth(param.getWidth());
                spotCheckOfPackageDetail.setHeight(param.getHeight());
                com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                        = searchExcessPicture(param.getPackageCode(), param.getCreateSiteCode());
                spotCheckOfPackageDetail.setImgList(invokeResult==null?null:invokeResult.getData());
                detailList.add(spotCheckOfPackageDetail);
            }
            //发运单维度匿名全程跟踪
            sendWaybillTrace(nowWeight,nowVolume,waybillCode,params.get(0).getCreateSiteCode());
            //数据落入es
            WeightVolumeCollectDto dto = createWeightVolumeCollectDtoOfPackage(1,params);
            reportExternalService.insertOrUpdateForWeightVolume(dto);
            //超标则给FXM发mq
            if(params.get(0).getIsExcess()==1){
                sendMqToFXM(dto,abnormalResultMq);
            }
        }catch (Exception e){
            logger.error("包裹维度B网抽检失败!",e);
            result.customMessage(600,"包裹维度B网抽检失败!");
        }

        return result;
    }

    @Override
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> searchExcessPicture(String packageCode, Integer siteCode) {

        return weightAndVolumeCheckService.searchExcessPictureOfB2b(packageCode,siteCode);
    }

    @Override
    public com.jd.bluedragon.distribution.base.domain.InvokeResult uploadExcessPicture(MultipartFile image, HttpServletRequest request) {

        com.jd.bluedragon.distribution.base.domain.InvokeResult result = new com.jd.bluedragon.distribution.base.domain.InvokeResult();
        String waybillOrPackageCode = request.getParameter("waybillOrPackageCode");
        Integer siteCode = Integer.valueOf(request.getParameter("createSiteCode"));
        Integer type = Integer.valueOf(request.getParameter("type"));
        long imageSize = image.getSize();
        String imageName = image.getOriginalFilename();
        String[] strArray = imageName.split("\\.");
        String suffixName = strArray[strArray.length - 1];
        try {
            String[] defualtSuffixName = new String[] {"jpg","jpeg","gif","png","bmp"};
            if(!Arrays.asList(defualtSuffixName).contains(suffixName)){
                result.customMessage(600,"文件格式不正确!"+suffixName);
                logger.error("文件格式不正确!"+suffixName);
                return result;
            }
            if(imageSize > SINGLE_IMAGE_SIZE_LIMIT){
                result.customMessage(600, MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, SINGLE_IMAGE_SIZE_LIMIT));
                logger.error("上传"+waybillOrPackageCode+"的超标图片失败,单个图片超出限制大小");
                return result;
            }
            String operateTimeForm = DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
            imageName = waybillOrPackageCode + "_" + siteCode + "_" + type + "_" + operateTimeForm + "." + suffixName;
            //上传到jss
            weightAndVolumeCheckService.uploadExcessPicture(imageName,imageSize,image.getInputStream());
        }catch (Exception e){
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.customMessage(600,formatMsg);
            logger.error(formatMsg,e);
        }

        return result;
    }

    private void sendWaybillTrace(Double nowWeight,Double nowVolume,String waybillCode,Integer createSiteCode) {

        Task tTask = new Task();
        tTask.setKeyword1(waybillCode);
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK));
        tTask.setCreateSiteCode(createSiteCode);
        tTask.setCreateTime(new Date());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK);
        status.setWaybillCode(waybillCode);
        status.setPackageCode(waybillCode);
        status.setOperateTime(new Date());
        status.setRemark("重量体积抽检：重量"+nowWeight+"公斤，体积"+nowVolume+"立方厘米");
        status.setCreateSiteCode(createSiteCode);
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);

    }

    /**
     * 运单维度组装数据称重数据
     * @param type 0:只返回重量体积
     *             1:返回所有数据
     * @param param
     * @return
     */
    @Cache(key = "WeightAndVolumeCheckOfB2bServiceImpl.getWeightAndVolume@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    private WeightVolumeCollectDto createWeightVolumeCollectDtoOfWaybill(int type,WeightVolumeCheckConditionB2b param) {
        WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
        if(StringUtils.isEmpty(param.getWaybillOrPackageCode())){
            return dto;
        }
        //查询运单称重流水  TODO
//        dto.setBillingWeight();
//        dto.setBillingVolume();
        if(type == 0){
            return dto;
        }
//        dto.setIsB2b(1);//B网运单
        dto.setReviewDate(new Date());
        String waybillCode = WaybillUtil.getWaybillCode(param.getWaybillOrPackageCode());
        dto.setWaybillCode(waybillCode);
        dto.setPackageCode(waybillCode);
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,
                true, false, false, false);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            Waybill waybill = baseEntity.getData().getWaybill();
            dto.setBusiCode(waybill.getBusiId());
            dto.setBusiName(baseEntity.getData().getWaybill().getBusiName());
            if(BusinessUtil.isSignChar(baseEntity.getData().getWaybill().getWaybillSign(),56,'1')){
                //信任商家
                dto.setIsTrustBusi(1);
            }else if(BusinessUtil.isSignInChars(baseEntity.getData().getWaybill().getWaybillSign(),56,'0','2','3')) {
                //普通商家
                dto.setIsTrustBusi(0);
            }
        }
        BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseStaffByErpNoCache(param.getLoginErp());
        if(baseSiteByDmsCode != null){
            dto.setReviewOrgCode(baseSiteByDmsCode.getOrgId());
            dto.setReviewOrgName(baseSiteByDmsCode.getOrgName());
            dto.setReviewSiteCode(baseSiteByDmsCode.getSiteCode());
            dto.setReviewSiteName(baseSiteByDmsCode.getSiteName());
            dto.setReviewSubType(baseSiteByDmsCode.getSubType());
            dto.setReviewErp(param.getLoginErp());
        }
        dto.setReviewWeight(param.getWaybillWeight());
        dto.setReviewVolume(param.getWaybillVolume());

//        private Integer billingOrgCode;
//        dto.setBillingOrgCode();
//        private String billingOrgName;
//        dto.setBillingOrgName();
//        private Integer billingDeptCode;
//        dto.setBillingDeptCode();
//        private String billingDeptName;
//        dto.setBillingDeptName();
//        private String billingErp;
//        dto.setBillingErp();
//        private Double billingWeight;
//        dto.setBillingWeight();
//        private Double billingVolume;
//        dto.setBillingVolume();
//        private Integer isExcess;
//        dto.setIsExcess();
//        private Integer isHasPicture;
//        dto.setIsHasPicture();
//
//        private String weightDiff;
//        dto.setWeightDiff();
//        private String volumeWeightDiff;
//        dto.setVolumeWeightDiff();
//        private String diffStandard;
//        dto.setDiffStandard();


        //查询图片地址
        com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                = searchExcessPicture(param.getWaybillOrPackageCode(), param.getCreateSiteCode());
        StringBuilder excessPictureUrls = new StringBuilder();
        for(String url : invokeResult.getData()){
            excessPictureUrls.append(url).append(";");
        }
        dto.setPictureAddress(excessPictureUrls.toString());


//        private Double reviewVolumeWeight;
//        dto.setReviewVolumeWeight();
//        private Double billingVolumeWeight;
//        dto.setBillingVolumeWeight();
//        private Integer volumeWeightIsExcess;
//        dto.setVolumeWeightIsExcess();
//        private String billingCompany;
//        dto.setBillingCompany();

        return dto;
    }

    /**
     * 包裹维度组装数据称重数据
     * @param type 0:只返回重量体积
     *             1:返回所有数据
     * @param params
     * @return
     */
    private WeightVolumeCollectDto createWeightVolumeCollectDtoOfPackage(int type,List<WeightVolumeCheckOfB2bPackage> params) {
        WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
        //查询运单称重流水  TODO
//        dto.setBillingWeight();
//        dto.setBillingVolume();
        if(type == 0){
            return dto;
        }
//        dto.setIsB2b(1);//B网运单
        dto.setReviewDate(new Date());
        String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
        dto.setWaybillCode(waybillCode);
        dto.setPackageCode(waybillCode);
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,
                true, false, false, false);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            Waybill waybill = baseEntity.getData().getWaybill();
            dto.setBusiCode(waybill.getBusiId());
            dto.setBusiName(baseEntity.getData().getWaybill().getBusiName());
            if(BusinessUtil.isSignChar(baseEntity.getData().getWaybill().getWaybillSign(),56,'1')){
                //信任商家
                dto.setIsTrustBusi(1);
            }else if(BusinessUtil.isSignInChars(baseEntity.getData().getWaybill().getWaybillSign(),56,'0','2','3')) {
                //普通商家
                dto.setIsTrustBusi(0);
            }
        }
        BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseStaffByErpNoCache(params.get(0).getLoginErp());
        if(baseSiteByDmsCode != null){
            dto.setReviewOrgCode(baseSiteByDmsCode.getOrgId());
            dto.setReviewOrgName(baseSiteByDmsCode.getOrgName());
            dto.setReviewSiteCode(baseSiteByDmsCode.getSiteCode());
            dto.setReviewSiteName(baseSiteByDmsCode.getSiteName());
            dto.setReviewSubType(baseSiteByDmsCode.getSubType());
            dto.setReviewErp(params.get(0).getLoginErp());
        }
        dto.setReviewWeight(params.get(0).getTotalWeight());
        dto.setReviewVolume(params.get(0).getTotalVolume());

//        private Integer billingOrgCode;
//        dto.setBillingOrgCode();
//        private String billingOrgName;
//        dto.setBillingOrgName();
//        private Integer billingDeptCode;
//        dto.setBillingDeptCode();
//        private String billingDeptName;
//        dto.setBillingDeptName();
//        private String billingErp;
//        dto.setBillingErp();
//        private Double billingWeight;
//        dto.setBillingWeight();
//        private Double billingVolume;
//        dto.setBillingVolume();
//        private Integer isExcess;
//        dto.setIsExcess();
//        private Integer isHasPicture;
//        dto.setIsHasPicture();
//
//        private String weightDiff;
//        dto.setWeightDiff();
//        private String volumeWeightDiff;
//        dto.setVolumeWeightDiff();
//        private String diffStandard;
//        dto.setDiffStandard();


        //查询图片地址
        StringBuilder excessPictureUrls = new StringBuilder();
        for(WeightVolumeCheckOfB2bPackage param : params){
            com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                    = searchExcessPicture(param.getPackageCode(), params.get(0).getCreateSiteCode());
            for(String url : invokeResult.getData()){
                excessPictureUrls.append(url).append(";");
            }
        }
        dto.setPictureAddress(excessPictureUrls.toString());


//        private Double reviewVolumeWeight;
//        dto.setReviewVolumeWeight();
//        private Double billingVolumeWeight;
//        dto.setBillingVolumeWeight();
//        private Integer volumeWeightIsExcess;
//        dto.setVolumeWeightIsExcess();
//        private String billingCompany;
//        dto.setBillingCompany();

        return dto;
    }

    @Override
    public InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> checkIsExcessOfWaybill(WeightVolumeCheckConditionB2b condition) {

        InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> result = new InvokeResult<>();
        if(condition == null){
            result.parameterError("参数错误!");
            return result;
        }
        //0.校验
        String waybillOrPackageCode = condition.getWaybillOrPackageCode();
        if(!WaybillUtil.isWaybillCode(waybillOrPackageCode)
                && !WaybillUtil.isPackageCode(waybillOrPackageCode)){
            result.parameterError("单号不符合规则!");
            return result;
        }
        String waybillCode = WaybillUtil.getWaybillCode(condition.getWaybillOrPackageCode());
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, true);
        /*if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            if(!BusinessUtil.isB2b(baseEntity.getData().getWaybill().getWaybillSign())){
                result.parameterError("此功能只支持B网运单抽检!");
                return result;
            }
        }else {
            result.parameterError("运单数据为空!");
            return result;
        }*/     //TODO 为方便测试注释掉

        List<WeightVolumeCheckOfB2bWaybill> list = new ArrayList<>();
        result.setData(list);
        WeightVolumeCheckOfB2bWaybill weightVolumeCheckOfB2bWaybill = new WeightVolumeCheckOfB2bWaybill();
        list.add(weightVolumeCheckOfB2bWaybill);
        weightVolumeCheckOfB2bWaybill.setWaybillCode(waybillCode);
        weightVolumeCheckOfB2bWaybill.setWaybillWeight(condition.getWaybillWeight());
        weightVolumeCheckOfB2bWaybill.setWaybillVolume(condition.getWaybillVolume());
        weightVolumeCheckOfB2bWaybill.setUpLoadNum(0);//初始上传图片数量
        weightVolumeCheckOfB2bWaybill.setPackNum(baseEntity.getData().getPackageList().size());

        //判断是否超标
        Double nowWeight = condition.getWaybillWeight();
        Double nowVolume = condition.getWaybillVolume();//TODO 立方米
        WeightVolumeCollectDto dto = createWeightVolumeCollectDtoOfWaybill(0,condition);
        Double beforeWeight = dto.getBillingWeight()==null?0.00:dto.getBillingWeight();
        Double beforeVolume = dto.getBillingVolume()==null?0.00:dto.getBillingVolume();
        Boolean sign = isExcess(nowWeight,beforeWeight,nowVolume,beforeVolume);
        weightVolumeCheckOfB2bWaybill.setIsExcess(sign?1:0);

        return result;
    }

    @Override
    public InvokeResult<Integer> checkIsExcessOfPackage(List<WeightVolumeCheckOfB2bPackage> params) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();

        //计算总体积总重量
        Double nowWeight = 0.00;
        Double nowVolume = 0.00;
        for(WeightVolumeCheckOfB2bPackage param : params){
            nowWeight = keeTwoDecimals(nowWeight + param.getWeight());
            nowVolume = keeTwoDecimals(nowVolume + param.getLength() * param.getWidth() * param.getHeight());//厘米
        }
        //获取运单中的体积和重量
        WeightVolumeCollectDto dto = createWeightVolumeCollectDtoOfPackage(0,params);
        Double beforeWeight = dto.getBillingWeight()==null?0.00:dto.getBillingWeight();
        Double beforeVolume = dto.getBillingVolume()==null?0.00:dto.getBillingVolume();
        //判断是否超标
        Boolean sign = isExcess(nowWeight,beforeWeight,nowVolume,beforeVolume);
        result.setData(sign?1:0);

        return result;
    }

    private Boolean isExcess(Double nowWeight,Double beforeWeight,Double nowVolume,Double beforeVolume){
        Boolean sign = Boolean.FALSE;
        if((nowWeight < 25 && Math.abs(nowWeight - beforeWeight) > 1)
                || (nowWeight >= 25 && Math.abs(nowWeight - beforeWeight) > 0.04 * nowWeight)){
            sign = Boolean.TRUE;
            return sign;
        }
        if((nowVolume < 100000 && Math.abs(nowVolume - beforeVolume) > 100000)
                || (nowVolume >= 100000 && Math.abs(nowVolume - beforeVolume) > 0.1 * nowVolume)){
            sign = Boolean.TRUE;
        }
        return sign;
    }

    private void sendMqToFXM(WeightVolumeCollectDto dto,AbnormalResultMq abnormalResultMq) {
        try {

            abnormalResultMq.setFrom(SystemEnum.DMS.getCode().toString());
            if(1==1||2==2||3==3){
                //责任为分拣或车队或站点无erp
                abnormalResultMq.setTo(SystemEnum.ZHIKONG.getCode().toString());
            }else if(4==4){
                //责任为站点有erp
                abnormalResultMq.setTo(SystemEnum.TMS.getCode().toString());
            }else if(5==5){
                //责任为信任商家
                abnormalResultMq.setTo(SystemEnum.PANZE.getCode().toString());
            }

            abnormalResultMq.setBillCode(dto.getWaybillCode());
            abnormalResultMq.setBusinessObjectId(dto.getBusiCode());
            abnormalResultMq.setBusinessObject(dto.getBusiName());
//            //责任类型1:仓2:分拣3:站点4:商家5:空6:车队
//            abnormalResultMq.setDutyType();
//
//            /**
//             * 责任一级id
//             */
//            private String firstLevelId;
//            /**
//             * 责任一级名称
//             */
//            private String firstLevelName;
//            /**
//             * 责任二级id
//             */
//            private String secondLevelId;
//
//            /**
//             * 责任二级名称
//             */
//            private String secondLevelName;
//            /**
//             * 责任三级id
//             */
//            private String threeLevelId;
//
//            /**
//             * 责任三级名称
//             */
//            private String threeLevelName;
//            /**
//             * 责任人erp
//             */
//            private String dutyErp;


            abnormalResultMq.setWeight(BigDecimal.valueOf(dto.getBillingWeight()));
            abnormalResultMq.setVolume(BigDecimal.valueOf(dto.getBillingVolume()));
            abnormalResultMq.setAbnormalId(dto.getPackageCode() + "_" +dto.getReviewDate().getTime());
            abnormalResultMq.setReviewDate(dto.getReviewDate());
            abnormalResultMq.setReviewDutyType(2);
            abnormalResultMq.setReviewFirstLevelId(dto.getReviewOrgCode());
            abnormalResultMq.setReviewFirstLevelName(dto.getReviewOrgName());
            abnormalResultMq.setReviewSecondLevelId(dto.getReviewSiteCode());
            abnormalResultMq.setReviewSecondLevelName(dto.getReviewSiteName());
            abnormalResultMq.setReviewErp(dto.getReviewErp());
            abnormalResultMq.setReviewMechanismType(dto.getReviewSubType());
            abnormalResultMq.setReviewWeight(dto.getReviewWeight());
            abnormalResultMq.setReviewVolume(dto.getReviewVolume());
            abnormalResultMq.setWeightDiff(Double.parseDouble(dto.getWeightDiff()));
            abnormalResultMq.setVolumeDiff(Double.parseDouble(dto.getVolumeWeightDiff()));
            abnormalResultMq.setDiffStandard(dto.getDiffStandard());
            abnormalResultMq.setIsExcess(dto.getIsExcess());
            abnormalResultMq.setPictureAddress(dto.getPictureAddress());
            //默认值:不认责判责
            abnormalResultMq.setIsAccusation(1);
            abnormalResultMq.setIsNeedBlame(0);
            abnormalResultMq.setOperateTime(dto.getReviewDate());
            abnormalResultMq.setReviewErp(dto.getReviewErp());
            abnormalResultMq.setBusinessType(2);

            logger.info("发送MQ[" + dmsWeightVolumeExcess.getTopic() + "],业务ID[" + abnormalResultMq.getBillCode() + "],消息主题: " + JsonHelper.toJson(abnormalResultMq));
            dmsWeightVolumeExcess.send(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
        }catch (Exception e){
            logger.error("B网抽检运单号:" + dto.getWaybillCode() + "下发FXM的mq发送失败:" + e.getMessage());
        }
    }

    @Override
    public InvokeResult<String> dealExcessDataOfWaybill(List<WeightVolumeCheckConditionB2b> params) {
        InvokeResult<String> result = new InvokeResult<>();
        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setInputMode(1);
        try{
            for(WeightVolumeCheckConditionB2b param : params){
                //查es防止二次提交
                String waybillCode = WaybillUtil.getWaybillCode(param.getWaybillOrPackageCode());
                WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
                condition.setReviewSiteCode(param.getCreateSiteCode());
                condition.setPackageCode(waybillCode);
                BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
                if(baseEntity != null && baseEntity.getData() != null
                        && baseEntity.getData().size() != 0){
                    result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，请勿重复操作!");
                    return result;
                }
                //发运单维度全程跟踪
                sendWaybillTrace(param.getWaybillWeight(),param.getWaybillVolume()*1000000,waybillCode,param.getCreateSiteCode());
                //组装数据落入es
                WeightVolumeCollectDto dto = createWeightVolumeCollectDtoOfWaybill(1,param);
                reportExternalService.insertOrUpdateForWeightVolume(dto);
                //超标给fxm发mq
                if(param.getIsExcess() == 1){
                    sendMqToFXM(dto,abnormalResultMq);
                }
            }
        }catch (Exception e){
            logger.error("B网按运单抽检失败",e);
            result.customMessage(600,"B网按运单抽检失败!");
        }

        return result;
    }

    @Override
    public InvokeResult<List<WeightVolumeCheckOfB2bPackage>> getPackageNum(String waybillOrPackageCode) {
        InvokeResult<List<WeightVolumeCheckOfB2bPackage>> result = new InvokeResult<>();
        List<WeightVolumeCheckOfB2bPackage> list = new ArrayList<>();
        result.setData(list);
        String waybillCode = waybillOrPackageCode.trim();
        if(StringUtils.isEmpty(waybillCode)){
            return result;
        }
        if(!WaybillUtil.isWaybillCode(waybillCode)
                && !WaybillUtil.isWaybillCode(waybillCode)){
            result.parameterError("单号不符合规则!");
            return result;
        }
        waybillCode = WaybillUtil.getWaybillCode(waybillCode);
        //禁止非B网运单抽检
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, true);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            /*if(!BusinessUtil.isB2b(baseEntity.getData().getWaybill().getWaybillSign())){
                result.parameterError("此功能只支持B网运单抽检!");
                return result;
            }*/     //TODO 为方便测试注释掉
            List<DeliveryPackageD> packList = baseEntity.getData().getPackageList();
            if(packList != null && packList.size() > 0){
                if(packList.size() > MAX_PACK_NUM){
                    result.customMessage(600,"运单包裹数大于10,请使用按运单抽检!");
                    return result;
                }
                for(DeliveryPackageD deliveryPackageD : packList){
                    WeightVolumeCheckOfB2bPackage weightVolumeCheckOfB2bPackage = new WeightVolumeCheckOfB2bPackage();
                    weightVolumeCheckOfB2bPackage.setPackageCode(deliveryPackageD.getPackageBarcode());
                    list.add(weightVolumeCheckOfB2bPackage);
                }
            }else {
                result.parameterError("运单包裹数据为空!");
            }
        }else {
            result.parameterError("运单数据为空!");
            return result;
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
     * 立方厘米转换成立方米
     * @param param
     */
    private Double convert2m(Double param) {
        if(param == null){
            return 0.00;
        }
        if(param <= 1000000){
            return param%1000000;
        }else {
            return param/1000000 + param%1000000;
        }
    }
}
