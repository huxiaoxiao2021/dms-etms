package com.jd.bluedragon.distribution.exportlog.service;

import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLogCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * @author: 刘春和
 * @date: 2020/6/17 16:39
 * @description:
 */
public interface ExportLogService {
    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<ExportLog> listData(ExportLogCondition condition);

    /**
     * 更新
     * @param exportLog
     * @return
     */
     Integer update(ExportLog exportLog);

    /**
     * 根据主键id查询
     * @param id
     * @return
     */
     ExportLog findOne(Long id);
}
