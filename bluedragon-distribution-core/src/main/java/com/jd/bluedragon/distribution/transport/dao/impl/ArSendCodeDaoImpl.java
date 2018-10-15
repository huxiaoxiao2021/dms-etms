package com.jd.bluedragon.distribution.transport.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.dao.ArSendCodeDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyoude
 * @ClassName: ArSendCodeDaoImpl
 * @Description: 发货批次表--Dao接口实现
 * @date 2017年12月28日 09:46:12
 */
@Repository("arSendCodeDao")
public class ArSendCodeDaoImpl extends BaseDao<ArSendCode> implements ArSendCodeDao {

    @Override
    public List<ArSendCode> getBySendRegisterId(Long sendRegisterId) {
        return this.sqlSession.selectList(getNameSpace() + ".getBySendRegisterId", sendRegisterId);
    }

    @Override
    public List<ArSendCode> getBySendRegisterIds(List<Long> sendRegisterIds) {
        return this.sqlSession.selectList(getNameSpace() + ".getBySendRegisterIds", sendRegisterIds);
    }

    @Override
    public int deleteBySendRegisterId(Long sendRegisterId, String userCode) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("sendRegisterId", sendRegisterId);
        parameter.put("updateUser", userCode);
        return this.sqlSession.update(getNameSpace() + ".deleteBySendRegisterId", parameter);
    }

}
