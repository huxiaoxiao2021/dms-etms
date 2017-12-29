package com.jd.bluedragon.distribution.transport.dao.impl;

import com.jd.ql.dms.common.domain.City;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: ArSendRegisterDaoImpl
 * @Description: 发货登记表--Dao接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Repository("arSendRegisterDao")
public class ArSendRegisterDaoImpl extends BaseDao<ArSendRegister> implements ArSendRegisterDao {
    public List<ArSendRegister> queryStartCityInfo(){
        return sqlSession.selectList(this.nameSpace+".queryStartCityInfo");
    }
    public List<ArSendRegister> queryEndCityInfo(){
        return sqlSession.selectList(this.nameSpace+".queryEndCityInfo");
    }

}
