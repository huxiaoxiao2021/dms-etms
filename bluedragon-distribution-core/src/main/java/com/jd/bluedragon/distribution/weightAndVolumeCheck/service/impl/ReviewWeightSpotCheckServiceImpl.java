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
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        PagerResult<ReviewWeightSpotCheck> result = queryByCondition(condition);
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

    private PagerResult<ReviewWeightSpotCheck> queryByCondition(WeightAndVolumeCheckCondition condition) {
        //TODO 从es中获取数据

        return null;
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
        PagerResult<ReviewWeightSpotCheck>  result = new PagerResult<ReviewWeightSpotCheck>();
        //1.根据查询条件获取分拣复重抽检任务数据集合
        List<SpotCheckInfo> list = reviewWeightSpotCheckDao.queryByCondition(condition);

        //TODO 2.从es中获取数据
        PagerResult<ReviewWeightSpotCheck> resultFromES = queryByCondition(condition);

        //TODO 3.将步骤1的数据和步骤2的数据结合生成result返回


        /*Integer num = queryNumByCondition(condition);
        result.setRows(list);
        result.setTotal(num);*/
        return result;
    }

}
