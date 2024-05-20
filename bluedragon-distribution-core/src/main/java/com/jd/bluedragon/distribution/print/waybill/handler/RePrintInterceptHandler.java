package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.print.domain.RePrintInterceptConf;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.user.JyUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.Constants.*;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT;

/**
 * @Author liwenji3
 * @Date 2024/5/7 17:36
 * @Description 包裹补打拦截处理 https://joyspace.jd.com/pages/3iPc4E8YnHnsOcPC6PUh
 */
@Service
@Slf4j
public class RePrintInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>>  {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyUserManager jyUserManager;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private ReceiveService receiveService;

    @Autowired
    private ReprintRecordService reprintRecordService;

    @Override
    @JProfiler(jKey = "dmsWeb.RePrintInterceptHandler.handle",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        try {
            WaybillPrintRequest request = context.getRequest();
            // 只针对包裹补打场景
            if (!PACKAGE_AGAIN_PRINT.getType().equals(request.getOperateType())) {
                return interceptResult;
            }
            SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(PACKAGE_REPRINT_INTERCEPT_CONFIG);
            if (sysConfig == null || StringUtils.isEmpty(sysConfig.getConfigContent())) {
                return interceptResult;
            }
            RePrintInterceptConf interceptConf = JsonHelper.fromJson(sysConfig.getConfigContent(), RePrintInterceptConf.class);
            if (interceptConf == null) {
                return interceptResult;
            }
            if (StringUtils.isEmpty(request.getPackageBarCode())) {
                return interceptResult;
            }
            // 场地开关校验
            if (!checkSite(request.getSiteCode(), interceptConf)) {
                return interceptResult;
            }

            // 校验路由是否包含当前场地
            boolean routerContainsSiteCode = routerContainsSiteCode(request);
            // 校验是否操作过验货或收货
            boolean isInspection = isInspection(request);
            if (!routerContainsSiteCode && !isInspection) {
                interceptResult.toFail("您所在场地不在此运单路由经过的场地范围内，禁止操作补打!");
                return interceptResult;
            }

            // 如果为场地负责人，不进行打印次数校验
            if (isSiteLeader(request)) {
                return interceptResult;
            }

            // 补打次数校验
            if (!rePrintNumCheck(request, interceptConf)) {
                return interceptResult;
            }
        }catch (Exception e) {
            log.error("RePrintInterceptHandler 执行异常{}", JsonHelper.toJson(context.getRequest()), e);
        }
        return interceptResult;
    }

    /**
     * 补打次数校验
     * @param request
     * @param interceptConf
     * @return
     */
    private boolean rePrintNumCheck(WaybillPrintRequest request, RePrintInterceptConf interceptConf) {
        // 获取查询时间范围
        Integer hour = interceptConf.getPrintFromTimeHour();
        Date dateFrom = DateHelper.addHours(new Date(), -hour);
        ReprintRecordQuery query = new ReprintRecordQuery();
        query.setBarCode(request.getPackageBarCode());
        query.setSiteCode(request.getSiteCode());
        query.setOperateTimeFrom(dateFrom);
        Response<Long> countResponse = reprintRecordService.queryCount(query);
        if (countResponse == null || countResponse.getData() == null) {
            return false;
        }
        Long printCount = interceptConf.getPrintCount();
        if (printCount >  countResponse.getData()) {
            return false;
        }
        return false;
    }

    /**
     * 在当前场地操作过验货或者收货
     * @param request
     * @return
     */
    private boolean isInspection(WaybillPrintRequest request) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarCode());
        // 根据场地和包裹号查询验货记录
        Inspection condition = new Inspection();
        condition.setWaybillCode(waybillCode);
        condition.setCreateSiteCode(request.getSiteCode());
        List<Inspection> inspections = inspectionService.queryByCondition(condition);
        if (CollectionUtils.isNotEmpty(inspections)) {
            return true;
        }
        // 查询收货记录
        Receive receive = receiveService.findLastByBoxCodeAndSiteCode(waybillCode, request.getSiteCode());
        return receive != null;
    }

    /**
     * 校验路由是否包含当前场地
     * @param request
     * @return
     */
    private boolean routerContainsSiteCode(WaybillPrintRequest request) {
        String routerStr = waybillCacheService.getRouterByWaybillCode(WaybillUtil.getWaybillCode(request.getPackageBarCode()));
        if (StringUtils.isEmpty(routerStr)) {
            return false;
        }
        String[] routers = routerStr.split(WAYBILL_ROUTER_SPLIT);
        List<String> routerList = Arrays.asList(routers);
        if (CollectionUtils.isEmpty(routerList)) {
            return false;
        }
        return routerList.contains(String.valueOf(request.getSiteCode()));
    }

    /**
     * 场地负责人校验
     * @param request
     * @return
     */
    private boolean isSiteLeader(WaybillPrintRequest request) {
        JyUserDto jyUserDto = jyUserManager.querySiteLeader(request.getSiteCode());
        if (jyUserDto == null) {
            return false;
        }
        String leaderErp = jyUserDto.getUserErp();
        return Objects.equals(request.getUserERP(), leaderErp);
    }

    /**
     * 校验场地
     * @param siteCode
     * @param interceptConf
     * @return
     */
    private boolean checkSite(Integer siteCode, RePrintInterceptConf interceptConf) {
        if (siteCode == null) {
            return false;
        }
        // 场地名单校验
        List<String> siteCodeList = interceptConf.getSiteCodeList();
        if (CollectionUtils.isEmpty(siteCodeList)) {
            return false;
        }
        if (!siteCodeList.contains(siteCode.toString()) && !siteCodeList.contains(STR_ALL)) {
            return false;
        }
        // 场地类型校验
        List<String>  siteSortTypeList = interceptConf.getSiteSortTypeList();
        if (CollectionUtils.isEmpty(siteSortTypeList)) {
            return false;
        }
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (siteInfo == null) {
            return false;
        }
        return siteSortTypeList.contains(String.valueOf(siteInfo.getSortType()));
    }
}
