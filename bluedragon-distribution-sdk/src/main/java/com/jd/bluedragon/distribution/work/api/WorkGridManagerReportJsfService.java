package com.jd.bluedragon.distribution.work.api;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportQuery;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 任务线上化-管理--JsfService接口
 * 
 * @author wuyoude
 * @date 2023年03月10日 14:30:43
 *
 */
public interface WorkGridManagerReportJsfService {
	/**
	 * 查询数量
	 * @param query
	 * @return
	 */
	Result<Integer> queryCountForExport(WorkGridManagerReportQuery query);
	/**
	 * 查询导出逻辑
	 * @param query
	 * @return
	 */
	Result<List<WorkGridManagerReportVo>> queryListForExport(WorkGridManagerReportQuery query);
	/**
	 * 分页查询逻辑
	 * @param query
	 * @return
	 */
	Result<PageDto<WorkGridManagerReportVo>> queryPageList(WorkGridManagerReportQuery query);
}
