package com.jd.bluedragon.distribution.jy.manager;

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
}
