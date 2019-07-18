package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dao.ReviewWeightSpotCheckDao;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.ReviewSpotCheckDto;
import com.jd.ql.dms.report.domain.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    private Logger logger = Logger.getLogger(ReviewWeightSpotCheckServiceImpl.class);

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
        heads.add("普通应抽查包裹数");
        heads.add("普通实际抽查包裹数");
        heads.add("普通抽查率");
        heads.add("普通抽查差异包裹数");
        heads.add("普通抽查差异率");
        heads.add("信任商家应抽查包裹数");
        heads.add("信任商家实际抽查包裹数");
        heads.add("信任商家抽查率");
        heads.add("信任商家抽查差异包裹数");
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
     * 查询条件转换
     * */
    private WeightVolumeQueryCondition transform(WeightAndVolumeCheckCondition condition) {
        WeightVolumeQueryCondition newCondition = new WeightVolumeQueryCondition();
        newCondition.setStartTime(DateHelper.parseDateTime(condition.getStartTime()));
        newCondition.setEndTime(DateHelper.parseDateTime(condition.getEndTime()));
        newCondition.setReviewOrgCode(condition.getReviewOrgCode()==null?null:condition.getReviewOrgCode().intValue());
        newCondition.setReviewSiteCode(condition.getCreateSiteCode()==null?null:condition.getCreateSiteCode().intValue());
        newCondition.setIsExcess(condition.getIsExcess());
        return newCondition;
    }


    /**
     * 校验模板数据
     * @param dataList
     * @return
     */
    @Override
    public String checkExportData(List<SpotCheckInfo> dataList,String importErpCode) {
        String errorString = "";
        if(dataList != null && dataList.size() > 0){
            if(dataList.size() > 1000){
                errorString = "导入数据超出1000条";
                return errorString;
            }
            int rowIndex = 1;
            Set<Integer> siteCodeSet = new HashSet<>();
            for(SpotCheckInfo spotCheckInfo : dataList){
                siteCodeSet.add(spotCheckInfo.getSiteCode());
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
     * @param dataList
     */
    @Override
    public void batchInsert(List<SpotCheckInfo> dataList) {
        for(SpotCheckInfo spotCheckInfo : dataList){
            SpotCheckInfo newSpotCheckInfo = queryBySiteCode(spotCheckInfo.getSiteCode());
            if(newSpotCheckInfo != null){
                //存在就更新
                updateBySiteCode(spotCheckInfo);
            }else{
                //不存在就新增
                insert(spotCheckInfo);
            }
        }
    }

    /**
     * 根据机构编码查询
     * @param siteCode
     */
    @Override
    public SpotCheckInfo queryBySiteCode(Integer siteCode) {
        return reviewWeightSpotCheckDao.queryBySiteCode(siteCode);
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
     * @param type 0:查询 1:导出
     * @return
     */
    @Override
    public PagerResult<ReviewWeightSpotCheck> listData(WeightAndVolumeCheckCondition condition,int type) {

        PagerResult<ReviewWeightSpotCheck>  result = new PagerResult<>();
        List<ReviewWeightSpotCheck> list = new ArrayList<>();

        //1.从es中获取所有数据
        List<WeightVolumeCollectDto> data = Collections.EMPTY_LIST;
        WeightVolumeQueryCondition transform = transform(condition);
        try{
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(transform);
            if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().size() > 0){
                data = baseEntity.getData();
            }
        }catch (Exception e){
            logger.error("查询es获取数据失败，"+ JsonHelper.toJson(condition));
        }


        //2.根据复核日期、站点分组（复核日期转换成yyyy-mm-dd）
        Map<Date,Map<Integer,List<WeightVolumeCollectDto>>> map = convertAndGroup(data);

        //3.将步骤1的数据和步骤2的数据结合生成result返回给前台
        if(map != null && map.size() > 0){

            for(Date date : map.keySet()){

                Map<Integer, List<WeightVolumeCollectDto>> mapOfSite = map.get(date);
                for(Integer siteCode : mapOfSite.keySet()){

                    //站点对应的复核总记录
                    List<WeightVolumeCollectDto> weightVolumeCollectDtos = mapOfSite.get(siteCode);
                    //查询站点的抽查任务数据
                    SpotCheckInfo spotCheckInfo = reviewWeightSpotCheckDao.queryBySiteCode(siteCode);

                    WeightVolumeCollectDto weightVolumeCollectDto = weightVolumeCollectDtos.get(0);
                    Integer trustNumOfActual = getPackageNumOfSpotCheck(weightVolumeCollectDtos,1);//信任商家实际抽查包裹数量
                    Integer normalNumOfActual = getPackageNumOfSpotCheck(weightVolumeCollectDtos,0);//普通商家实际抽查包裹数量
                    Integer trustNumOfShould = spotCheckInfo==null?null:spotCheckInfo.getTrustPackageNum();              //信任商家应抽查包裹数
                    Integer normalNumOfShould = spotCheckInfo==null?null:spotCheckInfo.getNormalPackageNum();            //普通应抽查包裹数
                    Integer trustNumOfExcess = getPackageNumOfExcess(weightVolumeCollectDtos,1);     //信任商家超标数
                    Integer normalNumOfExcess = getPackageNumOfExcess(weightVolumeCollectDtos,0);    //普通商家超标数

                    ReviewWeightSpotCheck reviewWeightSpotCheck = new ReviewWeightSpotCheck();
                    reviewWeightSpotCheck.setReviewDate(date);
                    reviewWeightSpotCheck.setReviewOrgName(weightVolumeCollectDto.getReviewOrgName());//复核区域
                    reviewWeightSpotCheck.setReviewMechanismType(weightVolumeCollectDto.getReviewSubType());//机构类型
                    reviewWeightSpotCheck.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());//机构名称
                    reviewWeightSpotCheck.setReviewSiteName(weightVolumeCollectDto.getReviewSiteName());//机构名称
                    reviewWeightSpotCheck.setNormalPackageNum(normalNumOfShould);                          //普通应抽查包裹数
                    reviewWeightSpotCheck.setNormalPackageNumOfActual(normalNumOfActual);                  //普通实际抽查包裹数
                    reviewWeightSpotCheck.setNormalCheckRate(normalNumOfShould==null?null:convertPercentage(normalNumOfActual,normalNumOfShould));        //普通抽查率
                    reviewWeightSpotCheck.setNormalPackageNumOfDiff(normalNumOfExcess);                    //普通抽查差异包裹数(超标)
                    reviewWeightSpotCheck.setNormalCheckRateOfDiff(convertPercentage(normalNumOfExcess,normalNumOfActual));   //普通抽查差异率
                    reviewWeightSpotCheck.setTrustPackageNum(trustNumOfShould);                            //信任商家应抽查包裹数
                    reviewWeightSpotCheck.setTrustPackageNumOfActual(trustNumOfActual);                    //信任商家实际抽查包裹数
                    reviewWeightSpotCheck.setTrustCheckRate(trustNumOfShould==null?null:convertPercentage(trustNumOfActual,trustNumOfShould));            //信任商家抽查率
                    reviewWeightSpotCheck.setTrustPackageNumOfDiff(trustNumOfExcess);                      //信任商家抽查差异包裹数(超标数)
                    reviewWeightSpotCheck.setTrustCheckRateOfDiff(convertPercentage(trustNumOfExcess,trustNumOfActual));      //信任商家抽查差异率
                    reviewWeightSpotCheck.setTotalCheckRate(spotCheckInfo==null?null:convertPercentage((trustNumOfActual+normalNumOfActual),(trustNumOfShould+normalNumOfShould)));                 //总抽查率

                    list.add(reviewWeightSpotCheck);

                }

            }

        }

        if(type == 0){
            //分页查询
            List<ReviewWeightSpotCheck> listOfReturn = new ArrayList<>();
            int limit = condition.getLimit();
            int offset = condition.getOffset();
            int start = offset;
            int end = limit+offset;
            for (int i = start; i < (list.size()<end?list.size():end); i++) {
                listOfReturn.add(list.get(i));
            }
            result.setRows(listOfReturn);
        }else{
            //导出
            result.setRows(list);
        }
        result.setTotal(list.size());

        return result;
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
                logger.warn("未导入抽查任务!");
                result.setRows(new ArrayList<ReviewWeightSpotCheck>());
                result.setTotal(0);
                return result;
            }
            SpotCheckQueryCondition spotCondition = convert2queryCondition(condition,spotCheckInfos);
            BaseEntity<List<ReviewSpotCheckDto>> entity = reportExternalService.getAllBySpotCheckCondition(spotCondition);
            List<ReviewSpotCheckDto> data = entity.getData();
            for(ReviewSpotCheckDto dto : data){

                for(SpotCheckInfo info : spotCheckInfos){
                    if(dto.getReviewSiteCode().equals(info.getSiteCode())){

                        ReviewWeightSpotCheck reviewWeightSpotCheck = new ReviewWeightSpotCheck();

                        Integer trustNumOfActual = dto.getTrustPackageNumOfActual();    //信任商家实际抽查包裹数量
                        Integer normalNumOfActual = dto.getNormalPackageNumOfActual();  //普通商家实际抽查包裹数量
                        Integer trustNumOfShould = info.getTrustPackageNum();           //信任商家应抽查包裹数
                        Integer normalNumOfShould = info.getNormalPackageNum();         //普通应抽查包裹数
                        Integer trustNumOfExcess = dto.getTrustPackageNumOfDiff();      //信任商家超标数
                        Integer normalNumOfExcess = dto.getNormalPackageNumOfDiff();    //普通商家超标数

                        reviewWeightSpotCheck.setReviewDate(dto.getReviewDate());
                        BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(dto.getReviewSiteCode());
                        reviewWeightSpotCheck.setReviewOrgName(baseStaffSiteOrgDto == null?null:baseStaffSiteOrgDto.getOrgName());
                        reviewWeightSpotCheck.setReviewMechanismType(1);
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
                        reviewWeightSpotCheck.setTotalCheckRate(convertPercentage((trustNumOfActual+normalNumOfActual),(trustNumOfShould+normalNumOfShould)));

                        list.add(reviewWeightSpotCheck);
                    }
                }
            }

            result.setRows(list);
            result.setTotal(list.size());

        }catch (Exception e){
            logger.error("查询失败!",e);
            result.setRows(new ArrayList<ReviewWeightSpotCheck>());
            result.setTotal(0);
        }

        return result;
    }

    private SpotCheckQueryCondition convert2queryCondition(WeightAndVolumeCheckCondition weightAndVolumeCheckCondition, List<SpotCheckInfo> spotCheckInfos) {
        List<Integer> siteCodes = new ArrayList<>();
        for(SpotCheckInfo spotCheckInfo : spotCheckInfos){
            siteCodes.add(spotCheckInfo.getSiteCode());
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setStartTime(weightAndVolumeCheckCondition.getReviewStartTime());
        condition.setEndTime(weightAndVolumeCheckCondition.getReviewEndTime());
        condition.setReviewSiteCodes(siteCodes);
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
