package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpService
 * @Description: ECLP外呼申请表--Service接口
 * @date 2018年03月14日 16:31:20
 */
public interface DmsAbnormalEclpService extends Service<DmsAbnormalEclp> {

    /**
     * 保存并发外呼申请
     *
     * @param dmsAbnormalEclp
     * @return
     */
    public JdResponse<Boolean> save(DmsAbnormalEclp dmsAbnormalEclp);

    /**
     * 写入外呼结果
     */
    public int updateResult(DmsAbnormalEclp dmsAbnormalEclp);
}
