package com.jd.bluedragon.core.jsf.workStation.impl;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.jsf.workStation.DockCodeAndPhoneMapper;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.workStation.DockCodeAndPhoneQuery;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.service.workStation.WorkGridBusinessJsfService;
import com.jdl.basic.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


/**
 * 获取运输月台号和联系人接口实现
 *
 * @author hujiping
 * @date 2022/4/6 6:05 PM
 */
@Slf4j
@Service("dockCodeAndPhoneMapper")
public class DockCodeAndPhoneMapperImpl implements DockCodeAndPhoneMapper {

    @Autowired
    private WorkGridBusinessJsfService workGridBusinessJsfService;

    /**
     * 获取运输月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return
     */
    @Override
    @JProfiler(jKey = Constants.UMP_APP_NAME + ".DockCodeAndPhoneMapperImpl.queryDockCodeByFlowDirection", jAppName = Constants.UMP_APP_NAME, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<String>> queryDockCodeByFlowDirection(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        log.info("DockCodeAndPhoneServiceImpl,queryDockCodeByFlowDirection 入参:" + JSON.toJSONString(dockCodeAndPhoneQueryDTO));
        DockCodeAndPhoneQuery dockCodeAndPhoneQuery = new DockCodeAndPhoneQuery();
        BeanUtils.copyProperties(dockCodeAndPhoneQueryDTO, dockCodeAndPhoneQuery);
        return workGridBusinessJsfService.queryDockCodeByFlowDirection(dockCodeAndPhoneQuery);
    }

    /**
     * 获取网格信息通过月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<WorkStationGrid>
     */
    @Override
    @JProfiler(jKey = Constants.UMP_APP_NAME + ".DockCodeAndPhoneMapperImpl.queryPhoneByDockCodeForTms", jAppName = Constants.UMP_APP_NAME, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<WorkStationGrid>> queryPhoneByDockCodeForTms(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        DockCodeAndPhoneQuery dockCodeAndPhoneQuery = new DockCodeAndPhoneQuery();
        BeanUtils.copyProperties(dockCodeAndPhoneQueryDTO, dockCodeAndPhoneQuery);
        return workGridBusinessJsfService.queryPhoneByDockCodeForTms(dockCodeAndPhoneQuery);
    }

}
