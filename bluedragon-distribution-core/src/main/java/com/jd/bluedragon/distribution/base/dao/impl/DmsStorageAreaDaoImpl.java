package com.jd.bluedragon.distribution.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.dao.DmsStorageAreaDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 *
 * @ClassName: DmsStorageAreaDaoImpl
 * @Description: 分拣中心库位--Dao接口实现
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
@Repository("dmsStorageAreaDao")
public class DmsStorageAreaDaoImpl extends BaseDao<DmsStorageArea> implements DmsStorageAreaDao {

    public  DmsStorageArea findByProAndCity(DmsStorageArea dmsStorageArea){

        return sqlSession.selectOne(getNameSpace() + ".findByProAndCity" , dmsStorageArea);
    }
}
