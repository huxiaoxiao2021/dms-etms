package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.jy.dto.send.SendPackageDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendWaybillDto;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;

/**
 * @ClassName IJySendVehicleJsfManager
 * @Description
 * @Author wyh
 * @Date 2022/6/14 14:08
 **/
public interface IJySendVehicleJsfManager {

    /**
     * 分页查询发车任务包裹记录
     * @param queryPager
     * @return
     */
    Pager<SendBarCodeDetailVo> queryByCondition(Pager<SendVehicleTaskQuery> queryPager);

    /**
     * 分页查询运单agg数据
     * @param queryPager
     * @return
     */
    Pager<SendWaybillDto> querySendTaskWaybill(Pager<SendVehicleTaskQuery> queryPager);

    /**
     * 分页查询包裹明细
     * @param queryPager
     * @return
     */
    Pager<SendPackageDto> querySendPackageDetail(Pager<SendVehicleTaskQuery> queryPager);
}
