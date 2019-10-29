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
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import java.util.HashMap;
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

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;


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
            for(WeightVolumeCheckOfB2bPackage param : params){
                SpotCheckOfPackageDetail spotCheckOfPackageDetail = new SpotCheckOfPackageDetail();
                spotCheckOfPackageDetail.setBillCode(param.getPackageCode());
                spotCheckOfPackageDetail.setWeight(param.getWeight());
                spotCheckOfPackageDetail.setLength(param.getLength());
                spotCheckOfPackageDetail.setWidth(param.getWidth());
                spotCheckOfPackageDetail.setHeight(param.getHeight());
                List<Map<String,String>> imgList = new ArrayList<>();
                spotCheckOfPackageDetail.setImgList(imgList);
                com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                        = searchExcessPicture(param.getPackageCode(), param.getCreateSiteCode());
                if(invokeResult != null && !CollectionUtils.isEmpty(invokeResult.getData())){
                    for(String url : invokeResult.getData()){
                        Map<String,String> map = new LinkedHashMap<>();
                        map.put(url,"");
                        imgList.add(map);
                    }
                }

                detailList.add(spotCheckOfPackageDetail);
            }
            //数据落入es
            WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
            assembleDataOfPackage(params,dto,abnormalResultMq);
            weightAndVolumeCheckService.setProductType(dto);
            reportExternalService.insertOrUpdateForWeightVolume(dto);
            //发运单维度匿名全程跟踪
            sendWaybillTrace(dto);
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
     * 运单维度组装数据
     * @param param
     * @param dto
     * @param abnormalResultMq
     * @return
     */
    private void assembleDataOfWaybill(WeightVolumeCheckConditionB2b param, WeightVolumeCollectDto dto, AbnormalResultMq abnormalResultMq) {

        //查询运单称重流水
        String waybillCode = WaybillUtil.getWaybillCode(param.getWaybillOrPackageCode());
        WaybillFlowDetail waybillFlowDetail = getFirstWeightAndVolumeDetail(waybillCode);

        abnormalResultMq.setDutyErp(waybillFlowDetail.getOperateErp());

        dto.setSpotCheckType(1);
        dto.setIsWaybillSpotCheck(1);
        dto.setBillingWeight(waybillFlowDetail.getTotalWeight()==null?0.00:waybillFlowDetail.getTotalWeight());
        dto.setBillingVolume(waybillFlowDetail.getTotalVolume()==null?0.00:waybillFlowDetail.getTotalVolume());
        dto.setBillingErp(waybillFlowDetail.getOperateErp());
        Integer siteId = waybillFlowDetail.getOperateSiteCode();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteId);
        if(baseStaffSiteOrgDto != null){
            abnormalResultMq.setFirstLevelId(baseStaffSiteOrgDto.getOrgId().toString());
            abnormalResultMq.setFirstLevelName(baseStaffSiteOrgDto.getOrgName());
            abnormalResultMq.setSecondLevelId(baseStaffSiteOrgDto.getSiteCode().toString());
            abnormalResultMq.setSecondLevelName(baseStaffSiteOrgDto.getSiteName());
            if(baseStaffSiteOrgDto.getSiteType() == 900){
                //责任类型仓
                abnormalResultMq.setDutyType(1);
            }else if(baseStaffSiteOrgDto.getSiteType() == 64){
                //责任类型分拣
                abnormalResultMq.setDutyType(2);
            }else if(baseStaffSiteOrgDto.getSiteType() == 4){
                //责任类型为站点
                abnormalResultMq.setDutyType(3);
            }else if(baseStaffSiteOrgDto.getSiteType() == 1024){
                //责任类型为B商家
                abnormalResultMq.setDutyType(4);
            }else if(baseStaffSiteOrgDto.getSiteType() == 96){
                //责任类型为车队
                abnormalResultMq.setDutyType(6);
                abnormalResultMq.setSecondLevelId(baseStaffSiteOrgDto.getAreaCode());
                abnormalResultMq.setSecondLevelName(baseStaffSiteOrgDto.getAreaName());
                abnormalResultMq.setThreeLevelId(baseStaffSiteOrgDto.getSiteCode().toString());
                abnormalResultMq.setThreeLevelName(baseStaffSiteOrgDto.getSiteName());
            }else {
                abnormalResultMq.setDutyType(5);
            }
            dto.setBillingOrgCode(baseStaffSiteOrgDto.getOrgId());
            dto.setBillingOrgName(baseStaffSiteOrgDto.getOrgName());
            dto.setBillingDeptCode(baseStaffSiteOrgDto.getSiteCode());
            dto.setBillingDeptName(baseStaffSiteOrgDto.getSiteName());
            dto.setBillingCompany(baseStaffSiteOrgDto.getSiteName());
        }
        dto.setIsExcess(param.getIsExcess());
        dto.setIsHasPicture(param.getIsExcess());
        if(param.getIsExcess()==1){
            //查询图片地址
            com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> invokeResult
                    = searchExcessPicture(waybillCode, param.getCreateSiteCode());
            List<SpotCheckOfPackageDetail> detailList = new ArrayList<>();
            abnormalResultMq.setDetailList(detailList);
            SpotCheckOfPackageDetail detail = new SpotCheckOfPackageDetail();
            detail.setBillCode(waybillCode);
            detail.setLength(dto.getBillingVolume());
            List<Map<String,String>> imgList = new ArrayList<>();
            detail.setImgList(imgList);
            if(invokeResult != null && !CollectionUtils.isEmpty(invokeResult.getData())){
                for(String url : invokeResult.getData()){
                    Map<String,String> map = new HashMap<>();
                    map.put(url,"");
                    imgList.add(map);
                }
            }
            detailList.add(detail);
        }
        dto.setReviewDate(new Date());
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
                abnormalResultMq.setIsTrustMerchant(1);
            }else if(BusinessUtil.isSignInChars(baseEntity.getData().getWaybill().getWaybillSign(),56,'0','2','3')) {
                //普通商家
                dto.setIsTrustBusi(0);
                abnormalResultMq.setIsTrustMerchant(0);
            }
        }
        String reviewErp = param.getLoginErp();
        if(!StringUtils.isEmpty(reviewErp)){
            BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseStaffByErpNoCache(reviewErp);
            if(baseSiteByDmsCode != null){
                dto.setReviewOrgCode(baseSiteByDmsCode.getOrgId());
                dto.setReviewOrgName(baseSiteByDmsCode.getOrgName());
                dto.setReviewSiteCode(baseSiteByDmsCode.getSiteCode());
                dto.setReviewSiteName(baseSiteByDmsCode.getSiteName());
                dto.setReviewSubType(baseSiteByDmsCode.getSubType());
                dto.setReviewErp(param.getLoginErp());
            }
        }
        dto.setReviewWeight(param.getWaybillWeight());
        dto.setReviewVolume(param.getWaybillVolume()==null?0:param.getWaybillVolume()*1000000);

        dto.setReviewVolumeWeight(keeTwoDecimals(dto.getReviewVolume()/8000));
        dto.setBillingVolumeWeight(keeTwoDecimals(dto.getBillingVolume()/8000));
        dto.setWeightDiff(new DecimalFormat("#0.00").format(dto.getReviewWeight()-dto.getBillingWeight()));
        dto.setVolumeWeightDiff(new DecimalFormat("#0.00").format(dto.getReviewVolumeWeight()-dto.getBillingVolumeWeight()));

    }

    /**
     * 包裹维度组装数据称重数据
     * @param params
     * @return
     */
    private void assembleDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params, WeightVolumeCollectDto dto, AbnormalResultMq abnormalResultMq) {

        //查询运单称重流水
        String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
        WaybillFlowDetail waybillFlowDetail = getFirstWeightAndVolumeDetail(waybillCode);
        dto.setIsWaybillSpotCheck(0);
        dto.setSpotCheckType(1);
        dto.setReviewDate(new Date());
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
                abnormalResultMq.setIsTrustMerchant(1);
            }else if(BusinessUtil.isSignInChars(baseEntity.getData().getWaybill().getWaybillSign(),56,'0','2','3')) {
                //普通商家
                dto.setIsTrustBusi(0);
                abnormalResultMq.setIsTrustMerchant(0);
            }
        }
        String reviewErp = params.get(0).getLoginErp();
        if(!StringUtils.isEmpty(reviewErp)){
            BaseStaffSiteOrgDto baseSiteByDmsCode = baseMajorManager.getBaseStaffByErpNoCache(reviewErp);
            if(baseSiteByDmsCode != null){
                dto.setReviewOrgCode(baseSiteByDmsCode.getOrgId());
                dto.setReviewOrgName(baseSiteByDmsCode.getOrgName());
                dto.setReviewSiteCode(baseSiteByDmsCode.getSiteCode());
                dto.setReviewSiteName(baseSiteByDmsCode.getSiteName());
                dto.setReviewSubType(baseSiteByDmsCode.getSubType());
                dto.setReviewErp(reviewErp);
            }
        }
        Double totalWeight = 0.00;
        Double totalVolume = 0.00;
        for(WeightVolumeCheckOfB2bPackage param : params){
            totalWeight = keeTwoDecimals(totalWeight + param.getWeight());
            totalVolume = keeTwoDecimals(totalVolume + param.getLength() * param.getWidth() * param.getHeight());
        }
        dto.setReviewWeight(totalWeight);
        dto.setReviewVolume(totalVolume);
        dto.setBillingWeight(waybillFlowDetail.getTotalWeight()==null?0.00:waybillFlowDetail.getTotalWeight());
        dto.setBillingVolume(waybillFlowDetail.getTotalVolume()==null?0.00:waybillFlowDetail.getTotalVolume());
        dto.setReviewVolumeWeight(keeTwoDecimals(dto.getReviewVolume()/8000));
        dto.setBillingVolumeWeight(keeTwoDecimals(dto.getBillingVolume()/8000));
        dto.setWeightDiff(new DecimalFormat("#0.00").format(dto.getReviewWeight() - dto.getBillingWeight()));
        dto.setVolumeWeightDiff(new DecimalFormat("#0.00").format(dto.getReviewVolumeWeight() - dto.getBillingVolumeWeight()));

        BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(waybillFlowDetail.getOperateSiteCode());
        if(baseSiteInfoDto != null){
            dto.setBillingOrgCode(baseSiteInfoDto.getOrgId());
            dto.setBillingOrgName(baseSiteInfoDto.getOrgName());
            dto.setBillingDeptCode(baseSiteInfoDto.getSiteCode());
            dto.setBillingDeptName(baseSiteInfoDto.getSiteName());
            dto.setBillingCompany(baseSiteInfoDto.getSiteName());
            abnormalResultMq.setFirstLevelId(baseSiteInfoDto.getOrgId().toString());
            abnormalResultMq.setFirstLevelName(baseSiteInfoDto.getOrgName());
            abnormalResultMq.setSecondLevelId(baseSiteInfoDto.getSiteCode().toString());
            abnormalResultMq.setSecondLevelName(baseSiteInfoDto.getSiteName());
            if(baseSiteInfoDto.getSiteType() == 900){
                //责任类型仓
                abnormalResultMq.setDutyType(1);
            }else if(baseSiteInfoDto.getSiteType() == 64){
                //责任类型分拣
                abnormalResultMq.setDutyType(2);
            }else if(baseSiteInfoDto.getSiteType() == 4){
                //责任类型为站点
                abnormalResultMq.setDutyType(3);
            }else if(baseSiteInfoDto.getSiteType() == 1024){
                //责任类型为B商家
                abnormalResultMq.setDutyType(4);
            }else if(baseSiteInfoDto.getSiteType() == 96){
                //责任类型为车队
                abnormalResultMq.setDutyType(6);
                abnormalResultMq.setSecondLevelId(baseSiteInfoDto.getAreaCode());
                abnormalResultMq.setSecondLevelName(baseSiteInfoDto.getAreaName());
                abnormalResultMq.setThreeLevelId(baseSiteInfoDto.getSiteCode().toString());
                abnormalResultMq.setThreeLevelName(baseSiteInfoDto.getSiteName());
            }else {
                abnormalResultMq.setDutyType(5);
            }
        }
        dto.setBillingErp(waybillFlowDetail.getOperateErp());
        dto.setIsExcess(params.get(0).getIsExcess());
        dto.setIsHasPicture(params.get(0).getIsExcess());
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
        //是否操作过抽检
        WeightVolumeQueryCondition weightVolumeQueryCondition = new WeightVolumeQueryCondition();
        weightVolumeQueryCondition.setPackageCode(waybillCode);
        BaseEntity<List<WeightVolumeCollectDto>> entity = reportExternalService.getByParamForWeightVolume(weightVolumeQueryCondition);
        if(entity != null && entity.getData() != null
                && entity.getData().size() != 0){
            result.customMessage(600,"运单"+waybillCode+"已经进行过抽检，请勿重复操作!");
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
        Boolean sign = isExcess(nowWeight,beforeWeight,1000000*nowVolume,beforeVolume);
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
            if(abnormalResultMq.getDutyType() != null){
                if(abnormalResultMq.getDutyType()==2 || abnormalResultMq.getDutyType()==6
                        || (abnormalResultMq.getDutyType()==3&&StringUtils.isEmpty(abnormalResultMq.getDutyErp()))){
                    //责任为分拣或车队或站点无erp
                    abnormalResultMq.setTo(SystemEnum.ZHIKONG.getCode().toString());
                }else if(abnormalResultMq.getDutyType()==3 && !StringUtils.isEmpty(abnormalResultMq.getDutyErp())){
                    //责任为站点有erp
                    abnormalResultMq.setTo(SystemEnum.TMS.getCode().toString());
                }else if(abnormalResultMq.getDutyType() == 4){
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
                //防止二次提交
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
                //组装数据落入es
                WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
                assembleDataOfWaybill(param,dto,abnormalResultMq);
                weightAndVolumeCheckService.setProductType(dto);
                reportExternalService.insertOrUpdateForWeightVolume(dto);
                //发运单维度全程跟踪
                sendWaybillTrace(dto);
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
                && !WaybillUtil.isPackageCode(waybillCode)){
            result.parameterError("单号不符合规则!");
            return result;
        }
        waybillCode = WaybillUtil.getWaybillCode(waybillCode);
        //禁止非B网运单抽检
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, true);
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
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setPackageCode(waybillCode);
            BaseEntity<List<WeightVolumeCollectDto>> entity = reportExternalService.getByParamForWeightVolume(condition);
            if(entity != null && entity.getData() != null
                    && entity.getData().size() != 0){
                result.customMessage(600,"运单已经进行过抽检，请勿重复操作!");
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
     1、取运单号相关的所有称重量方记录（包裹和运单维度的都要）
     2、剔除重量体积均为0（注意，只剔除都是0的）的无意义的称重量方记录（多为系统卡控需要，实际并未称重）
     3、按时间先后顺序，找到最早称重量方的人ERP
     4、筛选出该ERP操作的所有称重量方记录
     5、若既有整单录入又有包裹录入，以该ERP最后一次重量体积录入时的形式为准
     6、若是整单，则取最后一次整单录入的重量体积为对比对象
     7、若是包裹，则筛选出所有包裹维度称重量方的记录，然后以包裹维度进行去重，仅保留时间靠后的那条，最后汇总得到的重量体积为对比对象
     */
    @Cache(key = "DMS.BASE.WaybillPackageManagerImpl.getFirstWeightAndVolumeDetail@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public WaybillFlowDetail getFirstWeightAndVolumeDetail(String waybillCode){

        WaybillFlowDetail waybillFlowDetail = new WaybillFlowDetail();
        Page<PackFlowDetail> page = new Page<>();
        page.setPageSize(1000);
        com.jd.etms.waybill.domain.BaseEntity<Page<PackFlowDetail>> baseEntity = waybillPackageManager.getOpeDetailByCode(waybillCode,page);
        if(baseEntity != null && baseEntity.getData() != null
                && !CollectionUtils.isEmpty(baseEntity.getData().getResult())){
            List<PackFlowDetail> list = baseEntity.getData().getResult();
            List<PackFlowDetail> timeSortList = new ArrayList<>();
            //排除重量体积均为0的情况(系统卡控)
            for(PackFlowDetail detail : list){
                if(detail==null || ((detail.getpWeight()!=null&&detail.getpWeight()==0)
                        && (detail.getpLength()!=null&&detail.getpLength()==0&&detail.getpWidth()!=null&&detail.getpWidth()==0&&detail.getpHigh()!=null&&detail.getpHigh()==0))){
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
                        return 0;
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
                        return 0;
                    }
                    return o1.getMeasureTime().compareTo(o2.getMeasureTime());
                }
            });
            volumeTimeSortList.addAll(timeSortList);
            PackFlowDetail packFlowDetail = null;
            String operateErp = null;
            Date operatTime = null;
            Date weightTime = weightTimeSortList.get(0).getWeighTime();
            Date volumeTime = volumeTimeSortList.get(0).getWeighTime();
            if(volumeTime == null
                    || (weightTime!=null&&volumeTime!=null&&weightTime.getTime()>=volumeTime.getTime())){
                finalList = weightTimeSortList;
                packFlowDetail = weightTimeSortList.get(0);
                operateErp = packFlowDetail.getWeighUserErp();
                operatTime = packFlowDetail.getWeighTime();

            }else if(weightTime == null
                    || (weightTime!=null&&volumeTime!=null&&volumeTime.getTime()>=weightTime.getTime())){
                finalList = volumeTimeSortList;
                packFlowDetail = volumeTimeSortList.get(0);
                operateErp = packFlowDetail.getMeasureUserErp();
                operatTime = packFlowDetail.getMeasureTime();
            }else {
                return waybillFlowDetail;
            }
            waybillFlowDetail.setOperateErp(operateErp);
            waybillFlowDetail.setOperateTime(operatTime);
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
            Double totalWeight = getWeightOrVolume(realListOfWeight, 1);
            Double totalVolume = getWeightOrVolume(realListOfVolume, 0);
            waybillFlowDetail.setTotalWeight(totalWeight);
            waybillFlowDetail.setTotalVolume(totalVolume);
            return waybillFlowDetail;
        }else {
            logger.info("未获取到运单号"+waybillCode+"的称重流水");
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
        Map<String,PackFlowDetail> map1 = new LinkedHashMap<>();
        Map<String,PackFlowDetail> map2 = new LinkedHashMap<>();
        for(PackFlowDetail detail : realList){
            if(WaybillUtil.isWaybillCode(detail.getPackageCode())){
                map2.put(detail.getPackageCode(),detail);
            }
            map1.put(detail.getPackageCode(),detail);
        }
        if(map1.size() > map2.size()){
            if(map2.size() != 0){
                //既有整单又有包裹
                PackFlowDetail flowDetail = new PackFlowDetail();
                for(String packageCode : map1.keySet()){
                    flowDetail = map1.get(packageCode);
                }
                totalWeight = flowDetail.getpWeight();
                if(flowDetail.getpLength()==null
                        || flowDetail.getpWidth()==null || flowDetail.getpHigh()==null){
                    totalVolume = 0.00;
                }else {
                    totalVolume = flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh();
                }
            }else {
                //包裹
                for(String packageCode : map1.keySet()){
                    PackFlowDetail flowDetail = map1.get(packageCode);
                    totalWeight = totalWeight + flowDetail.getpWeight();
                    totalVolume = totalVolume + (flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh());
                }
            }
        }else {
            //整单
            PackFlowDetail flowDetail = new PackFlowDetail();
            for(String packageCode : map1.keySet()){
                flowDetail = map1.get(packageCode);
            }
            totalWeight = flowDetail.getpWeight();
            if(flowDetail.getpLength()==null
                    || flowDetail.getpWidth()==null || flowDetail.getpHigh()==null){
                totalVolume = 0.00;
            }else {
                totalVolume = flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh();
            }
        }
        if(type == 1){
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
        List<PackageState> list
                = waybillTraceManager.getPkStateByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
        if(list.size() > 0){
            PackageState packageState = list.get(0);
            waybillFlowDetail.setTotalWeight(0.00);
            waybillFlowDetail.setTotalVolume(0.00);
            waybillFlowDetail.setOperateSiteCode(packageState.getOperatorSiteId());
            waybillFlowDetail.setOperateSiteName(packageState.getOperatorSite());
            waybillFlowDetail.setOperateErp(packageState.getOperatorUserErp());
            waybillFlowDetail.setOperateTime(packageState.getCreateTime());
        }else {
            logger.error("运单"+waybillCode+"没有揽收记录");
        }
        return waybillFlowDetail;
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
