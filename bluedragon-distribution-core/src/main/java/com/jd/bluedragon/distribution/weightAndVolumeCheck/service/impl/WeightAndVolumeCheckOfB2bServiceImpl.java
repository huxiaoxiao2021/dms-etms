package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.DutyTypeEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckData;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckOfPackageDetail;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SystemEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WaybillFlowDetail;
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
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * 包裹维度抽检支持的最大包裹数
     * */
    private static final Integer MAX_PACK_NUM = 10;

    /**
     * 单张图片最大限制
     * */
    private static final int SINGLE_IMAGE_SIZE_LIMIT = 1024000;

    /**
     * 立方米转换立方厘米
     * */
    private static final int M3_TRANS_TO_CM3 = 1000000;
    /**
     * 重量阈值
     * */
    private static final double WEIGHT_THRESHOLD = 25;
    /**
     * 重量误差标准值
     * */
    private static final double WEIGHT_STANDARD_UP = 1;
    /**
     * 重量误差标准值
     * */
    private static final double WEIGHT_STANDARD_LOW = 0.04;
    /**
     * 体积阈值
     * */
    private static final double VOLUME_THRESHOLD = 100000;
    /**
     * 体积误差标准值
     * */
    private static final double VOLUME_STANDARD_LOW = 0.1;
    /**
     * 重泡比
     * */
    private static final int VOLUME_RATIO = 8000;
    /**
     * 重量维度
     * */
    private static final int IS_WEIGHT_TYPE = 1;
    /**
     * 体积维度
     * */
    private static final int IS_VOLUME_TYPE = 0;

    /**
     * 重泡比标准值
     * */
    private static final int WEIGHT_VOLUME_RATIO = 7800;
    /**
     * 重量限额，5000KG
     * */
    private static final int WEIGHT_MAX_RATIO = 5000;
    /**
     * 体积限额,5m³
     * */
    private static final int VOLUME_MAX_RATIO = 5;

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

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;


    @Override
    public InvokeResult<String> dealExcessDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params) {
        InvokeResult<String> result = new InvokeResult<String>();
        try{
            //查es防止二次提交
            String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
            if(isSpotCheck(waybillCode,params.get(0).getCreateSiteCode())){
                result.customMessage(600,"请勿重复提交!");
                return result;
            }
            //组装数据
            SpotCheckData spotCheckData = trans2SpotCheckDataOfPackage(params);
            WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
            AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
            assembleData(spotCheckData,dto,abnormalResultMq);
            weightAndVolumeCheckService.setProductType(dto);
            reportExternalService.insertOrUpdateForWeightVolume(dto);
            //发运单维度匿名全程跟踪
            sendWaybillTrace(dto);
            //超标则给FXM发mq
            if(params.get(0).getIsExcess()==1){
                sendMqToFXM(dto,abnormalResultMq);
            }
        }catch (Exception e){
            log.error("包裹维度B网抽检失败, 异常信息:{}" , e.getMessage(), e);
            result.customMessage(600,"包裹维度B网抽检失败!");
        }

        return result;
    }

    /**
     * 转成公共实体
     * @param params
     * @return
     */
    private SpotCheckData trans2SpotCheckDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params) {
        SpotCheckData spotCheckData = new SpotCheckData();
        spotCheckData.setWaybillCode(WaybillUtil.getWaybillCode(params.get(0).getPackageCode()));
        Double totalWeight = 0.00;
        Double totalVolume = 0.00;
        for(WeightVolumeCheckOfB2bPackage param : params){
            totalWeight = keeTwoDecimals(totalWeight + param.getWeight());
            totalVolume = keeTwoDecimals(totalVolume + param.getLength() * param.getWidth() * param.getHeight());
        }
        spotCheckData.setTotalWeight(totalWeight);
        spotCheckData.setTotalVolume(totalVolume);
        spotCheckData.setCreateSiteCode(params.get(0).getCreateSiteCode());
        spotCheckData.setLoginErp(params.get(0).getLoginErp());
        spotCheckData.setIsExcess(params.get(0).getIsExcess());
        spotCheckData.setIsWaybillSpotCheck(2);
        List<SpotCheckData.packageData> packageDataList = new ArrayList<>();
        spotCheckData.setPackageDataList(packageDataList);
        for(WeightVolumeCheckOfB2bPackage WeightVolumeCheckOfB2bPackage : params){
            SpotCheckData.packageData packageData = new SpotCheckData.packageData();
            packageData.setPackageCode(WeightVolumeCheckOfB2bPackage.getPackageCode());
            packageData.setWeight(WeightVolumeCheckOfB2bPackage.getWeight());
            packageData.setLength(WeightVolumeCheckOfB2bPackage.getLength());
            packageData.setWidth(WeightVolumeCheckOfB2bPackage.getWidth());
            packageData.setHeight(WeightVolumeCheckOfB2bPackage.getHeight());
            packageDataList.add(packageData);
        }
        return spotCheckData;
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
                log.warn("参数:{}, 异常信息:{}", suffixName,"文件格式不正确!");
                return result;
            }
            if(imageSize > SINGLE_IMAGE_SIZE_LIMIT){
                result.customMessage(600, MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, SINGLE_IMAGE_SIZE_LIMIT));
                log.warn("参数:{}, 异常信息:{}", waybillOrPackageCode, "上传的超标图片失败,单个图片超出限制大小");
                return result;
            }
            //是否操作过抽检
            String waybillCode = WaybillUtil.getWaybillCode(waybillOrPackageCode);
            if(isSpotCheck(waybillCode,null)){
                result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，禁止上传!");
                return result;
            }
            String operateTimeForm = DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
            imageName = waybillOrPackageCode + "_" + siteCode + "_" + type + "_" + operateTimeForm + "." + suffixName;
            //上传到jss
            weightAndVolumeCheckService.uploadExcessPicture(imageName,imageSize,image.getInputStream());
        }catch (Exception e){
            String formatMsg = MessageFormat.format("图片上传失败!该文件名称{0}",imageName );
            result.customMessage(600,formatMsg);
            log.error("参数:{}, 异常信息:{}", imageName , e.getMessage(), e);
        }

        return result;
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
        status.setPackageCode(dto.getWaybillCode());
        status.setOperateTime(dto.getReviewDate());
        status.setOperator(dto.getReviewErp());
        status.setRemark("重量体积抽检：重量"+dto.getReviewWeight()+"公斤，体积"+dto.getReviewVolume()+"立方厘米");
        status.setCreateSiteCode(dto.getReviewSiteCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);

    }

    /**
     * 组装数据
     * @param spotCheckData
     * @param dto
     * @param abnormalResultMq
     * @return
     */
    private void assembleData(SpotCheckData spotCheckData, WeightVolumeCollectDto dto, AbnormalResultMq abnormalResultMq) {

        abnormalResultMq.setInputMode(spotCheckData.getIsWaybillSpotCheck());
        //查询运单称重流水
        WaybillFlowDetail waybillFlowDetail = getFirstWeightAndVolumeDetail(spotCheckData.getWaybillCode());
        dto.setBillingWeight(waybillFlowDetail.getTotalWeight()==null?0.00:waybillFlowDetail.getTotalWeight());
        dto.setBillingVolume(waybillFlowDetail.getTotalVolume()==null?0.00:waybillFlowDetail.getTotalVolume());
        dto.setBillingErp(waybillFlowDetail.getOperateErp());
        dto.setSpotCheckType(1);
        dto.setIsWaybillSpotCheck(spotCheckData.getIsWaybillSpotCheck());
        dto.setIsExcess(spotCheckData.getIsExcess());
        dto.setIsHasPicture(spotCheckData.getIsExcess());
        List<SpotCheckOfPackageDetail> detailList = new ArrayList<>();
        abnormalResultMq.setDetailList(detailList);
        StringBuilder excessPictureUrl = new StringBuilder();

        if(spotCheckData.getIsWaybillSpotCheck() == 1){
            //运单维度
            if(spotCheckData.getIsExcess()==1){
                SpotCheckOfPackageDetail detail = new SpotCheckOfPackageDetail();
                detail.setBillCode(spotCheckData.getWaybillCode());
                detail.setWeight(spotCheckData.getTotalWeight());
                detail.setLength(spotCheckData.getTotalVolume()*M3_TRANS_TO_CM3);
                List<Map<String,String>> imgList = new ArrayList<>();
                detail.setImgList(imgList);
                detailList.add(detail);
                getExcessPictureAddress(excessPictureUrl,imgList,spotCheckData.getWaybillCode(),spotCheckData.getCreateSiteCode());
            }
            dto.setReviewVolume(spotCheckData.getTotalVolume()*M3_TRANS_TO_CM3);
        }else{
            //包裹维度
            if(spotCheckData.getIsExcess()==1) {
                for (SpotCheckData.packageData packageData : spotCheckData.getPackageDataList()) {
                    SpotCheckOfPackageDetail spotCheckOfPackageDetail = new SpotCheckOfPackageDetail();
                    spotCheckOfPackageDetail.setBillCode(packageData.getPackageCode());
                    spotCheckOfPackageDetail.setWeight(packageData.getWeight());
                    spotCheckOfPackageDetail.setLength(packageData.getLength());
                    spotCheckOfPackageDetail.setWidth(packageData.getWidth());
                    spotCheckOfPackageDetail.setHeight(packageData.getHeight());
                    List<Map<String, String>> imgList = new ArrayList<>();
                    spotCheckOfPackageDetail.setImgList(imgList);
                    detailList.add(spotCheckOfPackageDetail);
                    getExcessPictureAddress(excessPictureUrl,imgList,packageData.getPackageCode(),spotCheckData.getCreateSiteCode());
                }
            }
            dto.setReviewVolume(spotCheckData.getTotalVolume());
        }
        dto.setPictureAddress(StringHelper.getStringValue(excessPictureUrl));
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getWaybillAndPackByWaybillCode(spotCheckData.getWaybillCode());
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            Waybill waybill = baseEntity.getData().getWaybill();
            dto.setBusiCode(waybill.getBusiId());
            dto.setBusiName(baseEntity.getData().getWaybill().getBusiName());
            if(BusinessUtil.isTrustBusi(baseEntity.getData().getWaybill().getWaybillSign())){
                //信任商家
                dto.setIsTrustBusi(1);
                abnormalResultMq.setIsTrustMerchant(1);
            }else {
                //普通商家
                dto.setIsTrustBusi(0);
                abnormalResultMq.setIsTrustMerchant(0);
            }
        }
        String reviewErp = spotCheckData.getLoginErp();
        if(!StringUtils.isEmpty(reviewErp)){
            BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseStaffByErpNoCache(reviewErp);
            if(baseSiteByDmsCode != null){
                dto.setReviewOrgCode(baseSiteByDmsCode.getOrgId());
                dto.setReviewOrgName(baseSiteByDmsCode.getOrgName());
                dto.setReviewSiteCode(baseSiteByDmsCode.getSiteCode());
                dto.setReviewSiteName(baseSiteByDmsCode.getSiteName());
                dto.setReviewSubType(baseSiteByDmsCode.getSubType());
                dto.setReviewErp(spotCheckData.getLoginErp());
            }
        }
        dto.setReviewDate(new Date());
        dto.setWaybillCode(spotCheckData.getWaybillCode());
        dto.setPackageCode(spotCheckData.getWaybillCode());
        dto.setReviewWeight(spotCheckData.getTotalWeight());
        dto.setReviewVolumeWeight(keeTwoDecimals(dto.getReviewVolume()/VOLUME_RATIO));
        dto.setBillingVolumeWeight(keeTwoDecimals(dto.getBillingVolume()/VOLUME_RATIO));
        dto.setWeightDiff(new DecimalFormat("#0.00").format(Math.abs(dto.getReviewWeight()-dto.getBillingWeight())));
        dto.setVolumeWeightDiff(new DecimalFormat("#0.00").format(Math.abs(dto.getReviewVolumeWeight()-dto.getBillingVolumeWeight())));

        Integer siteId = waybillFlowDetail.getOperateSiteCode();
        if(waybillFlowDetail.getTrustBusi()){
            //责任类型为信任商家
            abnormalResultMq.setDutyType(DutyTypeEnum.BIZ.getCode());
            abnormalResultMq.setThreeLevelId(StringHelper.getStringValue(dto.getBusiCode()));
            abnormalResultMq.setThreeLevelName(dto.getBusiName());
            return;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteId);
        if(baseStaffSiteOrgDto != null){
            abnormalResultMq.setFirstLevelId(StringHelper.getStringValue(baseStaffSiteOrgDto.getOrgId()));
            abnormalResultMq.setFirstLevelName(baseStaffSiteOrgDto.getOrgName());
            abnormalResultMq.setSecondLevelId(StringHelper.getStringValue(baseStaffSiteOrgDto.getSiteCode()));
            abnormalResultMq.setSecondLevelName(baseStaffSiteOrgDto.getSiteName());
            if(BusinessUtil.isDistrubutionCenter(baseStaffSiteOrgDto.getSiteType())){
                //责任类型分拣
                abnormalResultMq.setDutyType(DutyTypeEnum.DMS.getCode());
            }else if(BusinessUtil.isSite(baseStaffSiteOrgDto.getSiteType())){
                //责任类型为站点
                abnormalResultMq.setDutyType(DutyTypeEnum.SITE.getCode());
                abnormalResultMq.setSecondLevelId(baseStaffSiteOrgDto.getAreaCode());
                abnormalResultMq.setSecondLevelName(baseStaffSiteOrgDto.getAreaName());
                abnormalResultMq.setThreeLevelId(StringHelper.getStringValue(baseStaffSiteOrgDto.getSiteCode()));
                abnormalResultMq.setThreeLevelName(baseStaffSiteOrgDto.getSiteName());
            }else if(BusinessUtil.isFleet(baseStaffSiteOrgDto.getSiteType())){
                //责任类型为车队
                abnormalResultMq.setDutyType(DutyTypeEnum.FLEET.getCode());
                abnormalResultMq.setSecondLevelId(baseStaffSiteOrgDto.getAreaCode());
                abnormalResultMq.setSecondLevelName(baseStaffSiteOrgDto.getAreaName());
                abnormalResultMq.setThreeLevelId(StringHelper.getStringValue(baseStaffSiteOrgDto.getSiteCode()));
                abnormalResultMq.setThreeLevelName(baseStaffSiteOrgDto.getSiteName());
                abnormalResultMq.setCarCaptionErp(waybillFlowDetail.getOperateErp());
            }else {
                abnormalResultMq.setDutyType(DutyTypeEnum.OTHER.getCode());
            }
            dto.setBillingOrgCode(baseStaffSiteOrgDto.getOrgId());
            dto.setBillingOrgName(baseStaffSiteOrgDto.getOrgName());
            dto.setBillingDeptCode(baseStaffSiteOrgDto.getSiteCode());
            dto.setBillingDeptName(baseStaffSiteOrgDto.getSiteName());
            dto.setBillingCompany(baseStaffSiteOrgDto.getSiteName());
        }
    }

    private void getExcessPictureAddress(StringBuilder excessPictureUrl,List<Map<String, String>> imgList,
                                         String waybillCode,Integer siteCode){
        com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                = searchExcessPicture(waybillCode, siteCode);
        if (invokeResult != null && !CollectionUtils.isEmpty(invokeResult.getData())) {
            for (String url : invokeResult.getData()) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("url", url);
                imgList.add(map);
                excessPictureUrl.append(url).append(";");
            }
        }
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
                = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            if(!BusinessUtil.isB2b(baseEntity.getData().getWaybill().getWaybillSign())){
                result.parameterError("此功能只支持B网运单抽检!");
                return result;
            }
        }else {
            result.parameterError("运单数据为空!");
            return result;
        }
        //是否妥投
        boolean finished = waybillTraceManager.isWaybillFinished(waybillCode);
        if(finished){
            result.parameterError("此运单已经妥投、请勿操作!");
            return result;
        }
        if(isSpotCheck(waybillCode,null)){
            result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，请勿重复操作!");
            return result;
        }
        //重泡比校验
        if(condition.getWaybillWeight()/condition.getWaybillVolume() > WEIGHT_VOLUME_RATIO){
            result.customMessage(600,"当前运单:"+waybillCode+"重泡比超过"+WEIGHT_VOLUME_RATIO+",请核实后重新录入!");
            return result;
        }
        int packNum = baseEntity.getData().getPackageList().size();
        if(condition.getWaybillWeight()/packNum > WEIGHT_MAX_RATIO){
            result.customMessage(600,"当前运单平均单个包裹重量超过"+WEIGHT_MAX_RATIO+"KG，请核实后重新录入!");
            return result;
        }
        if(condition.getWaybillVolume()/packNum > VOLUME_MAX_RATIO){
            result.customMessage(600,"当前运单平均单个包裹体积超过"+VOLUME_MAX_RATIO+"m³，请核实后重新录入!");
            return result;
        }

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
        Double nowVolume = condition.getWaybillVolume();
        WaybillFlowDetail waybillFlowDetail = getFirstWeightAndVolumeDetail(waybillCode);
        Double beforeWeight = waybillFlowDetail.getTotalWeight()==null?0.00:waybillFlowDetail.getTotalWeight();
        Double beforeVolume = waybillFlowDetail.getTotalVolume()==null?0.00:waybillFlowDetail.getTotalVolume();
        Boolean sign = isExcess(nowWeight,beforeWeight,M3_TRANS_TO_CM3*nowVolume,beforeVolume);
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

            //重泡比校验
            Double volume = param.getLength() * param.getWidth() * param.getHeight()/M3_TRANS_TO_CM3;
            if(param.getWeight()/volume > WEIGHT_VOLUME_RATIO){
                result.customMessage(600,"当前包裹号:"+param.getPackageCode()+"重泡比超过"+WEIGHT_VOLUME_RATIO+",请核实后重新录入!");
                return result;
            }
            if(param.getWeight() > WEIGHT_MAX_RATIO){
                result.customMessage(600,"当前包裹号:"+param.getPackageCode()+"重量超过"+WEIGHT_MAX_RATIO+"KG,请核实后重新录入!");
                return result;
            }
            if(volume > VOLUME_MAX_RATIO){
                result.customMessage(600,"当前包裹号:"+param.getPackageCode()+"体积超过"+VOLUME_MAX_RATIO+"m³,请核实后重新录入!");
                return result;
            }

            nowWeight = keeTwoDecimals(nowWeight + param.getWeight());
            nowVolume = keeTwoDecimals(nowVolume + param.getLength() * param.getWidth() * param.getHeight());//厘米
        }
        String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
        //获取运单中的体积和重量
        WaybillFlowDetail waybillFlowDetail = getFirstWeightAndVolumeDetail(waybillCode);
        Double beforeWeight = waybillFlowDetail.getTotalWeight()==null?0.00:waybillFlowDetail.getTotalWeight();
        Double beforeVolume = waybillFlowDetail.getTotalVolume()==null?0.00:waybillFlowDetail.getTotalVolume();
        //判断是否超标
        Boolean sign = isExcess(nowWeight,beforeWeight,nowVolume,beforeVolume);
        result.setData(sign?1:0);

        return result;
    }

    /**
     * 1、重量超标校验：
     *      录入重量<25kg,允许误差值(+-)1kg(含)
     *      录入重量>=25kg,允许误差值(+-)4%(含)
     * 2、体积超标校验：
     *      录入体积<0.1方,允许误差值(+-)0.1方(含)
     *      录入体积>=0.1方,允许误差值(+-)10%(含)
     * @param nowWeight 录入重量
     * @param beforeWeight 运单重量
     * @param nowVolume 录入体积
     * @param beforeVolume 运单体积
     * @return
     */
    private Boolean isExcess(Double nowWeight,Double beforeWeight,Double nowVolume,Double beforeVolume){
        if((nowWeight < WEIGHT_THRESHOLD && Math.abs(nowWeight - beforeWeight) > WEIGHT_STANDARD_UP)
                || (nowWeight >= WEIGHT_THRESHOLD && Math.abs(nowWeight - beforeWeight) > WEIGHT_STANDARD_LOW * nowWeight)){
            return Boolean.TRUE;
        }
        if((nowVolume < VOLUME_THRESHOLD && Math.abs(nowVolume - beforeVolume) > VOLUME_THRESHOLD)
                || (nowVolume >= VOLUME_THRESHOLD && Math.abs(nowVolume - beforeVolume) > VOLUME_STANDARD_LOW * nowVolume)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void sendMqToFXM(WeightVolumeCollectDto dto,AbnormalResultMq abnormalResultMq) {
        try {

            abnormalResultMq.setFrom(SystemEnum.DMS.getCode().toString());
            abnormalResultMq.setSource(SystemEnum.DMS.getCode());
            abnormalResultMq.setDutyErp(dto.getBillingErp());
            if(abnormalResultMq.getDutyType() != null){
                if(abnormalResultMq.getDutyType()==DutyTypeEnum.DMS.getCode()
                        || abnormalResultMq.getDutyType()==DutyTypeEnum.FLEET.getCode()
                        || (abnormalResultMq.getDutyType()==DutyTypeEnum.SITE.getCode()
                        &&StringUtils.isEmpty(abnormalResultMq.getDutyErp()))){
                    //责任为分拣或车队或站点无erp
                    abnormalResultMq.setTo(SystemEnum.ZHIKONG.getCode().toString());
                }else if(abnormalResultMq.getDutyType()==DutyTypeEnum.SITE.getCode()
                        && !StringUtils.isEmpty(abnormalResultMq.getDutyErp())){
                    //责任为站点有erp
                    StringBuilder to = new StringBuilder();
                    to.append(SystemEnum.TMS.getCode()).append(",").append(SystemEnum.ZHIKONG.getCode());
                    abnormalResultMq.setTo(to.toString());
                }else if(abnormalResultMq.getDutyType() == DutyTypeEnum.BIZ.getCode()){
                    //责任为信任商家
                    abnormalResultMq.setTo(SystemEnum.PANZE.getCode().toString());
                }
            }

            abnormalResultMq.setBillCode(dto.getWaybillCode());
            abnormalResultMq.setBusinessObjectId(dto.getBusiCode());
            abnormalResultMq.setBusinessObject(dto.getBusiName());

            abnormalResultMq.setWeight(BigDecimal.valueOf(dto.getBillingWeight()));
            abnormalResultMq.setVolume(BigDecimal.valueOf(dto.getBillingVolume()));
            abnormalResultMq.setId(dto.getPackageCode() + "_" +dto.getReviewDate().getTime());
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
            //默认值:不认责判责
            abnormalResultMq.setIsAccusation(1);
            abnormalResultMq.setIsNeedBlame(0);
            abnormalResultMq.setReviewDutyErp(dto.getReviewErp());
            abnormalResultMq.setReviewErp(dto.getReviewErp());
            abnormalResultMq.setBusinessType(2);

            if(log.isDebugEnabled()){
                log.debug("发送MQ成功topic：{},businessId：{}，msgContent：{}", dmsWeightVolumeExcess.getTopic(), abnormalResultMq.getBillCode(), JsonHelper.toJson(abnormalResultMq));
            }
            dmsWeightVolumeExcess.send(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
        }catch (Exception e){
            log.error("参数:{}, 异常信息:{}", dto.getWaybillCode() , e.getMessage(), e);
        }
    }

    @Override
    public InvokeResult<String> dealExcessDataOfWaybill(WeightVolumeCheckConditionB2b param) {
        InvokeResult<String> result = new InvokeResult<>();
        try{
            //防止二次提交
            String waybillCode = WaybillUtil.getWaybillCode(param.getWaybillOrPackageCode());
            if(isSpotCheck(waybillCode,param.getCreateSiteCode())){
                result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，请勿重复操作!");
                return result;
            }
            //组装数据
            SpotCheckData spotCheckData = trans2SpotCheckDataOfWaybill(param);
            AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
            WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
            assembleData(spotCheckData,dto,abnormalResultMq);
            weightAndVolumeCheckService.setProductType(dto);
            reportExternalService.insertOrUpdateForWeightVolume(dto);
            //发运单维度全程跟踪
            sendWaybillTrace(dto);
            //超标给fxm发mq
            if(param.getIsExcess() == 1){
                sendMqToFXM(dto,abnormalResultMq);
            }
        }catch (Exception e){
            log.error("参数:{}, 异常信息:{}", JsonHelper.toJson(param) , e.getMessage(), e);
            result.customMessage(600,"B网按运单抽检失败!");
        }
        return result;
    }

    /**
     * 转换成公共实体
     * @param param
     * @return
     */
    private SpotCheckData trans2SpotCheckDataOfWaybill(WeightVolumeCheckConditionB2b param) {
        SpotCheckData spotCheckData = new SpotCheckData();
        spotCheckData.setWaybillCode(WaybillUtil.getWaybillCode(param.getWaybillOrPackageCode()));
        spotCheckData.setTotalWeight(param.getWaybillWeight());
        spotCheckData.setTotalVolume(param.getWaybillVolume());
        spotCheckData.setCreateSiteCode(param.getCreateSiteCode());
        spotCheckData.setLoginErp(param.getLoginErp());
        spotCheckData.setIsExcess(param.getIsExcess());
        spotCheckData.setIsWaybillSpotCheck(1);
        return spotCheckData;
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
                && !WaybillUtil.isPackageCode(waybillCode)){
            result.parameterError("单号不符合规则!");
            return result;
        }
        waybillCode = WaybillUtil.getWaybillCode(waybillCode);
        //禁止非B网运单抽检
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
            if(!BusinessUtil.isB2b(baseEntity.getData().getWaybill().getWaybillSign())){
                result.parameterError("此功能只支持B网运单抽检!");
                return result;
            }
            if(waybillTraceManager.isWaybillFinished(waybillCode)){
                result.parameterError("此运单已经妥投、请勿操作!");
                return result;
            }
            //一单一检
            if(isSpotCheck(waybillCode,null)){
                result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，请勿重复操作!");
                return result;
            }
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
     0、信任商家直接取运单waybill中的重量体积goodWeight、goodVolume
     1、取运单号相关的所有称重量方记录（包裹和运单维度的都要）
     2、剔除重量体积均为0（注意，只剔除都是0的）的无意义的称重量方记录（多为系统卡控需要，实际并未称重）
     3、按时间先后顺序，找到最早称重量方的人ERP
     4、筛选出该ERP操作的所有称重量方记录
     5、若既有整单录入又有包裹录入，以该ERP最后一次重量体积录入时的形式为准
     6、若是整单，则取最后一次整单录入的重量体积为对比对象
     7、若是包裹，则筛选出所有包裹维度称重量方的记录，然后以包裹维度进行去重，仅保留时间靠后的那条，最后汇总得到的重量体积为对比对象
     */
    @Override
    public WaybillFlowDetail getFirstWeightAndVolumeDetail(String waybillCode){

        WaybillFlowDetail waybillFlowDetail = new WaybillFlowDetail();
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if(waybill != null){
            if(BusinessUtil.isTrustBusi(waybill.getWaybillSign())){
                //信任商家
                waybillFlowDetail.setTotalWeight(waybill.getGoodWeight());
                waybillFlowDetail.setTotalVolume(waybill.getGoodVolume());
                waybillFlowDetail.setTrustBusi(Boolean.TRUE);
                return waybillFlowDetail;
            }
        }else {
            log.error("参数:{}, 异常信息:{}", waybillCode , "通过运单号获取运单信息失败!");
            return waybillFlowDetail;
        }
        Page<PackFlowDetail> page = new Page<>();
        page.setPageSize(1000);
        Page<PackFlowDetail> result = waybillPackageManager.getOpeDetailByCode(waybillCode, page);
        if(result != null && !CollectionUtils.isEmpty(result.getResult())){
            List<PackFlowDetail> list = result.getResult();
            if(log.isInfoEnabled()){
                log.info("运单号:{}的称重量方流水记录:{}", waybillCode , JsonHelper.toJson(list));
            }
            List<PackFlowDetail> timeSortList = new ArrayList<>();
            //排除重量体积均为0的情况(系统卡控)
            for(PackFlowDetail detail : list){
                if(detail==null || ((detail.getpWeight()!=null&&detail.getpWeight()==0)
                        && (detail.getpLength()!=null&&detail.getpLength()==0
                        &&detail.getpWidth()!=null&&detail.getpWidth()==0
                        &&detail.getpHigh()!=null&&detail.getpHigh()==0))){
                    continue;
                }
                timeSortList.add(detail);
            }
            if(CollectionUtils.isEmpty(timeSortList)){
                return getWaybillFlowDetail(waybillCode,waybillFlowDetail);
            }

            List<PackFlowDetail> weightTimeSortList = new ArrayList<>();
            List<PackFlowDetail> volumeTimeSortList = new ArrayList<>();
            List<PackFlowDetail> finalList = new ArrayList<>();
            //按称重时间从小到大排序
            Collections.sort(timeSortList, new Comparator<PackFlowDetail>() {
                @Override
                public int compare(PackFlowDetail o1, PackFlowDetail o2) {
                    if(o1.getWeighTime() == null || o2.getWeighTime() == null){
                        return -1;
                    }
                    return o1.getWeighTime().compareTo(o2.getWeighTime());
                }
            });
            weightTimeSortList.addAll(timeSortList);
            //按量方时间从小到大排序
            Collections.sort(timeSortList, new Comparator<PackFlowDetail>() {
                @Override
                public int compare(PackFlowDetail o1, PackFlowDetail o2) {
                    if(o1.getMeasureTime() == null || o2.getMeasureTime() == null){
                        return -1;
                    }
                    return o1.getMeasureTime().compareTo(o2.getMeasureTime());
                }
            });
            volumeTimeSortList.addAll(timeSortList);
            PackFlowDetail packFlowDetail = null;
            String operateId = null;
            String operateErp = null;
            Date operateTime = null;
            Date weightTime = weightTimeSortList.get(0).getWeighTime();
            Date volumeTime = volumeTimeSortList.get(0).getMeasureTime();
            if(volumeTime == null
                    || (weightTime!=null&&volumeTime!=null&&weightTime.getTime()<=volumeTime.getTime())){
                finalList = weightTimeSortList;
                packFlowDetail = weightTimeSortList.get(0);
                operateId = packFlowDetail.getWeighUserId();
                operateErp = packFlowDetail.getWeighUserErp();
                operateTime = packFlowDetail.getWeighTime();

            }else if(weightTime == null
                    || (weightTime!=null&&volumeTime!=null&&volumeTime.getTime()<=weightTime.getTime())){
                finalList = volumeTimeSortList;
                packFlowDetail = volumeTimeSortList.get(0);
                operateId = packFlowDetail.getMeasureUserId();
                operateErp = packFlowDetail.getMeasureUserErp();
                operateTime = packFlowDetail.getMeasureTime();
            }else {
                return waybillFlowDetail;
            }
            // 防止ERP字段为空,根据id获取ERP
            try {
                if(StringUtils.isEmpty(operateErp)){
                    BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(Integer.valueOf(operateId));
                    operateErp = baseStaff.getAccountNumber();
                }
            }catch (Exception e){
                log.error("根据操作人id获取操作人信息异常,异常信息:【{}】",e.getMessage(),e);
            }

            waybillFlowDetail.setOperateErp(operateErp);
            waybillFlowDetail.setOperateTime(operateTime);
            waybillFlowDetail.setOperateSiteCode(packFlowDetail.getOperatorSiteId());
            waybillFlowDetail.setOperateSiteName(packFlowDetail.getOperatorSite());
            if(StringUtils.isEmpty(operateErp)){
                return waybillFlowDetail;
            }
            //获取最早操作人的所有称重、量方记录
            List<PackFlowDetail> realListOfWeight = new ArrayList<>();
            List<PackFlowDetail> realListOfVolume = new ArrayList<>();
            for(PackFlowDetail detail : finalList){
                if(operateErp.equals(detail.getWeighUserErp())){
                    realListOfWeight.add(detail);
                }
                if(operateErp.equals(detail.getMeasureUserErp())){
                    realListOfVolume.add(detail);
                }
            }
            //获取总重量体积并设置
            Double totalWeight = getWeightOrVolume(realListOfWeight, IS_WEIGHT_TYPE);
            Double totalVolume = getWeightOrVolume(realListOfVolume, IS_VOLUME_TYPE);
            waybillFlowDetail.setTotalWeight(totalWeight);
            waybillFlowDetail.setTotalVolume(totalVolume);
            return waybillFlowDetail;
        }else {
            log.error("参数:{}, 异常信息:{}", waybillCode , "未获取到运单号的称重流水");
            getWaybillFlowDetail(waybillCode,waybillFlowDetail);
        }
        return waybillFlowDetail;
    }

    /**
     * 获取重量或体积
     * @param realList 流水记录
     * @param type 1:获取重量
     *             0:获取体积
     * @return
     */
    private Double getWeightOrVolume(List<PackFlowDetail> realList,Integer type){
        Double totalWeight = 0.00;
        Double totalVolume = 0.00;
        if(realList.size() == 0){
            return 0.00;
        }
        Map<String,PackFlowDetail> waybillAndPackMap = new LinkedHashMap<>();
        Map<String,PackFlowDetail> waybillMap = new LinkedHashMap<>();
        Map<String,PackFlowDetail> packageMap = new LinkedHashMap<>();
        for(PackFlowDetail detail : realList){
            if(WaybillUtil.isWaybillCode(detail.getPackageCode())){
                waybillMap.put(detail.getPackageCode(),detail);
            }else {
                packageMap.put(detail.getPackageCode(),detail);
            }
            waybillAndPackMap.put(detail.getPackageCode(),detail);
        }
        if(waybillAndPackMap.size() > waybillMap.size()){
            if(waybillMap.size() != 0){
                //既有整单又有包裹
                PackFlowDetail lastFlowDetail = realList.get(realList.size() - 1);
                String waybillOrPackCode = lastFlowDetail.getPackageCode();
                //最后一次是运单则取运单，否则取包裹总和
                if(WaybillUtil.isWaybillCode(waybillOrPackCode)){
                    totalWeight = lastFlowDetail.getpWeight();
                    totalVolume = (lastFlowDetail.getpLength()==null?0.00:lastFlowDetail.getpLength())
                            *(lastFlowDetail.getpWidth()==null?0.00:lastFlowDetail.getpWidth())
                            *(lastFlowDetail.getpHigh()==null?0.00:lastFlowDetail.getpHigh());
                }else{
                    totalWeight = getTotalWeightAndVolume(packageMap,IS_WEIGHT_TYPE);
                    totalVolume = getTotalWeightAndVolume(packageMap,IS_VOLUME_TYPE);
                }
            }else {
                //包裹
                totalWeight = getTotalWeightAndVolume(waybillAndPackMap,IS_WEIGHT_TYPE);
                totalVolume = getTotalWeightAndVolume(waybillAndPackMap,IS_VOLUME_TYPE);
            }
        }else {
            //整单
            PackFlowDetail flowDetail = realList.get(realList.size()-1);
            totalWeight = flowDetail.getpWeight();
            if(flowDetail.getpLength()==null
                    || flowDetail.getpWidth()==null || flowDetail.getpHigh()==null){
                totalVolume = 0.00;
            }else {
                totalVolume = (flowDetail.getpLength()==null?0.00:flowDetail.getpLength())
                        *(flowDetail.getpWidth()==null?0.00:flowDetail.getpWidth())
                        *(flowDetail.getpHigh()==null?0.00:flowDetail.getpHigh());
            }
        }
        if(type == IS_WEIGHT_TYPE){
            return totalWeight;
        }else {
            return totalVolume;
        }
    }

    /**
     * 设置总重量总体积
     * @param map
     * @param type 1:重量 0:体积
     */
    private Double getTotalWeightAndVolume(Map<String, PackFlowDetail> map, Integer type) {
        Double totalWeight = 0.00;
        Double totalVolume = 0.00;
        for(String packageCode : map.keySet()){
            PackFlowDetail flowDetail = map.get(packageCode);
            totalWeight = totalWeight + (flowDetail.getpWeight()==null?0.00:flowDetail.getpWeight());
            totalVolume = totalVolume + ((flowDetail.getpLength()==null?0.00:flowDetail.getpLength())
                    *(flowDetail.getpWidth()==null?0.00:flowDetail.getpWidth())
                    *(flowDetail.getpHigh()==null?0.00:flowDetail.getpHigh()));
        }
        if(type==IS_WEIGHT_TYPE){
            return totalWeight;
        }else {
            return totalVolume;
        }
    }

    /**
     * 获取揽收单位信息
     * @param waybillCode
     * @return
     */
    private WaybillFlowDetail getWaybillFlowDetail(String waybillCode,WaybillFlowDetail waybillFlowDetail){
        List<PackageStateDto> list
                = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
        if(list.size() > 0){
            PackageStateDto packageState = list.get(0);
            waybillFlowDetail.setTotalWeight(0.00);
            waybillFlowDetail.setTotalVolume(0.00);
            waybillFlowDetail.setOperateSiteCode(packageState.getOperatorSiteId());
            waybillFlowDetail.setOperateSiteName(packageState.getOperatorSite());
            String operatorUserErp = null;
            Integer operatorUserId = packageState.getOperatorUserId();
            if(operatorUserId != null){
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffIdNoCache(operatorUserId);
                if(dto != null){
                    operatorUserErp = dto.getAccountNumber();
                }
            }
            waybillFlowDetail.setOperateErp(operatorUserErp);
            waybillFlowDetail.setOperateTime(packageState.getCreateTime());
        }else {
            log.error("参数:{}, 异常信息:{}", waybillCode , "运单没有揽收记录");
        }
        return waybillFlowDetail;
    }

    /**
     * 判断是否操作过抽检
     * @param waybillCode
     * @return
     */
    private Boolean isSpotCheck(String waybillCode,Integer siteCode){
        //是否操作过抽检
        WeightVolumeQueryCondition weightVolumeQueryCondition = new WeightVolumeQueryCondition();
        weightVolumeQueryCondition.setPackageCode(waybillCode);
        weightVolumeQueryCondition.setReviewSiteCode(siteCode);
        BaseEntity<List<WeightVolumeCollectDto>> entity = reportExternalService.getByParamForWeightVolume(weightVolumeQueryCondition);
        if(entity != null && entity.getData() != null
                && entity.getData().size() != 0){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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

}
