package com.jd.bluedragon.common.task;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreBindRelationEvent;
import com.jd.bluedragon.distribution.ministore.enums.MSDeviceBindEventTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 给上下游系统（保温箱、终端、仓储等）同步微仓绑定关系数据
 */
public class MiniStoreSyncBindRelationTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MiniStoreSyncBindRelationTask.class);

    private MSDeviceBindEventTypeEnum type;
    private Long miniStoreBindRelationId;
    private DefaultJMQProducer miniStoreSealBoxProducer;
    private MiniStoreService miniStoreService;
    private SortingService sortingService;
    private List<String> packageCodes;

    public List<String> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<String> packageCodes) {
        this.packageCodes = packageCodes;
    }

    public MiniStoreSyncBindRelationTask(MSDeviceBindEventTypeEnum type, Long miniStoreBindRelationId,
                                         DefaultJMQProducer miniStoreSealBoxProducer,
                                         MiniStoreService miniStoreService,
                                         SortingService sortingService) {
        this.type = type;
        this.miniStoreBindRelationId = miniStoreBindRelationId;
        this.miniStoreSealBoxProducer = miniStoreSealBoxProducer;
        this.miniStoreService = miniStoreService;
        this.sortingService = sortingService;
    }

    @Override
    public void run() {
        logger.info("MiniStoreSyncBindRelationTask(seal and unseal) start，current processType is "+type.getMsg());


        //查询微仓绑定数据
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectById(miniStoreBindRelationId);
        if (miniStoreBindRelation!=null){
            //查询分拣集包数据
            Sorting sorting =new Sorting();
            sorting.setBoxCode(miniStoreBindRelation.getBoxCode());
            sorting.setCreateSiteCode(miniStoreBindRelation.getCreateSiteCode().intValue());
            List<Sorting> sortingList = sortingService.listSortingByBoxCode(sorting);

            packageCodes =new ArrayList<>();
            for (Sorting s:sortingList){
                packageCodes.add(s.getPackageCode());
            }

            List<String> iceBoardCodes =new ArrayList<>();
            iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode1());
            iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode2());

            //封装消息体
            MiniStoreBindRelationEvent miniStoreEvent = new MiniStoreBindRelationEvent();
            miniStoreEvent.setPackageCodes(packageCodes);
            miniStoreEvent.setIceBoardCodes(iceBoardCodes);
            miniStoreEvent.setEventType(type.getCode());
            miniStoreEvent.setStoreCode(miniStoreBindRelation.getStoreCode());
            miniStoreEvent.setBoxCode(miniStoreBindRelation.getBoxCode());
            miniStoreEvent.setErrMsg(miniStoreBindRelation.getDes());
            miniStoreEvent.setCreateTime(TimeUtils.date2string(new Date(),TimeUtils.yyyy_MM_dd_HH_mm_ss));
            if (null!=miniStoreBindRelation.getDes() && !"".equals(miniStoreBindRelation.getDes())){
                miniStoreEvent.setErrMsg(miniStoreBindRelation.getDes());
            }
            logger.info("消息体："+JsonHelper.toJson(miniStoreEvent));
            miniStoreSealBoxProducer.sendOnFailPersistent(miniStoreBindRelation.getBoxCode(), JsonHelper.toJson(miniStoreEvent));
            //TODO businessId
            logger.info("MiniStoreSyncBindRelationTask has compeleted");
        }
    }
}
