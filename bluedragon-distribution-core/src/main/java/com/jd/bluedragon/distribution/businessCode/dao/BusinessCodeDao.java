package com.jd.bluedragon.distribution.businessCode.dao;

import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * <p>
 *     业务单号的持久化操作类
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public class BusinessCodeDao {

    private static final String namespace = BusinessCodeDao.class.getName();

    private SqlSession sqlSession;

    private SqlSessionTemplate sqlSessionRead;

    public Integer insertBusinessCode(BusinessCodePo businessCodePo) {
        return sqlSession.insert(namespace + ".insertBusinessCode", businessCodePo);
    }

    public BusinessCodePo findBusinessCodeByCode(String code) {
        return sqlSession.selectOne(namespace + ".findBusinessCodeByCode", code);
    }

    public Integer batchInsertBusinessCodeAttribute(List<BusinessCodeAttributePo> businessCodeAttributePos) {
        return sqlSession.insert(namespace + ".batchInsertBusinessCodeAttribute", businessCodeAttributePos);
    }

    public List<BusinessCodeAttributePo> findAllAttributesByCode(String code) {
        return sqlSession.selectList(namespace + ".findAllAttributesByCode", code);
    }

    public BusinessCodeAttributePo findAttributeByCodeAndKey(BusinessCodeAttributePo businessCodeAttributePo) {
        return sqlSession.selectOne(namespace + ".findAttributeByCodeAndKey", businessCodeAttributePo);
    }

    public Integer updateAttribute(BusinessCodeAttributePo businessCodeAttributePo) {
        return sqlSession.update(namespace + ".updateAttribute", businessCodeAttributePo);
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public SqlSessionTemplate getSqlSessionRead() {
        return sqlSessionRead;
    }

    public void setSqlSessionRead(SqlSessionTemplate sqlSessionRead) {
        this.sqlSessionRead = sqlSessionRead;
    }
}
