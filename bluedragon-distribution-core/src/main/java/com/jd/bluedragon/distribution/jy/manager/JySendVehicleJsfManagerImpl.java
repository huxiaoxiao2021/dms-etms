package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.send.ISendVehicleJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @ClassName JySendVehicleJsfManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/14 14:14
 **/
@Component
public class JySendVehicleJsfManagerImpl implements IJySendVehicleJsfManager {

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleJsfManagerImpl.class);

    @Autowired
    @Qualifier("jySendVehicleJsfService")
    private ISendVehicleJsfService sendVehicleJsfService;

    @Override
    public Pager<SendBarCodeDetailVo> queryByCondition(Pager<SendVehicleTaskQuery> queryPager) {
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJySendVehicleManager.queryByCondition");
        try {
            ServiceResult<Pager<SendBarCodeDetailVo>> serviceResult = sendVehicleJsfService.queryByCondition(queryPager);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
            else {
                log.warn("分页查询发车包裹失败. {}. {}", JsonHelper.toJson(queryPager), JsonHelper.toJson(serviceResult));
            }
        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询发车包裹明细异常. {}", JsonHelper.toJson(queryPager), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }
}
