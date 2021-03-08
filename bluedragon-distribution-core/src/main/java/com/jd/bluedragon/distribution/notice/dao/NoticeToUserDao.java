package com.jd.bluedragon.distribution.notice.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeToUser;
import com.jd.bluedragon.distribution.notice.request.NoticeToUserQuery;

import java.util.List;
import java.util.Map;

/**
 * 用户通知操作记录表
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-26 13:54:29 周五
 */
public class NoticeToUserDao extends BaseDao<NoticeToUser> {

    private final static String NAMESPACE = NoticeToUserDao.class.getName();

    /**
     * 查询所有通知
     *
     * @return 数据列表
     * @author fanggnag7
     * @time 2021-02-24 20:59:37 周三
     */
    public List<NoticeToUser> getByParam(Map<String, Object> parameter) {
        return this.getSqlSession().selectList(NoticeToUserDao.NAMESPACE + ".getByParam", parameter);
    }

    /**
     * 按条件查询总数
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public Long queryCount(NoticeToUserQuery query) {
        return this.getSqlSession().selectOne(NoticeToUserDao.NAMESPACE + ".queryCount", query);
    }

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public List<NoticeToUser> queryList(NoticeToUserQuery query) {
        return this.getSqlSession().selectList(NoticeToUserDao.NAMESPACE + ".queryList", query);
    }

    /**
     * 按条件查询一行数据
     * @param noticeToUserQuery 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public NoticeToUser selectOne(NoticeToUserQuery noticeToUserQuery) {
        return this.getSqlSession().selectOne(NoticeToUserDao.NAMESPACE + ".selectOne", noticeToUserQuery);
    }

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public NoticeToUser getByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NoticeToUserDao.NAMESPACE + ".getByPrimaryKey", id);
    }

    /**
     * 新增
     *
     * @param noticeToUser 参数
     * @return 插入结果
     * @author fanggang7
     * @time 2021-02-26 14:27:46 周五
     */
    public int add(NoticeToUser noticeToUser) {
        return this.getSqlSession().insert(NoticeToUserDao.NAMESPACE + ".add", noticeToUser);
    }

    /**
     * 逻辑删除
     *
     * @param parameter
     * @return
     */
    public int logicDelete(Map<String, Object> parameter) {
        return this.getSqlSession().update(NoticeToUserDao.NAMESPACE + ".logicDelete", parameter);
    }

    /**
     * 按主键更新
     * @param noticeToUser 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public int updateByPrimaryKey(NoticeToUser noticeToUser) {
        return this.getSqlSession().update(NoticeToUserDao.NAMESPACE + ".updateByPrimaryKey", noticeToUser);
    }

    /**
     * 按主键删除
     * @param noticeToUser 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-02-24 20:59:37 周三
     */
    public int deleteByPrimaryKey(NoticeToUser noticeToUser) {
        return this.getSqlSession().update(NoticeToUserDao.NAMESPACE + ".deleteByPrimaryKey", noticeToUser);
    }
}
