package com.jd.bluedragon.core.jsf.eclp;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.eclp.bbp.notice.domain.dto.BatchImportDTO;

/**
 * 
 * @ClassName: EclpImportServiceManager
 * @Description: 调用eclp站内信jsf接口定义
 * @author: wuyoude
 * @date: 2020年07月29日 下午2:37:26
 *
 */
public interface EclpImportServiceManager {
    /**
     * 调用eclp通用站内信创建接口
     * @param dto
     * @return
     */
	JdResult<Boolean> batchImport(BatchImportDTO dto);
}
