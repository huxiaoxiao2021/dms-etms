package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public void doFilter(FilterContext filterContext, FilterChain chain) throws Exception {
        logger.info("do filter process...");

        // 检查运单是否为特安运单，如果是特安运单需校验网格
        if (BusinessHelper.matchWaybillVasDto(Constants.TE_AN_SERVICE, filterContext.getWaybillVasDtos())) {
            // 查看网格作业区信息
            final PdaOperateRequest pdaOperateRequest = filterContext.getPdaOperateRequest();
            // 没有签到网格码
            if (StringUtils.isBlank(pdaOperateRequest.getWorkStationGridKey())) {
                throw new SortingCheckException(SortingResponse.CODE_29466, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_CODE));
            }

            final WorkStationGrid workStationGrid = this.getWorkStationGrid(pdaOperateRequest.getWorkStationGridKey());
            // 网格查找作业区为空
            if(workStationGrid == null){
                throw new SortingCheckException(SortingResponse.CODE_29467, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_EMPTY_WORK_AREA_CODE_HINT_CODE));
            }

            // 不是特安作业区
            if (!dmsConfigManager.getPropertyConfig().isTeanWorkAreaCode(workStationGrid.getAreaCode())) {
                throw new SortingCheckException(SortingResponse.CODE_29467, HintService.getHint(HintCodeConstants.TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_MSG_DEFAULT, HintCodeConstants.TEAN_WAYBILL_WRONG_WORK_AREA_CODE_HINT_CODE));
            }
        }

        chain.doFilter(filterContext, chain);
    }

    private WorkStationGrid getWorkStationGrid(String workStationGridKey) {
        final WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
        workStationGridQuery.setBusinessKey(workStationGridKey);
        final com.jdl.basic.common.utils.Result<WorkStationGrid> workStationGridResult = workStationGridManager.queryByGridKey(workStationGridQuery);
        if (!workStationGridResult.isSuccess()) {
            return null;
        }
        return workStationGridResult.getData();
    }
}
