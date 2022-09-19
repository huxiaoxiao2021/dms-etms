package com.jd.bluedragon.core.base;

import com.jd.tms.jdi.dto.*;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/5/24
 * @Description:
 */
public interface JdiTransWorkWSManager {

    /**
     * 查询派车单明细信息
     * @return
     */
    BigTransWorkItemDto queryTransWorkItemByOptionWithRead(String itemCode, BigQueryOption option);

    /**
     * 消费拣运发货任务车辆拍照消息并发送车辆停靠时间到运输平台的运输执行系统(JDI)
     * @param accountDto
     * @param transWorkPlatformEnterDto
     * @return
     */
    CommonDto recordBeginPlatformEnterTime(AccountDto accountDto, TransWorkPlatformEnterDto transWorkPlatformEnterDto);

}
