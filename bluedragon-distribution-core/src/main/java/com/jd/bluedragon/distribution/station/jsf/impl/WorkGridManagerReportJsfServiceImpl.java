package com.jd.bluedragon.distribution.station.jsf.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.service.work.WorkGridManagerReportService;
import com.jd.bluedragon.distribution.work.api.WorkGridManagerReportJsfService;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportQuery;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: WorkStationServiceImpl
 * @Description: 网格工序信息表--Service接口实现
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
@Service("workGridManagerReportJsfService")
public class WorkGridManagerReportJsfServiceImpl implements WorkGridManagerReportJsfService {

	private static final Logger logger = LoggerFactory.getLogger(WorkGridManagerReportJsfServiceImpl.class);

	@Autowired
	@Qualifier("workGridManagerReportService")
	private WorkGridManagerReportService workGridManagerReportService;

	@Override
	public Result<Integer> queryCountForExport(WorkGridManagerReportQuery query) {
		return workGridManagerReportService.queryCountForExport(query);
	}
	@Override
	public Result<List<WorkGridManagerReportVo>> queryListForExport(WorkGridManagerReportQuery query) {
		return workGridManagerReportService.queryListForExport(query);
	}
	@Override
	public Result<PageDto<WorkGridManagerReportVo>> queryPageList(WorkGridManagerReportQuery query) {
		return workGridManagerReportService.queryPageList(query);
	}

	/**
	 * 根据bizId调后端获取任务详细信息
	 *
	 * @param bizId
	 * @return
	 */
	@Override
	public Result<WorkGridManagerReportVo> queryTaskDataByBizId(String bizId) {
		return workGridManagerReportService.queryTaskDataByBizId(bizId);
	}
}
