package com.jd.bluedragon.common.task;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreEvent;
import com.jd.bluedragon.distribution.ministore.enums.MSDeviceBindEventTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MiniStoreSyncBindRelationTask implements Runnable {
    private MSDeviceBindEventTypeEnum type;
    private Long miniStoreBindRelationId;
    private DefaultJMQProducer miniStoreSealBoxProducer;
    private MiniStoreService miniStoreService;
    private SortingService sortingService;

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
        //查询微仓绑定数据
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectById(miniStoreBindRelationId);
        //查询分拣集包数据
        Sorting sorting =new Sorting();
        sorting.setBoxCode(miniStoreBindRelation.getBoxCode());
        sorting.setCreateSiteCode(miniStoreBindRelation.getCreateSiteCode().intValue());
        List<Sorting> sortingList = sortingService.findOrderDetail(sorting);

        List<String> packageCodes =new ArrayList<>();
        for (Sorting s:sortingList){
            packageCodes.add(s.getPackageCode());
        }
        List<String> iceBoardCodes =new ArrayList<>();
        iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode1());
        iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode2());

        //封装消息体
        MiniStoreEvent miniStoreEvent = new MiniStoreEvent();
        miniStoreEvent.setPackageCodes(packageCodes);
        miniStoreEvent.setIceBoardCodes(iceBoardCodes);
        miniStoreEvent.setEventType(type.getCode());
        miniStoreEvent.setStoreCode(miniStoreBindRelation.getStoreCode());
        miniStoreEvent.setBoxCode(miniStoreBindRelation.getBoxCode());
        miniStoreEvent.setCreateTime(TimeUtils.date2string(new Date(),TimeUtils.yyyy_MM_dd_HH_mm_ss));
        miniStoreSealBoxProducer.sendOnFailPersistent(miniStoreBindRelation.getBoxCode(), JsonHelper.toJson(miniStoreEvent));
        //TODO businessId
    }
}
