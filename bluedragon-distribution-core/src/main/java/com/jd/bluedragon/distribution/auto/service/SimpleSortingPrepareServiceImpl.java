package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.service.BatchSendService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;
import com.jd.bluedragon.distribution.sorting.service.SortingExceptionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wangtingwei on 2014/10/16.
 */
@Service("SortingPrepareService")
public class SimpleSortingPrepareServiceImpl extends AbstractSortingPrepareService {

    private static final Logger log= LoggerFactory.getLogger(SimpleSortingPrepareServiceImpl.class);

    private static final String AUTO_SORTING_WHITE_PAPER_PREFIX="auto.sorting.whitepaper.";

    @Autowired
    private TaskService taskService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private BatchSendService batchSendService;

    @Autowired
    private SortingExceptionService sortingExceptionService;

    @Autowired
    private BaseMinorManager baseMinorManager;
    /**
     * 计算分拣站点【1：计算是否跨分拣中心；2：计算是否自提柜订单;3：返调度使用反调度后站点】
     * @param entity
     * @return
     */
    @Override
    protected boolean prepareSite(Sorting entity) {
        Waybill waybill=this.waybillCommonService.findByWaybillCode(entity.getWaybillCode());
        if(null==waybill){
            log.warn("准备分拣站点->获取不到运单信息：{}",entity.getWaybillCode());
            return false;
        }else {
            entity.setReceiveSiteCode(waybill.getSiteCode());
            entity.setReceiveSiteName(waybill.getSiteName());

        }
        char zitiSign = waybill.getSendPay().charAt(21);    //自提柜、合作代收，便民自提
        if('5' == zitiSign || '6' == zitiSign || '7' == zitiSign){
            Integer destinctSiteCode = baseService.getSiteSelfDBySiteCode(entity.getReceiveSiteCode());
            if(null!=destinctSiteCode&&destinctSiteCode>0){
                entity.setReceiveSiteCode(destinctSiteCode);
                entity.setReceiveSiteName(baseService.getSiteNameBySiteID(destinctSiteCode));
            }
        }

        BaseDmsStore bds = new BaseDmsStore();
        Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());
        JdResult<CrossPackageTagNew> br = baseMinorManager.queryCrossPackageTag(bds, entity.getReceiveSiteCode(), entity.getCreateSiteCode(),originalCrossType);
        if(br!= null && br.isSucceed() && br.getData()!=null
                &&br.getData().getDestinationDmsId()!=null
                &&br.getData().getDestinationDmsId()>0
                &&!br.getData().getDestinationDmsId().equals(entity.getCreateSiteCode())){
            entity.setReceiveSiteCode(br.getData().getDestinationDmsId());
            entity.setReceiveSiteName(br.getData().getDestinationDmsName());
        }

        return true;
    }

    /**
     * 初始化批次信息【将波次号作为批次号】
     * @param entity
     * @return
     */
    @Override
    protected boolean prepareSorting(Sorting entity) {
        log.debug("准备获取波次信息{}",entity.getWaybillCode());
        BatchSend send=batchSendService.readAndGenerateIfNotExist(entity.getCreateSiteCode(),entity.getOperateTime(),entity.getReceiveSiteCode());
        if(null==send){
            log.warn("获取波次信息失败->站点：{}|操作时间:{}|接收站点:{}",
                    entity.getCreateSiteCode(),DateHelper.formatDateTime(entity.getOperateTime()),entity.getReceiveSiteCode());
            return false;
        }else {
            log.debug("获取波次信息成功:{}",entity.getWaybillCode());
            entity.setBoxCode(send.getSendCode());
            entity.setSpareReason(send.getBatchCode());
            return true;
        }
    }

    /**
     * 拦截处理
     * @param entity    分拣对象
     * @return          拦截处理结果
     */
    @Override
    protected boolean intercept(Sorting entity) {
        log.info("开始插入分拣拦截日志：{}",entity.getWaybillCode());
        BoxResponse response= WaybillCancelClient.checkAutoSorting(entity.getCreateSiteCode(),entity.getPackageCode(),entity.getReceiveSiteCode());
        log.info("拦截状态为：{}",response.getCode());
        if(!response.getCode().equals(Integer.valueOf(200))){
            SortingException exception=new SortingException();
            exception.setBoxCode(entity.getSpareReason());
            exception.setPackageCode(entity.getPackageCode());
            exception.setCreateSiteCode(entity.getCreateSiteCode());
            exception.setReceiveSiteCode(entity.getReceiveSiteCode());
            exception.setBusinessType(10);
            exception.setCreateUserCode(entity.getCreateUserCode());
            exception.setCreateUserName(entity.getCreateUser());
            exception.setCreateTime(new Date(System.currentTimeMillis()));
            exception.setYn(1);
            exception.setExceptionCode(response.getCode());
            exception.setExceptionMessage(response.getMessage());
            sortingExceptionService.add(exception);
            log.info("分拣拦截日志插入成功：{}",exception.toString());
        }
        return true;
    }

    /**
     * 推送数据至分拣任务
     * @param entity
     * @return
     */
    @Override
    protected boolean push(Sorting entity) {

        Task task=new Task();
        task.setKeyword1(entity.getBoxCode());
        task.setKeyword2(entity.getPackageCode());
        task.setCreateSiteCode(entity.getCreateSiteCode());
        task.setReceiveSiteCode(entity.getReceiveSiteCode());
        task.setCreateTime(entity.getOperateTime());
        task.setType(Task.TASK_TYPE_SORTING);
        task.setBoxCode(entity.getBoxCode());
        task.setTableName(Task.getTableName(task.getType()));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        StringBuilder fingerprint = new StringBuilder("");
        fingerprint.append(task.getCreateSiteCode()).append("_")
                .append(task.getReceiveSiteCode()).append("_").append(task.getBusinessType())
                .append("_").append(task.getBoxCode()).append("_").append(task.getKeyword2())
                .append("_").append(DateHelper.formatDateTimeMs(task.getOperateTime()));
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        List<SortingRequest> list=new ArrayList<SortingRequest>(1);

        SortingRequest request=new SortingRequest();
        request.setBoxCode(entity.getBoxCode());
        request.setBsendCode(entity.getBsendCode());
        request.setFeatureType(0);
        request.setIsCancel(0);
        request.setIsLoss(0);
        request.setPackageCode(entity.getPackageCode());
        request.setReceiveSiteCode(entity.getReceiveSiteCode());
        request.setReceiveSiteName(entity.getReceiveSiteName());
        request.setWaybillCode(entity.getWaybillCode());
        request.setBusinessType(entity.getType());
        request.setOperateTime(DateHelper.formatDateTime(entity.getOperateTime()));
        request.setSiteCode(entity.getCreateSiteCode());
        request.setSiteName(entity.getCreateSiteName());
        request.setUserCode(entity.getCreateUserCode());
        request.setUserName(entity.getCreateUser());
        list.add(request);
        task.setOwnSign(BusinessHelper.getOwnSign()); //fix 增加ownsign避免直接写库没有ownsign问题
        task.setBody(JsonHelper.toJson(list));
        log.info("插入分拣任务为：{}",task.toString());
        return taskService.add(task,true)>0?true:false;

    }

    /**
     * 过滤器
     * @param entity
     * @return 是否需要过滤【是：无需分拣；否：需要分拣】
     */
    @Override
    protected boolean filter(Sorting entity){
        List<SysConfig> configs=baseService.queryConfigByKeyWithCache(AUTO_SORTING_WHITE_PAPER_PREFIX+String.valueOf(entity.getCreateSiteCode()));
        if(configs!=null&&configs.size()>0&& configs.get(0).getConfigContent()!=null&&configs.get(0).getConfigContent().trim().length()>0){
            List<String> whiteList= Arrays.asList(configs.get(0).getConfigContent().split(","));
            if(!whiteList.contains(String.valueOf(entity.getReceiveSiteCode()))){
                log.warn("sorting-filter:true={}",entity.getWaybillCode());
                return true;
            }
        }
        log.warn("sorting-filter:false={}",entity.getWaybillCode());
        return false;
    }



}
