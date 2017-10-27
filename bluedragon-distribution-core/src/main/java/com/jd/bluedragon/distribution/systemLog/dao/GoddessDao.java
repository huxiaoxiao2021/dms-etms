package com.jd.bluedragon.distribution.systemLog.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Function;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.cassandra.BaseCassandraDao;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by wangtingwei on 2017/2/17.
 */
public class GoddessDao {
    private static final Logger logger = LoggerFactory.getLogger(GoddessDao.class);

    @Value("${cassandra.consistencyLevel.default}")
    protected ConsistencyLevel consistencyLevel;

    @Value("${cassandra.ttl}")
    protected long ttl;

    public static final int SIZE = 2000;

    @Resource
    private BaseCassandraDao baseCassandraDao;
    private PreparedStatement insertStatement;

    private PreparedStatement selectStatement;


    private PreparedStatement getInsertStatement() {
        if (null == insertStatement) {
            synchronized (this) {
                if (null == insertStatement) {
                    RegularStatement toPrepare = new SimpleStatement(
                            "insert into systemlogwaybill ( code , uu_id  , body,time) values ( ?,?,?,?) USING TTL " + ttl
                                    + ";");
                    toPrepare.setConsistencyLevel(consistencyLevel);
                    insertStatement = baseCassandraDao.getSession().prepare(toPrepare);
                }
            }
        }
        return insertStatement;
    }

    public void batchInsert(Goddess log) {
        try {
            long startTime = System.currentTimeMillis();
            List<BoundStatement> bstatementList = new ArrayList<BoundStatement>(1);
            Map<String, Object> values = new HashMap<String, Object>();
            long operateTime = new Date().getTime();
            BoundStatement bStatement = getInsertStatement().bind(log.getKey(), UUIDs.timeBased().toString(),
                    JsonHelper.toJson(log), operateTime);
            bstatementList.add(bStatement);
            baseCassandraDao.batchInsert(bstatementList, values);
            if (logger.isInfoEnabled()) {
                logger.info("OperationlogCassandra batchInsert execute success cost:"
                        + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (Throwable e) {
            logger.error("保存全程跟踪", e);
        }
    }

    private PreparedStatement getSelectStatement() {
        if (null == selectStatement) {
            synchronized (this) {
                if (null == selectStatement) {
                    RegularStatement toPrepare = new SimpleStatement(
                            "select * from systemlogwaybill  where code = ? order by time desc");
                    toPrepare.setConsistencyLevel(consistencyLevel);
                    selectStatement = baseCassandraDao.getSession().prepare(toPrepare);
                }
            }
        }
        return selectStatement;
    }

    public Pager<List<Goddess>> getSplitPage(Pager<String> pager) {
        Pager<List<Goddess>> result = new Pager<List<Goddess>>();
        result.setPageSize(pager.getPageSize());
        result.setPageNo(pager.getPageNo());
        //查询参数可能为空，为空时直接返回
        if(StringUtils.isBlank(pager.getData())){
            result.setTotalSize(0);
            return result;
        }

        long startTime = System.currentTimeMillis();
        try {
            BoundStatement bs = null;
            PagingState pagingState = null;
            ResultSet rs = null;
            bs = getSelectStatement().bind(pager.getData());
            rs = baseCassandraDao.preparedSelectBycode(bs);
            result.setTotalSize(rs.getAvailableWithoutFetching());

            bs.setFetchSize(pager.getPageSize());
            if (pager.getPageNo().equals(Integer.valueOf(1))) {
                rs = baseCassandraDao.preparedSelectBycode(bs);
            } else {
                rs = baseCassandraDao.preparedSelectBycode(bs);
                pagingState = rs.getExecutionInfo().getPagingState();
                for (int i = 2; i <= pager.getPageNo(); i++) {
                    bs.setPagingState(pagingState);
                    rs = baseCassandraDao.preparedSelectBycode(bs);
                    pagingState = rs.getExecutionInfo().getPagingState();
                }
            }
            result.setData(rsToList(rs, new RowToOrder()));
            if (logger.isInfoEnabled()) {
                logger.info("OperationlogCassandra getPage execute success cost:" + (System.currentTimeMillis() - startTime)
                        + "ms");
            }
        } catch (Throwable e) {
            logger.error("查询操作日志异常 异常原因：", e);
        }

        return result;
    }


    public static ArrayList<Goddess> rsToList(ResultSet rs, Function<Row, Goddess> rowToObject) {
        ArrayList<Goddess> al = new ArrayList<Goddess>();
        int available = rs.getAvailableWithoutFetching();
        for (int i = 0; i < available; i++) {
            Row row = rs.one();
            al.add(rowToObject.apply(row));
        }
        return al;
    }

    private static final String BODY = "body";

    public static final class RowToOrder implements Function<Row, Goddess> {
        @Override
        public Goddess apply(Row row) {
            return JsonHelper.fromJson(row.getString(BODY), Goddess.class);
        }
    }
}
