package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bPackage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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


    @Override
    public InvokeResult<String> dealExcessDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params) {
        InvokeResult<String> result = new InvokeResult<String>();
        Double nowWeight = 0.00;
        Double nowVolume = 0.00;
        //参数校验

        for(WeightVolumeCheckOfB2bPackage param : params){
            if(param == null || param.getWeight() == null || param.getLength() == null
                    || param.getWidth() == null || param.getHeight() == null){
                result.parameterError("参数不能为空!");
                return result;
            }
            nowWeight = keeTwoDecimals(nowWeight + param.getWeight());
            nowVolume = keeTwoDecimals(nowVolume + param.getLength() * param.getWidth() * param.getHeight());//厘米
        }
        //查es防止二次提交
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setReviewSiteCode(params.get(0).getCreateSiteCode());
        condition.setPackageCode(params.get(0).getPackageCode());
        BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
        if(baseEntity != null && baseEntity.getData() != null
                && baseEntity.getData().size() != 0){
            result.customMessage(600,"请勿重复提交!");
            return result;
        }

        //发送包裹维度匿名全程跟踪 TODO
        sendWaybillTrace(nowWeight,nowVolume);

        //计算重量、体积、获取图片链接 TODO
//        com.jd.bluedragon.distribution.base.domain.InvokeResult<String> pictureResult
//                = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);


        //数据落入es TODO
        String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
        WeightVolumeCollectDto dto = createWeightVolumeCollectDto(1,waybillCode);
        insertOrUpdateForWeightVolume(dto);

        //超标则给FXM发mq TODO
        if(params.get(0).getIsExcess()==1){
            sendMqToFXM(dto);
        }

        return result;
    }

    @Override
    public com.jd.bluedragon.distribution.base.domain.InvokeResult<String> searchExcessPicture(String packageCode, Integer siteCode) {

        return weightAndVolumeCheckService.searchExcessPicture(packageCode,siteCode);
    }

    private void sendWaybillTrace(Double nowWeight, Double nowVolume) {

//        Task tTask = new Task();
//        tTask.setKeyword1(waybillCode);
//        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN));
//        tTask.setCreateSiteCode(message.getSiteCode());
//        tTask.setCreateTime(new Date(message.getOperateTime()));
////        tTask.setReceiveSiteCode(0);
//        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
//        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
//        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
//        String ownSign = BusinessHelper.getOwnSign();
//        tTask.setOwnSign(ownSign);
//
//        WaybillStatus status=new WaybillStatus();
//        status.setOperateType(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN);
//        status.setWaybillCode(waybillCode);
//        status.setSendCode(newWaybillCode);//将新单号存到sendCode字段中
//        status.setOperateTime(new Date(message.getOperateTime()));
//        status.setOperator(message.getOperatorName());
//        status.setOperatorId(message.getOperatorUserId());
//        status.setRemark("签单返回合单");
//        status.setCreateSiteCode(message.getSiteCode());
//        status.setCreateSiteName(message.getSiteName());
//        status.setPackageCode(waybillCode);
//        tTask.setBody(JsonHelper.toJson(status));
//        taskService.add(tTask);

    }

    /**
     * 组装数据称重数据
     * @param type 0:只返回重量体积
     *             1:返回所有数据
     * @param waybillCode 运单号
     * @return
     */
    private WeightVolumeCollectDto createWeightVolumeCollectDto(int type,String waybillCode) {
        WeightVolumeCollectDto dto = new WeightVolumeCollectDto();
        if(StringUtils.isEmpty(waybillCode)){
            return dto;
        }
        //查询运单称重流水
//        dto.setBillingWeight();
//        dto.setBillingVolume();
        if(type == 0){
            return dto;
        }


        return dto;
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
        String waybillCode = WaybillUtil.getWaybillCode(params.get(0).getPackageCode());
        WeightVolumeCollectDto dto = createWeightVolumeCollectDto(0,waybillCode);
        Double beforeWeight = dto.getBillingWeight()==null?0.00:dto.getBillingWeight();
        Double beforeVolume = dto.getBillingVolume()==null?0.00:dto.getBillingVolume();
        //判断是否超标
        Boolean sign = Boolean.FALSE;
        if((nowWeight < 25 && Math.abs(nowWeight - beforeWeight) > 1)
            || (nowWeight >= 25 && Math.abs(nowWeight - beforeWeight) > 0.04 * nowWeight)){
            sign = Boolean.TRUE;
            result.setData(sign?1:0);
            return result;
        }
        if((nowVolume < 100000 && Math.abs(nowVolume - beforeVolume) > 100000)
                || (nowVolume >= 100000 && Math.abs(nowVolume - beforeVolume) > 0.1 * nowVolume)){
            sign = Boolean.TRUE;
        }
        result.setData(sign?1:0);

        return result;
    }

    private void sendMqToFXM(WeightVolumeCollectDto dto) {
        try {
            AbnormalResultMq abnormalResultMq = new AbnormalResultMq();

            logger.info("发送MQ[" + dmsWeightVolumeExcess.getTopic() + "],业务ID[" + abnormalResultMq.getBillCode() + "],消息主题: " + JsonHelper.toJson(abnormalResultMq));
            dmsWeightVolumeExcess.send(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
        }catch (Exception e){
            logger.error("B网抽检运单号:" + dto.getWaybillCode() + "下发FXM的mq发送失败:" + e.getMessage());
        }
    }

    /**
     * 数据落es
     * */
    private void insertOrUpdateForWeightVolume(WeightVolumeCollectDto dto) {

//        private Date reviewDate;
//        dto.setReviewDate(new Date());
//        private String waybillCode;
//        dto.setWaybillCode();
//        private String packageCode;
//        dto.setPackageCode();
//        private Integer isTrustBusi;
//        dto.setIsTrustBusi();
//        private String busiName;
//        dto.setBusiName();
//        private Integer busiCode;
//        dto.setBusiCode();
//        private Integer reviewOrgCode;
//        dto.setReviewOrgCode();
//        private String reviewOrgName;
//        dto.setReviewOrgName();
//        private Integer reviewSiteCode;
//        dto.setReviewSiteCode();
//        private String reviewSiteName;
//        dto.setReviewSiteName();
//        private Integer reviewSubType;
//        dto.setReviewSubType();
//        private String reviewErp;
//        dto.setReviewErp();
//        private Double reviewWeight;
//        dto.setReviewWeight();
//        private String reviewLWH;
//        dto.setReviewLWH();
//        private Double reviewVolume;
//        dto.setReviewVolume();
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
//        private String weightDiff;
//        dto.setWeightDiff();
//        private String volumeWeightDiff;
//        dto.setVolumeWeightDiff();
//        private String diffStandard;
//        dto.setDiffStandard();
//        private Integer isExcess;
//        dto.setIsExcess();
//        private Integer isHasPicture;
//        dto.setIsHasPicture();
//        private String pictureAddress;
//        dto.setPictureAddress();
//        private Double reviewVolumeWeight;
//        dto.setReviewVolumeWeight();
//        private Double billingVolumeWeight;
//        dto.setBillingVolumeWeight();
//        private Integer volumeWeightIsExcess;
//        dto.setVolumeWeightIsExcess();
//        private String billingCompany;
//        dto.setBillingCompany();
//        reportExternalService.insertOrUpdateForWeightVolume(dto);
    }

    @Override
    public InvokeResult<String> dealExcessDataOfWaybill(List<WeightVolumeCheckConditionB2b> params) {
        InvokeResult<String> result = new InvokeResult<>();

        //1.查询es是否该运单是否存在，存在直接返回，并提示“此运单已经进行过抽检，请勿重复操作”，不存在往下走
        Boolean isExcess = false;
        if(isExcess){
            result.customMessage(600,"此运单已经进行过抽检，请勿重复操作!");
            return result;
        }
        //2.查询称重流水数据判断是否超标

        //3.发运单维度全程跟踪

        //4.组装数据落入es

        //5.超标给fxm发mq

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
        //调运单接口获取运单下包裹信息组装后返回给前台
        List<DeliveryPackageD> packList = waybillQueryManager.findWaybillPackList(waybillCode);
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
        }

        return result;
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

        List<WeightVolumeCheckOfB2bWaybill> list = new ArrayList<>();
        result.setData(list);
        String waybillCode = WaybillUtil.getWaybillCode(condition.getWaybillOrPackageCode());
        WeightVolumeCheckOfB2bWaybill weightVolumeCheckOfB2bWaybill = new WeightVolumeCheckOfB2bWaybill();
        list.add(weightVolumeCheckOfB2bWaybill);
        weightVolumeCheckOfB2bWaybill.setWaybillCode(waybillCode);
        weightVolumeCheckOfB2bWaybill.setWaybillWeight(condition.getWaybillWeight());
        weightVolumeCheckOfB2bWaybill.setWaybillVolume(condition.getWaybillVolume());
        weightVolumeCheckOfB2bWaybill.setUpLoadNum(0);//初始上传图片数量

        //调运单接口判断是否超标 TODO
        weightVolumeCheckOfB2bWaybill.setPackNum(3);
        weightVolumeCheckOfB2bWaybill.setIsExcess(new Random().nextInt(2));

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
