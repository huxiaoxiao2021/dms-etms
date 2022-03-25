package com.jd.bluedragon.common.task;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreEvent;
import com.jd.bluedragon.distribution.ministore.enums.MSDeviceBindEventTypeEnum;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MiniStoreSyncBindRelationTask implements Runnable {
    private Integer type;
    private MiniStoreBindRelation miniStoreBindRelation;
    private DefaultJMQProducer miniStoreSealBoxProducer;
    private SortingDao sortingDao;

    @Override
    public void run() {
        Sorting sorting =new Sorting();
        sorting.setBoxCode(miniStoreBindRelation.getBoxCode());
        List<Sorting> sortingList = sortingDao.findOrderDetail(sorting);
        List<String> packageCodes =new ArrayList<>();
        for (Sorting s:sortingList){
            packageCodes.add(s.getPackageCode());
        }
        List<String> iceBoardCodes =new ArrayList<>();
        iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode1());
        iceBoardCodes.add(miniStoreBindRelation.getIceBoardCode2());

        MiniStoreEvent miniStoreEvent = new MiniStoreEvent();
        miniStoreEvent.setPackageCodes(packageCodes);
        miniStoreEvent.setIceBoardCodes(iceBoardCodes);
        miniStoreEvent.setEventType(type);
        miniStoreEvent.setStoreCode(miniStoreBindRelation.getStoreCode());
        miniStoreEvent.setBoxCode(miniStoreBindRelation.getBoxCode());
        miniStoreEvent.setCreateTime(TimeUtils.date2string(new Date(),TimeUtils.yyyy_MM_dd_HH_mm_ss));
        miniStoreSealBoxProducer.sendOnFailPersistent(miniStoreBindRelation.getBoxCode(), JsonHelper.toJson(miniStoreEvent));//TODO businessId
    }
}
