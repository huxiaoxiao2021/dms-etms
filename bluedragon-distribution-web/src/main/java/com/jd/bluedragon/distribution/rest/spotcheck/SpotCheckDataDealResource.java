package com.jd.bluedragon.distribution.rest.spotcheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
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

    /**
     * 从excel获取包裹号修复抽检无图片问题
     *
     * @param excelName
     * @return
     */
    @POST
    @Path("/spotCheck/repairPicUrlFromExcel/{excelName}")
    public InvokeResult<Boolean> repairPicUrlFromExcel(@PathParam("excelName") String excelName){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
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
     *
     * @param packMap
     * @return
     */
    @POST
    @Path("/spotCheck/repairPicUrl")
    public InvokeResult<Boolean> repairPicUrl(Map<String, Object> packMap){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();

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
            if(Objects.equals(accordList.size(), Constants.CONSTANT_NUMBER_ONE) || !Objects.equals(accordList.get(0).getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName())){
                // 运单维度抽检 | 非dws抽检 则不处理
                continue;
            }
            // 包裹维度抽检
            for (WeightVolumeCollectDto collectDto : accordList) {
                if(Objects.equals(collectDto.getRecordType(), SpotCheckRecordTypeEnum.WAYBILL.getCode())){
                    // 只处理包裹维度数据（汇总后的记录不处理）
                    continue;
                }
                String packageCode = collectDto.getPackageCode();
                if(StringUtils.isNotEmpty(collectDto.getPictureAddress())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "查询到图片已更新到抽检记录");
                    continue;
                }
                // 查看图片是否上传
                InvokeResult<String> searchPicResult = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);
                if(StringUtils.isEmpty(searchPicResult.getData())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "未查询图片");
                    continue;
                }
                // 待更新图片collect
                WeightVolumeCollectDto updateCollect = new WeightVolumeCollectDto();
                updateCollect.setPackageCode(packageCode);
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
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setPackageCode(packageCode);
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            List<WeightVolumeCollectDto> accordList = reportExternalManager.queryByCondition(condition);

            boolean packIsWaybillCode = false;
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            if(CollectionUtils.isEmpty(accordList)){
                logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "未查询到超标抽检数据");
                condition.setPackageCode(waybillCode);
                accordList = reportExternalManager.queryByCondition(condition);
                if(CollectionUtils.isEmpty(accordList)){
                    logger.warn("根据运单号：" + waybillCode + "站点：" + siteCode + "未查询到超标抽检数据");
                    continue;
                }
                if(StringUtils.isNotEmpty(accordList.get(0).getPictureAddress())){
                    logger.warn("根据运单号：" + waybillCode + "站点：" + siteCode + "查询到图片已更新到抽检记录");
                    continue;
                }
                packIsWaybillCode = true;
            }else {
                if(StringUtils.isNotEmpty(accordList.get(0).getPictureAddress())){
                    logger.warn("根据包裹号：" + packageCode + "站点：" + siteCode + "查询到图片已更新到抽检记录");
                    continue;
                }
            }
            // 待更新图片collect
            WeightVolumeCollectDto updateCollect = new WeightVolumeCollectDto();
            updateCollect.setPackageCode(packIsWaybillCode ? waybillCode : packageCode);
            updateCollect.setReviewSiteCode(siteCode);
            updateCollect.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            updateCollect.setPictureAddress(searchPicResult.getData());
            reportExternalManager.insertOrUpdateForWeightVolume(updateCollect);
        }
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
