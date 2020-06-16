package com.jd.bluedragon.distribution.financialForKA.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * 单号校验DAO
 *
 * @author: hujiping
 * @date: 2020/2/26 22:00
 */
public class WaybillCodeCheckDao extends BaseDao<WaybillCodeCheckDto> {

    public static final String namespace = WaybillCodeCheckDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(WaybillCodeCheckDto detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

    /**
     * 查询
     * @param condition
     * @return*/
    public List<WaybillCodeCheckDto> queryByCondition(KaCodeCheckCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    /**
     * 查询总数
     * @param condition
     * @return*/
    public Integer queryCountByCondition(KaCodeCheckCondition condition){
        return this.getSqlSession().selectOne(namespace + ".queryCountByCondition",condition);
    }

    /**
     * 导出
     * @param condition
     * @return*/
    public List<WaybillCodeCheckDto> exportByCondition(KaCodeCheckCondition condition){
        return this.getSqlSession().selectList(namespace + ".exportByCondition",condition);
    }

}
