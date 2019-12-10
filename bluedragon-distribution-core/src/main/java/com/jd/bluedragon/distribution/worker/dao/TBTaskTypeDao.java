package com.jd.bluedragon.distribution.worker.dao;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.worker.domain.TBTaskType;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wangtingwei on 2015/10/8.
 */
public class TBTaskTypeDao  {

    private static final Logger log = LoggerFactory.getLogger(TBTaskTypeDao.class);

    private static final String namespace=TBTaskTypeDao.class.getName();

    private static final String selectByNameUsePagerSQL=namespace+".selectByNameUsePager";

    private static final String selectCounterByNameSQL=namespace+".selectCounterByName";

    private static final String insertSingleSQL=namespace+".insertSingle";

    private static final String updateSingleByIdSQL=namespace+".updateSingleById";

    private static final String selectByIdSQL=namespace+".selectById";

    private SqlSessionTemplate sqlSessionTemplate;

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**

     * 分页查询任务表
     * @param pager
     * @return
     */
    public List<TBTaskType> selectByNameUsePager(Pager<String> pager){
        if(log.isInfoEnabled()) {
            log.info(JsonHelper.toJson(pager));
        }
        return sqlSessionTemplate.selectList(selectByNameUsePagerSQL, pager);
    }

    /**
     * 查询记录总数
     * @param pager
     * @return
     */
    public int selectCountByName(Pager<String> pager){
            return (Integer) sqlSessionTemplate.selectOne(selectCounterByNameSQL, pager);

    }

    /**
     * 插入单条记录
     * @param domain
     * @return
     */
    public int insertSingle(TBTaskType domain){
        return  sqlSessionTemplate.insert(insertSingleSQL,domain);
    }

    /**
     * 更新单条记录
     * @param domain
     * @return
     */
    public int updateSingleById(TBTaskType domain){
        return sqlSessionTemplate.update(updateSingleByIdSQL,domain);
    }

    public TBTaskType selectById(int id){
        return (TBTaskType)sqlSessionTemplate.selectOne(selectByIdSQL,id);
    }
}
