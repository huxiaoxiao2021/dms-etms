package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTransBoard;

import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/23 11:27
 */
public class UnloadCarTransBoardDao extends BaseDao<UnloadCarTransBoard> {

    public static final String namespace = UnloadCarTransBoardDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCarTransBoard detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

    /**
     * 更新包裹数量
     * @param detail
     * @return
     */
    public int updateCount(UnloadCarTransBoard detail) {
        return this.getSqlSession().update(namespace + ".updateCount",detail);
    }

    /**
     * 获取封车编码下板号
     * @param sealCarCode
     * @return
     */
    public List<String> searchBoardsBySealCode(String sealCarCode) {
        return this.getSqlSession().selectList(namespace + ".searchBoardsBySealCode",sealCarCode);
    }

    /**
     * 根据封车编码查询
     * @param sealCarCode
     * @return
     */
    public UnloadCarTransBoard searchBySealCode(String sealCarCode) {
        return this.getSqlSession().selectOne(namespace + ".searchBySealCode",sealCarCode);
    }
}
