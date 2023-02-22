package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.SendCodeCountDto;
import com.jd.dms.wb.report.api.dto.printhandover.SummaryPrintResult;
import com.jd.dms.workbench.utils.sdk.base.PageData;

import java.util.List;


/**
 * 打印交接清单包装服务接口
 *
 * @author hujiping
 * @date 2021/4/13 1:44 下午
 */
public interface PrintHandoverListManager {

    /**
     * 打印交接清单 - 根据条件分页查询
     * @param pager
     * @return
     */
    PageData<PrintHandoverListDto> queryPrintHandOverListByQueryCondition(Pager<PrintHandoverLitQueryCondition> pager);

    /**
     * 打印交接清单 - 根据条件scroll查询
     * @param query
     * @return
     */
    PageData<PrintHandoverListDto> queryPrintHandOverListByScroll(Pager<PrintHandoverLitQueryCondition> query);

    /**
     * 打印交接清单 - 根据条件查询总数
     * @param condition
     * @return
     */
    Long queryPrintHandOverListTotal(PrintHandoverLitQueryCondition condition);

    /**
     * 打印交接清单 - 新增记录
     * @param dto
     * @return
     */
    Boolean recordForPrintHandoverList(PrintHandoverListDto dto);

    /**
     * 打印交接清单 - 更新记录
     * @param dto
     * @return
     */
    Boolean updatePrintHandoverList(PrintHandoverListDto dto);

    /**
     * 打印交接清单 - 导出
     * @param query
     * @return
     */
    BaseEntity<Boolean> doExportAsync(Pager<PrintHandoverLitQueryCondition> query);

    /**
     * 打印交接清单 - 批量导出
     * @param query
     * @return
     */
    BaseEntity<Boolean> doBatchExportAsync(Pager<PrintHandoverLitQueryCondition> query);

    /**
     * 打印交接清单-批量导出到外部人员
     * @param query
     * @param content
     * @param tos
     * @param ccs
     * @return
     */
    BaseEntity<Boolean> doBatchExportAsyncToTripartite(Pager<PrintHandoverLitQueryCondition> query,String content, List<String> tos, List<String> ccs);

    /**
     * 查询批次统计详情
     * @param sendCode
     * @return
     */
    SendCodeCountDto queryCountInfoBySendCode(String sendCode);
}
