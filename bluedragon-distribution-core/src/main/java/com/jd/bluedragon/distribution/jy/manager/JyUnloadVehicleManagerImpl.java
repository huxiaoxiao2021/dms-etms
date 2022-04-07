package com.jd.bluedragon.distribution.jy.manager;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.unload.IUnloadVehicleJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName JyUnloadVehicleManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/6 19:45
 **/
@Service("jyUnloadVehicleManager")
public class JyUnloadVehicleManagerImpl implements IJyUnloadVehicleManager {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleManagerImpl.class);

    @Autowired
    @Qualifier("jyUnloadVehicleJsfService")
    private IUnloadVehicleJsfService unloadVehicleJsfService;

    @Override
    public List<JyVehicleTaskUnloadDetail> queryByCondition(Pager<JyVehicleTaskUnloadDetail> pager) {
        List<JyVehicleTaskUnloadDetail> unloadDetails = Lists.newArrayList();

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.queryByCondition");
        try {

        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询卸车明细失败. {}", JsonHelper.toJson(pager), ex);
        }
        Profiler.registerInfoEnd(ump);

        return unloadDetails;
    }

    @Override
    public JyVehicleTaskUnloadDetail findOneUnloadDetail(JyVehicleTaskUnloadDetail query) {
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.findOneUnloadDetail");
        try {

        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询一条卸车明细失败. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }
}
