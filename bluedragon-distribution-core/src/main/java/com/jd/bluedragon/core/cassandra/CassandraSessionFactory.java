package com.jd.bluedragon.core.cassandra;

import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author cyk
 * @since 2015/10/15 
 */
public class CassandraSessionFactory implements FactoryBean<Session> {
	
    private BaseCassandra baseCassandra;
    private String keyspace;
    private final Logger log = LoggerFactory.getLogger(CassandraSessionFactory.class);

    public CassandraSessionFactory(BaseCassandra baseCassandra, String keyspace) {
        this.baseCassandra = baseCassandra;
        this.keyspace = keyspace;
    }

    @Override
    public Session getObject() throws Exception {
        try {
            return this.baseCassandra.getSession(this.keyspace);
        } catch (Exception e) {
            log.error("cassandra 没有可用的host", e);
            return null;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return Session.class;
    }

    @Override
    public boolean isSingleton() {
        return true;

    }
}
