package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;

/**
 * 发货任务附属表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendAttachmentDao extends BaseDao<JySendAttachmentEntity> {

    private final static String NAMESPACE = JySendAttachmentDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendAttachmentEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int updateByBiz(JySendAttachmentEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBiz", entity);
    }

    public JySendAttachmentEntity hasPhoto(JySendAttachmentEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".hasPhoto", entity);
    }

    /**
     * 根据sendVehicleBizId查询记录
     * @param entity 查询参数
     * @return 对应记录数据
     * @author fanggang7
     * @time 2022-08-02 19:41:01 周二
     */
    public JySendAttachmentEntity selectBySendVehicleBizId(JySendAttachmentEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectBySendVehicleBizId", entity);
    }
}
