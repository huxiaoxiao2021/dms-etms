package com.jd.bluedragon.distribution.base.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jim.cli.redis.jedis.exceptions.JedisException;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KvIndexDao extends BaseDao<KvIndex> {

	@Autowired
	@Qualifier("redisClientCache")
	private Cluster redisClientCache;
	
    private static final Log LOGGER= LogFactory.getLog(KvIndexDao.class);

	public static final String namespace = KvIndexDao.class.getName();

	@Override
    public Integer add(String namespace, KvIndex entity) {
        return this.add(entity);
    }

    private List<String> queryByKeyword(String keyword) {
		return this.getSqlSession().selectList(namespace + ".queryByKeyword", keyword);
	}

    public Integer queryOneByKeyword(String keyword) {
    	String createSiteCode = this.getSqlSession().selectOne(namespace + ".queryOneByKeyword", keyword);
    	if(StringHelper.isNotEmpty(createSiteCode))
    		return NumberUtils.createInteger(createSiteCode);
    	return null;
	}
    
    public String queryRecentOneByKeyword(String keyword) {
    	return this.getSqlSession().selectOne(namespace + ".queryRecentOneByKeyword", keyword);
	}
    
    public Integer deleteByKeyword(String keyword) {
        return this.getSqlSession().delete(namespace + ".deleteByKey", keyword);
    }

    public List<Integer> queryByKeywordSet(List<String> keywords) {
        if (null == keywords || keywords.size() <= 0) {
            return new ArrayList<Integer>();
        }
        List<String> createSiteCodes = new ArrayList<String>();
        for (String key : keywords) {
            if (StringHelper.isNotEmpty(key)) {
                List<String> values = queryByKeyword(key.trim().toUpperCase());
                if (null != values && values.size() > 0) {
                    createSiteCodes.addAll(values);
                }
            }
        }
        if (null != createSiteCodes && createSiteCodes.size() > 0) {
            Set<Integer> uniqueSiteCodes = new HashSet<Integer>();
            for(String siteCode : createSiteCodes) {
                if(StringHelper.isNotEmpty(siteCode)) {
                    try {
                        uniqueSiteCodes.add(NumberUtils.createInteger(siteCode.trim()));
                    }catch (NumberFormatException e) {
                        LOGGER.error(MessageFormat.format("分库索引值转成为数字失败siteCode:{0}", siteCode));
                    }
                }
            }
            return new ArrayList<Integer>(uniqueSiteCodes);
        }
        return new ArrayList<Integer>();
    }

    /**
     * 获取创建站点索引值
     * @param keyword 关键字
     * @return
     */
    public List<Integer> queryCreateSiteCodesByKey(String keyword){
        CallerInfo info = Profiler.registerInfo("DMSDAO.KvIndexDao.queryCreateSiteCodesByKey", false, true);
        List<Integer> result=selectCreateSiteCodes(keyword);
        Profiler.registerInfoEnd(info);
        return result;
    }

    private List<Integer> selectCreateSiteCodes(String keyword){
        if(StringUtils.isBlank(keyword)){
            return new ArrayList<Integer>(0);
        }
        keyword=keyword.trim().toUpperCase();
        if(keyword.length()>50){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("KEY={0}值超过50，截断为50",keyword));
            }
            keyword=keyword.substring(0,50);
        }
        List<String> values= this.getSqlSession().selectList(namespace + ".queryByKeyword", keyword);
        if(null==values||values.size()==0){
            return new ArrayList<Integer>(0);
        }else{
            Set<Integer> sets=new HashSet<Integer>(values.size());
            for (String item:values){
                try {
                    sets.add(NumberUtils.createInteger(item));
                }catch (Throwable throwable){
                    LOGGER.error(MessageFormat.format("分库索引值转成为数字失败keyword:{0}", keyword), throwable);
                }
            }
            return new ArrayList<Integer>(sets);
        }
    }

    public Integer add(KvIndex entity) {
        CallerInfo info = Profiler.registerInfo("DMSDAO.KvIndexDao.add", false, true);
        Integer result=addEntity(entity);
        Profiler.registerInfoEnd(info);
        return result;
    }

    private Integer addEntity(KvIndex entity){
        if(null==entity||StringUtils.isBlank(entity.getKeyword())||StringUtils.isBlank(entity.getValue())){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("索引对象有空值{0}",JsonHelper.toJson(entity)));
            }
            return 0;
        }
        entity.setKeyword(entity.getKeyword().trim().toUpperCase());
        entity.setValue(entity.getValue().trim().toUpperCase());
        if(entity.getKeyword().length()>50){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("KEY={0}值超过50，截断为50",entity.getKeyword()));
            }
            entity.setKeyword(entity.getKeyword().substring(0,50));
        }
        if (entity.getValue().length() > 50) {
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("VALUE={0}值超过50，截断为50",entity.getValue()));
            }
            entity.setValue(entity.getValue().substring(0,50));
        }
        return this.repeatedFilter(entity);

    }

    /**
     * 超时时间
     */
    private static final long REDIS_TIMEOUT = 30 * 60;

    /**
     * 缓存过滤数据重复
     *
     * @param entity
     * @return
     */
    private Integer repeatedFilter(KvIndex entity){
        try {
            if (redisClientCache.exists(entity.toUniqueString())) {
                return 1;
            } else {
                Integer result = super.add(namespace, entity);
                redisClientCache.setEx(entity.toUniqueString(), "1", REDIS_TIMEOUT, TimeUnit.SECONDS);
                return result;
            }
        } catch (JedisException e) {
            LOGGER.error(MessageFormat.format("[kvIndex存储]访问redis时发生异常", e), e);
        }
        return super.add(namespace, entity);
    }

    public static void main(String[] args) {
        Set<Integer> sets=new HashSet<Integer>();
        sets.add(Integer.valueOf(3));
        sets.add(Integer.valueOf(3));
        sets.add(Integer.valueOf(123412134));

        sets.add(Integer.valueOf(123412134));
        System.out.println(JsonHelper.toJson(sets));

    }
    /**
     * 根据keyword查询站点信息，按时间降序排列
     * @param keyword
     * @return
     */
	public List<Integer> queryRecentSiteCodesByKey(String keyword) {
		if(StringHelper.isEmpty(keyword)){
            return new ArrayList<Integer>(0);
        }
        keyword=keyword.trim().toUpperCase();
        if(keyword.length()>50){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("KEY={0}值超过50，截断为50",keyword));
            }
            keyword=keyword.substring(0,50);
        }
        List<String> values= this.getSqlSession().selectList(namespace + ".queryRecentSiteCodesByKey", keyword);
        if(null==values||values.size()==0){
            return new ArrayList<Integer>(0);
        }else{
        	List<Integer> restList = new ArrayList<Integer>(8);
            Set<Integer> sets=new HashSet<Integer>(values.size());
            for (String item:values){
            	//判断是否存在，不存在放入新的list中
                try {
                	Integer siteCode = NumberUtils.createInteger(item);
                	if(!sets.contains(siteCode)){
                		restList.add(siteCode);
                		sets.add(siteCode);
                	}
                }catch (Throwable throwable){
                    LOGGER.error(MessageFormat.format("分库索引值转成为数字失败keyword:{0}", keyword), throwable);
                }
            }
            return restList;
        }
	}
}