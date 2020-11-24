package com.jd.bluedragon.distribution.businessCode.dao;

import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * <p>
 *     业务单号的持久化操作类
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public class BusinessCodeDao {

    private static final String NAMESPACE = BusinessCodeDao.class.getName();

    private SqlSession sqlSession;

    private SequenceGenAdaptor sequenceGenAdaptor;

    public Integer insertBusinessCode(BusinessCodePo businessCodePo) {
        return sqlSession.insert(NAMESPACE + ".insertBusinessCode", businessCodePo);
    }

    public BusinessCodePo findBusinessCodeByCode(String code) {
        return sqlSession.selectOne(NAMESPACE + ".findBusinessCodeByCode", code);
    }

    public Integer batchInsertBusinessCodeAttribute(List<BusinessCodeAttributePo> businessCodeAttributePos) {
        return sqlSession.insert(NAMESPACE + ".batchInsertBusinessCodeAttribute", businessCodeAttributePos);
    }

    public List<BusinessCodeAttributePo> findAllAttributesByCode(String code) {
        return sqlSession.selectList(NAMESPACE + ".findAllAttributesByCode", code);
    }

    public BusinessCodeAttributePo findAttributeByCodeAndKey(BusinessCodeAttributePo businessCodeAttributePo) {
        return sqlSession.selectOne(NAMESPACE + ".findAttributeByCodeAndKey", businessCodeAttributePo);
    }

    public Integer updateAttribute(BusinessCodeAttributePo businessCodeAttributePo) {
        return sqlSession.update(NAMESPACE + ".updateAttribute", businessCodeAttributePo);
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public SequenceGenAdaptor getSequenceGenAdaptor() {
        return sequenceGenAdaptor;
    }

    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
}
