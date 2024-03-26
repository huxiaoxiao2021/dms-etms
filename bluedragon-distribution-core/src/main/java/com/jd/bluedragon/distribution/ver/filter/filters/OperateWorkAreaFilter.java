package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 网格作业区拦截相关
 * @author fanggang7
 * @date 2024-01-22 13:58:00 周一
 */
public class OperateWorkAreaFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private WorkStationGridManager workStationGridManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Override
    public void doFilter(FilterContext filterContext, FilterChain chain) throws Exception {
        logger.info("OperateWorkAreaFilter do filter process...");

        final PdaOperateRequest pdaOperateRequest = filterContext.getPdaOperateRequest();

        // 如果是打印客户端操作的批量分拣，则不校验
        if (Objects.equals(SortingBizSourceEnum.PRINT_CLIENT_BATCH_SORTING.getCode(), pdaOperateRequest.getBizSource())) {
            chain.doFilter(filterContext, chain);
            return;
        }

        CallerInfo info = Profiler.registerInfo("DMS.WEB.SortingCheck.OperateWorkAreaFilter", false, true);
        // 如果不在白名单内，且是启用名单，则进行校验
        if (!dmsConfigManager.getPropertyConfig().isTeanSiteIdWhite4InterceptFilter(pdaOperateRequest.getCreateSiteCode())
            && dmsConfigManager.getPropertyConfig().isTeanSiteIdEnable4InterceptFilter(pdaOperateRequest.getCreateSiteCode())) {
            // 检查运单是否为特安运单，如果是特安运单需校验网格
            final Result<Boolean> checkWaybillVasResult = waybillCommonService.checkWaybillVas(filterContext.getWaybillCode(), WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFETY, filterContext.getWaybillVasDtos());
            if(!checkWaybillVasResult.isSuccess()){
                logger.error("OperateWorkAreaFilter_doFilter checkWaybillVas fail filterContext {} {}", JsonHelper.toJson(filterContext), JsonHelper.toJson(checkWaybillVasResult));
            } else {
                // 查看网格作业区信息
                // 没有签到网格码
                if (StringUtils.isBlank(pdaOperateRequest.getWorkStationGridKey())) {
                    Profiler.registerInfoEnd(info);
                    throw new SortingCheckException(SortingResponse.CODE_29466, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_CODE));
                }

                final WorkStationGrid workStationGrid = this.getWorkStationGrid(pdaOperateRequest.getWorkStationGridKey());
                // 网格查找作业区为空
                if (workStationGrid == null) {
                    Profiler.registerInfoEnd(info);
                    throw new SortingCheckException(SortingResponse.CODE_29467, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_CODE));
                }
                if (checkWaybillVasResult.getData()) {
                    // 不是特安作业区
                    if (!dmsConfigManager.getPropertyConfig().isTeanWorkAreaCode(workStationGrid.getAreaCode())
                            && !dmsConfigManager.getPropertyConfig().isTeanMixScanWorkAreaCode(workStationGrid.getAreaCode())) {
                        Profiler.registerInfoEnd(info);
                        throw new SortingCheckException(SortingResponse.CODE_29467, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_CODE));
                    }
                } else {
                    // 非特安运单，不能在特安作业区扫描
                    if (dmsConfigManager.getPropertyConfig().isTeanWorkAreaCode(workStationGrid.getAreaCode())) {
                        Profiler.registerInfoEnd(info);
                        throw new SortingCheckException(SortingResponse.CODE_29468, HintService.getHint(HintCodeConstants.NOT_TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.NOT_TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_CODE));
                    }
                }
            }
        }
        Profiler.registerInfoEnd(info);

        chain.doFilter(filterContext, chain);
    }

    private WorkStationGrid getWorkStationGrid(String workStationGridKey) {
        final com.jdl.basic.common.utils.Result<WorkStationGrid> workStationGridResult = workStationGridManager.queryWorkStationGridByBusinessKeyWithCache(workStationGridKey);
        if (!workStationGridResult.isSuccess()) {
            logger.error("OperateWorkAreaFilter queryWorkStationGridByBusinessKeyWithCache fail {}", JsonHelper.toJson(workStationGridKey));
            return null;
        }
        return workStationGridResult.getData();
    }
}
