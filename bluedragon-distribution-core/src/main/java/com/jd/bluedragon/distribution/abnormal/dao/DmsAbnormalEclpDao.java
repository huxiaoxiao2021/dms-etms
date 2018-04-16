package com.jd.bluedragon.distribution.abnormal.dao;

import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpDao
 * @Description: ECLP外呼申请表--Dao接口
 * @date 2018年03月14日 16:31:20
 */
public interface DmsAbnormalEclpDao extends Dao<DmsAbnormalEclp> {

    /**
     * 外呼结果写入
     *
     * @param dmsAbnormalEclpResponse
     * @return
     */
    public int updateResult(DmsAbnormalEclp dmsAbnormalEclp);

}
