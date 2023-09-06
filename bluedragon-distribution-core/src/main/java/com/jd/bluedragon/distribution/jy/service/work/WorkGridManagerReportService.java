package com.jd.bluedragon.distribution.jy.service.work;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportQuery;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: WorkGridManagerReportService
 * @Description: 巡检任务-报表--Service接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public interface WorkGridManagerReportService {

	Result<Integer> queryCountForExport(WorkGridManagerReportQuery query);

	Result<List<WorkGridManagerReportVo>> queryListForExport(WorkGridManagerReportQuery query);

	Result<PageDto<WorkGridManagerReportVo>> queryPageList(WorkGridManagerReportQuery query);

	/**
	 * 根据bizId调后端获取任务详细信息
	 *
	 * @param bizId
	 * @return
	 */
	Result<WorkGridManagerReportVo> queryTaskDataByBizId(String bizId);

}
