package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.security.SecurityCheckerExecutor;
import com.jd.bluedragon.core.security.enums.SecurityDataMapFuncEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.spotcheck.SpotCheckReportQueryCondition;
import com.jd.bluedragon.distribution.spotcheck.domain.ExportSpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckReportService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jss.util.ValidateValue;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.*;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckScrollResult;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽检报表服务
 *
 * @author hujiping
 * @date 2021/12/13 3:21 下午
 */
@Service("spotCheckReportService")
public class SpotCheckReportServiceImpl implements SpotCheckReportService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckReportServiceImpl.class);

    @Value("${DMS_ADDRESS}")
    private String dmsAddress;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Autowired
    private SecurityCheckerExecutor securityCheckerExecutor;

    @Override
    public PagerResult<WeightVolumeSpotCheckDto> listData(SpotCheckReportQueryCondition condition) {
        PagerResult<WeightVolumeSpotCheckDto> result = new PagerResult<WeightVolumeSpotCheckDto>();
        Pager<SpotCheckQueryCondition> pagerQuery = new Pager<>();
        pagerQuery.setPageNo(condition.getOffset() / condition.getLimit() + 1);
        pagerQuery.setPageSize(condition.getLimit());
        pagerQuery.setSearchVo(transferCondition(condition, SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode()));
        Pager<WeightVolumeSpotCheckDto> pagerResult = spotCheckQueryManager.querySpotCheckByPage(pagerQuery);
        int total = 0;
        List<WeightVolumeSpotCheckDto> rows = new ArrayList<>();
        if(pagerResult != null && CollectionUtils.isNotEmpty(pagerResult.getData())){
            total = Integer.parseInt(String.valueOf(pagerResult.getTotal()));
            rows = pagerResult.getData();
        }
        result.setTotal(total);
        result.setRows(rows);
        return result;
    }

    /**
     * 构建抽检查询条件
     *
     * @param condition
     * @param recordType 记录类型：2 总记录 1 明细记录
     * @return
     */
    private SpotCheckQueryCondition transferCondition(SpotCheckReportQueryCondition condition, Integer recordType) {
        SpotCheckQueryCondition spotCheckQueryCondition = new SpotCheckQueryCondition();
        spotCheckQueryCondition.setReviewStartTime(DateHelper.parseDate(condition.getReviewStartTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        spotCheckQueryCondition.setReviewEndTime(DateHelper.parseDate(condition.getReviewEndTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        spotCheckQueryCondition.setReviewOrgCode(condition.getReviewOrgCode() == null ? null : Integer.parseInt(String.valueOf(condition.getReviewOrgCode())));
        spotCheckQueryCondition.setReviewSiteCode(condition.getReviewSiteCode() == null ? null : Integer.parseInt(String.valueOf(condition.getReviewSiteCode())));
        spotCheckQueryCondition.setWaybillCode(condition.getWaybillCode());
        spotCheckQueryCondition.setPackageCode(condition.getPackageCode());
        spotCheckQueryCondition.setIsExcess(condition.getIsExcess() == null ? null : Integer.parseInt(String.valueOf(condition.getIsExcess())));
        spotCheckQueryCondition.setMerchantName(condition.getMerchantName());
        spotCheckQueryCondition.setReviewErp(condition.getReviewErp());
        spotCheckQueryCondition.setContrastErp(condition.getContrastErp());
        spotCheckQueryCondition.setBusinessType(condition.getBusinessType() == null ? null : Integer.parseInt(String.valueOf(condition.getBusinessType())));
        spotCheckQueryCondition.setIsGatherTogether(condition.getIsGatherTogether() == null ? null : Integer.parseInt(String.valueOf(condition.getIsGatherTogether())));
        spotCheckQueryCondition.setIsTrustMerchant(condition.getIsTrustMerchant() == null ? null : Integer.parseInt(String.valueOf(condition.getIsTrustMerchant())));
        spotCheckQueryCondition.setIsIssueDownstream(condition.getIsIssueDownstream() == null ? null : Integer.parseInt(String.valueOf(condition.getIsIssueDownstream())));
        spotCheckQueryCondition.setRecordType(recordType);
        return spotCheckQueryCondition;
    }

    @Override
    public PagerResult<WeightVolumeSpotCheckDto> packageDetailListData(SpotCheckReportQueryCondition condition) {
        PagerResult<WeightVolumeSpotCheckDto> packResult = new PagerResult<WeightVolumeSpotCheckDto>();
        Pager<SpotCheckQueryCondition> pagerQuery = new Pager<>();
        pagerQuery.setPageNo(condition.getOffset() / condition.getLimit() + 1);
        pagerQuery.setPageSize(condition.getLimit());
        pagerQuery.setSearchVo(transferCondition(condition, SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode()));
        Pager<WeightVolumeSpotCheckDto> pagerResult = spotCheckQueryManager.querySpotCheckByPage(pagerQuery);
        int total = 0;
        List<WeightVolumeSpotCheckDto> rows = new ArrayList<>();
        if(pagerResult != null && CollectionUtils.isNotEmpty(pagerResult.getData())){
            total = Integer.parseInt(String.valueOf(pagerResult.getTotal()));
            rows = pagerResult.getData();
        }
        packResult.setTotal(total);
        packResult.setRows(rows);
        return packResult;
    }

    @Override
    public void export(SpotCheckReportQueryCondition condition, BufferedWriter innerBfw) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, innerBfw, headerMap.values().size());
            // 分页查询记录
            SpotCheckQueryCondition spotCheckQueryCondition = transferCondition(condition, SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            // 设置总导出数据
            Integer uccSpotCheckMaxSize = exportConcurrencyLimitService.uccSpotCheckMaxSize();

            int queryTotal = 0;
            int index = 1;
            while (index++ <= 1000) {
                SpotCheckScrollResult spotCheckScrollResult = spotCheckQueryManager.querySpotCheckByScroll(spotCheckQueryCondition);
                if(spotCheckScrollResult == null || CollectionUtils.isEmpty(spotCheckScrollResult.getList())){
                    logger.warn("scroll方式查询抽检明细数据为空!");
                    break;
                }
                // 设置scrollId
                spotCheckQueryCondition.setScrollId(spotCheckScrollResult.getScrollId());
                // 输出至excel
                CsvExporterUtils.writeCsvByPage(innerBfw, headerMap, trans2ExportDto(spotCheckScrollResult.getList()));
                // 限制导出数量
                queryTotal += spotCheckScrollResult.getList().size();
                if(queryTotal > uccSpotCheckMaxSize){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getName(), end - start, queryTotal);
        }catch (Exception e){
            logger.error("scroll方式导出数据失败",e);
        }
    }

    private List<ExportSpotCheckDto> trans2ExportDto(List<WeightVolumeSpotCheckDto> list) {
        List<ExportSpotCheckDto> exportList = new ArrayList<>();
        if(CollectionUtils.isEmpty(list)){
            return exportList;
        }
        for (WeightVolumeSpotCheckDto spotCheckDto : list) {
            ExportSpotCheckDto exportSpotCheckDto = new ExportSpotCheckDto();
            exportSpotCheckDto.setReviewDate(spotCheckDto.getReviewDate() == null ? null : DateHelper.formatDate(new Date(spotCheckDto.getReviewDate()), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
            exportSpotCheckDto.setWaybillCode( spotCheckDto.getWaybillCode());
            exportSpotCheckDto.setBusinessType(spotCheckDto.getBusinessType() == null ? null : SpotCheckBusinessTypeEnum.analysisNameFromCode(spotCheckDto.getBusinessType()));
            exportSpotCheckDto.setProductTypeName(spotCheckDto.getProductTypeName());
            exportSpotCheckDto.setMerchantCode(spotCheckDto.getMerchantCode());
            exportSpotCheckDto.setMerchantName(spotCheckDto.getMerchantName());
            exportSpotCheckDto.setIsTrustMerchant(Objects.equals(spotCheckDto.getIsTrustMerchant(), 1) ? "是" : "否");
            exportSpotCheckDto.setReviewOrgName(spotCheckDto.getReviewOrgName());
            exportSpotCheckDto.setReviewSiteName(spotCheckDto.getReviewSiteName());
            exportSpotCheckDto.setSiteTypeName(Objects.equals(spotCheckDto.getSiteTypeName(), 1) ? "分拣中心" : "转运中心");
            exportSpotCheckDto.setReviewErp(spotCheckDto.getReviewUserErp());
            exportSpotCheckDto.setReviewWeight(spotCheckDto.getReviewWeight() == null ? null : String.valueOf(spotCheckDto.getReviewWeight()));
            exportSpotCheckDto.setReviewVolume(spotCheckDto.getReviewVolume() == null ? null : String.valueOf(spotCheckDto.getReviewVolume()));
            exportSpotCheckDto.setReviewLWH(spotCheckDto.getReviewLWH());
            exportSpotCheckDto.setContrastOrgName(spotCheckDto.getContrastOrgName());
            exportSpotCheckDto.setContrastWarZoneName(spotCheckDto.getContrastWarZoneName());
            exportSpotCheckDto.setContrastAreaName(spotCheckDto.getContrastAreaName());
            exportSpotCheckDto.setContrastSiteName(spotCheckDto.getContrastSiteName());
            exportSpotCheckDto.setContrastStaffAccount(StringUtils.isEmpty(spotCheckDto.getContrastStaffAccount())
                    ? spotCheckDto.getContrastStaffName() :spotCheckDto.getContrastStaffAccount());
            exportSpotCheckDto.setContrastWeight(spotCheckDto.getContrastWeight() == null ? null : String.valueOf(spotCheckDto.getContrastWeight()));
            exportSpotCheckDto.setContrastVolume(spotCheckDto.getContrastVolume() == null ? null : String.valueOf(spotCheckDto.getContrastVolume()));
            exportSpotCheckDto.setDiffWeight(spotCheckDto.getDiffWeight() == null ? null : String.valueOf(spotCheckDto.getDiffWeight()));
            exportSpotCheckDto.setDiffStandard(spotCheckDto.getDiffStandard());
            exportSpotCheckDto.setReviewSource(spotCheckDto.getReviewSource() == null ? null : SpotCheckSourceFromEnum.analysisDescFromCode(spotCheckDto.getReviewSource()));
            exportSpotCheckDto.setContrastSource(spotCheckDto.getReviewSource() == null ? null :
                    Objects.equals(spotCheckDto.getContrastSource(), 1) ? "计费重量(计费)" : Objects.equals(spotCheckDto.getContrastSource(), 2) ? "运单复重"
                            : Objects.equals(spotCheckDto.getContrastSource(), 3) ? "下单重量" : "计费重量(运单)");
            exportSpotCheckDto.setMachineCode(spotCheckDto.getMachineCode());
            exportSpotCheckDto.setIsGatherTogether(Objects.equals(spotCheckDto.getIsGatherTogether(), 1) ? "是" : "否");
            exportSpotCheckDto.setIsExcess(spotCheckDto.getIsExcess() == null ? null : ExcessStatusEnum.analysisNameFromCode(spotCheckDto.getIsExcess()));
            exportSpotCheckDto.setIsIssueDownstream( Objects.equals(spotCheckDto.getIsIssueDownstream(), 1) ? "是" : "否");
            exportSpotCheckDto.setSpotCheckStatus(spotCheckDto.getSpotCheckStatus() == null ? null : SpotCheckStatusEnum.analysisNameFromCode(spotCheckDto.getSpotCheckStatus()));
            exportSpotCheckDto.setIsHasPicture(Objects.equals(spotCheckDto.getIsHasPicture(), 1) ? "有" : "无");
            exportSpotCheckDto.setSearchPicUrl(pictureLookUrl(spotCheckDto));
            exportList.add(exportSpotCheckDto);
        }
        return exportList;
    }

    /**
     * 查看图片地址
     *
     * @param spotCheckDto
     * @return
     */
    private String pictureLookUrl(WeightVolumeSpotCheckDto spotCheckDto) {
        if(!Objects.equals(spotCheckDto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE)){
            return Constants.SEPARATOR_HYPHEN;
        }
        return String.format(SpotCheckConstants.SPOT_CHECK_PICTURE_LOOK_URL, dmsAddress, spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode(), spotCheckDto.getReviewSource());
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("reviewDate", "复核日期");
        headerMap.put("waybillCode", "运单号");
        headerMap.put("businessType", "业务类型");
        headerMap.put("productTypeName", "产品标识");
        headerMap.put("merchantCode", "配送商家编号");
        headerMap.put("merchantName", "商家名称");
        headerMap.put("isTrustMerchant", "信任商家");
        headerMap.put("reviewOrgName", "复核区域");
        headerMap.put("reviewSiteName", "复核分拣");
        headerMap.put("siteTypeName", "机构类型");
        headerMap.put("reviewErp", "复核人ERP");
        headerMap.put("reviewWeight", "复核重量");
        headerMap.put("reviewVolume", "复核体积");
        headerMap.put("reviewLWH", "复核长宽高");
        headerMap.put("contrastOrgName", "核对操作区域");
        headerMap.put("contrastWarZoneName", "核对操作战区");
        headerMap.put("contrastAreaName", "核对操作片区");
        headerMap.put("contrastSiteName", "核对操作站点");
        headerMap.put("contrastDutyErp", "核对操作人ERP");
        headerMap.put("contrastWeight", "核对重量");
        headerMap.put("contrastVolume", "核对体积");
        headerMap.put("diffWeight", "重量差异");
        headerMap.put("diffStandard", "误差标准值");
        headerMap.put("reviewSource", "复核来源");
        headerMap.put("contrastSource", "核对来源");
        headerMap.put("machineCode", "设备编码");
        headerMap.put("isGatherTogether", "是否集齐");
        headerMap.put("isExcess", "是否超标");
        headerMap.put("isIssueDownstream", "是否下发");
        headerMap.put("spotCheckStatus", "抽检状态");
        headerMap.put("isHasPicture", "有无图片");
        headerMap.put("searchPicUrl", "图片查询地址");
        return headerMap;
    }

    @Override
    public InvokeResult<Pager<WeightVolumePictureDto>> searchPicture(SpotCheckReportQueryCondition condition) {
        InvokeResult<Pager<WeightVolumePictureDto>> result = new InvokeResult<>();
        Pager<WeightVolumePictureDto> dataPager = new Pager<>();
        List<WeightVolumePictureDto> dataList = new ArrayList<>();
        dataPager.setData(dataList);
        result.setData(dataPager);
        try {
            if(condition.getReviewSiteCode() == null || StringUtils.isEmpty(condition.getWaybillCode()) || condition.getReviewSource() == null){
                result.parameterError(InvokeResult.PARAM_ERROR);
                return result;
            }
            List<WeightVolumeSpotCheckDto> list = spotCheckQueryManager.querySpotCheckByCondition(transferCondition(condition, null));
            if(CollectionUtils.isEmpty(list)){
                result.customMessage(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
                return result;
            }
            for (WeightVolumeSpotCheckDto spotCheckDto : list) {
                WeightVolumePictureDto weightVolumePictureDto = new WeightVolumePictureDto();
                weightVolumePictureDto.setWaybillCode(spotCheckDto.getWaybillCode());
                weightVolumePictureDto.setPackageCode(spotCheckDto.getPackageCode());
                weightVolumePictureDto.setUrl(spotCheckDto.getPictureAddress());

                if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getCode(), condition.getReviewSource())
                        || Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getCode(), condition.getReviewSource())
                        || Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getCode(), condition.getReviewSource())
                        || (Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getCode(), condition.getReviewSource())
                            && Objects.equals(spotCheckDto.getIsMultiPack(), Constants.NUMBER_ZERO))
                ){
                    // 安卓抽检 | 页面抽检 | 客户端抽检 | dws一单一件抽检（只有一条记录）
                    dataList.add(weightVolumePictureDto);
                    break;
                }
                if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getCode(), condition.getReviewSource())
                        && Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)){
                    // dws一单多件展示所有包裹记录（多条记录）
                    if(Objects.equals(spotCheckDto.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode())){
                        dataList.add(weightVolumePictureDto);
                    }
                }
            }
            dataPager.setTotal(Long.parseLong(String.valueOf(dataList.size())));
        }catch (Exception e){
            logger.error("searchPicture 获取图片链接失败! {}", JsonHelper.toJson(condition));
            result.parameterError(String.format("查看单号%s站点%s的图片失败!", condition.getWaybillCode(), condition.getReviewSiteCode()));
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> uploadExcessPicture(MultipartFile image, HttpServletRequest request) {
        // 校验图片
        InvokeResult<Boolean> result = checkImage(image);
        if(!result.codeSuccess()){
            return result;
        }
        Integer reviewSiteCode = Integer.valueOf(request.getParameter("reviewSiteCode"));
        String packageCode = request.getParameter("packageCode");
        // 校验是否已上传
        if(checkIsUpload(packageCode, reviewSiteCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "图片已上传，请勿重复上传!");
            return result;
        }
        String pictureUrl = null;
        try {
            pictureUrl = spotCheckDealService.uploadExcessPicture(image.getOriginalFilename(), image.getInputStream());
        }catch (Exception e){
            logger.error("上传包裹号:{}站点:{}的超标图片异常!", packageCode, reviewSiteCode);
        }
        if(StringUtils.isEmpty(pictureUrl)){
            result.error("图片上传异常,请重新上传!");
            return result;
        }
        // 上传图片后更新抽检数据
        afterUploadDeal(pictureUrl, packageCode, reviewSiteCode);
        return result;
    }

    @Override
    public InvokeResult<Boolean> securityCheck(String waybillCode, String userErp) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        // 信息安全校验
        com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> securityCheckResult
                = securityCheckerExecutor.verifyWaybillDetailPermission(SecurityDataMapFuncEnum.WAYBILL_PICTURE, userErp, waybillCode);
        if(!securityCheckResult.codeSuccess()){
            result.parameterError(securityCheckResult.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 图片上传后处理
     */
    private void afterUploadDeal(String pictureUrl, String packageCode, Integer reviewSiteCode) {
        // 设置已上传图片缓存
        try {
            String key = String.format(SpotCheckConstants.UPLOADED_PIC_PREFIX, packageCode, reviewSiteCode);
            jimdbCacheService.setEx(key, 1, 5, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置已上传图片缓存{}|{}异常!", packageCode, reviewSiteCode);
        }
        // 更新抽检数据
        WeightVolumeSpotCheckDto dto = new WeightVolumeSpotCheckDto();
        dto.setPackageCode(packageCode);
        dto.setReviewSiteCode(reviewSiteCode);
        dto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        dto.setPictureAddress(pictureUrl);
        spotCheckServiceProxy.insertOrUpdateProxyReform(dto);
    }

    private boolean checkIsUpload(String packageCode, Integer reviewSiteCode) {
        try {
            String key = String.format(SpotCheckConstants.UPLOADED_PIC_PREFIX, packageCode, reviewSiteCode);
            if(StringUtils.isNotEmpty(jimdbCacheService.get(key))){
                return true;
            }
            SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
            condition.setPackageCode(packageCode);
            condition.setReviewSiteCode(reviewSiteCode);
            condition.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            Integer count = spotCheckQueryManager.querySpotCheckCountByCondition(condition);
            return count != null && count > 0;
        }catch (Exception e){
            logger.error("查询已上传图片缓存{}|{}异常!", packageCode, reviewSiteCode);
        }
        return false;
    }

    private InvokeResult<Boolean> checkImage(MultipartFile image) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long imageSize = image.getSize();
        String imageName = image.getOriginalFilename();
        String[] strArray = imageName.split("\\.");
        String suffixName = strArray[strArray.length - 1];
        try{
            String[] defaultSuffixName = new String[] {"jpg", "jpeg", "gif", "png", "bmp"};
            if(!Arrays.asList(defaultSuffixName).contains(suffixName)){
                result.parameterError("文件格式不正确!"+suffixName);
                logger.warn("文件格式不正确:{}", suffixName);
                return result;
            }
            long maxSize = 1024000L;
            if(imageSize > maxSize){
                result.parameterError(MessageFormat.format("图片{0}的大小为{1}byte,超出单个图片最大限制{2}byte",
                        imageName, imageSize, maxSize));
                logger.warn("单个图片超出限制大小");
                return result;
            }
            //校验文件名称中的特殊字符
            ValidateValue.validateObjectKey(imageName);
        }catch (Exception e){
            String formatMsg = MessageFormat.format("文件名只能是由字母、数字、中划线(-)及点号(.)组成，该文件名称校验失败{0}", imageName );
            result.parameterError(formatMsg);
            logger.warn(formatMsg, e);
        }
        return result;
    }
}
