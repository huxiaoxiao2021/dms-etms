package com.jd.bluedragon.distribution.tms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.tms.TmsCarTaskManager;
import com.jd.bluedragon.distribution.tms.TmsCarTaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;

import com.jd.tms.tpc.dto.RouteLineCargoDto;
import com.jd.tms.tpc.dto.RouteLineCargoQueryDto;
import com.jd.tms.tpc.dto.RouteLineCargoUpdateDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TmsCarTaskServiceImpl implements TmsCarTaskService {

    @Autowired
    private TmsCarTaskManager tmsCarTaskManager;

    @Autowired
    private BasicSelectWS basicSelectWs;


    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode) {
        PageDto<com.jd.tms.basic.dto.TransportResourceDto> page = new PageDto<>();
        page.setCurrentPage(1);
        page.setPageSize(200);
        TransportResourceDto transportResourceDto = new TransportResourceDto();
        transportResourceDto.setStartNodeCode(startNodeCode);
        //只查询目的分拣中心数据
        transportResourceDto.setEndNodeType(2);
        return tmsCarTaskManager.getEndNodeList(page, transportResourceDto);
    }

    @Override
    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(CarTaskQueryRequest queryRequest) {

        RouteLineCargoQueryDto queryDto = new RouteLineCargoQueryDto();
        queryDto.setBeginNodeCode(queryRequest.getBeginNodeCode());
        queryDto.setEndNodeCode(queryRequest.getEndNodeCode());
        com.jd.tms.tpc.dto.PageDto<RouteLineCargoDto> pageDto = new com.jd.tms.tpc.dto.PageDto();
        pageDto.setCurrentPage(1);
        pageDto.setPageSize(200);
        return tmsCarTaskManager.queryCarTaskList(queryDto, pageDto);
    }

    @Override
    public JdCResponse updateCarTaskInfo(CarTaskUpdateDto updateDto) {
        RouteLineCargoUpdateDto volumeUpdateDto = new RouteLineCargoUpdateDto();
        volumeUpdateDto.setBeginNodeCode(updateDto.getBeginNodeCode());
        volumeUpdateDto.setEndNodeCode(updateDto.getEndNodeCode());
        volumeUpdateDto.setRouteLineCode(updateDto.getRouteLineCode());
        volumeUpdateDto.setPlanDepartTime(updateDto.getPlanDepartTime());
        volumeUpdateDto.setVolume(updateDto.getVolume());
        volumeUpdateDto.setAccountCode(updateDto.getAccountCode());
        volumeUpdateDto.setAccountName(updateDto.getAccountName());
        return tmsCarTaskManager.updateCarTaskInfo(volumeUpdateDto);
    }

    /**
     * 获取目的地编码
     *
     * @param opeSiteCode 当前操作场地ID
     * @param barCode     内容 必须为包裹号、运单号或者目的地ID中的一种
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.TmsCarTaskServiceImpl.findEndNodeCode", mState = {JProEnum.TP,JProEnum.FunctionError})
    public String findEndNodeCode(Integer opeSiteCode, String barCode) {
        if(opeSiteCode == null || opeSiteCode == 0){
            throw new RuntimeException("操作场地为空！");
        }
        boolean isPack = WaybillUtil.isPackageCode(barCode);
        boolean isWaybill = WaybillUtil.isWaybillCode(barCode);
        boolean isSiteCode = NumberHelper.isNumber(barCode);
        Integer endSiteId = null;
        if(isPack || isWaybill){
            String waybillCode = barCode;
            if(isPack){
                waybillCode = WaybillUtil.getWaybillCode(barCode);
            }
            String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
            endSiteId = getRouteNextSite(opeSiteCode,routerStr);

        }else if(isSiteCode){
            endSiteId = Integer.parseInt(barCode);
        }else {
            //必须为包裹号、运单号或者目的地ID中的一种
            throw new RuntimeException("仅支持包裹号、运单号或者目的地ID！");
        }
        if(endSiteId != null){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(endSiteId);
            if(baseStaffSiteOrgDto != null){
                return baseStaffSiteOrgDto.getDmsSiteCode();
            }
        }

        return StringUtils.EMPTY;
    }


    private static final String WAYBILL_ROUTER_SPLIT = "\\|";

    /**
     * 解析路由内容 返回下一站
     * @param startSiteId
     * @param routerStr
     * @return
     */
    private Integer getRouteNextSite(Integer startSiteId, String routerStr) {
        if (StringUtils.isNotBlank(routerStr)) {
            String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                Integer curNode = Integer.parseInt(routerNodes[i]);
                Integer nextNode = Integer.parseInt(routerNodes[i + 1]);
                if (curNode == startSiteId) {
                    return nextNode;
                }
            }
        }
        return null;
    }
}
