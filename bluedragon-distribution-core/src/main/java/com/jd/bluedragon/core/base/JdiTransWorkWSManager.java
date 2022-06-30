package com.jd.bluedragon.core.base;

import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.ws.JdiTransWorkWS;

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

}
