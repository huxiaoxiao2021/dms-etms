package com.jd.bluedragon.distribution.sorting.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;

public class SortingReturnDao extends BaseDao<SortingReturn> {

    public static final String namespace = SortingReturnDao.class.getName();

    /**
     * 根据退货数据判断数据是否存在
     * 
     * @param SortingReturn returns
     * @return Boolean
     */
    public Boolean exists(SortingReturn returns) {
    	SortingReturn result = (SortingReturn) this.getSqlSession().selectOne(namespace + ".exists", returns);
        return result != null ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 根据退货状态更新状态(status)和删除标志(yn)
     * 
     * @param SortingReturn returns
     * @return Integer
     */
    public Integer updateStatus(SortingReturn returns) {
        return this.getSqlSession().update(namespace + ".updateStatus", returns);
    }

    /**
     * 查询状态为"未处理"(0)的删除状态"使用"(1)的数据 限制50条
     * 
     * @return List<Returns>
     */
    @SuppressWarnings("unchecked")
    public List<SortingReturn> findByStatus(Integer fetchNum) {
        return this.getSqlSession().selectList(namespace + ".findByStatus", fetchNum);
    }

    /**
     * 批量更新完成状态
     * 
     * @param datas
     * @return Integer
     */
    public Integer updateStatusSuccess(List<SortingReturn> datas) {
        return this.getSqlSession().update(namespace + ".updateListStatusSuccess", datas);
    }
    
    public Integer updateListStatusFail(List<SortingReturn> datas) {
        return this.getSqlSession().update(namespace + ".updateListStatusFail", datas);
    }
}
