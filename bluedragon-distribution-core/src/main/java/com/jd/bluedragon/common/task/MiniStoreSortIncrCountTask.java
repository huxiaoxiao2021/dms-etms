package com.jd.bluedragon.common.task;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.sorting.request.PackSortTaskBody;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MiniStoreSortIncrCountTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MiniStoreSortIncrCountTask.class);

    private String boxCode;
    private String body;
    private MiniStoreService miniStoreService;

    public MiniStoreSortIncrCountTask(String boxCode, String body, MiniStoreService miniStoreService) {
        this.boxCode = boxCode;
        this.body = body;
        this.miniStoreService = miniStoreService;
    }

    @Override
    public void run() {
        logger.info("MiniStoreSortIncrCountTask start...");
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setBoxCode(boxCode);
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (null != miniStoreBindRelation) {
            logger.info("移动微仓集包业务更改集包数量,boxCode {}", boxCode);
            List<PackSortTaskBody> bodyList = JSON.parseArray(body, PackSortTaskBody.class);
            String updateUser = bodyList.get(0).getUserName();
            Long updateUserCode = Long.valueOf(bodyList.get(0).getUserCode());
            int rs = 0;
            try {
                rs = miniStoreService.incrSortCount(miniStoreBindRelation.getId(), updateUser, updateUserCode);
            } catch (Exception e) {
                logger.error("移动微仓更改集包数据异常", e);
            }
            if (rs > 0) {
                logger.info("移动微仓增加集包计数成功！");
            }
        }
    }
}
