package com.jd.bluedragon.quartz;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @ClassName: WeightAndVolumeSpotCheckJob
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/26 18:34
 */
@Service
public class WeightAndVolumeSpotCheckJob implements Job {

    private static final Log logger = LogFactory.getLog(WeightAndVolumeSpotCheckJob.class);

    /** 称重计费分页初始值 */
    private static final Integer PAGESIZE = 5000;
    private static final Integer PAGENO = 1;

    @Autowired
    private ReportExternalService reportExternalService;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    @Qualifier("dmsWeightVolumeAbnormal")
    private DefaultJMQProducer dmsWeightVolumeAbnormal;


    /**
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            int total = 0;
            //1.查询es获取所有超标且无图片链接的记录
            BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity = getPagerBaseEntity(PAGENO);
            if(baseEntity != null && baseEntity.getData() != null
                    && baseEntity.getData().getData() != null && baseEntity.getData().getTotal() != null){
                total = baseEntity.getData().getTotal().intValue();
                List<WeightVolumeCollectDto> list = baseEntity.getData().getData();
                updateAndSendMq(list);
            }
            if(total > PAGESIZE){
                int count;
                if(total % PAGESIZE != 0){
                    count = total/PAGESIZE + 1;
                }else {
                    count = total/PAGESIZE;
                }
                for (int i = 1; i < count; i++) {
                    int pageNo = i+1;
                    BaseEntity<Pager<WeightVolumeCollectDto>> entity = getPagerBaseEntity(pageNo);
                    if(entity != null && entity.getData() != null && entity.getData().getData() != null){
                        List<WeightVolumeCollectDto> list = entity.getData().getData();
                        updateAndSendMq(list);
                    }
                }

            }
        }catch (Exception e){
            logger.error("任务执行失败!"+e.getMessage());
        }

    }

    private BaseEntity<Pager<WeightVolumeCollectDto>> getPagerBaseEntity(int pageNo) {
        BaseEntity<Pager<WeightVolumeCollectDto>> baseEntity;
        Pager<WeightVolumeQueryCondition> pager = new Pager<>();
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setIsExcess(1);
        pager.setSearchVo(condition);
        pager.setPageSize(PAGESIZE);
        pager.setPageNo(pageNo);
        baseEntity = reportExternalService.getPagerByConditionForWeightVolume(pager);
        return baseEntity;
    }

    private void updateAndSendMq(List<WeightVolumeCollectDto> list) throws JMQException {
        for(WeightVolumeCollectDto weightVolumeCollectDto : list){
            //2.根据包裹号和站点查询oss是否有图片
            if(!StringHelper.isEmpty(weightVolumeCollectDto.getPictureAddress())){
                break;
            }
            String packageCode = weightVolumeCollectDto.getPackageCode();
            int siteCode = weightVolumeCollectDto.getReviewSiteCode();
            String pictureAddress = weightVolumeCollectDto.getPictureAddress();
            InvokeResult<String> result = weightAndVolumeCheckService.searchExcessPicture(packageCode, siteCode);
            if(result != null && result.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
                pictureAddress = result.getData();
            }
            //3.有则更新es 并给判责发mq
            if(!StringHelper.isEmpty(pictureAddress)){
                weightVolumeCollectDto.setPictureAddress(pictureAddress);
                weightVolumeCollectDto.setIsHasPicture(1);
                reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
                AbnormalPictureMq abnormalPictureMq = new AbnormalPictureMq();
                abnormalPictureMq.setAbnormalId(weightVolumeCollectDto.getPackageCode()+"|"+weightVolumeCollectDto.getReviewSiteCode());
                abnormalPictureMq.setWaybillCode(weightVolumeCollectDto.getPackageCode());
                abnormalPictureMq.setExcessPictureAddress(weightVolumeCollectDto.getPictureAddress());
                abnormalPictureMq.setUploadTime(new Date().getTime());
                this.logger.info("发送MQ[" + dmsWeightVolumeAbnormal.getTopic() + "],业务ID[" + abnormalPictureMq.getWaybillCode() + "],消息主题: " + JsonHelper.toJson(abnormalPictureMq));
                dmsWeightVolumeAbnormal.send(abnormalPictureMq.getAbnormalId(), JsonHelper.toJson(abnormalPictureMq));
            }
        }
    }



    /*public List<WaybillStaticRoute> selectListByCondition(WaybillStaticRouteParamDto parameter) throws Exception {
        BoolQueryBuilder queryParam = buildStaticRouteQuery(parameter);

        SearchRequestBuilder searchRequestBuilder = this.prepareSearch().setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchResponse searchResponse = searchRequestBuilder.setScroll(new TimeValue(60000)).setQuery(queryParam).setSize(Constants.EXPORT_STATIC_PAGE_SIZE).setExplain(true)
                .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        List<WaybillStaticRoute> list = new ArrayList(searchResponse.getTotalShards());
        if (hits.getTotalHits() < 1) {
            return list;
        }
        if(hits.getTotalHits()>Constants.EXPORT_STATIC_PAGE_SIZE){
            throw new Exception("导出条数不能多于"+Constants.EXPORT_STATIC_PAGE_SIZE+"条！");
        }
        do{
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                WaybillStaticRoute obj = WaybillStaticRoute.class.newInstance();
                this.convertToBean(obj, hit);
                list.add(obj);
            }
            searchResponse = super.getTransportClient().prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute()
                    .actionGet();
        }
        while(searchResponse.getHits().getHits().length != 0);

        return list;
    }*/

}
