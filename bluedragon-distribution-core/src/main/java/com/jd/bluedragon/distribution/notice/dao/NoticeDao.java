package com.jd.bluedragon.distribution.notice.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;

import java.util.List;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeDao
 * @date 2019/4/17
 */
public class NoticeDao extends BaseDao<Notice> {

    private final static String NAMESPACE = NoticeDao.class.getName();

    /**
     * 查询所有通知
     *
     * @return
     */
    public List<Notice> getByParam(Map<String, Object> parameter) {
        return this.getSqlSession().selectList(NoticeDao.NAMESPACE + ".getByParam", parameter);
    }

    /**
     * 按条件查询总数
     * @param noticeQuery 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public Long queryCount(NoticeQuery noticeQuery) {
        return this.getSqlSession().selectOne(NoticeDao.NAMESPACE + ".queryCount", noticeQuery);
    }

    /**
     * 按条件查询列表
     * @param noticeQuery 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public List<Notice> queryList(NoticeQuery noticeQuery) {
        return this.getSqlSession().selectList(NoticeDao.NAMESPACE + ".queryList", noticeQuery);
    }

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public Notice getByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NoticeDao.NAMESPACE + ".getByPrimaryKey", id);
    }

    /**
     * 新增
     *
     * @param notice
     * @return
     */
    public int add(Notice notice) {
        return this.getSqlSession().insert(NoticeDao.NAMESPACE + ".add", notice);
    }

    /**
     * 逻辑删除
     *
     * @param parameter
     * @return
     */
    public int logicDelete(Map<String, Object> parameter) {
        return this.getSqlSession().update(NoticeDao.NAMESPACE + ".logicDelete", parameter);
    }

    /**
     * 按主键更新
     * @param notice 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public int updateByPrimaryKey(Notice notice) {
        return this.getSqlSession().update(NoticeDao.NAMESPACE + ".updateByPrimaryKey", notice);
    }

    /**
     * 按主键删除
     * @param notice 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public int deleteByPrimaryKey(Notice notice) {
        return this.getSqlSession().update(NoticeDao.NAMESPACE + ".deleteByPrimaryKey", notice);
    }

}
