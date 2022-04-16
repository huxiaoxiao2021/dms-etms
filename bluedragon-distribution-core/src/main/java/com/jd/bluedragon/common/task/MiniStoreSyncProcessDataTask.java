package com.jd.bluedragon.common.task;

import com.jd.bluedragon.common.dto.ministore.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreSortingProcessEvent;
import com.jd.bluedragon.distribution.ministore.enums.ProcessTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 给上下游系统(保温箱等)同步 分拣业务节点数据信息
 */
public class MiniStoreSyncProcessDataTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MiniStoreSyncProcessDataTask.class);

    private ProcessTypeEnum processType;
    private String boxCode;
    private String updateUser;
    private Long updateUerCode;
    private String siteName;
    private MiniStoreService miniStoreService;
    private DefaultJMQProducer miniStoreSortProcessProducer;

    public MiniStoreSyncProcessDataTask(ProcessTypeEnum processType, String boxCode,
                                        String updateUser, Long updateUerCode,String siteName,
                                        MiniStoreService miniStoreService,
                                        DefaultJMQProducer miniStoreSortProcessProducer) {
        this.processType = processType;
        this.boxCode = boxCode;
        this.updateUser = updateUser;
        this.updateUerCode = updateUerCode;
        this.siteName =siteName;
        this.miniStoreService = miniStoreService;
        this.miniStoreSortProcessProducer =miniStoreSortProcessProducer;
    }

    @Override
    public void run() {
        logger.info("MiniStoreSyncProcessDataTask start，current processType is {}",processType.getMsg());
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setBoxCode(boxCode);
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (miniStoreBindRelation!=null){
            //判断是否是接货仓仓发货,接货仓发货往后不再更新绑定数据节点状态
            if (ProcessTypeEnum.SEND_JIEHUOCANG==processType){
                logger.info("接货仓发货同步节点数据...");
                MiniStoreBindRelation m =new MiniStoreBindRelation();
                m.setId(miniStoreBindRelation.getId());
                m.setUpdateUser(updateUser);
                m.setUpdateUserCode(updateUerCode);
                m.setState(Byte.valueOf(MiniStoreProcessStatusEnum.DELIVER_GOODS.getCode()));
                miniStoreService.updateById(m);
            }
            logger.info("移动微仓同步节点数据 {}",processType.getMsg());
            MiniStoreSortingProcessEvent event =new MiniStoreSortingProcessEvent();
            event.setStoreCode(miniStoreBindRelation.getStoreCode());
            event.setProcessType(processType.getType());
            event.setSiteName(siteName);
            Date time =new Date();
            event.setOperateTime(TimeUtils.date2string(time,TimeUtils.yyyy_MM_dd_HH_mm_ss));
            event.setOperateUser(updateUser);
            event.setCreateTime(TimeUtils.date2string(time,TimeUtils.yyyy_MM_dd_HH_mm_ss));
            logger.info("MiniStoreSyncProcessDataTask send mq消息体："+JsonHelper.toJson(event));
            miniStoreSortProcessProducer.sendOnFailPersistent(boxCode, JsonHelper.toJson(event));
        }

    }
}
