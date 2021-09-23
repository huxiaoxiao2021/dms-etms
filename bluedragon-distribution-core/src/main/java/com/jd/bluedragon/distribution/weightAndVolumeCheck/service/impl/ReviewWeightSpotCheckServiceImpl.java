package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dao.ReviewWeightSpotCheckDao;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.bluedragon.utils.*;
import com.jd.ldop.utils.CollectionUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Enum.SpotCheckTypeEnum;
import com.jd.ql.dms.report.domain.ReviewSpotCheckDto;
import com.jd.ql.dms.report.domain.SpotCheckQueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.text.NumberFormat;
import java.util.*;

/**
 * @ClassName: ReviewWeightSpotCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/23 9:36
 */
@Service("reviewWeightSpotCheckService")
public class ReviewWeightSpotCheckServiceImpl implements ReviewWeightSpotCheckService {

    private Logger log = LoggerFactory.getLogger(ReviewWeightSpotCheckServiceImpl.class);

    @Autowired
    private ReviewWeightSpotCheckDao reviewWeightSpotCheckDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;


    /**
     * 获取导出数据
     * @param condition
     * @param bufferedWriter
     * @return
     */
    @Override
    public void getExportData(WeightAndVolumeCheckCondition condition, BufferedWriter bufferedWriter) {
       try {
             long start = System.currentTimeMillis();
               // 报表头
               Map<String, String> headerMap = getTheHeaderMap();
               //设置最大导出数量
               Integer MaxSize = exportConcurrencyLimitService.uccSpotCheckMaxSize();
               Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();

               //设置单次导出数量
               condition.setLimit(oneQuery);
               CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
               int queryTotal = 0;
               int index = 1;
               while (index <= (MaxSize/oneQuery)+1) {
                   condition.setOffset((index - 1) * oneQuery);
                   index++;
                   PagerResult<ReviewWeightSpotCheck> result = listData(condition);
                   if(result==null || CollectionUtils.isEmpty(result.getRows())){
                       break;
                   }
                   List<ReviewWeightSpotCheckExportDto> dataList = transForm(result.getRows());
                    // 输出至csv文件中
                   CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
                   // 限制导出数量
                   queryTotal += dataList.size();
                   if(queryTotal > MaxSize ){
                       break;
                   }
               }
               long end = System.currentTimeMillis();
               exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.REVIEW_WEIGHT_SPOT_CHECK_REPORT.getName(), end-start,queryTotal);
           }catch(Exception e){
                log.error("分拣复重抽检任务统计表 export error",e);
           }
       }

    private List<ReviewWeightSpotCheckExportDto> transForm(List<ReviewWeightSpotCheck> list) {
        List<ReviewWeightSpotCheckExportDto> dataList = new ArrayList<>();
        //表格信息
        for(ReviewWeightSpotCheck reviewWeightSpotCheck : list){
            ReviewWeightSpotCheckExportDto body = new ReviewWeightSpotCheckExportDto();
            body.setReviewDate(reviewWeightSpotCheck.getReviewDate() == null ? null : DateHelper.formatDate(reviewWeightSpotCheck.getReviewDate(), Constants.DATE_FORMAT));
            body.setReviewOrgName(reviewWeightSpotCheck.getReviewOrgName());
            body.setReviewMechanismType((reviewWeightSpotCheck.getReviewMechanismType()==null || reviewWeightSpotCheck.getReviewMechanismType()==-1)?"":reviewWeightSpotCheck.getReviewMechanismType()==1?"分拣中心":"转运中心");
            body.setReviewSiteName(reviewWeightSpotCheck.getReviewSiteName());
            body.setSpotCheckType(reviewWeightSpotCheck.getSpotCheckType()==0?"C网":"B网");
            body.setNormalPackageNum(reviewWeightSpotCheck.getNormalPackageNum());
            body.setNormalPackageNumOfActual(reviewWeightSpotCheck.getNormalPackageNumOfActual());
            body.setNormalCheckRate(reviewWeightSpotCheck.getNormalCheckRate());
            body.setNormalPackageNumOfDiff(reviewWeightSpotCheck.getNormalPackageNumOfDiff());
            body.setNormalCheckRateOfDiff(reviewWeightSpotCheck.getNormalCheckRateOfDiff());
            body.setTrustPackageNum(reviewWeightSpotCheck.getTrustPackageNum());
            body.setTrustPackageNumOfActual(reviewWeightSpotCheck.getTrustPackageNumOfActual());
            body.setTrustCheckRate(reviewWeightSpotCheck.getTrustCheckRate());
            body.setTrustPackageNumOfDiff(reviewWeightSpotCheck.getTrustPackageNumOfDiff());
            body.setTrustCheckRateOfDiff(reviewWeightSpotCheck.getTrustCheckRateOfDiff());
            body.setTotalCheckRate(reviewWeightSpotCheck.getTotalCheckRate());
            dataList.add(body);
        }
        return  dataList;
    }

    private Map<String, String> getTheHeaderMap() {
        Map<String,String> headerMap = new LinkedHashMap<>();
        //添加表头
        headerMap.put("reviewRate","复核日期");
        headerMap.put("reviewOrgName","复核区域");
        headerMap.put("reviewMechanismType","机构类型");
        headerMap.put("reviewSiteName","机构名称");
        headerMap.put("spotCheckType","业务类型");
        headerMap.put("normalPackageNum","普通应抽查运单数");
        headerMap.put("normalPackageNumOfActual","普通实际抽查运单数");
        headerMap.put("normalCheckRate","普通抽查率");
        headerMap.put("normalPackageNumOfDiff","普通抽查差异运单数");
        headerMap.put("normalCheckRateOfDiff","普通抽查差异率");
        headerMap.put("trustPackageNum","信任商家应抽查运单数");
        headerMap.put("trustPackageNumOfActual","信任商家实际抽查运单数");
        headerMap.put("trustCheckRate","信任商家抽查率");
        headerMap.put("trustPackageNumOfDiff","信任商家抽查差异运单数");
        headerMap.put("trustCheckRateOfDiff","信任商家抽查差异率");
        headerMap.put("totalCheckRate","总抽查率");
        return  headerMap;
    }

    /**
     * 导出抽查任务
     * @return
     */
    @Override
    public List<List<Object>> exportSpotData() {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("区域编码");
        heads.add("机构编码");
        heads.add("机构名称");
        heads.add("普通应抽查包裹数");
        heads.add("信任商家应抽查包裹数");
        heads.add("导入人ERP");
        heads.add("导入时间");
        resList.add(heads);
        List<SpotCheckInfo> list = reviewWeightSpotCheckDao.queryAllSpotInfo();
        //表格信息
        for(SpotCheckInfo spotCheckInfo : list){
            List<Object> body = Lists.newArrayList();
            body.add(spotCheckInfo.getOrgCode());
            body.add(spotCheckInfo.getSiteCode());
            body.add(spotCheckInfo.getSiteName());
            body.add(spotCheckInfo.getNormalPackageNum());
            body.add(spotCheckInfo.getTrustPackageNum());
            body.add(spotCheckInfo.getImportErp());
            body.add(DateHelper.formatDateTime(spotCheckInfo.getTs()));
            resList.add(body);
        }
        return  resList;
    }

    /**
     * 校验模板数据
     * @param dataList
     * @return
     */
    @Override
    public String checkExportData(List<SpotCheckExcelData> dataList, String importErpCode) {
        String errorString = "";
        if(dataList != null && dataList.size() > 0){
            if(dataList.size() > 1000){
                errorString = "导入数据超出1000条";
                return errorString;
            }
            int rowIndex = 1;
            Set<String> siteCodeSet = new HashSet<>();
            for(SpotCheckExcelData spotCheckInfo : dataList){
                siteCodeSet.add(StringHelper.getStringValue(spotCheckInfo.getSiteCode())+StringHelper.getStringValue(spotCheckInfo.getSpotCheckType()));
                spotCheckInfo.setImportErp(importErpCode);
                BaseStaffSiteOrgDto site = siteService.getSite(spotCheckInfo.getSiteCode());
                if(site == null){
                    errorString = "第"+ rowIndex +"行机构编码不存在!";
                    return errorString;
                }else{
                    if(!site.getSiteName().equals(spotCheckInfo.getSiteName())){
                        errorString = "第"+ rowIndex +"行机构编码与机构名称不匹配!";
                        return errorString;
                    }
                    if(!site.getOrgId().equals(spotCheckInfo.getOrgCode()) ){
                        errorString = "第"+ rowIndex +"行区域编码与机构编码不匹配!";
                        return errorString;
                    }
                }
                if(!(spotCheckInfo.getSpotCheckType().equals("B网")||spotCheckInfo.getSpotCheckType().equals("C网"))){
                    errorString = "第" + rowIndex + "行业务类型填写有误!";
                    return errorString;
                }
                rowIndex ++;
            }

            if(siteCodeSet.size() != dataList.size()){
                errorString = "导入的excel表格中存在相同的机构编码!";
                return errorString;
            }
            //抽查任务数据落库
            batchInsert(dataList);
        }else{
            errorString = "导入数据过多或者异常，请检查excel数据";
            return errorString;
        }
        return errorString;
    }

    /**
     * 批量插入数据
     * @param spotCheckExcelDataList
     */
    @Override
    public void batchInsert(List<SpotCheckExcelData> spotCheckExcelDataList) {
        //将Excel中的业务类型B网/C网转换为1/0
        List<SpotCheckInfo> dataList = convert2spotCheckInfo(spotCheckExcelDataList);
        for(SpotCheckInfo spotCheckInfo : dataList){
            SpotCheckInfo spotCheckCondition = new SpotCheckInfo();
            spotCheckCondition.setSiteCode(spotCheckInfo.getSiteCode());
            spotCheckCondition.setSpotCheckType(spotCheckInfo.getSpotCheckType());
            SpotCheckInfo newSpotCheckInfo = queryBySiteCode(spotCheckCondition);
            if(newSpotCheckInfo != null){
                //存在就更新
                updateBySiteCode(spotCheckInfo);
            }else{
                //不存在就新增
                insert(spotCheckInfo);
            }
        }
    }

    private List<SpotCheckInfo> convert2spotCheckInfo(List<SpotCheckExcelData> spotCheckExcelDataList){
        List<SpotCheckInfo> checkInfoList = new ArrayList<>();
        for(int i=0;i<spotCheckExcelDataList.size();i++){
            SpotCheckInfo spotCheckInfo = new SpotCheckInfo();
            spotCheckInfo.setImportErp(spotCheckExcelDataList.get(i).getImportErp());
            spotCheckInfo.setOrgCode(spotCheckExcelDataList.get(i).getOrgCode());
            spotCheckInfo.setSiteCode(spotCheckExcelDataList.get(i).getSiteCode());
            spotCheckInfo.setSiteName(spotCheckExcelDataList.get(i).getSiteName());

            if(spotCheckExcelDataList.get(i).getSpotCheckType().equals("C网")){
                spotCheckInfo.setSpotCheckType(SpotCheckTypeEnum.SPOT_CHECK_TYPE_C.getCode());
            }else if(spotCheckExcelDataList.get(i).getSpotCheckType().equals("B网")){
                spotCheckInfo.setSpotCheckType(SpotCheckTypeEnum.SPOT_CHECK_TYPE_B.getCode());
            }
            spotCheckInfo.setNormalPackageNum(spotCheckExcelDataList.get(i).getNormalPackageNum());
            spotCheckInfo.setTrustPackageNum(spotCheckExcelDataList.get(i).getTrustPackageNum());
            checkInfoList.add(spotCheckInfo);
        }
        return checkInfoList;
    }

    /**
     * 根据机构编码查询
     * @param spotCheckInfo
     */
    @Override
    public SpotCheckInfo queryBySiteCode(SpotCheckInfo spotCheckInfo) {
        return reviewWeightSpotCheckDao.queryBySiteCode(spotCheckInfo);
    }

    /**
     * 更新
     * @param spotCheckInfo
     */
    @Override
    public int updateBySiteCode(SpotCheckInfo spotCheckInfo) {
        return reviewWeightSpotCheckDao.updateBySiteCode(spotCheckInfo);
    }

    /**
     * 新增
     * @param spotCheckInfo
     */
    @Override
    public int insert(SpotCheckInfo spotCheckInfo) {
        return reviewWeightSpotCheckDao.insert(spotCheckInfo);
    }

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    @Override
    public PagerResult<ReviewWeightSpotCheck> listData(WeightAndVolumeCheckCondition condition) {
        PagerResult<ReviewWeightSpotCheck> result = new PagerResult<>();
        List<ReviewWeightSpotCheck> list = new ArrayList<>();
        try {
            List<SpotCheckInfo> spotCheckInfos = reviewWeightSpotCheckDao.queryByCondition(condition);
            if(spotCheckInfos.size() == 0){
                log.warn("未导入抽查任务!");
                result.setRows(new ArrayList<ReviewWeightSpotCheck>());
                result.setTotal(0);
                return result;
            }
            Map<String,SpotCheckInfo> map = new HashMap<>();
            SpotCheckQueryCondition spotCondition = convert2queryCondition(condition,spotCheckInfos,map);
            BaseEntity<List<ReviewSpotCheckDto>> entity = reportExternalService.getAllBySpotCheckCondition(spotCondition);
            if(entity == null || entity.getData() == null|| entity.getData().isEmpty()){
                result.setRows(new ArrayList<ReviewWeightSpotCheck>());
                result.setTotal(0);
                return result;
            }
            List<ReviewSpotCheckDto> data = entity.getData();
            for(ReviewSpotCheckDto dto : data){

                String reviewSiteCodeAndSpotCheckType = StringHelper.getStringValue(dto.getSpotCheckType()) + StringHelper.getStringValue(dto.getReviewSiteCode());
                SpotCheckInfo info = map.get(reviewSiteCodeAndSpotCheckType);
                map.remove(reviewSiteCodeAndSpotCheckType);
                if(info != null){
                    ReviewWeightSpotCheck reviewWeightSpotCheck = new ReviewWeightSpotCheck();

                    // 信任商家数据
                    Integer trustNumOfShould = info.getTrustPackageNum();           //信任商家应抽查运单数
                    Integer trustNumOfActual = dto.getTrustWaybillNumOfActual();    //信任商家实际抽查运单数量
                    Integer trustNumOfExcess = dto.getTrustWaybillNumOfDiff();      //信任商家超标数
                    // 普通商家数据
                    Integer normalNumOfShould = info.getNormalPackageNum();         //普通应抽查运单数
                    Integer normalNumOfActual = dto.getNormalWaybillNumOfActual();  //普通商家实际抽查运单数量
                    Integer normalNumOfExcess = dto.getNormalWaybillNumOfDiff();    //普通商家超标数

                    reviewWeightSpotCheck.setReviewDate(dto.getReviewDate());
                    BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(dto.getReviewSiteCode());
                    reviewWeightSpotCheck.setReviewOrgName(baseStaffSiteOrgDto == null?null:baseStaffSiteOrgDto.getOrgName());
                    reviewWeightSpotCheck.setReviewMechanismType(dto.getSpotCheckType());
                    reviewWeightSpotCheck.setSpotCheckType(dto.getSpotCheckType());
                    reviewWeightSpotCheck.setReviewSiteCode(dto.getReviewSiteCode());
                    reviewWeightSpotCheck.setReviewSiteName(info.getSiteName());

                    reviewWeightSpotCheck.setNormalPackageNum(normalNumOfShould);
                    reviewWeightSpotCheck.setNormalPackageNumOfActual(normalNumOfActual);
                    reviewWeightSpotCheck.setNormalCheckRate(normalNumOfShould==null?null:convertPercentage(normalNumOfActual,normalNumOfShould));
                    reviewWeightSpotCheck.setNormalPackageNumOfDiff(normalNumOfExcess);
                    reviewWeightSpotCheck.setNormalCheckRateOfDiff(convertPercentage(normalNumOfExcess,normalNumOfActual));

                    reviewWeightSpotCheck.setTrustPackageNum(trustNumOfShould);
                    reviewWeightSpotCheck.setTrustPackageNumOfActual(trustNumOfActual);
                    reviewWeightSpotCheck.setTrustCheckRate(trustNumOfShould==null?null:convertPercentage(trustNumOfActual,trustNumOfShould));
                    reviewWeightSpotCheck.setTrustPackageNumOfDiff(trustNumOfExcess);
                    reviewWeightSpotCheck.setTrustCheckRateOfDiff(convertPercentage(trustNumOfExcess,trustNumOfActual));

                    reviewWeightSpotCheck.setTotalCheckRate(convertPercentage((IntegerHelper.integerToInt(trustNumOfActual) + IntegerHelper.integerToInt(normalNumOfActual)), (IntegerHelper.integerToInt(trustNumOfShould) + IntegerHelper.integerToInt(normalNumOfShould))));

                    list.add(reviewWeightSpotCheck);
                }
            }
            if(map.size()>0 && condition.getCreateSiteCode()==null){
                for (String reviewSiteCodeAndSpotCheckType :map.keySet()){
                    SpotCheckInfo info = map.get(reviewSiteCodeAndSpotCheckType);
                    ReviewWeightSpotCheck reviewWeightSpotCheck = new ReviewWeightSpotCheck();
                    Date reviewDate = data.get(0).getReviewDate();
                    reviewWeightSpotCheck.setReviewDate(reviewDate);
                    BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(info.getSiteCode());
                    reviewWeightSpotCheck.setReviewOrgName(baseStaffSiteOrgDto == null?null:baseStaffSiteOrgDto.getOrgName());
                    reviewWeightSpotCheck.setReviewMechanismType(info.getSpotCheckType());
                    reviewWeightSpotCheck.setSpotCheckType(info.getSpotCheckType());
                    reviewWeightSpotCheck.setReviewSiteCode(info.getSiteCode());
                    reviewWeightSpotCheck.setReviewSiteName(info.getSiteName());

                    reviewWeightSpotCheck.setNormalPackageNum(info.getNormalPackageNum());
                    reviewWeightSpotCheck.setNormalPackageNumOfActual(0);
                    reviewWeightSpotCheck.setNormalCheckRate("0%");
                    reviewWeightSpotCheck.setNormalPackageNumOfDiff(0);
                    reviewWeightSpotCheck.setNormalCheckRateOfDiff("0%");

                    reviewWeightSpotCheck.setTrustPackageNum(info.getTrustPackageNum());
                    reviewWeightSpotCheck.setTrustPackageNumOfActual(0);
                    reviewWeightSpotCheck.setTrustCheckRate("0%");
                    reviewWeightSpotCheck.setTrustPackageNumOfDiff(0);
                    reviewWeightSpotCheck.setTrustCheckRateOfDiff("0%");

                    reviewWeightSpotCheck.setTotalCheckRate("0%");

                    list.add(reviewWeightSpotCheck);
                }
            }
            result.setRows(list);
            result.setTotal(list.size());
        }catch (Exception e){
            log.error("查询失败!",e);
            result.setRows(new ArrayList<ReviewWeightSpotCheck>());
            result.setTotal(0);
        }
        return result;
    }

    private SpotCheckQueryCondition convert2queryCondition(WeightAndVolumeCheckCondition weightAndVolumeCheckCondition,
                                                           List<SpotCheckInfo> spotCheckInfos, Map<String, SpotCheckInfo> map) {
        List<Integer> siteCodes = new ArrayList<>();
        for(SpotCheckInfo spotCheckInfo : spotCheckInfos){
            siteCodes.add(spotCheckInfo.getSiteCode());
            map.put(StringHelper.getStringValue(spotCheckInfo.getSpotCheckType()) + StringHelper.getStringValue(spotCheckInfo.getSiteCode()),spotCheckInfo);
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setStartTime(weightAndVolumeCheckCondition.getReviewStartTime());
        condition.setEndTime(weightAndVolumeCheckCondition.getReviewEndTime());
        condition.setReviewSiteCodes(siteCodes);
        condition.setSpotCheckType(weightAndVolumeCheckCondition.getSpotCheckType());
        return condition;
    }

    /**
     * 转换成百分比
     * @param num1
     * @param num2
     * @return
     */
    private String convertPercentage(int num1,int num2){
        if(num2 == 0){
            return "0";
        }
        float num = (float)num1/num2;
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMinimumFractionDigits(2);
        String format = percentInstance.format(num);
        return format;
    }

}
