package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dao.ReviewWeightSpotCheckDao;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
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

    private Logger logger = Logger.getLogger(ReviewWeightSpotCheckServiceImpl.class);

    @Autowired
    private ReviewWeightSpotCheckDao reviewWeightSpotCheckDao;

    @Autowired
    private SiteService siteService;

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
        condition.setLimit(-1);
        PagerResult<ReviewWeightSpotCheck> result = listData(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<ReviewWeightSpotCheck> list = result.getRows();
            //表格信息
            for(ReviewWeightSpotCheck reviewWeightSpotCheck : list){
                List<Object> body = Lists.newArrayList();
                body.add(reviewWeightSpotCheck.getReviewDate() == null ? null : DateHelper.formatDate(reviewWeightSpotCheck.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(reviewWeightSpotCheck.getReviewOrgName());
                body.add(reviewWeightSpotCheck.getReviewMechanismType()==1?"分拣中心":"转运中心");
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

    private List<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition) {
        //TODO 6.第二个页面导出从es中获取数据
        //从es中获取数据(根据复核日期和分拣中心id获取数据)
//        List<WeightAndVolumeCheck>  result = queryFromEsByCondition(condition);
        List<WeightAndVolumeCheck>  result = new ArrayList<>();
        return result;
    }

    private Integer queryNumByCondition(WeightAndVolumeCheckCondition condition) {
        //TODO 7.第二个页面导出从es中获取数据总数
        //从es中获取数据(根据复核日期和分拣中心id获取数据)
//        Integer num = queryFromEsByCondition(condition);
        Integer num = 3;
        return num;
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
     * @return
     */
    @Override
    public PagerResult<ReviewWeightSpotCheck> listData(WeightAndVolumeCheckCondition condition) {

        //TODO 5.第二个页面查询从es中获取数据
        PagerResult<ReviewWeightSpotCheck>  result = new PagerResult<>();
        List<ReviewWeightSpotCheck> list = new ArrayList<>();

        //1.从es中获取所有数据
        List<WeightAndVolumeCheck> resultFromES = queryByCondition(condition);
        Integer num = queryNumByCondition(condition);

        //2.根据复核日期、站点分组（复核日期转换成yyyy-mm-dd）
        Map<Date,Map<Integer,List<WeightAndVolumeCheck>>> map = convertAndGroup(resultFromES);

        //3.将步骤1的数据和步骤2的数据结合生成result返回给前台
        if(map != null && map.size() > 0){

            for(Date date : map.keySet()){

                Map<Integer, List<WeightAndVolumeCheck>> mapOfSite = map.get(date);
                for(Integer siteCode : mapOfSite.keySet()){

                    //站点对应的复核总记录
                    List<WeightAndVolumeCheck> weightAndVolumeChecks = mapOfSite.get(siteCode);
                    //查询站点的抽查任务数据
                    SpotCheckInfo spotCheckInfo = reviewWeightSpotCheckDao.queryBySiteCode(siteCode);

                    WeightAndVolumeCheck weightAndVolumeCheck = weightAndVolumeChecks.get(0);

                    Integer trustNumOfActual = getPackageNumOfSpotCheck(weightAndVolumeChecks,1);//信任商家实际抽查包裹数量
                    Integer normalNumOfActual = getPackageNumOfSpotCheck(weightAndVolumeChecks,0);//普通商家实际抽查包裹数量
                    Integer trustNumOfShould = spotCheckInfo==null?null:spotCheckInfo.getTrustPackageNum();              //信任商家应抽查包裹数
                    Integer normalNumOfShould = spotCheckInfo==null?null:spotCheckInfo.getNormalPackageNum();            //普通应抽查包裹数
                    Integer trustNumOfExcess = getPackageNumOfExcess(weightAndVolumeChecks,1);     //信任商家超标数
                    Integer normalNumOfExcess = getPackageNumOfExcess(weightAndVolumeChecks,0);    //普通商家超标数

                    ReviewWeightSpotCheck reviewWeightSpotCheck = new ReviewWeightSpotCheck();
                    reviewWeightSpotCheck.setReviewDate(date);
                    reviewWeightSpotCheck.setReviewOrgName(weightAndVolumeCheck.getReviewOrg());//复核区域
                    reviewWeightSpotCheck.setReviewMechanismType(weightAndVolumeCheck.getMechanismType());//机构类型
                    reviewWeightSpotCheck.setReviewSiteCode(weightAndVolumeCheck.getReviewCreateSiteCode());//机构名称
                    reviewWeightSpotCheck.setReviewSiteName(weightAndVolumeCheck.getReviewCreateSiteName());//机构名称
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

        result.setRows(list);
        result.setTotal(num);

        return result;
    }



    /**
     * 根据复核日期、站点进行分组
     * @param weightAndVolumeChecks
     * @return
     */
    private Map<Date, Map<Integer, List<WeightAndVolumeCheck>>> convertAndGroup(List<WeightAndVolumeCheck> weightAndVolumeChecks) {

        Map<Date, Map<Integer, List<WeightAndVolumeCheck>>> map = new HashMap<>();
        //1.按日期分组
        Map<Date,List<WeightAndVolumeCheck>> mapOfDate = new HashMap<>();
        for(WeightAndVolumeCheck weightAndVolumeCheck : weightAndVolumeChecks){
            //将日期格式转换成yyyy-MM-dd类型
            weightAndVolumeCheck.setReviewDate(DateHelper.parseDate(DateHelper.formatDate(weightAndVolumeCheck.getReviewDate())));
            if(!mapOfDate.containsKey(weightAndVolumeCheck.getReviewDate())){

                List list = new ArrayList<WeightAndVolumeCheck>();
                list.add(weightAndVolumeCheck);
                mapOfDate.put(weightAndVolumeCheck.getReviewDate(),list);

            }else{
                mapOfDate.get(weightAndVolumeCheck.getReviewDate()).add(weightAndVolumeCheck);
            }
        }

        //2.按站点分组
        Map<Date,Map<String,List<WeightAndVolumeCheck>>> mapOfDateAndSite = new HashMap<>();
        for(Date date : mapOfDate.keySet()){

            //日期下的所有集合
            List<WeightAndVolumeCheck> weightAndVolumeChecks1 = mapOfDate.get(date);
            for(WeightAndVolumeCheck weightAndVolumeCheck : weightAndVolumeChecks1){

                if(!map.get(date).containsKey(weightAndVolumeCheck.getReviewCreateSiteCode())){

                    Map<Integer, List<WeightAndVolumeCheck>> mapOfSite = new HashMap<>();
                    List list = new ArrayList<WeightAndVolumeCheck>();
                    list.add(weightAndVolumeCheck);
                    mapOfSite.put(weightAndVolumeCheck.getReviewCreateSiteCode(),list);
                    map.put(date,mapOfSite);

                }else{
                    map.get(date).get(weightAndVolumeCheck.getReviewCreateSiteCode()).add(weightAndVolumeCheck);
                }

            }

        }

        return map;
    }

    /**
     * 获取信任/普通商家包裹数量
     * @param weightAndVolumeChecks
     * @param type 1:信任商家 0:普通商家
     * @return
     */
    private Integer getPackageNumOfSpotCheck(List<WeightAndVolumeCheck> weightAndVolumeChecks,int type) {
        int count = 0;
        for(WeightAndVolumeCheck weightAndVolumeCheck : weightAndVolumeChecks){
            if(weightAndVolumeCheck.getIsTrustBusiName() == type){
                count++;
            }
        }
        return count;
    }

    /**
     * 获取信任/普通商家超标包裹数
     * @param weightAndVolumeChecks
     * @param type 1:信任商家 0:普通商家
     * @return
     */
    private Integer getPackageNumOfExcess(List<WeightAndVolumeCheck> weightAndVolumeChecks, int type) {
        int count = 0;
        for(WeightAndVolumeCheck weightAndVolumeCheck : weightAndVolumeChecks){
            if(weightAndVolumeCheck.getIsTrustBusiName() == type && weightAndVolumeCheck.getIsExcess() == 1){
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
        float num = (float)num1/num2;
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMinimumFractionDigits(2);
        String format = percentInstance.format(num);
        return format;
    }

}
