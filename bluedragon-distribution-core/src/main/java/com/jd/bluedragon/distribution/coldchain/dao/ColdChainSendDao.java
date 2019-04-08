package com.jd.bluedragon.distribution.coldchain.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendDao
 * @date 2019/3/28
 */
public class ColdChainSendDao extends BaseDao<ColdChainSend> {

    private final static Log logger = LogFactory.getLog(ColdChainSendDao.class);

    private final static String NAMESPACE = ColdChainSendDao.class.getName();

    /**
     * 新增
     *
     * @param coldChainSend
     * @return
     */
    public int add(ColdChainSend coldChainSend) {
        return this.getSqlSession().insert(NAMESPACE + ".add", coldChainSend);
    }

    /**
     * 批量新增
     *
     * @param coldChainSends
     * @return
     */
    public int batchAdd(List<ColdChainSend> coldChainSends) {
        return this.getSqlSession().insert(NAMESPACE + ".batchAdd", coldChainSends);
    }

    /**
     * 根据条件查询
     *
     * @param parameter
     * @return
     */
    public List<ColdChainSend> get(ColdChainSend parameter) {
        return this.getSqlSession().selectList(NAMESPACE + ".get", parameter);
    }
}
