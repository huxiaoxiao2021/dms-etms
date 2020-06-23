package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTransBoard;

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

}
