package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.jy.dto.send.SendPackageDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendWaybillDto;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.job.SendBoxAgg;
import com.jdl.jy.realtime.model.es.job.SendPackageEsDto;
import com.jdl.jy.realtime.model.query.send.SendVehiclePackageDetailQuery;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import com.jdl.jy.realtime.model.vo.send.SendVehiclePackageDetailVo;

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

    Pager<SendVehiclePackageDetailVo> querySendVehicleToScanPackageDetail(Pager<SendVehiclePackageDetailQuery> queryPager);


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

    /**
     * 分页查询包裹明细 无箱号
     * @param queryPager
     * @return
     */
    Pager<SendPackageEsDto> querySendPackageDetailNoBox(Pager<SendVehicleTaskQuery> queryPager);

    /**
     * 分页查询箱号统计信息
     * @param queryPager
     * @return
     */
    Pager<SendBoxAgg> querySendBoxAgg(Pager<SendVehicleTaskQuery> queryPager);
}
