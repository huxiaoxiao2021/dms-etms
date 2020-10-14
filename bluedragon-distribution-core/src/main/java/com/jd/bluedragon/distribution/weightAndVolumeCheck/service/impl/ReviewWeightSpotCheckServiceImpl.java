package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckExcelData;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dao.ReviewWeightSpotCheckDao;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.ReviewSpotCheckDto;
import com.jd.ql.dms.report.domain.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 获取导出数据
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("复核区域");
        heads.add("机构类型");
        heads.add("机构名称");
        heads.add("业务类型");
        heads.add("普通应抽查运单数");
        heads.add("普通实际抽查运单数");
        heads.add("普通抽查率");
        heads.add("普通抽查差异运单数");
        heads.add("普通抽查差异率");
        heads.add("信任商家应抽查运单数");
        heads.add("信任商家实际抽查运单数");
        heads.add("信任商家抽查率");
        heads.add("信任商家抽查差异运单数");
        heads.add("信任商家抽查差异率");
        heads.add("总抽查率");
        resList.add(heads);
        PagerResult<ReviewWeightSpotCheck> result = listData(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<ReviewWeightSpotCheck> list = result.getRows();
            //表格信息
            for(ReviewWeightSpotCheck reviewWeightSpotCheck : list){
                List<Object> body = Lists.newArrayList();
                body.add(reviewWeightSpotCheck.getReviewDate() == null ? null : DateHelper.formatDate(reviewWeightSpotCheck.getReviewDate(), Constants.DATE_FORMAT));
                body.add(reviewWeightSpotCheck.getReviewOrgName());
                body.add((reviewWeightSpotCheck.getReviewMechanismType()==null || reviewWeightSpotCheck.getReviewMechanismType()==-1)?"":reviewWeightSpotCheck.getReviewMechanismType()==1?"分拣中心":"转运中心");
                body.add(reviewWeightSpotCheck.getReviewSiteName());
                body.add(reviewWeightSpotCheck.getSpotCheckType()==0?"C网":"B网");
                body.add(reviewWeightSpotCheck.getNormalPackageNum());
                body.add(reviewWeightSpotCheck.getNormalPackageNumOfActual());
                body.add(reviewWeightSpotCheck.getNormalCheckRate());
                body.add(reviewWeightSpotCheck.getNormalPackageNumOfDiff());
                body.add(reviewWeightSpotCheck.getNormalCheckRateOfDiff());
                body.add(reviewWeightSpotCheck.getTrustPackageNum());
                body.add(reviewWeightSpotCheck.getTrustPackageNumOfActual());
                body.add(reviewWeightSpotCheck.getTrustCheckRate());
                body.add(reviewWeightSpotCheck.getTrustPackageNumOfDiff());
                body.add(reviewWeightSpotCheck.getTrustCheckRateOfDiff());
                body.add(reviewWeightSpotCheck.getTotalCheckRate());
                resList.add(body);
            }
        }
        return  resList;
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
                spotCheckInfo.setSpotCheckType(0);
            }else if(spotCheckExcelDataList.get(i).getSpotCheckType().equals("B网")){
                spotCheckInfo.setSpotCheckType(1);
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
     * 根据复核日期、站点进行分组
     * @param weightVolumeCollectDtos
     * @return
     */
    private Map<Date, Map<Integer, List<WeightVolumeCollectDto>>> convertAndGroup(List<WeightVolumeCollectDto> weightVolumeCollectDtos) {


        //1.按日期分组
        Map<Date,List<WeightVolumeCollectDto>> mapOfDate = new HashMap<>();
        for(WeightVolumeCollectDto weightVolumeCollectDto : weightVolumeCollectDtos){
            //将日期格式转换成yyyy-MM-dd类型
            weightVolumeCollectDto.setReviewDate(DateHelper.parseDate(DateHelper.formatDate(weightVolumeCollectDto.getReviewDate())));
            if(!mapOfDate.containsKey(weightVolumeCollectDto.getReviewDate())){

                List list = new ArrayList<WeightVolumeCollectDto>();
                list.add(weightVolumeCollectDto);
                mapOfDate.put(weightVolumeCollectDto.getReviewDate(),list);

            }else{
                mapOfDate.get(weightVolumeCollectDto.getReviewDate()).add(weightVolumeCollectDto);
            }
        }

        //2.按站点分组
        Map<Date, Map<Integer, List<WeightVolumeCollectDto>>> map = new HashMap<>();
        for(Date date : mapOfDate.keySet()){

            //日期下的所有集合
            List<WeightVolumeCollectDto> weightVolumeCollectDto1 = mapOfDate.get(date);
            for(WeightVolumeCollectDto weightVolumeCollectDto : weightVolumeCollectDto1){

                if(!map.containsKey(date)){

                    Map<Integer, List<WeightVolumeCollectDto>> mapOfSite = new HashMap<>();
                    List list = new ArrayList<WeightVolumeCollectDto>();
                    list.add(weightVolumeCollectDto);
                    mapOfSite.put(weightVolumeCollectDto.getReviewSiteCode(),list);
                    map.put(date,mapOfSite);

                }else{

                    if(!map.get(date).containsKey(weightVolumeCollectDto.getReviewSiteCode())){

                        List<WeightVolumeCollectDto> list = new ArrayList<>();
                        list.add(weightVolumeCollectDto);
                        map.get(date).put(weightVolumeCollectDto.getReviewSiteCode(),list);
                    }else{
                        map.get(date).get(weightVolumeCollectDto.getReviewSiteCode()).add(weightVolumeCollectDto);
                    }
                }

            }

        }

        return map;
    }

    /**
     * 获取信任/普通商家包裹数量
     * @param weightVolumeCollectDtos
     * @param type 1:信任商家 0:普通商家
     * @return
     */
    private Integer getPackageNumOfSpotCheck(List<WeightVolumeCollectDto> weightVolumeCollectDtos,int type) {
        int count = 0;
        for(WeightVolumeCollectDto weightVolumeCollectDto : weightVolumeCollectDtos){
            if(weightVolumeCollectDto.getIsTrustBusi() == type){
                count++;
            }
        }
        return count;
    }

    /**
     * 获取信任/普通商家超标包裹数
     * @param weightVolumeCollectDtos
     * @param type 1:信任商家 0:普通商家
     * @return
     */
    private Integer getPackageNumOfExcess(List<WeightVolumeCollectDto> weightVolumeCollectDtos, int type) {
        int count = 0;
        for(WeightVolumeCollectDto weightVolumeCollectDto : weightVolumeCollectDtos){
            if(weightVolumeCollectDto.getIsTrustBusi() == type && weightVolumeCollectDto.getIsExcess() == 1){
                count++;
            }
        }
        return count;
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
