package com.jd.bluedragon.distribution.reverse.part.dao.impl;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.dao.ReversePartDetailDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: ReversePartDetailDaoImpl
 * @Description: 半退明细表--Dao接口实现
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
@Repository("reversePartDetailDao")
public class ReversePartDetailDaoImpl extends BaseDao<ReversePartDetail> implements ReversePartDetailDao {


    @Override
    public List<ReversePartDetail> queryByParam(ReversePartDetailCondition reversePartDetailCondition) {
        return this.sqlSession.selectList(getNameSpace()+".queryByParam", reversePartDetailCondition);
    }

    @Override
    public int queryByParamCount(ReversePartDetailCondition reversePartDetailCondition) {
        return this.sqlSession.selectOne(getNameSpace()+".queryByParamCount", reversePartDetailCondition);
    }
    @Override
    public int updateReceiveTime(ReversePartDetail reversePartDetail){
        return this.sqlSession.update(getNameSpace()+".updateReceiveTime", reversePartDetail);
    }

    @Override
    public int updateForCancelSend(ReversePartDetail reversePartDetail) {
        return this.sqlSession.update(getNameSpace()+".updateForCancelSend", reversePartDetail);
    }
}
