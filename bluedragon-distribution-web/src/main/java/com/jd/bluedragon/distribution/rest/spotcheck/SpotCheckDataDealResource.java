package com.jd.bluedragon.distribution.rest.spotcheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

/**
 * @author hujiping
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SpotCheckDataDealResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    private JssService jssService;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    /**
     * 从excel获取包裹号修复抽检无图片问题
     *  fixme：excel中只有两列，站点、单号
     *
     * @param excelName
     * @return
     */
    @POST
    @Path("/spotCheck/repairPicUrlFromExcel/{excelName}")
    public InvokeResult<Boolean> repairPicUrlFromExcel(@PathParam("excelName") String excelName){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long startTime = System.currentTimeMillis();
        // 从excel获取需要处理的数据，key：站点，value：站点需要修复的单号
        Map<Integer, Set<String>> todoRepairPack = getDataFromExcel(excelName);
        if(Objects.equals(todoRepairPack.size(), Constants.NUMBER_ZERO)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            return result;
        }
        for (Integer siteCode : todoRepairPack.keySet()) {
            Map<String, Object> packMap = new HashMap<>();
            packMap.put("packList", todoRepairPack.get(siteCode));
            packMap.put("siteCode", siteCode);
            repairPicUrl(packMap);
        }
        result.setMessage("处理下发数据耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return result;
    }

    private Map<Integer, Set<String>> getDataFromExcel(String excelName) {
        Map<Integer, Set<String>> todoRepairPack = new HashMap<>(5);

        InputStream inputStream = jssService.downloadFile("wangtingwei", excelName);
        //解析excel表格中的信息
        DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(1);
        List<SpotCheckPackPic> dataList = null;
        try {
            dataList = dataResolver.resolver(inputStream, SpotCheckDataDealResource.SpotCheckPackPic.class, new PropertiesMetaDataFactory("/excel/spotCheckPack.properties"));
        }catch (Exception e){
            logger.error("读取文件异常!", e);
        }
        if(CollectionUtils.isEmpty(dataList)){
            return todoRepairPack;
        }
        for (SpotCheckPackPic spotCheckPackPic : dataList) {
            Integer siteCode = spotCheckPackPic.getSiteCode();
            if(todoRepairPack.containsKey(siteCode)){
                todoRepairPack.get(siteCode).add(spotCheckPackPic.getBarCode());
            }else {
                Set<String> set = new HashSet<>();
                set.add(spotCheckPackPic.getBarCode());
                todoRepairPack.put(siteCode, set);
            }
        }
        return todoRepairPack;
    }

    /**
     * 修复抽检无图片问题
     *  fixme：入参是需要修复的单号以及所属站点，siteCode：站点，packList：包裹号集合
     * @param packMap
     * @return
     */
    @POST
    @Path("/spotCheck/repairPicUrl")
    public InvokeResult<Boolean> repairPicUrl(Map<String, Object> packMap){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long startTime = System.currentTimeMillis();
        Set<String> packSet = JsonHelper.fromJson(String.valueOf(packMap.get("packList")), Set.class);
        Integer siteCode = Integer.valueOf(String.valueOf(packMap.get("siteCode")));

        if(CollectionUtils.isEmpty(packSet)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.PARAM_ERROR);
            return result;
        }
        // 去重包裹号
        Set<String> multiPackList = new HashSet<>(5);
        Set<String> oncePackList = new HashSet<>(5);
        for (String packageCode : packSet) {
            if(!WaybillUtil.isPackageCode(packageCode) && !WaybillUtil.isWaybillCode(packageCode)){
                continue;
            }
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            if(waybill == null || waybill.getGoodNumber() == null){
                logger.warn("运单信息或运单包裹数不存在!!");
                continue;
            }
            if(Objects.equals(waybill.getGoodNumber(), Constants.CONSTANT_NUMBER_ONE)){
                String realPackageCode = waybillCode.concat("-1-1-");
                oncePackList.add(realPackageCode);
                continue;
            }
            multiPackList.add(waybillCode);
        }
        // 一单一件处理
        oncePackListDeal(oncePackList, siteCode);
        // 一单多件处理
        multiPackListDeal(multiPackList, siteCode);
        result.setMessage("处理下发数据耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return result;
    }

    private void multiPackListDeal(Set<String> multiPackList, Integer siteCode) {
        if(CollectionUtils.isEmpty(multiPackList)){
            return;
        }
        for (String waybillCode : multiPackList) {
            // 查看抽检数据是否超标
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setWaybillCode(waybillCode);
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            List<WeightVolumeCollectDto> accordList = reportExternalManager.queryByCondition(condition);
            if(CollectionUtils.isEmpty(accordList)){
                logger.warn("运单号：" + waybillCode + "站点：" + siteCode + "无超标抽检数据!");
                continue;
            }
            // 有且只有一条记录（只有总记录才会记录超标状态）
            WeightVolumeCollectDto collectDto = accordList.get(0);
            if(!(Objects.equals(collectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName())
                    || Objects.equals(collectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName()))){
                // 只处理dws抽检和平台打印抽检（这两种抽检是异步上传图片）
                continue;
            }
            // 包裹维度抽检
            WeightVolumeQueryCondition packCondition = new WeightVolumeQueryCondition();
            packCondition.setWaybillCode(waybillCode);
            packCondition.setReviewSiteCode(siteCode);
            packCondition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
            List<WeightVolumeCollectDto> packList = reportExternalManager.queryByCondition(packCondition);
            for (WeightVolumeCollectDto packCollect : packList) {
                String packageCode = packCollect.getPackageCode();
                if(StringUtils.isNotEmpty(packCollect.getPictureAddress())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "查询到图片已更新到抽检记录");
                    continue;
                }
                // 查看图片是否上传
                InvokeResult<String> searchPicResult = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);
                if(StringUtils.isEmpty(searchPicResult.getData())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "未查询图片");
                    continue;
                }
                // 待更新图片packCollect
                WeightVolumeCollectDto updateCollect = new WeightVolumeCollectDto();
                updateCollect.setPackageCode(packCollect.getPackageCode());
                updateCollect.setReviewSiteCode(siteCode);
                updateCollect.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
                updateCollect.setPictureAddress(searchPicResult.getData());
                reportExternalManager.insertOrUpdateForWeightVolume(updateCollect);
            }
        }
    }

    private void oncePackListDeal(Set<String> oncePackList, Integer siteCode) {
        if(CollectionUtils.isEmpty(oncePackList)){
            return;
        }
        for (String packageCode : oncePackList) {
            // 查看图片是否上传
            InvokeResult<String> searchPicResult = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);
            if(StringUtils.isEmpty(searchPicResult.getData())){
                logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "未查询图片");
                continue;
            }
            // 查看抽检数据是否超标
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setWaybillCode(waybillCode);
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            List<WeightVolumeCollectDto> accordList = reportExternalManager.queryByCondition(condition);
            WeightVolumeCollectDto collectDto = accordList.get(0);
            if(CollectionUtils.isEmpty(accordList)){
                logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "未查询到超标抽检数据");
                continue;
            }else {
                if(StringUtils.isNotEmpty(collectDto.getPictureAddress())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "查询到图片已更新到抽检记录");
                    continue;
                }
            }
            // 待更新图片collect
            WeightVolumeCollectDto updateCollect = new WeightVolumeCollectDto();
            updateCollect.setPackageCode(collectDto.getPackageCode());
            updateCollect.setReviewSiteCode(siteCode);
            updateCollect.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            updateCollect.setPictureAddress(searchPicResult.getData());
            reportExternalManager.insertOrUpdateForWeightVolume(updateCollect);
        }
    }

    /**
     * 从excel获取运单号处理后并下发至fxm
     *   fixme：excel中只有一列，单号
     * @param excelName
     * @return
     */
    @POST
    @Path("/spotCheck/issueFromExcel/{excelName}")
    public InvokeResult<Boolean> issueFromExcel(@PathParam("excelName") String excelName){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long startTime = System.currentTimeMillis();
        // 从excel获取需要处理的运单号
        InputStream inputStream = jssService.downloadFile("wangtingwei", excelName);
        //解析excel表格中的信息
        DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(1);
        List<SpotCheckPackPic> dataList = null;
        try {
            dataList = dataResolver.resolver(inputStream, SpotCheckDataDealResource.SpotCheckPackPic.class, new PropertiesMetaDataFactory("/excel/spotCheckPack.properties"));
        }catch (Exception e){
            logger.error("读取文件异常!", e);
        }
        if(CollectionUtils.isEmpty(dataList)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            return result;
        }
        Set<String> waybillSet = new HashSet<>();
        for (SpotCheckPackPic spotCheckPackPic : dataList) {
            waybillSet.add(WaybillUtil.getWaybillCode(spotCheckPackPic.getBarCode()));
        }
        if(CollectionUtils.isEmpty(waybillSet)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            return result;
        }
        issueFxm(new ArrayList<String>(waybillSet));
        result.setMessage("处理下发数据耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return result;
    }

    /**
     * 根据条件查询数据后进行下发
     *  fixme：入参是查询条件，根据条件查询到运单号进行下发处理
     * @param conditionMap
     * @return
     */
    @POST
    @Path("/spotCheck/issueByCondition")
    public InvokeResult<Boolean> issueByCondition(Map<String, Object> conditionMap){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long dealStartTime = System.currentTimeMillis();

        Integer siteCode = Integer.valueOf(String.valueOf(conditionMap.get("siteCode")));
        String fromSource = String.valueOf(conditionMap.get("fromSource"));
        long startTime = Long.parseLong(String.valueOf(conditionMap.get("startTime")));
        long endTime = Long.parseLong(String.valueOf(conditionMap.get("endTime")));

        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setReviewSiteCode(siteCode);
        condition.setFromSource(fromSource);
        condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        condition.setStartTime(new Date(startTime));
        condition.setEndTime(new Date(endTime));
        List<WeightVolumeCollectDto> accordList = reportExternalManager.queryByCondition(condition);
        if(CollectionUtils.isEmpty(accordList)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            return result;
        }
        Set<String> waybillSet = new HashSet<>();
        for (WeightVolumeCollectDto collectDto : accordList) {
            waybillSet.add(collectDto.getWaybillCode());
        }
        issueFxm(new ArrayList<String>(waybillSet));
        result.setMessage("处理下发数据耗时：" + (System.currentTimeMillis() - dealStartTime)/1000 + "s");
        return result;
    }

    /**
     * 下发超标数据至fxm
     *  fixme：入参是需要下发处理的单号
     *
     * @param barCodeList
     * @return
     */
    @POST
    @Path("/spotCheck/issueFxm")
    public InvokeResult<Boolean> issueFxm(List<String> barCodeList) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        long startTime = System.currentTimeMillis();
        if(CollectionUtils.isEmpty(barCodeList)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.PARAM_ERROR);
            return result;
        }
        // 包裹号转换为运单号（下发fxm是以运单维度下发）
        Set<String> waybillSet = new HashSet<>(5);
        for (String barCode : barCodeList) {
            waybillSet.add(WaybillUtil.getWaybillCode(barCode));
        }
        if(CollectionUtils.isEmpty(waybillSet)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.PARAM_ERROR);
            return result;
        }
        for (String waybillCode : waybillSet) {
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setWaybillCode(waybillCode);
            condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            List<WeightVolumeCollectDto> queryCollect = reportExternalManager.queryByCondition(condition);
            if(CollectionUtils.isEmpty(queryCollect)){
                continue;
            }
            WeightVolumeCollectDto collectDto = queryCollect.get(0);
            if(Objects.equals(collectDto.getIssueDownstream(), Constants.CONSTANT_NUMBER_ONE)){
                // 已下发不处理
                break;
            }
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            int packNum = waybill.getGoodNumber() == null ? 0 : waybill.getGoodNumber();
            if(packNum == 0){
                logger.warn("运单号:{}包裹数不正确!", waybillCode);
                continue;
            }
            // 1、运单维度抽检，只有一条记录
            if(Objects.equals(collectDto.getIsWaybillSpotCheck(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode()) || packNum == 1){
                // 1.1、是否有图片
                if(StringUtils.isEmpty(collectDto.getPictureAddress())){
                    continue;
                }
                // 1.2、是否发货
                if(!Objects.equals(collectDto.getWaybillStatus(), 2)){
                    SendDetailDto params = new SendDetailDto();
                    params.setWaybillCode(waybillCode);
                    params.setCreateSiteCode(collectDto.getReviewSiteCode());
                    params.setIsCancel(0);
                    params.setStatus(1);
                    List<String> sendPackList = sendDetailService.queryPackageByWaybillCode(params);
                    if(CollectionUtils.isEmpty(sendPackList) || !Objects.equals(sendPackList.size(), packNum)){
                        logger.warn("运单号:{}下的包裹未全部发货", collectDto.getWaybillCode());
                        continue;
                    }
                }
                // 1.3 都满足后直接下发
                spotCheckDealService.issueSpotCheckDetail(collectDto);
            }else {
                // 2、包裹维度抽检
                // 2.1、一单一件的只有一条记录，在上面已处理
                // 2.2、一单多件的有一条总记录多条包裹记录
                if (packNum > 1) {
                    // 查询所有包裹记录
                    WeightVolumeQueryCondition packCondition = new WeightVolumeQueryCondition();
                    packCondition.setWaybillCode(waybillCode);
                    packCondition.setReviewSiteCode(collectDto.getReviewSiteCode());
                    packCondition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
                    List<WeightVolumeCollectDto> packCollectList = reportExternalManager.queryByCondition(packCondition);

                    int hasSendCount = 0; // 已发货包裹数
                    int hasPicUrlCount = 0; // 有图片包裹数
                    for (WeightVolumeCollectDto packCollect : packCollectList) {
                        // 所有的包裹记录
                        // 2.2.1、包裹是否有图片
                        if (StringUtils.isEmpty(packCollect.getPictureAddress())) {
                            break;
                        }
                        hasPicUrlCount += 1;
                        // 2.2.2、包裹是否全发货
                        if (!Objects.equals(packCollect.getWaybillStatus(), 2)) {
                            List<SendDetail> packList = sendDetailService.findByWaybillCodeOrPackageCode(packCollect.getReviewSiteCode(), null, packCollect.getPackageCode());
                            if (CollectionUtils.isEmpty(packList)) {
                                logger.warn("包裹号:{}未发货", packCollect.getPackageCode());
                                break;
                            }
                            hasSendCount += 1;
                        }else {
                            hasSendCount += 1;
                        }
                    }
                    if (hasSendCount >= packNum && hasPicUrlCount >= packNum) {
                        // 2.3、下发超标数据
                        spotCheckDealService.issueSpotCheckDetail(collectDto);
                    }
                }
            }
        }
        result.setMessage("处理下发数据耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        return result;
    }

    public static class SpotCheckPackPic {
        private String barCode;
        private Integer siteCode;
        public String getBarCode() {
            return barCode;
        }
        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }
        public Integer getSiteCode() {
            return siteCode;
        }
        public void setSiteCode(Integer siteCode) {
            this.siteCode = siteCode;
        }
    }
}
