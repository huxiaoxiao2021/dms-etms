package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedDto;
import com.jd.dms.wb.report.api.sealCar.dto.client.SealCarNotCollectedPo;
import com.jd.dms.workbench.utils.sdk.base.PageData;
import com.jd.dms.workbench.utils.sdk.base.Result;

/**
 * 工作台封车集齐监控查询
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-04-21 16:04:10 周三
 */
public interface IDmsWbSealCarCollectManager {

    /**
     * 查询封车未集齐包裹的运单数据总数
     * @param paramObj 查询总数
     * @return 分页数据
     * @author fanggang7
     * @time 2021-04-13 09:58:54 周二
     */
    Result<Long> selectNotCollectedCount(SealCarNotCollectedPo paramObj);

    /**
     * 查询封车未集齐包裹的运单数据
     * @param paramObj 查询总数
     * @return 分页数据
     * @author fanggang7
     * @time 2021-04-13 09:58:54 周二
     */
    Result<PageData<SealCarNotCollectedDto>> selectNotCollectedPageList(SealCarNotCollectedPo paramObj);
}
