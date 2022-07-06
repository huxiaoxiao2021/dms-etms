package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.alibaba.fastjson.JSON;
import com.jd.jss.JingdongStorageService;
import com.jd.jss.client.Request;
import com.jd.jss.domain.ObjectListing;
import com.jd.jss.domain.ObjectSummary;
import com.jd.jss.http.JssInputStreamEntity;
import com.jd.jss.http.Scheme;
import com.jd.jss.service.BucketService;
import com.jd.jss.service.ObjectService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * @ClassName: WeightAndVolumeCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:48
 */
@Service("weightAndVolumeCheckService")
public class WeightAndVolumeCheckServiceImpl implements WeightAndVolumeCheckService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${DMS_ADDRESS}")
    private String dmsAddress;

    @Value("${spotCheck.fourSumLWH:70}")
    public String fourSumLWH;

    /** 存储空间文件夹名称 */
    @Value("${jss.bucket.picture}")
    private String bucket;

    @Value("${spotCheck.multiplePackageVolumeStandard:12700}")
    public String multiplePackageVolumeStandard;

    /** 对象存储 **/
    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";
    /** 预签名过期时间 */
    private static final Integer SIGNATURE_TIMEOUT = 2592000;
    /**
     * 需要进行过滤的https域名
     */
    @Value("#{'${jss.httpsSet}'.split(',')}")
    private HashSet<String> httpsSet;

    @Value("${jss.endpoint}")
    private String dmsWebJssEndpoint;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Autowired
    @Qualifier("dmsWebJingdongStorageService")
    private JingdongStorageService dmsWebJingdongStorageService;

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    DmsBaseDictService dmsBaseDictService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;


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
            if(StringUtils.isEmpty(waybillSpotCheckCondition.getFromSource())){
                result.parameterError("未知抽检来源!");
                return result;
            }

            BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity = reportExternalService.getPagerByConditionForWeightVolume(weightVolumeQueryConditionPager);
            if(baseEntity != null && baseEntity.getData() != null && CollectionUtils.isNotEmpty(baseEntity.getData().getData())){
                for (WeightVolumeCollectDto collectDto : baseEntity.getData().getData()) {
                    WeightVolumePictureDto weightVolumePictureDto = new WeightVolumePictureDto();
                    weightVolumePictureDto.setWaybillCode(collectDto.getWaybillCode());
                    weightVolumePictureDto.setPackageCode(collectDto.getPackageCode());
                    weightVolumePictureDto.setUrl(collectDto.getPictureAddress());

                    if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName(), waybillSpotCheckCondition.getFromSource())
                            || Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName(), waybillSpotCheckCondition.getFromSource())
                            || Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName(), waybillSpotCheckCondition.getFromSource())
                            || (Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName(), waybillSpotCheckCondition.getFromSource()))
                                    && (Objects.equals(collectDto.getMultiplePackage(), Constants.NUMBER_ZERO) || collectDto.getMultiplePackage() == null)){
                        // 安卓抽检 | 页面抽检 | 客户端抽检 | dws一单一件抽检 只有一条运单维度记录
                        dataList.add(weightVolumePictureDto);
                        break;
                    }
                    if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName(), waybillSpotCheckCondition.getFromSource())
                            && Objects.equals(collectDto.getMultiplePackage(), Constants.CONSTANT_NUMBER_ONE)){
                        // dws一单多件展示所有包裹记录
                        if(Objects.equals(collectDto.getRecordType(), Constants.CONSTANT_NUMBER_ONE)){
                            dataList.add(weightVolumePictureDto);
                        }
                    }
                    if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName(), waybillSpotCheckCondition.getFromSource())){
                        if(Objects.equals(collectDto.getIsWaybillSpotCheck(), Constants.CONSTANT_NUMBER_ONE)){
                            // 人工抽检运单维度
                            if(Objects.equals(collectDto.getRecordType(), SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode())){
                                dataList.add(weightVolumePictureDto);
                                break;
                            }
                        }else {
                            // 人工抽检包裹维度
                            if(Objects.equals(collectDto.getMultiplePackage(), Constants.NUMBER_ZERO)){
                                // 一单一件
                                dataList.add(weightVolumePictureDto);
                                break;
                            }else {
                                // 一单多件
                                if(Objects.equals(collectDto.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode())){
                                    dataList.add(weightVolumePictureDto);
                                }
                            }
                        }
                    }
                }
                dataPager.setTotal(baseEntity.getData().getTotal());
            }else {
                log.warn("查询单号:{}站点:{}的数据为空!", waybillSpotCheckCondition.getWaybillCode(), waybillSpotCheckCondition.getReviewSiteCode());
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

    private int getPackageNumberTotal(Waybill waybill, String packageCode){
        int packNum = 1;
        if(WaybillUtil.isPackageCode(packageCode)){
            packNum = WaybillUtil.getPackNumByPackCode(packageCode);
        }else {
            if(waybill != null){
                Integer goodNumber = waybill.getGoodNumber();
                if(goodNumber != null){
                    packNum = goodNumber;
                }
            }
        }
        return packNum;
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
        headerMap.put("pictureLookAddress","图片查看地址");
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
        if(Objects.equals(fromSource, SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())){
            return "平台打印抽检";
        }
        if(Objects.equals(fromSource, SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName())){
            return "DWS抽检";
        }
        if(Objects.equals(fromSource, SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())){
            return "人工抽检";
        }
        if(Objects.equals(fromSource, SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())){
            return "网页抽检";
        }
        if(Objects.equals(fromSource, SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName())){
            return "安卓抽检";
        }
        return null;
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
            exportWeightVolumeCollectDto.setIsExcess(
                    Objects.equals(dto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())
                            ? ExcessStatusEnum.EXCESS_ENUM_YES.getName() : Objects.equals(dto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode())
                            ? ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getName() : Objects.equals(dto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_NO.getCode())
                            ? ExcessStatusEnum.EXCESS_ENUM_NO.getName() : ExcessStatusEnum.EXCESS_ENUM_NO_KNOW.getName()
            );
            exportWeightVolumeCollectDto.setFromSource(getFromSource(dto.getFromSource()));
            exportWeightVolumeCollectDto.setIsHasPicture(Objects.equals(dto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE) ? "有" : "无");
            exportWeightVolumeCollectDto.setPictureLookAddress(pictureLookUrl(dto));
            list.add(exportWeightVolumeCollectDto);
        }
        return list;
    }

    /**
     * 查看图片地址
     *
     * @param dto
     * @return
     */
    private String pictureLookUrl(WeightVolumeCollectDto dto) {
        if(!Objects.equals(dto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE)){
            return Constants.SEPARATOR_HYPHEN;
        }
        return String.format(SpotCheckConstants.PICTURE_LOOK_URL, dmsAddress, dto.getWaybillCode(), dto.getReviewSiteCode(), dto.getFromSource());
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
        newCondition.setQueryForWeb(condition.getQueryForWeb());
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
        URI uri;
        if (httpsSet.contains(dmsWebJssEndpoint)){
            uri = dmsWebJingdongStorageService.bucket(bucket).object(keyName)
                    .presignedUrlProtocol(Scheme.HTTPS).generatePresignedUrl(SIGNATURE_TIMEOUT);
        }
        else{
            uri = dmsWebJingdongStorageService.bucket(bucket).object(keyName)
                    .generatePresignedUrl(SIGNATURE_TIMEOUT);
        }
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
        return spotCheckDealService.executeNewHandleProcess(weightAndVolumeCheckHandleMessage);
    }
}
