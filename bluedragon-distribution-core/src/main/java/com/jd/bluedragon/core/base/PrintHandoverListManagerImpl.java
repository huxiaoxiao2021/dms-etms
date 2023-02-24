package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.dms.wb.report.api.IPrintHandoverListJsfService;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.SendCodeCountDto;
import com.jd.dms.workbench.utils.sdk.base.PageData;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 打印交接清单包装服务
 *
 * @author hujiping
 * @date 2021/4/13 1:47 下午
 */
@Service("printHandoverListManager")
@Slf4j
public class PrintHandoverListManagerImpl implements PrintHandoverListManager {

    @Autowired
    @Qualifier("printHandoverListJsfService")
    private IPrintHandoverListJsfService printHandoverListJsfService;


    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.queryPrintHandOverListByQueryCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public PageData<PrintHandoverListDto> queryPrintHandOverListByQueryCondition(Pager<PrintHandoverLitQueryCondition> pager) {
        BaseEntity<PageData<PrintHandoverListDto>> baseEntity = printHandoverListJsfService.queryPrintHandOverListByQueryCondition(pager);
        if(baseEntity != null && baseEntity.isSuccess()){
            return baseEntity.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.queryPrintHandOverListByScroll", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public PageData<PrintHandoverListDto> queryPrintHandOverListByScroll(Pager<PrintHandoverLitQueryCondition> query) {
        BaseEntity<PageData<PrintHandoverListDto>> baseEntity = printHandoverListJsfService.queryPrintHandOverListByScroll(query);
        if(baseEntity != null && baseEntity.isSuccess()){
            return baseEntity.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.queryPrintHandOverListTotal", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Long queryPrintHandOverListTotal(PrintHandoverLitQueryCondition condition) {
        BaseEntity<Long> baseEntity = printHandoverListJsfService.queryPrintHandOverListTotal(condition);
        if(baseEntity != null && baseEntity.isSuccess()){
            return baseEntity.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.recordForPrintHandoverList", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Boolean recordForPrintHandoverList(PrintHandoverListDto dto) {
        BaseEntity<Boolean> baseEntity = printHandoverListJsfService.recordForPrintHandoverList(dto);
        if(baseEntity != null && baseEntity.isSuccess()){
            return baseEntity.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.updatePrintHandoverList", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Boolean updatePrintHandoverList(PrintHandoverListDto dto) {
        BaseEntity<Boolean> baseEntity = printHandoverListJsfService.updatePrintHandoverList(dto);
        if(baseEntity != null && baseEntity.isSuccess()){
            return baseEntity.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.doExportAsync", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<Boolean> doExportAsync(Pager<PrintHandoverLitQueryCondition> query) {
        return printHandoverListJsfService.doExportAsync(query);
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.doBatchExportAsync", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<Boolean> doBatchExportAsync(Pager<PrintHandoverLitQueryCondition> query) {
        return printHandoverListJsfService.doBatchExportAsync(query);
    }

    @JProfiler(jKey = "DMS.BASE.PrintHandoverListManagerImpl.doBatchExportAsyncToTripartite", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<Boolean> doBatchExportAsyncToTripartite(Pager<PrintHandoverLitQueryCondition> query,String content, List<String> tos, List<String> ccs) {
        return printHandoverListJsfService.doBatchExportAsyncToTripartite(query,content, tos, ccs);
    }
}
