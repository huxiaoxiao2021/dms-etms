package com.jd.bluedragon.distribution.inventory.dao.impl;

import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: InventoryExceptionDaoImpl
 * @Description: 盘点异常表--Dao接口实现
 *
 */
@Repository("inventoryExceptionDao")
public class InventoryExceptionDaoImpl extends BaseDao<InventoryException> implements InventoryExceptionDao {

    @Override
    public int updateExpStatus(Map<String, Object> params) {
        return sqlSession.update(this.nameSpace+".updateExpStatus", params);
    }

    @Override
    public List<InventoryException> getExportResultByCondition(InventoryExceptionCondition condition) {
        return this.getSqlSession().selectList(nameSpace + ".getExportResultByCondition", condition);
    }

    @Override
    public List<InventoryException> getInventoryLossException() {
        return this.getSqlSession().selectList(nameSpace + ".getInventoryLossException");
    }


}
