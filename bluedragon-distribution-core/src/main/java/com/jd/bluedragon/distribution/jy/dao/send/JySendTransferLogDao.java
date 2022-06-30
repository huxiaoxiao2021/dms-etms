package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendTransferLogEntity;
import java.util.List;

/**
 * 发货任务迁移记录表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendTransferLogDao extends BaseDao<JySendTransferLogEntity> {

    private final static String NAMESPACE = JySendTransferLogDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendTransferLogEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int batchInsert(List<JySendTransferLogEntity> list) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", list);
    }
}
